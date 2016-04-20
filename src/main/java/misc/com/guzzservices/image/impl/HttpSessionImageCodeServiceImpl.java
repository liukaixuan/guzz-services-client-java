/**
 * created by zachariah at 下午04:41:01
 */
package com.guzzservices.image.impl;

import java.awt.Image;
import java.security.SecureRandom;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.service.ServiceConfig;
import org.guzz.util.StringUtil;

/**
 * @author zachariah
 *
 */
public class HttpSessionImageCodeServiceImpl extends ImageCodeServiceImpl {
	
	private final String imageCodeSessionName = "imageCodeSessionName" ;
	
	//用通过传入的plainCode创建图片，实现接口方法
	public Image createCodeImageNoCache(String plainCode) {
		return super.createCodeImage(plainCode) ;
	}
		
	//构造新的验证码串
	public String getPlainCode(HttpServletRequest request, HttpServletResponse response) {
		String code = this.createRandomCode();
		request.getSession().setAttribute(imageCodeSessionName, code);
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
		
		String plainCode = (String) request.getSession().getAttribute(imageCodeSessionName) ;
		request.getSession().removeAttribute(imageCodeSessionName);
		return answer.equalsIgnoreCase(plainCode) ;
	}

	/**
	 * @see org.guzz.service.imageCode.impl.ImageCodeServiceImpl#configure(org.guzz.service.ServiceConfig[])
	 */
	public boolean configure(ServiceConfig[] scs) {
		return super.configure(scs);
	}
}
