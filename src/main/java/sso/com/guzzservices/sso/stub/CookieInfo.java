/**
 * 
 */
package com.guzzservices.sso.stub;

/**
 * 
 * cookie info passed between sso client and server.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class CookieInfo implements java.io.Serializable{
	
	private String name ;
	
	private String value ;
	
	private String domain ;
	
	private int maxAge ;
	
	/**
	 * deleted cookie info
	 */
	public static CookieInfo newCookieInfo(String name, String domain){
		return newCookieInfo(name, "", domain, 0) ;
	}
	
	/**
	 * added cookie info
	 */
	public static CookieInfo newCookieInfo(String name, String value, String domain, int maxAge){
		CookieInfo c = new CookieInfo() ;
		c.setMaxAge(maxAge) ;
		c.setName(name) ;
		c.setDomain(domain) ;
		c.setValue(value) ;
		
		return c ;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String cookieDomain) {
		this.domain = cookieDomain;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

}
