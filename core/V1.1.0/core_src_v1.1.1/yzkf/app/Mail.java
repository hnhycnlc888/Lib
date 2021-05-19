package yzkf.app;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang.StringUtils;

import yzkf.config.ConfigFactory;
import yzkf.config.MailConfig;
import yzkf.exception.MailException;
import yzkf.exception.ParserConfigException;

import com.sun.mail.smtp.SMTPTransport;
/**
 * 邮件<br/>
 * v1.0.1 2011.11.24<br/>
 * 依赖 JavaMail 1.4.4<br/>
 * 示例：
 * <pre>
try {
	ConfigFactory factory = ConfigFactory.getInstance();
	MailConfig config = factory.newMailConfig();
	//使用config配置对象 创建 计算活跃 的邮件对象
	Mail mail = new Mail(true,config);
	//设置发件人
	mail.setFrom("13760709457@139.com");
	//设置发件人名称
	mail.setFromname("Leo");
	//设置收件人，多个收件人逗号隔开
	mail.setTo("13912345678@139.com,alias@139.com");	
	//设置抄送
	//mail.setCc("13987654321@139.com,alias2@139.com");
	//设置密送
	//mail.setBcc("13912341234@139.com,alias3@139.com");
	mail.setSubject("千里寻她千百度2");//设置邮件标题
	//设置html正文
	mail.setHtml("<html><head><title>Hello 139 Mail!</title></head><body>Hello,I'm content of web.</body></html>");
	//设置wap简版正文，注意：仅限139邮箱，如果发送外遇，请不要同时设置 setHtml 和 setText，外遇邮箱将同时显示2种内容
	mail.setText("Hello,I'm content of wap!");
	//设置系统参数，对附件文件名进行编码处理，当文件名中有中文时必须设置，否则将出现乱码
	//System.setProperty("mail.mime.encodefilename", "true");
	//增加附件,如果附件的文件名含有中文，
	mail.addAttachments("E:\\attachment1.txt");
	//增加多个附件
	mail.addAttachments("E:\\attachment2.txt");
	//发送邮件
	mail.Send();
} catch (MailException e) {
	// 邮件发送异常
	e.printStackTrace();
} catch (ParserConfigException e1) {
	// 读取邮件配置文件异常
	e1.printStackTrace();
}
 * <pre>
 * @author qiulw
 *
 */
