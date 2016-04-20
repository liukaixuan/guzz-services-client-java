/**
 * 
 */
package com.guzzservices.rpc.util;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.guzz.exception.GuzzException;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class JsonUtil {
	
	private static ObjectMapper mapper = new ObjectMapper() ;
	
	static{
		mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false) ;
	}
	
	public static String toJson(Object obj){
		if(obj == null) return null ;
		if(obj instanceof String) return (String) obj ;
		
		try {
			return mapper.writeValueAsString(obj) ;
		} catch (JsonGenerationException e) {
			throw new GuzzException(String.valueOf(obj), e) ;
		} catch (JsonMappingException e) {
			throw new GuzzException(String.valueOf(obj), e) ;
		} catch (IOException e) {
			throw new GuzzException(String.valueOf(obj), e) ;
		}
	}
	 
	public static <T> T fromJson(String json, Class<T> classOfT){
		if(json == null) return null ;
		if(String.class.isAssignableFrom(classOfT)) return (T) json ;
		
		try {
			return mapper.readValue(json, classOfT) ;
		} catch (JsonParseException e) {
			throw new GuzzException(json, e) ;
		} catch (JsonMappingException e) {
			throw new GuzzException(json, e) ;
		} catch (IOException e) {
			throw new GuzzException(json, e) ;
		}
	}
	 
	public static <T> List<T> fromJson2List(String json, Class<T> classTypeInList){
		if(json == null) return null ;
		json = json.trim() ;
		if(json.length() == 0) return null ;
		
		//list must be [aa, bb, ccc...]
		char start= json.charAt(0) ;
		char end = json.charAt(json.length() - 1) ;
		
		//error
		if(start != '[' || end != ']'){
			throw new GuzzException("json is not a List. json:" + json) ;
		}
		
		//parse the list ourself.
		int startPos = 1 ;
		int lastMeet = 1 ;
		int endPos = json.length() - 1 ;
		char[] cs = json.toCharArray() ;
		
		int deepCount = 0 ;
		LinkedList results = new LinkedList() ;
		
		for(; startPos < endPos ; startPos++){
			char c = cs[startPos] ;
						
			if(c == '{' || c == '['){
				deepCount++ ;
			}else if(c == '}' || c == ']'){
				deepCount-- ;
			}
			
			//one thing
			if(c ==',' && deepCount == 0){
				String s = new String(cs, lastMeet, startPos - lastMeet) ;
				
				if(classTypeInList.isAssignableFrom(String.class)){
					if(s.length() == 2){
						s= "" ;
					}else{
						s = s.trim() ;
						s = s.substring(1, s.length() - 1) ;
					}
					results.add(s) ;
				}else{
					results.add(fromJson(s, classTypeInList)) ;
				}
				
				lastMeet = startPos + 1 ;
			}
		}
		
		//最后一个。
		if(lastMeet != endPos){
			String s = new String(cs, lastMeet, endPos - lastMeet) ;
			
			if(classTypeInList.isAssignableFrom(String.class)){
				if(s.length() == 2){
					s= "" ;
				}else{
					s = s.trim() ;
					s = s.substring(1, s.length() - 1) ;
				}
				results.add(s) ;
			}else{
				results.add(fromJson(s, classTypeInList)) ;
			}
		}
		
		return results ;
	}

}
