/**
 * created by zachariah at 下午04:41:01
 */
package com.guzzservices.image.impl;

import java.awt.Image;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.service.ServiceConfig;
import org.guzz.util.StringUtil;

/**
 * @author zachariah
 *
 */
public class TimerImageCodeServiceImpl extends ImageCodeServiceImpl {
	
	private long lastUpdateTime;
	
	private long intervalMilliSeconds;
	
	private Image cachedImage;
	
	private String cachedCode ;
	
	//用通过传入的plainCode创建新图片，并覆盖被缓存的图片
	public Image createCodeImageNoCache(String plainCode) {
		this.cachedImage = super.createCodeImage(plainCode);
		this.cachedCode = plainCode ;
		
		return this.cachedImage;
	}

	//如果缓存中有图片就返回缓存的图片，如果没有缓存图片就创建一个新图片
	public Image createCodeImage(String plainCode) {
		if(this.cachedImage == null){
			createCodeImageNoCache(plainCode) ;
		}
		
		return this.cachedImage ;
	}	
	
	//超时就构造一个新的验证码串，否则返回现有的验证码串
	public String getPlainCode(HttpServletRequest request, HttpServletResponse response) {
		long currentMilliSeconds = System.currentTimeMillis();
		
		long time = currentMilliSeconds / intervalMilliSeconds ;
		
		if(time == this.lastUpdateTime){//hit in cache
			if(this.cachedCode == null){
				this.cachedCode = createRandomCode(lastUpdateTime) ;
			}
			return this.cachedCode ;
		}else{
			lastUpdateTime = time;
			String code = createRandomCode(time);
			cachedImage = null;   //超时的话，缓存的图片就过期了，所以置空
			this.cachedCode = null ;
			return code;
		}
	}
	
	//实现接口方法,判断验证码是否正确
	public boolean isCodePass(HttpServletRequest request, HttpServletResponse response, String answerClient) {
		if(StringUtil.isEmpty(answerClient)){
			return false;
		}
		
		if(this.cachedCode == null){
			this.cachedCode = createRandomCode(lastUpdateTime) ;
		}
		
		return answerClient.equalsIgnoreCase(this.cachedCode);
	}

	//用milliSeconds做种子创造字符串
	private String createRandomCode(long milliSeconds){
		Random r = new Random(milliSeconds);
		StringBuilder code = new StringBuilder(plainCodeLength);
		for(int i=0 ; i<plainCodeLength ; i++){
			code.append(seeds.charAt(r.nextInt(seeds.length())));
		}
		
		return code.toString();
	}
	
	/**
	 * @see org.guzz.service.imageCode.impl.ImageCodeServiceImpl#configure(org.guzz.service.ServiceConfig[])
	 */
	public boolean configure(ServiceConfig[] scs) {
		if(super.configure(scs)){
			intervalMilliSeconds = (long)StringUtil.toInt(scs[0].getProps().getProperty("seconds"),120)*1000;
			return true;
		}else{
			return false;
		}	
	}
}
