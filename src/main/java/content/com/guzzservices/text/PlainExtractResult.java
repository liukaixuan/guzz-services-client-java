/**
 * 
 */
package com.guzzservices.text;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class PlainExtractResult {
	
	private String plainText ;
	
	private boolean textCutted ;
	
	private String[] images ;
	
	private String[] imageTitles ;
	
	private boolean moreImagesIgnored ;

	/**
	 * 抽取的纯文本内容，自动合并多余的空格。
	 */
	public String getPlainText() {
		return plainText;
	}
	
	/**
	 * 返回抽取的纯文本内容，并删除重复的换行。
	 * 
	 * @param newLineSymbol 将合并后的'\r'和'\r\n' 统一换成newLineSymbol
	 * @param maxNewLinesRepeatCount 内容中一处换行的地方最多允许出现多少个'\r'或'\r\n'。用于控制大段的空行。
	 * 
	 * @return 返回的内容不会控制字数，因换行替换成newLineSymbol可能使得返回的字符串超过调用时传入的最大允许长度，需要应用自己控制。
	 */
	public String getPlainTextDropNewLine(String newLineSymbol, int maxNewLinesRepeatCount){
		return getPlainTextDropNewLine(this.plainText, newLineSymbol, maxNewLinesRepeatCount) ;
	}
	
	/**
	 * 返回抽取的纯文本内容，并删除重复的换行。
	 * 
	 * @param content Text to be processed.
	 * @param newLineSymbol 将合并后的'\r'和'\r\n' 统一换成newLineSymbol
	 * @param maxNewLinesRepeatCount 内容中一处换行的地方最多允许出现多少个'\r'或'\r\n'。用于控制大段的空行。
	 * 
	 * @return 返回的内容不会控制字数，因换行替换成newLineSymbol可能使得返回的字符串超过调用时传入的最大允许长度，需要应用自己控制。
	 */
	public static String getPlainTextDropNewLine(String content, String newLineSymbol, int maxNewLinesRepeatCount){
		if(content == null) return null ;
		char[] cs = content.toCharArray() ;
		
		StringBuffer sb2 = new StringBuffer(cs.length + newLineSymbol.length() * maxNewLinesRepeatCount);
		
		//skip the start newlines
		int alreadyMeetNewLineCount = maxNewLinesRepeatCount ;
		int i = 0 ;
		
		for(; i < cs.length -1 ; i++){
			char c = cs[i] ;
			char nextChar = cs[i + 1] ;

			//meet \r\n
			if(c == '\r' && nextChar == '\n'){
				if(alreadyMeetNewLineCount >= maxNewLinesRepeatCount){
					continue ;
				}else{
					sb2.append(newLineSymbol) ;
					alreadyMeetNewLineCount++ ;
				}
				
				i++ ;
			}else if(c == '\r' || c == '\n'){
				if(alreadyMeetNewLineCount >= maxNewLinesRepeatCount){
					continue ;
				}else{
					sb2.append(newLineSymbol) ;
					alreadyMeetNewLineCount++ ;
				}
			}else if(c == ' ' && alreadyMeetNewLineCount > 0){
				//avoid "\n \r \r\n". Space between new lines should be skipped.
				
				if(nextChar == '\r' || nextChar == '\n'){
					//don't reset alreadyMeetNewLineCount
					
					//allow character
					if(alreadyMeetNewLineCount < maxNewLinesRepeatCount){
						sb2.append(c) ;
					}
					
					i++ ;
				}else{
					sb2.append(c) ;
					
					alreadyMeetNewLineCount = 0 ;
				}
				
			}else{
				sb2.append(c) ;
				
				alreadyMeetNewLineCount = 0 ;
			}
		}
		
		//check the last char is not new line
		if(i != cs.length){//the last two characters are not "\r\n"
			char c = cs[cs.length - 1] ;
			
			if(c == '\r' || c == '\n'){
				if(alreadyMeetNewLineCount < maxNewLinesRepeatCount){
					sb2.append(newLineSymbol) ;
					alreadyMeetNewLineCount++ ;
				}
			}else{
				sb2.append(c) ;
			}
		}
		
		return sb2.toString() ;
	}

	public void setPlainText(String plainText) {
		this.plainText = plainText;
	}

	/**
	 * 返回的纯文本内容是否是截断过的（还有更多文本，由于字数限制，没有返回）。
	 */
	public boolean isTextCutted() {
		return textCutted;
	}

	public void setTextCutted(boolean textCutted) {
		this.textCutted = textCutted;
	}

	/**
	 * HTML中发现的图片地址。如果没有图片，返回null。
	 */
	public String[] getImages() {
		return images;
	}

	public void setImages(String[] images) {
		this.images = images;
	}

	/**
	 * 除了返回的图片地址，html中是否还有更多图片地址？
	 */
	public boolean isMoreImagesIgnored() {
		return moreImagesIgnored;
	}

	public void setMoreImagesIgnored(boolean moreImagesIgnored) {
		this.moreImagesIgnored = moreImagesIgnored;
	}

	/**
	 * HTML中发现的图片的名称（title和alt属性的值）。如果没有图片，返回null。返回的数组顺序与getImages()返回的图片地址相对应。
	 */
	public String[] getImageTitles() {
		return imageTitles;
	}

	public void setImageTitles(String[] imageTitles) {
		this.imageTitles = imageTitles;
	}

}
