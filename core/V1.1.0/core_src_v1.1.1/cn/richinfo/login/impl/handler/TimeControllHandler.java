package cn.richinfo.login.impl.handler;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yzkf.config.ProjectConfig;

import cn.richinfo.login.ConfigHelper;
import cn.richinfo.login.abatrace.IGetUserInfoBeforeHandler;
import cn.richinfo.login.abatrace.ILoginBeforeHandler;
import cn.richinfo.login.pojo.Result;

public class TimeControllHandler implements ILoginBeforeHandler, IGetUserInfoBeforeHandler{
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static String configText(String node) {
		return ConfigHelper.getInstance().readLogin(node);
	}
	private static ProjectConfig projectConfig = ProjectConfig.getInstance();
	
	/**
	 * 登录前判断是否在活动时间内
	 */
	@Override
	public Result loginBeforeHandle(HttpServletRequest request,String verifyCode,String loginName,String loginType){
		logger.info("登录前置条件，判断活动时间开始");
		Result result = new Result();
		result = timeControll();
		logger.info("登录前置条件，判断活动时间结束");
		return result;
	}
	
	/**
	 * 获取用户信息前判断是否在活动时间内
	 */
	@Override
	public Result userInfoBeforeHandler(HttpServletRequest request){
		logger.info("获取用户信息前置条件，判断活动时间开始");
		Result result = new Result();
		result = timeControll();
		logger.info("获取用户信息前置条件，判断活动时间结束");
		return result;
	}
	
	/**
	 * 判断当前时间是否在活动时间内
	 * @return
	 */
	protected Result timeControll(){
		Result result = new Result();
		result.setOK(true);
		try {
			if (!projectConfig.isStart()) {
				result = setResult(false, "S1001", configText("LoginResult/NotStart"));
				logger.info("验证失败，活动时间未开始|projectCode={}", projectConfig.getCode());
			}
			if (projectConfig.isEnd()) {
				result = setResult(false, "S1002", configText("LoginResult/GameOver"));
				logger.info("验证失败，活动时间已结束|projectCode={}", projectConfig.getCode());
			}
		} catch (Exception e) {
			result = setResult(false, "S9995", configText("LoginResult/GameOver"));
			logger.error("验证活动时间报异常|projectCode={}", projectConfig.getCode());
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
