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
public class LoginUser {
	
	private int userId ;
	
	private String userName ;
	
	private String userNick ;
	
	private String displayName ;
	
	//millseconds
	private long loginTime ;
	
	private int status ;
	
	private int roleId ;
	
	private int authType ;
	
	private String talk ;
	
	private long version ;
	
	public boolean isLogin(){
		return true ;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
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

	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}

	public long getLoginTime() {
		return loginTime;
	}

	public String getUserNick() {
		return userNick;
	}

	public void setUserNick(String userNick) {
		this.userNick = userNick;
	}

	public int getAuthType() {
		return this.authType;
	}

	public void setAuthType(int authType) {
		this.authType = authType;
	}
	
}
