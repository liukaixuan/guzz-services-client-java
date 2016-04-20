/**
 * created by zachariah at 下午08:14:28
 */
package com.guzzservices.image.action;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.util.RequestUtil;
import org.guzz.util.ResponseUtil;
import org.guzz.web.context.GuzzWebApplicationContextUtil;

import com.guzzservices.image.ImageCodeService;

/**
 * @author zachariah
 *
 */
@SuppressWarnings("serial")
public class ImageCodeActionForServlet extends HttpServlet{
	private ImageCodeService imageCodeService;
	private static Log log = LogFactory.getLog(ImageCodeActionForServlet.class);
	public void service(HttpServletRequest request, HttpServletResponse response)  throws ServletException  {

		ResponseUtil.clearCache(response);
		
		if(imageCodeService == null){
			imageCodeService = (ImageCodeService)GuzzWebApplicationContextUtil.getGuzzContext(request.getSession().getServletContext()).getService("imageCodeService");
		}
		
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
		OutputStream os;
		try {
			os = response.getOutputStream();
			ImageIO.write(image, imageType, os);
			os.close();
		} catch (IOException e) {
			log.error(e);
		}
	}
}
