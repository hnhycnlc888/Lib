package yzkf.com;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.richinfo.core.mybatis.extend.dao.MybaitsDao;
import cn.richinfo.core.mybatis.extend.utils.MybatisDAOHelper;
import cn.richinfo.core.spring.SpringContext;
import cn.richinfo.core.utils.web.WebUtils;
import eu.bitwalker.useragentutils.UserAgent;
import yzkf.api.Disk;
import yzkf.api.Mail;
import yzkf.api.SSO;
import yzkf.api.VerifyCode;
import yzkf.api.result.DiskResult;
import yzkf.api.result.LoginResult;
import yzkf.api.result.MailResult;
import yzkf.api.result.Result;
import yzkf.api.result.SSOResult;
import yzkf.api.result.VerifyResult;
import yzkf.app.ActiveLog;
import yzkf.app.Memcached;
import yzkf.config.ProjectConfig;
import yzkf.enums.ActiveFunction;
import yzkf.enums.ActiveOperation;
import yzkf.exception.ApiException;
import yzkf.model.UserInfo;
import yzkf.utils.Utility;
/**
 * 业支开发统一登录组件
 * @author qiulw
 * @version v1.0.0 2014.04.20
 */
public class Login {
	private static final Logger LOG = LoggerFactory.getLogger(Login.class);
	private static final String CACHE_FAILED_TIMES = "login_failed_times_";
	private static final String CACHE_SESSION_KEY = "login_session_";
	private static final String SESSION_PROJECT_LOGINID = "myProjectLoginID";
	private Memcached cache;
	private Login(){
		cache = Memcached.getInstance();
	}
	private static Login instance;
	/**
	 * 获取登录类的单例
	 * @return 登录雷的单例
	 */
	public static Login getInstance() {
		if(instance == null)
			instance = new Login();
		return instance;
	}
	/**
	 * 获取用户是否在线/已登录
	 * @param request
	 * @return true 在线/登录状态，false 离线/未登录
	 */
	public boolean isOnline(HttpServletRequest request) {
		return getUserInfo(request) != null;
	}

