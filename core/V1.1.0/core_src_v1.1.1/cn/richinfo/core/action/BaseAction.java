package cn.richinfo.core.action;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public abstract class BaseAction {
	@InitBinder
	protected static void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		CustomDateEditor dateEditor = new CustomDateEditor(format, true);
		binder.registerCustomEditor(Date.class, dateEditor);
	}

	protected static SerializerFeature[] features = {SerializerFeature.WriteMapNullValue,SerializerFeature.WriteNullNumberAsZero,SerializerFeature.WriteNullStringAsEmpty,SerializerFeature.WriteDateUseDateFormat};
	public static void printError(HttpServletResponse response, String message){
		try {
			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write(message);
		} catch (IOException ie) {
			throw new RuntimeException(ie.getMessage());
		}
	}
	
	public static void printJson(HttpServletResponse response, boolean success, String msg, JSON result) {
		JSONObject retVal = new JSONObject();
		retVal.put("result", result);
		retVal.put("success", success);
		retVal.put("msg", msg);
		printJson(response, retVal, new HashMap<String, String>());
	}
	
	public static void printJson(HttpServletResponse response, boolean success, String msg, JSON result, Date expiresDate) {
		JSONObject retVal = new JSONObject();
		retVal.put("result", result);
		retVal.put("success", success);
		retVal.put("msg", msg);
		printJson(response, retVal, expiresDate);
	}
	
	public static void printJson(HttpServletResponse response, boolean success, String msg, JSON result, int cacheTimeout) {
		JSONObject retVal = new JSONObject();
		retVal.put("result", result);
		retVal.put("success", success);
		retVal.put("msg", msg);
		printJson(response, retVal, cacheTimeout);
	}
	
	public static void printJson(HttpServletResponse response, Object jsonObj) {
		printJson(response, jsonObj, new HashMap<String, String>());
	}
	
	public static void printJson(HttpServletResponse response, Object jsonObj, Date expiresDate) {
		Map<String, String> header = new HashMap<String, String>();
		if(expiresDate != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);  
		    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			header.put("Expires", sdf.format(expiresDate));
		}
		printJson(response, jsonObj, header);
	}
	
	public static void printJson(HttpServletResponse response, Object jsonObj, long cacheTimeout) {
		Map<String, String> header = new HashMap<String, String>();
		if(cacheTimeout > 0) {
			header.put("Cache-control", "max-age="+cacheTimeout);
		} else {
			header.put("Cache-control", "no-cache");
		}
		printJson(response, jsonObj, header);
	}
	
	public static void printJson(HttpServletResponse response, Object jsonObj, Map<String, String> header) {
		try {
			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpStatus.SC_OK);
			if(header != null) {
				for(String key : header.keySet()) {
					response.setHeader(key, header.get(key));
				}
			}
			response.getWriter().write(JSON.toJSONString(jsonObj,features));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public static void back(HttpServletResponse response) {
		try {
			response.setContentType("text/html; charset=UTF-8");
			response.setHeader("Cache-Control","no-cache");
			response.setHeader("Pragma","no-cache");
			response.setDateHeader ("Expires", 0);
			response.getWriter().write("<script type=\"text/javascript\">window.history.go(-2);window.reload();</script>");
		} catch(IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public static void setExpiresHeader(HttpServletResponse response,Date expiresDate){
		if(expiresDate != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);  
		    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		    response.setHeader("Expires",  sdf.format(expiresDate));
		}
	}
	
	public static void setExpiresHeader(HttpServletResponse response,long cacheTimeout){
		if(cacheTimeout > 0) {
			 response.setHeader("Cache-control", "max-age="+cacheTimeout);
		} else {
			 response.setHeader("Cache-control", "no-cache");
		}
	}
	
	/**
	 * 向客户端输出异常json信息 {success:false, result:data, msg:msg, exceptionType:异常的类型名称}
	 * @param response
	 * @param e 异常，可以为null
	 * @param msg 需要传递的信息
	 * @param data 额外的json数据，可以为null
	 * @param isCommitResponse 是否立刻让客户端响应
	 */
	public static void printExceptionJson(HttpServletResponse response, Exception e, String msg, JSON data, boolean isCommitResponse){
		JSONObject retVal = new JSONObject();
		retVal.put("result", data);
		retVal.put("success", false);
		retVal.put("msg", msg);
		if(e != null){
			retVal.put("exceptionType", e.getClass().getSimpleName());
		}
		Writer writer = null;
		try {
			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpStatus.SC_OK);
			writer = response.getWriter();
			writer.write(JSON.toJSONString(retVal, features));
			if(isCommitResponse){
				writer.flush();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage());
		}
	}
}
