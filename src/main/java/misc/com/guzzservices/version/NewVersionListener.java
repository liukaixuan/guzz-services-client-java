/**
 * 
 */
package com.guzzservices.version;

/**
 * 
 * 新版本可用时的通知回调接口。
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface NewVersionListener {
	
	/**
	 * 通知新版本可用。
	 * 
	 * @param path path
	 * @param localVersion 本机目前使用的旧版本号
	 * @param newVersion 最新的新版本号
	 */
	public void onNewVersion(String path, long localVersion, long newVersion) ;

	/**
	 * 节点被从版本控制中删除。在此方法调用前，path的监听器已经自动删除。
	 * 
	 * @param path path
	 * @param localVersion 删除时，本机的版本号
	 */
	public void onVersionDeleted(String path, long localVersion) ;
	
}
