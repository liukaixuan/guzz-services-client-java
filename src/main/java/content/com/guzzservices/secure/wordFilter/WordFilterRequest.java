/**
 * 
 */
package com.guzzservices.secure.wordFilter;

import java.io.Serializable;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class WordFilterRequest implements Serializable {
	
	public static final String COMMAND_FILTER_HTML = "gs.fw.f.html" ;
	
	public static final String COMMAND_FILTER_TEXT = "gs.fw.f.text" ;
	
	private String content;
	
	private String[] groupNames;
	
	private boolean markContent ;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String[] getGroupNames() {
		return groupNames;
	}

	public void setGroupNames(String[] groupNames) {
		this.groupNames = groupNames;
	}

	public boolean isMarkContent() {
		return markContent;
	}

	public void setMarkContent(boolean markContent) {
		this.markContent = markContent;
	}

}
