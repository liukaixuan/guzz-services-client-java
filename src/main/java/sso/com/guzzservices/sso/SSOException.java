/**
 * 
 */
package com.guzzservices.sso;

import org.guzz.util.StringUtil;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
@SuppressWarnings("serial")
public class SSOException extends Exception {
	
	private int errorCode ;
	
	private String errorMsg ;
	
	public SSOException(int errorCode, String errorMsg){
		super(StringUtil.isEmpty(errorMsg) ? "error code:" + errorCode : errorMsg) ;
	
		this.errorCode = errorCode ;
		this.errorMsg = errorMsg ;
	}

	public SSOException(int errorCode) {
		this.errorCode = errorCode ;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

}
