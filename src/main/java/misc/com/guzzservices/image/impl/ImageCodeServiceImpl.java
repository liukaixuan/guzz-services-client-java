/**
 * created by zachariah at 下午04:41:01
 */
package com.guzzservices.image.impl;

import java.awt.Image;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.service.AbstractService;
import org.guzz.service.ServiceConfig;
import org.guzz.util.StringUtil;

import com.guzzservices.image.ImageCodeService;

/**
 * @author zachariah
 *
 */
public abstract class ImageCodeServiceImpl extends AbstractService implements ImageCodeService {
	
	protected String seeds;
	
	protected int plainCodeLength;
	
	protected ImageProducer imageProducer ;	

	protected String mappedURL ;
	
	private static Log log = LogFactory.getLog(ImageCodeServiceImpl.class);
	
	/**
	 * @see org.guzz.Service#configure(org.guzz.service.ServiceConfig[])
	 */
	public boolean configure(ServiceConfig[] scs){
		if(scs.length > 0){
			ServiceConfig sc = scs[0] ;
			
			//the parameter defined in properties file
			String impl = sc.getProps().getProperty("imageProducer");
			int imageHeight = StringUtil.toInt(sc.getProps().getProperty("imageHeight"),35);
			int padding = StringUtil.toInt(sc.getProps().getProperty("padding"),10);
			boolean isNoised = StringUtil.toBoolean(sc.getProps().getProperty("isNoised"),false);
			boolean isBordered = StringUtil.toBoolean(sc.getProps().getProperty("isBordered"),false);
			seeds = sc.getProps().getProperty("seeds");
			
			if(seeds==null||seeds.length()==0){
				seeds = "234567890";
			}
			mappedURL = sc.getProps().getProperty("mappedURL");
			plainCodeLength = StringUtil.toInt(sc.getProps().getProperty("plainCodeLength"),4);
			
			if("captcha".equalsIgnoreCase(impl)){
				imageProducer = new CaptchaImageProducer(imageHeight, padding,isNoised,isBordered) ;
			}
			return true ;
		}else{
			log.debug(this.getClass().getPackage()+"."+this.getClass().getName()+" service is not be loaded");			
			return false;			
		}
	}
	
	/**
	 * @see org.guzz.service.imageCode.ImageCodeService#createCodeImage(java.lang.String)
	 */
	public Image createCodeImage(String plainCode){
		return imageProducer.createCodeImage(plainCode);
	}

	/**
	 * @see org.guzz.service.imageCode.ImageCodeService#getImageType()
	 */
	public String getImageType() {
		return imageProducer.getImageType() ;
	}

	/**
	 * @see org.guzz.service.imageCode.ImageCodeService#getImageURL(java.lang.String, java.lang.String)
	 */
	public String getImageURL(String appUrl, String plainCode) {
		return appUrl + mappedURL ;
	}
	
	public ImageProducer getImageProducer() {
		return imageProducer;
	}

	public void setImageProducer(ImageProducer imageProducer) {
		this.imageProducer = imageProducer;
	}
	
	public boolean isAvailable() {
		return imageProducer != null ;
	}

	public void shutdown() {
		imageProducer = null ;
	}

	public void startup() {
	}

}
