package com.guzzservices.image.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nl.captcha.Captcha;
import nl.captcha.Captcha.Builder;
import nl.captcha.noise.CurvedLineNoiseProducer;
import nl.captcha.text.producer.DefaultTextProducer;
import nl.captcha.text.renderer.ColoredWordRenderer;
import nl.captcha.text.renderer.WordRenderer;

public class CaptchaImageProducer implements ImageProducer {
	private int imageHeight;
	private int padding;
	private boolean isNoise;
	private boolean isBordered;
	private List<Font> textFonts;

	/**
	 * @param imageHeight 
	 * 		the height of image
	 * @param padding
	 * 		the padding is side-padding which is only used for left and right side 
	 * @param isNoise
	 * 		whether add curved line into the image
	 * @param isBordered
	 * 		whether add border for the image
	 */
	public CaptchaImageProducer(int imageHeight, int padding, boolean isNoise, boolean isBordered) {
		this.imageHeight = imageHeight;
		this.padding = padding;
		this.isBordered = isBordered;
		this.isNoise = isNoise;
		textFonts = new ArrayList<Font>();
		Random r = new Random();
			textFonts.add(new Font("arial", Font.PLAIN, (int)(imageHeight*(0.8+r.nextInt(3)/10.0))));  //字大小在imageHight的80%~100%之间
			textFonts.add(new Font("Gungsuh", Font.PLAIN, (int)(imageHeight*(0.8+r.nextInt(3)/10.0))));
			textFonts.add(new Font("Andalus", Font.PLAIN, (int)(imageHeight*(0.8+r.nextInt(3)/10.0))));
			textFonts.add(new Font("BroadWay", Font.PLAIN, (int)(imageHeight*(0.8+r.nextInt(3)/10.0))));
			textFonts.add(new Font("arial", Font.PLAIN, (int)(imageHeight*(0.8+r.nextInt(3)/10.0))));


	}

	/**
	 * create image via simpleCaptcha
	 * @see org.guzz.service.imageCode.impl.ImageProducer#createCodeImage(java.lang.String)
	 */
	public Image createCodeImage(String plainCode) {
		Random r = new SecureRandom();
		int imageHeight = this.imageHeight ;
		int padding = this.padding;
		int imageWidth = (int)(imageHeight*0.405 * plainCode.length() + 2*padding+5);
		// 字色
		ArrayList<Color> textColors = new ArrayList<Color>();
		for (int i = 0; i < 20; i++) {
			textColors.add(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
		}
		WordRenderer word = new ColoredWordRenderer(textColors, textFonts,padding);

		DefaultTextProducer textProducer = new DefaultTextProducer(plainCode.toCharArray());

		// 生成图片
		Builder cBuilder = new Captcha.Builder(imageWidth, imageHeight);
		cBuilder.addText(textProducer, word);
		if(this.isNoise){
			cBuilder.addNoise(new CurvedLineNoiseProducer(textColors.get(r.nextInt(20)),1));
		}
		if(this.isBordered){
			cBuilder.addBorder();
		}
		Captcha img = cBuilder.build();
		return img.getImage();
	}

	/**
	 * @see org.guzz.service.imageCode.impl.ImageProducer#getImageType()
	 */
	public String getImageType() {
		return "png";
	}

}
