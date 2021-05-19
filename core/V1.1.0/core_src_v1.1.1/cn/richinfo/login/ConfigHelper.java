package cn.richinfo.login;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yzkf.utils.Xml;

/**
 * 获取LoginConfig
 * 
 * @author Administrator
 * 
 */
public class ConfigHelper {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	// 登录配置
	private final static String PATH_Login = "/LoginConfig.xml";
	private static ConfigHelper instance;

	public static ConfigHelper getInstance() {
		if (instance == null)
			instance = new ConfigHelper();
		return instance;
	}
	
	private ConfigHelper(){}

	/**
	 * 读取LoginConfig.xml配置文件
	 * 
	 * @param node
	 *            节点名称
	 * @return
	 * @throws Exception
	 */
	public String readLogin(String node) {
		try {
			ClassLoader standardClassloader = Thread.currentThread()
					.getContextClassLoader();
			URL url = null;
			if (standardClassloader != null) {
				url = standardClassloader.getResource(PATH_Login);
			}
			if (url == null) {
				url = ConfigHelper.class.getResource(PATH_Login);
			}
			Xml xml = new Xml(url);
			return xml.evaluate("LoginConfig/" + node);
		} catch (Exception e) {
			logger.error("读取config.xml文件出错", e);
		}
		return "";
	}

	
}
