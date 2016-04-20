/**
 * 
 */
package com.guzzservices.version;

import org.guzz.service.core.LeaderService;

/**
 * 
 * Enhanced {@link LeaderService} with leader status changed notifier hook.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface EnhancedLeaderService extends LeaderService{

	/**
	 * The added listener will receive ONLY the events AFTER this operation.
	 * 
	 * The current status of the {@link LeaderService} won't send again to the listener.
	 * 
	 */
	public void addLeaderStatusListener(LeaderStatusListener listener) ;
	
	public void removeLeaderStatusListener(LeaderStatusListener listener) ;
	
	
	/**
	 * Leader status changed Listener.
	 */
	public static interface LeaderStatusListener{
		
		/**
		 * Leader status changed trigged. 
		 * <p/>
		 * The oldIsLeader's value could be equal to the new nowLeader's value. Be care!
		 * 
		 * @param oldIsLeader Is leader before this change?
		 * @param nowLeader Is leader now in the new status?
		 */
		public boolean leaderStatusChanged(boolean oldIsLeader, boolean nowLeader) ;
		
	}
	
	
}
