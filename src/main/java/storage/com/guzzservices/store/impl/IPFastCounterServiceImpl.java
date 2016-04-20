/**
 * IPFastCounterServiceImpl.java created at 2009-12-17 下午04:57:40 by liukaixuan@gmail.com
 */
package com.guzzservices.store.impl;

import org.guzz.service.AbstractService;
import org.guzz.service.ServiceConfig;
import org.guzz.util.StringUtil;

import com.guzzservices.store.IPFastCounterService;

/**
 * 
 * 本服务可配置倒计时周期counterInterval，单位为分钟。<p/>
 * 每当到达1个counterInterval周期时，系统自动减少每个统计IP的统计值，使得统计值在IP计数较少（正常）时可以随着时间的推移逐步递减，完成类似“自动解封IP”的功能。
 * 
 * counterInterval配置为0，则不进行倒计时。默认倒计时周期为0。
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class IPFastCounterServiceImpl extends AbstractService implements IPFastCounterService {
	
	private int counterInterval = 0 ;
	
	/**按照D类地址统计IP（同一D类地址下的IP算做同一个IP）*/
	private boolean blockD = true ;

	protected int[] ipCount = new int[65536] ;
	
	private DemonCounterDown demonCounterDown ;
	
	public int hashIP(String IP) {
		//算法：后两位相乘
		String[] segs = IP.split("\\.") ;
		
		if(blockD){
			int a = Integer.parseInt(segs[1]) ;
			int b = Integer.parseInt(segs[2]) ;
			
			return a * 256 + b;
		}else{
			int a = Integer.parseInt(segs[2]) ;
			int b = Integer.parseInt(segs[3]) ;
			return a * 256 + b;
		}
	}
	
	public int getCount(int ipHash) {
		return this.ipCount[ipHash] ;
	}

	public int incCount(int ipHash, int countToInc) {
		int value = this.ipCount[ipHash] + countToInc ;
		this.ipCount[ipHash] = value ;
		 
		return value ;
	}

	public void startup() {
		if(counterInterval > 0){
			demonCounterDown = new DemonCounterDown() ;
			demonCounterDown.start() ;
		}
	}

	public boolean configure(ServiceConfig[] scs) {
		if(scs == null || scs.length == 0){
			return false ;
		}
		
		String m_interval = scs[0].getProps().getProperty("counterInterval") ;
		String m_blockD = scs[0].getProps().getProperty("blockD") ;
		
		int m_counterInterval = StringUtil.toInt(m_interval, 0) ;
		this.blockD = StringUtil.toBoolean(m_blockD, true) ;
		
		if(m_counterInterval > 0){
			this.counterInterval = m_counterInterval * 60 * 1000 ;
		}
		
		return true;
	}

	public boolean isAvailable() {
		return true;
	}

	public void shutdown() {
		if(this.demonCounterDown != null){
			this.demonCounterDown.shutdown() ;
			this.demonCounterDown = null ;
		}
	}
	
	class DemonCounterDown extends Thread{
		
		private long lastCleanTime ;
		
		private boolean keepRunning = true ;
		
		public DemonCounterDown(){
			this.setDaemon(true) ;
		}

		public void shutdown(){
			this.keepRunning = false ;
			
			synchronized (this) {
				this.notifyAll() ;
			}
		}
		
		public void run() {
			while(keepRunning){
				try {
					long now = System.currentTimeMillis() ;
					
					if(now - lastCleanTime >= counterInterval){
						lastCleanTime = now ;
						doClean() ;
					}
					
					synchronized (this) {
						this.wait(counterInterval) ;				
					}
				} catch (Exception e) {
				}
			}
		}
		
		protected void doClean(){
			for(int i = 0 ; i < ipCount.length ; i++){
				//递减算法：value = value - value*10% - 2 ;
				int value = ipCount[i] ;
				
				//不处理0和负数
				if(value < 1) continue ;
				
				if(value < 3){
					value = 0 ;
				}else if(value < 5){
					value = value - 2 ;
				}else{
					value = value - (int) (value * 0.1) - 2 ;
				}
				
				ipCount[i] = value ;
			}
		}
		
	}

}
