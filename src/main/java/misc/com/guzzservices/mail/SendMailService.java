/**
 * 
 */
package com.guzzservices.mail;

/**
 * 
 * 邮件服务。
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface SendMailService {

	/**
	 * 发送text/plain的纯文本邮件
	 * 
	 * @param from 发送者
	 * @param to 接受者
	 * @param subject 邮件标题
	 * @param content 邮件内容
	 * @return 是否发送成功。结果的可靠程度由具体实现者确定。
	 * */
	public boolean sendPlainMail(String from, String to, String subject, String content) ;
	
	/**
	 * 发送text/html的HTML文本邮件
	 * 
	 * @param from 发送者
	 * @param to 接受者
	 * @param subject 邮件标题
	 * @param content 邮件内容
	 * @return 是否发送成功。结果的可靠程度由具体实现者确定。
	 * */
	public boolean sendHtmlMail(String from, String to, String subject, String content) ;

}
