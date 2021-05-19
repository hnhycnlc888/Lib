package yzkf.app;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.helpers.LogLog;

import cn.richinfo.core.utils.web.HttpClient;
import cn.richinfo.core.utils.web.WebUtils;
import yzkf.config.ConfigFactory;
import yzkf.config.ProjectConfig;
import yzkf.enums.ActiveFunction;
import yzkf.enums.ActiveOperation;
import yzkf.utils.FileRW;
import yzkf.utils.Utility;

public class ActiveLog {
	private String projectCode;
	private String serverIP;
	private String clickTwiceUrl;
	private String activeLogPath;	
	
	private static ActiveLog instance;
	/**
	 * 获取单例
	 * @return
	 */
	public static ActiveLog getInstance() {
		if(instance == null)
			instance = new ActiveLog();
		return instance;
	}
	private ActiveLog(){}
	/**
	 * 项目编号
	 * @return
	 */
	protected String getProjectCode() {
		if(StringUtils.isEmpty(projectCode))
			projectCode = ProjectConfig.getInstance().getCode();
		return projectCode;
	}
	protected void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	/**
	 * 服务器IP
	 * @return
	 */
	protected String getServerIP() {
		if(StringUtils.isEmpty(serverIP)){
			try{
		    	serverIP = InetAddress.getLocalHost().getHostAddress();
		     }catch(UnknownHostException t){
		         LogLog.debug("获取服务器IP地址异常", t);
		     }
		}
		return serverIP;
	}
	/**
	 * 二次点击上报路径
	 * @return
	 */
	protected String getClickTwiceUrl() {
		if(StringUtils.isEmpty(clickTwiceUrl))
			clickTwiceUrl = ConfigFactory.getInstance().newAppConfig().getLogClickUrl();
		return clickTwiceUrl;
	}
	/**
	 * 用户活跃日志路径
	 * @return
	 */
	protected String getActiveLogPath() {
		if(StringUtils.isEmpty(activeLogPath))
			activeLogPath = ConfigFactory.getInstance().newAppConfig().getLogActionPath();
		return activeLogPath;
	}

