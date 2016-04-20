/**
 * ChinaTaiwanRuleMatcher.java created by liu kaixuan(liukaixuan@gmail.com) at 8:55:35 AM on Jul 9, 2008 
 */
package com.guzzservices.dir.ip.china;

import com.guzzservices.dir.ip.CityRuleMatcher;
import com.guzzservices.dir.ip.LocateResult;

/**
 * 是否 港澳台 地区？
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class ChinaTaiwanRuleMatcher implements CityRuleMatcher {

	private static String[] mainlandMarkers = {
		"台湾", "台北", "高雄", "香港", "澳门"
	} ;
	
	public boolean matchRule(LocateResult location) {
		if(location == null) return true ;
		String locationName = location.fullLocation() ;
		
		for(int i = 0 ; i < mainlandMarkers.length ; i++){
			if(locationName.contains(mainlandMarkers[i])){
				return true ;
			}
		}
		
		return false ;
	}

}
