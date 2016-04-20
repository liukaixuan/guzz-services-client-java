/**
 * 
 */
package com.guzzservices.sso;

/**
 * 
 * user status listener.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface UserStatusListener {
	
	public void notifyLogin(LoginUser loginUser, String IP, int maxAge) ;
	
	public void notifyOnline(LoginUser loginUser) ;
	
	public void notifyLogout(LoginUser loginUser) ;

}
