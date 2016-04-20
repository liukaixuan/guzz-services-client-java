/**
 * created by zachariah at 下午04:41:01
 */
package com.guzzservices.image.impl;

import java.awt.Image;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.util.CookieUtil;
import org.guzz.util.StringUtil;

import com.guzzservices.store.CacheService;

/**
 * @author zachariah
 *
 */
public class CacheBasedImageCodeServiceImpl extends ImageCodeServiceImpl {
	
	private CacheService cacheService ;
	
	private final String imageCodeCookieName = "_gs_ic_t" ;
	
	private final String prefixCacheKey = "cbics_" ;
	
	private CookieUtil cookieUtil = CookieUtil.forVersion0() ;
	
	//用通过传入的plainCode创建图片，实现接口方法
	public Image createCodeImageNoCache(String plainCode) {
		return super.createCodeImage(plainCode) ;
	}
		
	//构造新的验证码串
	public String getPlainCode(HttpServletRequest request, HttpServletResponse response) {
		String key = cookieUtil.readCookie(request, imageCodeCookieName) ;
		if(key == null){
			key = prefixCacheKey + UUID.randomUUID().toString() ;
			cookieUtil.writeCookie(response, imageCodeCookieName, key, CookieUtil.COOKIE_AGE_1Week) ;
		}
		
		String code = this.createRandomCode();
		
		//store 2 mins
		this.cacheService.asyncStoreToCache(key, code, 120) ;
		
		return code ;
	}
	
	//从seeds中创造随机字符 
	private String createRandomCode() {
		Random r = new SecureRandom();
		StringBuilder code = new StringBuilder(plainCodeLength);
		for(int i=0 ; i<plainCodeLength ; i++){
			code.append(seeds.charAt(r.nextInt(seeds.length())));
		}
		
		return code.toString();
	}

	//实现接口方法
	public boolean isCodePass(HttpServletRequest request, HttpServletResponse response, String answer) {
		if(StringUtil.isEmpty(answer)){
			return false;
		}
		
		String key = cookieUtil.readCookie(request, imageCodeCookieName) ;
		if(key == null){
			return false ;
		}
				
		//store 2 mins
		String code = (String) this.cacheService.getFromCache(key) ;
		
		return answer.equalsIgnoreCase(code) ;
	}

	public CacheService getCacheService() {
		return cacheService;
	}

	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}
}
