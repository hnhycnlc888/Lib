package cn.richinfo.core.mybatis.extend.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import cn.richinfo.core.utils.lang.CollectionHelper;

public class OgnlUtils {
	
	public static boolean isEmpty(Object obj){
		if(obj == null){
			return true;
		} else if(obj instanceof String){
			return StringUtils.isEmpty((String) obj);
		} else if(obj instanceof Collection){
			return CollectionHelper.isEmpty((Collection<?>) obj);
		} else if(obj instanceof Map){
			return CollectionHelper.isEmpty((Map<?, ?>) obj);
		} else if(obj.getClass().isArray()){
			return Array.getLength(obj) == 0;
		} else {
			return false;
		}
	}
	
	public static boolean isNotEmpty(Object obj){
		return !isEmpty(obj);
	}

}
