/**
 * 
 */
package com.guzzservices.sso.impl;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.apache.commons.codec.binary.Hex;
import org.guzz.exception.ServiceExecutionException;
import org.guzz.service.AbstractService;
import org.guzz.service.ServiceConfig;
import org.guzz.util.CookieUtil;
import org.guzz.util.StringUtil;
import org.guzz.util.thread.DemonQueuedThread;

import com.guzzservices.rpc.server.CommandHandler;
import com.guzzservices.rpc.server.CommandHandlerAdapter;
import com.guzzservices.rpc.server.CommandServerService;
import com.guzzservices.rpc.util.JsonUtil;
import com.guzzservices.sso.GuestUser;
import com.guzzservices.sso.LoginException;
import com.guzzservices.sso.LoginUser;
import com.guzzservices.sso.SSOServerService;
import com.guzzservices.sso.UserStatusListener;
import com.guzzservices.sso.UserStoreService;
import com.guzzservices.sso.impl.CommandSSOServiceImpl.CheckPasswordCommandRequest;
import com.guzzservices.sso.impl.CommandSSOServiceImpl.LoginCommandRequest;
import com.guzzservices.sso.stub.CookieInfo;
import com.guzzservices.sso.stub.SSOInfo;
import com.guzzservices.store.CacheService;

