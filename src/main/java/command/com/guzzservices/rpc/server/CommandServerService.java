/**
 * 
 */
package com.guzzservices.rpc.server;

/**
 * 
 * server-side CommandService Acceptor.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface CommandServerService {
	
	public void addCommandHandler(String command, CommandHandler handler) ;

	public CommandHandler removeCommandHandler(String command) ;
	
	public CommandHandler queryCommandHandler(String command) ;
	
	public CommandResponse executeCommand(ClientInfo client, CommandRequest request) ;
	
	public boolean isAuthedClient(ClientInfo client) ;
	
}
