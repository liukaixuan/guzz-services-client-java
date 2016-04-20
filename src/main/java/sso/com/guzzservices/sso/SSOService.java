/**
 * 
 */
package com.guzzservices.sso;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.guzzservices.sso.impl.CookieUser;

/**
 * 
 * user single signed in.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface SSOService {
	
	public static final String CACHED_LOGIN_USER_KEY = "guzzLoginUser" ;

	public Object getLoginUser(HttpServletRequest request, HttpServletResponse response) ;	

	/**
	 * Get LoginUser from the server side. Slow but promising.
	 */
	public Object getLoginUserForUpdate(HttpServletRequest request, HttpServletResponse response) ;
	
	public CookieUser readCookieUser(HttpServletRequest request, HttpServletResponse response) throws IOException ;
	
	public void checkPassword(String IP, String userName, String password) throws SSOException ;
	
	/**
	 * login for the current browser session.
	 * <p/>only allowed for local login.
	 */
	public void login(HttpServletRequest request, HttpServletResponse response, String userName) throws LoginException ;
	
	public void login(HttpServletRequest request, HttpServletResponse response, String userName, int maxAge) throws LoginException ;
	
	/**
	 * login for the current browser session.
	 */
	public void login(HttpServletRequest request, HttpServletResponse response, String userName, String password) throws LoginException ;
	
	public void login(HttpServletRequest request, HttpServletResponse response, String userName, String password, int maxAge) throws LoginException ;
	
	public void logout(HttpServletRequest request, HttpServletResponse response) ;
	
	/**
	 * Is anyone logged in?
	 * <br>Not strict checking. May return a wrong result in hacking.
	 */
	public boolean isLogin(HttpServletRequest request, HttpServletResponse response) ;
	
	/**
	 * get guzzSessionId.
	 * 
	 * @return return null if not logged in.
	 * */
	public String readSessionId(HttpServletRequest request, HttpServletResponse response) ;
	
}
