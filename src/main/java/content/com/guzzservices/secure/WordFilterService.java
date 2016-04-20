package com.guzzservices.secure;

import com.guzzservices.secure.wordFilter.MatchResult;


/**
 * Filter bad words and mark it.
 *
 * @author liukaixuan
 */
public interface WordFilterService {
	
	/**
	 * 过滤一段文字，根据参数决定是否标红。如果不含有任何过滤词，返回null。
	 * 
	 * @param content 检测内容
	 * @param groupName[] 过滤词组编号
	 * @param markContent 是否同时标红过滤的内容。
	 * @return MatchResult
	 */
	public MatchResult filterText(String content, String[] groupNames, boolean markContent) throws Exception ;
	
	/**
	 * 过滤一段html代码段，根据参数决定是否标红。如果不含有任何过滤词，返回null。
	 * 
	 * @param content 检测内容
	 * @param groupNames 过滤词组编号
	 * @param markContent 是否同时标红过滤的内容。
	 * @return MatchResult
	 */
	public MatchResult filterHtml(String content, String[] groupNames, boolean markContent) throws Exception ;
	
}
