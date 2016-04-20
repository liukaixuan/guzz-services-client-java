/**
 * 
 */
package com.guzzservices.secure.wordFilter;

import java.util.LinkedList;

import com.guzzservices.rpc.JsonCommandService;
import com.guzzservices.secure.WordFilterService;

/**
 * 
 * wordFilter client API--call remote service by socket in json
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class WordFilterServiceImpl extends JsonCommandService<WordFilterRequest, MatchResult> implements WordFilterService {
	
	private int maxContentLen = 100000 ;
	
	public MatchResult filterHtml(String content, String[] groupNames, boolean markContent) throws Exception {
		return doFilter(WordFilterRequest.COMMAND_FILTER_HTML, content, groupNames, markContent) ;
	}

	public MatchResult filterText(String content, String[] groupNames, boolean markContent) throws Exception {
		return doFilter(WordFilterRequest.COMMAND_FILTER_TEXT, content, groupNames, markContent) ;
	}
	
	protected MatchResult doFilter(String command, String content, String[] groupNames, boolean markContent) throws Exception {
		if(groupNames == null) return null ;
		if(groupNames.length == 0) return null ;
		if(content == null) return null ;
		if(content.length() == 0) return null ;
		
		if(content.length() > maxContentLen){
			return new MatchResult(new LinkedList<String>(), content, "too long!", 9) ;
		}
		
		WordFilterRequest r = new WordFilterRequest() ;
		r.setContent(content) ;
		r.setGroupNames(groupNames) ;
		r.setMarkContent(markContent) ;
		
		return super.executeJsonCommand(command, r) ;
	}
	
}
