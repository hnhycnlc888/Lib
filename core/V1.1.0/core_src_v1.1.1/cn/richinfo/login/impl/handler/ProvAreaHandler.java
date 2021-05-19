package cn.richinfo.login.impl.handler;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yzkf.config.ProjectConfig;

import cn.richinfo.login.ConfigHelper;
import cn.richinfo.login.abatrace.IGetUserInfoPostHandler;
import cn.richinfo.login.abatrace.ILoginPostHandler;
import cn.richinfo.login.pojo.Result;
import cn.richinfo.login.pojo.UserInfo;

public class ProvAreaHandler implements ILoginPostHandler,IGetUserInfoPostHandler{
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static String configText(String node) {
		return ConfigHelper.getInstance().readLogin(node);
	}
	private static ProjectConfig projectConfig = ProjectConfig.getInstance();
	
	@Override
	public Result loginPostHandle(HttpServletRequest request,UserInfo userInfo, String loginName, String loginType){
		logger.info("登录后置条件，判断省份地市开始");
		Result result = new Result();
		result.setOK(true);
		if(userInfo != null){
			result = provAreaControll(request, userInfo);
		}
		logger.info("登录后置条件，判断省份地市结束");
		return result;
	}
	
	@Override
	public Result userInfoPostHandler(HttpServletRequest request,UserInfo userInfo) {
		logger.info("获取用户信息后置条件，判断省份地市开始");
		Result result = new Result();
		result.setOK(true);
		if(userInfo != null){
			result = provAreaControll(request, userInfo);
		}
		logger.info("获取用户信息后置条件，判断省份地市结束");
		return result;
	}
	
	protected Result provAreaControll(HttpServletRequest request,UserInfo userInfo){
		Result result = new Result();
		result.setOK(true);
		try {
			// 省份地区判断
			if (!projectConfig.isChinaMobile(userInfo.getProvCode())) {
				result = setResult(false, "S1005", configText("LoginResult/NotChinaMobile"));
				logger.info("号码非中国移动手机号码|passportId={}|userNumber={}", userInfo.getPassPortId(),
						userInfo.getUserNumber());
			}
			if (!projectConfig.checkProv(userInfo.getProvCode())) {
				result = setResult(false, "S1003", configText("LoginResult/ProvNotAllow"));
				logger.info("活动未对该省份开放|passportId={}|provCode={}", userInfo.getPassPortId(), userInfo.getProvCode());
			}
			if (!projectConfig.checkArea(userInfo.getAreaCode())) {
				result = setResult(false, "S1004", configText("LoginResult/AreaNotAllow"));
				logger.info("活动未对该地市开放|passportId={}|areaCode={}", userInfo.getPassPortId(), userInfo.getAreaCode());
			}
		} catch (Exception e) {
			result = setResult(false, "S9997", configText("LoginResult/SystemError"));
			logger.error("判断是否移动手机号码、省份、地市限制时报异常|passportId={}|provCode={}|areaCode={}",
					userInfo.getPassPortId(), userInfo.getProvCode(), userInfo.getAreaCode(), e);
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
