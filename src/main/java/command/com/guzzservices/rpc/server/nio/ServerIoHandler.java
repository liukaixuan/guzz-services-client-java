/**
 * 
 */
package com.guzzservices.rpc.server.nio;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.guzzservices.rpc.server.ClientInfo;
import com.guzzservices.rpc.server.CommandRequest;
import com.guzzservices.rpc.server.CommandResponse;
import com.guzzservices.rpc.server.CommandServerService;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ServerIoHandler extends IoHandlerAdapter {
	private static final Log log = LogFactory.getLog(ServerIoHandler.class) ;
	
	private final CommandServerService commandServerService ;
	
	public ServerIoHandler(CommandServerService commandServerService){
		this.commandServerService = commandServerService ;
	}
	
	public void messageReceived(IoSession session, Object message) throws Exception {
		CommandRequest request = (CommandRequest) message ;
		InetSocketAddress addr = (InetSocketAddress) session.getRemoteAddress() ;		
		ClientInfo client = new ClientInfo(addr) ;
		
		CommandResponse rp = this.commandServerService.executeCommand(client, request) ;
		
		session.write(rp);
	}
	
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		StringBuilder sb = new StringBuilder(64) ;
		sb.append(cause.getMessage())
		  .append('(')
		  .append(session.getRemoteAddress())
		  .append(')') ;
		
		if(log.isDebugEnabled()){
			log.debug(sb.toString(), cause) ;
		}else{
			log.info(sb.toString());
		}
	    
	    session.close(true) ;
	}

	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		if(log.isInfoEnabled()){
			log.info("sessionIdle. count:" + session.getWriterIdleCount() + ", last write time:" + session.getLastWriteTime()) ;
		}
		
		if(IdleStatus.WRITER_IDLE.equals(status)){
			session.close(false) ;
		}
	}

	public void sessionCreated(IoSession session) throws Exception {
		InetSocketAddress addr = (InetSocketAddress) session.getRemoteAddress() ;		
		ClientInfo client = new ClientInfo(addr) ;
		
		//没有授权的IP
		if(!this.commandServerService.isAuthedClient(client)){
			throw new IOException("Permission denied! Your IP:" + client) ;
		}
	}

}
