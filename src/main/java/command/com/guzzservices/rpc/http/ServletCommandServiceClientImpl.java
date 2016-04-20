/**
 * 
 */
package com.guzzservices.rpc.http;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.guzz.service.AbstractService;
import org.guzz.service.ServiceConfig;
import org.guzz.util.FileUtil;

import com.guzzservices.rpc.CommandException;
import com.guzzservices.rpc.CommandService;

/**
 * 
 * Execute a command through HTTP servlet interface.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ServletCommandServiceClientImpl extends AbstractService implements CommandService {
	
	private String servletUrl ;
	
	private String authKey ;

	public String executeCommand(String command, String param) throws Exception {
		return executeCommand(command, true, param) ;
	}

	public byte[] executeCommand(String command, byte[] param) throws Exception {
		String resultInString = executeCommand(command, false, Base64.encodeBase64String(param)) ;
		
		if(resultInString == null){
			return null ;
		}else if(resultInString.length() == 0){
			return new byte[0] ;
		}else{
			return Base64.decodeBase64(resultInString) ;
		}
	}
	
	public ByteBuffer executeCommand(String command, ByteBuffer param) throws Exception {
		byte[] bs = null ;
		if(param == null){
			
		}else if(param.remaining() == 0){
			bs = new byte[0] ;
		}else{
			bs = new byte[param.remaining()] ;
			param.get(bs) ;
			param.clear() ;
		}
		
		byte[] result = this.executeCommand(command, bs) ;
		
		return ByteBuffer.wrap(result) ;
	}
	
	public String executeCommand(String command, boolean isStringParam, String param) throws Exception {
		HttpClient client = new DefaultHttpClient() ;
				
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("command", command));
		formparams.add(new BasicNameValuePair("isStringParam", isStringParam ? "1" : "0"));
		formparams.add(new BasicNameValuePair("param", param));
		if(this.authKey != null){
			formparams.add(new BasicNameValuePair("authKey", authKey));
		}
		
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		
		HttpPost httppost = new HttpPost(servletUrl) ;
		httppost.setEntity(entity);
		
		HttpResponse response = client.execute(httppost) ;
		
		boolean isException = true ;
		boolean isString ;
		int guzzCommandServiceLength = 0 ;
		
		Header[] headers = response.getAllHeaders() ;
		for(Header h : headers){
			String key = h.getName() ;
			
			if("guzzCommandServiceException".equalsIgnoreCase(key)){
				isException = "1".equals(h.getValue()) ;
			}else if("guzzCommandServiceString".equalsIgnoreCase(key)){
				isString = "1".equals(h.getValue()) ;
			}else if("guzzCommandServiceLength".equalsIgnoreCase(key)){
				guzzCommandServiceLength = Integer.parseInt(h.getValue()) ;
			}
		}
		
		String resultValue = null ; 
		
		if(guzzCommandServiceLength == 0){
			resultValue = "" ;
		}else if(guzzCommandServiceLength > 0){
			resultValue = FileUtil.readText(response.getEntity().getContent(), "UTF-8") ;
		}
		
		if(isException){
			throw new CommandException(resultValue) ;
		}
		
		return resultValue ;
	}

	public boolean configure(ServiceConfig[] scs) {
		if(scs.length == 0){
			log.warn("CommandService Service is not started. no configuration found.") ;
			return false ;
		}else{
			this.servletUrl = scs[0].getProps().getProperty("servletUrl") ;
			this.authKey = scs[0].getProps().getProperty("authKey") ;
			
			return true ;
		}
	}

	public boolean isAvailable() {
		return servletUrl != null ;
	}
	
	public void shutdown() {
		servletUrl = null ;
	}

	public void startup() {
	}
}
