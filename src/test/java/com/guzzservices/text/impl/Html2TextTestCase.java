/**
 * 
 */
package com.guzzservices.text.impl;

import java.util.HashMap;
import java.util.List;

import com.guzzservices.rpc.util.JsonUtil;
import com.guzzservices.text.PlainExtractResult;

import junit.framework.TestCase;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class Html2TextTestCase extends TestCase {
	
	public void testJsonUtil(){
		List<String> ss = JsonUtil.fromJson2List("[\"@g2@1\",\"@g2@2\",\"@g2@1089\",\"@g2@588\"]", String.class) ;
		assertEquals(ss.size(), 4) ;
		assertEquals(ss.get(0), "@g2@1") ;
		assertEquals(ss.get(1), "@g2@2") ;
		assertEquals(ss.get(2), "@g2@1089") ;
		assertEquals(ss.get(3), "@g2@588") ;
	}
	
	public void testPlainText() throws Exception{
		LocalHtmlParserExtractServiceImpl p = new LocalHtmlParserExtractServiceImpl() ;
		HashMap<String, String> tips = new HashMap<String, String>() ;
		tips.put("keepA", "true") ;
		tips.put("keepATarget", "mainFrame") ;
		p.setTips(tips) ;
		
		String text = "hello \n \r\n\rworld!\r" ;
		
		assertEquals(text, p.extractText(text).getPlainText()) ;
		assertEquals(null, p.extractText(null).getPlainText()) ;
		assertEquals("", p.extractText("").getPlainText()) ;
		assertEquals("hello <br>world!<br>", p.extractText("hello \n \r\n\rworld!\r").getPlainTextDropNewLine("<br>", 1)) ;
		assertEquals("hello <br> world!<br>", p.extractText("hello \n \r\n\r world!\r\n").getPlainTextDropNewLine("<br>", 1)) ;
		assertEquals("", p.extractText("").getPlainTextDropNewLine("<br>", 1)) ;
		assertEquals(null, p.extractText(null).getPlainTextDropNewLine("<br>", 1)) ;
		assertEquals("", p.extractText(" ").getPlainTextDropNewLine("<br>", 1)) ;
		assertEquals("", p.extractText("\n\r").getPlainTextDropNewLine("<br>", 1)) ;
		assertEquals("", p.extractText("\r\n").getPlainTextDropNewLine("<br>", 1)) ;
		
	
		
		PlainExtractResult result = p.extractTextWithImage(text, 197, 1) ;
		
		assertFalse(result.isTextCutted()) ;
		assertEquals(null, result.getImages()) ;
		
		text = "政治改革必须为人民服务<br/><span class='hidden_white' style='color:#FFFFFF;'>人民(http://bbs.people.com.cn)</span><br/>政治体制改革势在必行。政治体制必须沿着正确政治方向积极稳妥推进政治体制改革。政治体制改革必须为最广大人民服务，出发点和落脚点都必须是让人民满意。<br/><span class='hidden_white' style='color:#FFFFFF;'>人民网强国社区(http://bbs.people.com.cn)</span><br/>中国共产党是全国各族人民的领导核心，政治体制改革必须在党的坚强领导下进行。<br/><span class='hidden_white' style='color:#FFFFFF;'>人民网强国社区(http://bbs.people.com.cn)</span><br/>政治体制改革的目的是让社会更和谐更稳定更发展，让人民生活水平更高，让人民群众更满意，这是根本，这是方向，更是原则，不能有丝毫放松。<br/><span class='hidden_white' style='color:#FFFFFF;'>人民网强国社区(http://bbs.people.com.cn)</span><br/>政治体制改革必须从实际出发，尊重国情、尊重历史、尊重文化，决不可以成为空中楼阁。政治体制改革必须借鉴学习先进，打造公平，维护公正，提高效率，促进稳定和发展。<br/><span class='hidden_white' style='color:#FFFFFF;'>人民网强国社区(http://bbs.people.com.cn)</span><br/>政治体制发展必须革故鼎新，把束缚了经济社会的发展，不适应或者不完全适应发展需要的部分实行改革，也必将触及一些人，特别是少数当权者的利益。这必然会遇到阻力。因此改革的态度必须坚决，立场必须鲜明，措施必须有力。<br/><span class='hidden_white' style='color:#FFFFFF;'>人民网强国社区(http://bbs.people.com.cn)</span><br/>让政治体制改革更好地促进经济社会发展，更让人民满意吧。<br/><span class='hidden_white' style='color:#FFFFFF;'>人民网强国社区(http://bbs.people.com.cn)</span><br/>" ;
		
		result = p.extractTextWithImage(text, 300, 1) ;
		
		assertTrue(result.isTextCutted()) ;
		assertEquals(null, result.getImages()) ;
		
		System.out.println(result.getPlainTextDropNewLine("<br>", 1)) ;
		
		text = "政治<a href=\"http://finance.ce.cn/macro/gdxw/200901/21/t20090121_14143831.shtml\" target=\"_blank\" onmousedown=\"return si_T('&amp;ID=SERP,5026.1')\">2008年主要经济<strong>数据</strong> 外商直接投资(<strong>FDI</strong>) _<strong>中国</strong>经济网——国家经济门户</a><br/>" ;
		
		System.out.println(p.extractText(text).getPlainTextDropNewLine("<br>", 1)) ;
		
		long start = System.nanoTime();
		for (int i = 0; i < 1000 * 1000; ++i) {
		    long v = System.currentTimeMillis();
		}
		
		long end = System.nanoTime();
		
		
		System.out.println("1 m nano:" + (end - start)) ;
		

		start = System.nanoTime();
		for (int i = 0; i < 1000 * 1000; ++i) {
		    long v = System.nanoTime() ;
		}
		
		end = System.nanoTime();
		
		
		System.out.println("1 m nano:" + (end - start)) ;
		
		System.out.println("aaaaaaaaaaaaaa:" + p.extractText(text, 10).getPlainText()) ;
		assertEquals(p.extractText(text, 10).getPlainText(), "政治") ;
		
		text = "政治政治政治政治......<br><br>政治政治<a target=\"_blank\" href=\"http://tv.people.com.cn/GB/166419/15389044.html\">http://tv.people.com.cn/GB/166419/15389044.html</a>" ;
	
		assertEquals(p.extractText(text, 124).getPlainText(), "政治政治政治政治......\n\n政治政治") ;
	}
	
	public static void main(String[] args) throws Exception{
		LocalHtmlParserExtractServiceImpl p = new LocalHtmlParserExtractServiceImpl() ;
		HashMap<String, String> tips = new HashMap<String, String>() ;
		tips.put("keepA", "true") ;
		tips.put("keepATarget", "mainFrame") ;
		p.setTips(tips) ;
		
		String text = "政治<a href=\"http://finance.ce.cn/macro/gdxw/200901/21/t20090121_14143831.shtml\" target=\"_blank\" onmousedown=\"return si_T('&amp;ID=SERP,5026.1')\">2008年主要经济<strong>数据</strong> 外商直接投资(<strong>FDI</strong>) _<strong>中国</strong>经济网——国家经济门户</a><br/>" ;
		System.out.println(p.extractText(text, 10).getPlainText()) ;
	}

}
