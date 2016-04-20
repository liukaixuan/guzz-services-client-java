/**
 * 
 */
package com.guzzservices.rpc.server;

import java.nio.ByteBuffer;

/**
 * 
 * Handle a remote command.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface CommandHandler {
	/**
	 * Execute a command, and return the result as a string.
	 * 
	 * @param client
	 * @param command command name
	 * @param param parameters
	 * @throws Exception
	 */
	public String executeCommand(ClientInfo client, String command, String param) throws Exception ;
	
	/**
	 * Execute a command, and return the result as a byte array.
	 * 
	 * @param client
	 * @param command command name
	 * @param param parameters
	 * @throws Exception
	 */
	public byte[] executeCommand(ClientInfo client, String command, byte[] param) throws Exception ;
	
	/**
	 * Execute a command, and return the result as a ByteBuffer.
	 * 
	 * @param client
	 * @param command command name
	 * @param param parameter
	 * @throws Exception
	 */
	public ByteBuffer executeCommand(ClientInfo client, String command, ByteBuffer param) throws Exception ;
	
}
