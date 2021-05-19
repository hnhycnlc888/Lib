package yzkf.config;

import yzkf.exception.ParserConfigException;
/**
 * 正则表达式配置文件
 * v1.0.0 2011.11.22
 * @author qiulw
 *
 */
public class RegexConfig extends Configuration {

	//private static RegexConfig instance;
	
	private String mobile;
	private String mobile86;
	private String mail139alias;
	private String mobile139alias;
	private String mailpassword;
	private String mailaddress;
	private String chinaphone;
	private String chinaidcard;
	private String zipcode;
	private String ipaddress;
	private String url;
	private String filename;
	private String path;
	private String spnumber;
	private String smsBody;
	private String mmsSubject;
	
	/**
	 * 读取指定路径xml文件，创建正则表达式配置类实例
	 * @param path
	 * @throws ParserConfigException
	 */
	RegexConfig(String xmlPath) throws ParserConfigException {
		super(xmlPath);
		mobile = getXPathValue("/regexconfig/mobile");
		mobile86 = getXPathValue("/regexconfig/mobile86");
		mail139alias = getXPathValue("/regexconfig/mail139alias");
		mobile139alias = getXPathValue("/regexconfig/mobile139alias");
		mailpassword = getXPathValue("/regexconfig/mailpassword");
		mailaddress = getXPathValue("/regexconfig/mailaddress");
		chinaphone = getXPathValue("/regexconfig/chinaphone");
		chinaidcard = getXPathValue("/regexconfig/chinaidcard");
		zipcode = getXPathValue("/regexconfig/zipcode");
		ipaddress = getXPathValue("/regexconfig/ipaddress");
		url = getXPathValue("/regexconfig/url");
		filename = getXPathValue("/regexconfig/filename");
		path = getXPathValue("/regexconfig/path");
		spnumber = getXPathValue("/regexconfig/spnumber");
		smsBody = getXPathValue("/regexconfig/smsbody");
		mmsSubject = getXPathValue("/regexconfig/mmssubject");
	}
//	public static RegexConfig getInstance(String path) throws ParserConfigException{
//		if(instance == null)
//			instance = new RegexConfig(path);
//		return instance;
//	}
	public String search(String path) throws ParserConfigException{
		return getXPathValue("/regexconfig/"+path);
	}
	public String getMobile() {
		return mobile;
	}
	public String getMobile86(){
		return mobile86;
	}
	public String getMail139alias() {
		return mail139alias;
	}
	public String getMobile139alias() {
		return mobile139alias;
	}
	public String getMailpassword() {
		return mailpassword;
	}
	public String getMailaddress() {
		return mailaddress;
	}
	public String getChinaphone() {
		return chinaphone;
	}
	public String getChinaidcard() {
		return chinaidcard;
	}
	public String getZipcode() {
		return zipcode;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public String getUrl() {
		return url;
	}
	public String getFilename() {
		return filename;
	}
	public String getPath() {
		return path;
	}
	public String getSpNumber(){
		return spnumber;
	}
	public String getSmsBody(){
		return smsBody;
	}
	public String getMmsSubject(){
		return mmsSubject;
	}
}
