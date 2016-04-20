/**
 * 
 */
package com.guzzservices.sso;

import java.util.Map;

/**
 * 
 * user info service.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface UserInfoService {
	
	/**
	 * Query user info.
	 * 
	 * @param userName
	 * @return return null if user not exist.
	 * @throws SSOException server internal error.
	 */
	public Map<String, Object> queryUserInfo(String userName) throws SSOException ;
	
	/**
	 * Query userId by userName.
	 * 
	 * @param userName
	 * @return return -1 if user not exist.
	 * @throws SSOException server internal error.
	 */
	public int queryUserId(String userName) throws SSOException ;
	
	/**
	 * Query userName by userId.
	 * 
	 * @param userId
	 * @return return null if user not exist.
	 * @throws SSOException server internal error.
	 */
	public String queryUserName(int userId) throws SSOException ;
	
	/**
	 * 检查给定的属性值是否合法。
	 * 
	 * @param propToCheck 要检查的属性
	 * @param value 属性值
	 * @return null表示合法；其他为错误信息 
	 * @throws SSOException server internal error.
	 */
	public String checkProfileValid(String propToCheck, String value) throws SSOException ;	

	/**
	 * 注册用户
	 * 
	 * @param userInfos 注册用户的资料。与{@link #queryUserInfo(String)} 返回的Map格式一致。
	 * @return null表示注册成功；其他为错误信息 
	 * @throws SSOException server internal error.
	 */
	public String regUser(Map<String, Object> userInfos) throws SSOException ;
	
}
