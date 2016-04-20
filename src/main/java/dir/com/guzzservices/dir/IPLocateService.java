/**
 * IPLocationService.java created at 2009-10-19 下午05:11:01 by liukaixuan@gmail.com
 */
package com.guzzservices.dir;

import org.guzz.service.FutureResult;

import com.guzzservices.dir.ip.LocateResult;

/**
 * 
 * IP到地理位置的反查服务。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface IPLocateService {
	
	public FutureResult<LocateResult> findLocationInFuture(String IP) ;
	
	public LocateResult findLocation(String IP) ;

}
