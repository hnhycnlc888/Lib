package yzkf.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.fastjson.JSONObject;

import cn.richinfo.core.utils.MD5;
import cn.richinfo.core.utils.web.HttpClient;
import cn.richinfo.core.utils.web.WebUtils;
import yzkf.api.result.MailResult;
import yzkf.app.Pattern;
import yzkf.config.ApiConfig;
import yzkf.config.ConfigFactory;
import yzkf.enums.MailVersion;
import yzkf.enums.NotifyType;
import yzkf.enums.SSOType;
import yzkf.enums.UserType;
import yzkf.exception.ApiException;
import yzkf.exception.ParserConfigException;
import yzkf.model.AdaptInfo;
import yzkf.model.MsInfo;
import yzkf.model.UserInfo;
import yzkf.utils.SocketClient;
import yzkf.utils.TryParse;
import yzkf.utils.Utility;
import yzkf.utils.Xml;

/**
 * 邮箱相关接口处理类
 * @author qiulw
 * @version V4.0.0 2012.02.06
 */
@SuppressWarnings("deprecation")
public class Mail {
	private static final Logger LOG = LoggerFactory.getLogger(Mail.class.getName());
	private ApiConfig config;
	public Mail() throws ParserConfigException{
		ConfigFactory factory = ConfigFactory.getInstance();
		this.config = factory.newApiConfig();
	}
	public Mail(ApiConfig config){
		this.config = config;
	}
	public UserInfo userInfo;
	/***
	 * 获取接口返回的用户信息
	 * @return
	 */
	public UserInfo getUserInfo() {
		return userInfo;
	}
	private final static String INTEGRALINSERT_DATA = "<root><check>{check}</check><method>IntegralInsertRev</method><param><usernumber>{mobile}</usernumber><ruleid>{ruleid}</ruleid><recdate>{recdate}</recdate><multiple>{multiple}</multiple></param></root>";
	private final static String GETINTEGRAL_DATA = "<root><check>{check}</check><method>GetIntegralRev</method><param><usernumber>{mobile}</usernumber></param></root>";
	private final static String INTEGRALUSE_DATA = "<root><check>{check}</check><method>IntegralUseRev</method><param><usernumber>{mobile}</usernumber><ruleid>{ruleid}</ruleid><recdate>{recdate}</recdate></param></root>";
	private final static String INTEGRALSELECT_DATA = "<root><check>{check}</check><method>IntegralSelectRev</method><param><usernumber>{mobile}</usernumber><beginstatedate>{begindate}</beginstatedate><endstatedate>{enddate}</endstatedate></param></root>";
	private final static String INTEGRALSELECTDAY_DATA = "<root><check>{check}</check><method>IntegralSelectDayRev</method><param><usernumber>{mobile}</usernumber><beginstatedate>{begindate}</beginstatedate><endstatedate>{enddate}</endstatedate></param></root>";
	private final static String INTEGRALSELECTUSEDAY_DATA = "<root><check>{check}</check><method>IntegralSelectUseDayRev</method><param><usernumber>{mobile}</usernumber><beginstatedate>{begindate}</beginstatedate><endstatedate>{enddate}</endstatedate></param></root>";
	private final static String QUERYMAILNUM_TDATA = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
	              +"<soapenv:Body><QueryMailNum xmlns=\"http://system.chinamobile.com\"><Version>{Version}</Version><Account></Account><Pass></Pass><UID>{UID}</UID><RequestTime>{Timestamp}</RequestTime></QueryMailNum></soapenv:Body></soapenv:Envelope>";
	private final static String ADAPT_GETPROPVERSION_DATA="<object><int name=\"comefrom\">{0}</int>"
			+"<String name=\"systemSrc\">{1}</String>"
			+"<string name=\" mobileNumber\">{2}</string>"
			+"<string name=\"userAgent\"><![CDATA[ {2} ]]></string>"
			+"<string name=\"clientIp\">{4}</string>"
			+"<int name=\"nettype\">{5}</int>"
			+"<string name=\"host\">{6}</string>"
			+"<string name=\"accept\"> <![CDATA[ {7} ]]></string>"
			+"<String name=\"accept_charset\">{8}</String>"
			+"<String name=\"accept_encoding\">{9}</String>"
			+"<String name=\" Accept-Language\">{10}</String>"
			+"<String name=\" encoding_version\">{11}</string>"
			+"<String name=\"ua_x\"><![CDATA[ {12} ]]></String></object>";
	/**
	 * 获取邮箱版本适配信息
	 * @see #getAdaptInfo(HttpServletRequest, String, boolean)
	 * @param request
	 * @return
	 * @throws ApiException
	 */
	public MailResult getAdaptInfo(HttpServletRequest request) throws ApiException{
		return getAdaptInfo(request, "", false);
	}
	/**
	 * 获取邮箱版本适配信息
	 * @param request Http请求信息
	 * @param mobile 手机号码，可空
	 * @param isWap 是否Wap访问
	 * @return 接口执行结果，如果{@link MailResult#isOK()} == true ，则通过{@link AdaptInfo} adaptInfo = {@link MailResult#getValue(Class) MailResult.getValue(AdaptInfo.class)} 可获取适配结果
	 * @throws ApiException
	 */
	public MailResult getAdaptInfo(HttpServletRequest request,String mobile,boolean isWap) throws ApiException{
		String postData = MessageFormat.format(ADAPT_GETPROPVERSION_DATA, config.Mail_Adapt_Comefrom,isWap?1:2,mobile,request.getHeader("User-Agent"),
				WebUtils.getClientIP(request),isWap?3:4,request.getHeader("Host"),request.getHeader("Accept"),
				request.getHeader("Accept-Charset"),request.getHeader("Accept-Encoding"),request.getHeader("Accept-Language"),
				request.getHeader("Encoding-Version"),request.getHeader("Ua-X"));
		String out = null;
		try {
			out = HttpClient.post(this.config.Mail_Adapt_BaseUrl, postData,this.config.Mail_Adapt_Encoding);
		} catch (IOException e) {
			ApiException ex = new ApiException("适配邮箱版本接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		String outRetCode = "";
		String outRetMsg = "";
		out = out.substring(out.indexOf("{"));
		JSONObject json = JSONObject.parseObject(out);
		outRetCode = json.getString("code");
		outRetMsg = json.getString("msg");
		if(outRetCode.equalsIgnoreCase("S_OK")){
			JSONObject jsonInfo = json.getJSONObject("var");
			AdaptInfo adaptInfo = new AdaptInfo();			
			adaptInfo.setMailVersion(MailVersion.parse(jsonInfo.getString("adaptVer")));
			adaptInfo.setAllowVersion(jsonInfo.getString("allowVersion"));
			adaptInfo.setNetType(jsonInfo.getString("netType"));
			adaptInfo.setScreenSize(jsonInfo.getString("screenSize"));
			adaptInfo.setOs(jsonInfo.getString("os"));
			adaptInfo.setOsVersion(jsonInfo.getString("osVersion"));
			adaptInfo.setBrowser(jsonInfo.getString("browser"));
			adaptInfo.setBrowserVersion(jsonInfo.getString("browserVersion"));
			adaptInfo.setPhoneModel(jsonInfo.getString("model"));
			adaptInfo.setVendor(jsonInfo.getString("vendor"));
			return MailResult.OK.setValue(adaptInfo);
		}else{
			return MailResult.Other.setValue(outRetMsg);
		}
	}
	/**
	 * 获取用户未读邮件数和邮件总数
	 * @see #getMailNum(String)
	 * @param userinfo 用户信息对象，必须包含手机号码或别名
	 * @return 返回结果枚举 {@link MailResult}，如果 {@link MailResult#isOK()} == true，
	 * 则通过  {@link UserInfo} userInfo =   {@link MailResult#getValue(Class) MailResult.getValue(UserInfo.class)}; 可获取接口返回的用户信息对象； 
	 *  {@link UserInfo#getUnreadMailNum() userInfo.getUnreadMailNum()} 获得用户未读邮件数，
	 *  {@link UserInfo#getTotalMailNum() userInfo.getTotalMailNum()} 获得用户邮件总数
	 * @throws ApiException
	 * @example
	 * <pre>
yzkf.api.Mail api = new yzkf.api.Mail();
MailResult result = null;
UserInfo userInfo = new UserInfo();
userInfo.setAlias("lwqiu");
//userInfo.setMobile("13760709457");
try {
	result = api.getMailNum(userInfo);
} catch (ApiException e) {
	e.printStackTrace();
}
if(result.isOK()){
	System.out.println("账号："+userInfo.getAccount());
	System.out.println("未读邮件数："+userInfo.getUnreadMailNum());
	System.out.println("邮件总数："+userInfo.getTotalMailNum());
}else{
	System.out.println("接口执行不成功，返回码："+result.getValue());
}
	 * </pre>
	 */
	public MailResult getMailNum(UserInfo userinfo) throws ApiException{
		if(userinfo == null)
			return MailResult.EmptyMobile;
		this.userInfo = userinfo;
		return getMailNum(userinfo.getAccount());
	}
	/**
	 * 获取用户未读邮件数和邮件总数
	 * @param account 用户别名/手机号
	 * @return 返回结果枚举 {@link MailResult}，如果 {@link MailResult#isOK()} == true，
	 * 则通过  {@link UserInfo} userInfo =   {@link MailResult#getValue(Class) MailResult.getValue(UserInfo.class)}; 可获取接口返回的用户信息对象； 
	 *  {@link UserInfo#getUnreadMailNum() userInfo.getUnreadMailNum()} 获得用户未读邮件数，
	 *  {@link UserInfo#getTotalMailNum() userInfo.getTotalMailNum()} 获得用户邮件总数
	 * @throws ApiException
	 * @example
	 * <pre>
yzkf.api.Mail api = new yzkf.api.Mail();
MailResult result = null;
try {
	result = api.getMailNum("lwqiu");
} catch (ApiException e) {
	e.printStackTrace();
}
if(result.isOK()){
	UserInfo userInfo = result.getValue(UserInfo.class);
	System.out.println("账号："+userInfo.getAccount());
	System.out.println("未读邮件数："+userInfo.getUnreadMailNum());
	System.out.println("邮件总数："+userInfo.getTotalMailNum());
}else{
	System.out.println("接口执行不成功，返回码："+result.getValue());
}
	 * </pre>
	 */
	public MailResult getMailNum(String account) throws ApiException{
		if(StringUtils.isEmpty(account))
			throw new NullPointerException("参数account为空或null值");
		if(!Pattern.isMobile139alias(account))
			return MailResult.InvalidAccount;
		String timestamp ="";
		try {
			timestamp = String.valueOf(Utility.getTimeSpan("1970-01-01",0));
		} catch (ParseException e) {
			LOG.error("时间戳转换失败", e);
		}
		String postData = QUERYMAILNUM_TDATA.replace("{Version}", this.config.Mail_QueryMailNum_Version)
				.replace("{UID}", account).replace("{Timestamp}", timestamp);
		
		String out = null;
		try {
			out = HttpClient.post(this.config.Mail_QueryMailNum_BaseUrl, postData,this.config.Mail_QueryMailNum_Encoding);
		} catch (IOException e) {
			ApiException ex = new ApiException("查询未读邮件数接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		Xml outXml = null;
		try {
			outXml = Xml.parseXml(out);
		}catch(Exception e){
			ApiException ex = new ApiException("查询未读邮件数接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}
		String outRetCode;
		try {
			outRetCode = outXml.evaluate("//ResultCode");
			
			if(outRetCode.endsWith("0")){
				//String outVersion = outXml.evaluate("QueryMailNumResponse/Version");
				String outUID = outXml.evaluate("//UID");
				int outCout = TryParse.toInt(outXml.evaluate("//Count"));
				int outTotalMailNum = TryParse.toInt(outXml.evaluate("//TotalMailNum"));
				setOutUserInfo(outUID);
				if(Pattern.isMobile(outUID))
					userInfo.setMobile(outUID);
				else
					userInfo.setAlias(outUID);
				userInfo.setUnreadMailNum(outCout);
				userInfo.setTotalMailNum(outTotalMailNum);
				return MailResult.OK.setValue(userInfo);
			}
			else if(outRetCode.endsWith("203")){
				return MailResult.InvalidAccount.setValue(outRetCode);		
			}else {
				LOG.error("查询未读邮件数接口返回错误信息："+outRetCode);
//				100	无效的XML
//				200	操作权限错误，请求IP不匹配
//				201	鉴权失败，Account或Pass错误
//				202	版本号错误
//				203	手机号码格式不正确
//				204	请求已失效
//				205	部分请求成功
//				206	数据库操作失败
//				400	错误请求
//				999	未知错误
				return MailResult.ApiFailed.setValue(outRetCode);
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("查询未读邮件数接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}
	}
	/**
	 * 增加用户积分
	 * @see yzkf.api.Mail#integralAdd(String, String, Date, int)
	 * @param mobile 用户手机号码
	 * @param ruleid 积分规则
	 * @return 返回结果枚举 {@link MailResult}
	 * @throws ApiException
	 * @example
	 * <pre>
yzkf.api.Mail api = new yzkf.api.Mail();
MailResult result = null;
try {
	result = api.integralAdd("13900001234","21");
} catch (ApiException e) {
	e.printStackTrace();
}
if(result.isOK()){
	System.out.println("增加积分成功");
}else{
	
}
	 * </pre>
	 */
	public MailResult integralAdd(String mobile,String ruleid) throws ApiException{
		return integralAdd(mobile,ruleid,new Date(),1);
	}
	
	/**
	 * 增加用户积分
	 * @param mobile 用户手机号码
	 * @param ruleid 积分规则
	 * @param recdate 统计的日期（格式为yyyyMMdd），默认当天
	 * @param multiple 操作的次数，默认1
	 * @return 返回结果枚举 {@link MailResult}
	 * @throws ApiException
	 * <pre>
yzkf.api.Mail api = new yzkf.api.Mail();
MailResult result = null;
try {
	result = api.integralAdd("13900001234","21",new Date(),1);
} catch (ApiException e) {
	e.printStackTrace();
}
if(result.isOK()){
	System.out.println("增加积分成功");
}else{
	System.out.println("接口执行不成功，返回码："+result.getValue());
}
	 * </pre>
	 */
	public MailResult integralAdd(String mobile,String ruleid,Date recdate,int multiple) throws ApiException{		
		String checkUUID = UUID.randomUUID().toString();
		
		String strRequestData = INTEGRALINSERT_DATA.replace("{check}", checkUUID)
                .replace("{mobile}", mobile)
                .replace("{ruleid}", ruleid)
                .replace("{recdate}", Utility.formatDate(recdate,"yyyyMMdd"))
                .replace("{multiple}", String.valueOf(multiple));
		String out = null;
		try {
			out = SocketClient.SendString(this.config.Mail_Integral_Host, this.config.Mail_Integral_Port, strRequestData,this.config.Mail_Integral_Encoding);
		} catch (IOException e) {
			ApiException ex = new ApiException("添加用户积分接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		Xml outXml = null;
		try {
			outXml = Xml.parseXml(out);
		}catch(Exception e){
			ApiException ex = new ApiException("添加用户积分接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}
		String outCheck = "";
		String outRetCode = "";
		
		try {
			outCheck = outXml.evaluate("/root/check");
			if(!outCheck.equals(checkUUID))
				throw new ApiException("添加用户积分接口发生串号：返回check码与请求check码不一致");
			outRetCode = outXml.evaluate("/root/result");
			
			if(outRetCode.equals("0")){		
				return MailResult.OK;
			}else{
				//返回未知结果
				return MailResult.Unknow.setValue(outRetCode);
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("添加用户积分接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}
	}
	
	/**
	 * 消费用户积分
	 * @see yzkf.api.Mail#integralSub(String, String, Date)
	 * @param mobile 用户手机号码
	 * @param ruleid 积分规则
	 * @return 返回结果枚举 {@link MailResult}
	 * @throws ApiException
	 * <pre>
yzkf.api.Mail api = new yzkf.api.Mail();
MailResult result = null;
try {
	result = api.integralSub("13900001234","11");
} catch (ApiException e) {
	e.printStackTrace();
}
if(result.isOK()){
	System.out.println("消费积分成功");
}else{
	System.out.println("接口执行不成功，返回码："+result.getValue());
}
	 * </pre>
	 */
	public MailResult integralSub(String mobile,String ruleid) throws ApiException{
		return integralSub(mobile,ruleid,new Date());
	}
	
	/**
	 * 消费用户积分
	 * @param mobile 用户手机号码
	 * @param ruleid 积分规则
	 * @param recdate 统计的日期（格式为yyyyMMdd），默认当天
	 * @return 返回结果枚举 {@link MailResult}
	 * @throws ApiException
	 * <pre>
yzkf.api.Mail api = new yzkf.api.Mail();
MailResult result = null;
try {
	result = api.integralSub("13900001234","11",new Date());
} catch (ApiException e) {
	e.printStackTrace();
}
if(result.isOK()){
	System.out.println("消费积分成功");
}else{
	System.out.println("接口执行不成功，返回码："+result.getValue());
}
	 * </pre>
	 */
	public MailResult integralSub(String mobile,String ruleid,Date recdate) throws ApiException{		
		String checkUUID = UUID.randomUUID().toString();
		
		String strRequestData = INTEGRALUSE_DATA.replace("{check}", checkUUID)
                .replace("{mobile}", mobile)
                .replace("{ruleid}", ruleid)
                .replace("{recdate}", Utility.formatDate(recdate,"yyyyMMdd"));
		String out = null;
		try {
			out = SocketClient.SendString(this.config.Mail_Integral_Host, this.config.Mail_Integral_Port, strRequestData,this.config.Mail_Integral_Encoding);
		} catch (IOException e) {
			ApiException ex = new ApiException("消费用户积分接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		Xml outXml = null;
		try {
			outXml = Xml.parseXml(out);
		}catch(Exception e){
			ApiException ex = new ApiException("消费用户积分接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}
		String outCheck = "";
		String outRetCode = "";
		
		try {
			outCheck = outXml.evaluate("/root/check");
			if(!outCheck.equals(checkUUID))
				throw new ApiException("消费用户积分接口发生串号：返回check码与请求check码不一致");
			outRetCode = outXml.evaluate("/root/result");
			
			if(outRetCode.equals("0")){		
				return MailResult.OK;
			}else{
				//返回未知结果
				return MailResult.Unknow.setValue(outRetCode);
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("消费用户积分接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}
	}
	
	
	/**
	 * 查询用户积分总数
	 * @param mobile 用户手机号码
	 * @return	返回结果枚举 {@link MailResult}，当 {@link MailResult#isOK()} 为 true 时，
	 * 通过 {@link UserInfo} userInfo = {@link MailResult#getValue(Class) MailResult.getValue(UserInfo.class)}; 可获取接口返回的用户信息对象；
	 * {@link UserInfo#getIntegralActive() userInfo.getIntegralActive()} 获得用户当前可以使用的活动积分
	 * @throws ApiException
	 * @example
	 * <pre>
yzkf.api.Mail api = new yzkf.api.Mail();
MailResult result = null;
try {
	result = api.getIntegralTotal("13760709457");
} catch (ApiException e) {
	e.printStackTrace();
}
if(result.isOK()){
	//也可以写成：UserInfo userInfo = api.userInfo;
	UserInfo userInfo = result.getValue(UserInfo.class);
	
	System.out.println("用户当前可以使用的活动积分(产品为特定活动定义)："+userInfo.getIntegralActive());			
	System.out.println("用户已使用积分："+userInfo.getIntegralUse());
	System.out.println("用户可用积分（运营活动中使用该字段）："+userInfo.getIntegralEffect());
	System.out.println("用户等级："+userInfo.getUserLevel());
}else{
	System.out.println("接口执行不成功，返回码："+result.getValue());
}
	 * </pre>
	 */
	public MailResult getIntegralTotal(String mobile) throws ApiException{
		String checkUUID = UUID.randomUUID().toString();
		
		String strRequestData = GETINTEGRAL_DATA.replace("{check}", checkUUID)
                .replace("{mobile}", mobile);
		String out = null;
		try {
			out = SocketClient.SendString(this.config.Mail_Integral_Host, this.config.Mail_Integral_Port, strRequestData,this.config.Mail_Integral_Encoding);
		} catch (IOException e) {
			ApiException ex = new ApiException("查询用户积分接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		Xml outXml = null;
		try {
			outXml = Xml.parseXml(out);
		}catch(Exception e){
			ApiException ex = new ApiException("查询用户积分接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}
		String outCheck = "";
		String outRetCode = "";
		
		try {
			outCheck = outXml.evaluate("/root/check");
			if(!outCheck.equals(checkUUID))
				throw new ApiException("查询用户积分接口发生串号：返回check码与请求check码不一致");
			outRetCode = outXml.evaluate("/root/result");
			
			if(outRetCode.equals("0")){
				int effectIntegral = TryParse.toInt(outXml.evaluate("/root/effectintegra"));	//用户当前可以使用的积分
				String userLevel = outXml.evaluate("/root/userlevel");	//用户等级
				int useIntegral = TryParse.toInt(outXml.evaluate("/root/useintegral"));	//用户已经消耗的积分
				int activeIntegral = TryParse.toInt(outXml.evaluate("/root/activeintegral"));	//用户当前可以使用的活动积分
				setOutUserInfo(mobile);
				userInfo.setIntegralEffect(effectIntegral);
				userInfo.setUserLevel(userLevel);
				userInfo.setIntegralUse(useIntegral);
				userInfo.setIntegralActive(activeIntegral);
				return MailResult.OK.setValue(userInfo);
			}else{
				//返回未知结果
				return MailResult.Unknow.setValue(outRetCode);
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("查询用户积分接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}
	}
	
	/**
	 * 获取积分按天统计明细
	 * @see #getIntegralDaySum(String, String, String)
	 * @param mobile 用户号码
	 * @param begin 开始日期
	 * @param end 结束日期
	 * @return 返回接口执行结果。当 {@link MailResult#isOK()} 为 true 时，
	 * 通过ArrayList<String[]> list = (ArrayList<String[]>) {@link MailResult#getValue()}; 可获取接口返回的明细列表。；
	 * list.get(0)[0] 是日期，list.get(0)[1] 是当日增加积分总和，list.get(0)[2] 是当日消耗积分总和
	 * @throws ApiException
	 * @example
	 * <pre>
yzkf.api.Mail api = new yzkf.api.Mail();
MailResult result = null;
try {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	Date begin = null;
	Date end = null;
	try {
		begin = sdf.parse("20140301");
		end = sdf.parse("20140320");	
	} catch (ParseException e) {
		e.printStackTrace();
	}
	result = api.getIntegralDaySum("13760709457",begin, end);
} catch (ApiException e) {
	e.printStackTrace();
}
if(result.isOK()){
	ArrayList<String[]> list = (ArrayList<String[]>) result.getValue();
	Iterator<String[]> itr = list.iterator();
	while(itr.hasNext()){
		String[] array = itr.next();
		System.out.println("日期(格式：yyyyMMdd)："+array[0]+"，增加积分："+array[1]+"，消耗："+array[2]);
	}
}else{
	System.out.println("接口执行不成功，返回码："+result.getValue());
}
	 * </pre>
	 */
	public MailResult getIntegralDaySum(String mobile,Date begin,Date end) throws ApiException{
		return getIntegralDaySum(mobile,Utility.formatDate(begin, "yyyyMMdd"),Utility.formatDate(end, "yyyyMMdd"));
	}
	
	/**
	 * 获取积分按天统计明细
	 * @param mobile 用户号码
	 * @param begin 开始日期，格式yyyyMMdd
	 * @param end 结束日期，格式yyyyMMdd
	 * @return 返回接口执行结果。当 {@link MailResult#isOK()} 为 true 时，
	 * 通过ArrayList<String[]> list = (ArrayList<String[]>) {@link MailResult#getValue()}; 可获取接口返回的明细列表。；
	 * list.get(0)[0] 是日期，list.get(0)[1] 是当日增加积分总和，list.get(0)[2] 是当日消耗积分总和
	 * @throws ApiException
	 * @example
	 * <pre>
yzkf.api.Mail api = new yzkf.api.Mail();
MailResult result = null;
try {
	result = api.getIntegralDaySum("13760709457","20140301", "20140320");
} catch (ApiException e) {
	e.printStackTrace();
}
if(result.isOK()){
	ArrayList<String[]> list = (ArrayList<String[]>) result.getValue();
	Iterator<String[]> itr = list.iterator();
	while(itr.hasNext()){
		String[] array = itr.next();
		System.out.println("日期(格式：yyyyMMdd)："+array[0]+"，增加积分："+array[1]+"，消耗："+array[2]);
	}
}else{
	System.out.println("接口执行不成功，返回码："+result.getValue());
}
	 * </pre>
	 */
	public MailResult getIntegralDaySum(String mobile,String begin,String end) throws ApiException{
		String checkUUID = UUID.randomUUID().toString();
		
		String strRequestData = INTEGRALSELECT_DATA.replace("{check}", checkUUID)
                .replace("{mobile}", mobile)
                .replace("{begindate}", begin)
                .replace("{enddate}", end);
		String out = null;
		try {
			out = SocketClient.SendString(this.config.Mail_Integral_Host, this.config.Mail_Integral_Port, strRequestData,this.config.Mail_Integral_Encoding);
		} catch (IOException e) {
			ApiException ex = new ApiException("查询用户积分按日统计接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		Xml outXml = null;
		try {
			outXml = Xml.parseXml(out);
		}catch(Exception e){
			ApiException ex = new ApiException("查询用户积分按日统计解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}
		String outCheck = "";
		String outRetCode = "";
		
		try {
			outCheck = outXml.evaluate("/root/check");
			if(!outCheck.equals(checkUUID))
				throw new ApiException("查询用户积分按日统计发生串号：返回check码与请求check码不一致");
			outRetCode = outXml.evaluate("/root/result");
			
			if(outRetCode.equals("0")){
				ArrayList<String[]> list = new ArrayList<String[]>();
				
				NodeList items = (NodeList) outXml.evaluate("/root/info/item", XPathConstants.NODESET);
				for(int i=0;i<items.getLength();i++)
				{
					String[] array = new String[3];
					Node node = items.item(i);
					NodeList childNodes = node.getChildNodes();
					
					for(int j=0;j<childNodes.getLength();j++)
					{
						Node childNode = childNodes.item(j);
						if(childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equals("statedate"))
							array[0]=childNode.getFirstChild().getNodeValue();
						if(childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equals("addcount"))
							array[1]=childNode.getFirstChild().getNodeValue();
						if(childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equals("subcount"))
							array[2]=childNode.getFirstChild().getNodeValue();
					}			
					list.add(array);
				}
				
				return MailResult.OK.setValue(list);
			}else{
				//返回未知结果
				return MailResult.Unknow.setValue(outRetCode);
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("查询用户积分按日统计获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}
	}
	
	/**
	 * 查询用户积分增加明细
	 * @param mobile 用户号码
	 * @param begin 开始日期，格式yyyyMMdd
	 * @param end 结束日期，格式yyyyMMdd
	 * @return 返回接口执行结果。当 {@link MailResult#isOK()} 为 true 时，
	 * 通过ArrayList<String[]> list = (ArrayList<String[]>) {@link MailResult#getValue()}; 可获取接口返回的明细列表。；
	 * list.get(0)[0] 是日期，list.get(0)[1] 是积分规则编号，list.get(0)[2] 是积分规则名称，list.get(0)[3] 是增加的积分数
	 * @throws ApiException
	 * @example
	 * <pre>
yzkf.api.Mail api = new yzkf.api.Mail();
MailResult result = null;
try {
	result = api.getIntegralAddDetail("13760709457","20140301", "20140320");
} catch (ApiException e) {
	e.printStackTrace();
}
if(result.isOK()){
	ArrayList<String[]> list = (ArrayList<String[]>) result.getValue();
	Iterator<String[]> itr = list.iterator();
	while(itr.hasNext()){
		String[] array = itr.next();
		System.out.println("日期(格式：yyyyMMdd)："+array[0]+"，规则编号："+array[1]+"，规则名称："+array[2]+"，增加积分数："+array[3]);
	}
}else{
	System.out.println("接口执行不成功，返回码："+result.getValue());
}
	 * </pre>
	 */
	public MailResult getIntegralAddDetail(String mobile,String begin,String end) throws ApiException{
		String checkUUID = UUID.randomUUID().toString();
		
		String strRequestData = INTEGRALSELECTDAY_DATA.replace("{check}", checkUUID)
                .replace("{mobile}", mobile)
                .replace("{begindate}", begin)
                .replace("{enddate}", end);
		String out = null;
		try {
			out = SocketClient.SendString(this.config.Mail_Integral_Host, this.config.Mail_Integral_Port, strRequestData,this.config.Mail_Integral_Encoding);
		} catch (IOException e) {
			ApiException ex = new ApiException("查询用户积分增加明细接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		Xml outXml = null;
		try {
			outXml = Xml.parseXml(out);
		}catch(Exception e){
			ApiException ex = new ApiException("查询用户积分增加明细接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}
		String outCheck = "";
		String outRetCode = "";
		
		try {
			outCheck = outXml.evaluate("/root/check");
			if(!outCheck.equals(checkUUID))
				throw new ApiException("查询用户积分增加明细接口发生串号：返回check码与请求check码不一致");
			outRetCode = outXml.evaluate("/root/result");
			
			if(outRetCode.equals("0")){
				ArrayList<String[]> list = new ArrayList<String[]>();
				
				NodeList items = (NodeList) outXml.evaluate("/root/info/item", XPathConstants.NODESET);
				for(int i=0;i<items.getLength();i++)
				{
					String[] array = new String[4];
					Node node = items.item(i);
					NodeList childNodes = node.getChildNodes();
					
					for(int j=0;j<childNodes.getLength();j++)
					{
						Node childNode = childNodes.item(j);
						if(childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equals("statedate"))
							array[0]=childNode.getFirstChild().getNodeValue();
						if(childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equals("ruleid"))
							array[1]=childNode.getFirstChild().getNodeValue();
						if(childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equals("rulename"))
							array[2]=childNode.getFirstChild().getNodeValue();
						if(childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equals("totalcount"))
							array[3]=childNode.getFirstChild().getNodeValue();
					}			
					list.add(array);
				}
				
				return MailResult.OK.setValue(list);
			}else{
				//返回未知结果
				return MailResult.Unknow.setValue(outRetCode);
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("查询用户积分增加明细接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}
	}
	
	/**
	 * 查询用户积分消耗明细
	 * @param mobile 用户号码
	 * @param begin 开始日期，格式yyyyMMdd
	 * @param end 结束日期，格式yyyyMMdd
	 * @return 返回接口执行结果。当 {@link MailResult#isOK()} 为 true 时，
	 * 通过ArrayList<String[]> list = (ArrayList<String[]>) {@link MailResult#getValue()}; 可获取接口返回的明细列表。；
	 * list.get(0)[0] 是日期，list.get(0)[1] 是积分规则编号，list.get(0)[2] 是积分规则名称，list.get(0)[3] 是消耗的积分数
	 * @throws ApiException
	 * @example
	 * <pre>
yzkf.api.Mail api = new yzkf.api.Mail();
MailResult result = null;
try {
	result = api.getIntegralSubDetail("13760709457","20140301", "20140320");
} catch (ApiException e) {
	e.printStackTrace();
}
if(result.isOK()){
	ArrayList<String[]> list = (ArrayList<String[]>) result.getValue();
	Iterator<String[]> itr = list.iterator();
	while(itr.hasNext()){
		String[] array = itr.next();
		System.out.println("日期(格式：yyyyMMdd)："+array[0]+"，规则编号："+array[1]+"，规则名称："+array[2]+"，消耗积分数："+array[3]);
	}
}else{
	System.out.println("接口执行不成功，返回码："+result.getValue());
}
	 * </pre>
	 */
	public MailResult getIntegralSubDetail(String mobile,String begin,String end) throws ApiException{
		String checkUUID = UUID.randomUUID().toString();
		
		String strRequestData = INTEGRALSELECTUSEDAY_DATA.replace("{check}", checkUUID)
                .replace("{mobile}", mobile)
                .replace("{begindate}", begin)
                .replace("{enddate}", end);
		String out = null;
		try {
			out = SocketClient.SendString(this.config.Mail_Integral_Host, this.config.Mail_Integral_Port, strRequestData,this.config.Mail_Integral_Encoding);
		} catch (IOException e) {
			ApiException ex = new ApiException("查询用户积分消耗明细接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		Xml outXml = null;
		try {
			outXml = Xml.parseXml(out);
		}catch(Exception e){
			ApiException ex = new ApiException("查询用户积分消耗明细接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}
		String outCheck = "";
		String outRetCode = "";
		
		try {
			outCheck = outXml.evaluate("/root/check");
			if(!outCheck.equals(checkUUID))
				throw new ApiException("查询用户积分消耗明细接口发生串号：返回check码与请求check码不一致");
			outRetCode = outXml.evaluate("/root/result");
			
			if(outRetCode.equals("0")){
				ArrayList<String[]> list = new ArrayList<String[]>();
				
				NodeList items = (NodeList) outXml.evaluate("/root/info/item", XPathConstants.NODESET);
				for(int i=0;i<items.getLength();i++)
				{
					String[] array = new String[4];
					Node node = items.item(i);
					NodeList childNodes = node.getChildNodes();
					
					for(int j=0;j<childNodes.getLength();j++)
					{
						Node childNode = childNodes.item(j);
						if(childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equals("statedate"))
							array[0]=childNode.getFirstChild().getNodeValue();
						if(childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equals("ruleid"))
							array[1]=childNode.getFirstChild().getNodeValue();
						if(childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equals("rulename"))
							array[2]=childNode.getFirstChild().getNodeValue();
						if(childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equals("totalcount"))
							array[3]=childNode.getFirstChild().getNodeValue();
					}			
					list.add(array);
				}
				
				return MailResult.OK.setValue(list);
			}else{
				//返回未知结果
				return MailResult.Unknow.setValue(outRetCode);
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("查询用户积分消耗明细接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}
	}
	
	/**
	 * Web登录验证
	 * 
	 * @param account 用户手机号码或者别名（不包含@139.com）
	 * @param password 用户密码
	 * @return 登录验证结果枚举值MailEnum
	 * @throws ApiException 接口执行异常
	 * @throws ParserConfigException 读取配置文件异常
	 * @deprecated 已弃用，使用 {@link yzkf.api.Disk#validateLogin(account, password, clientIP)} 替代
	 * @see {@link yzkf.api.Disk#validateLogin(account, password, clientIP)}
	 */
	public MailResult validateLogin(String account,String password) throws ApiException, ParserConfigException{		
		if(StringUtils.isEmpty(account))
			return MailResult.EmptyAccount;	//空账户
		if(StringUtils.isEmpty(password))
			return MailResult.EmptyPassword;	//空密码
		
		if(!Pattern.isMobile139alias(account))
			return MailResult.InvalidAccount;	//无效账号
		if(!Pattern.isMailpassword(password))
			return MailResult.InvalidPassword;	//无效错误	
		
		String comefrom = this.config.Mail_ValidateLogin_ComeFrom;
		String timestamp = String.valueOf(Utility.getTimeSpan(this.config.Mail_ValidateLogin_TimeExpires));
		String skey = MD5.encode(comefrom+account+password+timestamp+this.config.Mail_ValidateLogin_MD5Key);
		
		try {
			account=URLEncoder.encode(account, "UTF-8");
			password=URLEncoder.encode(password, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			LOG.warn("对用户账号或密码进行URLEncode时发生字符编码格式错误",e1);
		}		
		String apiURL = HttpClient.prepareURL(this.config.Mail_ValidateLogin_BaseURL, null, 
						"comefrom",comefrom,
						"account",account,
						"passwd",password,
						"timestamp",timestamp,
						"skey",skey);
//		String apiURL = prepareURL(this.config.Mail_ValidateLogin_BaseURL, this.config.Mail_ValidateLogin_MD5Key, 
//				"comefrom",this.config.Mail_ValidateLogin_ComeFrom,
//				"account",account,
//				"passwd",password,
//				"timestamp",String.valueOf(Utility.getTimeSpan(this.config.Mail_ValidateLogin_TimeExpires)),
//				"skey");
		String out = null;
		try {
			out = HttpClient.get(apiURL);
		} catch (IOException e) {
			ApiException ex = new ApiException("Mail登录验证时发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		 //account  ||   returncode  ||  timestamp  ||  skey  ||  SID
        String[] arrayOut = out.split("\\|\\|");

        String out_Account = "";//保存返回时的帐号
        String out_ReurnCode = "";//保存返回码
        long out_TimeStamp = 0;//保存返回时的时间戳
        String out_Skey = "";//保存返回的sKey
        String out_sid = "";//保存返回的SID

        //如果返回的参数个数不等于4也不等于5则表示返回的参数错误
        if(arrayOut.length <4 || arrayOut.length > 5)
        	throw new ApiException("Mail登录接口返回参数解析错误，返回值："+out);

    	out_Account = arrayOut[0].trim();
    	out_ReurnCode = arrayOut[1].trim();
    	out_TimeStamp = Long.parseLong(arrayOut[2].trim());
    	out_Skey = arrayOut[3].trim();
    	if(arrayOut.length == 5)
    		out_sid = arrayOut[4].trim();
        //数字签名，MD5（account+ returncode +timestamp+key+SID），输出32位全大写字母，其中key是约定的密匙
    	String out_Signed = MD5.encode(out_Account+out_ReurnCode+out_TimeStamp+this.config.Mail_ValidateLogin_MD5Key+out_sid);
    	

        //判断数字签名是否正确
        if (!out_Signed.equalsIgnoreCase(out_Skey))
            throw new ApiException("Mail登录接口返回数据签名错误，返回值："+out);

        //判断时间戳是否过期
        if (Utility.getTimeSpan(0) > out_TimeStamp)
        	throw new ApiException("Mail登录接口超时");
        //返回账号和登录账号是否相符
        if(!out_Account.equalsIgnoreCase(account))
        	throw new ApiException("Mail登录接口异常，登录账号和返回账户不相符");
        //根据接口返回值，返回响应结果
	    if(out_ReurnCode.equals("00")){
	    	userInfo = new UserInfo();
	    	if(Pattern.isMobile(account))
	    		userInfo.setMobile(account);
	    	else
	    		userInfo.setAlias(account);
	    	userInfo.setSid(out_sid);
	        return MailResult.OK.setValue(userInfo);
	    }
	    if(out_ReurnCode.equals("01"))
	            return MailResult.Expires;
	    if(out_ReurnCode.equals("02"))
	            throw new ApiException("Mail登录接口返回错误信息：skey无效");
	    if(out_ReurnCode.equals("03"))
	        	throw new ApiException("Mail登录接口返回错误信息：IP地址被拒绝");
	    if(out_ReurnCode.equals("04"))
	            return MailResult.WrongPassword;
	    if(out_ReurnCode.equals("05"))
	            return MailResult.Frozen;
	    if(out_ReurnCode.equals("06"))
	            return MailResult.Inexistent;
	    if(out_ReurnCode.equals("07"))
	        	throw new ApiException("Mail登录接口返回错误信息：请求来源非法，不是指定的来源");
	    if(out_ReurnCode.equals("08"))
	        	throw new ApiException("Mail登录接口返回错误信息：请求过来的参数不完整");
	    if(out_ReurnCode.equals("09"))
	            return MailResult.Cancelled;
	    if(out_ReurnCode.equals("99"))
	        	throw new ApiException("Mail登录接口返回错误信息：未知错误");
	    else
	        	throw new ApiException("Mail登录接口返回未解析的结果："+out);        
	}
	/***
	 * 创建单点登录到邮箱的URL
	 * <br/>使用 yzkf.api.SSO 的 getAct2MailUrl 方法
	 * @param account 用户账号
	 * @param sid 用户SID
	 * @param ssotype 单点登录页面类型，yzkf.enums.SSOType枚举，如：
	 * @param param 其他参数，不需要使用URLEncoder.encode
	 * @return 单点登录URL
	 * @throws ParserConfigException 
	 * @deprecated 已弃用，使用<code> {@link yzkf.api.SSO#getWebLoginUrl}<code> 替代
	 */
	public String getSSOUrl(String account,String sid,SSOType ssotype,String param) throws ParserConfigException{
		SSO sso = new SSO(this.config);
		return sso.getAct2MailUrl(account, sid, ssotype, param);
	}
	/**
	 * 获取用户手机品牌
	 * @param userinfo 用户信息对象，至少包含属性mobile，API执行成功将保留或更新已有属性
	 * @return 返回接口执行结果MailEnum，使用getUserInfo().getUserType()获取手机品牌
	 * @throws ParserConfigException 配置文件读取异常
	 * @throws ApiException 接口执行异常
	 */
	public MailResult getCardTypeInfo(UserInfo userinfo) throws ParserConfigException, ApiException{
		if(userinfo == null)
			return MailResult.EmptyMobile;
		this.userInfo = userinfo;
		return getCardTypeInfo(userinfo.getMobile());
	}
	/**
	 * 获取用户手机品牌
	 * @param mobile 用户手机号码(不带86)，如：13912345678
	 * @return 返回接口执行结果MailEnum，使用getUserInfo().getUserType()获取手机品牌
	 * @throws ParserConfigException 配置文件读取异常
	 * @throws ApiException 接口执行异常
	 * @example
	 * <pre>
Mail api = new Mail();
try {
	MailResult result = api.getCardTypeInfo("15978053984");
	if(result.isOK()){
		UserInfo info = result.getValue(UserInfo.class);
		System.out.print("用户省份编号："+ info.getProvCode() );
	}else{
		System.out.println("查询用户号码属性失败："+result.getDescr());
	}
} catch (ApiException e) {
	System.out.println("接口返回异常："+e.getMessage());
}
	 * </pre>
	 */
	public MailResult getCardTypeInfo(String mobile) throws ParserConfigException, ApiException{
		if(StringUtils.isEmpty(mobile))
			return MailResult.EmptyMobile;	//空号码
		if(!Pattern.isMobile(mobile))
			return MailResult.InvalidMobile;	//无效手机号
		long timeStamp = Utility.getTimeSpan(this.config.Mail_CardType_TimeExpires);
		String signed = MD5.encode(mobile
				+this.config.Mail_CardType_ComeFrom
				+String.valueOf(timeStamp)
				+this.config.Mail_CardType_MD5Key);
		String postData = "<?xml version=\"1.0\" encoding=\"GB2312\"?>\n"
			+"<RequestData>\n"
			+"<Mobile>"+mobile+"</Mobile>\n"
			+"<ComeFrom>"+this.config.Mail_CardType_ComeFrom+"</ComeFrom>\n"
			+"<TimeStamp>"+String.valueOf(timeStamp)+"</TimeStamp>\n"
			+"<Skey>"+signed+"</Skey>\n"
			+"</RequestData>";
		String out = null;
		try {
			out = HttpClient.post(this.config.Mail_CardType_BaseUrl, postData,this.config.Mail_CardType_Encoding);
		} catch (IOException e) {
			ApiException ex = new ApiException("获取用户手机品牌接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		Xml outXml = null;
		try {
			outXml = Xml.parseXml(out);
		}catch(Exception e){
			ApiException ex = new ApiException("获取用户手机品牌接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}
		String outRetCode = "";
		try {
			String outMobile = outXml.evaluate("/ResponseData/Mobile");			
			String outProvDesc = outXml.evaluate("/ResponseData/ProvDesc");
			String outProvcode = outXml.evaluate("/ResponseData/ProvCode");
			String outAreaCode = outXml.evaluate("/ResponseData/AreaCode");
			String outAreaDesc = outXml.evaluate("/ResponseData/AreaDesc");
			String outCardType = outXml.evaluate("/ResponseData/CardType");	
			outRetCode = outXml.evaluate("/ResponseData/RetCode");
			String outTimeStamp = outXml.evaluate("/ResponseData/TimeStamp");
			String outSkey = outXml.evaluate("/ResponseData/Skey");
			
			String outSigned = MD5.encode(outMobile+outProvcode+outAreaCode+outRetCode+outTimeStamp+this.config.Mail_CardType_MD5Key);
			//判断数字签名是否正确
	        if (!outSigned.equalsIgnoreCase(outSkey))
	            throw new ApiException("获取用户手机品牌接口返回数据签名错误，返回值："+out);
	
	        //判断时间戳是否过期
	        if (Utility.getTimeSpan(0) > Long.parseLong(outTimeStamp))
	        	throw new ApiException("获取用户手机品牌接口超时");
	        //返回账号和登录账号是否相符
	        if(!outMobile.equalsIgnoreCase(mobile))
	        	throw new ApiException("获取用户手机品牌接口异常，登录账号和返回账户不相符");
	        /// [000]获取用户属性成功
			if(outRetCode.equals("000")){				
				setOutUserInfo(outMobile);
				userInfo.setMobile(outMobile);
				userInfo.setProvCode(TryParse.toInt(outProvcode));
				userInfo.setAreaCode(TryParse.toInt(outAreaCode));
				userInfo.setProvDesc(outProvDesc);
				userInfo.setAreaDesc(outAreaDesc);
				userInfo.setCardType(outCardType);	
				return MailResult.OK.setValue(userInfo);
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("获取用户手机品牌接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}
        /// [001]未查询到相关记录
        if(outRetCode.equals("001"))
        	return MailResult.Inexistent;
       return convertResult(outRetCode);
	}
	/**
	 * 获取用户属性，执行成功后使用 getUserInfo()获得用户信息
	 * @param userinfo 用户信息对象，必须包含属性mobile或alias其中之一，API执行成功将保留或更新已有属性
	 * @return 返回接口执行结果，使用getUserInfo()获得用户信息
	 * @throws ParserConfigException 配置文件读取异常
	 * @throws ApiException 接口执行异常
	 */
	public MailResult getAttribute(UserInfo userinfo) throws ParserConfigException, ApiException{
		if(userinfo == null)
			return MailResult.EmptyAccount;
		this.userInfo = userinfo;
		return getAttribute(userinfo.getAccount());
	}
	/**
	 * 获取用户属性，执行成功后使用 getUserInfo()获得用户信息
	 * @param account 用户手机号(不带86)或别名
	 * @return 返回接口执行结果，使用getUserInfo()获得用户信息
	 * @throws ParserConfigException 配置文件读取异常
	 * @throws ApiException 接口执行异常
	 */
	public MailResult getAttribute(String account) throws ParserConfigException, ApiException{
		if(StringUtils.isEmpty(account))
			return MailResult.EmptyAccount;	//空账户
		if(!Pattern.isMobile139alias(account))
			return MailResult.InvalidAccount;	//无效账号
		long timeStamp = Utility.getTimeSpan(this.config.Mail_Attribute_TimeExpires);
		String signed = MD5.encode(account
				+this.config.Mail_Attribute_ComeFrom
				+String.valueOf(timeStamp)
				+this.config.Mail_Attribute_MD5Key);
		String postData = "<?xml version=\"1.0\" encoding=\"GB2312\"?>\n"
			+"<RequestData>\n"
			+"<Account>"+account+"</Account>\n"
			+"<ComeFrom>"+this.config.Mail_Attribute_ComeFrom+"</ComeFrom>\n"
			+"<TimeStamp>"+String.valueOf(timeStamp)+"</TimeStamp>\n"
			+"<Skey>"+signed+"</Skey>\n"
			+"</RequestData>";
		String out = null;
		try {
			out = HttpClient.post(this.config.Mail_Attribute_BaseUrl, postData,this.config.Mail_Attribute_Encoding);
		} catch (IOException e) {
			ApiException ex = new ApiException("获取属性接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		Xml outXml = null;
		try {
			outXml = Xml.parseXml(out);
		}catch(Exception e){
			ApiException ex = new ApiException("获取属性接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}
		String outRetCode = "";
		try {
			String outAccount = outXml.evaluate("/ResponseData/Account");		
			String outUserMobile = outXml.evaluate("/ResponseData/UserMobile");
			String outAliasName = outXml.evaluate("/ResponseData/AliasName");
			String outCardType = outXml.evaluate("/ResponseData/CardType");
			String outProvcode = outXml.evaluate("/ResponseData/Provcode");
			String outAreaCode = outXml.evaluate("/ResponseData/AreaCode");
			String outUserType = outXml.evaluate("/ResponseData/UserType");
			String outOrderType = outXml.evaluate("/ResponseData/OrderType");
			String outOrderStatus = outXml.evaluate("/ResponseData/OrderStatus");
			outRetCode = outXml.evaluate("/ResponseData/RetCode");
			String outTimeStamp = outXml.evaluate("/ResponseData/TimeStamp");
			String outSkey = outXml.evaluate("/ResponseData/Skey");
			
			String outSigned = MD5.encode(outAccount+outUserMobile+outAliasName+outCardType+outProvcode+outAreaCode+outUserType
					+outOrderType+outOrderStatus+outRetCode+outTimeStamp+this.config.Mail_Attribute_MD5Key);
			//判断数字签名是否正确
	        if (!outSigned.equalsIgnoreCase(outSkey))
	            throw new ApiException("获取属性接口返回数据签名错误，返回值："+out);	
	        //判断时间戳是否过期
	        if (Utility.getTimeSpan(0) > Long.parseLong(outTimeStamp))
	        	throw new ApiException("获取属性接口超时");
	        //返回账号和登录账号是否相符
	        if(!outAccount.equalsIgnoreCase(account))
	        	throw new ApiException("获取属性接口异常，登录账号和返回账户不相符");
	        /// [000]获取用户属性成功
			if(outRetCode.equals("000")){
				String outLastOpenTime = outXml.evaluate("/ResponseData/LastOpenTime");
				String outCancelDate = outXml.evaluate("/ResponseData/CancelDate");
				String outCancelType = outXml.evaluate("/ResponseData/CancelType");		
				String outPresentSMS = outXml.evaluate("/ResponseData/PresentSMS");
				String outPresentMMS = outXml.evaluate("/ResponseData/PresentMMS");
				String outPresentNMS = outXml.evaluate("/ResponseData/PresentNMS");
				String outPresentFax = outXml.evaluate("/ResponseData/PresentFax");
				String outServiceCode = outXml.evaluate("/ResponseData/ServiceCode");
				String outBindTypeId = outXml.evaluate("/ResponseData/BindTypeId");

				setOutUserInfo(outUserMobile,outAliasName);
				userInfo.setMobile(outUserMobile);
				if(!StringUtils.isEmpty(outAliasName))
					userInfo.setAlias(outAliasName);
				else{
					if(!Pattern.isMobile(outAccount))
						userInfo.setAlias(outAccount);
				}
				userInfo.setProvCode(TryParse.toInt(outProvcode));
				userInfo.setAreaCode(TryParse.toInt(outAreaCode));
				userInfo.setBindTypeId(outBindTypeId);
				userInfo.setCancelDate(outCancelDate);
				userInfo.setCancelType(outCancelType);
				userInfo.setCardType(outCardType);				
				
				Date lastOpenTime;
				try {
					lastOpenTime = DateFormat.getDateTimeInstance().parse(outLastOpenTime);					
				} catch (ParseException e) {
					try {
						DateFormat df = new SimpleDateFormat("M/d/yyyy hh:mm:ss a",Locale.US);
						lastOpenTime = df.parse(outLastOpenTime);
					} catch (ParseException e1) {
						lastOpenTime = new Date();
						LOG.debug("Mail_Attribute接口返回最后登录时间转换失败："+outLastOpenTime, e1);
					}
					//LOG.debug("Mail_Attribute接口返回最后登录时间第一次转换失败："+outLastOpenTime, e);
				}
				userInfo.setLastOpenTime(lastOpenTime);
				
				userInfo.setOrderStatus(outOrderStatus);
				userInfo.setOrderType(outOrderType);
				userInfo.setPresentFax(Integer.parseInt(outPresentFax));
				userInfo.setPresentMMS(Integer.parseInt(outPresentMMS));
				userInfo.setPresentNMS(Integer.parseInt(outPresentNMS));
				userInfo.setPresentSMS(Integer.parseInt(outPresentSMS));				
				userInfo.setServiceCode(outServiceCode);
				userInfo.setUserType(UserType.parse(outUserType));
				if(userInfo.getOrderStatus().equals("1"))
					return MailResult.Cancelled;
				if(userInfo.getOrderStatus().equals("3"))
					return MailResult.Frozen;
				return MailResult.OK.setValue(userInfo);
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("获取用户属性接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}
		return convertResult(outRetCode);
	}
	/**
	 * 注册邮箱
	 * @param mobile 户邮箱注册的手机号码(不带86)，如：13912345678
	 * @param areaCode 城市编号，对应系统表sps_Areacode中的AreaCode
	 * @param serviceCode 业务代码，开通的邮箱类型广东:0010-无限、免费，其它:0015-2G、免费
	 * @param bindTypeId 活动号,用户订购业务绑定的活动号，默认为0。115-广东，3504-云南，4944-广西，11804-上海，12044-山东，13004-北京，13244-辽宁，13245-福建
	 * @return 注册结果MailEnum
	 * @throws ParserConfigException 配置文件读取异常
	 * @throws ApiException 接口执行异常
	 */
	public MailResult register(String mobile,int areaCode,String serviceCode,String bindTypeId) throws ParserConfigException, ApiException{
		if(StringUtils.isEmpty(mobile))
			return MailResult.EmptyMobile;	//空号码
		if(!Pattern.isMobile(mobile))
			return MailResult.InvalidMobile;	//无效号码
		if(areaCode == 0)
			throw new NullPointerException("参数areaCode为空或null值");
		if(StringUtils.isEmpty(serviceCode))
			throw new NullPointerException("参数serviceCode为空或null值");
		if(StringUtils.isEmpty(bindTypeId))
			bindTypeId = "0";
		long timeStamp = Utility.getTimeSpan(this.config.Mail_Register_TimeExpires);
		String signed = MD5.encode(areaCode
				+mobile
				+serviceCode
				+bindTypeId
				+this.config.Mail_Register_ComeFrom
				+String.valueOf(timeStamp)
				+this.config.Mail_Register_MD5Key);
		String postData = "<?xml version=\"1.0\" encoding=\"GB2312\"?>\n"
			+"<RequestData>\n"
			+"<CityID>"+areaCode+"</CityID>\n"
			+"<Mobile>"+mobile+"</Mobile>\n"
			+"<ServiceCode>"+serviceCode+"</ServiceCode>\n"
			+"<BindTypeId>"+bindTypeId+"</BindTypeId>\n"
			+"<ComeFrom>"+this.config.Mail_Register_ComeFrom+"</ComeFrom>\n"
			+"<TimeStamp>"+String.valueOf(timeStamp)+"</TimeStamp>\n"
			+"<Skey>"+signed+"</Skey>\n"
			+"</RequestData>";
		String out = null;
		try {
			out = HttpClient.post(this.config.Mail_Register_BaseUrl, postData,this.config.Mail_Register_Encoding);
		} catch (IOException e) {
			ApiException ex = new ApiException("注册邮箱接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		Xml outXml = null;
		try {
			outXml = Xml.parseXml(out);
		}catch(Exception e){
			ApiException ex = new ApiException("注册邮箱接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}
		String outRetCode = "";
		try {
			String outMobile = outXml.evaluate("/ResponseData/Mobile");				
			String outServiceCode = outXml.evaluate("/ResponseData/ServiceCode");	
			outRetCode = outXml.evaluate("/ResponseData/RetCode");
			String outTimeStamp = outXml.evaluate("/ResponseData/TimeStamp");
			String outSkey = outXml.evaluate("/ResponseData/Skey");
			
			String outSigned = MD5.encode(outMobile+outServiceCode+outRetCode+outTimeStamp+this.config.Mail_Register_MD5Key);
			//判断数字签名是否正确
	        if (!outSigned.equalsIgnoreCase(outSkey))
	            throw new ApiException("注册邮箱接口返回数据签名错误，返回值："+out);
	
	        //判断时间戳是否过期
	        if (Utility.getTimeSpan(0) > Long.parseLong(outTimeStamp))
	        	throw new ApiException("注册邮箱接口超时");
	        //返回账号和登录账号是否相符
	        if(!outMobile.equalsIgnoreCase(mobile))
	        	throw new ApiException("注册邮箱接口异常，登录账号和返回账户不相符");
	        /// [000]获取用户属性成功
			if(outRetCode.equals("000")){
				return MailResult.OK;
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("注册邮箱接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}
		/// [001]用户已存在
		if(outRetCode.equals("002"))
			return MailResult.ExistsYet;
		/// [002]开户失败
        if(outRetCode.equals("002"))
        	return MailResult.RegisterFailed;
        else
        	return convertResult(outRetCode);
	}
	/**
	 * 注册邮箱
	 * @param mobile 户邮箱注册的手机号码(不带86)，如：13912345678
	 * @param bindTypeId 活动号,用户订购业务绑定的活动号，默认为0。115-广东，3504-云南，4944-广西，11804-上海，12044-山东，13004-北京，13244-辽宁，13245-福建
	 * @return 注册结果MailEnum
	 * @throws ParserConfigException 配置文件读取异常
	 * @throws ApiException 接口执行异常
	 */
	public MailResult register(String mobile,String bindTypeId) throws ParserConfigException, ApiException{
		MailResult result = getCardTypeInfo(mobile);
		if(!result.isOK()){
			return result;
		}
		result = register(mobile, this.getUserInfo().getAreaCode(), this.getUserInfo().getProvCode() == 1 ? "0010" : "0015", bindTypeId);
		return result;
	}
	/**
	 * 注册邮箱，使用默认的活动号bindTypeId=“0”
	 * @param mobile 户邮箱注册的手机号码(不带86)，如：13912345678
	 * @return 注册结果MailApiResult
	 * @throws ParserConfigException 配置文件读取异常
	 * @throws ApiException 接口执行异常
	 */
	public MailResult register(String mobile) throws ParserConfigException, ApiException{
		return register(mobile,"0");
	}
	/**
	 * 通过SessionKey查询在线用户号码
	 * @param sid 用户访问的SessionKey值（36位），第三方请求的SessionKey，和请求参数不一样，该sid用于userloginurl.aspx单点登录
	 * @return 返回接口执行结果 {@link MailResult}，如果 {@link MailResult#isOK()} == true，则可以通过 {@link MailResult#getValue()}得到用户的手机号码
	 * @throws ApiException 接口执行异常
	 * @example
	 * <pre>
Mail api = new Mail();
MailResult result;
try {
	result = api.getMobileBySid(sid);
	if(result.isOK()){
		System.out.println("用户在线，号码是："+result.getValue(String.class));
	}else if(result == MailResult.OffLine){
		System.out.println("用户不在线");
	}else{
		System.out.println("接口返回异常："+result.getDescr());				
	}
} catch (ApiException e) {
	e.printStackTrace();
}
	 * </pre>
	 */
	public MailResult getMobileBySid(String sid) throws ApiException{
		MailResult result = getMobileAndMailSidBySid(sid);
		if(result.isOK()){
			String mobile = (result.getValue(String[].class))[0];
			return result.setValue(mobile);
		}
		return result;
	}
	/**
	 * 通过sid查询用户是否在线，返回在线用户号码以及邮箱真实sid
	 * <p>参数的sid可以是网盘的sid，也可以是邮箱的sid，接口返回的一定是邮箱的sid</p>
	 * @param sid 用户的sid
	 * @return 返回接口执行结果 {@link MailResult}，如果 {@link MailResult#isOK()} == true，则可以通过String[] mobileAndSid = {@link MailResult#getValue(Class) MailResult.getValue(String[].class)} 得到用户的手机号码和邮箱SID的数组，
	 * mobileAndSid[0] 是手机号码，mobileAndSid[1] 是邮箱中的真实SID，可能与传入的sid参数不相同。
	 * @throws ApiException
	 */
	public MailResult getMobileAndMailSidBySid(String sid) throws ApiException{
		if(StringUtils.isEmpty(sid))
			throw new NullPointerException("参数sid为空或null值");
		long timeStamp = Utility.getTimeSpan(this.config.Mail_Userbykey_TimeExpires);
		String signed = MD5.encode(sid
				+this.config.Mail_Userbykey_ComeFrom
				+String.valueOf(timeStamp)
				+this.config.Mail_Userbykey_MD5Key);
		String postData = "<?xml version=\"1.0\" encoding=\"GB2312\"?>"
			+"<RequestData>"
			+"<SessionKey>"+sid+"</SessionKey>"			
			+"<ComeFrom>"+this.config.Mail_Userbykey_ComeFrom+"</ComeFrom>"
			+"<TimeStamp>"+String.valueOf(timeStamp)+"</TimeStamp>"
			+"<Skey>"+signed+"</Skey>\n"
			+"</RequestData>";
		String out = null;
		try {
			out = HttpClient.post(this.config.Mail_Userbykey_BaseUrl, postData,this.config.Mail_Userbykey_Encoding);
		} catch (IOException e) {
			ApiException ex = new ApiException("查询在线用户号码接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		Xml outXml = null;
		try {
			outXml = Xml.parseXml(out);
		}catch(Exception e){
			ApiException ex = new ApiException("查询在线用户号码接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}
		String outRetCode = "";
		try {
			outRetCode = outXml.evaluate("/ResponseData/RetCode");			
			
	        /// [000]用户登录查询成功(表示在线用户)
			if(outRetCode.equals("000")){				
				String outSid = outXml.evaluate("/ResponseData/SessionKey");
				String outMobile = outXml.evaluate("/ResponseData/UserNumber");	
				String outTimeStamp = outXml.evaluate("/ResponseData/TimeStamp");
				String outSkey = outXml.evaluate("/ResponseData/Skey");
				
				String outSigned = MD5.encode(outSid+outMobile+outRetCode+outTimeStamp+this.config.Mail_Userbykey_MD5Key);
				//判断数字签名是否正确
		        if (!outSigned.equalsIgnoreCase(outSkey))
		            throw new ApiException("查询在线用户号码接口返回数据签名错误，返回值："+out);
		
		        //判断时间戳是否过期
		        if (Utility.getTimeSpan(0) > Long.parseLong(outTimeStamp))
		        	throw new ApiException("查询在线用户号码接口超时");
//		        //返回账号和登录账号是否相符（通过disk登录接口获取的sid会产生不一致）
//		        if(!outSid.equalsIgnoreCase(sid))
//		        	throw new ApiException("查询在线用户号码接口异常，查询SID和返回SID不相符");
		        
//				setOutUserInfo(outMobile);
//				userInfo.setMobile(outMobile);
//				userInfo.setSid(outSid);
		        String[] mobileAndSid = new String[]{outMobile,outSid};
				return MailResult.OK.setValue(mobileAndSid);
			}
			// [001]用户登录查询失败（表示用户不在线）
			else if(outRetCode.equals("001")){
				return MailResult.OffLine;
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("查询在线用户号码接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}		
        return convertResult(outRetCode);
	}
	/**
	 * 查询用户是否开通账单
	 * @param mobile 用户手机号码，不带86
	 * @return 
	 * @throws ApiException
	 * @throws ParserConfigException 
	 */
	public MailResult queryBillStatus(String mobile) throws ApiException, ParserConfigException{
		if(StringUtils.isEmpty(mobile))
			throw new NullPointerException("参数mobile为空或null值");
		if(!Pattern.isMobile(mobile))
			return MailResult.NotChinaMobile;
		long timeStamp = Utility.getTimeSpan(this.config.Mail_QueryBill_TimeExpires);
		String signed = MD5.encode(mobile
				+this.config.Mail_QueryBill_ComeFrom
				+String.valueOf(timeStamp)
				+this.config.Mail_QueryBill_MD5Key);
		String postData = "<?xml version=\"1.0\" encoding=\"GB2312\"?>"
			+"<RequestData>"
			+"<Mobile>"+mobile+"</Mobile>"			
			+"<ComeFrom>"+this.config.Mail_QueryBill_ComeFrom+"</ComeFrom>"
			+"<TimeStamp>"+String.valueOf(timeStamp)+"</TimeStamp>"
			+"<Skey>"+signed+"</Skey>"
			+"</RequestData>";
		String out = null;
		Xml outXml = null;
		try {
			out = HttpClient.post(this.config.Mail_QueryBill_BaseUrl, postData,this.config.Mail_QueryBill_Encoding);
			outXml = Xml.parseXml(out);
		} catch (IOException e) {
			ApiException ex = new ApiException("查询账单接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}catch(Exception e){
			ApiException ex = new ApiException("查询账单接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}
		String outRetCode = "";
		try {
			String outMobile = outXml.evaluate("/ResponseData/Mobile");				
			outRetCode = outXml.evaluate("/ResponseData/RetCode");
			String outTimeStamp = outXml.evaluate("/ResponseData/TimeStamp");
			String outSkey = outXml.evaluate("/ResponseData/Skey");
			
			String outSigned = MD5.encode(outMobile+outRetCode+outTimeStamp+this.config.Mail_QueryBill_MD5Key);
			//判断数字签名是否正确
	        if (!outSigned.equalsIgnoreCase(outSkey))
	            throw new ApiException("查询账单接口返回数据签名错误，返回值："+out);
	
	        if(!outMobile.equals(mobile))
	        	throw new ApiException("查询账单接口返回的Mobile与输入的mobile不符");
	        //判断时间戳是否过期
	        if (Utility.getTimeSpan(0) > Long.parseLong(outTimeStamp))
	        	throw new ApiException("查询账单接口超时");

			if(outRetCode.equals("000")){
				//已开通
				return MailResult.BillOpened;
			}else if(outRetCode.equals("002") || outRetCode.equals("323")){
				//未开通
				return MailResult.BillNotOpen;
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("查询账单接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}		
        return convertResult(outRetCode);
	}

	/**
	 * 开通账单服务
	 * @param mobile 用户手机号码，不带86
	 * @param isSendSms 是否下发短信提示
	 * @return 接口执行结果
	 * @throws ApiException 接口执行异常
	 * @throws ParserConfigException  配置文件读取异常
	 */
	public MailResult openBill(String mobile,boolean isSendSms) throws ParserConfigException, ApiException{
		return openOrCancelBill(mobile, true, isSendSms);
	}
	/**
	 * 取消账单服务
	 * @param mobile 用户手机号码，不带86
	 * @param isSendSms 是否下发短信提示
	 * @return 接口执行结果
	 * @throws ApiException 接口执行异常
	 * @throws ParserConfigException  配置文件读取异常
	 */
	public MailResult cancelBill(String mobile,boolean isSendSms) throws ParserConfigException, ApiException{
		return openOrCancelBill(mobile, false, isSendSms);
	}
	
	/**
	 * 开通或取消账单
	 * @param mobile 用户手机号码，不带86
	 * @param isOpen true 开通账单，false 取消账单
	 * @param isSendSms 是否下发短信提示
	 * @return 返回接口处理结果
	 * @throws ParserConfigException 配置文件读取异常
	 * @throws ApiException 接口执行异常
	 */
	private MailResult openOrCancelBill(String mobile,boolean isOpen,boolean isSendSms) throws ParserConfigException, ApiException{
		if(StringUtils.isEmpty(mobile))
			throw new NullPointerException("参数mobile为空或null值");
		if(!Pattern.isMobile(mobile))
			return MailResult.NotChinaMobile;
		long timeStamp = Utility.getTimeSpan(this.config.Mail_OpenBill_TimeExpires);
		String operType = isOpen ? "ZD" : "QXZD";
		String sendSmsFlag = isSendSms ? "0" : "1";
		String signed = MD5.encode(mobile
				+operType+sendSmsFlag
				+this.config.Mail_OpenBill_ComeFrom
				+String.valueOf(timeStamp)
				+this.config.Mail_OpenBill_MD5Key);
		String postData = "<?xml version=\"1.0\" encoding=\"GB2312\"?>"
			+"<RequestData>"
			+"<Mobile>"+mobile+"</Mobile>"
			+"<OperType>"+operType+"</OperType>"
			+"<IsSendSMS>"+sendSmsFlag+"</IsSendSMS>"
			+"<ComeFrom>"+this.config.Mail_OpenBill_ComeFrom+"</ComeFrom>"
			+"<TimeStamp>"+String.valueOf(timeStamp)+"</TimeStamp>"
			+"<Skey>"+signed+"</Skey>"
			+"</RequestData>";
		String out = null;
		Xml outXml = null;
		try {
			out = HttpClient.post(this.config.Mail_OpenBill_BaseUrl, postData,this.config.Mail_OpenBill_Encoding);
			outXml = Xml.parseXml(out);
		} catch (IOException e) {
			ApiException ex = new ApiException("开通取消账单接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}catch(Exception e){
			ApiException ex = new ApiException("开通取消账单接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}
		String outRetCode = "";
		try {
			String outMobile = outXml.evaluate("/ResponseData/Mobile");
			String outOperType = outXml.evaluate("/ResponseData/OperType");	
			outRetCode = outXml.evaluate("/ResponseData/RetCode");
			String outTimeStamp = outXml.evaluate("/ResponseData/TimeStamp");
			String outSkey = outXml.evaluate("/ResponseData/Skey");
			
			String outSigned = MD5.encode(outMobile+outOperType+outRetCode+outTimeStamp+this.config.Mail_OpenBill_MD5Key);
			//判断数字签名是否正确
	        if (!outSigned.equalsIgnoreCase(outSkey))
	            throw new ApiException("开通取消账单接口返回数据签名错误，返回值："+out);
	
	        if(!outMobile.equals(mobile))
	        	throw new ApiException("开通取消账单接口返回的Mobile与输入的mobile不符");
	        //判断时间戳是否过期
	        if (Utility.getTimeSpan(0) > Long.parseLong(outTimeStamp))
	        	throw new ApiException("开通取消账单接口超时");
	        if(!operType.equals(outOperType))
	        	throw new ApiException("开通取消账单接口返回的操作类型operType与输入的operType不符");
			if(outRetCode.equals("000")){
				//操作成功
				return MailResult.OK;
			}else if(outRetCode.equals("002")){
				//开通账单投递失败
				return MailResult.OpenBillFailed;
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("开通取消账单接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}		
        return convertResult(outRetCode);
	}
	/**
	 * 开通邮件提醒
	 * <br/>
	 * 判断用户是否已开通邮件提醒。若已开通功能，则不改变用户的提醒设置；
	 * 若未开通或已关闭功能，则按默认值（方式为普通短信，时段为8~22点）为其开通提醒功能。
	 * @param mobile 用户手机号码，不带86
	 * @return
	 * @throws ApiException
	 * @throws ParserConfigException 
	 */
	public MailResult openMailNotify(String mobile) throws ApiException, ParserConfigException{
		return queryOrOpenMailNotify(mobile,true);
	}
	/**
	 * 查询用户邮件提醒状态 
	 * <br/>
	 * 返回OK时查询成功，可使用方法 getUserInfo().getNotifyType()获取开通情况
	 * @param mobile 用户手机号码，不带86
	 * @return
	 * @throws ApiException
	 * @throws ParserConfigException 
	 */
	public MailResult queryMailNotify(String mobile) throws ApiException, ParserConfigException{
		return queryOrOpenMailNotify(mobile,false);
	}
	/**
	 * 查询或开通邮件提醒
	 * @param mobile 用户手机号码，不带86
	 * @param isOpen true 开通，false 查询
	 * @return Api执行结果
	 * @throws ApiException Api执行发生异常 
	 * @throws ParserConfigException 
	 */
	private MailResult queryOrOpenMailNotify(String mobile,boolean isOpen) throws ApiException, ParserConfigException{
		if(StringUtils.isEmpty(mobile))
			throw new NullPointerException("参数mobile为空或null值");
		if(!Pattern.isMobile(mobile))
			return MailResult.NotChinaMobile;
		String OpType = isOpen ? "2" : "1";
		long timeStamp = Utility.getTimeSpan(this.config.Mail_SmsNotify_TimeExpires);
		String signed = MD5.encode(mobile
				+OpType
				+this.config.Mail_SmsNotify_ComeFrom
				+String.valueOf(timeStamp)
				+this.config.Mail_SmsNotify_MD5Key);
		String postData = "<?xml version=\"1.0\" encoding=\"GB2312\"?>"
			+"<RequestData>"
			+"<Mobile>"+mobile+"</Mobile>"
			+"<OpType>"+OpType+"</OpType>"			
			+"<ComeFrom>"+this.config.Mail_SmsNotify_ComeFrom+"</ComeFrom>"
			+"<TimeStamp>"+String.valueOf(timeStamp)+"</TimeStamp>"
			+"<Skey>"+signed+"</Skey>"
			+"</RequestData>";
		String out = null;
		Xml outXml = null;
		try {
			out = HttpClient.post(this.config.Mail_SmsNotify_BaseUrl, postData,this.config.Mail_SmsNotify_Encoding);
			outXml = Xml.parseXml(out);
		} catch (IOException e) {
			ApiException ex = new ApiException("查询开通邮件提醒接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}catch(Exception e){
			ApiException ex = new ApiException("查询开通邮件提醒接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}
		String outRetCode = "";
		try {
			String outMobile = outXml.evaluate("/ResponseData/Mobile");				
			outRetCode = outXml.evaluate("/ResponseData/RetCode");
			String outTimeStamp = outXml.evaluate("/ResponseData/TimeStamp");
			String outSkey = outXml.evaluate("/ResponseData/Skey");
			
			String outSigned = MD5.encode(outMobile+outRetCode+outTimeStamp+this.config.Mail_SmsNotify_MD5Key);
			//判断数字签名是否正确
	        if (!outSigned.equalsIgnoreCase(outSkey))
	            throw new ApiException("查询开通邮件提醒接口返回数据签名错误，返回值："+out);
	
	        if(!outMobile.equals(mobile))
	        	throw new ApiException("查询开通邮件提醒接口返回的Mobile与输入的mobile不符");
	        //判断时间戳是否过期
	        if (Utility.getTimeSpan(0) > Long.parseLong(outTimeStamp))
	        	throw new ApiException("查询开通邮件提醒接口超时");     

			if(outRetCode.equals("000")){				
				int outBeginHour = Integer.parseInt(outXml.evaluate("/ResponseData/BeginTime"));
				int outEndHour = Integer.parseInt(outXml.evaluate("/ResponseData/EndTime"));
				NotifyType outNotifyType = NotifyType.parse(outXml.evaluate("/ResponseData/NotifyType"));
				
				setOutUserInfo(outMobile);
				userInfo.setMobile(outMobile);
				userInfo.setNotifyBeginHour(outBeginHour);
				userInfo.setNotifyEndHour(outEndHour);
				userInfo.setNotifyType(outNotifyType);
				
				return MailResult.OK.setValue(userInfo);
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("查询开通邮件提醒接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}		
        return convertResult(outRetCode);
	}
	
	/**
	 * 开通增值业务
	 * @param mobile 用户手机号码，不带86
	 * @param cityID 城市编号
	 * @param serviceID 业务ID<br/>
	 * * 70-手机硬盘(网络硬盘)<br/>
	 * * 80-短信仓库<br/>
 	 * * 90-彩信仓库<br/>
	 * * 100-立通PushMail<br/>
	 * * 130-邮箱伴侣<br/>
	 * @param serviceItem 业务代码，开通的增值业务类型，对应mail139库os_service_info表的serviceitem字段(status=0,)
	 * @param bindTypeID 活动号,用户订购业务绑定的活动号<br/>
	 * 6584-网盘<br/>
	 * 7944-短信<br/>
	 * 8764-彩信<br/>
	 * 9024-立通PushMail
	 * @return 接口执行结果
	 * @throws ParserConfigException 配置文件读取异常
	 * @throws ApiException 接口执行异常
	 */
	public MailResult openService(String mobile,int cityID,String serviceID,String serviceItem,String bindTypeID) throws ParserConfigException, ApiException{
		if(StringUtils.isEmpty(mobile))
			throw new NullPointerException("参数mobile为空或null值");
		if(!Pattern.isMobile(mobile))
			return MailResult.NotChinaMobile;
		if(StringUtils.isEmpty(serviceID))
			throw new NullPointerException("参数serviceID为空或null值");
		if(StringUtils.isEmpty(serviceItem))
			throw new NullPointerException("参数serviceItem为空或null值");
		if(StringUtils.isEmpty(bindTypeID))
			throw new NullPointerException("参数bindTypeID为空或null值");
		
		long timeStamp = Utility.getTimeSpan(this.config.Mail_Services_TimeExpires);

		String signed = MD5.encode(cityID+mobile
				+serviceID+serviceItem+bindTypeID
				+this.config.Mail_Services_ComeFrom
				+String.valueOf(timeStamp)
				+this.config.Mail_Services_MD5Key);
		String postData = "<?xml version=\"1.0\" encoding=\"GB2312\"?>"
			+"<RequestData>"
			+"<CityID>"+cityID+"</CityID>"
			+"<Mobile>"+mobile+"</Mobile>"
			+"<ServiceId>"+serviceID+"</ServiceId>"
			+"<ServiceItem>"+serviceItem+"</ServiceItem>"
			+"<BindTypeId>"+bindTypeID+"</BindTypeId>"
			+"<ComeFrom>"+this.config.Mail_Services_ComeFrom+"</ComeFrom>"
			+"<TimeStamp>"+String.valueOf(timeStamp)+"</TimeStamp>"
			+"<Skey>"+signed+"</Skey>"
			+"</RequestData>";
		String out = null;
		Xml outXml = null;
		try {
			out = HttpClient.post(this.config.Mail_Services_BaseUrl, postData,this.config.Mail_Services_Encoding);
			outXml = Xml.parseXml(out);
		} catch (IOException e) {
			ApiException ex = new ApiException("开通增值业务接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}catch(Exception e){
			ApiException ex = new ApiException("开通增值业务接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}
		String outRetCode = "";
		try {
			String outMobile = outXml.evaluate("/ResponseData/Mobile");
			String outServiceItem = outXml.evaluate("/ResponseData/ServiceItem");	
			outRetCode = outXml.evaluate("/ResponseData/RetCode");
			String outTimeStamp = outXml.evaluate("/ResponseData/TimeStamp");
			String outSkey = outXml.evaluate("/ResponseData/Skey");
			
			String outSigned = MD5.encode(outMobile+outServiceItem+outRetCode+outTimeStamp+this.config.Mail_Services_MD5Key);
			//判断数字签名是否正确
	        if (!outSigned.equalsIgnoreCase(outSkey))
	            throw new ApiException("开通增值业务接口返回数据签名错误，返回值："+out);
	
	        if(!outMobile.equals(mobile))
	        	throw new ApiException("开通增值业务接口返回的Mobile与输入的mobile不符");
	        //判断时间戳是否过期
	        if (Utility.getTimeSpan(0) > Long.parseLong(outTimeStamp))
	        	throw new ApiException("开通增值业务接口超时");
	        if(!outServiceItem.equals(serviceItem))
	        	throw new ApiException("开通增值业务接口返回的操作类型outServiceItem与输入的serviceItem不符");
			if(outRetCode.equals("000")){
				return MailResult.OK;
			}else if(outRetCode.equals("002")){
				//开通账单投递失败
				return MailResult.OpenServiceFailed;
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("开通增值业务接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}		
        return convertResult(outRetCode);
	}
	/**
	 * 查询是否开通业务
	 * @param mobile 用户手机号码，不带86
	 * @param serviceID 业务ID<br/>
	 * * 10-手机邮箱
	 * * 70-手机硬盘(网络硬盘)<br/>
	 * * 80-短信仓库<br/>
 	 * * 90-彩信仓库<br/>
	 * * 100-立通PushMail<br/>
	 * * 130-邮箱伴侣<br/>
	 * @return
	 * @throws ApiException
	 * @throws ParserConfigException
	 */
	public MailResult queryService(String mobile,String serviceID) throws ApiException, ParserConfigException{
		if(StringUtils.isEmpty(mobile))
			throw new NullPointerException("参数mobile为空或null值");
		if(!Pattern.isMobile(mobile))
			return MailResult.NotChinaMobile;
		if(StringUtils.isEmpty(serviceID))
			throw new NullPointerException("参数serviceID为空或null值");
		
		long timeStamp = Utility.getTimeSpan(this.config.Mail_IsOpen_TimeExpires);

		String signed = MD5.encode(mobile
				+serviceID
				+this.config.Mail_IsOpen_ComeFrom
				+String.valueOf(timeStamp)
				+this.config.Mail_IsOpen_MD5Key);
		
		String postData = "<?xml version=\"1.0\" encoding=\"GB2312\"?>"
			+"<RequestData>"
			+"<Account>"+mobile+"</Account>"
			+"<ServiceId>"+serviceID+"</ServiceId>"
			+"<ComeFrom>"+this.config.Mail_IsOpen_ComeFrom+"</ComeFrom>"
			+"<TimeStamp>"+String.valueOf(timeStamp)+"</TimeStamp>"
			+"<Skey>"+signed+"</Skey>"
			+"</RequestData>";
		
		String out = null;
		Xml outXml = null;
		try {
			out = HttpClient.post(this.config.Mail_IsOpen_BaseUrl, postData,this.config.Mail_IsOpen_Encoding);
			outXml = Xml.parseXml(out);
		} catch (IOException e) {
			ApiException ex = new ApiException("查询增值业务接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}catch(Exception e){
			ApiException ex = new ApiException("查询增值业务接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}
		String outRetCode = "";
		try {
			String outMobile = outXml.evaluate("/ResponseData/Account");	
			outRetCode = outXml.evaluate("/ResponseData/RetCode");
			String outTimeStamp = outXml.evaluate("/ResponseData/TimeStamp");
			String outSkey = outXml.evaluate("/ResponseData/Skey");
			
			String outSigned = MD5.encode(outMobile+outRetCode+outTimeStamp+this.config.Mail_IsOpen_MD5Key);
			//判断数字签名是否正确
	        if (!outSigned.equalsIgnoreCase(outSkey))
	            throw new ApiException("查询增值业务接口返回数据签名错误，返回值："+out);
	
	        if(!outMobile.equals(mobile))
	        	throw new ApiException("查询增值业务接口返回的Mobile与输入的mobile不符");
	        //判断时间戳是否过期
	        if (Utility.getTimeSpan(0) > Long.parseLong(outTimeStamp))
	        	throw new ApiException("查询增值业务接口超时");
	       
			if(outRetCode.equals("000")){
				//已开通该业务
				return MailResult.Opened;
			}else if(outRetCode.equals("002")){
				//未开通该业务
				return MailResult.NotOpen;
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("查询增值业务接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}		
        return convertResult(outRetCode);
	}
	
	/**
	 * 查询免费彩信当月使用情况
	 * @param mobile 用户手机号码，不带86
	 * @return 返回接口执行情况
	 * <br/>可通过<code>getUserInfo().getMmsInfo()</code> 获取彩信使用情况
	 * @throws ApiException 接口异常
	 * @throws ParserConfigException 配置文件读取异常
	 */
	public MailResult queryFreeMms(String mobile) throws ApiException, ParserConfigException{
		return queryFreeMS(mobile,"2");
	}
	/**
	 * 查询免费短信当月使用情况
	 * @param mobile 用户手机号码，不带86
	 * @return 返回接口执行情况
	 * <br/>可通过<code>getUserInfo().getSmsInfo()</code> 获取短信使用情况
	 * @throws ApiException 接口异常
	 * @throws ParserConfigException 配置文件读取异常
	 */
	public MailResult queryFreeSms(String mobile) throws ApiException, ParserConfigException{
		return queryFreeMS(mobile,"1");
	}
	/**
	 * 扣减用户当月免费短信条数
	 * @param mobile 用户号码，不带86
	 * @param count 需要扣减的数量
	 * @return 执行结果 <code>MailResult.OK</code>则扣减成功
	 * @throws ParserConfigException 读取配置文件异常
	 * @throws ApiException  接口执行异常
	 */
	public MailResult reduceFreeSms(String mobile,int count) throws ApiException, ParserConfigException{
		return reduceFreeMS(mobile, count, "1");
	}
	/**
	 * 扣减用户当月免费彩信条数
	 * @param mobile 用户号码，不带86
	 * @param count 需要扣减的数量
	 * @return 执行结果 <code>MailResult.OK</code>则扣减成功
	 * @throws ParserConfigException 读取配置文件异常
	 * @throws ApiException  接口执行异常
	 */
	public MailResult reduceFreeMms(String mobile,int count) throws ApiException, ParserConfigException{
		return reduceFreeMS(mobile, count, "2");
	}
	/**
	 * 查询免费短彩当月使用情况
	 * @param mobile 用户手机号码，不带86
	 * @param operType 1 ： 短信，2：彩信
	 * @return 返回接口执行情况
	 * @throws ApiException 接口异常
	 * @throws ParserConfigException 配置文件读取异常
	 */
	private MailResult queryFreeMS(String mobile,String operType) throws ApiException, ParserConfigException{
		if(StringUtils.isEmpty(mobile))
			throw new NullPointerException("参数mobile为空或null值");
		if(!Pattern.isMobile(mobile))
			return MailResult.NotChinaMobile;

		long timeStamp = Utility.getTimeSpan(this.config.Mail_QueryMS_TimeExpires);
		String signed = MD5.encode(mobile
				+operType
				+this.config.Mail_QueryMS_ComeFrom
				+String.valueOf(timeStamp)
				+this.config.Mail_QueryMS_MD5Key);
		String postData = "<?xml version=\"1.0\" encoding=\"GB2312\"?>"
			+"<RequestData>"
			+"<Mobile>"+mobile+"</Mobile>"
			+"<OperType>"+operType+"</OperType>"			
			+"<ComeFrom>"+this.config.Mail_QueryMS_ComeFrom+"</ComeFrom>"
			+"<TimeStamp>"+String.valueOf(timeStamp)+"</TimeStamp>"
			+"<Skey>"+signed+"</Skey>"
			+"</RequestData>";
		String out = null;
		Xml outXml = null;
		try {
			out = HttpClient.post(this.config.Mail_QueryMS_BaseUrl, postData,this.config.Mail_QueryMS_Encoding);
			outXml = Xml.parseXml(out);
		} catch (IOException e) {
			ApiException ex = new ApiException("查询短彩免费数量接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}catch(Exception e){
			ApiException ex = new ApiException("查询短彩免费数量接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}
		String outRetCode = "";
		try {
			String outMobile = outXml.evaluate("/ResponseData/Mobile");
			String outOperType = outXml.evaluate("/ResponseData/OperType");
			String outUserType = outXml.evaluate("/ResponseData/UserType");
			
			String outMonthTotal = outXml.evaluate("/ResponseData/MonthTotal");
			String outRemainMonthTotal = outXml.evaluate("/ResponseData/RemainMonthTotal");
			String outRemainDayTotal = outXml.evaluate("/ResponseData/RemainDayTotal");
			String outFreeDownIsAllowSend = outXml.evaluate("/ResponseData/FreeDownIsAllowSend");
			String outFeeValue = outXml.evaluate("/ResponseData/FeeValue");
			
			outRetCode = outXml.evaluate("/ResponseData/RetCode");
			String outTimeStamp = outXml.evaluate("/ResponseData/TimeStamp");
			String outSkey = outXml.evaluate("/ResponseData/Skey");
			
			String outSigned = MD5.encode(outMobile+outOperType+outUserType+outMonthTotal
					+outRemainMonthTotal+outRemainDayTotal+outFreeDownIsAllowSend
					+outFeeValue+outRetCode+outTimeStamp+this.config.Mail_QueryMS_MD5Key);
			//判断数字签名是否正确
	        if (!outSigned.equalsIgnoreCase(outSkey))
	            throw new ApiException("查询短彩免费数量接口返回数据签名错误，返回值："+out);	
	        if(!outMobile.equals(mobile))
	        	throw new ApiException("查询短彩免费数量接口返回的Mobile与输入的mobile不符");
	        //判断时间戳是否过期
	        if (Utility.getTimeSpan(0) > Long.parseLong(outTimeStamp))
	        	throw new ApiException("查询短彩免费数量接口超时");
	        if(!outOperType.equals(operType))
	        	throw new ApiException("查询短彩免费数量接口返回的operType与输入的operType不符");

			if(outRetCode.equals("000")){
				/**
				 * 设置返回值
				 */
				setOutUserInfo(outMobile);
				MsInfo msInfo = new MsInfo();
				msInfo.setMonthTotal(Integer.parseInt(outMonthTotal));
				msInfo.setMonthRemain(Integer.parseInt(outRemainMonthTotal));
				msInfo.setDayRemain(Integer.parseInt(outRemainDayTotal));
				msInfo.setFeeSend(outFreeDownIsAllowSend.equals("0")?false:true);
				msInfo.setFeeValue(Integer.parseInt(outFeeValue));
				if(outOperType.equals("1")){
					msInfo.setType("SMS");
					this.userInfo.setSmsInfo(msInfo);
				}else{
					msInfo.setType("MMS");
					this.userInfo.setSmsInfo(msInfo);
				}
				UserType userType = null;
				if(outUserType.endsWith("0"))
					userType = UserType.Normal;
				if(outUserType.endsWith("1"))
					userType = UserType.BlackList;
				if(outUserType.endsWith("2"))
					userType = UserType.RedList;
				this.userInfo.setUserType(userType);
				
				return MailResult.OK.setValue(userInfo);
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("查询短彩免费数量接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}		
        return convertResult(outRetCode);
	}
	/**
	 * 扣减用户当月免费短彩数量
	 * @param mobile 用户手机号码，不带86
	 * @param count 扣减数量
	 * @param operType 1 ： 短信，2：彩信
	 * @return 返回接口执行情况
	 * @throws ApiException 接口异常
	 * @throws ParserConfigException 配置文件读取异常
	 */
	private MailResult reduceFreeMS(String mobile,int count,String operType) throws ApiException, ParserConfigException{
		if(StringUtils.isEmpty(mobile))
			throw new NullPointerException("参数mobile为空或null值");
		if(!Pattern.isMobile(mobile))
			return MailResult.NotChinaMobile;

		long timeStamp = Utility.getTimeSpan(this.config.Mail_ReduceMS_TimeExpires);
		String signed = MD5.encode(mobile
				+operType+count
				+this.config.Mail_ReduceMS_ComeFrom
				+String.valueOf(timeStamp)
				+this.config.Mail_ReduceMS_MD5Key);
		String postData = "<?xml version=\"1.0\" encoding=\"GB2312\"?>"
			+"<RequestData>"
			+"<Mobile>"+mobile+"</Mobile>"
			+"<OperType>"+operType+"</OperType>"
			+"<OperCount>"+count+"</OperCount>"
			+"<ComeFrom>"+this.config.Mail_ReduceMS_ComeFrom+"</ComeFrom>"
			+"<TimeStamp>"+String.valueOf(timeStamp)+"</TimeStamp>"
			+"<Skey>"+signed+"</Skey>"
			+"</RequestData>";
		String out = null;
		Xml outXml = null;
		try {
			out = HttpClient.post(this.config.Mail_ReduceMS_BaseUrl, postData,this.config.Mail_ReduceMS_Encoding);
			outXml = Xml.parseXml(out);
		} catch (IOException e) {
			ApiException ex = new ApiException("扣减短彩免费数量接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}catch(Exception e){
			ApiException ex = new ApiException("扣减短彩免费数量接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}
		String outRetCode = "";
		try {
			String outMobile = outXml.evaluate("/ResponseData/Mobile");
			String outOperType = outXml.evaluate("/ResponseData/OperType");
			
			String outRemainMonthTotal = outXml.evaluate("/ResponseData/RemainMonthTotal");
			String outRemainDayTotal = outXml.evaluate("/ResponseData/RemainDayTotal");
			//String outOperCount = outXml.evaluate("/ResponseData/OperCount");
			
			outRetCode = outXml.evaluate("/ResponseData/RetCode");
			String outTimeStamp = outXml.evaluate("/ResponseData/TimeStamp");
			String outSkey = outXml.evaluate("/ResponseData/Skey");
			
			String outSigned = MD5.encode(outMobile+outOperType
					+outRemainMonthTotal+outRemainDayTotal
					+outRetCode+outTimeStamp+this.config.Mail_ReduceMS_MD5Key);
			//判断数字签名是否正确
	        if (!outSigned.equalsIgnoreCase(outSkey))
	            throw new ApiException("扣减短彩免费数量接口返回数据签名错误，返回值："+out);	
	        if(!outMobile.equals(mobile))
	        	throw new ApiException("扣减短彩免费数量接口返回的Mobile与输入的mobile不符");
	        //判断时间戳是否过期
	        if (Utility.getTimeSpan(0) > Long.parseLong(outTimeStamp))
	        	throw new ApiException("扣减短彩免费数量接口超时");
	        if(!outOperType.equals(operType))
	        	throw new ApiException("扣减短彩免费数量接口返回的operType与输入的operType不符");

			if(outRetCode.equals("000")){
				/**
				 * 设置返回值
				 */
				setOutUserInfo(outMobile);				
				MsInfo msInfo = null;//new MsInfo();
				
				if(outOperType.equals("1")){
					msInfo = this.userInfo.getSmsInfo();
					if(msInfo == null){
						msInfo = new MsInfo(); 
						msInfo.setType("SMS");
					}						
				}else{
					msInfo = this.userInfo.getMmsInfo();
					if(msInfo == null){
						msInfo = new MsInfo(); 
						msInfo.setType("MMS");
					}						
				}
				msInfo.setMonthRemain(Integer.parseInt(outRemainMonthTotal));
				msInfo.setDayRemain(Integer.parseInt(outRemainDayTotal));
				return MailResult.OK.setValue(msInfo);
			}else if(outRetCode.equals("001")){
				return MailResult.PartOK;
			}			
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("扣减短彩免费数量接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}		
        return convertResult(outRetCode);
	}
	/**
	 * 设置当前对象的用户信息userInfo为返回的接口的userInfo
	 * @param outMobile 接口返回的用户手机号码
	 */
	protected void setOutUserInfo(String outMobile,String outAlias){
		if(this.userInfo == null 
				|| (!StringUtils.isEmpty(this.userInfo.getMobile()) && !this.userInfo.getMobile().equals(outMobile))
				|| (!StringUtils.isEmpty(this.userInfo.getAlias()) && !this.userInfo.getAlias().equals(outAlias)))
			this.userInfo = new UserInfo();
	}
	protected void setOutUserInfo(String outMobile){
		if(this.userInfo == null 
				|| (!StringUtils.isEmpty(this.userInfo.getMobile()) && !this.userInfo.getMobile().equals(outMobile)))
			this.userInfo = new UserInfo();
	}
	/**
	 * 将接口返回值转换为枚举<br/>
	 * 返回值以“0“开头的(如：“000”、“001”、“002”等)需要根据不同接口分别处理，该方法不对“0”开头的返回值进行处理
	 * @param outRetCode 接口返回值，如："201"
	 * @return 返回对应接口返回值的枚举
	 * @throws ApiException 返回值为异常类型时抛出接口执行异常
	 */
	protected MailResult convertResult(String outRetCode) throws ApiException{
		if(outRetCode.equals("000"))
			return MailResult.OK;
        /// [001]该帐号已存在
        if(outRetCode.equals("001"))
        	return MailResult.ExistsYet;
        /// [002]获取操作失败
        if(outRetCode.equals("002"))
        	return MailResult.ApiFailed;
        	//throw new ApiException("Mail接口返回错误信息：[002]获取操作失败");
        /// [101]参数不全
        if(outRetCode.equals("101"))
        	throw new ApiException("Mail接口返回错误信息：[101]参数不全");
        /// [102]Skey签名无效
        if(outRetCode.equals("102"))
        	throw new ApiException("Mail接口返回错误信息：[102]Skey签名无效");
        /// [103]时间戳超时
        if(outRetCode.equals("103"))
        	throw new ApiException("Mail接口返回错误信息：[103]时间戳超时");
        /// [104]ComeFrom非法
        if(outRetCode.equals("104"))
        	throw new ApiException("Mail接口返回错误信息：[104]ComeFrom非法");        
        /// [105]参数超出指定的长度限制
        if(outRetCode.equals("105"))
        	throw new ApiException("Mail接口返回错误信息：[105]参数超出指定的长度限制");
        /// [106]参数值错误
        if(outRetCode.equals("106"))
        	throw new ApiException("Mail接口返回错误信息：[106]参数值错误");
        /// [201]服务器忙
        if(outRetCode.equals("201"))
        	throw new ApiException("Mail接口返回错误信息：[201]服务器忙");
        /// [202]无法连接数据库
        if(outRetCode.equals("202"))
        	throw new ApiException("Mail接口返回错误信息：[202]无法连接数据库");
        /// [203]BOSS鉴权失败
        if(outRetCode.equals("203"))
        	throw new ApiException("Mail接口返回错误信息：[203]BOSS鉴权失败");
        /// [204]查询操作失败
        if(outRetCode.equals("204"))
        	throw new ApiException("Mail接口返回错误信息：[204]查询操作失败");
        /// [301]非移动手机号码
        if(outRetCode.equals("301"))
        	return MailResult.NotChinaMobile;
        /// [302]该帐号不存在
        if(outRetCode.equals("302"))
        	return MailResult.Inexistent;
        /// [303]该帐号已被冻结
        if(outRetCode.equals("303"))
        	return MailResult.Frozen;
        /// [304]密码错误
        if(outRetCode.equals("304"))
        	return MailResult.WrongPassword;
        /// [305]城市编号不存在
        if(outRetCode.equals("305"))
        	return MailResult.InvalidAreaCode;
        /// [306]手机号和城市编号不对应
        if(outRetCode.equals("306"))
        	return MailResult.MismatchArea;
        /// [307]开户ServiceCode非法
        if(outRetCode.equals("307"))
        	return MailResult.InvalidServiceCode;
//        /// [308]积分操作类型不正确
//        if(outRetCode.equals("308"))
//
//        /// [309]积分值类型不正确
//        if(outRetCode.equals("309"))
//        
//        /// [310]当前积分额不够扣减
//        if(outRetCode.equals("310"))
//        
//        /// [311]日期格式不对
//        if(outRetCode.equals("311"))
//        
//        /// [312]开始日期大于截止日期
//        if(outRetCode.equals("312"))
//        
//        /// [313]用户属性非法
//        if(outRetCode.equals("313"))
//        
//        /// [314]Spsid不正确
//        if(outRetCode.equals("314"))
//        
//        /// [315]Spnumber不正确
//        if(outRetCode.equals("315"))
//        
        /// [316]短信内容超长
        if(outRetCode.equals("316"))
        	return MailResult.InvalidSmsBody;
//        /// [317]短信下行类型不正确
//        if(outRetCode.equals("317"))
//        
//        /// [318]活动号BindTypeId非法
//        if(outRetCode.equals("318"))
//        
//        /// [319]业务ID错误
//        if(outRetCode.equals("319"))
//        
//        /// [320]子业务代码错误
//        if(outRetCode.equals("320"))
//        
//        /// [321]该城市不存在对应业务
//        if(outRetCode.equals("321"))
//        
        /// [322]用户已开通该业务，并正常使用
        if(outRetCode.equals("322"))
        	return MailResult.Opened;
        /// [323]用户未开通本业务
        if(outRetCode.equals("323"))
        	return MailResult.NotOpen;
//        /// [324]用户网盘目录DirectoryId不存在
//        if(outRetCode.equals("324"))
//        	
        /// [999]未知错误
        if(outRetCode.equals("999"))
        	throw new ApiException("Mail接口返回错误信息：未知错误");
	    else
	        throw new ApiException("Mail接口返回未解析的结果："+outRetCode);
	}
}
