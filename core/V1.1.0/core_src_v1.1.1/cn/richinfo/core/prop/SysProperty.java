package cn.richinfo.core.prop;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import cn.richinfo.core.utils.lang.CollectionHelper;

public class SysProperty extends PropertyPlaceholderConfigurer {
	
	private static final Properties prop = new Properties();
	
	public static String getPropertyValue(String key, String defaultValue){
		return prop.getProperty(key, defaultValue);
	}
	
	@Override
	protected Properties mergeProperties() throws IOException {
		Properties p = super.mergeProperties();
		if(CollectionHelper.isNotEmpty(p)){
			prop.putAll(p);
		}
		return p;
	}

}
