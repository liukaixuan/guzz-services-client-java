/**
 * 
 */
package com.guzzservices.sso;


/**
 * 
 * server-side sso provider interface.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface SSOServerService {
	
	public void addUserStatusListener(UserStatusListener listener);
	
	public void removeUserStatusListener(UserStatusListener listener);
	
}
