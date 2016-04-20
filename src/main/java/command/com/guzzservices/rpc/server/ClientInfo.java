/**
 * 
 */
package com.guzzservices.rpc.server;

import java.net.InetSocketAddress;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ClientInfo {
	
	private String IP ;
	
	private int port ;
	
	public ClientInfo(InetSocketAddress addr){
		this.port = addr.getPort() ;
		if(addr.getAddress() != null){
			this.IP = addr.getAddress().getHostAddress() ;
		}
	}
	
	public ClientInfo(String IP, int port){
		this.IP = IP ;
		this.port = port ;
	}

	public String getIP() {
		return IP;
	}

	public int getPort() {
		return port;
	}
	
	public String toString(){
		return IP + ":" + port ;
	}

}