	/**
	 * 记录WEB及WAP页面二次点击链接日志，返回000表示记录成功；201表示记录点击日志失败	 
	 * @param mobile 手机号码
	 * @param vistype 访问类型 通道编号 12为web，13为wap
	 * @param parms 附加字段
	 * @param projectcode 项目编号，为空则读配置文件中的项目编号
	 * @param request 客户端request对象
	 * @return
	 */
	protected boolean ClickTwice(String mobile, String vistype, String parms,String proCode, HttpServletRequest request)
	{
		if(!StringUtils.isEmpty(proCode))
			setProjectCode(proCode);
	    String pageurl = request.getRequestURL().toString();
	    String sessionid = request.getSession(true).getId();
	    String ua = request.getHeader("User-Agent"); 
	    String clientIP = WebUtils.getClientIP(request);
		    
	    String cip;
	    String sip;
		try {
			pageurl = URLEncoder.encode(pageurl, "utf-8");
			cip = URLEncoder.encode(clientIP, "utf-8");
			sip = URLEncoder.encode(getServerIP(), "utf-8");
			ua = URLEncoder.encode(ua, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			cip = clientIP;
			sip = getServerIP();
			ua = "0";
			LogLog.debug("二次点击行为日志执行UrlEncode异常", e1);
		}

	    String url = getClickTwiceUrl() + "?mobile=" + mobile + "&seid=" + sessionid + "&prj=" + getProjectCode() + "&vt=" + vistype + "&purl=" + pageurl + "&ua=" + ua + "&cip=" + cip + "&sip=" + sip + "&pa=" + parms;
	    
	    HttpClient.getAsync(url, "utf-8");
//    	try {
//			HttpClient.get(url, "utf-8");
//		} catch (IOException e) {
//			LogLog.debug("二次点击行为日志发送一次", e);
//			return false;
//		}
        return true;
	}
	/**
	 * 记录WAP页面二次点击链接日志
	 * @param mobile 手机号码
	 * @param projectcode 项目编号
	 * @param request
	 * @return
	 */
	public boolean WapClickTwice(String mobile, String projectcode, HttpServletRequest request)
	{
	    return ClickTwice(mobile, "13","",projectcode,request);
	}
	/**
	 * 记录WAP页面二次点击链接日志
	 * @param mobile 手机号码
	 * @param request
	 * @return
	 */
	public boolean WapClickTwice(String mobile, HttpServletRequest request)
	{
	    return ClickTwice(mobile, "13","","",request);
	}
	/**
	 * 记录WEB页面二次点击链接日志
	 * @param mobile 手机号码
	 * @param projectcode 项目编号
	 * @param request
	 * @return
	 */
	public boolean WebClickTwice(String mobile,String projectcode, HttpServletRequest request)
	{
	    return ClickTwice(mobile, "12","",projectcode,request);
	}
	/**
	 * 记录WEB页面二次点击链接日志
	 * @param mobile 手机号码
	 * @param request
	 * @return
	 */
	public boolean WebClickTwice(String mobile, HttpServletRequest request)
	{
	    return ClickTwice(mobile, "12","","",request);
	}
	/**
	 * 记录行为日志
	 * @param proCode 项目编号
	 * @param userMobile 手机号码
	 * @param provCode 省份编号
	 * @param areaCode 地区编号
	 * @param userIP 用户IP
	 * @param functionId 活跃行为编号
	 * @param operationId 功能行为编号
	 * @param winPrizeInfo 中奖奖品信息(不能超过25个字符 格式如：用户获得XXXX奖品)
	 * @param userTag 用户属性标签编号
	 */
	protected void WriteBehaviorLog(String proCode,String userMobile, int provCode, int areaCode,String userIP, int functionId,int operationId, String winPrizeInfo, String userTag)
	{
		if(!StringUtils.isEmpty(proCode))
			setProjectCode(proCode);

	    if (userTag != "")
	    {
	    	if(!Pattern.isMatches("^(\\d+[,])*(\\d+)$", userTag))
	    		userTag = "";
	    }
	    if (!StringUtils.isEmpty(userMobile) && !StringUtils.isEmpty(getProjectCode()))
	    {
	        String fileName = getActiveLogPath() + "/" + getProjectCode() + "/"
	        		+ getProjectCode() + "_" + getServerIP() + "_" + Utility.formatDate(new Date(), "yyyyMMdd") + ".log";
            
            String writeContent = getProjectCode() +"|"+ getServerIP() +"|"+ userMobile +"|"+ provCode +"|"+ areaCode +"|"+ userIP
            		 +"|"+ Utility.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss") +"|"+ functionId +"|"+ operationId +"|"+ winPrizeInfo +"|"+ userTag;

            FileRW.appendlnAsync(fileName, writeContent, "utf-8");
            
//            try {
//				FileRW.appendln(fileName, writeContent, "utf-8");
//			} catch (IOException e) {
//				LogLog.debug("写入行为日志时发生异常", e);
//			}
        }
	}
	/**
	 * 记录行为日志
	 * @param userMobile 手机号码
	 * @param provCode 省份编号
	 * @param areaCode 地区编号
	 * @param userIP 用户IP
	 * @param functionId 活跃行为编号
	 * @param operationId 功能行为编号
	 * @param winPrizeInfo 中奖奖品信息(不能超过25个字符 格式如：用户获得XXXX奖品)
	 * @param userTag 用户属性标签编号
	 */
	protected void WriteBehaviorLog(String userMobile, int provCode, int areaCode,String userIP, int functionId,int operationId, String winPrizeInfo, String userTag){
		WriteBehaviorLog(null, userMobile, provCode, areaCode, userIP, functionId, operationId, winPrizeInfo, userTag);
	}
	/**
	 * 记录行为日志
	 * @param proCode 项目编号
	 * @param userMobile 手机号码
	 * @param provCode 省份编号
	 * @param areaCode 地区编号
	 * @param userIP 用户IP
	 * @param function 活跃行为，ActiveFunction 枚举
	 * @param operation 功能行为，ActiveOperation 枚举
	 * @param winPrizeInfo 中奖奖品信息(不能超过25个字符 格式如：用户获得XXXX奖品)
	 * @param userTag 用户属性标签编号
	 */
	public void WriteBehaviorLog(String proCode,String userMobile, int provCode, int areaCode,String userIP, ActiveFunction function, ActiveOperation operation, String winPrizeInfo, String userTag){
		WriteBehaviorLog(proCode, userMobile, provCode, areaCode, userIP, function.getValue(), operation.getValue(), winPrizeInfo, userTag);
	}
	/**
	 * 记录行为日志
	 * @param userMobile 手机号码
	 * @param provCode 省份编号
	 * @param areaCode 地区编号
	 * @param userIP 用户IP
	 * @param function 活跃行为，{@link ActiveFunction} 枚举
	 * @param operation 功能行为，{@link ActiveOperation} 枚举
	 * @param winPrizeInfo 中奖奖品信息(不能超过25个字符 格式如：用户获得XXXX奖品)
	 * @param userTag 用户属性标签编号
	 */
	public void WriteBehaviorLog(String userMobile, int provCode, int areaCode,String userIP, ActiveFunction function, ActiveOperation operation, String winPrizeInfo, String userTag){
		WriteBehaviorLog(null, userMobile, provCode, areaCode, userIP, function.getValue(), operation.getValue(), winPrizeInfo, userTag);
	}
}
