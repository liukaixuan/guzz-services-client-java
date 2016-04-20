/**
 * 
 */
package com.guzzservices.text.impl;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class HtmlExtractRequest implements Serializable {
	
	public static final String COMMAND_EXTRACT_HTML_TO_PLAIN = "gs.cnt.cvt.h2plain" ;

	private String htmlText ;
	
	private int resultLengthLimit ;
	
	private int imageCountLimit ;
	
	private Map tips ;

	public String getHtmlText() {
		return htmlText;
	}

	public void setHtmlText(String htmlText) {
		this.htmlText = htmlText;
	}

	public int getResultLengthLimit() {
		return resultLengthLimit;
	}

	public void setResultLengthLimit(int resultLengthLimit) {
		this.resultLengthLimit = resultLengthLimit;
	}

	public int getImageCountLimit() {
		return imageCountLimit;
	}

	public void setImageCountLimit(int imageCountLimit) {
		this.imageCountLimit = imageCountLimit;
	}

	public Map getTips() {
		return tips;
	}

	public void setTips(Map tips) {
		this.tips = tips;
	}
		
}
