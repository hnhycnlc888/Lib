package yzkf.api.result;

import yzkf.config.EnumConfig;
/**
 * 枚举接口类
 * <br/>定义了获取枚举描述的方法
 * @author qiulw
 * @version V4.0.0
 */
public interface Result {
	/**
	 * 枚举值是否返回OK
	 * @return
	 */
	boolean isOK();
	/**
	 * 获取默认配置文件中对应枚举的描述
	 * @return
	 */
	String getDescr();
	/**
	 * 获取指定配置对象中对应枚举的描述
	 * @param config
	 * @return
	 */
	String getDescr(EnumConfig config);
	/**
	 * 获取返回值
	 * @return
	 */
	Object getValue();
	/**
	 * 获取返回值
	 * @param type 返回值数据类型
	 * @return
	 */
	<T> T getValue(Class<T> type);
	/**
	 * 设置返回值
	 * @param value
	 * @return
	 */
	Result setValue(Object value);
}
