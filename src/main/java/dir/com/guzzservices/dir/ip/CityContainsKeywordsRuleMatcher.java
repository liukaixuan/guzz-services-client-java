/**
 * CityContainsKeywordsRuleMatcher.java created by liu kaixuan(liukaixuan@gmail.com) at 8:35:00 AM on Jul 9, 2008 
 */
package com.guzzservices.dir.ip;


/**
 * 城市名称中包含ruleExpression给定的关键字。
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class CityContainsKeywordsRuleMatcher implements CityRuleMatcher {

	private String[] keywords ;
	
	public CityContainsKeywordsRuleMatcher(String[] keywords){
		this.keywords = keywords ;
		
		for(int i = 0 ; i < keywords.length ; i++){
			keywords[i] = keywords[i].toLowerCase() ;
		}
	}

	public boolean matchRule(LocateResult location) {
		if(location == null) return false ;
		String cityName = location.fullLocation().toLowerCase() ;
		
		for(int i = 0 ; i < keywords.length ; i++){
			return cityName.contains(keywords[i]) ;
		}
		
		return false ;
	}

}
