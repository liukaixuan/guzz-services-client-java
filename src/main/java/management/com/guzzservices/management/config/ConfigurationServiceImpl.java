/**
 * 
 */
package com.guzzservices.management.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.guzz.exception.DataTypeException;
import org.guzz.exception.ServiceExecutionException;
import org.guzz.service.AbstractService;
import org.guzz.service.ServiceConfig;
import org.guzz.util.Assert;
import org.guzz.util.StringUtil;
import org.guzz.util.thread.DemonQueuedThread;

import com.guzzservices.management.ConfigurationService;
import com.guzzservices.rpc.CommandService;
import com.guzzservices.rpc.util.JsonUtil;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ConfigurationServiceImpl extends AbstractService implements ConfigurationService {
	
	public static final String COMMAND_LOAD_CONFIG = "gs.conf.load.group" ;
	
	public static final String COMMAND_QUERY_VERSION = "gs.conf.q.version" ;
	
	private CommandService commandService ;
	
	private VersionCheckThread versionCheckThread ;
	
	private String groupId ;
	
	private int version = -1 ;
	
	private int checkIntervalInSeconds = 10 ;
		
	private Map<String, Object> configs = new HashMap<String, Object>() ;
	
	private Map<String, String> localConfigs ;

	public String getString(String parameter) {
		Object value = this.configs.get(parameter) ;
		
		if(value == null) return null ;
		
		if(value instanceof String){
			return (String) value ;
		}
		
		throw new DataTypeException("configuration item:[" + parameter + "] should be a " + value.getClass()) ;
	}
	
	public String getString(String parameter, String defaultValue) {
		String s = this.getString(parameter) ;
		
		return s == null ? defaultValue : s ;
	}

	public boolean getBoolean(String parameter, boolean defaultValue) {
		Object value = this.configs.get(parameter) ;
		
		if(value == null) return defaultValue ;
		
		if(value instanceof Boolean){
			return (Boolean) value ;
		}
		
		throw new DataTypeException("configuration item:[" + parameter + "] should be boolean but was " + value.getClass()) ;
	}

	public double getDouble(String parameter, double defaultValue) {
		Object value = this.configs.get(parameter) ;
		
		if(value == null) return defaultValue ;
		
		if(value instanceof Number){
			return ((Number) value).doubleValue() ;
		}
		
		throw new DataTypeException("configuration item:[" + parameter + "] should be double but was " + value.getClass()) ;
	}

	public float getFloat(String parameter, float defaultValue) {
		Object value = this.configs.get(parameter) ;
		
		if(value == null) return defaultValue ;
		
		if(value instanceof Number){
			return ((Number) value).floatValue() ;
		}
		
		throw new DataTypeException("configuration item:[" + parameter + "] should be float but was " + value.getClass()) ;
	}

	public int getInt(String parameter, int defaultValue) {
		Object value = this.configs.get(parameter) ;
		
		if(value == null) return defaultValue ;
		
		if(value instanceof Number){
			return ((Number) value).intValue() ;
		}
		
		throw new DataTypeException("configuration item:[" + parameter + "] should be int but was " + value.getClass()) ;
	}

	public long getLong(String parameter, long defaultValue) {
		Object value = this.configs.get(parameter) ;
		
		if(value == null) return defaultValue ;
		
		if(value instanceof Long){
			return ((Number) value).longValue() ;
		}
		
		throw new DataTypeException("configuration item:[" + parameter + "] should be long but was " + value.getClass()) ;
	}

	public short getShort(String parameter, short defaultValue) {
		Object value = this.configs.get(parameter) ;
		
		if(value == null) return defaultValue ;
		
		if(value instanceof Number){
			return ((Number) value).shortValue() ;
		}
		
		throw new DataTypeException("configuration item:[" + parameter + "] should be short but was " + value.getClass()) ;
	}

	public boolean configure(ServiceConfig[] scs) {
		if(scs.length == 1){
			String groupId = scs[0].getProps().getProperty("groupId") ;
			Assert.assertNotEmpty(groupId, "config item 'groupId' is a must!") ;
			
			this.groupId = groupId ;
			
			this.checkIntervalInSeconds = StringUtil.toInt(scs[0].getProps().getProperty("checkIntervalInSeconds"), this.checkIntervalInSeconds) ;
			Assert.assertBigger(this.checkIntervalInSeconds, 0, "config item 'checkIntervalInSeconds' must be postive!") ;
			
			localConfigs = new HashMap<String, String>() ;
			
			for(Entry e :scs[0].getProps().entrySet()){
				String key = (String) e.getKey() ;
				if("groupId".equals(key) || "checkIntervalInSeconds".equals(key)) continue ;
				
				localConfigs.put((String) e.getKey(), (String) e.getValue()) ;
			}
			
			return true ;
		}
		
		return false;
	}

	public boolean isAvailable() {
		return version > 0 ;
	}

	public void shutdown() {
		if(this.versionCheckThread != null){
			this.versionCheckThread.shutdown() ;
		}
	}

	public void startup() {
		this.versionCheckThread = new VersionCheckThread("configServiceVersionCheck", 1) ;
		this.versionCheckThread.setMillSecondsToSleep(checkIntervalInSeconds*1000) ;
		this.versionCheckThread.start() ;
	}

	public CommandService getCommandService() {
		return commandService;
	}

	public void setCommandService(CommandService commandService) {
		this.commandService = commandService;
	}
	
	class VersionCheckThread extends DemonQueuedThread{

		public VersionCheckThread(String threadName, int queueSize) {
			super(threadName, queueSize);
		}

		protected boolean doWithTheQueue() throws Exception {
			String s_version = commandService.executeCommand(COMMAND_QUERY_VERSION, groupId) ;
			int i_version = Integer.parseInt(s_version) ;
			
			if(i_version > version){
				//new update, load the map from the server.
				String result = commandService.executeCommand(COMMAND_LOAD_CONFIG, groupId) ;
				Map<String, Object> m = JsonUtil.fromJson(result, HashMap.class) ;
				
				if(log.isInfoEnabled()){
					log.info("reloading configuration for group:[" + groupId + "], from version " + version + " to " + i_version) ;
				}
				
				//update to the latest version.
				version = i_version ;
				configs = m ;
				
				//override by the local config
				configs.putAll(localConfigs) ;
			}
			
			return false ;
		}
	}

	public void clear() {
		throw new ServiceExecutionException("no write operation allowed.") ;
	}

	public boolean containsKey(Object key) {
		throw new ServiceExecutionException("not supported") ;
	}

	public boolean containsValue(Object value) {
		throw new ServiceExecutionException("not supported") ;
	}

	public Set entrySet() {
		throw new ServiceExecutionException("not supported") ;
	}

	public Object get(Object key) {
		return this.getString((String) key);
	}

	public boolean isEmpty() {
		throw new ServiceExecutionException("not supported") ;
	}

	public Set keySet() {
		throw new ServiceExecutionException("not supported") ;
	}

	public Object put(String key, Object value) {
		throw new ServiceExecutionException("no write operation allowed.") ;
	}

	public void putAll(Map t) {
		throw new ServiceExecutionException("no write operation allowed.") ;
	}

	public Object remove(Object key) {
		throw new ServiceExecutionException("no write operation allowed.") ;
	}

	public int size() {
		throw new ServiceExecutionException("not supported") ;
	}

	public Collection values() {
		throw new ServiceExecutionException("not supported") ;
	}

}
