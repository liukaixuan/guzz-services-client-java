/**
 * 
 */
package com.guzzservices.dir;

import java.io.Serializable;

/**
 * 
 * 联系人信息。
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class Contact implements Serializable {
	
	private String name ;
	
	private String email ;
	
	public Contact(){}
	
	public Contact(String name, String email){
		this.name = name ;
		this.email = email ;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
