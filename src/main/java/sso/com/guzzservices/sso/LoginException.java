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
@SuppressWarnings("serial")
public class LoginException extends SSOException {
	
	public static final int NO_SUCH_USER = 1 ;
	
	public static final int PASSWORD_WRONG = 5 ;
	
	public static final int IP_BANNED = 10 ;
	
	public static final int USER_BANNED = 15 ;
	
	public static final int PERMISSION_DENIED = 20 ;
	
	public static final int SERVER_INTERNAL_ERROR = 30 ;	

	public static final int USER_DEFINED_ERROR = 100 ;
	
	public LoginException(int errorCode, String errorMsg){
		super(errorCode, errorMsg) ;
	}

//	public LoginException(int errorCode) {
//		super(errorCode, null) ;
//	}

}
