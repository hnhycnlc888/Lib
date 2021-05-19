package yzkf.config;

import yzkf.exception.ParserConfigException;
/**
 * 枚举描述配置类
 * <p>提供yzkf.enums包中枚举类对象的说明</p>
 * @author qiulw
 * @version V4.0.0 2011.11.28
 */
public class EnumConfig extends Configuration {
	/**
	 * 
	 * @param path
	 * @throws ParserConfigException
	 */
	EnumConfig(String path) throws ParserConfigException {
		super(path);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 获取返回值枚举对应配置文件的文字说明
	 * @param enumClass 枚举类名
	 * @param enumName 枚举实例名
	 * @return
	 * @throws ParserConfigException
	 */
	public String getEnumDescr(String enumClass,String enumName) throws ParserConfigException{
		return getXPathValue("enums/"+enumClass+"/"+enumName);
	}
}