	/**
	 * Web通过账号和密码登录
	 * <p>post/get参数必须按照指定名称：<br/>
	 * 账号，request.getParameter("account")<br/>
	 * 密码：request.getParameter("password")<br/>
	 * 验证码：request.getParameter("verifycode")<br/>
	 * 验证码标识：Utility.getCookieValue(request, "agentid")<br/></p>
	 * @param request 
	 * @return 接口执行结果{@link Result#getValue(Class)}，如果{@link Result#isOK()} == true 则通过  {@link Result#getValue(Class)}可获取到用户信息 {@link UserInfo}，否则{@link Result#getDescr()}获取登录失败友好提示
	 * @throws ApiException
	 */
	public Result web(HttpServletRequest request) throws ApiException{
		String account=request.getParameter("account");
		String password=request.getParameter("password");
		String verifyCode=request.getParameter("verifycode");
		String agentId=WebUtils.getCookieValue(request, "agentid");
		return web(request, account, password, verifyCode, agentId);
	}
	/**
	 * Web通过账号和密码登录
	 * @param request
	 * @param account
	 * @param password
	 * @param verifyCode
	 * @param agentId
	 * @return 接口执行结果{@link Result#getValue(Class)}，如果{@link Result#isOK()} == true 则通过  {@link Result#getValue(Class)}可获取到用户信息 {@link UserInfo}，否则{@link Result#getDescr()}获取登录失败友好提示
	 * @throws ApiException
	 */
	public Result web(HttpServletRequest request, String account,String password,String verifyCode,String agentId) throws ApiException{

		String clientIP = WebUtils.getClientIP(request);
		ProjectConfig projectConfig = ProjectConfig.getInstance();
		
		//验证码校验
		long failedTimes = cache.getCounter(CACHE_FAILED_TIMES+account);
		if(failedTimes == -1) failedTimes = 0;//缓存不存在，则为0次
		if(failedTimes >= projectConfig.getLoginMaxFailedTimes()){
			return LoginResult.FailedTooMuch;
		}
		if(failedTimes >= projectConfig.getLoginMinFailedTimes() ){
			VerifyResult verifyResult = VerifyCode.validate(agentId, verifyCode, account, clientIP);
			if(!verifyResult.isOK())
				return verifyResult;
		}
		
		//密码校验
		Disk diskApi = new Disk();
		DiskResult diskResult = diskApi.validateLogin(account, password, clientIP);
		if(!diskResult.isOK()){	
			failedTimes = cache.addOrIncr(CACHE_FAILED_TIMES+account, 1L, Utility.getDateWithoutTime(Calendar.DAY_OF_YEAR, 1));
			return diskResult;
		}
		UserInfo userInfo = diskResult.getValue(UserInfo.class);

		return okDo(request, userInfo, "Web", ActiveFunction.WebLogin);
	}
	/**
	 * 短信登录
	 * @param request
	 * @param account 用户号码/别名
	 * @param smsVerifyCode 短信验证码
	 * @return 接口执行结果{@link Result#getValue(Class)}，如果{@link Result#isOK()} == true 则通过  {@link Result#getValue(Class)}可获取到用户信息 {@link UserInfo}，否则{@link Result#getDescr()}获取登录失败友好提示
	 * @throws ApiException
	 */
	public Result sms(HttpServletRequest request, String account,String smsVerifyCode) throws ApiException{

		ProjectConfig projectConfig = ProjectConfig.getInstance();		
		//验证码校验
		long failedTimes = cache.getCounter(CACHE_FAILED_TIMES+account);
		if(failedTimes >= projectConfig.getLoginMaxFailedTimes()){
			return LoginResult.FailedTooMuch;
		}
		Mail mailApi = new Mail();
		MailResult mailResult = mailApi.getAttribute(account);
		if(!mailResult.isOK()) return mailResult;
		UserInfo userInfo = mailResult.getValue(UserInfo.class);
		Result comResult = SMSVerify.getInstance().check(userInfo.getMobile(), smsVerifyCode);
		if(!comResult.isOK()) return comResult;
		
		return okDo(request, userInfo, "SMS", ActiveFunction.WebLogin);
	}
	/**
	 * Wap短地址登录
	 * @param request
	 * @return
	 * @throws ApiException
	 */
	public Result wap(HttpServletRequest request) throws ApiException{
		SSO api = new SSO();
		SSOResult ssoResult= api.validate(request);
		if(!ssoResult.isOK()) return ssoResult;
		
		UserInfo userInfo = ssoResult.getValue(UserInfo.class);
		return okDo(request, userInfo, "Wap", ActiveFunction.WapLogin);
	}
	/**
	 * 邮箱单点登录到活动
	 * @param request
	 * @return
	 * @throws ApiException
	 */
	public Result sso(HttpServletRequest request) throws ApiException{
		SSO api = new SSO();
		SSOResult ssoResult= api.validate(request);
		if(!ssoResult.isOK()) return ssoResult;
		
		UserInfo userInfo = ssoResult.getValue(UserInfo.class);
		return okDo(request, userInfo, "SSO", ActiveFunction.SSOLogin);
	}
	/**
	 * 校验账号密码成功后逻辑处理
	 * @param request
	 * @param userInfo
	 * @param logType
	 * @param act
	 * @return
	 * @throws ApiException
	 */
	protected Result okDo(HttpServletRequest request,UserInfo userInfo,String logType,ActiveFunction act) throws ApiException{
		if(!logType.equalsIgnoreCase("SMS")){
			//获取属性
			Mail mailApi = new Mail();
			MailResult mailResult = mailApi.getAttribute(userInfo);
			if(!mailResult.isOK())
				return mailResult;
		}
		ProjectConfig projectConfig = ProjectConfig.getInstance();
		userInfo.setLoginProject(projectConfig.getCode());
		if(!logType.equals("SSO")){
			//记录用户登录数据到数据库
			long loginID = addDbRecord(request, userInfo, logType, "0");
			userInfo.setLoginId(loginID);
			//记录会话状态
			request.getSession().invalidate();//强制更新sessionid
			request.getSession(true).setAttribute(SESSION_PROJECT_LOGINID, loginID);
			cache.set(CACHE_SESSION_KEY+request.getSession(true).getId(), userInfo, Utility.getNow(Calendar.MINUTE, projectConfig.getLoginSessionTimeout()));
			//行为上报
			ActiveLog.getInstance().WriteBehaviorLog(userInfo.getMobile(), userInfo.getProvCode(), userInfo.getAreaCode(), WebUtils.getClientIP(request), act, ActiveOperation.Nothing, "", projectConfig.getLoginUsertag());
		}
		
		//活动时间控制
		if(!projectConfig.isStart())
			return LoginResult.NotStart;
		if(projectConfig.isEnd())
			return LoginResult.GameOver;
		//省份地区判断
		if(!projectConfig.isChinaMobile(userInfo.getProvCode()))
			return LoginResult.NotChinaMobile;
		if(!projectConfig.checkProv(userInfo.getProvCode()))
			return LoginResult.ProvNotAllow;
		if(!projectConfig.checkArea(userInfo.getAreaCode()))
			return LoginResult.AreaNotAllow;		
		
		return LoginResult.OK.setValue(userInfo);
	}
	/**
	 * 退出登录
	 * @param request
	 */
	public void logOut(HttpServletRequest request){
		cache.delete(CACHE_SESSION_KEY+request.getSession().getId());
	}
	/**
	 * 获取当前登录的用户信息
	 * @param request
	 * @return
	 */
	public UserInfo getUserInfo(HttpServletRequest request){
		UserInfo userInfo = (UserInfo) cache.get(CACHE_SESSION_KEY+request.getSession(true).getId());
		if(userInfo != null){			
			cache.set(CACHE_SESSION_KEY+request.getSession(true).getId(), userInfo, Utility.getNow(Calendar.MINUTE, ProjectConfig.getInstance().getLoginSessionTimeout()));	//用户活跃，则延迟会话时间
			String projectCode = ProjectConfig.getInstance().getCode();
			if(!projectCode.equalsIgnoreCase(userInfo.getLoginProject())){				
				Object loginID = request.getSession(true).getAttribute(SESSION_PROJECT_LOGINID);
				if(loginID == null || (Long)loginID < userInfo.getLoginId()) {
					//记录活动到活动单点登录数据
					loginID = addDbRecord(request, userInfo, "SSO", userInfo.getLoginProject());
					request.getSession(true).setAttribute(SESSION_PROJECT_LOGINID,loginID);
					//行为上报
					ActiveLog.getInstance().WriteBehaviorLog(userInfo.getMobile(), userInfo.getProvCode(), userInfo.getAreaCode(), WebUtils.getClientIP(request), ActiveFunction.WebLogin, ActiveOperation.Nothing, "", ProjectConfig.getInstance().getLoginUsertag());
				}
			}
		}
		return userInfo;
	}
	/**
	 * 记录用户会话信息，并记录到数据库、上报登录行为数据
	 * @param request 当前http的request对象
	 * @param userInfo 用户信息，将保存到会话中
	 */
	public void setUserInfo(HttpServletRequest request,UserInfo userInfo){
		setUserInfo(request, userInfo, true);
	}
	/**
	 * 设置用户会话信息
	 * 
	 * @param request 当前http的request对象
	 * @param userInfo 用户信息，将保存到会话中
	 * @param isLogin 本次保存是否登录行为（默认true）.当isLogin == true 时会将登录信息记录到数据库并提交行为上报数据
	 */
	public void setUserInfo(HttpServletRequest request,UserInfo userInfo,boolean isLogin){
		cache.set(CACHE_SESSION_KEY+request.getSession(true).getId(), userInfo, Utility.getNow(Calendar.MINUTE, ProjectConfig.getInstance().getLoginSessionTimeout()));
		if(isLogin){			
			//记录活动到活动单点登录数据
			long loginID = addDbRecord(request, userInfo, "SSO", userInfo.getLoginProject());
			request.getSession(true).setAttribute(SESSION_PROJECT_LOGINID,loginID);
			//行为上报
			ActiveLog.getInstance().WriteBehaviorLog(userInfo.getMobile(), userInfo.getProvCode(), userInfo.getAreaCode(), WebUtils.getClientIP(request), ActiveFunction.WebLogin, ActiveOperation.Nothing, "", ProjectConfig.getInstance().getLoginUsertag());
		}
	}
	/**
	 * 记录登录数据到数据库哭
	 * @param request
	 * @param userInfo 用户信息对象
	 * @param logType 登录仿古式：Web、Wap、SSO
	 * @param ssoFrom SSO登录来源：邮箱 0 ，其它活动为来源项目编号
	 */
	protected long addDbRecord(HttpServletRequest request,UserInfo userInfo,String logType,String ssoFrom){
		String userAgentHeader = request.getHeader("User-Agent");
		return addDbRecord(userInfo, userAgentHeader, WebUtils.getClientIP(request), ProjectConfig.getInstance().getCode(), logType, ssoFrom);
	}
	/**
	 * 记录登录数据到数据库哭
	 * @param userInfo 用户信息对象
	 * @param userAgentHeader http请求的UA头信息
	 * @param clientIP 用户本地IP
	 * @param projectCode 当前活动项目编号
	 * @param logType 登录方式：Web、Wap、SSO
	 * @param ssoFrom 单点登录来源项目编号
	 * @return 本次登录的标识LoginID
	 */
	protected long addDbRecord(UserInfo userInfo,String userAgentHeader,String clientIP,String projectCode,String logType,String ssoFrom){
		//LOG.warn(userInfo.getMobile()+userInfo.getAlias()+userInfo.getProvCode()+userInfo.getAreaCode()+userInfo.getCancelType()+userAgentHeader+clientIP+projectCode+logType+ssoFrom);
		UserAgent userAgent = UserAgent.parseUserAgentString(userAgentHeader);
		
		String browser = userAgent.getBrowser().getName();
		String os = userAgent.getOperatingSystem().getName();
		try {
			Map<String, Object> pm = new HashMap<String, Object>();
			pm.put("procedureName", ProjectConfig.getInstance().getLoginProcedure());
			pm.put("i_usermobile", userInfo.getMobile());
			pm.put("i_alias", StringUtils.isEmpty(userInfo.getAlias())?userInfo.getMobile():userInfo.getAlias());
			pm.put("i_usealiaslogin", 0);
			pm.put("i_provcode", userInfo.getProvCode());
			pm.put("i_areacode", userInfo.getAreaCode());
			pm.put("i_cardtype", StringUtils.isEmpty(userInfo.getCardType())?"0":userInfo.getCardType());
			pm.put("i_comefrom", logType);
			pm.put("i_projectno", projectCode);
			pm.put("i_ip", clientIP);
			pm.put("i_browser", browser);
			pm.put("i_yearmonth", Utility.formatDate(new Date(), "yyyyMM"));
			pm.put("i_datestring", Utility.formatDate(new Date(), "yyyyMMdd"));
			pm.put("i_clientinfo", os);
			pm.put("i_reserve", ssoFrom);
			SpringContext.getBean(MybaitsDao.class).selectList("Yzkf.addLoginRec", pm);
			return MybatisDAOHelper.getLongValue(pm, "o_loginid");
//			dao.procedure("{call "+ProjectConfig.getInstance().getLoginProcedure()+"(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}", 
//					userInfo.getMobile(),
//					Utility.isEmptyOrNull(userInfo.getAlias())?userInfo.getMobile():userInfo.getAlias(),
//					0,
//					userInfo.getProvCode(),
//					userInfo.getAreaCode(),
//					Utility.isEmptyOrNull(userInfo.getCardType())?"0":userInfo.getCardType(),
//					logType,
//					projectCode,
//					clientIP,
//					browser,
//					Utility.formatDate(new Date(), "yyyyMM"),
//					Utility.formatDate(new Date(), "yyyyMMdd"),
//					os,
//					ssoFrom,
//					outRowCount,
//					outLoginID);
		} catch (Exception e) {
			LOG.error("记录登录信息异常", e);
			return 0;
		}
//		return outLoginID.getValue().longValue();
	}
	
}
