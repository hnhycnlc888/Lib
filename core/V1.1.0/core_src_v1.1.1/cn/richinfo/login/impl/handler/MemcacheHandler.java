package cn.richinfo.login.impl.handler;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yzkf.app.Memcached;
import yzkf.utils.TryParse;
import yzkf.utils.Utility;
import cn.richinfo.login.ConfigHelper;
import cn.richinfo.login.abatrace.IGetUserInfoPostHandler;
import cn.richinfo.login.abatrace.ILoginPostHandler;
import cn.richinfo.login.pojo.Result;
import cn.richinfo.login.pojo.UserInfo;

public class MemcacheHandler implements ILoginPostHandler, IGetUserInfoPostHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private Memcached cache = Memcached.getInstance();
	private static final String SESSION_PROJECT_LOGINID = "myProjectLoginID";
	private static final String CACHE_SESSION_KEY = "login_session_";
	private static final String CACHE_FAILED_TIMES = "login_failed_times_";

	private static String configText(String node) {
		return ConfigHelper.getInstance().readLogin(node);
	}

	public Result loginPostHandle(HttpServletRequest request, UserInfo userInfo, String loginName,
			String loginType) {
		logger.info("登录后置条件，开始设置memcache");
		Result result = new Result();
		// 记录会话状态
		request.getSession().invalidate();// 强制更新sessionid
		request.getSession(true).setAttribute(SESSION_PROJECT_LOGINID, userInfo.getLoginid());
		result = memcacheHandler(request, userInfo);
		//如登录成功则清楚错误次数，否则+1
		if (result.isOK()) {
			cache.delete(CACHE_FAILED_TIMES + loginName);
		} else {
			cache.addOrIncr(CACHE_FAILED_TIMES + loginName, 1L,
					Utility.getDateWithoutTime(Calendar.DAY_OF_YEAR, 1));
		}
		logger.info("登录后置条件，设置memcache结束");
		return result;
	}

	public Result userInfoPostHandler(HttpServletRequest request, UserInfo userInfo) {
		logger.info("获取用户信息后置条件，开始设置memcache");
		Result result = new Result();
		if(!userInfo.getIssso().equalsIgnoreCase("0")){
			// 更新会话状态
			request.getSession().invalidate();// 强制更新sessionid
			request.getSession(true).setAttribute(SESSION_PROJECT_LOGINID, userInfo.getLoginid());
		}
		result = memcacheHandler(request, userInfo);
		logger.info("获取用户信息后置条件，设置memcache结束");
		return result;
	}

	protected Result memcacheHandler(HttpServletRequest request, UserInfo userInfo) {
		Result result = new Result();
		result.setOK(true);
		if(userInfo == null){
			return result;
		}
		try {
			cache.set(CACHE_SESSION_KEY + request.getSession(true).getId(), userInfo,
					DateUtils.addMinutes(new Date(), TryParse.toInt(configText("Login/sessiontimeout"))));
		} catch (Exception e) {
			result = setResult(false, "S9994", configText("LoginResult/SystemError"));
			logger.error("设置memcache时报异常|userNumber={}|passportID={}", userInfo.getUserNumber(),
					userInfo.getPassPortId());
		}
		return result;
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
