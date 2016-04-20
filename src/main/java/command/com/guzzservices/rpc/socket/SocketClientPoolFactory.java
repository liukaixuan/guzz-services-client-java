/**
 * 
 */
package com.guzzservices.rpc.socket;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.DelayQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.guzz.util.StringUtil;
import org.guzz.util.lb.LBRound;

/**
 * 
 * TCP/IP connection pool.<br/>
 * supports weight to several machines.<br/>
 * supports re-connect.<p/>
 * 
 * Based on dennis's net.rubyeye.xmemcached.impl.MemcachedConnector
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class SocketClientPoolFactory implements PoolableObjectFactory {
	private final static Log log = LogFactory.getLog(SocketClientPoolFactory.class) ;

	private GenericObjectPool pool;

	private LBRound lb;

	// max active connection size for all machines.
	private int totalMaxLoad;

	private int maxSizeInWaitingQueue;

	private long healConnectionInterval = 2000L;

	private final DelayQueue<ReconnectRequest> waitingQueue = new DelayQueue<ReconnectRequest>();
	
	private SocketMonitor socketMonitor ;
	
	private boolean isShuttingDown;
	
	/**连接到的服务器个数*/
	private int totalServers ;

	public SocketClientPoolFactory() {
	}
	
	public void activateObject(Object client) throws Exception {
		return;
	}

	public void destroyObject(Object client) throws Exception {
		((SocketCommandService) client).dispose();
	}

	public Object makeObject() throws Exception {
		Properties props = (Properties) lb.getCard();

		try {
			return new SocketCommandService(props);
		} catch (Exception e) {
			// add to the retry pool
			addToWatingQueue(new ReconnectRequest(props, 0, this.healConnectionInterval));

			log.error("failed to connect to :[" + props + "], error message:" + e.getMessage()) ;
			
			throw e;
		}
	}

	public void passivateObject(Object client) throws Exception {
		return;
	}

	public boolean validateObject(Object client) {
		SocketCommandService scs = (SocketCommandService) client;
		
		if (!scs.isClosed() && scs.isChannelConnected())
			return true;

		//add to the retry queue.
		if (scs.isDisposedForIOException()) {
			// We don't know how the pool acts. Be a little careful.
			scs.setDisposedForIOException(false);

			addToWatingQueue(new ReconnectRequest(scs.getProps(), 0, this.healConnectionInterval));
		}

		return false;
	}
	
	public void clearReconnectRequestQueue(){
		this.waitingQueue.clear() ;
		this.pool.setMaxActive(this.totalMaxLoad) ;
	}

	public Queue<ReconnectRequest> get() {
		return this.waitingQueue;
	}

	public void addToWatingQueue(ReconnectRequest request) {
		if (isShuttingDown)
			return;

		if (this.maxSizeInWaitingQueue <= this.waitingQueue.size()) {
			return;
		}
		
		/*
		 * 如果所有服务端全部失败，就没有必要创建到最大连接数了。 waitingQueue控制在minSize + 5的量上。
		 * 增加5的增量目的：防止只有1台服务器端，默认的minIdle为0，并且启动后服务器端提前失败，造成连接失败同时waitingQueue为空，再也无法连上。
		 * 
		 * 还有一个目的是提高重试连接频率，当服务器可用时，可以更快速的连上。
		 */
		Iterator<ReconnectRequest> i = this.waitingQueue.iterator() ;
		
		//如果并发异常就忽略。丢失一个重试性连接应该影响不大。
		HashSet<java.util.Properties> s = new HashSet<java.util.Properties>() ;
		while(i.hasNext()){
			s.add(i.next().getProps()) ;
		}
		
		if(s.size() >= this.totalServers && this.waitingQueue.size() >= this.pool.getMinIdle() + 5){
			return ;
		}
		
		if (this.maxSizeInWaitingQueue > this.waitingQueue.size()) {
			this.waitingQueue.add(request);
			this.pool.setMaxActive(totalMaxLoad - this.waitingQueue.size());
		}
		
	}

	public void setProperties(GenericObjectPool pool, Properties[] ps) {
		/*
		 * load balance policy: 1. Max connection number for one server:10000 2.
		 * If pool.maxActive is not specified, set it to the average value of
		 * all set maxActives. 3. If no pool.maxActive is specified, set all to
		 * the default value:500
		 */
		LBRound lb = new LBRound();

		int totalMaxLoad = 0;
		for (Properties p : ps) {
			int maxLoad = StringUtil.toInt(p.getProperty("pool.maxActive"), 0);
			if (maxLoad > 10000) {
				maxLoad = 10000;
				p.setProperty("pool.maxActive", String.valueOf(maxLoad));
			}

			totalMaxLoad += maxLoad;
		}

		int averageLoad = totalMaxLoad / ps.length;
		if (averageLoad <= 0) {
			averageLoad = 500;
		}

		// recompute total connections allowed
		totalMaxLoad = 0;
		for (Properties p : ps) {
			int maxLoad = StringUtil.toInt(p.getProperty("pool.maxActive"), 0);
			if (maxLoad < 1) {
				maxLoad = averageLoad;
				p.setProperty("pool.maxActive", String.valueOf(maxLoad));
			}

			totalMaxLoad += maxLoad;
			lb.addToPool(p, maxLoad);
		}

		lb.applyNewPool();
		pool.setMaxActive(totalMaxLoad);

		this.totalMaxLoad = totalMaxLoad;
		this.totalServers = ps.length ;

		// max 80% in the waiting queue, others in the pool to retry again and again...
		this.maxSizeInWaitingQueue = (int) (totalMaxLoad * 0.8);
		this.lb = lb;
		this.pool = pool;
	}
	
	public void startup() {
		this.socketMonitor = new SocketMonitor() ;
		this.socketMonitor.start() ;
	}

	public void shutdown() {
		this.isShuttingDown = true;
		if(this.socketMonitor != null){
			this.socketMonitor.interrupt() ;
		}
	}
	
	/**
	 * Session monitor for healing sessions.
	 */
	class SocketMonitor extends Thread {
		public SocketMonitor() {
			this.setDaemon(true) ;
			this.setName("Heal-Socket-Thread");
		}

		@Override
		public void run() {
			SocketClientPoolFactory fac = SocketClientPoolFactory.this ;
			
			while (!fac.isShuttingDown) {
				try {
					ReconnectRequest request = fac.waitingQueue.take();
					Properties props = request.getProps() ;
					
					log.info("Trying to reconnect to [" + props + "] for " + request.getTries()	+ " times");
					
					try {
						SocketCommandService scs = new SocketCommandService(props) ;
						fac.pool.setMaxActive(fac.pool.getMaxActive() + 1) ;
						fac.pool.returnObject(scs) ;
					} catch (Exception e) {
						request.setTries(request.getTries() + 1);
						
						// update timestamp for next reconnecting
						request.updateNextReconnectTimeStamp(SocketClientPoolFactory.this.healConnectionInterval * request.getTries());
						if(log.isWarnEnabled()){
							log.warn("Reconnect to [" + props + "] failed.");
						}
						
						// add to tail
						fac.waitingQueue.offer(request);
					}
				} catch (InterruptedException e) {
					// ignore,check status
				} catch (Throwable e) {
					log.error("SocketMonitor connect error", e);
				}
			}
		}
	}

}
