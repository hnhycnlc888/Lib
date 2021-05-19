package yzkf.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * 显示安全类型转换
 * v1.0.0 2012.06.20
 * @author qiulw
 *
 */
public class TryParse {
	/**
	 * 将String强制转换为Long，转换失败返回0，不抛出异常
	 * @param source 要转换的String
	 * @return
	 */
	public static Long toLong(String source){
		return toLong(source,(long) 0);
	}
	/**
	 * 将String强制转换为Long，转换失败返回替代值，不抛出异常
	 * @param source 要转换的String
	 * @param instead 转换失败时返回的值
	 * @return
	 */
	public static Long toLong(String source,Long instead){
		try{
			if(StringUtils.isEmpty(source))
				return instead;
			return Long.parseLong(source);
		}
		catch(Exception e){
			return instead;
		}
	}
	/**
	 * 将String强制转换为Int，转换失败返回0，不抛出异常
	 * @param source 要转换的String
	 * @return
	 */
	public static int toInt(String source){
		return toInt(source,0);
	}
	/**
	 * 将String强制转换为Int，转换失败返回替代值，不抛出异常
	 * @param source 要转换的String
	 * @param instead 转换失败时返回的值
	 * @return
	 */
	public static int toInt(String source,int instead){
		try{
			if(StringUtils.isEmpty(source))
				return instead;
			return Integer.parseInt(source);
		}
		catch(Exception e){
			return instead;
		}
	}
	/**
	 * 将String强制转换为Boolean，转换失败返回false，不抛出异常
	 * @param source 要转换的String
	 * @return
	 */
	public static boolean toBoolean(String source){
		return toBoolean(source,false);
	}
	/**
	 * 将String强制转换为Boolean，转换失败返回替代值，不抛出异常
	 * @param source 要转换的String
	 * @param instead 转换失败时返回的值
	 * @return
	 */
	public static boolean toBoolean(String source,boolean instead){
		try{
			if(StringUtils.isEmpty(source))
				return instead;
			if(source.toLowerCase() == "false" || source=="0")
				return false;
			return true;
		}
		catch(Exception e){
			return instead;
		}
	}
	/**
	 * 将String按格式yyyy-MM-dd强制转换为Date，转换失败返回null，不抛出异常
	 * @param source 要转换德String
	 * @return
	 */
	public static Date toDate(String source){
		return toDate(source,"yyyy-MM-dd",null);
	}
	/**
	 * 将String强制转换为Date，转换失败返回替代值，不抛出异常
	 * @param source 要转换德String
	 * @param format 转换格式：yyyy-MM-dd
	 * @param instead 转换失败时返回的值
	 * @return
	 */
	public static Date toDate(String source,String format,Date instead){		
		try{
			if(StringUtils.isEmpty(source))
				return instead;
			SimpleDateFormat sdf=new SimpleDateFormat(format);
			return sdf.parse(source);			
		}
		catch(Exception e){
			return instead;
		}
	}
}
