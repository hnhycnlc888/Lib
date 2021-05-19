package cn.richinfo.login.impl.handler;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yzkf.app.ActiveLog;
import yzkf.config.ProjectConfig;
import yzkf.enums.ActiveFunction;
import yzkf.enums.ActiveOperation;

import cn.richinfo.core.utils.web.WebUtils;
import cn.richinfo.login.abatrace.IGetUserInfoPostHandler;
import cn.richinfo.login.abatrace.ILoginPostHandler;
import cn.richinfo.login.pojo.Result;
import cn.richinfo.login.pojo.UserInfo;

/**
 * 
 */
public class ActivityLogHandler implements ILoginPostHandler, IGetUserInfoPostHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static ProjectConfig projectConfig = ProjectConfig.getInstance();

	@Override
	public Result loginPostHandle(HttpServletRequest request, UserInfo userInfo, String loginName,
			String loginType) {
		Result result = new Result();
		result.setOK(true);
		try {
			// 行为上报
			ActiveFunction act = ActiveFunction.WebLogin;
			if (loginType.equals("Wap"))
				act = ActiveFunction.WapLogin;
			ActiveLog.getInstance().WriteBehaviorLog(userInfo.getUserNumber(), userInfo.getProvCode(),
					userInfo.getAreaCode(), WebUtils.getClientIP(request), act, ActiveOperation.Nothing, "",
					projectConfig.getLoginUsertag());
			logger.info("记录行为日志成功|userNumber={}", userInfo.getUserNumber());
		} catch (Exception e) {
			logger.error("记录登录行为日志异常|userNumber={}", userInfo.getUserNumber(), e);
		}
		return result;
	}

	@Override
	public Result userInfoPostHandler(HttpServletRequest request, UserInfo userInfo) {
		Result result = new Result();
		result.setOK(true);
		try {
			if (!userInfo.getIssso().equals("0")) {
				// 行为上报
				ActiveFunction act = ActiveFunction.SSOLogin;
				ActiveLog.getInstance().WriteBehaviorLog(userInfo.getUserNumber(), userInfo.getProvCode(),
						userInfo.getAreaCode(), WebUtils.getClientIP(request), act, ActiveOperation.Nothing,
						"", projectConfig.getLoginUsertag());
				logger.info("记录行为日志成功|userNumber={}", userInfo.getUserNumber());
			}
		} catch (Exception e) {
			logger.error("记录行为日志报异常|userNumber={}", userInfo.getUserNumber(), e);
		}
		return result;
	}

}
