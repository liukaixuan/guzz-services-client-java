/**
 * 
 */
package com.guzzservices.store.impl;

import java.util.HashMap;
import java.util.Map;

import org.guzz.service.AbstractService;
import org.guzz.service.ServiceConfig;

import com.guzzservices.store.CacheService;

/**
 * 
 * cache stored in a HashMap. The cache object will never expire.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class HashMapCacheServiceImpl extends AbstractService implements CacheService {
	
	private Map<String, Object> cache = new HashMap<String, Object>() ;

	public Object getFromCache(String key) {
		return cache.get(key) ;
	}

	public void removeFromCache(String key) {
		cache.remove(key) ;
	}
	
	public void asyncRemoveFromCache(String key) {
		cache.remove(key) ;
	}

	public void storeToCache(String key, Object value) {
		cache.put(key, value) ;
	}

	public void storeToCache(String key, Object value, int maxAge) {
		cache.put(key, value) ;
	}

	public void asyncStoreToCache(String key, Object value) {
		storeToCache(key, value) ;
	}

	public void asyncStoreToCache(String key, Object value, int maxAge) {
		storeToCache(key, value, maxAge) ;
	}

	public boolean configure(ServiceConfig[] scs) {
		return true ;
	}

	public boolean isAvailable() {
		return true ;
	}

	public void shutdown() {
	}

	public void startup() {
	}

}
