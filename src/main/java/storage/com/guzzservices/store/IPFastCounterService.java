/**
 * IPFastCounterService.java created at 2009-12-17 下午04:51:46 by liukaixuan@gmail.com
 */
package com.guzzservices.store;

/**
 * 
 * 快速IP计数器，一般用于在应用端判断刷票机之类恶意操作，以直接拒绝请求。避免进行资源消耗较多的cache查询操作。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface IPFastCounterService {
	
	/**将IP散列为服务使用的数字*/
	public int hashIP(String IP) ;
	
	/**获得当前IP的值*/
	public int getCount(int ipHash) ;
	
	/**
	 * 增加计数，并返回增加后的计数。如果ipHash非法，返回-1.
	 */
	public int incCount(int ipHash, int countToInc) ;

}
