package cn.richinfo.login.impl.handler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yzkf.app.Memcached;
import yzkf.exception.ApiException;
import yzkf.exception.ParserConfigException;
import yzkf.utils.TryParse;

import cn.richinfo.core.utils.web.HttpClient;
import cn.richinfo.core.utils.web.WebUtils;
import cn.richinfo.login.ConfigHelper;
import cn.richinfo.login.abatrace.ILoginBeforeHandler;
import cn.richinfo.login.pojo.Result;

public class ValidateHandler implements ILoginBeforeHandler{
	private Logger logger = LoggerFactory.getLogger(getClass());
	private Memcached cache = Memcached.getInstance();
	private static final String CACHE_FAILED_TIMES = "login_failed_times_";
	private static String configText(String node) {
		return ConfigHelper.getInstance().readLogin(node);
	}
	public static final String DEFAULT_OPERATION_TYPE = "999";
	
	/**
	 * 校验验证码方法
	 */
	@Override
	public Result loginBeforeHandle(HttpServletRequest request,String verifyCode,String loginName,String loginType){
		logger.info("登录前置条件，校验验证码开始");
		Result result = new Result();
		String agentId = WebUtils.getCookieValue(request, "agentid");
		logger.info("进入图形验证码验证方法|loginName={}|agentId={}", loginName, agentId);
		result.setOK(false);
		String clientIP = WebUtils.getClientIP(request);
		
		long failedTimes = cache.getCounter(CACHE_FAILED_TIMES + loginName);
		if (failedTimes == -1)
			failedTimes = 0;// 缓存不存在，则为0次
		if (failedTimes >= TryParse.toInt(configText("Login/maxfailedtimes"))) {
			result.setDescr(configText("Login/FailedTooMuch"));
			logger.info("登录失败次数太多，限制登录|loginName={}",loginName);
			return result;
		}
		String smsVerifyCode = configText("Login/smsverifycode");
		// 短信登录时判断是否需要开启图形验证码
		if (!loginType.equals("SMS") || !smsVerifyCode.equals("0")) {
			// 图形验证码校验
			if (failedTimes >= TryParse.toInt(configText("Login/minfailedtimes"))) {
				// 调用验证码验证接口
				String verifyResult = validate(agentId, verifyCode, loginName, clientIP,
						DEFAULT_OPERATION_TYPE);
				if (!verifyResult.equals("OK")) {
					result.setDescr(verifyResult);
					return result;
				} else {
					logger.info("验证码验证不通过|错误信息：" + verifyResult);
				}
			}
		}
		result.setOK(true);
		logger.info("登录前置条件，校验验证码结束");
		return result;
	}
	
	/**
	 * 验证码校验接口
	 * 
	 * @param strAgentId
	 *            获取验证码时写入cookie中的AgentID值
	 * @param rndcode
	 *            用户输入的验证码
	 * @param user
	 *            用户号码或者SessionID
	 * @param clientIP
	 *            用户客户端IP
	 * @param operationType
	 *            操作类型：1,2,3,7,999,9999，为null时默认999
	 * @return 验证码验证结果
	 * @throws ParserConfigException
	 * @throws ApiException
	 */
	protected String validate(String strAgentId, String rndcode, String user, String clientIP,
			String operationType) {
		logger.info("开始校验图形验证码");
		// 检查参数
		if (StringUtils.isEmpty(operationType)) {
			operationType = DEFAULT_OPERATION_TYPE;
			// throw new NullPointerException("参数 operationType 不能为空.");
		}
		if (StringUtils.isEmpty(user)) {
			throw new NullPointerException("参数 user 不能为空.");
		}
		if (StringUtils.isEmpty(clientIP)) {
			throw new NullPointerException("参数 clientIP 不能为空.");
		}
		if (StringUtils.isEmpty(rndcode)) {
			return configText("VerifyResult/Empty");
		}
		if (StringUtils.isEmpty(strAgentId)) {
			return configText("VerifyResult/NullAgent");
		}

		String url = configText("verify/post") + "?clientid=" + operationType + "&pvccode=" + rndcode
				+ "&agentid=" + strAgentId + "&clientip=" + clientIP + "&user=" + user;
		logger.info("打印验证码请求参数：" + url);
		String out = null;
		try {
			out = HttpClient.get(url);
		} catch (IOException e) {
			logger.info("验证码验证接口报异常|user=" + user, e);
			throw new RuntimeException("验证码校验发生IO异常");
		}
		logger.info("打印验证码请求返回的参数：" + out);
		if (out.indexOf(strAgentId) == 0) {
			int flag = TryParse.toInt(out.substring(out.indexOf("Result=") + 7), -1);// 长度变更
																						// 2014.3.4
			switch (flag) {
			case -1:
				break;
			case 0:
				return "OK";
			case 1:
				return configText("VerifyResult/Invalid");// 验证码错误
			case 2:
				return configText("VerifyResult/Unknow");// "对不起，系统繁忙，请重试！";
			case 3:
				return configText("VerifyResult/Expires");// "验证码已失效，请重新输入！";
			case 4:
				return configText("VerifyResult/Illegal");// "验证码操作太过频繁，请稍后再试！";
			case 5:
				return configText("VerifyResult/Expires");// "验证码已失效，请重新输入！";
			case 6:
				return configText("VerifyResult/ServerBusy");// "对不起，系统繁忙，请重试[0XX01]！";
			case 7:
				return configText("VerifyResult/Failed");// "获取验证码失败，请刷新验证码后重试！";
			default:
				logger.error("验证码校验返回未定义的结果：" + flag);
				break;
			}
		}
		return configText("VerifyResult/Invalid");
	}
	
}
