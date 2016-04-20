/**
 * 
 */
package com.guzzservices.version;

/**
 * 
 * 版本控制服务。提供3项功能：<br/>
 * 
 * 1. 高效的查询一个节点在云中的最新版本号；<br/>
 * 2. 注册某个节点的监听，当节点版本升级或被删除时，获得回调通知；<br>
 * 3. 更新一个节点的版本号。
 * <p/>
 * 无论何种操作，如果传入的节点在版本控制中不存在，自动创建。新创建的节点，如果没有传入初始版本号，则默认初始为版本0.
 * <br>
 * 对于注册了节点监听器的服务，调用register时，应该自行初始化到传入的initVersion位置。对于后续的版本变动，只需要调用版本更新操作即可，本服务会对比版本，并回调操作让服务处理更新变化。
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface VersionControlService {
	
	/**
	 * 注册新版本更新时的监听器。
	 */
	public void register(String path, long initVersion, NewVersionListener listener) ;
	
	public void unregister(String path) ;
	
	/**
	 * path如：/alog/xxx
	 */
	public long getVersion(String path) ;
	
	/**
	 * 云端版本号加1。此操作为异步操作。
	 * */
	public void incVersion(String path) ;
	
	/**
	 * 将云端版本号提升到给定的新版本。此操作为同步操作。
	 * 
	 * @param path
	 * @param newVersion 提升到的新版本号
	 * @return true: 成功；false:失败。如果云端的版本号等于或高于newVersion，返回false.
	 */
	public boolean upgradeVersionTo(String path, long newVersion) ;
	
	/**
	 * 将云端此版本节点删除。
	 * 
	 * @param path
	 */
	public boolean deleteVersion(String path) ;

}
