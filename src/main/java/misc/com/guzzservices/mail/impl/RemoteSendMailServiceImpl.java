/**
 * 
 */
package com.guzzservices.mail.impl;

import java.util.HashMap;

import com.guzzservices.mail.SendMailService;
import com.guzzservices.rpc.JsonCommandService;

/**
 * 
 * send email through remote Send Mail Service command.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class RemoteSendMailServiceImpl extends JsonCommandService<HashMap<String, String>, String> implements SendMailService {
	
	public static final String SEND_HTML_MAIL = "gs.mail.s.html" ;
	
	public static final String SEND_PLAIN_TEXT_MAIL = "gs.mail.s.plain" ;
	
	public boolean sendHtmlMail(String from, String to, String subject, String content) {
		HashMap<String, String> params = new HashMap<String, String>() ;
		params.put("from", from) ;
		params.put("to", to) ;
		params.put("subject", subject) ;
		params.put("content", content) ;
		
		try {
			return "true".equals(this.executeJsonCommand(SEND_HTML_MAIL, params)) ;
		} catch (Exception e) {
			log.error("send html mail to :" + to, e) ;
			
			return false;
		}
	}

	public boolean sendPlainMail(String from, String to, String subject, String content) {
		HashMap<String, String> params = new HashMap<String, String>() ;
		params.put("from", from) ;
		params.put("to", to) ;
		params.put("subject", subject) ;
		params.put("content", content) ;
		
		try {
			return "true".equals(this.executeJsonCommand(SEND_PLAIN_TEXT_MAIL, params)) ;
		} catch (Exception e) {
			log.error("send plain text mail to :" + to, e) ;
			
			return false;
		}
	}

}
