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
public class CommandRequest {
	
	public String command ;
	
	/**Fliped ByteBuffer*/
	public ByteBuffer paramB ;
	
	public String paramS ;
	
	public boolean isStringParam ;

}
