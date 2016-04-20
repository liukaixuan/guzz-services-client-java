/**
 * 
 */
package com.guzzservices.rpc.server;

import java.nio.ByteBuffer;

import org.guzz.exception.ServiceExecutionException;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class CommandHandlerAdapter implements CommandHandler {

	public String executeCommand(String command, String param) throws Exception {
		throw new ServiceExecutionException("not supported!") ;
	}

	public byte[] executeCommand(String command, byte[] param) throws Exception {
		throw new ServiceExecutionException("not supported!") ;
	}

	/**
	 * Pass to {@link #executeCommand(String, byte[])} in default.
	 */
	public ByteBuffer executeCommand(String command, ByteBuffer param) throws Exception {
		byte[] result ;
		
		if(param == null){
			result = executeCommand(command, (byte[]) null) ;
		}else if(param.remaining() == 0){
			result = executeCommand(command, new byte[0]) ;
		}else{
			byte[] bs = new byte[param.remaining()] ;
			param.get(bs) ;
			param.clear() ;
			
			result = executeCommand(command, bs) ;
		}
		
		if(result == null){
			return null ;
		}else if(result.length == 0){
			return ByteBuffer.allocate(0) ;
		}else{
			ByteBuffer r = ByteBuffer.allocate(result.length) ;
			r.put(result) ;
			r.flip() ;
			
			return r ;
		}
	}

	public String executeCommand(ClientInfo client, String command, String param) throws Exception {
		return executeCommand(command, param) ;
	}

	public byte[] executeCommand(ClientInfo client, String command, byte[] param) throws Exception {
		return executeCommand(command, param) ;
	}

	public ByteBuffer executeCommand(ClientInfo client, String command, ByteBuffer param) throws Exception {
		return executeCommand(command, param) ;
	}

}
