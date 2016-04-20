/**
 * VerifyImageCodeActionForSpring.java created at 2010-1-8 下午03:58:26 by liukaixuan@gmail.com
 */
package com.guzzservices.image.action;

import java.awt.image.BufferedImage;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.util.RequestUtil;
import org.guzz.util.ResponseUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.guzzservices.image.ImageCodeService;

/**
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ImageCodeActionForSpring implements Controller {
	private ImageCodeService imageCodeService;
	
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ResponseUtil.clearCache(response);
		
		int isBlur = RequestUtil.getParameterAsInt(request, "isBlur", 0);
		BufferedImage image;
		
		String code = imageCodeService.getPlainCode(request, response);
		//当用户点击“看不清”链接时，就产生一个新的图片，并把新图片缓存住
		if(isBlur>0){
			image = (BufferedImage)imageCodeService.createCodeImageNoCache(code);
		//当新加载页面时，从缓存中拿出一个图片。如果是空的，那就新出一个图片
		}else{
			image = (BufferedImage) imageCodeService.createCodeImage(code);
		}
		
		String imageType = imageCodeService.getImageType();
		response.setContentType("image/" + imageType);
		OutputStream os = response.getOutputStream();
		ImageIO.write(image, imageType, os);
		os.close();
		
		return null;
	}

	public ImageCodeService getImageCodeService() {
		return imageCodeService;
	}

	public void setImageCodeService(ImageCodeService imageCodeService) {
		this.imageCodeService = imageCodeService;
	}

}
