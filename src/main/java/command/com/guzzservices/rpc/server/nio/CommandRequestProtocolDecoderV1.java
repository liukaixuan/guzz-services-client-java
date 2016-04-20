package com.guzzservices.rpc.server.nio;

import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.guzzservices.rpc.server.CommandRequest;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class CommandRequestProtocolDecoderV1 extends CumulativeProtocolDecoder {
	private static final Log log = LogFactory.getLog(CommandRequestProtocolDecoderV1.class) ;
	private static final String CONTEXT = "GS_CRPDV1_CONTEXT" ;
		
	private final CharsetDecoder cd ;
	
	public CommandRequestProtocolDecoderV1(Charset charset){
		cd = charset.newDecoder() ;
	}

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		Context context = getContext(session) ;
		
		if(!context.isHeadRead()){
			//form CommandRequest
			if(in.remaining() < 10){
				return false ;
			}
			
			//读取版本信息
			short version = in.getShort() ;
			
			if(version != 1){
				if(log.isDebugEnabled()){
					log.debug("package received:" + Base64.encodeBase64String(in.array())) ;
				}
				
				throw new SocketException("wrong version! expected version 1, but was:" + version) ;
			}
			
			context.setStringParam(in.getShort() == (short) 1) ;
			context.setCommandBLen(in.getShort()) ;
			context.setParamLen(in.getInt()) ;
			
			context.setHeadRead(true) ;
		}
		
		if(!context.isCommandRead()){
			short commandBLen = context.getCommandBLen() ;
			
			if(in.remaining() >= commandBLen){
				context.setCommand(in.getString(commandBLen, cd)) ;
				context.setCommandRead(true) ;
			}else{
				return false ;
			}
		}
		
		int paramLen = context.getParamLen() ;
		int inputBodyCount = context.getInputBodyCount() ;
		IoBuffer buffer = context.getInnerBuffer() ;
		
		while(inputBodyCount < paramLen && in.hasRemaining()){
			buffer.put(in.get()) ;
			inputBodyCount++ ;
		}
		
		//reading a command or end of the stream
		
		//a command is prepared.
		if(inputBodyCount >= paramLen){
			CommandRequest cr = new CommandRequest() ;
			
			cr.command = context.getCommand() ;
			cr.isStringParam = context.isStringParam() ;
			
			buffer.flip() ;
			
			if(paramLen > 0){
				if(cr.isStringParam){
					cr.paramS = buffer.getString(paramLen, cd) ;
				}else{
					cr.paramB = buffer.buf() ;
				}
			}
			
			context.reset() ;
			
			out.write(cr) ;
			return true ;
		}else{
			context.setInputBodyCount(inputBodyCount) ;
			
			return false ;
		}
	}
	
	private Context getContext(IoSession session) {
		Context context = (Context) session.getAttribute(CONTEXT);
		if (context == null) {
			context = new Context();
			session.setAttribute(CONTEXT, context);
		}
		
		return context;
	}
	
	private static class Context {
		//param body
		private final IoBuffer innerBuffer;
		
		private boolean headRead ;
		
		private boolean commandRead ;
		
		private boolean isStringParam ;
		
		private short commandBLen  ;
		
		private int paramLen ;
		
		private String command ;
		
		private int inputBodyCount ;
		
		
		public Context() {
			innerBuffer = IoBuffer.allocate(100).setAutoExpand(true);
			reset() ;
		}
		
		public void reset(){
			headRead = false ;
			commandRead = false ;
			command = null ;
			inputBodyCount = 0 ;
			innerBuffer.clear() ;
		}

		public boolean isHeadRead() {
			return headRead;
		}

		public void setHeadRead(boolean headRead) {
			this.headRead = headRead;
		}

		public IoBuffer getInnerBuffer() {
			return innerBuffer;
		}

		public boolean isCommandRead() {
			return commandRead;
		}

		public void setCommandRead(boolean commandRead) {
			this.commandRead = commandRead;
		}

		public boolean isStringParam() {
			return isStringParam;
		}

		public void setStringParam(boolean isStringParam) {
			this.isStringParam = isStringParam;
		}

		public short getCommandBLen() {
			return commandBLen;
		}

		public void setCommandBLen(short commandBLen) {
			this.commandBLen = commandBLen;
		}

		public int getParamLen() {
			return paramLen;
		}

		public void setParamLen(int paramLen) {
			this.paramLen = paramLen;
		}

		public String getCommand() {
			return command;
		}

		public void setCommand(String command) {
			this.command = command;
		}

		public int getInputBodyCount() {
			return inputBodyCount;
		}

		public void setInputBodyCount(int inputBodyCount) {
			this.inputBodyCount = inputBodyCount;
		}
	
	}

}
