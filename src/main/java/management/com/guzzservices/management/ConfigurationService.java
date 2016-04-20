/**
 * 
 */
package com.guzzservices.management;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface ConfigurationService extends java.util.Map<String, Object>{
	
	/**get string type parameter. including string and text.*/
	public String getString(String parameter) ;	

	/**get string type parameter. including string and text.*/
	public String getString(String parameter, String defaultValue) ;

	/**get short type parameter. including string and text.*/
	public short getShort(String parameter, short defaultValue) ;
	
	public int getInt(String parameter, int defaultValue) ;
	
	public long getLong(String parameter, long defaultValue) ;
	
	public float getFloat(String parameter, float defaultValue) ;
	
	public double getDouble(String parameter, double defaultValue) ;

	public boolean getBoolean(String parameter, boolean defaultValue) ;

}