public class Mail {
	private String mailhost = "smtp.139.com";
	private String mailPort = "";
	private String activeSMTP;
	private String noActvieSMTP;
	private boolean debug = false;
	private boolean auth = false;
	private boolean active = true;	
	private String from;
	private String fromname;
	private String to;
	private String cc;
	private String bcc;
	private String subject;
	//private Vector<String> attachments;	
	private Vector<File> attachments;
	private String text;
	private String html;
	private String user;
	private String password;	
	private String testSendDomain;
	private String testReceiveDomain;
	/**
	 * 创建Mail对象
	 * @param active 是否计算活跃，不提供则默认true
	 * @param config Mail配置类对象，不提供则使用默认配置文件
	 * @throws ParserConfigException
	 */
	public Mail(boolean active,MailConfig config) throws ParserConfigException{
		if(config != null){
			activeSMTP = config.getActiveSMTPServer();
			noActvieSMTP = config.getNoActiveSMTPSever();
			setAuth(config.isTest());
			setUser(config.getTestUID());
			setPassword(config.getTestPWD());
			setActive(active);
			testSendDomain = config.getTestSendDomain();
			testReceiveDomain = config.getTestReceiveDomain();
		}		
		setAttachments(new Vector<File>());
	}
	/**
	 * 创建Mail对象
	 * @param config Mail配置类对象
	 * @throws ParserConfigException
	 */
	public Mail(MailConfig config) throws ParserConfigException{
		this(true,config);
	}
	/**
	 * 创建Mail对象<br>
	 * - 该方法使Mail与MailConfig耦合度增高，不建议使用，只为方便获取Mail对象
	 * @param active 是否计算活跃，不提供则默认true
	 * @throws ParserConfigException
	 */
	public Mail(boolean active) throws ParserConfigException{
		this(active,(ConfigFactory.getInstance()).newMailConfig());
	}
	/**
	 * 创建Mail对象<br>
	 * - 该方法使Mail与MailConfig耦合度增高，不建议使用，只为方便获取Mail对象
	 * @throws ParserConfigException
	 */
	public Mail() throws ParserConfigException{
		this(true);
	}
	/**
	 * 发送邮件
	 * @return
	 * @throws MailException
	 */
	public void Send() throws MailException{
//		try{
			Properties props = new Properties();
			props.put("mail.smtp.host", mailhost);
			if(!StringUtils.isEmpty(mailPort))
				props.put("mail.smtp.port",mailPort);
			if(auth)
				props.put("mail.smtp.auth", true);
			Session session = Session.getInstance(props);
			if(debug)
				session.setDebug(true);
			Message msg = new MimeMessage(session);
			try {
				if (getFrom() != null)				
					msg.setFrom(new InternetAddress(getFrom(),fromname));				
				else
					msg.setFrom();
			} catch (UnsupportedEncodingException e) {
				MailException me = new MailException("设置发件人时发生异常");
				me.initCause(e);
				throw me;
			} catch(MessagingException ex){
				MailException me = new MailException("设置发件人时发生异常，请检查发件人是否正确");
				me.initCause(ex);
				throw me;
			}
			try {
				msg.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(getTo(), false));			
			if (getCc() != null)
				msg.setRecipients(Message.RecipientType.CC,
							InternetAddress.parse(getCc(), false));
		    if (getBcc() != null)
		    	msg.setRecipients(Message.RecipientType.BCC,
						InternetAddress.parse(getBcc(), false));
			} catch (MessagingException e) {
				MailException me = new MailException("设置收件人时发生异常，请检查收件人是否正确");
				me.initCause(e);
				throw me;
			}
		    try {
				msg.setSubject(subject);
			    MimeMultipart mp = new MimeMultipart();		    
			    MimeBodyPart htmlMBP = new MimeBodyPart();
			    htmlMBP.setDataHandler(new DataHandler(
					new ByteArrayDataSource(html, "text/html")));
				mp.addBodyPart(htmlMBP);
				if(text != null){
					MimeBodyPart textMBP = new MimeBodyPart();
					textMBP.setText(text);
				    mp.addBodyPart(textMBP);
				}
			    if (this.attachments != null && !this.attachments.isEmpty()) {			    	
			    	for(File file : attachments){		
			    		MimeBodyPart fileMBP = new MimeBodyPart();
			    		fileMBP.attachFile(file);
						mp.addBodyPart(fileMBP);
			    	}			    	
				}
			    msg.setContent(mp);		    
			    msg.setHeader("X-Mailer", "jmail");
			    msg.setSentDate(new Date());
		    } catch (MessagingException e) {
		    	MailException me = new MailException("设置邮件内容时发生异常");
				me.initCause(e);
				throw me;
			}catch(IOException ex){
				MailException me = new MailException("设置邮件正文或附件时发生IO异常");
				me.initCause(ex);
				throw me;
			}
			try{
		    SMTPTransport trans = (SMTPTransport)session.getTransport("smtp");
			    try {
					if (auth)
						trans.connect(mailhost, user, password);
					else
						trans.connect();
					trans.sendMessage(msg, msg.getAllRecipients());
			    } finally {				
			    	trans.close();
			    }
			}catch(NoSuchProviderException e){
				MailException me = new MailException("发送邮件时发生异常，未找到SMTP提供者");
				me.initCause(e);
				throw me;				
			}catch(SendFailedException exc){
				MailException me = new MailException("发送邮件失败");
				me.initCause(exc);
				throw me;
			}catch(MessagingException ex){
				MailException me = new MailException("发送邮件时发生异常");
				me.initCause(ex);
				throw me;
			}
//		}catch(UnsupportedEncodingException e){
//		}catch(AddressException e){			
//		}catch(MessagingException e){
//		}catch(IOException e){			
//		}
	}
	
//	/**
//	 * 获取邮箱smtp服务地址
//	 * @return
//	 * 邮箱smtp服务地址
//	 */
//	public String getMailhost() {
//		return mailhost;
//	}
	/**
	 * 设置邮箱服务器地址，默认smtp.139.com
	 * @param mailhost
	 * smtp服务地址
	 */
	public void setMailhost(String mailhost) {
		this.mailhost = mailhost;
	}
