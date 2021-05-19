package cn.richinfo.core.filter.xss;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 
 * @author liyuan
 * 
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
	HttpServletRequest orgRequest = null;

	public XssHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
		orgRequest = request;
	}

	/**
	 * 覆盖getParameter方法，将参数名和参数值都做xss过滤。<br/>
	 * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取<br/>
	 * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖
	 */
	@Override
	public String getParameter(String name) {

		String value = super.getParameter(xssEncode(name));
		if (value != null) {
			value = xssEncode(value);
		}
		return value;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map getParameterMap() {
		HashMap<String, String[]> paramMap = (HashMap<String, String[]>) super
				.getParameterMap();
		paramMap = (HashMap<String, String[]>) paramMap.clone();

		for (Iterator iterator = paramMap.entrySet().iterator(); iterator
				.hasNext();) {
			Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) iterator
					.next();
			String[] values = entry.getValue();
			for (int i = 0; i < values.length; i++) {
				if (values[i] instanceof String) {
					values[i] = xssEncode(values[i]);
				}
			}
			entry.setValue(values);
		}
		return paramMap;
	}

	/**
	 * 覆盖getHeader方法，将参数名和参数值都做xss过滤。<br/>
	 * 如果需要获得原始的值，则通过super.getHeaders(name)来获取<br/>
	 * getHeaderNames 也可能需要覆盖
	 */
	@Override
	public String getHeader(String name) {

		String value = super.getHeader(xssEncode(name));
		if (value != null) {
			value = xssEncode(value);
		}
		return value;
	}

	/**
	 * 将容易引起xss漏洞的半角字符直接替换成全角字符
	 * 
	 * @param s
	 * @return
	 */
	private static String xssEncode(String s) {
		if (s == null || s.isEmpty()) {
			return s;
		}

		s = s.replaceAll("&", "&amp;");
		s = s.replaceAll("\"", "&quot;");
		s = s.replaceAll("<", "&lt;");
		s = s.replaceAll(">", "&gt;");
		s = s.replaceAll("%3C", "&lt;");
		s = s.replaceAll("%3E", "&gt;");

		s = s.replaceAll("%27", "");
		s = s.replaceAll("%22", "");
		s = s.replaceAll("%3E", "");
		s = s.replaceAll("%3C", "");
		s = s.replaceAll("%3D", "");
		s = s.replaceAll("%2F", "");
		s = regexReplace("^>", "", s);

		s = regexReplace("<([^>]*?)(?=<|$)", "&lt;$1", s);
		s = regexReplace("(^|>)([^<]*?)(?=>)", "$1$2&gt;<", s);

		s = s.replaceAll("\\|", "");
		s = s.replaceAll("alert", "");
		s = s.replaceAll("STYLE=", "");
		s = s.replaceAll("<iframe", "");
		s = s.replaceAll("<script", "");
		s = s.replaceAll("<IMG", "");
		return s;
	}

	private static String regexReplace(String regex_pattern,
			String replacement, String s) {
		Pattern p = Pattern.compile(regex_pattern);
		Matcher m = p.matcher(s);
		return m.replaceAll(replacement);
	}

	/**
	 * 获取最原始的request
	 * 
	 * @return
	 */
	public HttpServletRequest getOrgRequest() {
		return orgRequest;
	}

	/**
	 * 获取最原始的request的静态方法
	 * 
	 * @return
	 */
	public static HttpServletRequest getOrgRequest(HttpServletRequest req) {
		if (req instanceof XssHttpServletRequestWrapper) {
			return ((XssHttpServletRequestWrapper) req).getOrgRequest();
		}

		return req;
	}
}