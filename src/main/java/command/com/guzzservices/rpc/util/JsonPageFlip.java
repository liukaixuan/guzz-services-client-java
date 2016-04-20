/**
 * 
 */
package com.guzzservices.rpc.util;

import java.util.List;

import org.guzz.dao.PageFlip;

/**
 * 
 * 用于json转换的PageFlip. 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class JsonPageFlip<T> {
	
	public String toJson(){
		__JsonPageFlip tmp = new __JsonPageFlip() ;
		tmp.pageNo = this.pageNo ;
		tmp.pageSize = this.pageSize ;
		tmp.totalCount = this.totalCount ;
		tmp.elements = JsonUtil.toJson(this.elements) ;
		
		return JsonUtil.toJson(tmp) ;
	}
	
	public static <T> JsonPageFlip<T> fromJson(String json, Class<T> elementType){
		__JsonPageFlip tmp = JsonUtil.fromJson(json, __JsonPageFlip.class) ;
		
		JsonPageFlip<T> jpf = new JsonPageFlip<T>() ;
		jpf.pageNo = tmp.pageNo ;
		jpf.pageSize = tmp.pageSize ;
		jpf.totalCount = tmp.totalCount ;
		jpf.elements = JsonUtil.fromJson2List(tmp.elements, elementType) ;
		
		return jpf ;
	}
	
	public static <T> JsonPageFlip<T> fromPageFlip(PageFlip pageFlip, Class<T> elementType){
		JsonPageFlip<T> jpf = new JsonPageFlip<T>() ;
		jpf.pageNo = pageFlip.getPageNo() ;
		jpf.pageSize = pageFlip.getPageSize() ;
		jpf.totalCount = pageFlip.getTotalCount() ;
		jpf.elements = pageFlip.getElements() ;
		
		return jpf ;
	}
	
	public PageFlip toPageFlip(){
		PageFlip pf = new PageFlip() ;
		pf.setResult(totalCount, pageNo, pageSize, elements) ;
		
		return pf ;
	}
	
	private int pageNo ;
	
	private int pageSize ;
	
	private int totalCount ;
	
	private List<T> elements ;

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<T> getElements() {
		return elements;
	}

	public void setElements(List<T> elements) {
		this.elements = elements;
	}
	
	static class __JsonPageFlip{
		
		public int pageNo ;
		
		public int pageSize ;
		
		public int totalCount ;
		
		public String elements ;
	}
	

}
