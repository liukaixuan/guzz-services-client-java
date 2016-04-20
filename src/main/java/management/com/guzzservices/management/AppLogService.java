/**
 * 
 */
package com.guzzservices.management;

import java.util.List;
import java.util.Map;

import org.guzz.dao.PageFlip;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface AppLogService {
	
	/**
	 * 根据配置的日志组，插入一条日志。
	 * 
	 * @param userId 操作用户
	 * @param customProps 日志自定义属性
	 */
	public void insertLog(int userId, Map<String, Object> customProps) throws Exception ;
	
	/**
	 * 
	 * 根据配置的日志组查询日志。用户编号的属性名为userId；日志记录时间的属性名为createdTime，传入查询条件的格式为：yyyy-MM-dd HH:mm:ss
	 * 
	 * @param conditions 条件列表。每条一个条件，如：userId=1，如：title~=读书
	 * @param orderBy 
	 * @param pageNo
	 * @param pageSize
	 * 
	 * @return 如果条件不足，可能返回null；如果条件错误，可能抛出异常。
	 */
	public PageFlip queryLogs(List<String> conditions, String orderBy, int pageNo, int pageSize) throws Exception ;
	
	/**
	 * 根据配置的日志组，查询配置的自定义属性的元数据。
	 * 
	 * <p/>返回的是数据库中记录的数据，如果自定义属性正在调整，可能会和{@link #queryLogs(List, String, int, int)} 返回的数据列对不上。
	 * 
	 * @return 返回自定义属性的元数据Map。key为java属性名，value为用于对网友显示的displayName。
	 */
	public Map<String, String> queryCustomPropsMetaInfo() throws Exception ;
	
	/**
	 * 插入一条日志到给定的日志组。
	 * 
	 * @param secureCode 日志组密码
	 * @param userId 操作用户
	 * @param customProps 日志自定义属性
	 */
	public void insertLog(String secureCode, int userId, Map<String, Object> customProps) throws Exception ;
	
	/**
	 * 
	 * 查询给定日志组的日志。用户编号的属性名为userId；日志记录时间的属性名为createdTime，传入查询条件的格式为：yyyy-MM-dd HH:mm:ss
	 * 
	 * @param secureCode 日志组密码
	 * @param conditions 条件列表。每条一个条件，如：userId=1，如：title~=读书
	 * @param orderBy 
	 * @param pageNo
	 * @param pageSize
	 * 
	 * @return 如果条件不足，可能返回null；如果条件错误，可能抛出异常。
	 */
	public PageFlip queryLogs(String secureCode, List<String> conditions, String orderBy, int pageNo, int pageSize) throws Exception ;
	
	/**
	 * 查询给定日志组配置的自定义属性的元数据。
	 * 
	 * <p/>返回的是数据库中记录的数据，如果自定义属性正在调整，可能会和{@link #queryLogs(String, List, String, int, int)} 返回的数据列对不上。
	 * 
	 * @param secureCode 日志组密码
	 * @return 返回自定义属性的元数据Map。key为java属性名，value为用于对网友显示的displayName。
	 */
	public Map<String, String> queryCustomPropsMetaInfo(String secureCode) throws Exception ;

}
