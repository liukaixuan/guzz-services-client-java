package com.guzzservices.rpc.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.guzz.service.AbstractService;
import org.guzz.service.ServiceConfig;
import org.guzz.util.Assert;
import org.guzz.util.StringUtil;

import com.guzzservices.rpc.CommandException;
import com.guzzservices.rpc.CommandService;

/**
 * 
 * Execute command through a connected TCP/IP socket pool.
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class CommandServiceClientImpl extends AbstractService implements CommandService {
	private static final Log log = LogFactory.getLog(CommandServiceClientImpl.class) ;
	
	private GenericObjectPool pool ;
	
	private Properties firstProps ;
	
	private SocketClientPoolFactory scf = new SocketClientPoolFactory() ;
	
	protected SocketCommandService getClient(){
		SocketCommandService client = null ;
		try {
			client = (SocketCommandService) pool.borrowObject();
		} catch (Exception e) {
			log.error("cann't get client. configured firstProps:[" + this.firstProps + "].", e) ;
		}
		
		return client ;
	}
	
	protected void returnClient(SocketCommandService client){
		try {
			pool.returnObject(client) ;
		} catch (Exception e) {
			log.error("cann't return client. configured firstProps:[" + this.firstProps + "].", e) ;
		}
	}

	public String executeCommand(String command, String param) throws Exception {
		final SocketCommandService client = getClient() ;
		
		if(client == null || client.isClosed()) {
			throw new CommandException("no idle socket available!") ;
		}
		
		try{
			return client.executeCommand(command, param) ;
		}catch(IOException e){
			//retry again
			return client.executeCommand(command, param) ;
		}finally{
			returnClient(client) ;
		}
	}

	public byte[] executeCommand(String command, byte[] param) throws Exception {
		final SocketCommandService client = getClient() ;
		
		if(client == null || client.isClosed()) {
			throw new CommandException("no idle socket available!") ;
		}
		
		try{
			return client.executeCommand(command, param) ;
		}catch(IOException e){
			//retry again
			return client.executeCommand(command, param) ;
		}finally{
			returnClient(client) ;
		}
	}
	
	public ByteBuffer executeCommand(String command, ByteBuffer param) throws Exception{
		final SocketCommandService client = getClient() ;
		
		if(client == null || client.isClosed()) {
			throw new CommandException("no idle socket available!") ;
		}
		
		try{
			return client.executeCommand(command, param) ;
		}catch(IOException e){
			//retry again
			return client.executeCommand(command, param) ;
		}finally{
			returnClient(client) ;
		}
	}

	public boolean configure(ServiceConfig[] scs) {
		if(scs.length == 0){
			log.warn("CommandService Service is not started. no configuration found.") ;
			return false ;
		}else{
			this.firstProps = scs[0].getProps() ;
			
			Properties[] ps = new Properties[scs.length] ;
			for(int i = 0 ; i < scs.length ; i++){
				ps[i] = scs[i].getProps() ;
				
				//check parameter "host" exists.
				String host = ps[i].getProperty("host") ;
				
				//Invalid configuration
				Assert.assertNotEmpty(host, "Missing configuration item [host] in [" + ps[i] + "].") ;
			}
			
			pool = new GenericObjectPool(scf) ;
			scf.setProperties(pool, ps) ;

			//testing cost is very low
			pool.setTestOnBorrow(true) ;
			
			//test on borrow
			pool.setTestOnReturn(false) ;
			pool.setTestWhileIdle(false) ;
			pool.setTimeBetweenEvictionRunsMillis(2000) ;
			pool.setNumTestsPerEvictionRun(10) ;
			pool.setMaxWait(3000) ;
			
			pool.setMaxIdle(StringUtil.toInt(this.firstProps.getProperty("pool.maxIdle"), GenericObjectPool.DEFAULT_MAX_IDLE)) ;
			pool.setMinIdle(StringUtil.toInt(this.firstProps.getProperty("pool.minIdle"), 3)) ;
			
			int whenExhaustedAction = StringUtil.toInt(this.firstProps.getProperty("pool.whenExhaustedAction"), 3) ;
			if(whenExhaustedAction == 1){
				pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_FAIL) ;
			}else if(whenExhaustedAction == 2){
				pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_GROW) ;
			}else{
				pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK) ;
			}
			
			return true ;
		}
	}

	public boolean isAvailable() {
		return pool != null ;
	}
	
	public void shutdown() {
		try {
			scf.shutdown() ;
			pool.close() ;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startup() {
		scf.startup() ;
	}
}
