package cn.richinfo.core.utils.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public abstract class WebUtils {

	/**
	 * 获取客户端IP
	 * @param request
	 * @return
	 */
	public static String getClientIP(HttpServletRequest request) {    
		String ip = "";
		ip = request.getHeader("X-Forwarded-For");
		if(StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)){
			ip = request.getHeader("Proxy-Client-IP");
		}
		if(StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)){
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if(StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)){
			ip = request.getRemoteAddr();
		}
		if("0:0:0:0:0:0:0:1".equals(ip)){
			ip = "localhost";
		}
		return ip;
    }
	
	/**
	 * 获取指定名称cookie的值
	 * @param request HttpServletRequest对象
	 * @param name cookie的名称
	 * @return cookie的值，不存在则返回空字符
	 */
	public static String getCookieValue(HttpServletRequest request, String name){
		Cookie[] cookies = request.getCookies();
		return getCookieValue(cookies, name);
	}
	/**
	 * 获取指定名称cookie的值
	 * @param cookies cookie数组
	 * @param name cookie的名称
	 * @return cookie的值，不存在则返回空字符
	 */
	public static String getCookieValue(Cookie[] cookies, String name){
		if(cookies == null) return "";
		for(int i=0; i<cookies.length; i++) {
			Cookie cookie = cookies[i];
			if (name.equals(cookie.getName()))
				return cookie.getValue();
		}
		return "";
	}
	
	/**
	 * 判断请求是否是ajax请求
	 * @param request
	 * @return
	 */
	public static boolean isAjaxRequest(HttpServletRequest request){
		String requestType = request.getHeader("X-Requested-With");
		return StringUtils.isNotEmpty(requestType) && "XMLHttpRequest".equals(requestType);
	}
}
