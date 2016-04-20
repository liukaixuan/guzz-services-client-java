/**
 * 
 */
package com.guzzservices.sso;

import com.guzzservices.sso.stub.SSOInfo;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface UserStoreService {

	/**
	 * Query the user info as {@link LoginUser}.
	 * 
	 * @param userName
	 * @return the {@link LoginUser} to be stored in session.
	 */
	public LoginUser getLoginUser(String userName) ;
	
	/**
	 * Can the user log in?
	 * 
	 * @param userName
	 * @param password
	 * @param IP
	 * @return {@link SSOInfo#SUCCESS} if permission granted; or return a constant error code in {@link LoginException} on failed.
	 */
	public int checkLogin(String userName, String password, String IP) ;
		
	/**
	 * Translate the errorCode to a human readable message.
	 * 
	 * @param errorCode
	 * @return human readable message
	 */
	public String translateErrorCode(int errorCode) ;
	
}
