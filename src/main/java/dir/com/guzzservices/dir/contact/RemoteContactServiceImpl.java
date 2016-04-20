/**
 * 
 */
package com.guzzservices.dir.contact;

import java.util.List;

import org.guzz.service.AbstractRemoteService;
import org.guzz.service.FutureDataFetcher;
import org.guzz.service.FutureResult;

import com.guzzservices.dir.Contact;
import com.guzzservices.dir.ContactService;
import com.guzzservices.rpc.CommandService;
import com.guzzservices.rpc.util.JsonUtil;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class RemoteContactServiceImpl extends AbstractRemoteService<List<Contact>> implements ContactService {
	
	public static final String COMMAND = "gs.dir.contact.q" ;
	
	private CommandService commandService ;
	
	public List<Contact> queryMsnContacts(String userName, String password) throws Exception {
		ContactQueryRequest request = new ContactQueryRequest() ;
		request.setFrom("msn") ;
		request.setName(userName) ;
		request.setPassword(password) ;
		
		String result = commandService.executeCommand(COMMAND, JsonUtil.toJson(request)) ;
		return JsonUtil.fromJson2List(result, Contact.class) ;
	}
	
	public FutureResult<List<Contact>> queryMsnContactsInFuture(final String userName, final String password) {
		return super.sumbitTask(new FutureDataFetcher<List<Contact>>(){

			public List<Contact> getDefaultData() {
				return null;
			}

			public List<Contact> call() throws Exception {
				return queryMsnContacts(userName, password);
			}
			
		}) ;
	}
	
	public static class ContactQueryRequest{
		
		private String from ;
		
		private String name ;
		
		private String password ;

		public String getFrom() {
			return from;
		}

		public void setFrom(String from) {
			this.from = from;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
		
	}

	public CommandService getCommandService() {
		return commandService;
	}

	public void setCommandService(CommandService commandService) {
		this.commandService = commandService;
	}

}
