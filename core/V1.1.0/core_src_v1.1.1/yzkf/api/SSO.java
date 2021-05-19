package yzkf.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.richinfo.core.utils.MD5;
import cn.richinfo.core.utils.web.HttpClient;
import yzkf.api.result.MailResult;
import yzkf.api.result.SSOResult;
import yzkf.app.Pattern;
import yzkf.config.ApiConfig;
import yzkf.config.ConfigFactory;
import yzkf.enums.SSOFlag;
import yzkf.enums.SSOType;
import yzkf.enums.WapSSOFlag;
import yzkf.exception.ApiException;
import yzkf.exception.ParserConfigException;
import yzkf.model.UserInfo;
import yzkf.utils.Security;
import yzkf.utils.TryParse;
import yzkf.utils.Utility;
import yzkf.utils.YZHttpServletRequestWrapper;
/**
 * 单点登录处理类
 * @author qiulw
 * @version V4.0.0 2012.02.06
 */
@SuppressWarnings("deprecation")
public class SSO {
	private static final Logger LOG = LoggerFactory.getLogger(SSO.class.getName());
	public UserInfo userInfo;
	private ApiConfig config;	
	private String flag;
	/***
	 * 获取接口返回的用户信息
	 * @return
	 */
	public UserInfo getUserInfo() {
		return userInfo;
	}
	/**
	 * 创建SSO对象，使用默认配置
	 * @throws ParserConfigException
	 */
	public SSO() throws ParserConfigException{
		ConfigFactory factory = ConfigFactory.getInstance();
		this.config = factory.newApiConfig();
	}
	/**
	 * 创建SSO对象，指定配置对象
	 * @param config API配置对象
	 */
	public SSO(ApiConfig config){
		this.config = config;
	}
	/**
	 * 验证SSO单点登录，web和wap通用
	 * @param request Servlet请求对象
	 * @return 验证结果 {@link SSOResult}，通过UserInfo user = (UserInfo) {@link SSOResult#getValue()}; 获取验证成功后的用户信息
	 * @example
<pre>
yzkf.api.SSO api = new yzkf.api.SSO();
SSOResult apiResult = api.validate(request);
if(apiResult.isOK()){
	UserInfo userInfo = (UserInfo)apiResult.getValue();
}
</pre>
	 */
	public SSOResult validate(HttpServletRequest request){
		long timestamp ;
		String skey;
		YZHttpServletRequestWrapper req = new YZHttpServletRequestWrapper(request);
		if(StringUtils.isEmpty(req.getParameterIgnoreCase("Sid"))){
			String mo = req.getParameterIgnoreCase("mo");
			String ua = req.getParameterIgnoreCase("ua");
			String retcode = req.getParameterIgnoreCase("retcode");
			timestamp = TryParse.toLong(req.getParameterIgnoreCase("timestamp"));
			skey = req.getParameterIgnoreCase("skey");
	
			return validateWap(mo, ua, retcode,timestamp, skey);
		}else{
			String mobile = req.getParameterIgnoreCase("Mobile");
			String sid = req.getParameterIgnoreCase("Sid");
			timestamp = TryParse.toLong(req.getParameterIgnoreCase("Timestamp"));
			String from = req.getParameterIgnoreCase("From");
			String to = req.getParameterIgnoreCase("To");
			skey = req.getParameterIgnoreCase("Skey");
			String outflag = req.getParameterIgnoreCase("Flag");

			return validateWeb(mobile,sid,timestamp,from,to,skey,outflag);
		}
	}	
	/**
	 * 验证SSO单点登录
	 * @param mobile 用户手机号，开头不带86
	 * @param sid 用户登录运营活动获得的虚拟sid
	 * @param timestamp 虚拟sid的有效时间戳，设定为通过userlogin.aspx验证后2个小时内，通过SSO登录不改变timestamp值
	 * @param from 来源运营活动编号，如是邮箱，则设为0
	 * @param to 目标运营活动编号
	 * @param skey 数据有效性签名，值为Md5(Mobile + Sid + Timestamp +  From + To + Key)，32位全大写字符串
	 * @return 验证结果 {@link SSOResult}，通过UserInfo user = (UserInfo) {@link SSOResult#getValue()}; 获取验证成功后的用户信息
	 * @example
<pre>
String mobile = request.getParameter("Mobile");
String sid = request.getParameter("Sid");
long timestamp = TryParse.toLong(request.getParameter("Timestamp"));
String from = request.getParameter("From");
String to = request.getParameter("To");
String skey = request.getParameter("Skey");
String outflag = request.getParameter("Flag");

yzkf.api.SSO api = new yzkf.api.SSO();
SSOResult apiResult = api.validateWeb(mobile,sid,timestamp,from,to,skey,outflag);
if(apiResult.isOK()){
	UserInfo ui = (UserInfo)apiResult.getValue();
}
</pre>
	 */
	public SSOResult validateWeb(String mobile,String sid,long timestamp,String from,String to,String skey,String outflag){
		if(StringUtils.isEmpty(mobile)
			|| StringUtils.isEmpty(sid)
			|| timestamp <= 0
			|| StringUtils.isEmpty(from)
			|| StringUtils.isEmpty(to)
			|| StringUtils.isEmpty(skey)){
			//throw new NullPointerException("参数不能为空：mo="+mo+",ua="+ua+",retcode="+retcode+",timestamp="+timestamp+",skey="+skey);
			return SSOResult.ParamterEmpty;
		}		
		String signed = MD5.encode(mobile+sid+timestamp+from+to+this.config.SSO_OPSSO_MD5Key);
		if(!signed.equalsIgnoreCase(skey)){
			//签名错误
			return SSOResult.InvalidKey;
		}
		if(Utility.getTimeSpan(0) > timestamp){
			//已过期
			return SSOResult.Expires;
		}
		//设置用户信息
		this.flag = outflag;
		this.userInfo = new UserInfo();
		if(!Pattern.isMobile(mobile) && !StringUtils.isEmpty(this.config.SSO_OPSSO_DESKey))
			mobile = Security.decryptDES(mobile, MD5.encode(this.config.SSO_OPSSO_DESKey).substring(0, 8),"utf-8");
		this.userInfo.setMobile(mobile);
		this.userInfo.setSid(sid);
		this.userInfo.setTimestamp(timestamp);
		
		return SSOResult.OK.setValue(this.userInfo);
	}
	/**
	 * 执行登录验证
	 * @param mo 用户号码
	 * @param ua 手机型号
	 * @param retcode 返回码
	 * @param timetamp 时间戳
	 * @param skey 数据签名
	 * @return 验证结果 {@link SSOResult}，通过UserInfo user = (UserInfo) {@link SSOResult#getValue()}; 获取验证成功后的用户信息
	 * @example
<pre>
String mo = request.getParameter("mo");
String ua = request.getParameter("ua");
String retcode = request.getParameter("retcode");
long timestamp = TryParse.toLong(request.getParameter("timestamp"));
String skey = request.getParameter("skey");

yzkf.api.SSO api = new yzkf.api.SSO();
SSOResult apiResult = api.validateWap(mo, ua, retcode,timestamp, skey);
if(apiResult.isOK()){
	UserInfo ui = (UserInfo)apiResult.getValue();
}
</pre>
	 */
	public SSOResult validateWap(String mo,String ua,String retcode,long timestamp,String skey){
		if(StringUtils.isEmpty(mo)
			|| StringUtils.isEmpty(ua)
			|| StringUtils.isEmpty(retcode)
			|| timestamp<=0
			|| StringUtils.isEmpty(skey)){
			//throw new NullPointerException("参数不能为空：mo="+mo+",ua="+ua+",retcode="+retcode+",timestamp="+timestamp+",skey="+skey);
			return SSOResult.ParamterEmpty;
		}		
		String signed = MD5.encode(mo+ua+retcode+timestamp+config.Wap_Login_MD5Key);
		if(!signed.equalsIgnoreCase(skey)){
			//签名错误
			return SSOResult.InvalidKey;
		}
		if(Utility.getTimeSpan(0) > timestamp){
			//已过期
			return SSOResult.Expires;
		}
		if(retcode.equalsIgnoreCase("000")){
			this.userInfo = new UserInfo();
			this.userInfo.setMobile(mo);
			this.userInfo.setPhoneModel(ua);
			this.userInfo.setTimestamp(timestamp);
			return SSOResult.OK.setValue(this.userInfo);
		}else{
			LOG.debug("Wap短地址跳转，返回未定义的返回值："+retcode);
			return SSOResult.Unknow;
		}		
	}
	/**
	 * 注册单点登录使用的SID
	 * @param mobile 用户手机号码
	 * @return 接口结果 {@link SSOResult}，通过 String sid = (String) {@link SSOResult#getValue()}; 获取注册成功的sid
	 * @throws ApiException
	 * @example
<pre>示例：
	yzkf.api.SSO api = new yzkf.api.SSO();
	String mobile = "13900001234";
	SSOResult apiResult = SSOResult.OK;
	try {
		apiResult = api.registerSID(mobile);
	} catch (ApiException e) {
		e.printStackTrace();
	}
	if(apiResult.isOK()){
		String sid = apiResult.getValue();
		System.out.println(sid);
	}
</pre>
	 */
	public SSOResult registerSID(String mobile) throws ApiException{
		if(StringUtils.isEmpty(mobile)){
			return SSOResult.ParamterEmpty;
		}
		String clientID = this.config.SSO_REGLOGIN_ClientID;
		String msisdn = mobile;
		try {
			msisdn = DesEcbEncrypt(mobile, this.config.SSO_REGLOGIN_DESKey);
		} catch (Exception e) {
			//LOG.error("SSO注册SID接口DES加密异常", e);
			ApiException ex = new ApiException("SSO注册SID接口DES加密异常");
			ex.initCause(e);
			throw ex;
		}
		String timestamp = String.valueOf(Utility.getTimeSpan(this.config.SSO_REGLOGIN_Expires));
		String apiURL = HttpClient.prepareURL(this.config.SSO_REGLOGIN_RegUrl, this.config.SSO_REGLOGIN_MD5Key, 
				"ClientID",clientID,
				"MSISDN",msisdn,
				"TimeStamp",timestamp,
				"Skey");
		String out = null;
		try {
			out = HttpClient.get(apiURL);
		} catch (IOException e) {
			ApiException ex = new ApiException("SSO注册SID接口异常");
			ex.initCause(e);
			throw ex;
		}
		String outResult = Pattern.getFirstMatch("Result:(-?[0-9]+)",out);
		if(outResult.equals("0")){
			String sid = Pattern.getFirstMatch("SSOSID:([0-9a-zA-Z -]*)", out);
			return SSOResult.OK.setValue(sid);
		}
		if(outResult.equals("-1")){
			return SSOResult.Failed;
		}
		if(outResult.equals("-2")){
			return SSOResult.InvalidIP;
		}
		if(outResult.equals("-3")){
			return SSOResult.Expires;
		}
		if(outResult.equals("-4")){
			return SSOResult.InvalidKey;
		}
		return SSOResult.Unknow;
	}
	/**
	 * 获取活动单点到邮箱的URL
	 * <br/>	
	 * @param mobile 用户手机号码
	 * @param msisdn 通过 registerSID 获取的用户sid
	 * @param flag 访问页面类型
	 * @param mailTo 收件人邮件地址。说明：用户写邮件页面，并且自动填入收件人邮件地址（只有Flag.getValue()=1时才有效），如果有多个邮件地址，采用逗号分割
	 * @param message 扩展字段，其他需要包含信息，
	 * @return 接口结果 {@link SSOResult}，通过 String ssoUrl = (String) {@link SSOResult#getValue()}; 获取单点登录邮箱的URL
	 * @see #registerSID(String)
	 * @example
<pre>示例：
	yzkf.api.SSO api = new yzkf.api.SSO();
	String mobile = "13900001234";
	SSOResult apiResult = SSOResult.OK;
	try {
		apiResult = api.registerSID(mobile);
	} catch (ApiException e) {
		e.printStackTrace();
	}
	if(apiResult.isOK()){
		String sid = api.sid;
		apiResult = api.getWebLoginUrl(mobile, sid, SSOFlag.Other.setValue("20"), "", "");
		if(apiResult.isOK())
			System.out.println(apiResult.getValue());
	}
</pre>
	 */
	public SSOResult getWebLoginUrl(String mobile,String sid,SSOFlag flag,String mailTo,String message){
		if(StringUtils.isEmpty(mobile))
			throw new NullPointerException("用户号码不能为空");
		if(StringUtils.isEmpty(sid))
			throw new NullPointerException("用户SID不能为空");
		if(!StringUtils.isEmpty(message)){
			try {
				message = URLEncoder.encode(message,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOG.warn("对单点登录到邮箱的message进行URLEncode时发生字符编码格式错误",e);
			}
		}
		String ssoUrl = HttpClient.prepareURL(this.config.SSO_REGLOGIN_LoginUrl, null, 
				"Mobile_No",mobile,
				"Flag",flag.getValue(),
				"MailTo",mailTo,
				"SSOID",sid,
				"Message",message);
		return SSOResult.OK.setValue(ssoUrl);
	}
	/**
	 * 获取活动单点到邮箱的URL,自动生成SID
	 * <p>谨慎使用，确保号码已登录才可使用<p/>
	 * @param mobile 用户手机号码
	 * @param flag 访问页面类型
	 * @param mailTo 收件人邮件地址。说明：用户写邮件页面，并且自动填入收件人邮件地址（只有Flag.getValue()=1时才有效），如果有多个邮件地址，采用逗号分割
	 * @param message 扩展字段，其他需要包含信息，
	 * @return 单点登录邮箱的URL
	 * @example
<pre>示例：
	yzkf.api.SSO api = new yzkf.api.SSO();
	String mobile = "13760709457";
	SSOResult apiResult = null;
	try {
		apiResult = api.getWebLoginUrl(mobile, SSOFlag.Inbox, "", "");
	} catch (ApiException e) {
		e.printStackTrace();
	}
	if(apiResult.isOK())
		System.out.println(apiResult.getValue());
</pre>
	 */
	public SSOResult getWebLoginUrl(String mobile,SSOFlag flag,String mailTo,String message) throws ApiException{
		SSOResult apiResult = SSOResult.OK;
		apiResult = this.registerSID(mobile);
		if(apiResult.isOK()){
			String sid = (String) apiResult.getValue();
			apiResult = this.getWebLoginUrl(mobile, sid, flag, mailTo, message);
		}
		return apiResult;
	}
	/**
	 * 获取活动单点到邮箱的URL
	 * <p>该方法比安全， 检测用户在线后将自动生成sso使用的临时sid</p>
	 * @see #getWebLoginUrl(String, String, SSOFlag, String, String)
	 * @param userInfo 登录用户信息，mobile和sid必须不为空
	 * @param flag 访问页面类型
	 * @param mailTo 收件人邮件地址。说明：用户写邮件页面，并且自动填入收件人邮件地址（只有Flag.getValue()=1时才有效），如果有多个邮件地址，采用逗号分割
	 * @param message 扩展字段，其他需要包含信息，
	 * @return 单点登录邮箱的URL
	 * @throws ApiException
	 */
	public SSOResult getWebLoginUrl(UserInfo userInfo,SSOFlag flag,String mailTo,String message) throws ApiException{
		SSOResult apiResult = SSOResult.OK;
		if(isOnline(userInfo) == false)
			return SSOResult.OffLine;
		apiResult = this.registerSID(userInfo.getMobile());
		if(apiResult.isOK()){
			String sid = (String) apiResult.getValue();
			apiResult = this.getWebLoginUrl(userInfo.getMobile(), sid, flag, mailTo, message);
		}
		return apiResult;
	}
	/**
	 * 获取活动单点到Wap版邮箱的URL
	 * <br/>	
	 * @param mobile 用户手机号码
	 * @param msisdn 通过 registerSID 获取的用户sid
	 * @param flag 访问页面类型
	 * @param mailTo 收件人邮件地址。说明：用户写邮件页面，并且自动填入收件人邮件地址（只有Flag.getValue()=1时才有效），如果有多个邮件地址，采用逗号分割
	 * @param message 扩展字段，其他需要包含信息，
	 * @return 接口结果 {@link SSOResult}，通过 String ssoUrl = (String) {@link SSOResult#getValue()}; 获取单点登录邮箱的URL
	 * @see #registerSID(String)
	 * @example
<pre>示例：
	yzkf.api.SSO api = new yzkf.api.SSO();
	String mobile = "13900001234";
	SSOResult apiResult = SSOResult.OK;
	try {
		apiResult = api.registerSID(mobile);
	} catch (ApiException e) {
		e.printStackTrace();
	}
	if(apiResult.isOK()){
		String sid = api.sid;
		apiResult = api.getWapLoginUrl(mobile, sid, WapSSOFlag.Other.setValue("20"), "", "");
		if(apiResult.isOK())
			System.out.println(apiResult.getValue());
	}
</pre>
	 */
	public SSOResult getWapLoginUrl(String mobile,String sid,WapSSOFlag flag,String mailTo,String message){
		if(StringUtils.isEmpty(mobile))
			throw new NullPointerException("用户号码不能为空");
		if(StringUtils.isEmpty(sid))
			throw new NullPointerException("用户SID不能为空");
		if(!StringUtils.isEmpty(message)){
			try {
				message = URLEncoder.encode(message,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOG.warn("对单点登录到邮箱的message进行URLEncode时发生字符编码格式错误",e);
			}
		}
		String ssoUrl = HttpClient.prepareURL(this.config.SSO_REGLOGIN_WapLoginUrl, null, 
				"Mobile_No",mobile,
				"Flag",flag.getValue(),
				"MailTo",mailTo,
				"SSOID",sid,
				"Message",message);
		return SSOResult.OK.setValue(ssoUrl);
	}
	/**
	 * 获取活动单点到Wap版邮箱的URL
	 * <p>该方法比安全， 检测用户在线后将自动生成sso使用的临时sid</p>
	 * @see #getWapLoginUrl(String, String, SSOFlag, String, String)
	 * @param userInfo 登录用户信息，mobile和sid必须不为空
	 * @param flag 访问页面类型
	 * @param mailTo 收件人邮件地址。说明：用户写邮件页面，并且自动填入收件人邮件地址（只有Flag.getValue()=1时才有效），如果有多个邮件地址，采用逗号分割
	 * @param message 扩展字段，其他需要包含信息，
	 * @return 单点登录邮箱的URL
	 * @throws ApiException
	 */
	public SSOResult getWapLoginUrl(UserInfo userInfo,WapSSOFlag flag,String mailTo,String message) throws ApiException{
		SSOResult apiResult = SSOResult.OK;
		if(isOnline(userInfo) == false)
			return SSOResult.OffLine;
		apiResult = this.registerSID(userInfo.getMobile());
		if(apiResult.isOK()){
			String sid = (String) apiResult.getValue();
			apiResult = this.getWapLoginUrl(userInfo.getMobile(), sid, flag, mailTo, message);
		}
		return apiResult;
	}
	/**
	 * 判断用户号码在邮箱中是否在线
	 * <p>通过{@link Mail#getMobileBySid(String)} 接口查询{@link UserInfo#getSid()}在邮箱中的号码是否与 {@link UserInfo#getMobile()}相等来判断是否在线</p>
	 * @param userInfo 登录用户的信息，mobile和sid必须不能为空
	 * @return true 在线，false 离线
	 * @throws ApiException
	 */
	public boolean isOnline(UserInfo userInfo) throws ApiException{
		if(userInfo == null || StringUtils.isEmpty(userInfo.getSid())){
			return false;
		}
		Mail mailApi = new Mail();
		MailResult mailApiResult = mailApi.getMobileBySid(userInfo.getSid());
		if(!mailApiResult.isOK()){
			return false;
		}
		if(!mailApiResult.getValue(String.class).equals(userInfo.getMobile())){
			return false;
		}
		return true;
	}
	/**
	 * DES加密
	 * <p>注册sid接口{@link #registerSID(String)}中使用</p>
	 * @param source 源字符串
	 * @param key 密钥
	 * @return
	 */
	private String DesEcbEncrypt(String source,String key){
		try{
			byte[] byteKey = key.getBytes("ASCII");
			byte[] desByteKey = new byte[8];
			for (int i = 0; i < byteKey.length; i++)
	        {
	            if (i > 7)
	                break;
	            desByteKey[i] = byteKey[i];
	        }
	
			Cipher enCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");// 得到加密对象Cipher	     
	        
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			DESKeySpec keySpec = new DESKeySpec(desByteKey);
			SecretKey deskey = keyFactory.generateSecret(keySpec);
			enCipher.init(Cipher.ENCRYPT_MODE, deskey);
			
			byte[] pasByte = enCipher.doFinal(source.getBytes("gb2312"));
			StringBuilder sb = new StringBuilder();
		   for (int i = 0; i < pasByte.length; i++) { 
			   String hex = Integer.toHexString(pasByte[i] & 0xFF); 
			   if (hex.length() == 1)
				   hex = '0' + hex; 
			   sb.append(hex);
		    }
			return sb.toString();
		}catch(Exception e){
			LOG.error("yzkf.api.SSO.DesEcbEncrypt异常", e);
			return source;
		}
	}
	/**
	 * 获取活动单点到活动的URL
	 * @param from 来源运营活动编号
	 * @param to 目标运营活动编号
	 * @param mobile 用户手机号，开头不带86
	 * @param sid 用户登录运营活动获得的虚拟sid
	 * @param timestamp 虚拟sid的有效时间戳，设定为通过userlogin.aspx验证后2个小时内(+5400)，通过SSO登录不改变timestamp值
	 * @return 活动单点到活动的URL
	 */
	public String getAct2ActUrl(String from, String to, String mobile, String sid, long timestamp)
    {
        return getAct2ActUrl(from, to, mobile, sid, timestamp, "");
    }
	/**
	 * 获取活动单点到活动的URL
	 * @param from 来源运营活动编号
	 * @param to 目标运营活动编号
	 * @param mobile 用户手机号，开头不带86
	 * @param sid 用户登录运营活动获得的虚拟sid
	 * @param timestamp 虚拟sid的有效时间戳，设定为通过userlogin.aspx验证后2个小时内(+5400)，通过SSO登录不改变timestamp值
	 * @param flag 标志位
	 * @return 活动单点到活动的URL
	 */
    public String getAct2ActUrl(String from, String to, String mobile, String sid, long timestamp, String flag)
    {
    	String signed = MD5.encode(mobile + sid + timestamp + from + to + this.config.SSO_OPSSO_MD5Key);
        return this.config.SSO_OPSSO_JumpUrl+"?mobile="+mobile
        	+"&sid="+sid
        	+"&timestamp="+timestamp
        	+"&from="+from
        	+"&to="+to
        	+"&skey="+signed
        	+(StringUtils.isEmpty(flag)?"":"&flag="+flag);
    }

    /**
     * 获取Mail单点到活动的跳转url，含flag标志位
     * @param sid 用户登录邮箱获得的真实sid
     * @param to 目标运营活动编号
     * @param flag 标志位
     * @return 邮箱单点到活动的URL
     */
    public String getMail2ActUrl(String sid, String to, String flag)
    {
    	return this.config.SSO_OPSSO_ForMail+"?sid="+sid
    		+"&to="+to
        	+"&flag="+flag;        
    }
    /**
     * 获取活动单点登录到邮箱的url
     * @param account 用户账号
     * @param sid 用户SID
     * @param ssotype 单点登录页面类型，{@link yzkf.enums.SSOType} 枚举
     * @param param 其他参数，不需要使用{@link java.net.URLEncoder#encode}
     * @return 单点登录URL
     * @deprecated 已弃用，使用<code> {@link #getWebLoginUrl}<code> 替代
     */    
    public String getAct2MailUrl(String account,String sid,SSOType ssotype,String param){
    	if(StringUtils.isEmpty(account))
			throw new NullPointerException("用户账号不能为空");
		if(StringUtils.isEmpty(sid))
			throw new NullPointerException("用户SID不能为空");
		if(ssotype == null)
			ssotype = SSOType.Mail;
		//进入账单收件箱需要设置param="id=22"
		if(ssotype == SSOType.Bill){
			param += (StringUtils.isEmpty(param)? "" : "&") + "id=22";
		}
		try {
			param = URLEncoder.encode(param,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.warn("对单点登录到邮箱的param进行URLEncode时发生字符编码格式错误",e);
		}
		String apiURL = this.config.Mail_SSOUrl_BaseUrl + "?" 
				+"?comefrom="+this.config.Mail_SSOUrl_ComeFrom
				+"@account="+account
				+"@flag="+ssotype.getValue()
				+"@sid="+sid
				+"@param="+param;
		return apiURL;
    }
	/**
	 * 获取登录后的标志
	 * @return
	 */
	public String getFlag() {
		return flag;
	}	
}
