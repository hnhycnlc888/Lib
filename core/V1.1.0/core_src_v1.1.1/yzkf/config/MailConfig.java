package yzkf.config;

import yzkf.exception.ParserConfigException;

/**
 * 邮件配置信息
 * v1.0.0 2011.11.14
 * @author qiulw
 *
 */
public class MailConfig extends yzkf.config.Configuration {

	
	private final String PATH_ACTIVESMTP = "/appmailsendconfig/activesmtpserver";
	private final String PATH_NOACTIVESMTP = "/appmailsendconfig/noactivesmtpserver";
	private final String PATH_FROM="/appmailsendconfig/from";
	private final String PATH_TESTUID="/appmailsendconfig/testuserid";
	private final String PATH_TESTPWD ="/appmailsendconfig/testuserpass";
	
	private String activeSMTPServer;
	private String noActiveSMTPSever;
	private boolean test;
	private String testUID;
	private String testPWD;
	private String testSendDomain;
	private String testReceiveDomain;
	MailConfig(String path) throws ParserConfigException {
		super(path);
		setActiveSMTPServer(getXPathValue(PATH_ACTIVESMTP));
		setNoActiveSMTPSever(getXPathValue(PATH_NOACTIVESMTP));
		setTest(getXPathValue(PATH_FROM).equalsIgnoreCase("fengxingoffice"));
		setTestUID(getXPathValue(PATH_TESTUID));
		setTestPWD(getXPathValue(PATH_TESTPWD));
		setTestSendDomain(getXPathValue("/appmailsendconfig/testsenddomain"));
		setTestReceiveDomain(getXPathValue("/appmailsendconfig/testreceivedomain"));
	}
	public String getActiveSMTPServer() {
		return activeSMTPServer;
	}
	private void setActiveSMTPServer(String activeSMTPServer) {
		this.activeSMTPServer = activeSMTPServer;
	}
	public String getNoActiveSMTPSever() {
		return noActiveSMTPSever;
	}
	private void setNoActiveSMTPSever(String noActiveSMTPSever) {
		this.noActiveSMTPSever = noActiveSMTPSever;
	}
	public boolean isTest() {
		return test;
	}
	private void setTest(boolean test) {
		this.test = test;
	}
	public String getTestUID() {
		return testUID;
	}
	private void setTestUID(String testUID) {
		this.testUID = testUID;
	}
	public String getTestPWD() {
		return testPWD;
	}
	private void setTestPWD(String testPWD) {
		this.testPWD = testPWD;
	}
	public String getTestSendDomain() {
		return testSendDomain;
	}
	public void setTestSendDomain(String testSendDomain) {
		this.testSendDomain = testSendDomain;
	}
	public String getTestReceiveDomain() {
		return testReceiveDomain;
	}
	public void setTestReceiveDomain(String testReceiveDomain) {
		this.testReceiveDomain = testReceiveDomain;
	}
	
}
