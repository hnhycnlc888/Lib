package cn.richinfo.login.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import yzkf.app.Memcached;
import yzkf.utils.TryParse;
import yzkf.utils.Xml;
import cn.richinfo.core.utils.MD5;
import cn.richinfo.core.utils.web.HttpClient;
import cn.richinfo.core.utils.web.WebUtils;
import cn.richinfo.login.ConfigHelper;
import cn.richinfo.login.DesService;
import cn.richinfo.login.abatrace.AbstractLogin;
import cn.richinfo.login.pojo.Result;
import cn.richinfo.login.pojo.UserInfo;

public class Login extends AbstractLogin {

	private static Logger logger = LoggerFactory.getLogger(Login.class);

	private final static String LOGIN_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "<object>"
			+ "<string name=\"timeToken\">{timeToken}</string>"
			+ "<string name=\"loginType\">{loginType}</string>"
			+ "<string name=\"loginName\">{loginName}</string>"
			+ "<string name=\"loginPassword\">{loginPassword}</string>" + "</object>";
	private final static String INVIAL_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "<object>"
			+ "<string name=\"timeToken\">{timeToken}</string>" + "<string name=\"sid\">{sid}</string>"
			+ "<string name=\"rmkey\">{rmkey}</string>" + "</object>";
	private String encoding = ConfigHelper.getInstance().readLogin("Login/Encoding");
	private String loginurl = ConfigHelper.getInstance().readLogin("Login/ApiUrl") + UUID.randomUUID();
	private String invialurl = ConfigHelper.getInstance().readLogin("Invial/ApiUrl") + UUID.randomUUID();
	private static final String CACHE_SESSION_KEY = "login_session_";
	public static final String DEFAULT_OPERATION_TYPE = "999";
	private Memcached cache = Memcached.getInstance();

	private static String configText(String node) {
		return ConfigHelper.getInstance().readLogin(node);
	}

	/**
	 * 用户登录方法
	 * 
	 * @param request
	 * @param response
	 * @param loginName
	 *            用户名
	 * @param loginPassword
	 *            密码
	 * @param verifyCode
	 *            验证码
	 * @param loginType
	 *            登录方式：Web,Wap,SMS(注意区分大小写)
	 * @return
	 */
	protected Result doLogin(HttpServletRequest request, HttpServletResponse response, String loginName,
			String loginPassword, String verifyCode, String loginType) {
		Result result = new Result();
		result.setOK(false);
		try {
			// 验证码校验通过后，调用登录接口
			result = okDo(request, loginName, loginPassword, loginType);

			// // 将rmkey写入cookie
			// javax.servlet.http.Cookie cookie = new
			// javax.servlet.http.Cookie("RMKEY", userInfo.getRmkey());
			// cookie.setPath("/");
			// cookie.setDomain(configText("Login/mailDomain"));
			// cookie.setMaxAge(24 * 60 * 60);
			// cookie.setHttpOnly(true);
			// response.addCookie(cookie);
		} catch (Exception e) {
			result = setResult(false, "S9999", configText("LoginResult/SystemError"));
			logger.error("登录方法异常|loginName={}", loginName, e);
		}
		return result;
	}

	/**
	 * 退出登录
	 * 
	 * @param request
	 */
	protected void doLogOut(HttpServletRequest request, HttpServletResponse response) {
		request.getSession().invalidate();
		cache.delete(CACHE_SESSION_KEY + request.getSession().getId());
		String mailDomain = ConfigHelper.getInstance().readLogin("Login/mailDomain");
		String[] arrDomain = mailDomain.split(",");
		for (int i = 0; i < arrDomain.length; i++) {
			javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie("RMKEY", null);
			cookie.setMaxAge(0);
			cookie.setPath("/");
			cookie.setDomain(arrDomain[i]);
			response.addCookie(cookie);
		}
	}

