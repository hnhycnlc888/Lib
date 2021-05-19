package cn.richinfo.login.impl.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yzkf.config.ProjectConfig;
import yzkf.utils.Utility;

import cn.richinfo.core.mybatis.extend.dao.MybaitsDao;
import cn.richinfo.core.mybatis.extend.utils.MybatisDAOHelper;
import cn.richinfo.core.spring.SpringContext;
import cn.richinfo.core.utils.web.WebUtils;
import cn.richinfo.login.ConfigHelper;
import cn.richinfo.login.abatrace.IGetUserInfoPostHandler;
import cn.richinfo.login.abatrace.ILoginPostHandler;
import cn.richinfo.login.pojo.Result;
import cn.richinfo.login.pojo.UserInfo;
import eu.bitwalker.useragentutils.UserAgent;

/**
 * 
 */
public class OracleLoginDbLogHandler implements ILoginPostHandler, IGetUserInfoPostHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static final String SESSION_PROJECT_LOGINID = "myProjectLoginID";

	private static String configText(String node) {
		return ConfigHelper.getInstance().readLogin(node);
	}

	private MybaitsDao mybatisDao;

	public void setMybatisDao(MybaitsDao mybatisDao) {
		this.mybatisDao = mybatisDao;
	}

	@Override
	public Result loginPostHandle(HttpServletRequest request, UserInfo userInfo, String loginName,
			String loginType) {
		logger.info("登录后置条件，oracle数据库记录登录数据开始");
		Result result = new Result();
		result.setOK(true);
		try {
			// 记录用户登录数据到数据库
			long loginID = addDbRecord(request, userInfo, loginType, "0");
			if (loginID == 0) {
				result = setResult(false, "S9996", configText("LoginResult/SystemError"));
				logger.info("oracle数据库记录用户登录数据时loginID=0|userNumber={}", userInfo.getUserNumber());
				return result;
			}
			userInfo.setLoginid(loginID);
		} catch (Exception e) {
			result = setResult(false, "S9996", configText("LoginResult/SystemError"));
			logger.error("oracle数据库记录用户登录数据时异常|userNumber={}", userInfo.getUserNumber(), e);
		}
		userInfo.setLoginProject(ProjectConfig.getInstance().getCode());
		result.setUserInfo(userInfo);
		logger.info("登录后置条件，oracle数据库记录登录数据结束");
		return result;
	}

	@Override
	public Result userInfoPostHandler(HttpServletRequest request, UserInfo userInfo) {
		logger.info("获取用户信息后置条件，oracle数据库记录登录数据开始");
		Result result = new Result();
		result.setOK(true);
		userInfo.setIssso("0");

		try {
			// 判断是否为单点登录操作
			Object loginID = request.getSession(true).getAttribute(SESSION_PROJECT_LOGINID);
			String projectCode = ProjectConfig.getInstance().getCode();
			String ssoFrom = "0";
			if (!StringUtils.isEmpty(userInfo.getLoginProject()))
				ssoFrom = userInfo.getLoginProject();

			// 判断当前项目编号与用户信息中项目编号是否相等
			if (!projectCode.equalsIgnoreCase(userInfo.getLoginProject())) {
				if (loginID == null || (Long) loginID < userInfo.getLoginid() || userInfo.getLoginid() == 0) {
					long newLoginID = addDbRecord(request, userInfo, "SSO", ssoFrom);
					if (newLoginID == 0) {
						logger.info("oracle数据库记录用户单点登录数据时loginID=0|userNumber={}", userInfo.getUserNumber());
					} else {
						userInfo.setLoginid(newLoginID);
						userInfo.setLoginProject(projectCode);
						userInfo.setIssso(ssoFrom);
					}
				} 
			}
		} catch (Exception e) {
			result = setResult(false, "S9996", configText("LoginResult/SystemError"));
			logger.error("oracle数据库记录用户单点登录数据时异常|userNumber={}", userInfo.getUserNumber(), e);
		}

		result.setUserInfo(userInfo);
		logger.info("获取用户信息后置条件，oracle数据库记录登录数据结束");
		return result;
	}

	protected long addDbRecord(HttpServletRequest request, UserInfo userInfo, String logType, String ssoFrom) {
		String userAgentHeader = request.getHeader("User-Agent");
		return addDbRecord(userInfo, userAgentHeader, WebUtils.getClientIP(request), ProjectConfig
				.getInstance().getCode(), logType, ssoFrom);
	}

	/**
	 * 记录登录数据到数据库
	 * 
	 * @param userInfo
	 *            用户信息对象
	 * @param userAgentHeader
	 *            http请求的UA头信息
	 * @param clientIP
	 *            用户本地IP
	 * @param projectCode
	 *            当前活动项目编号
	 * @param logType
	 *            登录方式：Web、Wap、SSO
	 * @param ssoFrom
	 *            单点登录来源项目编号
	 * @return 本次登录的标识LoginID
	 */
	protected long addDbRecord(UserInfo userInfo, String userAgentHeader, String clientIP,
			String projectCode, String logType, String ssoFrom) {
		UserAgent userAgent = UserAgent.parseUserAgentString(userAgentHeader);

		String browser = userAgent.getBrowser().getName();
		String os = userAgent.getOperatingSystem().getName();
		try {
			logger.info("开始记录登录信息|userNumber={}", userInfo.getUserNumber());
			Map<String, Object> pm = new HashMap<String, Object>();
			pm.put("procedureName", ProjectConfig.getInstance().getLoginProcedure());
			pm.put("i_usermobile", userInfo.getUserNumber());
			pm.put("i_alias",
					StringUtils.isEmpty(userInfo.getAliase()) ? userInfo.getUserNumber() : userInfo
							.getAliase());
			pm.put("i_usealiaslogin", 0);
			pm.put("i_provcode", userInfo.getProvCode());
			pm.put("i_areacode", userInfo.getAreaCode());
			pm.put("i_cardtype", StringUtils.isEmpty(userInfo.getCardType()) ? "0" : userInfo.getCardType());
			pm.put("i_comefrom", logType);
			pm.put("i_projectno", projectCode);
			pm.put("i_ip", clientIP);
			pm.put("i_browser", browser);
			pm.put("i_yearmonth", Utility.formatDate(new Date(), "yyyyMM"));
			pm.put("i_datestring", Utility.formatDate(new Date(), "yyyyMMdd"));
			pm.put("i_clientinfo", os);
			pm.put("i_reserve", ssoFrom);
			SpringContext.getBean(MybaitsDao.class).selectList("Yzkf.addLoginRec", pm);
			logger.info("记录登录信息成功|userNumber={}|loginId={}", userInfo.getUserNumber(),
					MybatisDAOHelper.getLongValue(pm, "o_loginid"));
			logger.info("oracle记录用户登录数据|PASSPORTID={}|BROWSER={}|CLIENTINFO={}|COMEFROM={}",
					userInfo.getPassPortId(), browser, os, logType);
			return MybatisDAOHelper.getLongValue(pm, "o_loginid");
		} catch (Exception e) {
			logger.error("记录登录信息异常", e);
			return 0;
		}
	}

	/**
	 * 设置返回信息
	 * 
	 * @param isOK
	 *            是否成功
	 * @param code
	 *            返回码
	 * @param descr
	 *            返回信息描述
	 * @return
	 */
	protected Result setResult(boolean isOK, String code, String descr) {
		Result result = new Result();
		result.setOK(isOK);
		result.setCode(code);
		result.setDescr(descr);
		return result;
	}

}
