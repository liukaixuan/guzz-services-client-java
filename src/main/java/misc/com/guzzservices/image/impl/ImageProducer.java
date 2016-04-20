
package com.guzzservices.image.impl;

import java.awt.Image;

/**
 *   in charge of creating images
 * @author zzc
 */
public interface ImageProducer {
	
	/**
	 *returns the type of the image you want create
	 *@return the type is returned as string name, such as "png","jpg" etc
	 */
	public String getImageType() ;
	
	/**
	 * creates image with a specified code string
	 *@param plainCode
	 *			the code string such as "12345"
	 *@return  the image class ImageProducer has created
	 */
	public Image createCodeImage(String plainCode) ;


}
