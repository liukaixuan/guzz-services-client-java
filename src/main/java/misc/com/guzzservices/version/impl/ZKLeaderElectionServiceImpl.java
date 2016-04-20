/**
 * 
 */
package com.guzzservices.version.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.guzz.exception.InvalidConfigurationException;
import org.guzz.exception.ServiceExecutionException;
import org.guzz.service.AbstractService;
import org.guzz.service.ServiceConfig;
import org.guzz.util.Assert;
import org.guzz.util.StringUtil;

import com.guzzservices.version.EnhancedLeaderService;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ZKLeaderElectionServiceImpl extends AbstractService implements EnhancedLeaderService, Watcher {

	private ZooKeeper zk;

	private boolean zkAvailable;

	private Properties props;

	private boolean isShuttingdown;

	private String lockPath;

	private volatile boolean isLeader = false;

	private long seqNum = Long.MAX_VALUE;

	private ArrayList<LeaderStatusListener> listeners = new ArrayList<LeaderStatusListener>();

	private boolean alwaysLeader;

	private boolean alwaysNotLeader;

	protected long getSeqNum(String nodeName) {
		return Long.parseLong(nodeName.substring(nodeName.lastIndexOf("seq") + 3));
	}

	public boolean configure(ServiceConfig[] scs) {
		if (scs.length > 0) {
			String alwaysLeader = scs[0].getProps().getProperty("alwaysLeader", null);
			this.alwaysLeader = Boolean.parseBoolean(alwaysLeader);

			String alwaysNotLeader = scs[0].getProps().getProperty("alwaysNotLeader", null);
			this.alwaysNotLeader = Boolean.parseBoolean(alwaysNotLeader);

			// test mode
			if (this.alwaysLeader || this.alwaysNotLeader) {
				return true;
			}

			// 10.100.4.31:2181,10.100.4.32:2181
			this.lockPath = scs[0].getProps().getProperty("lockPath", null);
			String connectString = scs[0].getProps().getProperty("connectString");
			Assert.assertNotEmpty(connectString, "missing parameter: connectString");
			Assert.assertNotEmpty(connectString, "missing parameter: lockPath");

			this.props = scs[0].getProps();
			reconnectToZK0();

			return true;
		}

		return false;
	}

	protected void shutdownZK0() {
		if (zk != null) {
			try {
				zk.close();
			} catch (Exception e) {
				log.error("exception on closing zookeeper.", e);
			}
		}
	}

	protected void reconnectToZK0() {
		if (isShuttingdown)
			return;

		shutdownZK0();

		String connectString = props.getProperty("connectString");
		int sessionTimeout = StringUtil.toInt(props.getProperty("sessionTimeout"), 8 * 60 * 60 * 1000);

		log.info("connecting to zookeeper:" + connectString);

		try {
			zk = new ZooKeeper(connectString, sessionTimeout, this);
			zkAvailable = true;

			this.zk.getChildren(lockPath, true);

			try {
				String actualPath = this.zk.create(lockPath + "/seq", new byte[] { 'a' }, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
				this.seqNum = this.getSeqNum(actualPath);

			} catch (KeeperException e) {
				throw new ServiceExecutionException("zookeeper KeeperException while creating node under :[" + lockPath + "]", e);
			} catch (InterruptedException e) {
				throw new ServiceExecutionException("zookeeper InterruptedException while creating node under :[" + lockPath + "]", e);
			}
		} catch (Exception e) {
			zkAvailable = false;

			throw new InvalidConfigurationException("unable to start zookeeper", e);
		}
	}

	public boolean isAvailable() {
		return zkAvailable;
	}

	public void shutdown() {
		this.isShuttingdown = true;
		shutdownZK0();
	}

	public void startup() {
	}

	public void process(WatchedEvent event) {
		if (isShuttingdown) {
			return;
		}

		if (event.getType() == Event.EventType.None) {
			// We are are being told that the state of the
			// connection has changed
			switch (event.getState()) {
			case SyncConnected:
				// In this particular example we don't need to do anything
				// here - watches are automatically re-registered with
				// server and any watches triggered while the client was
				// disconnected will be delivered (in order of course)
				break;
			case Expired:
				// It's all over
				zkAvailable = false;

				// try to reconnect
				reconnectToZK0();
				break;
			case AuthFailed:
				// It's all over
				zkAvailable = false;
				break;
			}
		} else if (event.getPath().equals(this.lockPath)) {
			try {
				if (log.isInfoEnabled()) {
					log.info("lock [" + this.lockPath + "] changed. event:" + event);
				}

				List<String> children = this.zk.getChildren(lockPath, true);

				if (event.getType() == Event.EventType.NodeChildrenChanged) {
					long minSeq = this.seqNum;

					for (String s : children) {
						long num = this.getSeqNum(s);
						minSeq = Math.min(num, minSeq);

						if (log.isInfoEnabled()) {
							log.info("child " + s + ", this.seqNum=" + this.seqNum);
						}
					}

					boolean oldStatus = this.isLeader;

					// Leader
					if (this.seqNum == minSeq) {
						if (!this.isLeader) {
							this.isLeader = true;
							log.info("promote to leader for lock:" + lockPath);
						} else {
							log.info("keep leader for lock:" + lockPath);
						}
					} else {
						if (this.isLeader) {
							log.info("release leader for lock:" + lockPath);
						} else {
							log.info("keep follower for lock:" + lockPath);
						}

						this.isLeader = false;
					}

					notifyLeaderStatusChanged(oldStatus, this.isLeader);
				}
			} catch (KeeperException e) {
				throw new ServiceExecutionException("zookeeper KeeperException while fetching children under :[" + lockPath + "]", e);
			} catch (InterruptedException e) {
				throw new ServiceExecutionException("zookeeper InterruptedException while fetching children under :[" + lockPath + "]", e);
			}
		}

		// 别的事件没有兴趣
	}

	public boolean amILeader() {
		if (this.alwaysLeader)
			return true;
		if (this.alwaysNotLeader)
			return false;

		return this.isLeader;
	}

	public void addLeaderStatusListener(LeaderStatusListener listener) {
		listeners.add(listener);
	}

	public void removeLeaderStatusListener(LeaderStatusListener listener) {
		listeners.remove(listener);
	}

	public void notifyLeaderStatusChanged(boolean oldIsLeader, boolean nowLeader) {
		for (LeaderStatusListener l : this.listeners) {
			try {
				l.leaderStatusChanged(oldIsLeader, nowLeader);
			} catch (Throwable t) {
				log.error("failed to notify the leader status changed event to listener:" + l, t);
			}
		}
	}

}
