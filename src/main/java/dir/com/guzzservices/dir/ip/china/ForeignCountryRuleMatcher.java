/**
 * ForeignCountryRuleMatcher.java created at 2009-10-21 下午01:15:15 by liukaixuan@gmail.com
 */
package com.guzzservices.dir.ip.china;

import com.guzzservices.dir.ip.CityRuleMatcher;
import com.guzzservices.dir.ip.LocateResult;

/**
 * 
 * 国外地区验证，香港澳门台湾作为输入时返回false。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ForeignCountryRuleMatcher implements CityRuleMatcher {

	public boolean matchRule(LocateResult location) {
		if(location == null) return true ;
		
		return "海外".equals(location.getCityMarker()) ;
	}

}
