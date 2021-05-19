package yzkf.config;

import yzkf.exception.ParserConfigException;
import yzkf.utils.TryParse;

public class ApiConfig extends Configuration {
	/**
	 * Wap短地址跳转参数校验MD5签名密钥
	 */
	public String Wap_Login_MD5Key;	
	/**
	 * Wap段地址跳转有效期，单位秒
	 */
	public int Wap_Login_Expires;
	/**
	 * 单点登录到运营活动参数校验MD5签名密钥
	 */
	public String SSO_OPSSO_MD5Key;
	/**
	 * 单点登录到运营活动手机号码DES解密密钥
	 */
	public String SSO_OPSSO_DESKey;
	/**
	 * 单点登录到运营活动地址跳转有效期，单位秒
	 */
	public int SSO_OPSSO_Expires;
	/**
	 * 活动单点到活动的请求地址
	 */
	public String SSO_OPSSO_JumpUrl;
	/**
	 * 邮箱单点到活动的请求地址
	 */
	public String SSO_OPSSO_ForMail;
	
	public String SSO_REGLOGIN_ClientID;
	public String SSO_REGLOGIN_Encoding;
	public int SSO_REGLOGIN_Expires;
	public String SSO_REGLOGIN_DESKey;
	public String SSO_REGLOGIN_MD5Key;
	public String SSO_REGLOGIN_RegUrl;
	public String SSO_REGLOGIN_LoginUrl;
	public String SSO_REGLOGIN_WapLoginUrl;
	
	public String Mail_Integral_Host;
	public int Mail_Integral_Port;
	public String Mail_Integral_Encoding;
	public int Mail_Integral_TimeExpires;
	
	public String Mail_ValidateLogin_ComeFrom;
	public String Mail_ValidateLogin_MD5Key;
	public int Mail_ValidateLogin_TimeExpires;
	public String Mail_ValidateLogin_BaseURL;
	
	public String Mail_SSOUrl_ComeFrom;
	public String Mail_SSOUrl_BaseUrl;
	
	public String Mail_Attribute_ComeFrom;
	public String Mail_Attribute_Encoding;
	public String Mail_Attribute_MD5Key;
	public int Mail_Attribute_TimeExpires;
	public String Mail_Attribute_BaseUrl;
	
	public String Mail_CardType_ComeFrom;
	public String Mail_CardType_Encoding;
	public String Mail_CardType_MD5Key;
	public int Mail_CardType_TimeExpires;
	public String Mail_CardType_BaseUrl;
	
	public String Mail_Register_ComeFrom;
	public String Mail_Register_Encoding;
	public String Mail_Register_MD5Key;
	public int Mail_Register_TimeExpires;
	public String Mail_Register_BaseUrl;
	
