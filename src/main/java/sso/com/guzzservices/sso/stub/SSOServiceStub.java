/**
 * 
 */
package com.guzzservices.sso.stub;

import com.guzzservices.sso.LoginUser;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
@Deprecated
public interface SSOServiceStub {

	/**
	 * @return {@link LoginUser} in json
	 */
	public String getLoginUser(String guzzSessionId) ;
	
	/**
	 * login for maxAge
	 * 
	 * @param oldSessionId
	 * @param userName
	 * @param IP
	 * @param maxAge -1 for current browser session.
	 * 
	 * @return {@link SSOInfo} in json
	 */
	public String login(String oldSessionId, String userName, String IP, int maxAge) ;
	
	/**
	 * check user/password, and login for maxAge.
	 * 
	 * @param oldSessionId
	 * @param userName
	 * @param password
	 * @param IP
	 * @param maxAge -1 for current browser session.
	 * 
	 * @return {@link SSOInfo} in json
	 */
	public String login(String oldSessionId, String userName, String password, String IP, int maxAge) ;
	
	/**
	 * logout.
	 * 
	 * @return {@link SSOInfo} in json
	 */
	public String logout(String sessionId) ;
	
}
