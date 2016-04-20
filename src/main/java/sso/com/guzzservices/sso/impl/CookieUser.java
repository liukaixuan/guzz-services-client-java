/**
 * 
 */
package com.guzzservices.sso.impl;


/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class CookieUser {

	private boolean login ;
	
	private int userId ;
	
	private String userName ;
	
	private String userNick ;
	
	private String displayName ;
	
	private int authType ;
	
	private String talk ;
	
	private long version ;
	
	private long loginTime ;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}

	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public String getUserNick() {
		return userNick;
	}

	public void setUserNick(String userNick) {
		this.userNick = userNick;
	}

	public String getTalk() {
		return talk;
	}

	public void setTalk(String talk) {
		this.talk = talk;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getAuthType() {
		return this.authType;
	}

	public void setAuthType(int authType) {
		this.authType = authType;
	}

}
