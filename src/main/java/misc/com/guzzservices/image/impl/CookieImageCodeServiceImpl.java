
package com.guzzservices.image.impl;

import java.awt.Image;
import java.security.SecureRandom;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.service.ServiceConfig;
import org.guzz.util.CookieUtil;
import org.guzz.util.StringUtil;

import com.guzzservices.store.CacheService;

public class CookieImageCodeServiceImpl extends ImageCodeServiceImpl {
	
	private CacheService cacheService ;
	
	private final String imageCodeCookieName = "_i_c_c_n_3" ;
	
	private int MAX_AGE = CookieUtil.COOKIE_AGE_1Min * 20 ;
	
	private CookieUtil cu = CookieUtil.forVersion0() ;
	
	//用通过传入的plainCode创建图片，实现接口方法
	public Image createCodeImageNoCache(String plainCode) {
		return super.createCodeImage(plainCode) ;
	}
		
	//构造新的验证码串
	public String getPlainCode(HttpServletRequest request, HttpServletResponse response) {
		Random r = new SecureRandom();
		StringBuilder code = new StringBuilder(12);
		for(int i=0 ; i< 12 ; i++){
			code.append(seeds.charAt(r.nextInt(seeds.length())));
		}
		
		String cookieKey = code.toString() ;
		String plainCode = this.createRandomCode();
		
		cu.writeTempCookie(response, this.imageCodeCookieName, cookieKey) ;
		
		this.cacheService.asyncStoreToCache(this.imageCodeCookieName + cookieKey, plainCode, MAX_AGE) ;
		return plainCode ;
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
		
		String cookieKey = cu.readCookie(request, this.imageCodeCookieName) ;
		if(cookieKey == null) return false ;
		
		String plainCode = (String) this.cacheService.getFromCache(this.imageCodeCookieName + cookieKey) ;
		if(plainCode == null) return false ;
		
		this.cacheService.asyncRemoveFromCache(this.imageCodeCookieName + cookieKey) ;
		
		return answer.equalsIgnoreCase(plainCode) ;
	}

	/**
	 * @see org.guzz.service.imageCode.impl.ImageCodeServiceImpl#configure(org.guzz.service.ServiceConfig[])
	 */
	public boolean configure(ServiceConfig[] scs) {
		return super.configure(scs);
	}

	public CacheService getCacheService() {
		return this.cacheService;
	}

	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}

	public String getImageCodeCookieName() {
		return this.imageCodeCookieName;
	}
}