	/**
	 * 获取短信验证码
	 * 
	 * @param request
	 * @param loginName
	 *            手机号码
	 * @return
	 */
	protected Result doGetSMS(HttpServletRequest request, String loginName) {
		Map<String, String> requestHeaders = new HashMap<String, String>();
		Result result = new Result();
		result.setOK(false);
		// 设置请求头信息
		requestHeaders.put("Content-Type", "application/xml");
		requestHeaders.put("User-Agent", request.getHeader("User-Agent"));
		requestHeaders.put("Richinfo-Client-IP", WebUtils.getClientIP(request));

		String smsUrl = configText("Sms/ApiUrl") + UUID.randomUUID();
		String fv = configText("Sms/fv");
		String clientId = configText("Sms/clientId");
		String version = configText("Sms/version");

		// 组装请求的XML参数
		Document dom = DocumentHelper.createDocument();
		Element rootEl = DocumentHelper.createElement("object");
		dom.add(rootEl);
		Element stringEl = DocumentHelper.createElement("string");
		stringEl.addAttribute("name", "loginName");
		stringEl.setText(loginName);
		Element stringE2 = DocumentHelper.createElement("string");
		stringE2.addAttribute("name", "fv");
		stringE2.setText(fv);
		Element stringE3 = DocumentHelper.createElement("string");
		stringE3.addAttribute("name", "clientId");
		stringE3.setText(clientId);
		Element stringE4 = DocumentHelper.createElement("string");
		stringE4.addAttribute("name", "version");
		stringE4.setText(version);
		rootEl.add(stringEl);
		rootEl.add(stringE2);
		rootEl.add(stringE3);
		rootEl.add(stringE4);

		logger.info("获取短信验证码请求的URL地址：{}", smsUrl);
		logger.info("请求的XML参数为：{}", dom.asXML());
		String out = "";
		try {
			// 发送post请求
			out = HttpClient.send(smsUrl, dom.asXML(), encoding, true, requestHeaders);
			logger.info("短信验证码接口返回的参数为：{}", out);
		} catch (Exception e) {
			logger.error("获取短信验证码接口异常|loginName={}", loginName, e);
			result.setCode("PML401010999");
			result.setDescr(configText("SmsResult/Failed"));
			return result;
		}

		try {
			// 对接口返回的json进行解析
			JSONObject outJson = JSONObject.parseObject(out);
			String code = outJson.getString("code");
			String summary = outJson.getString("summary");
			if (code.equals("S_OK")) {
				result.setOK(true);
				logger.info("获取短信验证码接口返回成功|code={}|summary={}", code, summary);
			} else {
				summary = configText("SmsResult/" + code);
				logger.info("获取短信验证码接口返回失败|错误码code={}|错误原因summary={}", code, summary);
			}
			result.setCode(code);
			result.setDescr(summary);
		} catch (Exception e) {
			logger.error("解析获取短信验证码接口返回数据异常|loginName={}", loginName, e);
			result.setDescr(configText("SmsResult/PML401010009"));
		}
		return result;
	}

