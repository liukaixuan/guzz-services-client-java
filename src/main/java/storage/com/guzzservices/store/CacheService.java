/**
 * 
 */
package com.guzzservices.store;

/**
 * 
 * Common Cache interface.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface CacheService {
	
	public void storeToCache(String key, Object value) ;
	
	public void asyncStoreToCache(String key, Object value) ;
	
	/**
	 * store to cache. add or update.
	 * 
	 * @param key key
	 * @param value value
	 * @param maxAge max seconds to alive
	 */
	public void storeToCache(String key, Object value, int maxAge) ;
	
	public void asyncStoreToCache(String key, Object value, int maxAge) ;
	
	public void removeFromCache(String key) ;
	
	public void asyncRemoveFromCache(String key) ;
	
	public Object getFromCache(String key) ;
	
}
