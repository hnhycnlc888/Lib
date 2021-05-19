package cn.richinfo.core.springMVC;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;

import cn.richinfo.core.prop.SysProperty;

public class CustomMvcExceptionResolver implements HandlerExceptionResolver {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	private String errorPage;

	public CustomMvcExceptionResolver() {
		String errorViewPage = SysProperty.getPropertyValue("mvc.errorPage", null);
		if(StringUtils.isNotEmpty(errorViewPage))
			this.errorPage = errorViewPage;
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		String requestType = request.getHeader("X-Requested-With");
		logger.error("执行请求异常，handler=" + handler, ex);
		
		//ajax请求
		if(StringUtils.isNotEmpty(requestType)){
			JSONObject retVal = new JSONObject();
			retVal.put("result", null);
			retVal.put("success", false);
			retVal.put("msg", ex.getMessage());
			retVal.put("exceptionType", ex.getClass().getSimpleName());
			
			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpStatus.SC_OK);
			Writer writer = null;
			try {
				writer = response.getWriter();
				writer.write(retVal.toJSONString());
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(writer);
			}
			return null;
		}
		
		if(StringUtils.isNotEmpty(this.errorPage)){
			ModelAndView modelAndView = new ModelAndView(this.errorPage);
			return modelAndView;
		} else {
			throw new RuntimeException(ex);
		}
	}

}