	/**
	 * 调用登录接口并返回UserInfo信息
	 * 
	 * @param request
	 * @param loginName
	 *            用户名
	 * @param loginPassword
	 *            密码
	 * @param loginType
	 *            登录方式：Web,Wap,SMS(注意区分大小写)
	 * @return
	 */
	public Result okDo(HttpServletRequest request, String loginName, String loginPassword, String loginType) {
		Map<String, String> requestHeaders = new HashMap<String, String>();
		Result result = new Result();
		result.setOK(true);
		result.setUserInfo(null);

		// 设置请求头信息
		requestHeaders.put("Content-Type", "application/xml");
		requestHeaders.put("User-Agent", request.getHeader("User-Agent"));
		requestHeaders.put("Richinfo-Client-IP", WebUtils.getClientIP(request));
		UserInfo user = new UserInfo();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestamp = sdf.format(new Date());
		// 替换需要post的xml参数
		String strRequestData = LOGIN_XML.replace("{timeToken}", timestamp).replace("{loginType}", "0")
				.replace("{loginName}", loginName).replace("{loginPassword}", loginPassword);
		String out = "";
		try {
			// 发送post请求
			out = HttpClient.send(loginurl, strRequestData, encoding, true, requestHeaders);
		} catch (IOException e) {
			logger.error("登录接口异常|loginName=" + loginName, e);
			result = setResult(false, "S9999", configText("LoginResult/SystemError"));
			return result;
		}
		System.out.println(out);

		try {
			// 对接口返回的json进行解析
			JSONObject outJson = JSONObject.parseObject(out);
			String code = outJson.getString("code");
			String summary = outJson.getString("summary");
			result.setCode(code);
			result.setDescr(summary);
			if (!code.equals("S_OK")) {
				result = setResult(false, code, summary);
				logger.info("登录接口返回失败|loginName={}|code={}|descr={}" + loginName, code, summary);
				return result;
			}
			// 将返回的json数据进行封装处理
			user = parseUserJson(out);
			if (user.getPassPortId() == 0) {
				result = setResult(false, "S1006", configText("LoginResult/PassportIdEmpty"));
				logger.info("调用登录接口返回通行证号码为空|loginName={}", loginName);
				return result;
			}
			// 用户数据解析成功后记录日志
			logger.info("调用登录接口成功并返回用户信息|loginName={}", loginName);
			result.setUserInfo(user);
		} catch (Exception e) {
			result = setResult(false, "S9998", configText("LoginResult/SystemError"));
			logger.error("解析登录接口返回数据异常|loginName:" + loginName, e);
			return result;
		}

		return result;
	}

	/**
	 * 调用用户是否在线接口
	 * 
	 * @param request
	 * @param sid
	 *            用户sid
	 * @param rmkey
	 *            用户rmkey
	 * @return
	 */
	public Result invialUserInfo(HttpServletRequest request, String sid, String rmkey) {
		Map<String, String> requestHeaders = new HashMap<String, String>();
		Result result = new Result();
		result.setOK(true);
		result.setUserInfo(null);

		// 设置请求头信息
		requestHeaders.put("Content-Type", "application/xml");
		requestHeaders.put("User-Agent", request.getHeader("User-Agent"));
		requestHeaders.put("Richinfo-Client-IP", WebUtils.getClientIP(request));
		UserInfo user = new UserInfo();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestamp = sdf.format(new Date());
		// 替换需要post的xml参数
		String strRequestData = INVIAL_XML.replace("{timeToken}", timestamp).replace("{loginType}", "0")
				.replace("{sid}", sid).replace("{rmkey}", rmkey);
		String out = "";
		try {
			// 发送post请求
			out = HttpClient.send(invialurl, strRequestData, encoding, true, requestHeaders);
		} catch (IOException e) {
			logger.error("用户在线接口异常|sid:" + sid, e);
			result.setCode("S_9999");
			result.setDescr(configText("LoginResult/SystemError"));
			return result;
		}
		System.out.println(out);

		try {
			// 对接口返回的json进行解析
			JSONObject outJson = JSONObject.parseObject(out);
			String code = outJson.getString("code");
			String summary = outJson.getString("summary");
			result.setCode(code);
			result.setDescr(summary);
			if (!code.equals("S_OK")) {
				logger.info("登录接口返回失败|错误码code:" + code + "|错误原因：" + summary);
				return result;
			}
			// 将返回的json数据进行封装处理
			user = parseUserJson(out);
			if (user.getPassPortId() == 0) {
				result.setCode("S_9998");
				result.setDescr(configText("LoginResult/PassportIdEmpty"));
				logger.info("调用登录接口返回通行证号码为空|sid={}", sid);
				return result;
			}
			// 用户数据解析成功后记录日志
			logger.info("调用用户在线接口成功并返回用户信息|passPortId=" + user.getPassPortId());

			result.setUserInfo(user);
			result.setOK(true);
		} catch (Exception e) {
			logger.error("解析用户是否在线接口返回数据异常|sid=" + sid, e);
			result.setDescr(configText("LoginResult/SystemError"));
			return result;
		}
		return result;
	}

