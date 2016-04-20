package com.guzzservices.secure.wordFilter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.guzz.util.Assert;

/**
 * 
 * 匹配结果
 * 
 * @author liu kaixuan
 */
public class MatchResult {

	/**成功匹配的过滤词*/
	private List<String> matchedFilterWords ;
	
	/**标记以后的内容*/
	private String markedContent ;
	
	/**匹配到的内容组成的字符串列表*/
	private String hittedContentList ;
	
	private int highestLevel ;
	
	public MatchResult(){}
	
	public MatchResult(List<String> matchedFilterWords, String markedContent, String hittedContentList, int highestLevel){
		this.matchedFilterWords = matchedFilterWords ;
		this.markedContent = markedContent ;
		this.hittedContentList = hittedContentList ;
		this.highestLevel = highestLevel ;
	}
	
	/**
	 * 将发现的过滤词列表进行Distinct处理，同时统计每个词的出现次数
	 * 
	 * @return 返回Map<Word, 出现次数> 包含 Distinct 处理后的过滤词列表以及相应的出现次数
	 */
	public Map<String, Integer> groupMatchedFilterWords() {
		Map<String, Integer> count = new HashMap<String, Integer>();
		for (Iterator<String> it = matchedFilterWords.iterator(); it.hasNext();) {
			String obj = it.next();
			Integer c = (Integer) (count.get(obj));
			if (c == null) {
				count.put(obj, Integer.valueOf(1));
			} else {
				count.put(obj, Integer.valueOf(c.intValue() + 1));
			}
		}
		
		return count;
	}

	/**
	 * 返回得到的过滤词中最高警告级别
	 * 
	 * @return 最高警告级别
	 */
	public int getHighestLevel() {
		return highestLevel ;
	}
	
	/**是否可以通过给定的过滤词等级*/
	public boolean canPass(int level){
		return getHighestLevel() < level ;
	}
	
	public String getMarkedContent() {
		return markedContent;
	}

	public List<String> getMatchedFilterWords() {
		return matchedFilterWords;
	}

	public String getHittedContentList() {
		return hittedContentList;
	}
	
	/**
	 * 返回匹配的过滤词列表。方法会自动删除重复的过滤词，并且将返回字符串长度限制在@param maxLength范围内。
	 */
	public String getMatchedContentList(String wordSep, int maxLength) {
		Assert.assertBigger(maxLength, 0, "parameter maxLength must be a positive number.") ;
		
		if(hittedContentList == null || hittedContentList.length() <= maxLength) return hittedContentList ;
		
		//过滤词长度太长，压缩过滤词，将重复的删掉。
		Map<String, Integer> words = groupMatchedFilterWords() ;
		Iterator<String> i = words.keySet().iterator() ;
		StringBuffer sb = new StringBuffer(maxLength + 100) ;
		
		boolean meet = false ;
		while(i.hasNext()){
			if(meet){
				sb.append(wordSep) ;
			}else{
				meet = true ;
			}
			sb.append(i.next()) ;
		}
		
		if(sb.length() > maxLength){ //还是超
			sb.setLength(maxLength) ;
		}
		
		return sb.toString();
	}

	public void setMatchedFilterWords(List<String> matchedFilterWords) {
		this.matchedFilterWords = matchedFilterWords;
	}

	public void setMarkedContent(String markedContent) {
		this.markedContent = markedContent;
	}

	public void setHittedContentList(String hittedContentList) {
		this.hittedContentList = hittedContentList;
	}

	public void setHighestLevel(int highestLevel) {
		this.highestLevel = highestLevel;
	}

}
