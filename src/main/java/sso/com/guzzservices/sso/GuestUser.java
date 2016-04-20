/**
 * 
 */
package com.guzzservices.sso;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class GuestUser extends LoginUser {
	
	public static transient final GuestUser GUEST = new GuestUser() ;
	
	public static transient final GuestUser DEMON_TASK_USER = new GuestUser() ;

	public boolean isLogin(){
		return false ;
	}
	
}