	/**
	 * 判断用户是否在线，返回UserInfo为null则表示用户不在线，需重新登录
	 * 
	 * @param request
	 * @return
	 */
	protected UserInfo doGetUserInfo(HttpServletRequest request) {
		UserInfo localUserInfo = (UserInfo) cache.get(CACHE_SESSION_KEY + request.getSession(true).getId());
		if(StringUtils.isNotEmpty(request.getSession(true).getId())){
			logger.info("请求id={}",request.getSession(true).getId());
		} else {
			logger.info("请求id为空");
		}
		String sid = request.getParameter("sid");
		String rmkey = WebUtils.getCookieValue(request, "RMKEY");
		Result invialResult = new Result();
		try {
			logger.info("开始调用request获取用户信息接口|sid={}|rmkey={}", sid, rmkey);
			if (localUserInfo == null) {
				if (StringUtils.isNotEmpty(sid) && StringUtils.isNotEmpty(rmkey)) {
					invialResult = invialUserInfo(request, sid, rmkey);
					if (invialResult.isOK()) {
						localUserInfo = invialResult.getUserInfo();
					} else {
						logger.info("调用用户是否在线接口返回false|code=" + invialResult.getCode() + "|descr="
								+ invialResult.getDescr());
						return localUserInfo;
					}
				} else {
					logger.info("sid或rmkey为空，无法调用用户是否在线接口|sid={}|rmkey={}", sid, rmkey);
					return localUserInfo;
				}
			}
		} catch (Exception e) {
			logger.error("获取用户信息时报异常|sid={}|rmkey={}", sid, rmkey);
			localUserInfo = null;
		}
		return localUserInfo;
	}

	/**
	 * 通过sid和rmkey获取用户登录状态
	 * 
	 * @param request
	 * @param sid
	 * @param rmkey
	 * @return
	 */
	protected UserInfo doGetUserInfo(HttpServletRequest request, String sid, String rmkey) {
		UserInfo localUserInfo = (UserInfo) cache.get(CACHE_SESSION_KEY + request.getSession(true).getId());
		Result invialResult = new Result();
		try {
			logger.info("开始调用sid和rmkey获取用户信息接口|sid={}|rmkey={}", sid, rmkey);
			if (localUserInfo == null) {
				if (StringUtils.isNotEmpty(sid) && StringUtils.isNotEmpty(rmkey)) {
					invialResult = invialUserInfo(request, sid, rmkey);
					if (invialResult.isOK()) {
						localUserInfo = invialResult.getUserInfo();
					} else {
						logger.info("调用用户是否在线接口返回false|code=" + invialResult.getCode() + "|descr="
								+ invialResult.getDescr());
						return localUserInfo;
					}
				} else {
					logger.info("sid或rmkey为空，无法调用用户是否在线接口|sid=" + sid + "|rmkey=" + rmkey);
					return localUserInfo;
				}
			} else {
				cache.set(CACHE_SESSION_KEY + request.getSession(true).getId(), localUserInfo,
						DateUtils.addMinutes(new Date(), TryParse.toInt(configText("Login/sessiontimeout")))); // 用户活跃，则延迟会话时间
			}
		} catch (Exception e) {
			logger.error("获取用户信息时报异常|sid=" + sid + "|rmkey=" + rmkey);
			localUserInfo = null;
		}
		return localUserInfo;
	}

