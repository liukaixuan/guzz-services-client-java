/**
 * CityNoKeywordsRuleMatcher.java created by liu kaixuan(liukaixuan@gmail.com) at 8:38:59 AM on Jul 9, 2008 
 */
package com.guzzservices.dir.ip;


/**
 * 城市名称中没有ruleExpression给定的关键字。
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class CityNoKeywordsRuleMatcher implements CityRuleMatcher {

	private String[] keywords ;
	
	public CityNoKeywordsRuleMatcher(String[] keywords){
		this.keywords = keywords ;
		
		for(int i = 0 ; i < keywords.length ; i++){
			keywords[i] = keywords[i].toLowerCase() ;
		}
	}


	public boolean matchRule(LocateResult location) {
		if(location == null) return true ;
		String cityName = location.fullLocation().toLowerCase() ;
		
		for(int i = 0 ; i < keywords.length ; i++){
			if(cityName.contains(keywords[i])){
				return false ;
			}
		}
		
		return true ;
	}

}
