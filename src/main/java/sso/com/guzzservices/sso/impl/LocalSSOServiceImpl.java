/**
 * 
 */
package com.guzzservices.sso.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.guzzservices.sso.LoginException;
import com.guzzservices.sso.LoginUser;
import com.guzzservices.sso.SSOException;
import com.guzzservices.sso.stub.SSOInfo;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class LocalSSOServiceImpl extends AbstractSSOServiceImpl {
	
	private GlobalSSOServerServiceImpl globalSSOServerService ;
	
	protected SSOInfo internalLogout(HttpServletRequest request, HttpServletResponse response, String sessionId) {
		return globalSSOServerService.localLogout(sessionId) ;
	}
	
	protected LoginUser internalGetLoginUser(HttpServletRequest request, HttpServletResponse response, CookieUser cu, String sessionId) {
		return globalSSOServerService.localGetLoginUser(sessionId) ;
	}
	
	public void checkPassword(String IP, String userName, String password) throws SSOException {
		int errorCode = globalSSOServerService.checkPassword(IP, userName, password) ;
		
		if(SSOInfo.SUCCESS != errorCode){
			throw new SSOException(errorCode) ;
		}
	}

	protected SSOInfo internalLogin(HttpServletRequest request, HttpServletResponse response, String oldSessionId, String userName, String password, int maxAge, boolean checkPassword) throws LoginException {
		String ip = request.getRemoteAddr() ;
		
		SSOInfo info = null ; 
		
		if(checkPassword){
			info = globalSSOServerService.localLogin(oldSessionId, userName, password, ip, maxAge) ;
		}else{
			info = globalSSOServerService.localLogin(oldSessionId, userName, ip, maxAge) ;
		}
		
		return info ;
	}

	public boolean isAvailable() {
		return this.globalSSOServerService != null ;
	}

	public void setGlobalSSOServerService(GlobalSSOServerServiceImpl globalSSOServerService) {
		this.globalSSOServerService = globalSSOServerService;
	}

}

