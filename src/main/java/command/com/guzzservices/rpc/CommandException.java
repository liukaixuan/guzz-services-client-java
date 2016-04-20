/**
 * 
 */
package com.guzzservices.rpc;

/**
 * 
 * Exception while executing a command
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class CommandException extends RuntimeException {

	public CommandException(){
		
	}
	
	public CommandException(String msg){
		super(msg) ;
	}
	
}