	/**
	 * 根据SessionKey得到用户名接口配置
	 */
	public String Mail_Userbykey_ComeFrom;
	public String Mail_Userbykey_Encoding;
	public String Mail_Userbykey_MD5Key;
	public int Mail_Userbykey_TimeExpires;
	public String Mail_Userbykey_BaseUrl;
	/**
	 * 查询账单开通状态
	 */
	public String Mail_QueryBill_ComeFrom;
	public String Mail_QueryBill_Encoding;
	public String Mail_QueryBill_MD5Key;
	public int Mail_QueryBill_TimeExpires;
	public String Mail_QueryBill_BaseUrl;	
	/**
	 * 开通注销账单
	 */
	public String Mail_OpenBill_ComeFrom;
	public String Mail_OpenBill_Encoding;
	public String Mail_OpenBill_MD5Key;
	public int Mail_OpenBill_TimeExpires;
	public String Mail_OpenBill_BaseUrl;
	/**
	 * 查询邮件数
	 */
	public String Mail_QueryMailNum_Version;
	public String Mail_QueryMailNum_Encoding;
	public String Mail_QueryMailNum_BaseUrl;
	/**
	 * 邮件提醒开通和查询
	 */
	public String Mail_SmsNotify_ComeFrom;
	public String Mail_SmsNotify_Encoding;
	public String Mail_SmsNotify_MD5Key;
	public int Mail_SmsNotify_TimeExpires;
	public String Mail_SmsNotify_BaseUrl;
	/**
	 * 增值业务开通
	 */
	public String Mail_Services_ComeFrom;
	public String Mail_Services_Encoding;
	public String Mail_Services_MD5Key;
	public int Mail_Services_TimeExpires;
	public String Mail_Services_BaseUrl;
	/**
	 * 增值业务查询
	 */
	public String Mail_IsOpen_ComeFrom;
	public String Mail_IsOpen_Encoding;
	public String Mail_IsOpen_MD5Key;
	public int Mail_IsOpen_TimeExpires;
	public String Mail_IsOpen_BaseUrl;
	/**
	 * 查询免费短彩次数
	 */
	public String Mail_QueryMS_ComeFrom;
	public String Mail_QueryMS_Encoding;
	public String Mail_QueryMS_MD5Key;
	public int Mail_QueryMS_TimeExpires;
	public String Mail_QueryMS_BaseUrl;
	/**
	 * 扣除免费短彩数
	 */
	public String Mail_ReduceMS_ComeFrom;
	public String Mail_ReduceMS_Encoding;
	public String Mail_ReduceMS_MD5Key;
	public int Mail_ReduceMS_TimeExpires;
	public String Mail_ReduceMS_BaseUrl;
	/**
	 * 版本适配
	 */
	public String Mail_Adapt_Encoding;
	public String Mail_Adapt_Comefrom;
	public String Mail_Adapt_BaseUrl;	
	/**
	 * 发送短信编码
	 */
	public String SMS_Send_Encoding;
	/**
	 * 发送短信OperCode
	 */
	public String SMS_Send_OperCode;
	/**
	 * 发送短信AppId
	 */
	public String SMS_Send_AppId;
	/**
	 * 发送短信接口地址
	 */
	public String SMS_Send_BaseUrl;
	
	/**
	 * 发送彩信编码
	 */
	public String MMS_Send_Encoding;
	/**
	 * 发送彩信MD5签名密钥
	 */
	public String MMS_Send_MD5Key;
	/**
	 * 发送彩信接口超时时间，单位秒
	 */
	public int MMS_Send_TimeExpires;
	/**
	 * 发送彩信接口地址
	 */
	public String MMS_Send_BaseUrl;
	
	public String Disk_Login_Encoding;
	public String Disk_Login_Cmd;
	public String Disk_Login_ClienType;
	public String Disk_Login_Channel;
	public String Disk_Login_BaseUrl;
	public String Disk_Init_Encoding;
	public String Disk_Init_BaseUrl;
	public String Disk_Upload_Encoding;
	public String Disk_Upload_BaseUrl;
	/**
	 * 通讯录接口字符编码
	 */
	public String Contact_Encoding;
	/**
	 *  分页查询联系人接口/查询指定联系人/查询指定首字母联系人 接口地址，包含所有字段
	 */
	public String Contact_QueryContacts;
	/**
	 * 模糊查询联系人地址（精简字段）
	 */
	public String Contact_SearchContacts;
	/**
	 * 分页查询所有联系人接口地址（精简字段）
	 */
	public String Contact_QueryAllContacts;
	/**
	 * 按首字母查询所有联系人接口地址(精简字段)
	 */
	public String Contact_QueryContactsByName;
	/**
	 * 获取最近/紧密联系人接口地址
	 */
	public String Contact_GetLCContacts;
	/**
	 * 获取最近10天将要过生日的联系人接口地址
	 */
	public String Contact_GetRecentBirday;
	/**
	 * 添加联系人接口地址
	 */
	public String Contact_AddContacts;
	/**
	 * 添加用户自身信息接口
	 */
	public String Contact_AddUserInfo;
	/**
	 * 修改用户自身信息接口
	 */
	public String Contact_ModUserInfo;
	
	/**
	 * 查询用户自身信息的接口地址
	 */
	public String Contact_QueryUserInfo;
	/**
	 * 通讯录用户头像根地址
	 */
	public String Contact_HeaderImageBaseUrl;
	
