package cn.richinfo.login.impl.handler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yzkf.config.ProjectConfig;
import yzkf.utils.TryParse;

import cn.richinfo.core.mybatis.extend.dao.MybaitsDao;
import cn.richinfo.core.mybatis.extend.utils.MybatisDAOHelper;
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
public class MysqlLoginDbLogHandler implements ILoginPostHandler, IGetUserInfoPostHandler {
	private final String mapperId = "LoginRecord";
	private static final String SESSION_PROJECT_LOGINID = "myProjectLoginID";
	private MybaitsDao mybatisDao;
	private Logger logger = LoggerFactory.getLogger(getClass());

	private static String configText(String node) {
		return ConfigHelper.getInstance().readLogin(node);
	}

	@Override
	public Result loginPostHandle(HttpServletRequest request, UserInfo userInfo, String loginName,
			String loginType) {
		logger.info("登录后置条件，mysql记录登录数据开始");
		Result result = new Result();
		result.setOK(true);
		if (userInfo == null) {
			return result;
		}

		// 将用户信息转换为map类型
		Map<String, Object> pm = parseUserInfo(request, userInfo, loginType, "0");

		UserInfo recordInfo = addRecord(pm, userInfo);
		if (recordInfo.getLoginid() == 0 || recordInfo.getUserId() == 0) {
			result = setResult(false, "S9996", configText("LoginResult/SystemError"));
			return result;
		}

		recordInfo.setLoginProject(ProjectConfig.getInstance().getCode());
		result.setUserInfo(recordInfo);
		logger.info("登录后置条件，mysql记录登录数据结束");
		return result;
	}

	@Override
	public Result userInfoPostHandler(HttpServletRequest request, UserInfo userInfo) {
		logger.info("获取用户信息后置条件，mysql记录登录数据开始");
		Result result = new Result();
		result.setOK(true);
		if (userInfo == null) {
			return result;
		}

		// 判断是否为单点登录操作
		Object loginID = request.getSession(true).getAttribute(SESSION_PROJECT_LOGINID);
		String projectCode = ProjectConfig.getInstance().getCode();
		String ssoFrom = "0";
		if (!StringUtils.isEmpty(userInfo.getLoginProject()))
			ssoFrom = userInfo.getLoginProject();

		// 判断当前项目编号与用户信息中项目编号是否相等
		if (!projectCode.equalsIgnoreCase(userInfo.getLoginProject())) {
			if (loginID == null || (Long) loginID < userInfo.getLoginid() || userInfo.getLoginid() == 0) {
				// 将用户信息转换为map类型
				if(ssoFrom.equals("0"))
					ssoFrom = "1";
				Map<String, Object> pm = parseUserInfo(request, userInfo, "SSO", ssoFrom);
				UserInfo recordInfo = addRecord(pm, userInfo);
				if (recordInfo.getLoginid() != 0 && recordInfo.getUserId() != 0) {
					userInfo = recordInfo;
					userInfo.setLoginProject(projectCode);
					userInfo.setIssso(ssoFrom);
				}
			} else {
				userInfo.setIssso("0");	
			}
		} else {
			userInfo.setIssso("0");
		}

		result.setUserInfo(userInfo);
		logger.info("获取用户信息后置条件，mysql记录登录数据结束");
		return result;
	}

	public void setMybatisDao(MybaitsDao mybatisDao) {
		this.mybatisDao = mybatisDao;
	}

