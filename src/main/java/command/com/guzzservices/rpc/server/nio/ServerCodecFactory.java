/**
 * 
 */
package com.guzzservices.rpc.server.nio;

import java.nio.charset.Charset;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ServerCodecFactory implements ProtocolCodecFactory{
	
	private final CommandRequestProtocolDecoderV1 decoder ;
	private final CommandResultProtocolEncoder encoder ;
	
	public ServerCodecFactory(String charset){
		Charset c = Charset.forName(charset) ;
		
		this.decoder = new CommandRequestProtocolDecoderV1(c) ;
		this.encoder = new CommandResultProtocolEncoder(charset) ;
	}

	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder ;
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder ;
	}

}
