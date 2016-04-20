/**
 * 
 */
package com.guzzservices.management.alog;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 日志记录。
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */

public class LogRecord {
	
	private long id ;
	
	private int userId ;
	
	private int appId ;
	
	/**
	 * 请求插入此日志的服务器IP
	 */
	private String appIP ;
	
	private Date createdTime ;
	
	/**
	 * 自定义属性的值。
	 */
	private Map<String, Object> otherProps = new HashMap<String, Object>() ;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Map<String, Object> getOtherProps() {
		return otherProps;
	}

	public void setOtherProps(Map<String, Object> otherProps) {
		this.otherProps = otherProps;
	}

	public String getAppIP() {
		return appIP;
	}

	public void setAppIP(String appIP) {
		this.appIP = appIP;
	}

}
