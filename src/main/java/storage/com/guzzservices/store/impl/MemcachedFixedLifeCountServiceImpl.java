/**
 * MemcachedFixedLifeCountServiceImpl.java created at 2010-2-4 下午05:36:55 by liukaixuan@gmail.com
 */
package com.guzzservices.store.impl;

import org.guzz.service.AbstractRemoteService;
import org.guzz.service.DummyFutureResult;
import org.guzz.service.FutureDataFetcher;
import org.guzz.service.FutureResult;

import com.guzzservices.store.FixedLifeCountService;
import com.guzzservices.store.MemcachedService;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class MemcachedFixedLifeCountServiceImpl extends AbstractRemoteService<Integer> implements FixedLifeCountService {
	
	protected MemcachedService memcachedService ;
		
	public FutureResult<Integer> incCount(final String key, final int addCount, final int maxLifeInSeconds) {
		try{
			return this.sumbitTask(new FutureDataFetcher<Integer>(){
				public Integer getDefaultData() {
					return null;
				}

				public Integer call() throws Exception {
					return (int) memcachedService.incr(key, addCount, addCount , maxLifeInSeconds) ;
				}				
			}) ;
		}catch(Exception e){//memcached的宕机不影响其他功能使用。
			log.error("memcached client error.", e) ;
			
			return DummyFutureResult.NULL ;
		}
	}

	public boolean incCountIfLess(String key, int addCount, int maxCountAllowed, int maxLifeInSeconds) {
		int countNow = (int) memcachedService.incr(key, addCount, addCount , maxLifeInSeconds) ;
		
		return (maxCountAllowed >= countNow) ;
	}

	public boolean isAvailable() {
		return super.isAvailable() && memcachedService != null ;
	}

	public MemcachedService getMemcachedService() {
		return memcachedService;
	}

	public void setMemcachedService(MemcachedService memcachedService) {
		this.memcachedService = memcachedService;
	}

}