//	/**
//	 * 是否设置调试标志
//	 * @return
//	 * true - 显示调试信息
//	 * false - 隐藏调试信息
//	 */
//	public boolean isDebug() {
//		return debug;
//	}
	/**
	 * 设置调试标志
	 * @param debug
	 * true - 显示调试信息
	 * false - [默认]隐藏调试信息
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
//	/**
//	 * 是否需要身份验证
//	 * @return
//	 * true - 需要验证
//	 * false - 免身份验证
//	 */
//	public boolean isAuth() {
//		return auth;
//	}
	/**
	 * 设置身份验证
	 * @param auth
	 * true - 需要验证,需设置用户名(setUser)、密码(setPassword)
	 * false - [默认]免身份验证
	 */
	public void setAuth(boolean auth) {
		this.auth = auth;
	}
	/**
	 * 是否计算活跃
	 * @return
	 */
	protected boolean isActive() {
		return active;
	}
	/**
	 * 设置是否计算活跃
	 * @param active
	 */
	public void setActive(boolean active) {
		mailhost = active?activeSMTP:noActvieSMTP;
		int idx = mailhost.indexOf(':');
		if(idx>=1){
			mailPort = mailhost.substring(idx+1);
			mailhost = mailhost.substring(0, idx);
		}
		this.active = active;
	}
	/**
	 * 获取发件人地址
	 * @return
	 */
	protected String getFrom() {
		if(from == null) return null;
		if(StringUtils.isEmpty(testSendDomain))
			return from;
		else
			return from.replace("@139.com", testSendDomain);
	}
	/**
	 * 设置发件人地址
	 * @param from 邮箱地址
	 */
	public void setFrom(String from) {
		this.from = from;
	}
	/**
//	 * 获取发件人名称
//	 * @return
//	 */
//	public String getFromname() {
//		return fromname;
//	}
	/**
	 * [可选]设置发件人名称
	 * @param fromname
	 */
	public void setFromname(String fromname) {
		this.fromname = fromname;
	}
	/**
	 * 获取收件人邮件地址
	 * @return
	 */
	protected String getTo() {
		if(to == null) return null;
		if(StringUtils.isEmpty(testReceiveDomain))
			return to;
		else
			return to.replace("@139.com", testReceiveDomain);
	}
	/**
	 * 设置收件人邮件地址
	 * @param to
	 * 邮件地址，多个地址以英文逗号隔开，如：yz@139.com,kf@139.com
	 */
	public void setTo(String to) {
		this.to = to;
	}
	/**
	 * 获取抄送邮件地址
	 * @return
	 */
	protected String getCc() {
		if(cc == null) return null;
		if(StringUtils.isEmpty(testReceiveDomain))
			return cc;
		else
			return cc.replace("@139.com", testReceiveDomain);
	}
	/**
	 * 设置抄送邮件地址
	 * @param cc
	 * 邮件地址，多个地址以英文逗号隔开，如：yz@139.com,kf@139.com
	 */
	public void setCc(String cc) {
		this.cc = cc;
	}
	/**
	 * 获取密送邮件地址
	 * @return
	 * 邮件地址
	 */
	protected String getBcc() {
		if(bcc == null) return null;
		if(StringUtils.isEmpty(testReceiveDomain))
			return bcc;
		else
			return bcc.replace("@139.com", testReceiveDomain);
	}
	/**
	 * 设置密送邮件地址
	 * @param bcc
	 * 件地址，多个地址以英文逗号隔开，如：yz@139.com,kf@139.com
	 */
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}
//	/**
//	 * 获取邮件标题
//	 * @return
//	 */
//	public String getSubject() {
//		return subject;
//	}
	/**
	 * 设置邮件标题
	 * @param subject 邮件标题
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
//	/**
//	 * 获取附件集合
//	 * @return
//	 */
//	public Vector<File> getAttachments() {
//		return attachments;
//	}
	/**
	 * 设置附件集合
	 * @param attachments
	 */
	public void setAttachments(Vector<File> attachments) {
		this.attachments = attachments;
	}
	/**
	 * 添加附件
	 * @param attachment 附件本地绝对路径
	 */
	public void addAttachments(String attachment){
		File f = new File(attachment);
		this.attachments.add(f);
	}
	/**
	 * 添加附件
	 * @param attachment 附件文件对象
	 */
	public void addAttachments(File attachment){
		this.attachments.add(attachment);
	}
//	/**
//	 * 获取Wap简版邮件内容（短信回复A、M可查看）
//	 * @return Wap简版邮件内容 
//	 */
//	public String getText() {
//		return text;
//	}
	/**
	 * 设置Wap简版邮件正文（短信回复A、M可查看）
	 * @param text Wap简版邮件正文 
	 */
	public void setText(String text) {
		this.text = text;
	}
//	/**
//	 * 获取Web邮件正文
//	 * @return html字符串
//	 */
//	public String getHtml() {
//		return html;
//	}
	/**
	 * 设置Web邮件正文
	 * @param html html字符串，建议使用GB2312编码
	 */
	public void setHtml(String html) {
		this.html = html;
	}
//	/**
//	 * 获取邮箱用户名
//	 * @return
//	 */
//	public String getUser() {
//		return user;
//	}
	/**
	 * 设置邮箱用户名，需要验证时设置
	 * @param user 用户名
	 */
	public void setUser(String user) {
		this.user = user;
	}
//	/**
//	 * 获取邮箱密码
//	 * @return
//	 */
//	public String getPassword() {
//		return password;
//	}
	/**
	 * 设置邮箱密码，需要验证时设置
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}	
}
