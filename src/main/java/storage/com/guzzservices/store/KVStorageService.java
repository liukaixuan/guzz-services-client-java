/**
 * 
 */
package com.guzzservices.store;

/**
 * 
 * key-value storage service.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface KVStorageService {
	
	/**
	 * 同步保存
	 */
	public void storeTo(String key, String value) ;
	
	/**
	 * 异步保存
	 */
	public void asyncStoreTo(String key, String value) ;
	
	/**
	 * 同步删除
	 */
	public void remove(String key) ;
	
	public String get(String key) ;

}