/**
 * 
 * server-side service for sso login from any application.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class GlobalSSOServerServiceImpl extends AbstractService implements SSOServerService {
	
	private String sessionIdCookieName = "guzz_session_id" ;
	
	private String sessionUserCookieName = "guzz_session_user" ;
	
	private CacheService cacheService ;
	
	private UserStoreService userStoreService ;
	
	private String[] cookieDomains ;
		
	private List<UserStatusListener> listeners = new LinkedList<UserStatusListener>() ;
	
	private OnlineStatusSync onlineStatusSyncThread = new OnlineStatusSync() ;
	
	/**
	 * Thread pool to perform user login & logout notifications.
	 */
	private ExecutorService executorService ;

	public boolean configure(ServiceConfig[] scs) {
		if(scs.length != 0){
			Properties props = scs[0].getProps() ;
			
			String domains = props.getProperty("cookieDomains") ;
			
			if(StringUtil.isEmpty(domains)){
				this.cookieDomains = new String[]{null} ;
			}else{
				this.cookieDomains = StringUtil.splitString(domains, ",") ;
				
				for(int i = 0 ; i < this.cookieDomains.length ; i++){
					this.cookieDomains[i] = this.cookieDomains[i].trim() ;
					
					if(StringUtil.isEmpty(this.cookieDomains[i])){
						this.cookieDomains[i] = null ;
					}
				}
			}
			
			this.sessionIdCookieName = props.getProperty("sessionIdCookieName", sessionIdCookieName) ;
			this.sessionUserCookieName = props.getProperty("sessionUserCookieName", sessionUserCookieName) ;
		}
		
		return true ;
	}
	
	protected String generateSessionId(){
		String uuid = UUID.randomUUID().toString() ;
		return uuid.replace('-', 'a').toLowerCase() ;
	}
	
	protected String userIdToCacheKey(int userId){
		return "gssos@" + userId ;
	}
	
	public void updateUserInfoInSession(int userId){
		String key = userIdToCacheKey(userId) ;
		String sessionId = (String) this.cacheService.getFromCache(key) ;
		if(sessionId == null) return ;
		
		//pre login user
		LoginUser lu = localGetLoginUser(sessionId) ;
		if(lu == null) return ;
		
		LoginUser loginUser = this.userStoreService.getLoginUser(lu.getUserName()) ;
		
		if(loginUser == null ){
			return ;
		}else{
			loginUser.setLoginTime(lu.getLoginTime()) ;
		}
		
		this.cacheService.storeToCache(sessionId, JsonUtil.toJson(loginUser), CookieUtil.COOKIE_AGE_24H) ;
	}

	protected String getLoginUser(String guzzSessionId) {
		if(StringUtil.isEmpty(guzzSessionId)) return null ;
		String loginUser = (String) this.cacheService.getFromCache(guzzSessionId) ;
		
		this.onlineStatusSyncThread.notifyUserActive(guzzSessionId, loginUser) ;
		
		return (String) loginUser ;
	}

	public String login(String oldSessionId, String userName, String IP, int maxAge) {
		SSOInfo info = this.localLogin(oldSessionId, userName, IP, maxAge) ;
		
		return JsonUtil.toJson(info) ;
	}

	public String login(String oldSessionId, String userName, String password, String IP, int maxAge) {
		SSOInfo info = this.localLogin(oldSessionId, userName, password, IP, maxAge) ;
		
		return JsonUtil.toJson(info) ;
	}

	public String logout(String sessionId) {
		SSOInfo info = this.localLogout(sessionId) ;
		
		return JsonUtil.toJson(info) ;
	}

	public LoginUser localGetLoginUser(String guzzSessionId) {
		String json = this.getLoginUser(guzzSessionId) ;
		
		if(json == null){
			return GuestUser.GUEST ;
		}
		
		return JsonUtil.fromJson(json, LoginUser.class) ;
	}

	public SSOInfo localLogin(String oldSessionId, String userName, String password, String IP, int maxAge) {
		int errorCode = this.userStoreService.checkLogin(userName, password, IP) ;
		
		if(errorCode != SSOInfo.SUCCESS){
			return new SSOInfo(errorCode, this.userStoreService.translateErrorCode(errorCode)) ;
		}
		
		return localLogin(oldSessionId, userName, IP, maxAge) ;
	}
	
	public int checkPassword(String IP, String userName, String password) {
		return this.userStoreService.checkLogin(userName, password, IP) ;
	}
	
	public SSOInfo localLogin(String oldSessionId, String userName, final String IP, int maxAge) {
		//删除以前登录的用户
		if(StringUtil.notEmpty(oldSessionId)){
			this.cacheService.removeFromCache(oldSessionId) ;
		}
		
		//memcached不让按照相对时间存储超过1个月有效期的数据。
		if(maxAge > CookieUtil.COOKIE_AGE_1Month){
			maxAge = CookieUtil.COOKIE_AGE_1Month ;
		}
		
		LoginUser loginUser = this.userStoreService.getLoginUser(userName) ;
		
		if(loginUser == null ){
			return new SSOInfo(LoginException.NO_SUCH_USER, this.userStoreService.translateErrorCode(LoginException.NO_SUCH_USER)) ;
		}
		
		loginUser.setLoginTime(new Date().getTime()) ;
		
		//store to cache.
		String sessionId = this.generateSessionId() ;
		String userKey = userIdToCacheKey(loginUser.getUserId()) ;
		
		if(maxAge < 100){//maybe current browser session cookie
			this.cacheService.storeToCache(sessionId, JsonUtil.toJson(loginUser), CookieUtil.COOKIE_AGE_24H) ;
			this.cacheService.storeToCache(userKey, sessionId, CookieUtil.COOKIE_AGE_24H) ;
		}else{
			this.cacheService.storeToCache(sessionId, JsonUtil.toJson(loginUser), maxAge) ;
			this.cacheService.storeToCache(userKey, sessionId, maxAge) ;
		}
		
		if(!this.listeners.isEmpty()){
			//如果有pool就用pool
			if(this.executorService != null){
				this.executorService.execute(new _Notify_Task(this.listeners, loginUser, maxAge) {
					
					public void run() {
						for(UserStatusListener l : this.listeners){
							l.notifyLogin(this.loginUser, IP, this.maxAge) ;
						}
					}
				}) ;
				
			}else{
				for(UserStatusListener l : this.listeners){
					l.notifyLogin(loginUser, IP, maxAge) ;
				}
			}
		}
		
		//store to cookies
		return buildSSOInfo(sessionId, loginUser, maxAge);
	}
	
	static abstract class _Notify_Task implements Runnable{
		
		protected final List<UserStatusListener> listeners ;
		
		protected final LoginUser loginUser ;
		
		protected final int maxAge ;
		
		public _Notify_Task(List<UserStatusListener> listeners, LoginUser loginUser, int maxAge){
			this.listeners = listeners ;
			this.loginUser = loginUser ;
			this.maxAge = maxAge ;
		}
	}

	public SSOInfo localLogout(String sessionId) {
		if(!this.listeners.isEmpty() && sessionId != null){
			LoginUser loginUser = localGetLoginUser(sessionId) ;
			
			if(loginUser != null && loginUser.isLogin()){
				//如果有pool就用pool
				if(this.executorService != null){
					this.executorService.execute(new _Notify_Task(this.listeners, loginUser, 0) {
						
						public void run() {
							for(UserStatusListener l : this.listeners){
								l.notifyLogout(this.loginUser) ;
							}
						}
					}) ;
					
				}else{
					for(UserStatusListener l : this.listeners){
						l.notifyLogout(loginUser) ;
					}
				}
			}
		}
		
		if(sessionId != null){
			this.cacheService.removeFromCache(sessionId) ;
		}
		
		//delete cookies
		SSOInfo info = new SSOInfo() ;
		for(String domain : this.cookieDomains){
			info.addCookieInfo(CookieInfo.newCookieInfo(sessionIdCookieName, domain)) ;
			info.addCookieInfo(CookieInfo.newCookieInfo(sessionUserCookieName, domain)) ;
		}
		
		return info ;
	}
		
	protected SSOInfo buildSSOInfo(String sessionId, LoginUser loginUser, int maxAge){
		CookieUser cu = new CookieUser() ;
		cu.setLogin(loginUser.isLogin()) ;
		cu.setUserId(loginUser.getUserId()) ;
		cu.setDisplayName(loginUser.getDisplayName()) ;
		cu.setLoginTime(new Date().getTime()) ;
		cu.setUserNick(loginUser.getUserNick()) ;
		cu.setUserName(loginUser.getUserName()) ;
		cu.setVersion(loginUser.getVersion()) ;
		cu.setAuthType(loginUser.getAuthType()) ;
		
		//4KB per cookie maximum 
		String talk = loginUser.getTalk() ;
		if(talk != null){
			if(talk.length() > 140){
				talk = talk.substring(0, 140) ;
			}
		}else{
			talk = "" ;
		}
		
		cu.setTalk(talk) ;
		
		String hexValue = null ;
		try {
			hexValue = String.valueOf(Hex.encodeHex(JsonUtil.toJson(cu).getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			log.error("unable to hex:" + JsonUtil.toJson(cu), e) ;
		}
		
		SSOInfo info = new SSOInfo() ;
		for(String domain : this.cookieDomains){
			info.addCookieInfo(CookieInfo.newCookieInfo(sessionIdCookieName, sessionId, domain, maxAge)) ;
			info.addCookieInfo(CookieInfo.newCookieInfo(sessionUserCookieName, hexValue, domain, maxAge)) ;
		}
		
		return info ;
	}
	
	public void addUserStatusListener(UserStatusListener listener){
		listeners.add(listener) ;
	}
	
	public void removeUserStatusListener(UserStatusListener listener){
		listeners.remove(listener) ;
	}

	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}

	public boolean isAvailable() {
		return this.cacheService != null ;
	}

	public void startup() {
		onlineStatusSyncThread.setMillSecondsToSleep(1000 * 60 * 3) ;
		onlineStatusSyncThread.start() ;
	}

	public void shutdown() {
		onlineStatusSyncThread.shutdown() ;
	}

	public String getSessionIdCookieName() {
		return sessionIdCookieName;
	}

	public String getSessionUserCookieName() {
		return sessionUserCookieName;
	}

	public void setUserStoreService(UserStoreService userStoreService) {
		this.userStoreService = userStoreService;
	}
	
	public void setCommandServerService(CommandServerService css){
		css.addCommandHandler(CommandSSOServiceImpl.COMMAND_LOGIN, commandHandler) ;
		css.addCommandHandler(CommandSSOServiceImpl.COMMAND_LOGOUT, commandHandler) ;
		css.addCommandHandler(CommandSSOServiceImpl.COMMAND_GET_LOGIN_USER, commandHandler) ;
		css.addCommandHandler(CommandSSOServiceImpl.COMMAND_CHECK_PASSWORD, commandHandler) ;
	}
	
	private CommandHandler commandHandler = new CommandHandlerAdapter(){
		public String executeCommand(String command, String param) throws Exception {
			String result = null ; 
			
			if(CommandSSOServiceImpl.COMMAND_LOGIN.equals(command)){
				LoginCommandRequest r = JsonUtil.fromJson(param, LoginCommandRequest.class) ;
				
				if(r.checkPassword){
					result = login(r.oldSessionId, r.userName, r.password, r.IP, r.maxAge) ;
				}else{
					//只有local允许按照用户名随意登录。
					throw new LoginException(LoginException.PERMISSION_DENIED, "Password is required!") ;
//					result = this.login(r.oldSessionId, r.userName, r.IP, r.maxAge) ;
				}
				
				return result ;
				
			}else if(CommandSSOServiceImpl.COMMAND_LOGOUT.equals(command)){
				String sessionId = param ;
				
				result = logout(sessionId) ;
				
			}else if(CommandSSOServiceImpl.COMMAND_GET_LOGIN_USER.equals(command)){
				String guzzSessionId = param ;
				
				result = getLoginUser(guzzSessionId) ;
			}else if(CommandSSOServiceImpl.COMMAND_CHECK_PASSWORD.equals(command)){
				CheckPasswordCommandRequest r = JsonUtil.fromJson(param, CheckPasswordCommandRequest.class) ;
				int errorCode = checkPassword(r.IP, r.userName, r.password) ;
				
				return String.valueOf(errorCode) ;
			}else{
				throw new ServiceExecutionException("unknown command for sso:" + command) ;
			}
			
			return result;
		}
	} ;
	
	class OnlineStatusSync extends DemonQueuedThread{
		private HashMap<String, String> activeUsers = new HashMap<String, String>() ;
		
		public void notifyUserActive(String guzzSessionId, String loginUser){
			if(loginUser == null) return ;
			if(listeners == null || listeners.isEmpty()) return ;
			
			//something is wrong!
			if(this.activeUsers.size() > 100000) return ;
			
			activeUsers.put(guzzSessionId, loginUser) ;
		}

		public OnlineStatusSync() {
			super("onlineSync", 1) ;
		}

		@Override
		protected boolean doWithTheQueue() throws Exception {
			if(this.activeUsers.isEmpty()) return false ;
			
			HashMap<String, String> users = this.activeUsers ;
			this.activeUsers = new HashMap<String, String>() ;
			
			for(String jsonUser : users.values()){
				LoginUser lu = JsonUtil.fromJson(jsonUser, LoginUser.class) ;
				if(lu == null || !lu.isLogin()) continue ;
				
				for(UserStatusListener l : listeners){
					l.notifyOnline(lu) ;
				}
			}
			
			users.clear() ;
			
			return false ;
		}
		
	}

	public ExecutorService getExecutorService() {
		return this.executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}
	
}
