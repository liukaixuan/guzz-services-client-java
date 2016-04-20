/**
 * 
 */
package com.guzzservices.velocity;

import java.util.Map;

/**
 * 
 * velocity template engine with guzz custom directors supporting.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface VelocityService {
	
	/**
	 * translate the template to a text string.
	 * 
	 * @param data parameters for the template
	 * @param templateName template name
	 */
	public String translate(Map<String, Object> data, String templateName) throws Exception ;
	
	public String translateText(Map<String, Object> data, String logTitle, String text) throws Exception ;

}
