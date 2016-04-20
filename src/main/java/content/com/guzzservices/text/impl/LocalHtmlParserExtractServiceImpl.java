/**
 * 
 */
package com.guzzservices.text.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.guzz.service.AbstractService;
import org.guzz.service.ServiceConfig;
import org.guzz.util.StringUtil;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.visitors.NodeVisitor;

import com.guzzservices.text.Html2PlainExtractService;
import com.guzzservices.text.PlainExtractResult;

/**
 * 
 * Local implementation based on <a href="http://htmlparser.sourceforge.net/">HtmlParser 2.0</a>
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class LocalHtmlParserExtractServiceImpl extends AbstractService implements Html2PlainExtractService {
	
	//ignore images contains these URLs. (maybe smiles or something else useless to show out) 
	private String[] ignoreImages = new String[0] ;
	
	private Map<String, String> tips ;

	public PlainExtractResult extractText(String htmlText) throws Exception {
		return extractTextWithImage(htmlText, 0, 0) ;
	}
	
	public PlainExtractResult extractText(String htmlText, int resultLengthLimit) throws Exception {
		return extractTextWithImage(htmlText, resultLengthLimit, 0) ;
	}

	public PlainExtractResult extractTextWithAllImages(String htmlText, int resultLengthLimit) throws Exception {
		return extractTextWithImage(htmlText, resultLengthLimit, -1) ;
	}

	public PlainExtractResult extractTextWithImage(String htmlText, int resultLengthLimit, int imageCountLimit) throws Exception {
		return extractTextWithImage(htmlText, resultLengthLimit, imageCountLimit, this.tips) ;
	}
	
	public PlainExtractResult extractTextWithImage(String htmlText, int resultLengthLimit, int imageCountLimit, Map<String, String> tips) throws Exception {
		if(log.isDebugEnabled()){
			log.debug("extract html:" + htmlText) ;
		}
		
		if(htmlText == null) return new PlainExtractResult() ;
		
		//translating hidden &amp;... to HTML tags.
		htmlText = StringEscapeUtils.unescapeHtml(htmlText) ;
		
		Parser p = new Parser(new Lexer(htmlText)) ;
		TextAndImageVisitor v = new TextAndImageVisitor(htmlText.length(), imageCountLimit >= 0 ? imageCountLimit : Integer.MAX_VALUE, this.ignoreImages, tips) ;
		
		p.visitAllNodesWith(v) ;
		
		String plainText = v.getExtractedText() ;
		
		PlainExtractResult result = new PlainExtractResult() ;
		List<String> images = v.getImages() ;
		
		if(images != null){
			result.setImages(images.toArray(new String[0])) ;
			result.setImageTitles(v.getImageTitles().toArray(new String[0])) ;
		}
		
		result.setMoreImagesIgnored(v.isImageIgnored()) ;
		
		if(resultLengthLimit <= 0 || resultLengthLimit >= plainText.length()){
			result.setPlainText(plainText) ;
		}else{
			result.setPlainText(plainText.substring(0, resultLengthLimit)) ;
			result.setTextCutted(true) ;
		}
		
		//如果有没有关闭的A标签，关闭之（默认策略为：宁可返回的字符数不够，也不返回含有未关闭标签的html代码，避免引起页面错乱）
		String text = result.getPlainText() ;
		for(int i = 0 ; i < v.getOpenedAHrefs() ; i++){
			int pos = text.lastIndexOf("<a href=\"") ;
			
			if(pos > 0){
				text = text.substring(0, pos) ;
			}else{
				break ;
			}
		}
		
		//删除截取后的html未关闭处理，TODO: 优化在解析树中处理
		if(text.length() > 0){
			int startPos = text.lastIndexOf("<a") ;
			int endPos = text.indexOf("</a>", startPos) ;
			
			//发现截错的标签
			if(startPos > 0 && endPos < 0){
				text = text.substring(0, startPos) ;
			}else if(text.charAt(text.length() - 1) == '<'){
				text = text.substring(0, text.length() - 1) ;
			}
		}
		
		result.setPlainText(text) ;
		
		if(log.isDebugEnabled()){
			log.debug("extracted html:" + result.getPlainText()) ;
		}
		
		return result ;
	}
	
	static class TextAndImageVisitor extends NodeVisitor{
		
		private LinkedList<String> images ;
		private LinkedList<String> imageTitles ;
		
		private final StringBuilder sb ;
		
		private final int maxImageCount ;
		
		private boolean imageIgnored ;
		
		private boolean ignoreContent ;
		
		/**保留html a标签*/
		private boolean keepA ;
		
		private String keepATarget = "_blank" ;
		
		private String[] ignoreImages ;
		
		private int openedAHrefs ;
		
		public TextAndImageVisitor(int contentLength, int maxImageCount, String[] ignoreImages, Map<String, String> tips){
			 sb = new StringBuilder(contentLength) ;
			 this.maxImageCount = maxImageCount ;
			 this.ignoreImages = ignoreImages ;
			 
			 if(tips != null && Boolean.valueOf(tips.get("keepA"))){
				 this.keepA = true ;
				 String keepATarget = (String) tips.get("keepATarget") ;
				 if(StringUtil.notEmpty(keepATarget)){
					 this.keepATarget = keepATarget ;
				 }
			 }
		}
		
		public void visitTag(Tag tag) {
			if("IMG".equals(tag.getTagName())){
				//不需要图片
				if(this.maxImageCount == 0){
					this.imageIgnored = true ;
					return ;
				}
				
				String img = tag.getAttribute("src") ;
				if(img == null) return ;
				
				img = img.trim() ;
				if(img.length() == 0) return ;
				
				//ignore this image?
				for(String ignorePath : this.ignoreImages){
					if(img.contains(ignorePath)){
						return ;
					}
				}
				
				if(images == null){
					images = new LinkedList<String>() ;
					imageTitles = new LinkedList<String>() ;
				}
					
				if(images.size() < maxImageCount ){
					images.addLast(img) ;
						
					String imageTitle = tag.getAttribute("title") ;
					if(StringUtil.isEmpty(imageTitle)){
						imageTitle = tag.getAttribute("alt") ;
					}
					
					if(imageTitle == null){
						imageTitle = "" ;
					}else{
						imageTitle = imageTitle.trim() ;
					}
					
					imageTitles.addLast(imageTitle) ;
				}else{
					this.imageIgnored = true ;
				}
			}else if("A".equals(tag.getTagName())){
				if(!this.keepA) return ;
				
				String href = tag.getAttribute("href") ;
				if(href == null) return ;
				href = href.trim() ;
				if(href.length() == 0) return ;
				
				sb.append("<a href=\"").append(href) ;
				
				if(!"none".equals(this.keepATarget)){
					sb.append("\" target=\"" + this.keepATarget + "\"") ;
				}
				
				sb.append('>') ;
				
				openedAHrefs++ ;
			}else if("SCRIPT".equals(tag.getTagName())){
				ignoreContent = true ;
			}else if("STYLE".equals(tag.getTagName())){
				ignoreContent = true ;
			}else if("BR".equals(tag.getTagName())){
				sb.append("\n") ;
			}else if("HR".equals(tag.getTagName())){
				sb.append("\n") ;
			}
		}

		public void visitStringNode(Text string) {
			if(ignoreContent) return ;
			
			String text = string.getText() ;
			if(text == null) return ;
			
			sb.append(text) ;
		}

		public LinkedList<String> getImages() {
			return images;
		}
		
		public String getExtractedText(){
			return StringUtil.squeezeWhiteSpace(sb.toString()) ;
		}

		public boolean isImageIgnored() {
			return imageIgnored;
		}

		public void visitEndTag(Tag tag) {
			super.visitEndTag(tag);
			
			if("A".equals(tag.getTagName())){
				if(!this.keepA) return ;
				
				sb.append("</a>") ;
				
				openedAHrefs-- ;
			}else if("SCRIPT".equals(tag.getTagName())){
				ignoreContent = false ;
			}else if("STYLE".equals(tag.getTagName())){
				ignoreContent = false ;
			}
		}

		public LinkedList<String> getImageTitles() {
			return imageTitles;
		}

		public int getOpenedAHrefs() {
			return this.openedAHrefs;
		}
	}
	
	public boolean configure(ServiceConfig[] scs) {
		if(scs.length > 0){
			String ignoreImages = scs[0].getProps().getProperty("ignoreImages") ;
			
			//Ignore default icons
			if(StringUtil.notEmpty(ignoreImages)){
				this.ignoreImages = StringUtil.splitString(ignoreImages, ";") ;
				
				for(int i = 0 ; i < this.ignoreImages.length ; i++){
					this.ignoreImages[i] = this.ignoreImages[i].trim() ;
				}
			}
			
			HashMap<String, String> tips = new HashMap<String, String>() ;
			
			String keepA = scs[0].getProps().getProperty("keepA") ;
			if("true".equalsIgnoreCase(keepA)){
				tips.put("keepA", "true") ;
				tips.put("keepATarget", scs[0].getProps().getProperty("keepATarget")) ;
			}
			
			if(tips.isEmpty()){
				this.tips = null ;
			}else{
				this.tips = tips ;
			}
		}
		
		return true ;
	}

	public boolean isAvailable() {
		return true;
	}

	public void shutdown() {		
	}

	public void startup() {		
	}

	public String[] getIgnoreImages() {
		return ignoreImages;
	}

	public void setIgnoreImages(String[] ignoreImages) {
		this.ignoreImages = ignoreImages;
	}

	public Map<String, String> getTips() {
		return tips;
	}

	public void setTips(Map<String, String> tips) {
		this.tips = tips;
	}
	
}
