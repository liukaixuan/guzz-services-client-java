/**
 * created by zachariah at 下午04:41:01
 */
package com.guzzservices.image;

import java.awt.Image;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  class ImageCodeService is for guzz framework. When you add this service to the guzz, you can use it anywhere.
 * 	 This service is used for generating VerifyCode and asserting the answer
 * @author zachariah
 */
public interface ImageCodeService {

	/**
	 * create the image answer as string
	 *@return returns the new image code
	 */
	public String getPlainCode(HttpServletRequest request, HttpServletResponse response) ;
	
	/**
	 *returns the absolute path of the current Service(ImageCodeService)
	 *@param appUrl
	 *		the root URL for the current project
	 *@param plainCode
	 *		the Image Code answer
	 *@return the absolute path of the current Service(ImageCodeService)
	 */
	public String getImageURL(String appUrl, String plainCode) ;
	
	/**
	 *returns the type of the image you want create
	 *@return the type is returned as string name, such as "png","jpg" etc
	 */
	public String getImageType() ;
	
	/**
	 * creates a new image with a specified code string
	 *@param plainCode
	 *			the code string such as "2345"
	 *@return  the image that class ImageProducer has created
	 */
	public Image createCodeImageNoCache(String plainCode) ;
	
	public Image createCodeImage(String plainCode) ;
	
	/**
	 * assert whether the answer is correct or not
	 *@param answer
	 *		the answer you want to judge 
	 *@return whether the answer is identical with the current instance's
	 */
	public boolean isCodePass(HttpServletRequest request, HttpServletResponse response, String answer) ;

}