	/**
	 * 对接口返回的JSON数据进行封装和处理
	 * 
	 * @param jsonString
	 * @return
	 */
	public UserInfo parseUserJson(String jsonString) {
		String userNumber = "";
		JSONObject json = JSONObject.parseObject(jsonString);
		UserInfo userInfo = json.getObject("var", UserInfo.class);
		try {
			JSONObject var = json.getJSONObject("var");
			JSONObject aliases = var.getJSONObject("aliases");
			String alaisFetion = aliases.getString("alaisFetion");
			String aliasName = aliases.getString("aliasName");
			userNumber = userInfo.getUserNumber();
			String aliase = "";
			int passPortId = 0;
			// 对飞信别名进行分隔并获取通行证ID
			if (StringUtils.isNotEmpty(alaisFetion)) {
				String[] arrAliases = alaisFetion.split("@");
				String passPortIdString = arrAliases[0];
				passPortId = TryParse.toInt(passPortIdString);
			}
			// 对邮箱别名进行分隔并获取邮箱别名
			if (StringUtils.isNotEmpty(aliasName)) {
				String[] arrAliases = aliasName.split("@");
				aliase = arrAliases[0];
			}
			// 对手机号码作去除86操作
			if (StringUtils.isNotEmpty(userNumber)) {
				userInfo.setUserNumber(userNumber.substring(2, userNumber.length()));
			}
			userInfo.setAliase(aliase);
			userInfo.setPassPortId(passPortId);
			userInfo.setLoginid(0);
		} catch (Exception e) {
			logger.error("对接口返回的JSON数据进行封装和处理时异常|passPortId=" + userInfo.getPassPortId(), e);
		}
		return userInfo;
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

	/**
	 * 通过手机号码获取SSOID
	 * 
	 * @param usermobile
	 *            手机号码
	 * @return
	 */
	public static String getSsoId(String usermobile) {
		String url = configText("SsoApi/url");
		String clientId = configText("SsoApi/clientId");
		String key = configText("SsoApi/key");
		String msisdn = "";
		try {
			msisdn = DesService.encrypt(usermobile);

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
			Date old = format.parse("2000-1-1 00-00-00");
			Date now = new Date();
			String timeStamp = String.valueOf((now.getTime() - old.getTime()) / 1000 + 1000);

			String skey = MD5.encode(clientId + msisdn + timeStamp + key);
			url = url + "?ClientID=" + clientId + "&MSISDN=" + msisdn + "&TimeStamp=" + timeStamp + "&Skey="
					+ skey;
		} catch (Exception e) {
			logger.error("生成获取SSOID的链接异常|usermobile={}", usermobile, e);
			return "0";
		}
		String out = "";
		String result = "0";
		Map<String, String> requestHeaders = new HashMap<String, String>();
		String encoding = configText("Login/encoding");
		requestHeaders.put("Content-Type", "application/test");
		try {
			logger.info("打印请求URL地址：--------" + url);
			out = HttpClient.send(url, "", encoding, false, requestHeaders);
			// out = "Result:0\r\n SSOSID:08097b73-b428-4bcc-9cfe-7024646e9a23";
			if (out.indexOf("Result:0") > -1) {
				String[] arrResult = out.split(":");
				result = arrResult[arrResult.length - 1];
			}
		} catch (IOException e) {
			logger.error("获取SSOID接口异常|usermobile={}", usermobile, e);
			return "0";
		}
		return result;
	}

	/**
	 * 通过手机号码获取用户在邮箱的SID及RMKEY
	 * 
	 * @param usermobile
	 *            手机号码
	 * @return
	 */
	public Map<String, Object> getSid(String usermobile) {
		String authurl = configText("SsoApi/authurl");
		// 请求SSOID
		String ssoid = getSsoId(usermobile);
		Map<String, Object> userMap = new HashMap<String, Object>();
		try {
			// 获取邮箱的SID和RMKEY
			String geturl = authurl + "?Mobile_No=%s&SSOID=%s";
			logger.info("请求获取SID接口的链接为|geturl=", geturl);
			String out = HttpClient.get(String.format(geturl, usermobile, ssoid));
			logger.info("获取SID接口返回的参数为|out={}", out);
			Xml outXml = Xml.parseXml(out);
			// 返回RMKEY保存到Session
			String rmkey = outXml.evaluate("/responsedata/mailSid");
			// 返回SID保存到Session
			String sid = outXml.evaluate("/responsedata/realSid");
			userMap.put("rmkey", rmkey);
			userMap.put("sid", sid);
		} catch (Exception e) {
			logger.error("获取用户sid和rmkey方法异常|usermobile=", usermobile, e);
		}
		return userMap;
	}

}
