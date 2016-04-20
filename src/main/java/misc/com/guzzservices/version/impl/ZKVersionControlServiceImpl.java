/**
 * 
 */
package com.guzzservices.version.impl;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.guzz.exception.InvalidConfigurationException;
import org.guzz.exception.ServiceExecutionException;
import org.guzz.service.AbstractService;
import org.guzz.service.ServiceConfig;
import org.guzz.util.Assert;
import org.guzz.util.StringUtil;

import com.guzzservices.version.NewVersionListener;
import com.guzzservices.version.VersionControlService;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ZKVersionControlServiceImpl extends AbstractService implements VersionControlService, Watcher {
	
	public String prefixPath = "/v" ;
	
	private ZooKeeper zk ;
	
	private boolean zkAvailable ;
	
	private Properties props ;
	
	private boolean isShuttingdown ;
	
	private Map<String, NewVersionDataCallback> listeners = new ConcurrentHashMap<String, NewVersionDataCallback>() ;
	
	protected String normalizePath(String path){
		if(!zkAvailable){
			throw new ServiceExecutionException("zookeeper is not available.") ;
		}
		
		if(prefixPath != null){
			return prefixPath + path ;
		}else{
			return path ;
		}
	}
	
	protected String unNormalizePath(String normalizedPath){
		if(prefixPath != null){
			return normalizedPath.substring(prefixPath.length()) ;
		}else{
			return normalizedPath ;
		}
	}

	public long getVersion(String path) {
		//如果注册了监听，本地版本应该和云端版本一样。
		NewVersionDataCallback callback = this.listeners.get(path) ;
		if(callback != null){
			return callback.getLocalVersion() ;
		}
		
		String normalizedPath = normalizePath(path) ;
		
		try {
			byte[] bs = this.zk.getData(normalizedPath, false, null) ;
			
			return bytes2long(bs) ;
		} catch (KeeperException e) {
			if(Code.NONODE.equals(e.code())){
				return createVersionNode(normalizedPath, 0) ;
			}
			
			throw new ServiceExecutionException("zookeeper KeeperException while getting:[" + normalizedPath + "]", e) ;
		} catch (InterruptedException e) {
			throw new ServiceExecutionException("zookeeper InterruptedException while getting:[" + normalizedPath + "]", e) ;
		}
	}
	
	//创建节点，返回实际已经创建节点的版本。
	protected long createVersionNode(String normalizedPath, long initVersion){
		String[] subNames = normalizedPath.split("/") ;
		String folderNow = "/" ;
		
		for(String sn : subNames){
			if(sn.length() == 0) continue ;
			folderNow += sn ;
			
			try {
				if(this.zk.exists(folderNow, false) == null){
					//节点不存在，则创建。
					this.zk.create(folderNow, long2bytes(initVersion), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT) ;
				}
			} catch (KeeperException e) {
				//在本机器的创建过程中，其他机器已经创建了。
				if(Code.NODEEXISTS.equals(e.code()) && normalizedPath.equals(folderNow)){
					try {
						return bytes2long(this.zk.getData(normalizedPath, false, null)) ;
					} catch (KeeperException e1) {
						throw new ServiceExecutionException("zookeeper KeeperException while getting:[" + normalizedPath + "]", e1) ;
					} catch (InterruptedException e1) {
						throw new ServiceExecutionException("zookeeper KeeperException while getting:[" + normalizedPath + "]", e1) ;
					}
				}else{
					throw new ServiceExecutionException("zookeeper KeeperException while creating:[" + normalizedPath + "]", e) ;
				}
			} catch (InterruptedException e) {
				throw new ServiceExecutionException("zookeeper InterruptedException while creating:[" + normalizedPath + "]", e) ;
			}
			
			folderNow += "/" ;
		}
		
		return initVersion ;
	}

	public void incVersion(String path) {
		String normalizedPath = normalizePath(path) ;
		
		this.zk.getData(normalizedPath, false, new DataCallback(){

			public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
				if(rc == Code.NoNode){//节点不存在就创建
					((ZKVersionControlServiceImpl) ctx).createVersionNode(path, 1) ;
					return ;
				}
				
				long version = bytes2long(data) ;
				version++ ;
				
				try {
					((ZKVersionControlServiceImpl) ctx).zk.setData(path, long2bytes(version), stat.getVersion()) ;
				} catch (KeeperException e) {
					if(Code.BADVERSION.equals(e.code())){
						//修改冲突。用同步的方式，尝试3次，再不行才抛错。
						for(int i = 0 ; i < 3 ; i++){
							try {
								Stat s2 = new Stat() ;
								byte[] data2 = ((ZKVersionControlServiceImpl) ctx).zk.getData(path, false, s2);
								long v2 = bytes2long(data2) ;
								v2++ ;
							
								((ZKVersionControlServiceImpl) ctx).zk.setData(path, long2bytes(v2), s2.getVersion()) ;
								
								return ;
							} catch (KeeperException e2) {
								log.error("zookeeper KeeperException while inc version:[" + path + "]", e2) ;
							} catch (InterruptedException e2) {
								log.error("zookeeper InterruptedException while inc version:[" + path + "]", e2) ;
							}
						}
					}
					
					log.error("zookeeper KeeperException while inc version:[" + path + "]", e) ;
				} catch (InterruptedException e) {
					log.error("zookeeper InterruptedException while inc version:[" + path + "]", e) ;
				}
			}
			
		}, this) ;
	}

	public boolean upgradeVersionTo(String path, long newVersion) {
		String normalizedPath = normalizePath(path) ;
		Stat stat = new Stat() ;
		byte[] data;
		boolean successed = false ;
		
		//允许重试2次
		for(int i = 0 ; i < 3 ; i++){
			try {
				data = this.zk.getData(normalizedPath, false, stat);
				long version = bytes2long(data) ;		
				if(version >= newVersion) return false ;
			
				this.zk.setData(normalizedPath, long2bytes(newVersion), stat.getVersion()) ;
				successed = true ;
			} catch (KeeperException e) {
				if(Code.NONODE.equals(e.code())){
					return createVersionNode(normalizedPath, newVersion) == newVersion ;
				}
				
				log.error("zookeeper KeeperException while upgrading version:[" + path + "]", e) ;
			} catch (InterruptedException e) {
				log.error("zookeeper InterruptedException while upgrading version:[" + path + "]", e) ;
			}
		}
		
		return successed ;
	}

	public void register(String path, long initVersion, NewVersionListener listener) {
		NewVersionDataCallback callback = this.listeners.get(path) ;
		Assert.assertNull(callback, "listener already registered for path:[" + path + "]") ;
				
		callback = new NewVersionDataCallback(listener, initVersion, path) ;
		this.listeners.put(path, callback) ;
		
		//确认已经创建
		String normalizedPath = normalizePath(path) ;
		try {
			if(this.zk.exists(normalizedPath, false) == null){
				//节点不存在，则创建。
				createVersionNode(normalizedPath, initVersion) ;
			}
		} catch (KeeperException e) {
		} catch (InterruptedException e) {
		}

		this.zk.getData(normalizedPath, true, callback, null) ;
	}

	public void unregister(String path) {
		this.listeners.remove(path) ;
	}
	
	public boolean deleteVersion(String path){
		String normalizedPath = normalizePath(path) ;
		
		try {
			this.zk.delete(normalizedPath, -1) ;
			return true ;
		} catch (InterruptedException e) {
			log.error("zookeeper InterruptedException while deleting version:[" + path + "]", e) ;
		} catch (KeeperException e) {
			if(Code.NONODE.equals(e.code())){
				return true ;
			}
			
			log.error("zookeeper KeeperException while deleting version:[" + path + "]", e) ;
		}
		
		return false ;
	}
	
	public boolean configure(ServiceConfig[] scs) {
		if(scs.length > 0){
			this.prefixPath = scs[0].getProps().getProperty("prefixPath", this.prefixPath) ;
			
			//10.100.4.31:2181,10.100.4.32:2181
			String connectString = scs[0].getProps().getProperty("connectString") ;
			Assert.assertNotEmpty(connectString, "missing parameter: connectString") ;

			
			this.props = scs[0].getProps() ;
			
			reconnectToZK0() ;
		}
		
		return true;
	}
	
	protected void shutdownZK0(){		
		if(zk != null){
			try {
				zk.close() ;
			} catch (Exception e) {
				log.error("exception on closing zookeeper.", e) ;
			}
		}
	}
	
	protected void reconnectToZK0(){
		if(isShuttingdown) return ;
		
		shutdownZK0() ;
		
		String connectString = props.getProperty("connectString") ;
		int sessionTimeout = StringUtil.toInt(props.getProperty("sessionTimeout"), 8*60*60*1000) ;
		
		log.info("connecting to zookeeper:" + connectString) ;
		
		try {
			zk = new ZooKeeper(connectString, sessionTimeout, this) ;

			zkAvailable = true ;
			
			//重新绑定监听
			for(NewVersionDataCallback callback : this.listeners.values()){
				String normalizedPath = this.normalizePath(callback.path) ;
				
				try {
					this.zk.getData(normalizedPath, true, null) ;
				} catch (KeeperException e) {
					log.error("zookeeper InterruptedException while getting:[" + normalizedPath + "]", e) ;
				} catch (InterruptedException e) {
					log.error("zookeeper InterruptedException while getting:[" + normalizedPath + "]", e) ;
				}
			}
		} catch (Exception e) {
			zkAvailable = false ;
			
			throw new InvalidConfigurationException("unable to start zookeeper", e) ;
		}
	}

	public boolean isAvailable() {
		return zkAvailable;
	}

	public void shutdown() {
		this.isShuttingdown = true ;
		shutdownZK0() ;
	}

	public void startup() {
	}
	
	public void process(WatchedEvent event) {
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
            	
            	//try to reconnect
            	reconnectToZK0() ;
                break;
            case AuthFailed:
                // It's all over
            	zkAvailable = false;
                break;
            }
        }else{
    		String normalizedPath = event.getPath() ;
    		String path = this.unNormalizePath(normalizedPath) ;
    		
    		NewVersionDataCallback callback = this.listeners.get(path) ;
    		if(callback != null){//registered
    			if(event.getType() == Event.EventType.NodeDataChanged){
	    			try {
						byte[] data = this.zk.getData(normalizedPath, true, null) ;
						
						long newVersion = bytes2long(data) ;
						callback.notifyVersionChanged(newVersion) ;
					} catch (KeeperException e) {
						log.error("zookeeper InterruptedException while getting:[" + normalizedPath + "]", e) ;
					} catch (InterruptedException e) {
						log.error("zookeeper InterruptedException while getting:[" + normalizedPath + "]", e) ;
					}
    			}else if(event.getType() == Event.EventType.NodeDeleted){
    				this.listeners.remove(path) ;
    	        	callback.notifyVersionDeleted() ;
            	}
    		}
		}
        //别的事件没有兴趣
	}
	
	public static long bytes2long(byte[] data){
		try {
			String s = new String(data, "ISO-8859-1") ;
			
			return Long.valueOf(s) ;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			
			return -1 ;
		}
	}
	
	public static byte[] long2bytes(long l){
		try {
			return String.valueOf(l).getBytes("ISO-8859-1") ;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			
			return String.valueOf(l).getBytes() ;
		}
	}
	
	
	static class NewVersionDataCallback implements DataCallback{
		protected transient final Log log = LogFactory.getLog(this.getClass()) ;
		
		private NewVersionListener listener ;
		
		private long localVersion ;
		
		private final String path ;
		
		public NewVersionDataCallback(NewVersionListener listener, long localVersion, String path){
			this.listener = listener ;
			this.localVersion = localVersion ;
			this.path = path ;
		}

		//version changed by other machines while registering
		public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
			notifyVersionChanged(bytes2long(data)) ;
		}

		//version changed by other machines
		public void notifyVersionChanged(long newVersion) {	
			long localVersion = this.localVersion ;
			if(newVersion > localVersion){
				if(log.isInfoEnabled()){
					log.info("[" + path + "] upgrading from version " + this.localVersion + " to " + newVersion) ;
				}
				
				this.localVersion = newVersion ;
				listener.onNewVersion(this.path, localVersion, newVersion) ;
			}
		}
		
		public void notifyVersionDeleted() {
			if(log.isInfoEnabled()){
				log.info("[" + path + "] version deleted. localVersion " + this.localVersion) ;
			}
			
			listener.onVersionDeleted(path, localVersion) ;
		}

		public NewVersionListener getListener() {
			return listener;
		}

		public void setListener(NewVersionListener listener) {
			this.listener = listener;
		}

		public long getLocalVersion() {
			return localVersion;
		}

		public void setLocalVersion(long localVersion) {
			this.localVersion = localVersion;
		}
		
	}

}
