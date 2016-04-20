/**
 * 
 */
package com.guzzservices.rpc;

import java.lang.reflect.ParameterizedType;

import org.guzz.service.AbstractService;
import org.guzz.service.ServiceConfig;

import com.guzzservices.rpc.util.JsonUtil;

/**
 * 
 * json based service with the command service call.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class JsonCommandService<RequestType, ResponseType> extends AbstractService {
	
	private CommandService commandService ;
	
	private Class<ResponseType> resultClass ;
	
	@SuppressWarnings("unchecked")
	public JsonCommandService(){
		resultClass = (Class<ResponseType>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1]; 
	}
	
	protected ResponseType executeJsonCommand(String command, RequestType param) throws Exception {		
		String json = JsonUtil.toJson(param) ;
		
		String result = this.commandService.executeCommand(command, json) ;
		
		if(result == null) return null ;
		
		return JsonUtil.fromJson(result, resultClass) ;
	}

	public boolean configure(ServiceConfig[] scs) {
		return true;
	}

	public boolean isAvailable() {
		return true;
	}

	public void shutdown() {

	}

	public void startup() {

	}

	public final CommandService getCommandService() {
		return commandService;
	}

	public final void setCommandService(CommandService commandService) {
		this.commandService = commandService;
	}

}
