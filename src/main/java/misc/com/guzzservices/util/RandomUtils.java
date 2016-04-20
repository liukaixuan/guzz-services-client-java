/**
 * 
 */
package com.guzzservices.util;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * 
 * 名字真难想。。。。
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class RandomUtils {

	private static SecureRandom random = new SecureRandom() ;
	
	public static String generateRandomString(int length){
		String s = new BigInteger(length*5, random).toString(36);
		
		if(s.length() > length){
			return s.substring(0, s.length()) ;
		}
		
		return s ;
	}

}