	/** 精品订阅接口字符编码 **/
	public String JPDY_Encoding;
  	/** 渠道定义：1	SMS，2	WEB，3	WAP，4	后台批量开通，5	客服，6	广东MISC，7	客户端  	 **/
  	public String JPDY_Channel;
  	/** 来源定义：522 用户中心 **/
  	public String JPDY_Comefrom;
  	/** 客户端定义：2004	139邮箱用户中心 **/
  	public String JPDY_Clientid;
  	/** 操作人ID **/
  	public String JPDY_Operatorid;
  	/** 接口根地址 **/
  	public String JPDY_Baseurl;
  	/** 免费订阅接口 **/
  	public String JPDY_Subscribe;
  	/** 退订免费接口 **/
  	public String JPDY_Unsubscribe;
  	/** 收费订阅接口 **/
  	public String JPDY_Feesubscribe;
  	/** 退订收费接口 **/
  	public String JPDY_Feeunsubscribe;
  	/** 设置到达提醒 **/
  	public String JPDY_Setnotify;
  	/** 查询订阅关系 **/
  	public String JPDY_Queryusersetting;
  	/** 日程订阅 **/
  	public String Calendar_Encoding;
  	public String Calendar_Comefrom;
  	public String Calendar_BaseUrl;
	/**
	 *  构造API配置文件对象，并读取配置文件中的信息初始化该对象
	 * @param path 配置文件路径
	 * @throws ParserConfigException
	 */
	ApiConfig(String path) throws ParserConfigException{
		super(path);
		//Wap 短地址登录活动
		Wap_Login_MD5Key = getXPathValue("/apiconfig/wap/wapjump/md5key");
		Wap_Login_Expires = TryParse.toInt(getXPathValue("/apiconfig/wap/wapjump/timeexpires"),20);
		//SSO 登录运营活动
		SSO_OPSSO_MD5Key = getXPathValue("/apiconfig/sso/opsso/md5key");
		SSO_OPSSO_DESKey = getXPathValue("/apiconfig/sso/opsso/deskey");
		SSO_OPSSO_Expires = TryParse.toInt(getXPathValue("/apiconfig/sso/opsso/timeexpires"),20);
		SSO_OPSSO_JumpUrl = getXPathValue("/apiconfig/sso/opsso/jumpurl");
		SSO_OPSSO_ForMail = getXPathValue("/apiconfig/sso/opsso/ssoformail");
		
		SSO_REGLOGIN_Encoding = getXPathValue("/apiconfig/sso/reglogin/encoding");
		SSO_REGLOGIN_ClientID  = getXPathValue("/apiconfig/sso/reglogin/clientid");
		SSO_REGLOGIN_MD5Key = getXPathValue("/apiconfig/sso/reglogin/md5key");
		SSO_REGLOGIN_DESKey = getXPathValue("/apiconfig/sso/reglogin/deskey");
		SSO_REGLOGIN_Expires = TryParse.toInt(getXPathValue("/apiconfig/sso/reglogin/timeexpires"),20);
		SSO_REGLOGIN_RegUrl = getXPathValue("/apiconfig/sso/reglogin/regurl");
		SSO_REGLOGIN_LoginUrl = getXPathValue("/apiconfig/sso/reglogin/loginurl");
		SSO_REGLOGIN_WapLoginUrl = getXPathValue("/apiconfig/sso/reglogin/waploginurl");
		//积分
		Mail_Integral_Host = getXPathValue("/apiconfig/mail/integral/host");
		Mail_Integral_Port = TryParse.toInt(getXPathValue("/apiconfig/mail/integral/port"));
		Mail_Integral_Encoding = getXPathValue("/apiconfig/mail/integral/encoding");
		Mail_Integral_TimeExpires = TryParse.toInt(getXPathValue("/apiconfig/mail/integral/timeexpires"));
		//Web 登录校验
		Mail_ValidateLogin_ComeFrom = getXPathValue("/apiconfig/mail/userlogin/comefrom");
		Mail_ValidateLogin_MD5Key = getXPathValue("/apiconfig/mail/userlogin/md5key");
		Mail_ValidateLogin_TimeExpires = TryParse.toInt(getXPathValue("/apiconfig/mail/userlogin/timeexpires"));
		Mail_ValidateLogin_BaseURL = getXPathValue("/apiconfig/mail/userlogin/requesturl");
		//SSO 登录到邮箱
		Mail_SSOUrl_ComeFrom = getXPathValue("/apiconfig/mail/userloginurl/comefrom");
		Mail_SSOUrl_BaseUrl = getXPathValue("/apiconfig/mail/userloginurl/requesturl");
		//用户属性
		Mail_Attribute_ComeFrom=getXPathValue("/apiconfig/mail/userattribute/comefrom");
		Mail_Attribute_Encoding=getXPathValue("/apiconfig/mail/userattribute/encoding");
		Mail_Attribute_MD5Key=getXPathValue("/apiconfig/mail/userattribute/md5key");
		Mail_Attribute_TimeExpires=TryParse.toInt(getXPathValue("/apiconfig/mail/userattribute/timeexpires"));
		Mail_Attribute_BaseUrl=getXPathValue("/apiconfig/mail/userattribute/requesturl");
		//用户手机号码品牌信息
		Mail_CardType_ComeFrom=getXPathValue("/apiconfig/mail/cardtypeinfo/comefrom");
		Mail_CardType_Encoding=getXPathValue("/apiconfig/mail/cardtypeinfo/encoding");
		Mail_CardType_MD5Key=getXPathValue("/apiconfig/mail/cardtypeinfo/md5key");
		Mail_CardType_TimeExpires=TryParse.toInt(getXPathValue("/apiconfig/mail/cardtypeinfo/timeexpires"));
		Mail_CardType_BaseUrl=getXPathValue("/apiconfig/mail/cardtypeinfo/requesturl");
		//用户注册
		Mail_Register_ComeFrom=getXPathValue("/apiconfig/mail/userregister/comefrom");
		Mail_Register_Encoding=getXPathValue("/apiconfig/mail/userregister/encoding");
		Mail_Register_MD5Key=getXPathValue("/apiconfig/mail/userregister/md5key");
		Mail_Register_TimeExpires=TryParse.toInt(getXPathValue("/apiconfig/mail/userregister/timeexpires"));
		Mail_Register_BaseUrl=getXPathValue("/apiconfig/mail/userregister/requesturl");
		//根据SessionKey得到用户名接口配置
		Mail_Userbykey_ComeFrom=getXPathValue("/apiconfig/mail/userbykey/comefrom");
		Mail_Userbykey_Encoding=getXPathValue("/apiconfig/mail/userbykey/encoding");
		Mail_Userbykey_MD5Key=getXPathValue("/apiconfig/mail/userbykey/md5key");
		Mail_Userbykey_TimeExpires=TryParse.toInt(getXPathValue("/apiconfig/mail/userbykey/timeexpires"));
		Mail_Userbykey_BaseUrl=getXPathValue("/apiconfig/mail/userbykey/requesturl");
		
		SMS_Send_Encoding = getXPathValue("/apiconfig/sms/smsmsgsend/encoding");
		SMS_Send_OperCode = getXPathValue("/apiconfig/sms/smsmsgsend/opercode");
		SMS_Send_AppId = getXPathValue("/apiconfig/sms/smsmsgsend/appid");
		SMS_Send_BaseUrl = getXPathValue("/apiconfig/sms/smsmsgsend/requesturl");
		
		MMS_Send_Encoding = getXPathValue("/apiconfig/mms/mmsmsgsend/encoding");
		MMS_Send_MD5Key = getXPathValue("/apiconfig/mms/mmsmsgsend/md5key");
		MMS_Send_TimeExpires = TryParse.toInt(getXPathValue("/apiconfig/mms/mmsmsgsend/timeexpires"));
		MMS_Send_BaseUrl = getXPathValue("/apiconfig/mms/mmsmsgsend/requesturl");
		
		Mail_QueryBill_ComeFrom = getXPathValue("/apiconfig/mail/querybill/comefrom");
		Mail_QueryBill_Encoding = getXPathValue("/apiconfig/mail/querybill/encoding");
		Mail_QueryBill_MD5Key = getXPathValue("/apiconfig/mail/querybill/md5key");
		Mail_QueryBill_TimeExpires = TryParse.toInt(getXPathValue("/apiconfig/mail/querybill/timeexpires"));
		Mail_QueryBill_BaseUrl = getXPathValue("/apiconfig/mail/querybill/requesturl");
		
		Mail_OpenBill_ComeFrom = getXPathValue("/apiconfig/mail/billmailing/comefrom");
		Mail_OpenBill_Encoding = getXPathValue("/apiconfig/mail/billmailing/encoding");
		Mail_OpenBill_MD5Key = getXPathValue("/apiconfig/mail/billmailing/md5key");
		Mail_OpenBill_TimeExpires = TryParse.toInt(getXPathValue("/apiconfig/mail/billmailing/timeexpires"));
		Mail_OpenBill_BaseUrl = getXPathValue("/apiconfig/mail/billmailing/requesturl");
		
		Mail_QueryMailNum_Version = getXPathValue("/apiconfig/mail/querymailnum/version");
		Mail_QueryMailNum_Encoding = getXPathValue("/apiconfig/mail/querymailnum/encoding");
		Mail_QueryMailNum_BaseUrl = getXPathValue("/apiconfig/mail/querymailnum/requesturl");
		
		Mail_SmsNotify_ComeFrom = getXPathValue("/apiconfig/mail/mailnotify/comefrom");
		Mail_SmsNotify_Encoding = getXPathValue("/apiconfig/mail/mailnotify/encoding");
		Mail_SmsNotify_MD5Key = getXPathValue("/apiconfig/mail/mailnotify/md5key");
		Mail_SmsNotify_TimeExpires = TryParse.toInt(getXPathValue("/apiconfig/mail/mailnotify/timeexpires"));
		Mail_SmsNotify_BaseUrl = getXPathValue("/apiconfig/mail/mailnotify/requesturl");
		
		Mail_Services_ComeFrom = getXPathValue("/apiconfig/mail/incrementopen/comefrom");
		Mail_Services_Encoding = getXPathValue("/apiconfig/mail/incrementopen/encoding");
		Mail_Services_MD5Key = getXPathValue("/apiconfig/mail/incrementopen/md5key");
		Mail_Services_TimeExpires = TryParse.toInt(getXPathValue("/apiconfig/mail/incrementopen/timeexpires"));
		Mail_Services_BaseUrl = getXPathValue("/apiconfig/mail/incrementopen/requesturl");
		
		Mail_IsOpen_ComeFrom = getXPathValue("/apiconfig/mail/userisopen/comefrom");
		Mail_IsOpen_Encoding = getXPathValue("/apiconfig/mail/userisopen/encoding");
		Mail_IsOpen_MD5Key = getXPathValue("/apiconfig/mail/userisopen/md5key");
		Mail_IsOpen_TimeExpires = TryParse.toInt(getXPathValue("/apiconfig/mail/userisopen/timeexpires"));
		Mail_IsOpen_BaseUrl = getXPathValue("/apiconfig/mail/userisopen/requesturl");
		
		Mail_QueryMS_ComeFrom = getXPathValue("/apiconfig/mail/userquerysmsormms/comefrom");
		Mail_QueryMS_Encoding = getXPathValue("/apiconfig/mail/userquerysmsormms/encoding");
		Mail_QueryMS_MD5Key = getXPathValue("/apiconfig/mail/userquerysmsormms/md5key");
		Mail_QueryMS_TimeExpires = TryParse.toInt(getXPathValue("/apiconfig/mail/userquerysmsormms/timeexpires"));
		Mail_QueryMS_BaseUrl = getXPathValue("/apiconfig/mail/userquerysmsormms/requesturl");
		
		Mail_ReduceMS_ComeFrom = getXPathValue("/apiconfig/mail/userreducesmsormms/comefrom");
		Mail_ReduceMS_Encoding = getXPathValue("/apiconfig/mail/userreducesmsormms/encoding");
		Mail_ReduceMS_MD5Key = getXPathValue("/apiconfig/mail/userreducesmsormms/md5key");
		Mail_ReduceMS_TimeExpires = TryParse.toInt(getXPathValue("/apiconfig/mail/userreducesmsormms/timeexpires"));
		Mail_ReduceMS_BaseUrl = getXPathValue("/apiconfig/mail/userreducesmsormms/requesturl");
		/** 版本适配 **/
		Mail_Adapt_Encoding = getXPathValue("/apiconfig/adapt/getpropversion/encoding");
		Mail_Adapt_Comefrom = getXPathValue("/apiconfig/adapt/getpropversion/comefrom");
		Mail_Adapt_BaseUrl = getXPathValue("/apiconfig/adapt/getpropversion/requesturl");
		//网盘登录接口
		Disk_Login_Encoding= getXPathValue("/apiconfig/disk/login/encoding");
		Disk_Login_Cmd= getXPathValue("/apiconfig/disk/login/cmd");
		Disk_Login_ClienType= getXPathValue("/apiconfig/disk/login/clientype");
		Disk_Login_Channel= getXPathValue("/apiconfig/disk/login/channel");
		Disk_Login_BaseUrl= getXPathValue("/apiconfig/disk/login/requesturl");
		Disk_Init_Encoding= getXPathValue("/apiconfig/disk/Init/encoding");
		Disk_Init_BaseUrl = getXPathValue("/apiconfig/disk/Init/requesturl");
		Disk_Upload_Encoding= getXPathValue("/apiconfig/disk/Upload/encoding");
		Disk_Upload_BaseUrl = getXPathValue("/apiconfig/disk/upload/requesturl");
		//通讯录接口
		Contact_Encoding = getXPathValue("/apiconfig/contact/encoding");
		Contact_QueryContacts = getXPathValue("/apiconfig/contact/querycontacts");
		Contact_SearchContacts = getXPathValue("/apiconfig/contact/searchcontacts");
		Contact_QueryAllContacts = getXPathValue("/apiconfig/contact/queryallcontacts");
		Contact_QueryContactsByName = getXPathValue("/apiconfig/contact/querycontactsbyname");
		Contact_GetLCContacts = getXPathValue("/apiconfig/contact/getlccontacts");
		Contact_AddContacts = getXPathValue("/apiconfig/contact/addcontacts");
		Contact_AddUserInfo = getXPathValue("/apiconfig/contact/adduserinfo");
		Contact_ModUserInfo = getXPathValue("/apiconfig/contact/modeuserinfo");
		Contact_QueryUserInfo = getXPathValue("/apiconfig/contact/queryuserinfo");
		Contact_HeaderImageBaseUrl =getXPathValue("/apiconfig/contact/headerimagebaseurl");
		Contact_GetRecentBirday =getXPathValue("/apiconfig/contact/getrecentbirday");
		//日程订阅
		Calendar_Encoding = getXPathValue("/apiconfig/calendar/encoding");
		Calendar_Comefrom = getXPathValue("/apiconfig/calendar/comefrom");
		Calendar_BaseUrl = getXPathValue("/apiconfig/calendar/requesturl");
		//云邮局/精品订阅
		JPDY_Encoding = getXPathValue("/apiconfig/jpdy/encoding");
	  	JPDY_Channel = getXPathValue("/apiconfig/jpdy/channel");
	  	JPDY_Comefrom = getXPathValue("/apiconfig/jpdy/comefrom");
	  	JPDY_Clientid = getXPathValue("/apiconfig/jpdy/clientid");
	  	JPDY_Operatorid = getXPathValue("/apiconfig/jpdy/operatorid");
	  	JPDY_Baseurl = getXPathValue("/apiconfig/jpdy/baseurl");
	  	JPDY_Subscribe = getXPathValue("/apiconfig/jpdy/subscribe");
	  	JPDY_Unsubscribe = getXPathValue("/apiconfig/jpdy/unsubscribe");
	  	JPDY_Feesubscribe = getXPathValue("/apiconfig/jpdy/feesubscribe");
	  	JPDY_Feeunsubscribe = getXPathValue("/apiconfig/jpdy/feeunsubscribe");
	  	JPDY_Setnotify = getXPathValue("/apiconfig/jpdy/setnotify");
	  	JPDY_Queryusersetting = getXPathValue("/apiconfig/jpdy/queryusersetting");
	}
}
