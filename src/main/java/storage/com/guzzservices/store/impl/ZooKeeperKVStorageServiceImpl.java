/**
 * 
 */
package com.guzzservices.store.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.guzz.exception.InvalidConfigurationException;
import org.guzz.exception.ServiceExecutionException;
import org.guzz.service.AbstractService;
import org.guzz.service.ServiceConfig;
import org.guzz.util.Assert;
import org.guzz.util.StringUtil;

import com.guzzservices.store.KVStorageService;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ZooKeeperKVStorageServiceImpl extends AbstractService implements KVStorageService, Watcher {
	
	public String prefixPath = "/kv" ;
	
	private ZooKeeper zk ;
	
	private boolean zkAvailable ;
	
	private Properties props ;
	
	private boolean isShuttingdown ;
	
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
	
	public void storeTo(String path, String value){
		if(value == null){
			remove(path) ;
		}
		
		String normalizedPath = normalizePath(path) ;
		byte[] data = null;
		try {
			data = value.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		//允许重试2次
		for(int i = 0 ; i < 3 ; i++){
			try {
				this.zk.setData(normalizedPath, data, -1) ;
			} catch (KeeperException e) {
				if(Code.NONODE.equals(e.code())){
					createNode(normalizedPath, data) ;
				}else{
					log.error("zookeeper KeeperException while upgrading version:[" + path + "]", e) ;
				}
			} catch (InterruptedException e) {
				log.error("zookeeper InterruptedException while upgrading version:[" + path + "]", e) ;
			}
		}
	}
	
	public void asyncStoreTo(String path, String value) {
		if(value == null){
			//change to async delete.
			remove(path) ;
		}
		
		String normalizedPath = normalizePath(path) ;
		byte[] data = null;
		try {
			data = value.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		Holder h = new Holder() ;
		h.zks = this ;
		h.data = data ;
		
		this.zk.setData(normalizedPath, data, -1, new StatCallback(){

			public void processResult(int rc, String path, Object ctx, Stat stat) {
				if(rc == Code.NoNode){//节点不存在就创建
					((Holder) ctx).zks.createNode(path, ((Holder) ctx).data) ;
					return ;
				}
			}
					
		}, h) ;
	}
	
	static class Holder{
		public ZooKeeperKVStorageServiceImpl zks ;
		
		public byte[] data ;
	}
	
	public void remove(String path) {
		String normalizedPath = normalizePath(path) ;
		
		try {
			this.zk.delete(normalizedPath, -1) ;
		} catch (InterruptedException e) {
			log.error("zookeeper InterruptedException while deleting version:[" + path + "]", e) ;
		} catch (KeeperException e) {
			if(Code.NONODE.equals(e.code())){
				
			}else{
				log.error("zookeeper KeeperException while deleting version:[" + path + "]", e) ;
			}
		}
	}
	
	public String get(String path) {
		String normalizedPath = normalizePath(path) ;
		
		try {
			byte[] bs = this.zk.getData(normalizedPath, false, null) ;
			
			return new String(bs, "UTF-8") ;
		} catch (KeeperException e) {
			if(Code.NONODE.equals(e.code())){
				return null ;
			}
			
			throw new ServiceExecutionException("zookeeper KeeperException while getting:[" + normalizedPath + "]", e) ;
		} catch (InterruptedException e) {
			throw new ServiceExecutionException("zookeeper InterruptedException while getting:[" + normalizedPath + "]", e) ;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			
			return null ;
		}
	}
	
	//创建节点，返回实际已经创建节点的版本。
	protected void createNode(String normalizedPath, byte[] data){
		String[] subNames = normalizedPath.split("/") ;
		String folderNow = "/" ;
		
		for(String sn : subNames){
			if(sn.length() == 0) continue ;
			folderNow += sn ;
			
			try {
				if(this.zk.exists(folderNow, false) == null){
					//节点不存在，则创建。
					this.zk.create(folderNow, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT) ;
				}
			} catch (KeeperException e) {
				//在本机器的创建过程中，其他机器已经创建了。
				if(Code.NODEEXISTS.equals(e.code()) && normalizedPath.equals(folderNow)){
					try {
						this.zk.setData(normalizedPath, data, -1) ;
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
		} catch (IOException e) {
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
        }
        
        //别的事件没有兴趣
	}

}
