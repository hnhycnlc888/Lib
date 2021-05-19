package yzkf.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.richinfo.core.utils.MD5;
import yzkf.exception.ParserConfigException;

/**
 * 配置文件工场类，负责创建各种配置对象
 * 
 * @author qiulw
 * @version V4.0.0 2011.11.28
 */
public class ConfigFactory extends Configuration {

	private static final Logger LOG = LoggerFactory.getLogger(ConfigFactory.class.getName());
	private final static String PATH_DEFAULT = "/yzkf.xml";
	private final static String PATH_FAILSAFE = "/yzkf-failsafe.xml";
	
	private static ConfigFactory instance;
	
	private ConcurrentHashMap<String, Object> configMap = new ConcurrentHashMap<String, Object>();
	private String pathDatabaseConfig;
	private String pathApiConfig;
	private String pathAppConfig;
	private String pathMailConfig;	
	private String pathRegexConfig;
	private String pathMemcachedConfig;
	private String pathEnumConfig;
	private String pathLogConfig;
	private String logFilePath;
	
	/**
	 * 获取默认配置文件工厂
	 * @return
	 */
	public static ConfigFactory getInstance() {
		if(instance == null)
			instance = new ConfigFactory();
		return instance;
	}
	/**
	 * 创建默认配置文件工厂类实例
	 * @throws ParserConfigException
	 */
	private ConfigFactory() throws ParserConfigException{
		ClassLoader standardClassloader = Thread.currentThread().getContextClassLoader();
        URL url = null;
        if (standardClassloader != null) {
            url = standardClassloader.getResource(PATH_DEFAULT);
            //LOG.debug("Thread.currentThread().getContextClassLoader(): {}",url);
        }
        if (url == null) {
            url = ConfigFactory.class.getResource(PATH_DEFAULT);
            //LOG.debug("ConfigFactory.class.getResource(PATH_DEFAULT): {}",url);
        }
        if (url != null) {
            LOG.debug("XML配置文件路径: {}",url);
            super.setXml(url);
        } else {
            //url = ConfigFactory.class.getResource(PATH_FAILSAFE);
        	LOG.debug("yzkf.xml配置文件未找到，使用安全配置文件路径：{} ", ConfigFactory.class.getResource(PATH_FAILSAFE));
        	InputStream is = ConfigFactory.class.getResourceAsStream(PATH_FAILSAFE);
        	super.setXml(is);            
        }		
		initProps();
	}
	/**
	 * 创建配置文件工厂类
	 * @param path 主配置文件路径,不传入参数则使用默认路径
	 * @throws ParserConfigException
	 */
	public ConfigFactory(String path) throws ParserConfigException{
		super(path);
		initProps();
	}
	/**
	 * 初始化子配置节点路径
	 * @throws ParserConfigException
	 */
	private void initProps() throws ParserConfigException{
		pathDatabaseConfig = getXPathValue("yzkf/dbconfig/path");
		pathAppConfig = getXPathValue("yzkf/appconfig/path");
		pathApiConfig = getXPathValue("yzkf/apiconfig/path");
		pathMailConfig = getXPathValue("yzkf/mailconfig/path");
		pathRegexConfig = getXPathValue("yzkf/regexconfig/path");
		pathMemcachedConfig = getXPathValue("yzkf/memcachedconfig/path");
		pathEnumConfig = getXPathValue("yzkf/enumconfig/path");
		pathLogConfig = getXPathValue("yzkf/log4jconfig/path");
		logFilePath = getXPathValue("yzkf/log4jFile/path");
	}
	/**
	 * 获取log4j配置文件路径
	 * @return
	 */
	public String getLog4jConfigPath(){
		return pathLogConfig;
	}
	/**
	 * 获取log4j日志组件的配置属性
	 * @return
	 * @throws ParserConfigException
	 */
	protected Properties getLog4jConfig() throws ParserConfigException{
		return getLog4jConfig(pathLogConfig);
	}
	/**
	 * 获取log4j日志组件的配置属性
	 * @param path properties配置文件路径，不提供参数则使用主配置文件中的路径读取配置文件
	 * @return 
	 * @throws ParserConfigException
	 */
	protected Properties getLog4jConfig(String path) throws ParserConfigException{
		if(StringUtils.isEmpty(path))
			return null;
		Properties props = new Properties();
	    FileInputStream istream = null;
	    try {
	      istream = new FileInputStream(path);
	      props.load(istream);
	      istream.close();
	    }
	    catch (Exception e) {
	      if (e instanceof InterruptedIOException || e instanceof InterruptedException) {
	          Thread.currentThread().interrupt();
	      }
	      ParserConfigException pce = new ParserConfigException("读取Log4j配置文件发生错误");
		  pce.initCause(e);
		  throw pce;
	    } finally {
	        if(istream != null) {
	            try {
	                istream.close();
	            } catch(InterruptedIOException ignore) {
	                Thread.currentThread().interrupt();
	            } catch(Throwable ignore) {
	            }
	        }
	    }	
	    return props;
	}
	/**
	 * 创建数据库配置对象，使用主配置文件中的路径读取配置文件
	 * @return
	 * @throws ParserConfigException
	 */
//	public DBConfig newDatabaseConfig() throws ParserConfigException{
//		return newDatabaseConfig(pathDatabaseConfig);
//	}
	/**
	 * 创建数据库配置对象
	 * @param path XML配置文件路径
	 * @return
	 * @throws ParserConfigException
	 */
//	public DBConfig newDatabaseConfig(String path) throws ParserConfigException{
//		return newConfiguration(path,DBConfig.class);
//		//return new DBConfig(path);
//	}
	/**
	 * 创建应用接口配置文件对象，使用主配置文件中的路径读取配置文件
	 * @return
	 * @throws ParserConfigException
	 */
	public ApiConfig newApiConfig() throws ParserConfigException{
		return newApiConfig(pathApiConfig);
	}
	/**
	 * 创建应用接口配置文件对象
	 * @param path XML配置文件路径，不提供参数则使用主配置文件中的路径读取配置文件
	 * @return
	 * @throws ParserConfigException
	 */
	public ApiConfig newApiConfig(String path) throws ParserConfigException{
		return newConfiguration(path,ApiConfig.class);
		//return new ApiConfig(path);
	}
	/**
	 * 创建应用程序配置文件对象，使用主配置文件中的路径读取配置文件
	 * @return
	 * @throws ParserConfigException
	 */
	public AppConfig newAppConfig() throws ParserConfigException{
		return newAppConfig(pathAppConfig);
	}
	/**
	 * 创建应用程序配置文件对象
	 * @param path XML配置文件路径，不提供参数则使用主配置文件中的路径读取配置文件
	 * @return
	 * @throws ParserConfigException
	 */
	public AppConfig newAppConfig(String path) throws ParserConfigException{
		return newConfiguration(path,AppConfig.class);
		//return new AppConfig(path);
	}
	/**
	 * 创建返回值枚举对应配置文件对象，使用主配置文件中的路径读取配置文件
	 * @return
	 * @throws ParserConfigException
	 */
	public EnumConfig newEnumConfig() throws ParserConfigException{
		return newEnumConfig(pathEnumConfig);
	}
	/**
	 * 创建返回值枚举对应配置文件对象
	 * @param path XML配置文件路径，不提供参数则使用主配置文件中的路径读取配置文件
	 * @return
	 * @throws ParserConfigException
	 */
	public EnumConfig newEnumConfig(String path) throws ParserConfigException{
		return newConfiguration(path,EnumConfig.class);
		//return new EnumConfig(path);
	}
	/**
	 * 创建正则表达式配置类对象，使用主配置文件中的路径读取配置文件
	 * @return
	 * @throws ParserConfigException
	 */
	public RegexConfig newRegexConfig() throws ParserConfigException{
		return newRegexConfig(pathRegexConfig);
	}
	/**
	 * 创建正则表达式配置类对象
	 * @param path XML配置文件路径，不提供参数则使用主配置文件中的路径读取配置文件
	 * @return
	 * @throws ParserConfigException
	 */
	public RegexConfig newRegexConfig(String path) throws ParserConfigException{
		return newConfiguration(path,RegexConfig.class);
		//return new RegexConfig(path);
	}
	/**
	 * 创建邮件配置类对象，使用主配置文件中的路径读取配置文件
	 * @return
	 * @throws ParserConfigException
	 */
	public MailConfig newMailConfig() throws ParserConfigException{
		return newMailConfig(pathMailConfig);
	}
	/**
	 * 创建邮件配置类对象
	 * @param path 配置文件路径，不提供参数则使用主配置文件中的路径读取配置文件
	 * @return
	 * @throws ParserConfigException
	 */
	public MailConfig newMailConfig(String path) throws ParserConfigException{
		return newConfiguration(path,MailConfig.class);
		//return new MailConfig(path);
	}
	/**
	 * 创建Memcached配置类对象，使用主配置文件中的路径读取配置文件
	 * @return
	 * @throws ParserConfigException
	 */
	public MemcachedConfig newMemcachedConfig() throws ParserConfigException{
		return newMemcachedConfig(pathMemcachedConfig);
	}
	/**
	 * 创建Memcached配置类对象
	 * @param path 配置文件路径，不提供参数则使用主配置文件中的路径读取配置文件
	 * @return
	 * @throws ParserConfigException
	 */
	public MemcachedConfig newMemcachedConfig(String path) throws ParserConfigException{
		return newConfiguration(path,MemcachedConfig.class);
		//return new MemcachedConfig(path);
	}
	@SuppressWarnings("unchecked")
	protected <T> T newConfiguration(String path,Class<T> type){
		String mapKey = MD5.encode(path);
		if(configMap.containsKey(mapKey))
			return (T) configMap.get(mapKey);
		try {
			T cfg = type.getDeclaredConstructor(String.class).newInstance(path);
			configMap.putIfAbsent(mapKey, cfg);
		} catch (Exception e) {
			LOG.debug("Cannot create " + type.getName() + ": " + e.getMessage(), e);
		}
		return (T) configMap.get(mapKey);
	}
	
	public String getDbConfigPath(){
		return pathDatabaseConfig;
	}
	
	public String getLogFilePath(){
		if(logFilePath != null){
			logFilePath = StringUtils.replace(logFilePath, "${projectCode}", ProjectConfig.getInstance().getCode());
		}
		return logFilePath;
	}
}