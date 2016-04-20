/**
 * FixedLifeCountService.java created at 2009-10-23 上午08:55:45 by liukaixuan@gmail.com
 */
package com.guzzservices.store;

import org.guzz.service.FutureResult;

/**
 * 
 * 对短时间缓存对象进行计数的服务。对象的缓存时间在第一次计数时进行设置，以后不再更改。
 * <br>
 * 该服务一般用于防止作弊等方面。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface FixedLifeCountService {
	
	/**
	 * 增加计数。
	 * @param key 计数对象的唯一标识。
	 * @param addCount 增加计数值。正数增加计数，负数减少计数值。如果为0，不更改计数值。
	 * @param maxLifeInSeconds 计数有效时间
	 * @return 返回增加此次计数后，总的计数值。默认值为null。
	 */
	public FutureResult<Integer> incCount(String key, int addCount, int maxLifeInSeconds) ;

	
	/**
	 * 如果增加计数后的值小于或等于最大允许值，增加计数，并返回true；否则不进行计数操作，并返回false。
	 * 
	 * @param key 计数对象的唯一标识。
	 * @param addCount 增加计数值。正数增加计数，负数减少计数值。如果为0，不更改计数值。
	 * @param maxCountAllowed 最大允许计数到的值
	 * @param maxLifeInSeconds 计数有效时间
	 * @return 返回增加当前计数后，计数值是否超过最大允许值( > @param maxCountAllowed)。
	 */
	public boolean incCountIfLess(String key, int addCount, int maxCountAllowed, int maxLifeInSeconds) ;
}
