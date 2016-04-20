/**
 * CityRuleMatcher.java created by liu kaixuan(liukaixuan@gmail.com) at 5:17:35 PM on Jul 8, 2008 
 */
package com.guzzservices.dir.ip;


/**
 * 
 * 规则匹配器。用于匹配一个城市是否符合条件。
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public interface CityRuleMatcher {
	
	public boolean matchRule(LocateResult location) ;
		
}
