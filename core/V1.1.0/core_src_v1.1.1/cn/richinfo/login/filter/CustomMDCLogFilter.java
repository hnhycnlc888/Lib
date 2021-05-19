package cn.richinfo.login.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.richinfo.core.spring.SpringContext;
import cn.richinfo.core.utils.web.WebUtils;
import cn.richinfo.login.impl.Login;
import cn.richinfo.login.pojo.UserInfo;

public class CustomMDCLogFilter extends OncePerRequestFilter {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	private Login login;
	
	@Override
	protected void initFilterBean() throws ServletException {
		logger.info("启用用户MDC日志信息Filter");
		super.initFilterBean();
	}
	
	@Override
	public void destroy() {
		logger.info("销毁用户MDC日志信息Filter");
		super.destroy();
		this.login = null;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		try {
			this.beforeRequest(request, response);
			chain.doFilter(request, response);
		} finally {
			this.afterRequest(request);
		}
	}

	private void beforeRequest(HttpServletRequest request,HttpServletResponse response) {
		String ip = WebUtils.getClientIP(request);
		MDC.put("userInfo", "clientIP:" + ip);
		MDC.put("clientIP", ip);
		
		UserInfo userInfo = getLogin().getUserInfo(request, response);
		if(userInfo != null){
			MDC.put("userPassportId", userInfo.getPassPortId() + "");
			MDC.put("userInfo", "clientIP:" + ip + ", userPassportId:" + userInfo.getPassPortId());
		}
	}

	private void afterRequest(HttpServletRequest request) {
		org.apache.log4j.MDC.clear();
	}
	
	private Login getLogin(){
		if(this.login == null){
			this.login = SpringContext.getBean("Login", Login.class);
		}
		return this.login;
	}

}
