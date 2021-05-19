package yzkf.api;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.richinfo.core.utils.web.HttpClient;
import cn.richinfo.core.utils.web.WebUtils;
import yzkf.api.result.VerifyResult;
import yzkf.config.AppConfig;
import yzkf.config.ConfigFactory;
import yzkf.exception.ApiException;
import yzkf.exception.ParserConfigException;
import yzkf.utils.TryParse;
/**
 * 验证码验证接口
 * @author qiulw
 * @version V4.0.0
 */
public class VerifyCode {
	private static final Logger LOG = LoggerFactory.getLogger(VerifyCode.class.getName());
	public static final String DEFAULT_OPERATION_TYPE = "999"; 
	/**
	 * 校验验证码
	 * @param strAgentId 获取验证码时写入cookie中的AgentID值
	 * @param rndcode 用户输入的验证码
	 * @param user 用户号码或者SessionID
	 * @param clientIP 用户客户端IP
	 * @param operationType 操作类型：1,2,3,7,999,9999，为null时默认999
	 * @param config 配置对象，为null时使用默认配置
	 * @return 验证码验证结果
	 * @throws ParserConfigException
	 * @throws ApiException
	 */	
	public static VerifyResult validate(String strAgentId,String rndcode,String user,String clientIP,String operationType,AppConfig config) throws ParserConfigException, ApiException{
		//检查参数
		if(StringUtils.isEmpty(operationType)){
			operationType = DEFAULT_OPERATION_TYPE;
			//throw new NullPointerException("参数 operationType 不能为空.");
		}if(StringUtils.isEmpty(user)){
			throw new NullPointerException("参数 user 不能为空.");
		}if(StringUtils.isEmpty(clientIP)){
			throw new NullPointerException("参数 clientIP 不能为空.");
		}
		if(StringUtils.isEmpty(rndcode)){
			return VerifyResult.Empty;
		}
		if(StringUtils.isEmpty(strAgentId)){
			return VerifyResult.NullAgent;
		}
		if(config == null)
			config = ConfigFactory.getInstance().newAppConfig();
		
		String url = config.getVerifyPost() 
			+ "?clientid=" + operationType 
			+ "&pvccode=" + rndcode 
			+ "&agentid=" + strAgentId
			+ "&clientip=" + clientIP 
			+ "&user=" + user;
		String out = null;
		try {
			out = HttpClient.get(url);
		} catch (IOException e) {
			ApiException ex = new ApiException("验证码校验发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		if(out.indexOf(strAgentId)==0){			
			int flag = TryParse.toInt(out.substring(out.indexOf("Result=")+7),-1);//长度变更 2014.3.4
			switch(flag){
				case -1:
					break;
				case 0:
					return VerifyResult.OK;
				case 1:
					return VerifyResult.Invalid;//验证码错误
				case 2:
		            return VerifyResult.Unknow;//"对不起，系统繁忙，请重试！";
		        case 3:
		            return VerifyResult.Expires;//"验证码已失效，请重新输入！";
		        case 4:
		            return VerifyResult.Illegal;//"验证码操作太过频繁，请稍后再试！";
		        case 5:
		            return VerifyResult.Expires;//"验证码已失效，请重新输入！";
		        case 6:
		            return VerifyResult.ServerBusy;//"对不起，系统繁忙，请重试[0XX01]！";
		        case 7:		        	
		            return VerifyResult.Failed;//"获取验证码失败，请刷新验证码后重试！";
		        default:
	            	LOG.debug("验证码校验返回未定义的结果："+flag);
	            	break;
			}
		}
		return VerifyResult.Invalid;
		//return VerifyResult.Other;//验证码输入错误或输入位数不正确！
	}
	/**
	 * 校验验证码
	 * @param strAgentId 获取验证码时写入cookie中的AgentID值
	 * @param rndcode 用户输入的验证码
	 * @param user 用户号码或者SessionID
	 * @param clientIP 用户客户端IP
	 * @param operationType 操作类型：1,2,3,7,999,9999，为null时默认999
	 * @return 验证码验证结果
	 * @throws ParserConfigException
	 * @throws ApiException
	 */	
	public static VerifyResult validate(String strAgentId,String rndcode,String user,String clientIP,String operationType) throws ParserConfigException, ApiException{
		return validate(strAgentId, rndcode, user, clientIP, operationType, null);
	}
	/**
	 * 校验验证码
	 * @param strAgentId 获取验证码时写入cookie中的AgentID值
	 * @param rndcode 用户输入的验证码
	 * @param user 用户号码或者SessionID
	 * @param clientIP 用户客户端IP
	 * @return 验证码验证结果
	 * @throws ParserConfigException
	 * @throws ApiException
	 */	
	public static VerifyResult validate(String strAgentId,String rndcode,String user,String clientIP) throws ParserConfigException, ApiException{
		return validate(strAgentId, rndcode, user, clientIP, DEFAULT_OPERATION_TYPE);
	}
	/**
	 * 校验验证码
	 * @param request HttpServletRequest对象
	 * @param rndcode 用户输入的验证码
	 * @param user 用户号码或者SessionID
	 * @return
	 * @throws ParserConfigException
	 * @throws ApiException
	 */
	public static VerifyResult validate(HttpServletRequest request, String rndcode,String user) throws ParserConfigException, ApiException{
		return validate(WebUtils.getCookieValue(request, "agentid"), rndcode, user, WebUtils.getClientIP(request));
	}
	/**
	 * 校验验证码，验证码参数名必须是verifycode
	 * @param request HttpServletRequest对象,包含用户输入验证码值 verifycode
	 * @param user 用户号码或者SessionID
	 * @return
	 * @throws ParserConfigException
	 * @throws ApiException
	 */
	public static VerifyResult validate(HttpServletRequest request, String user) throws ParserConfigException, ApiException{
		return validate(request, request.getParameter("verifycode"), user);
	}
	/**
	 * 校验验证码，验证码参数名必须是verifycode
	 * <br/>
	 * <b>当且仅当使用SessionID作为用户标识时使用该方法</b>
	 * @param request HttpServletRequest对象,包含用户输入验证码值 verifycode
	 * @return
	 * @throws ParserConfigException
	 * @throws ApiException
	 */
	public static VerifyResult validate(HttpServletRequest request) throws ParserConfigException, ApiException{
		return validate(request, request.getSession().getId());
	}
}
