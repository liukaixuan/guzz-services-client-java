/**
 * OutChinaMainlandRuleMatcher.java created by liu kaixuan(liukaixuan@gmail.com) at 9:43:46 AM on Jul 9, 2008 
 */
package com.guzzservices.dir.ip.china;

import com.guzzservices.dir.ip.LocateResult;


/**
 * 
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 * @date Jul 9, 2008 9:43:46 AM
 */
public class OutChinaMainlandRuleMatcher extends ChinaMainlandRuleMatcher {

	public boolean matchRule(LocateResult location) {
		if(location == null) return false ;
		
		return !super.matchRule(location) ;
	}
}
