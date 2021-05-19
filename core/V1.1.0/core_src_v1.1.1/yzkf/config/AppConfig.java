package yzkf.config;

import yzkf.exception.ParserConfigException;
/**
 * 应用程序配置类
 * <p>包含 文字过滤、日志路径、验证码路径 配置信息</p>
 * @author qiulw
 * @version V1.0.0 2011.11.28
 *
 */
public class AppConfig extends Configuration {
	private String filterWordsUrl;
	//private String logAppPath;
	private String logActionPath;
	private String logClickUrl;
	private String verifyDown;
	private String verifyPost;
	/**
	 * 
	 * @param path
	 * @throws ParserConfigException
	 */
	AppConfig(String path) throws ParserConfigException{
		super(path);
		filterWordsUrl = getXPathValue("/app/filterwords");
		//logAppPath = getXPathValue("/app/log/applogpath");
		logActionPath = getXPathValue("/app/log/actionlogpath");
		logClickUrl = getXPathValue("/app/log/clicklogurl");
		verifyDown = getXPathValue("/app/verify/down");
		verifyPost = getXPathValue("/app/verify/post");
	}
	/**
	 * 获取提供文字过滤串的URL
	 * @return
	 */
	public String getFilterWordsUrl() {
		return filterWordsUrl;		
	}
//	/**
//	 * 获取系统日志文件夹路径
//	 * @return
//	 */
//	public String getLogAppPath() {
//		return logAppPath;
//	}
	/**
	 * 获取行为日志文件夹路径
	 * @return
	 */
	public String getLogActionPath() {
		return logActionPath;
	}
	/**
	 * 获取系统日志文件夹路径
	 * @return
	 */
	public String getLogClickUrl() {
		return logClickUrl;
	}
	/**
	 * 获取显示验证码的URL
	 * @return
	 */
	public String getVerifyDown() {
		return verifyDown;
	}
	/**
	 * 获取校验验证码的URL
	 * @return
	 */
	public String getVerifyPost() {
		return verifyPost;
	}
	
}
