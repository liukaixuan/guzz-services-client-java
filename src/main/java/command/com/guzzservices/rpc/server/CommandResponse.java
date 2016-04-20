/**
 * 
 */
package com.guzzservices.rpc.server;

import java.nio.ByteBuffer;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class CommandResponse {
	
	/**Fliped ByteBuffer*/
	public ByteBuffer resultB ;
	
	public String resultS ;
	
	public boolean isStringResult ;
	
	public boolean isException ;

}
