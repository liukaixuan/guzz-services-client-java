/**
 * 
 */
package com.guzzservices.dir;

import java.util.List;

import org.guzz.service.FutureResult;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface ContactService {
	
	/**
	 * 查询联系人列表。
	 * 
	 * @param ServiceExecutionException 如果遇到错误或超时。
	 */
	public List<Contact> queryMsnContacts(String userName, String password) throws Exception ;
	
	public FutureResult<List<Contact>> queryMsnContactsInFuture(final String userName, final String password) ;

}
