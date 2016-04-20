/**
 * 
 */
package com.guzzservices.text.impl;

import java.util.HashMap;
import java.util.Map;

import org.guzz.service.ServiceConfig;
import org.guzz.util.StringUtil;

import com.guzzservices.rpc.JsonCommandService;
import com.guzzservices.text.Html2PlainExtractService;
import com.guzzservices.text.PlainExtractResult;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class RemoteHtml2PlainExtractServiceImpl extends JsonCommandService<HtmlExtractRequest, PlainExtractResult> implements Html2PlainExtractService {

	private Map<String, String> tips ;
	
	public PlainExtractResult extractText(String htmlText) throws Exception {
		return extractTextWithImage(htmlText, 0, 0) ;
	}
	
	public PlainExtractResult extractText(String htmlText, int resultLengthLimit) throws Exception {
		return extractTextWithImage(htmlText, resultLengthLimit, 0) ;
	}

	public PlainExtractResult extractTextWithAllImages(String htmlText, int resultLengthLimit) throws Exception {
		return extractTextWithImage(htmlText, resultLengthLimit, -1) ;
	}

	public PlainExtractResult extractTextWithImage(String htmlText, int resultLengthLimit, int imageCountLimit) throws Exception {
		if(StringUtil.isEmpty(htmlText)){
			PlainExtractResult r = new PlainExtractResult() ;
			r.setPlainText(htmlText) ;
			
			return r ;
		}
		
		HtmlExtractRequest r = new HtmlExtractRequest() ;
		r.setHtmlText(htmlText) ;
		r.setImageCountLimit(imageCountLimit) ;
		r.setResultLengthLimit(resultLengthLimit) ;
		r.setTips(this.tips) ;
		
		return super.executeJsonCommand(HtmlExtractRequest.COMMAND_EXTRACT_HTML_TO_PLAIN, r) ;
	}
	
	public boolean configure(ServiceConfig[] scs) {
		if(!super.configure(scs)) return false ;
		
		if(scs.length > 0){
			HashMap<String, String> tips = new HashMap<String, String>() ;
			
			String keepA = scs[0].getProps().getProperty("keepA") ;
			if("true".equalsIgnoreCase(keepA)){
				tips.put("keepA", "true") ;
				tips.put("keepATarget", scs[0].getProps().getProperty("keepATarget")) ;
			}
			
			if(tips.isEmpty()){
				this.tips = null ;
			}else{
				this.tips = tips ;
			}
		}
		
		return true ;
	}
}
