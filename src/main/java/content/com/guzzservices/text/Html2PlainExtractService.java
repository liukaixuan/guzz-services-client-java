/**
 * 
 */
package com.guzzservices.text;

/**
 * 
 * Extract plain text from a HTML string.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface Html2PlainExtractService {
	
	/**
	 * 抽取普通文本，不包含图片信息。
	 * 
	 * @param htmlText 传入的html
	 */
	public PlainExtractResult extractText(String htmlText) throws Exception ;
	
	/**
	 * 抽取普通文本，不包含图片信息。
	 * 
	 * @param htmlText 传入的html
	 * @param resultLengthLimit 抽取的普通文本最长只需要截取到的长度。0表示抽取所有。
	 */
	public PlainExtractResult extractText(String htmlText, int resultLengthLimit) throws Exception ;
	
	/**
	 * 抽取普通文本，包含所有图片信息。图片信息会另存，不会混合到返回的普通文本中。
	 * 
	 * @param htmlText 传入的html
	 * @param resultLengthLimit 抽取的普通文本最长只需要截取到的长度。0表示抽取所有。
	 */
	public PlainExtractResult extractTextWithAllImages(String htmlText, int resultLengthLimit) throws Exception ;
	
	/**
	 * 抽取普通文本，含图片信息。图片信息会另存，不会混合到返回的普通文本中。
	 * 
	 * @param htmlText 传入的html
	 * @param resultLengthLimit 抽取的普通文本最长只需要截取到的长度。0表示抽取所有。
	 * @param imageCountLimit 最多只需要提取的图片数。-1表示抽取所有；0表示不抽取；其他表示抽取的图片张数。
	 */
	public PlainExtractResult extractTextWithImage(String htmlText, int resultLengthLimit, int imageCountLimit) throws Exception ;

}
