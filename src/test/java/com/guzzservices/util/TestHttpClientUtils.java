/**
 * 
 */
package com.guzzservices.util;

import java.util.Map;

import junit.framework.TestCase;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class TestHttpClientUtils extends TestCase {
	
	public void testParseParamsFromUrl() throws Exception{
		assertEquals(HttpClientUtils.parseParamsFromUrl(null), null) ;
		assertEquals(HttpClientUtils.parseParamsFromUrl(""), null) ;
		
		assertEquals(HttpClientUtils.parseParamsFromUrl("abc"), null) ;
		assertEquals(HttpClientUtils.parseParamsFromUrl("?abc=a").size(), 2) ;
		assertEquals(HttpClientUtils.parseParamsFromUrl("?=abc").size(), 1) ;
		assertEquals(HttpClientUtils.parseParamsFromUrl("?a&b=c").size(), 3) ;
		assertEquals(HttpClientUtils.parseParamsFromUrl("?a&b=c").get(HttpClientUtils.PARAM_NAKED_URL), "") ;
		

		assertEquals(HttpClientUtils.parseParamsFromUrl("?").size(), 1) ;
		assertEquals(HttpClientUtils.parseParamsFromUrl("?").get(HttpClientUtils.PARAM_NAKED_URL), "") ;
		assertEquals(HttpClientUtils.parseParamsFromUrl("abc?").size(), 1) ;
		assertEquals(HttpClientUtils.parseParamsFromUrl("abc?a").size(), 2) ;
		assertEquals(HttpClientUtils.parseParamsFromUrl("abc?=a").size(), 1) ;
		assertEquals(HttpClientUtils.parseParamsFromUrl("abc?a=2").size(), 2) ;
		assertEquals(HttpClientUtils.parseParamsFromUrl("abc?a=2&b=3").size(), 3) ;
		
		Map<String, String> map = HttpClientUtils.parseParamsFromUrl("abc?a=2&b=3&c&d=&&&&?") ;
		assertEquals(map.remove(HttpClientUtils.PARAM_NAKED_URL), "abc") ;
		System.out.println("#$%^&U:" + map.keySet()) ;
		assertEquals(map.size(), 4) ;
		assertEquals(map.get("a"), "2") ;
		assertEquals(map.get("b"), "3") ;
		assertEquals(map.get("c"), "") ;
		assertEquals(map.get("d"), "") ;
		assertEquals(map.get("e"), null) ;
	}

}
