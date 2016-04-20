/**
 * 
 */
package com.guzzservices.rpc;

import java.nio.ByteBuffer;

/**
 * 
 * Execute a command and return.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface CommandService {
	
	/**
	 * Execute a command, and return the result as a string.
	 * 
	 * @param command command name
	 * @param param parameters
	 * @throws Exception
	 */
	public String executeCommand(String command, String param) throws Exception ;
	
	/**
	 * Execute a command, and return the result as a byte array.
	 * 
	 * @param command command name
	 * @param param parameters
	 * @throws Exception
	 */
	public byte[] executeCommand(String command, byte[] param) throws Exception ;
	
	/**
	 * Execute a command, and return the result as a ByteBuffer.
	 * 
	 * @param command command name
	 * @param param parameters
	 * @throws Exception
	 */
	public ByteBuffer executeCommand(String command, ByteBuffer param) throws Exception ;

}
