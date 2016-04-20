/**
 * 
 */
package com.guzzservices.dir.ip;

import org.guzz.service.AbstractRemoteService;
import org.guzz.service.FutureDataFetcher;
import org.guzz.service.FutureResult;
import org.guzz.util.StringUtil;

import com.guzzservices.dir.IPLocateService;
import com.guzzservices.rpc.CommandService;
import com.guzzservices.rpc.util.JsonUtil;

/**
 * 
 * IPService client API--call remote service by {@link CommandService} in json
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ChinaIPLocateServiceImpl extends AbstractRemoteService<LocateResult> implements IPLocateService {
	
	public static final String COMMAND_IP_QUERY = "gs.ip.q.cn" ;
	
	private CommandService commandService ;

	public LocateResult findLocation(String IP) {
		if(StringUtil.isEmpty(IP)) return null ;
		
		try {
			String result = commandService.executeCommand(COMMAND_IP_QUERY, IP) ;
			
			if(result == null) return null ;
			
			return JsonUtil.fromJson(result, LocateResult.class) ;
		} catch (Exception e) {
			log.error(IP, e) ;
		}
		
		return null;
	}

	public FutureResult<LocateResult> findLocationInFuture(final String IP) {
		return super.sumbitTask(new FutureDataFetcher<LocateResult>() {
			
			public LocateResult call() throws Exception {
				return findLocation(IP);
			}
			
			public LocateResult getDefaultData() {
				return null;
			}
		}) ;
	}

	public CommandService getCommandService() {
		return commandService;
	}

	public void setCommandService(CommandService commandService) {
		this.commandService = commandService;
	}
	
}
