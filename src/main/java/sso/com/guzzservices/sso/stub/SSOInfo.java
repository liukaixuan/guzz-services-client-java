/**
 * 
 */
package com.guzzservices.sso.stub;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class SSOInfo implements java.io.Serializable{
	
	public static final int SUCCESS = 0 ;

	private List<CookieInfo> cookieInfos ;
	
	private int errorCode ;
	
	private String errorMsg ;
	
	public SSOInfo(int errorCode, String errorMsg){
		this.errorCode = errorCode ;
		this.errorMsg = errorMsg ;
	}
	
	public SSOInfo(){
		this.errorCode = SUCCESS ;
	}

	public List<CookieInfo> getCookieInfos() {
		return cookieInfos;
	}

	public void setCookieInfos(List<CookieInfo> cookieInfos) {
		this.cookieInfos = cookieInfos;
	}
	
	public void addCookieInfo(CookieInfo cookieInfo){
		if(this.cookieInfos == null){
			this.cookieInfos = new LinkedList<CookieInfo>() ;
		}
		
		this.cookieInfos.add(cookieInfo) ;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	public boolean isSuccess(){
		return this.errorCode == SUCCESS ;
	}

	public String getErrorMsg() {
		return this.errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

}
