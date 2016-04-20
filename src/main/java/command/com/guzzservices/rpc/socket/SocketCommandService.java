package com.guzzservices.rpc.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.util.StringUtil;

import com.guzzservices.rpc.CommandException;

/**
 * 
 * Socket through TCP/IP
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class SocketCommandService {
	private static final Log log = LogFactory.getLog(SocketCommandService.class) ;
	private final Properties props ;
	
	private final SocketChannel channel ;
	//private InputStream is ;
	//private OutputStream os ;
	private final String charset ;
	private final short version = 1 ;
	
	private boolean isClosed ;
	
	private boolean disposedForIOException ;
	
	public boolean isClosed(){
		return isClosed ;
	}
	
	public boolean isChannelConnected(){
		return this.channel.isConnected() ;
	}
	
	public void dispose(){
		if(!isClosed){
			isClosed = true ;
			this.disposedForIOException = false ;
			if(this.channel != null){
				try {
					this.channel.close() ;
				} catch (IOException e) {
					log.error("fail to close the channel.", e) ;
				}
			}
		}
	}
	
	public SocketCommandService(Properties props) throws UnknownHostException, IOException{
		this.props = props ;
		
		//int idleTimeSeconds = StringUtil.toInt(props.getProperty("idleTimeSeconds"), 300) ;
		int soTimeoutSeconds = StringUtil.toInt(props.getProperty("soTimeoutSeconds"), 15) ;
		String host = props.getProperty("host") ;
		int port = StringUtil.toInt(props.getProperty("port"), 6618) ;
		this.charset = props.getProperty("charset", "UTF-8") ;
		
		this.channel = SocketChannel.open(new InetSocketAddress(host, port)) ;
		Socket socket = channel.socket() ;
		
		socket.setTcpNoDelay(true) ;
		socket.setSoTimeout(soTimeoutSeconds * 1000) ;
		socket.setPerformancePreferences(1, 3, 0) ;
		socket.setTrafficClass(0x10) ;
		socket.setReceiveBufferSize(4096) ;
		
		this.channel.finishConnect() ;
	}
	
	public String executeCommand(String command, String param) throws Exception {
		byte[] bs = executeCommand(command, param == null ? null : param.getBytes(charset), true) ;
		
		if(bs == null){
			return null ;
		}
		
		return new String(bs, charset) ;
	}
	
	public byte[] executeCommand(String command, byte[] param) throws Exception {
		return executeCommand(command, param, false) ;
	}
	
	protected byte[] executeCommand(String command, byte[] param, boolean isStringParam) throws Exception {
		try {
			writeRequest(command, param, isStringParam) ;
			
			ByteBuffer buff = this.readResponse() ;
			
			if(buff == null) return null ;
			if(buff.remaining() == 0) return new byte[0] ;
			
			byte[] bs = new byte[buff.remaining()] ;
			buff.get(bs) ;
			buff.clear() ;
			
			return bs ;
		} catch (CommandException e){
			//CommandException is fine, we have cleaned the channel.
			throw e ;
		} catch (Exception e) {
			//on error dispose.
			this.dispose() ;
			this.disposedForIOException = true ;
			
			throw e ;
		}
	}
	
	public ByteBuffer executeCommand(String command, ByteBuffer param) throws Exception {
		try {
			byte[] bs = null ;
			if(param == null){
			}else if(param.remaining() == 0){
				bs = new byte[0] ;
			}else{
				bs = new byte[param.remaining()] ;
				param.get(bs) ;
				param.clear() ;
			}
			
			writeRequest(command, bs, false) ;
			
			return readResponse() ;
		} catch (CommandException e){
			//CommandException is fine, we have cleaned the channel.
			throw e ;
		} catch (Exception e) {
			//on error dispose.
			this.dispose() ;
			this.disposedForIOException = true ;
			
			throw e ;
		}
	}
	
	protected void writeRequest(String command, byte[] param, boolean isStringParam) throws IOException{
		byte[] commandBs = command.getBytes(charset) ;
		
		ByteBuffer buffer = null ;
		
		if(param == null){
			buffer = ByteBuffer.allocate(10 + commandBs.length) ;
			
			buffer.putShort(version) ;
			buffer.putShort((short) (isStringParam ? 1 : 0)) ;
			buffer.putShort((short) commandBs.length) ;
			buffer.putInt(-1) ;
			buffer.put(commandBs) ;
		}else{
			buffer = ByteBuffer.allocate(10 + commandBs.length + param.length) ;

			buffer.putShort(version) ;
			buffer.putShort((short) (isStringParam ? 1 : 0)) ;
			buffer.putShort((short) commandBs.length) ;
			buffer.putInt(param.length) ;
			buffer.put(commandBs) ;
			
			if(param.length > 0){
				buffer.put(param) ;
			}
		}
		
		buffer.flip() ;
		channel.write(buffer) ;
	}
	
//	protected byte[] readResponse() throws IOException, CommandException{
//		//read version
//		ByteBuffer buffer = ByteBuffer.allocate(10) ;
//		int retBytes = this.channel.read(buffer) ;
//		if(retBytes != 10){
//			buffer.clear() ;
//			throw new SocketException("header length error:" + retBytes) ;
//		}
//		
//		buffer.flip() ;
//		short version = buffer.getShort() ;
//		
//		if(version != this.version){
//			buffer.clear() ;
//			throw new SocketException("only support version 1. The received version is:" + version) ;
//		}
//		
//		boolean isException = buffer.getShort() == 1 ;
//		//boolean isStringResult  == 1
//		buffer.getShort() ;
//		int resultLen = buffer.getInt() ;
//		buffer.clear() ;
//		
//		byte[] result = null ;
//		
//		if(resultLen > 0){
//			buffer = ByteBuffer.allocate(resultLen) ;
//						
//			while(this.channel.read(buffer) != -1){
//				if(buffer.remaining() == 0) break ;
//			}
//			
//			buffer.flip() ;
//			if(buffer.remaining() != resultLen){
//				buffer.clear() ;
//				throw new IOException("result body length error.") ;
//			}
//			
//			result = new byte[resultLen] ;
//			buffer.get(result) ;
//			buffer.clear() ;
//		}else if(resultLen == 0){
//			result = new byte[0] ;
//		}
//		
//		//server exception. Throw after reading all responded data.
//		if(isException){
//			if(result == null){
//				throw new CommandException() ;
//			}else{
//				throw new CommandException(new String(result, charset)) ;
//			}
//		}
//		
//		return result ;
//	}
	
	protected ByteBuffer readResponse() throws IOException, CommandException{
		//read version
		ByteBuffer buffer = ByteBuffer.allocate(10) ;
		int retBytes = this.channel.read(buffer) ;
		if(retBytes != 10){
			buffer.clear() ;
			throw new SocketException("header length error:" + retBytes) ;
		}
		
		buffer.flip() ;
		short version = buffer.getShort() ;
		
		if(version != this.version){
			buffer.clear() ;
			throw new SocketException("only support version 1. The received version is:" + version) ;
		}
		
		boolean isException = buffer.getShort() == 1 ;
		//boolean isStringResult  == 1
		buffer.getShort() ;
		int resultLen = buffer.getInt() ;
		buffer.clear() ;
		
		ByteBuffer body = null ;
		
		if(resultLen > 0){
			body = ByteBuffer.allocate(resultLen) ;
						
			while(this.channel.read(body) != -1){
				if(body.remaining() == 0) break ;
			}
			
			body.flip() ;
			if(body.remaining() != resultLen){
				body.clear() ;
				throw new IOException("result body length error.") ;
			}
			
		}else if(resultLen == 0){
			body = ByteBuffer.allocate(0) ;
		}
		
		//server exception. Throw after reading all responded data.
		if(isException){
			if(body == null){
				throw new CommandException() ;
			}else if(body.remaining() == 0){
				throw new CommandException() ;
			}else{
				byte[] result = new byte[resultLen] ;
				body.get(result) ;
				body.clear() ;
				
				throw new CommandException(new String(result, charset)) ;
			}
		}
		
		return body ;
	}

	public boolean isDisposedForIOException() {
		return disposedForIOException;
	}

	public Properties getProps() {
		return props;
	}

	public void setDisposedForIOException(boolean disposedForIOException) {
		this.disposedForIOException = disposedForIOException;
	}
	
}
