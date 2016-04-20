/**
 * MemcachedFixedLifeCountServiceImpl.java created at 2010-2-4 下午05:36:55 by liukaixuan@gmail.com
 */
package com.guzzservices.store.impl;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;
import net.spy.memcached.ConnectionObserver;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.NodeLocator;
import net.spy.memcached.internal.BulkFuture;
import net.spy.memcached.transcoders.Transcoder;

import org.guzz.exception.ServiceExecutionException;
import org.guzz.service.AbstractService;
import org.guzz.service.ServiceConfig;
import org.guzz.util.StringUtil;

import com.guzzservices.store.MemcachedService;

/**
 * 
 * spy memcached client implementation.
 * 
 * http://code.google.com/p/spymemcached/
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class MemcachedServiceImpl extends AbstractService implements MemcachedService {
	
	protected MemcachedClient client ;
	
	protected int defaultMaxAgeInSeconds = 3600 * 24 ;
	
	public boolean configure(ServiceConfig[] scs) {
		if(scs.length > 0){
			String serverList = scs[0].getProps().getProperty("serverList") ;
			this.defaultMaxAgeInSeconds = StringUtil.toInt(scs[0].getProps().getProperty("defaultMaxAgeInSeconds"), defaultMaxAgeInSeconds) ;
			
			try {
				this.client = new MemcachedClient(AddrUtil.getAddresses(serverList)) ;
				
				return true ;
			} catch (IOException e) {
				log.error(serverList, e) ;
				return false ;
			}
		}else {
			log.warn("FixedLifeCountService is not started. no configuration found.") ;
			return false ;
		}
	}

	public Object getFromCache(String key) {
		return this.get(key) ;
	}

	public void removeFromCache(String key) {
		this.delete(key) ;
	}

	public void storeToCache(String key, Object value) {
		this.storeToCache(key, value, defaultMaxAgeInSeconds) ;
	}

	public void storeToCache(String key, Object value, int maxAge) {
		this.set(key, maxAge, value) ;
	}

	public void asyncStoreToCache(String key, Object value) {
		this.asyncStoreToCache(key, value, defaultMaxAgeInSeconds) ;
	}

	public void asyncStoreToCache(String key, Object value, int maxAge) {
		this.set(key, maxAge, value) ;
	}
	
	public void asyncRemoveFromCache(String key) {
		this.delete(key) ;
	}

	public void startup() {}

	public boolean isAvailable() {
		return client != null ;
	}

	public void shutdown() {
		if(client != null){
			client.shutdown() ;
		}
	}

	////////////////////delegate methods to MemcachedClient
	public Future<Boolean> add(String key, int exp, Object o) {
		return client.add(key, exp, o);
	}

	public <T> Future<Boolean> add(String key, int exp, T o, Transcoder<T> tc) {
		return client.add(key, exp, o, tc);
	}

	public boolean addObserver(ConnectionObserver obs) {
		return client.addObserver(obs);
	}

	public Future<Boolean> append(long cas, String key, Object val) {
		return client.append(cas, key, val);
	}

	public <T> Future<Boolean> append(long cas, String key, T val, Transcoder<T> tc) {
		return client.append(cas, key, val, tc);
	}

	public <T> Future<CASResponse> asyncCAS(String key, long casId, int exp, T value, Transcoder<T> tc) {
		return client.asyncCAS(key, casId, exp, value, tc);
	}

	public Future<CASResponse> asyncCAS(String key, long casId, Object value) {
		return client.asyncCAS(key, casId, value);
	}

	public <T> Future<CASResponse> asyncCAS(String key, long casId, T value, Transcoder<T> tc) {
		return client.asyncCAS(key, casId, value, tc);
	}

	public Future<Long> asyncDecr(String key, int by) {
		return client.asyncDecr(key, by);
	}

	public <T> Future<T> asyncGet(String key, Transcoder<T> tc) {
		return client.asyncGet(key, tc);
	}

	public Future<Object> asyncGet(String key) {
		return client.asyncGet(key);
	}

	public <T> Future<CASValue<T>> asyncGets(String key, Transcoder<T> tc) {
		return client.asyncGets(key, tc);
	}

	public Future<CASValue<Object>> asyncGets(String key) {
		return client.asyncGets(key);
	}

	public Future<Long> asyncIncr(String key, int by) {
		return client.asyncIncr(key, by);
	}

	public <T> CASResponse cas(String key, long casId, int exp, T value, Transcoder<T> tc) {
		return client.cas(key, casId, exp, value, tc);
	}

	public CASResponse cas(String key, long casId, Object value) {
		return client.cas(key, casId, value);
	}

	public <T> CASResponse cas(String key, long casId, T value, Transcoder<T> tc) {
		return client.cas(key, casId, value, tc);
	}

	public void connectionEstablished(SocketAddress sa, int reconnectCount) {
		client.connectionEstablished(sa, reconnectCount);
	}

	public void connectionLost(SocketAddress sa) {
		client.connectionLost(sa);
	}

	public long decr(String key, int by, long def, int exp) {
		return client.decr(key, by, def, exp);
	}

	public long decr(String key, int by, long def) {
		return client.decr(key, by, def);
	}

	public long decr(String key, int by) {
		return client.decr(key, by);
	}

	/**
	 * @deprecated 
	 */
	public Future<Boolean> delete(String key, int hold) {
		return client.delete(key, hold);
	}

	public Future<Boolean> delete(String key) {
		return client.delete(key);
	}

	public <T> T get(String key, Transcoder<T> tc) {
		return client.get(key, tc);
	}

	public Object get(String key) {
		return client.get(key);
	}

	public Collection<SocketAddress> getAvailableServers() {
		return client.getAvailableServers();
	}

	public <T> Map<String, T> getBulk(Collection<String> keys, Transcoder<T> tc) {
		return client.getBulk(keys, tc);
	}

	public Map<String, Object> getBulk(Collection<String> keys) {
		return client.getBulk(keys);
	}

	public Map<String, Object> getBulk(String... keys) {
		return client.getBulk(keys);
	}

	public <T> Map<String, T> getBulk(Transcoder<T> tc, String... keys) {
		return client.getBulk(tc, keys);
	}

	public <T> CASValue<T> gets(String key, Transcoder<T> tc) {
		return client.gets(key, tc);
	}

	public CASValue<Object> gets(String key) {
		return client.gets(key);
	}

	public Transcoder<Object> getTranscoder() {
		return client.getTranscoder();
	}

	public Collection<SocketAddress> getUnavailableServers() {
		return client.getUnavailableServers();
	}

	public Map<SocketAddress, String> getVersions() {
		return client.getVersions();
	}

	public long incr(String key, int by, long def, int exp) {
		return client.incr(key, by, def, exp);
	}

	public long incr(String key, int by, long def) {
		return client.incr(key, by, def);
	}

	public long incr(String key, int by) {
		return client.incr(key, by);
	}

	public Set<String> listSaslMechanisms() {
		return client.listSaslMechanisms();
	}

	public Future<Boolean> prepend(long cas, String key, Object val) {
		return client.prepend(cas, key, val);
	}

	public <T> Future<Boolean> prepend(long cas, String key, T val, Transcoder<T> tc) {
		return client.prepend(cas, key, val, tc);
	}

	public boolean removeObserver(ConnectionObserver obs) {
		return client.removeObserver(obs);
	}

	public Future<Boolean> replace(String key, int exp, Object o) {
		return client.replace(key, exp, o);
	}

	public <T> Future<Boolean> replace(String key, int exp, T o, Transcoder<T> tc) {
		return client.replace(key, exp, o, tc);
	}

	public Future<Boolean> set(String key, int exp, Object o) {
		return client.set(key, exp, o);
	}

	public <T> Future<Boolean> set(String key, int exp, T o, Transcoder<T> tc) {
		return client.set(key, exp, o, tc);
	}

	public boolean shutdown(long timeout, TimeUnit unit) {
		throw new ServiceExecutionException("shutdown the client with Service's method.") ;
	}

	public Future<Boolean> flush() {
		return client.flush() ;
	}

	public Future<Boolean> flush(int delay) {
		return client.flush(delay) ;
	}

	public NodeLocator getNodeLocator() {
		return client.getNodeLocator() ;
	}

	public Map<SocketAddress, Map<String, String>> getStats() {
		return client.getStats() ;
	}

	public Map<SocketAddress, Map<String, String>> getStats(String prefix) {
		return client.getStats(prefix) ;
	}

	public boolean waitForQueues(long timeout, TimeUnit unit) {
		return client.waitForQueues(timeout, unit) ;
	}

	public <T> BulkFuture<Map<String, T>> asyncGetBulk(Collection<String> keys, Iterator<Transcoder<T>> tcs) {
		return client.asyncGetBulk(keys, tcs);
	}

	public <T> BulkFuture<Map<String, T>> asyncGetBulk(Collection<String> keys, Transcoder<T> tc) {
		return client.asyncGetBulk(keys, tc);
	}

	public BulkFuture<Map<String, Object>> asyncGetBulk(Collection<String> keys) {
		return client.asyncGetBulk(keys);
	}

	public <T> BulkFuture<Map<String, T>> asyncGetBulk(Transcoder<T> tc, String... keys) {
		return client.asyncGetBulk(tc, keys);
	}

	public BulkFuture<Map<String, Object>> asyncGetBulk(String... keys) {
		return client.asyncGetBulk(keys);
	}

}
