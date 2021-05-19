package cn.richinfo.core.filter.xss;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XssFilter implements Filter {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void init(FilterConfig config) throws ServletException {
		logger.info("启用XssFilter");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(
				(HttpServletRequest) request);

		HttpServletResponse hresponse = (HttpServletResponse) response;
		HttpServletRequest hRequest = (HttpServletRequest) request;
		// httponly是微软对cookie做的扩展,该值指定 Cookie 是否可通过客户端脚本访问,
		// 解决用户的cookie可能被盗用的问题,减少跨站脚本攻击
		hresponse.setHeader("Set-Cookie", "name=value; HttpOnly");
		//System.out.print(request.getServerName());
		// HTTP 头设置 Referer过滤
	/*	String referer = hRequest.getHeader("Referer");
		if (referer != null && referer.indexOf(request.getServerName()) < 0) {
			hRequest.getRequestDispatcher(hRequest.getRequestURI()).forward(
					hRequest, response);
		}*/

		chain.doFilter(xssRequest, response);
	}

	@Override
	public void destroy() {
	}
}