	// 插入登录数据和用户信息数据
	protected UserInfo addRecord(Map<String, Object> pm, UserInfo userInfo) {

		long loginId = 0;
		long userId = 0;

		try {
			// 插入用户登录数据
			logger.info("开始插入登录数据");
			loginId = insertLogin(pm);
			logger.info("插入登录数据成功|passportId={}|loginId={}", userInfo.getPassPortId(), loginId);

			// 插入用户信息数据
			try {
				logger.info("开始插入用户信息数据");
				int userCount = userInfoSelect(pm);
				if (userCount == 0) {
					logger.info("数据库无用户信息数据，新增一条用户信息记录");
					userId = userInfoInsert(pm);
				} else {
					logger.info("数据库已有户信息数据，更新用户信息记录");
					userId = updateUserInfo(pm);
				}
				logger.info("插入或更新用户信息数据成功|userCount={}|userId={}", userCount, userId);
			} catch (Exception e) {
				userId = updateUserInfo(pm);
				logger.error("mysql数据库记录用户信息数据报异常|passportid={}", userInfo.getPassPortId(), e);
			}
		} catch (Exception e) {
			logger.error("mysql数据库记录用户信息和登录数据时报异常|passportid={}", userInfo.getPassPortId(), e);
		}

		// 更新用户信息
		userInfo.setUserId(userId);
		userInfo.setLoginid(loginId);
		return userInfo;
	}

	/**
	 * 将用户信息转换为map类型
	 * 
	 * @param request
	 * @param userInfo
	 * @param loginType
	 * @return
	 */
	protected Map<String, Object> parseUserInfo(HttpServletRequest request, UserInfo userInfo,
			String loginType, String ssoFrom) {
		UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
		String browser = userAgent.getBrowser().getName();
		String os = userAgent.getOperatingSystem().getName();
		SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put("PASSPORTID", userInfo.getPassPortId());
		pm.put("LOGINTIME", new Date());
		pm.put("USERMOBILE", userInfo.getUserNumber());
		pm.put("ALIAS", userInfo.getAliase());
		pm.put("USEALIASLOGIN", 0);
		pm.put("PROVCODE", userInfo.getProvCode());
		pm.put("AREACODE", userInfo.getAreaCode());
		pm.put("CARDTYPE", userInfo.getCardType());
		pm.put("COMEFROM", loginType);
		pm.put("PROJECTNO", ProjectConfig.getInstance().getCode());
		pm.put("IP", WebUtils.getClientIP(request));
		pm.put("BROWSER", browser);
		pm.put("YEARMONTH", TryParse.toInt(yearMonthFormat.format(new Date())));
		pm.put("DATESTRING", TryParse.toInt(dateFormat.format(new Date())));
		pm.put("RECORDTIME", new Date());
		pm.put("CLIENTINFO", os);
		pm.put("RESERVE", "");
		pm.put("CREATETIME", new Date());
		pm.put("LASTTIME", new Date());
		pm.put("ACTTIMES", 0);
		pm.put("RESERVE", ssoFrom);
		logger.info("mysql记录用户登录数据|PASSPORTID={}|BROWSER={}|CLIENTINFO={}|COMEFROM={}",
				userInfo.getPassPortId(), browser, os, loginType);
		return pm;
	}

	/**
	 * 插入用户登录信息数据
	 * 
	 * @param pm
	 * @return
	 */
	public long insertLogin(Map<String, Object> pm) {
		mybatisDao.insert(mapperId + ".loginInsert", pm);
		return MybatisDAOHelper.getLongValue(pm, "ID");
	}

	/**
	 * 插入用户信息数据
	 * 
	 * @param pm
	 * @return
	 */
	public long userInfoInsert(Map<String, Object> pm) {
		mybatisDao.insert(mapperId + ".userInfoInsert", pm);
		return MybatisDAOHelper.getLongValue(pm, "ID");
	}

	/**
	 * 更新用户信息数据
	 * 
	 * @param pm
	 * @return
	 */
	public long updateUserInfo(Map<String, Object> pm) {
		return mybatisDao.update(mapperId + ".updateUserInfo", pm);
	}

	/**
	 * 查询用户信息数据
	 * 
	 * @param pm
	 * @return
	 */
	public int userInfoSelect(Map<String, Object> pm) {
		return mybatisDao.selectOne(mapperId + ".userInfoSelect", pm);
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
