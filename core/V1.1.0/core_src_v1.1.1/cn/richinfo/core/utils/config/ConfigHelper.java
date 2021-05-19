package cn.richinfo.core.utils.config;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yzkf.utils.Xml;

/**
 * 获取APPConfig & TextConfig
 * 
 * @author Administrator
 * 
 */
public class ConfigHelper {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	// 系统参数
	private final static String PATH_App = "/AppConfig.xml";
	// 提示文字
	private final static String PATH_Text = "/TextConfig.xml";
	// 接口配置文件
	private final static String PATH_Api = "/ApiConfig.xml";
	private static ConfigHelper instance;

	public static ConfigHelper getInstance() {
		if (instance == null)
			instance = new ConfigHelper();
		return instance;
	}
	
	private ConfigHelper(){}

	/**
	 * 读取AppConfig.xml配置文件
	 * 
	 * @param node
	 *            节点名称
	 * @return
	 * @throws Exception
	 */
	public String readApp(String node) {
		try {
			ClassLoader standardClassloader = Thread.currentThread()
					.getContextClassLoader();
			URL url = null;
			if (standardClassloader != null) {
				url = standardClassloader.getResource(PATH_App);
			}
			if (url == null) {
				url = ConfigHelper.class.getResource(PATH_App);
			}
			Xml xml = new Xml(url);
			return xml.evaluate("AppConfig/" + node);
		} catch (Exception e) {
			logger.error("读取config.xml文件出错", e);
		}
		return "";
	}

	/**
	 * 读取TextConfig.xml配置文件
	 * 
	 * @param node
	 *            节点名称
	 * @return
	 * @throws Exception
	 */
	public String readText(String node) {
		try {
			ClassLoader standardClassloader = Thread.currentThread()
					.getContextClassLoader();
			URL url = null;
			if (standardClassloader != null) {
				url = standardClassloader.getResource(PATH_Text);
			}
			if (url == null) {
				url = ConfigHelper.class.getResource(PATH_Text);
			}
			Xml xml = new Xml(url);
			return xml.evaluate("Message/" + node);
		} catch (Exception e) {
			logger.error("读取config.xml文件出错", e);
		}
		return "";
	}
	
	public String readApi(String node) {
		try {
			ClassLoader standardClassloader = Thread.currentThread()
					.getContextClassLoader();
			URL url = null;
			if (standardClassloader != null) {
				url = standardClassloader.getResource(PATH_Api);
			}
			if (url == null) {
				url = ConfigHelper.class.getResource(PATH_Api);
			}
			Xml xml = new Xml(url);
			return xml.evaluate("ApiConfig/" + node);
		} catch (Exception e) {
			logger.error("读取config.xml文件出错", e);
		}
		return "";
	}
}
