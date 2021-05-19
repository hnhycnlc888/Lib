package yzkf.api.result;

import yzkf.config.ConfigFactory;
import yzkf.config.EnumConfig;
import yzkf.exception.ParserConfigException;
/**
 * 枚举描述类
 * <p>查询XML配置文件中对应枚举的描述内容</p>
 * @author qiulw
 * @version V4.0.0 2011.11.28
 */
public class Describe {
	private static EnumConfig config;
	/**
	 * 获取 默认的EnumConfig对象
	 * @return
	 * @throws ParserConfigException 
	 */
	private static EnumConfig getConfig() throws ParserConfigException{
		if(config == null){
			ConfigFactory  factory = ConfigFactory.getInstance();
			config = factory.newEnumConfig();
		}
		return config;
	}
	/**
	 * 查询指定配置对象中对应枚举的描述内容
	 * @param config 配置对象
	 * @param className 枚举类名
	 * @param enumName 枚举对象名
	 * @return
	 */
	private static String query(EnumConfig config,String className,String enumName){
		try {
			return config.getEnumDescr(className, enumName);
		} catch (ParserConfigException e) {
			return null;
		}
	}
	/**
	 * 查询指定配置对象中对应枚举的描述内容
	 * @param config 配置对象
	 * @param em 枚举对象
	 * @return
	 */
	public static String query(EnumConfig config,Enum<?> em){
		if(config == null)
			return query(em);
		return query(config,em.getClass().getSimpleName(),em.name());
	}
	/**
	 * 查询默认配置对象中对应枚举的描述内容
	 * @param em 枚举对象
	 * @return
	 */
	public static String query(Enum<?> em){
		try {
			return query(getConfig(),em.getClass().getSimpleName(),em.name());
		} catch (ParserConfigException e) {
			return em.toString();		
			//return null;
		}
	}	
}
