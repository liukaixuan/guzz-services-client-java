/**
 * 
 */
package com.guzzservices.sso;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 
 * （协同）应用本地session管理。
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface LocalSessionService {
	
	/**
	 * 获取本地应用登录信息。如果没有，则进行登录。本地登录时，只需要按照session时长设置超时即可。
	 * 
	 * @param request
	 * @param response
	 * @param sessionId
	 * @param loginUser 统一登录用户。
	 */
	public Object getLocalLoginUser(HttpServletRequest request, HttpServletResponse response, String sessionId, LoginUser loginUser) ;
	
	/**
	 * 获取本地应用访客信息。
	 * 
	 * @param request
	 * @param response
	 */
	public Object getLocalGuest(HttpServletRequest request, HttpServletResponse response) ;
	
	/**
	 * 本地退出。本方法不保证调用，应用需要自己删除本地过期的session。
	 */
	public void localLogout(HttpServletRequest request, HttpServletResponse response, String sessionId) ;

}
