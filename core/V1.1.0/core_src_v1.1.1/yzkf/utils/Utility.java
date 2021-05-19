package yzkf.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import yzkf.exception.AppException;

/**
 * 公共静态方法类
 * v1.0.0 2011.11.14
 * @author qiulw
 *
 */
public class Utility {
	public Utility(){
		
	}	
	/**
	 * 创建泛型实例
	 * @param c
	 * @return
	 * @throws AppException
	 */
	public static <T> T newInstance(Class<T> c) throws AppException {
        try {
            return c.newInstance();

        } catch (InstantiationException e) {
            throw new AppException("Cannot create " + c.getName() + ": " + e.getMessage());

        } catch (IllegalAccessException e) {
            throw new AppException("Cannot create " + c.getName() + ": " + e.getMessage());
        }
    }
	/**
	 * 删除指定名称的cookie
	 * @param response
	 * @param name
	 */
	public static void deleteCookie(HttpServletResponse response,String name){
		Cookie cookie = new Cookie(name, "");
		cookie.setMaxAge(0);
		//cookie.setPath("/");
		//cookie.setDomain("");
		response.addCookie(cookie);
	}
	/**
	 * 格式化时间
	 * @param date 时间
	 * @param format 格式：yyyy-MM-dd
	 * @return
	 */
	public static String formatDate(Date date,String format){
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		return sdf.format(date);
	}
	/**
	 * 获取当期日期，时分秒都为0
	 * @return  返回当前日期
	 */
	public static Date getDateWithoutTime(){
		return getDateWithoutTime(-1, 0);
	}
	/**
	 * 获取时间的日期部分，时分秒都为0
	 * @param field 需要调整的日期字段，如：{@link Calendar#DAY_OF_YEAR}
	 * @param amount 要设置为给定的日历字段的值。
	 * @return 返回时分秒为0的日期对象
	 */
	public static Date getDateWithoutTime(int field,int amount){
		Calendar now=Calendar.getInstance();
		if(field >= 0)
			now.add(field, amount);
		now.set(Calendar.HOUR_OF_DAY,0);
		now.set(Calendar.MINUTE,0); 
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		return now.getTime();
	}
	/**
	 * 获取当前时间
	 * @return
	 */
	public static Date getNow(){
		return getNow(-1, 0);
	}
	/**
	 * 获取相对当前时间指定时间字段偏移量的时间
	 * @param field 需要调整的时间字段，如：{@link Calendar#MINUTE}
	 * @param amount 要设置为给定的时间字段的值。
	 * @return
	 */
	public static Date getNow(int field,int amount){
		Calendar now=Calendar.getInstance();
		if(field >= 0)
			now.add(field, amount);
		return now.getTime();
	}
	/**
	 * 获取时间戳
	 * @param date 时间戳开始日期，格式：2010-01-01
	 * @param timeexp 过期时间，单位秒
	 * @return
	 * @throws ParseException 
	 */
	public static long getTimeSpan(String date,int timeexp) throws ParseException
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Date d=sdf.parse(date);
		Calendar Time=Calendar.getInstance();
		Calendar New=Calendar.getInstance();
		Time.setTime(d);
		return (New.getTimeInMillis()-Time.getTimeInMillis())/1000+timeexp;
	}
	/**
	 * 获取时间戳
	 * @param timeexp 过期时间，单位秒
	 * @return
	 */
	public static long getTimeSpan(int timeexp){
		try {
			return getTimeSpan("2000-01-01",timeexp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	/**
	 * 将文件转换为Base64编码字符串
	 * @param pathname 文件路径
	 * @return
	 * @throws IOException 文件不存在或读取异常
	 */
	public static String encodeFileToBase64(String pathname) throws IOException{
		File file= new File(pathname);
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[(int)file.length()];
		fis.read(buffer);
		fis.close();
		return new BASE64Encoder().encode(buffer);
	}
	/**
	 * 将Base64字符串解码为文件并保存到指定路径
	 * @param base64String Base64字符串
	 * @param pathname 文件保存路径
	 * @throws IOException
	 */
	public static void decoderBase64ToFile(String base64String,String pathname) throws IOException{
		byte[] buffer = new BASE64Decoder().decodeBuffer(base64String);
		FileOutputStream fos = new FileOutputStream(pathname);
		fos.write(buffer);
		fos.close();
	}
	/**
	 * 获取当前系统时间转换为数据库时间格式
	 * @return 返回当前时间的数据库时间格式 {@link java.sql.TimeStamp}
	 */
	public static java.sql.Timestamp getDBTime(){
		return getDBTime(new Date());
	}
	/**
	 * 获取当前系统时间转换为数据库时间格式
	 * @param date java时间对象
	 * @return 返回当前时间的数据库时间格式 {@link java.sql.TimeStamp}
	 */
	public static java.sql.Timestamp getDBTime(Date date){
		return getDBTime(date.getTime());
	}
	/**
	 * 获取当前系统时间转换为数据库时间格式
	 * @param date  the number of milliseconds since January 1, 1970, 00:00:00 GMT
	 * @return 返回当前时间的数据库时间格式 {@link java.sql.TimeStamp}
	 */
	public static java.sql.Timestamp getDBTime(Long date){
		return new java.sql.Timestamp(date);
	}
	/**
	 * 获取字符串从位置0开始到指定长度的部分，并添加后缀
	 * @param str 源字符串
	 * @param len 长度(byte长度)
	 * @return
	 */
	public static String cutString(String str,int len){
		return cutString(str, len, "");
	}
	/**
	 * 获取字符串从位置0开始到指定长度的部分，并添加后缀
	 * @param str 源字符串
	 * @param len 长度(byte长度)
	 * @param suffix 后缀(如：...)
	 * @return
	 */
	public static String cutString(String str,int len, String suffix)
	{
	   int counterOfDoubleByte = 0;
	   byte[] b = str.getBytes();
	   if(b.length <= len)
	     return str;
	   for(int i = 0; i < len; i++)
	   {
	     if(b[i] < 0)
	       counterOfDoubleByte++;
	   }
	   if(counterOfDoubleByte % 2 == 0)
	     return new String(b, 0, len) + suffix;
	   else
	     return new String(b, 0, len - 1) + suffix;
	} 
	/**
	 * 获取字符串的字节长度
	 * @param str
	 * @return
	 */
	public static int getStrByteLen(String str){
		return str.getBytes().length;
	}
	/**
	 * 将字符串数组转换为使用指定分隔符分隔的字符串
	 * @param arr 字符串数组
	 * @param split 分隔符
	 * @return
	 */
	public static String getArrayStr(String[] arr,String split){
		if(arr == null) return null;
		int len = arr.length;
		if(len == 0) return "";
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<len;i++){
			sb.append(arr[i]);
			sb.append(split);
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
}
