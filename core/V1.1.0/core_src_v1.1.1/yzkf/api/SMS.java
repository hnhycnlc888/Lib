package yzkf.api;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;

import cn.richinfo.core.utils.web.HttpClient;
import yzkf.api.result.SMSResult;
import yzkf.app.Pattern;
import yzkf.config.ApiConfig;
import yzkf.config.ConfigFactory;
import yzkf.enums.MSCmdType;
import yzkf.exception.ApiException;
import yzkf.exception.ParserConfigException;
import yzkf.utils.Utility;
import yzkf.utils.Xml;

/**
 * 短信接口处理类
 * @author qiulw
 * @version V4.0.0 2012.02.06
 */
public class SMS {
	//private static final Logger LOG = LoggerFactory.getLogger(SMS.class.getName());
	private ApiConfig config;
	public SMS() throws ParserConfigException{
		ConfigFactory factory = ConfigFactory.getInstance();
		this.config = factory.newApiConfig();
	}
	public SMS(ApiConfig config){
		this.config = config;
	}
	/**
	 * 发送方手机号，不带86
	 */
	private String sendMobile;
	/**
	 * 计费方手机号码
	 * <br/>设置时自动加86
	 */
	private String feeMobile;
	/**
	 * 网关id，默认广东网关
	 * <br/>yzkf.enums.SpsId属性
	 */
	private String spsId = yzkf.enums.SpsId.GD.getValue();
	/**
	 * 下发显示号码，最长21字符（含06139）
	 * <br/>不加前缀，系统自动加06139前缀
	 */
	private String spNumber = "06139";
	/**
	 * 短信内容，最长400字符
	 */
	private String sendMsg;
	/**
	 * 短信的发送来源
	 * <br/>yzkf.enums.SmsComeFrom属性
	 */
	private int comeFrom = yzkf.enums.SmsComeFrom.Normal.getValue();
	/**
	 * 发送类型：0、普通短信，1、长短信，2、免提短信
	 */
	private int sendType = 0;
	/**
	 * 优先级,优先级的定义根据各个应用配置
	 */
	private int priority = 0;
	/**
	 * 操作类型：用于行为统计
	 */
	private int operType = 100;
	/**
	 * 发送标志，是否定时
	 * <br/>0、不受限发送，1、根据StartSendTime+StartTime+EndTime进行发送
	 */
	private int sendFlag = 0;
	/**
	 * 开始发送时间
	 * <br/>格式为：YYYYMMDDHHmmss 如：20080319010101
	 */
	private String startSendTime=(new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
	/**
	 * 定点发送的开始钟点（0-1440）
	 */
	private int startTime = 0;
	/**
	 * 定点发送的结束钟点（0-1440）
	 */
	private int endTime = 0;
	/**
	 * 计费类别<br/><br/>
	 * 默认： 对“计费用户号码”免费	 
	 */
	private int feeType = 1;
	/**
	 * 发送短信所要收取的信息费，单位（分），默认为0分
	 */
	private int feeValue = 0;
	/**
	 * 组ID值，默认为空
	 */
	private String groupId = "";
	/**
	 * 业务代码，默认空，最长10个字符
	 */
	private String serviceType = "1000";
	/**
	 * 操作员号
	 */
	private String createOperator="";
	/**
	 * 客户端IP地址，格式：192.168.1.1
	 */
	private String clientIP;
	/**
	 * 调用方编号
	 * <br/>yzkf.enums.MSCmdType属性
	 */
	private int cmdType = MSCmdType.WebSms.getValue();	

	/**
	 * [必选]设置发送方手机号码，不带86
	 * @param sendMobile
	 */
	public void setSendMobile(String sendMobile) {
		if(!sendMobile.startsWith("86"))
			this.sendMobile = "86" + sendMobile;
		if(StringUtils.isEmpty(this.feeMobile))
			this.setFeeMobile(this.sendMobile);
		if(StringUtils.isEmpty(this.spNumber) || spNumber.equals("06139"))
			this.setSpNumber(this.sendMobile);
	}
	/**
	 * [可选]设置计费方手机号码 ，不带86
	 * <br/><br/>默认：使用发送方手机号
	 * @param feeMobile 不带86
	 */
	public void setFeeMobile(String feeMobile) {
		if(!feeMobile.startsWith("86"))
			this.feeMobile = "86" + feeMobile;
		else
			this.feeMobile = feeMobile;
	}
	/**
	 * [可选]设置网关ID
	 * <br/><br/>默认：广东网关 yzkf.enums.SpsId.GD
	 * @param spsId yzkf.enums.SpsId
	 */
	public void setSpsId(yzkf.enums.SpsId spsId) {
		this.spsId = spsId.getValue();
	}
	/**
	 * [可选]设置网关ID
	 * <br/><br/>默认：广东网关 yzkf.enums.SpsId.GD.getValue()
	 * @param spsId yzkf.enums.SpsId.getValue()
	 */
	public void setSpsId(String spsId) {
		this.spsId = spsId;
	}
	/**
	 * [可选]设置下端口号，即下发显示号码 ，长度不超过16
	 * 
	 * <br/>不需加前缀，设置后将自动加06139前缀
	 * <br/><br/>默认：06139+发送方号码
	 * @param spsNumber
	 */
	public void setSpNumber(String spNumber) {
		if(spNumber.startsWith("86"))
			spNumber = spNumber.substring(2);
		if(!spNumber.startsWith("06139"))
			this.spNumber = "06139" + spNumber;
		else
			this.spNumber = spNumber;
	}
	/**
	 * [必须]设置短信内容，最长400字符
	 * <br/>超长时可用“NL”进行切割
	 * @param sendMsg
	 */
	public void setSendMsg(String sendMsg) {
		this.sendMsg = sendMsg;
	}
	/**
	 * [可选]设置短信发送来源
	 * <br/><br/>默认：yzkf.enums.SmsComeFrom.Normal 即时发送短信（不活跃），如需活跃请选择yzkf.enums.SmsComeFrom.Active
	 * @param comeFrom yzkf.enums.SmsComeFrom
	 */
	public void setComeFrom(yzkf.enums.SmsComeFrom comeFrom) {		
		this.comeFrom = comeFrom.getValue();
	}
	/**
	 * [可选]设置短信发送来源
	 * <br/><br/>默认：yzkf.enums.SmsComeFrom.Normal 即时发送短信（不活跃），如需活跃请选择yzkf.enums.SmsComeFrom.Active
	 * @param comeFrom yzkf.enums.SmsComeFrom.getValue()
	 */
	public void setComeFrom(int comeFrom) {		
		this.comeFrom = comeFrom;
	}	
	/**
	 * [可选]设置发送类型
	 * <br/><br/>默认：普通短信
	 * @param sendType 0、普通短信，1、长短信，2、免提短信
	 */
	public void setSendType(int sendType) {
		this.sendType = sendType;
	}
	/**
	 * [可选]设置优先级
	 * <br/><br/>默认：0
	 * @param priority 优先级的定义根据各个应用配置
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	/**
	 * [可选]设置操作类型，用于记录用户行为
	 * <br/><br/>默认：100 (运营)
	 * @param operType
	 */
	public void setOperType(int operType) {
		this.operType = operType;
	}
	/**
	 * [可选]设置发送标志，是否定时；如果设置为定时，则必须设置setStartSendTime
	 * <br/><br/>默认：不受限发送
	 * @param sendFlag 0、不受限发送，1、根据StartSendTime+StartTime+EndTime进行发送
	 */
//	public void setSendFlag(int sendFlag) {
//		this.sendFlag = sendFlag;
//	}
	/**
	 * [可选]设置开始发送时间
	 * <br/><br/>默认：即时发送
	 * @param startSendTime 格式为：YYYYMMDDHHmmss 如：20120202020202
	 */
	public void setStartSendTime(Date startSendTime) {
		Date now = new Date();
		if(startSendTime.after(now)){
			this.sendFlag = 1;
			this.startSendTime = (new SimpleDateFormat("yyyyMMddHHmmss")).format(startSendTime);
		}else{
			this.sendFlag = 0;
			this.startSendTime = (new SimpleDateFormat("yyyyMMddHHmmss")).format(now);
		}
	}
	/**
	 * [可选]设置定点发送的开始钟点
	 * <br/><br/>默认：0
	 * @param startTime 0-1440
	 */
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	/**
	 * [可选]设置定点发送的结束钟点
	 * <br/><br/>默认：0
	 * @param endTime 0-1440
	 */
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}	
	/**
	 * [可选]设置计费类别<br/><br/>
	 * 默认： 1 (对“计费用户号码”免费)<br/>	 
	 * @param freeType 
	 * <br/>0:“短消息类型”为“发送”，对“计费用户号码”不计信息费，
	 * 此类话单仅用于核减SP对称的信道费<br/>
	 * 1:对“计费用户号码”免费<br/>
	 * 2:对“计费用户号码”按条计信息费<br/>
	 * 3:对“计费用户号码”按包月收取信息费<br/>
	 * 4:对“计费用户号码”的收费是由SP实现<br/>
	 * 5:予留计费类型，如按流量计费<br/>
	 */
	public void setFeeType(int feeType){
		this.feeType = feeType;
	}
	/**
	 * [可选]发送短信所要收取的信息费，单位（分）
	 * <br/><br/>默认：0
	 * @param feeValue 单位（分）
	 */
	public void setFeeValue(int feeValue){
		this.feeValue = feeValue;
	}
	/**
	 * [可选]设置组ID值
	 * 默认：空
	 * @param groupId
	 */
	public void setGroupId(String groupId){
		this.groupId = groupId;
	}
	/**
	 * [可选]设置操作员
	 * <br/><br/>默认：空
	 * @param createOperator
	 */
	public void setCreateOperator(String createOperator) {
		this.createOperator = createOperator;
	}
	/**
	 * [可选]设置业务代码
	 * <br/><br/>默认：1000
	 * @param serviceType
	 */
	public void setServiceType(String serviceType){
		this.serviceType=serviceType;
	}
	/**
	 * [必选]设置客户端IP地址
	 * @param clientIP 格式：192.168.1.1
	 */
	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}
	/**
	 * [可选]设置调用方编号
	 * 默认：WEB自写短信方式
	 * @param cmdType <code>yzkf.enums.MSCmdType</code>
	 */
	public void setCmdType(MSCmdType cmdType) {
		this.cmdType = cmdType.getValue();
	}
	/**
	 * [可选]设置调用方编号
	 * 默认：WEB自写短信方式
	 * @param cmdType <code>yzkf.enums.MSCmdType.getValue()</code>
	 */
	public void setCmdType(int cmdType) {
		this.cmdType = cmdType;
	}
	/**
	 * 获取接收方号码个数
	 */
	private int getReceiveNumber(){
		return this.receiveMobiles.size();
	}
	private Vector<String> receiveMobiles = new Vector<String>();
	/**
	 * 设置收件方号码，多个用英文逗号分开，不带86
	 * @param mobiles 收件方号码，多个用英文逗号分开，如：13912341234,13956785678
	 */
	public void setReceives(String mobiles){
		if(StringUtils.isEmpty(mobiles))
			throw new NullPointerException("收件人号码为null");
		String[] arrMobiles = mobiles.split(",");
		for(int i=0;i<arrMobiles.length;i++){
			this.addReceive(arrMobiles[i]);
		}		
	}
	/**
	 * 添加接收方号码
	 * @param mobile 手机号码，不带86
	 * @return true 添加陈功；false 添加失败或已存在或已超过限制
	 */
	public boolean addReceive(String mobile){		
		if(!mobile.startsWith("86"))
			mobile = "86" + mobile;
		if(this.receiveMobiles.indexOf(mobile) < 0){
			if(this.getReceiveNumber() >=20)
				return false;
			return this.receiveMobiles.add(mobile);
		}
		return false;			
	}
	/**
	 * 删除接受方号码
	 * @param mobile 手机号码，不带86
	 * @return true 删除成功；false 删除失败或不存在
	 */
	public boolean removeReceive(String mobile){
		if(!mobile.startsWith("86"))
			mobile = "86" + mobile;
		if(this.receiveMobiles.indexOf(mobile) > 0){
			return this.receiveMobiles.remove(mobile);
		}
		return false;	
	}
	/**
	 * 获取接口执行后返回的信息
	 */
	public String Message;
	/**
	 * 发送短信
	 * @param body 短信内容，长度不大于400字符
	 * @return 返送结果 yzkf.enums.SmsEnum
	 * @throws ApiException 接口异常
	 * @throws ParserConfigException 读取配置文件异常
	 */
	public SMSResult send(String body) throws ApiException, ParserConfigException{
		this.sendMsg = body;
		return this.send();
	}
	/**
	 * 发送短信
	 * @param sender 发送方手机号码，不带86
	 * @param receiver 接收方手机号码，不带86
	 * @param body 短信内容，长度不大于400字符
	 * @param clientIP 客户短IP地址
	 * @return 返送结果 yzkf.enums.SmsEnum
	 * @throws ApiException 接口异常
	 * @throws ParserConfigException 读取配置文件异常
	 */
	public SMSResult send(String sender,String receiver,String body,String clientIP) throws ApiException, ParserConfigException{
		this.sendMobile = sender;
		this.sendMsg = body;
		this.clientIP = clientIP;
		this.addReceive(receiver);
		return this.send();
	}
	/**
	 * 发送短信
	 * @return 返送结果 yzkf.enums.SmsEnum
	 * @throws ApiException 接口异常
	 * @throws ParserConfigException 读取配置文件异常
	 */
	public SMSResult send() throws ApiException, ParserConfigException{
		//发送方的手机号码不能为空
        if (!Pattern.isMobile86(this.sendMobile))
            return SMSResult.InvalidSender;
        //计费方的手机号码不能为空
        if (!Pattern.isMobile86(this.feeMobile))
        	return SMSResult.InvalidFeeMobile;
        //发送的长端口号不能为空
        if (!Pattern.isSpNumber(this.spNumber))
        	return SMSResult.InvalidSpNumber;
        //发送的内容不能为空
        if(StringUtils.isEmpty(this.sendMsg))
        	throw new NullPointerException("下发短信内容为空");
        //短信内容长度超过限制
        if (!Pattern.isSmsBody(this.sendMsg))
        	return SMSResult.InvalidSmsBody;
        //客户端IP不能为空
        if (!Pattern.isIpaddress(this.clientIP))
        	throw new ApiException("客户端IP地址格式错误");
        //接收方的手机号码不能为空
        if (this.getReceiveNumber() <= 0)
        	throw new NullPointerException("接收方手机号码不能为空");
        String postData = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            +"<Root>"
	            +"<OperCode>"+this.config.SMS_Send_OperCode+"</OperCode>"
	            +"<AppId>"+this.config.SMS_Send_AppId+"</AppId>"
	            +"<Req>"
		            +"<UserNumber>"+this.sendMobile+"</UserNumber>"
		            +"<UserMobile>"+this.feeMobile+"</UserMobile>"
		            +"<SpsId>"+this.spsId+"</SpsId>"
		            +"<SpNumber>"+this.spNumber+"</SpNumber>"
					+"<SendMsg>"+this.sendMsg+"</SendMsg>"
					+"<ComeFrom>"+this.comeFrom+"</ComeFrom>"
					+"<SendType>"+this.sendType+"</SendType>"
					+"<Priority>"+this.priority+"</Priority>"
					+"<OperType>"+this.operType+"</OperType>"
					+"<SendFlag>"+this.sendFlag+"</SendFlag>"
					+"<StartSendTime>"+this.startSendTime+"</StartSendTime>"
					+"<StartTime>"+this.startTime+"</StartTime>"
					+"<EndTime>"+this.endTime+"</EndTime>"
					+"<FeeType>"+this.feeType+"</FeeType>"
					+"<FeeValue>"+this.feeValue+"</FeeValue>"
					+"<GroupId>"+this.groupId+"</GroupId>"
					+"<CreateOperator>"+this.createOperator+"</CreateOperator>"
					+"<ServiceType>"+this.serviceType+"</ServiceType>"
					+"<ClientIP>"+this.clientIP+"</ClientIP>"
					+"<CmdType>"+this.cmdType+"</CmdType>"
					+"<Mobiles>"
						+"<Number>"+this.getReceiveNumber()+"</Number>";
        for(int i=0;i<this.getReceiveNumber();i++){
        	String mobile = this.receiveMobiles.get(i);
        	if(!Pattern.isMobile86(mobile))
        		return SMSResult.InvalidReceiver;
        	else
        		postData += "<Mobile>"+mobile+"</Mobile>";
        }
        postData +=	 "</Mobiles>"
				+"</Req>"
			+"</Root>";
        /**
         * 发起请求
         */
        String out = null;
		try {
			out = HttpClient.post(this.config.SMS_Send_BaseUrl, postData,this.config.SMS_Send_Encoding);
		} catch (IOException e) {
			ApiException ex = new ApiException("发送短信接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		Xml outXml = null;
		try {
			outXml = Xml.parseXml(out);
		}catch(Exception e){
			ApiException ex = new ApiException("发送短信接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}
		String outRetCode = "";
		try {
			String outOperCode = outXml.evaluate("/Root/OperCode");				
			//String outAppId = outXml.evaluate("/Root/AppId");	
			outRetCode = outXml.evaluate("/Root/ResultCode");
			this.Message = outXml.evaluate("/Root/ResultMsg");
			if(!outOperCode.equals(this.config.SMS_Send_OperCode))
				throw new ApiException("发送短信接口异常，发送OperCode和返回OperCode不相符");
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("发送短信接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}
		if(outRetCode.equals("000"))
			return SMSResult.OK;
		//部分成功
		if(outRetCode.equals("100"))
			return SMSResult.PartialOK;
		if(outRetCode.equals("999"))
			throw new ApiException("短信平台内部错误");
		if(outRetCode.equals("001"))
			return SMSResult.InvalidSender;
		if(outRetCode.equals("002"))
			return SMSResult.InvalidReceiver;
		if(outRetCode.equals("003"))
			return SMSResult.TooManyReceiver;
		if(outRetCode.equals("004"))
			return SMSResult.InvalidBodyEncode;
		if(outRetCode.equals("005"))
			return SMSResult.InvalidSendTime;
		if(outRetCode.equals("006"))
			return SMSResult.InvalidFeeType;
		if(outRetCode.equals("007"))
			throw new ApiException("发送短信接口返回错误信息：请求消息包不正确");
		if(outRetCode.equals("008"))
			throw new ApiException("发送短信接口返回错误信息：查询号段失败");
		if(outRetCode.equals("009"))
			return SMSResult.BlacklistUser4Business;
		if(outRetCode.equals("010"))
			throw new ApiException("发送短信接口返回错误信息：订购关系里不存在相关数据");
		if(outRetCode.equals("011"))
			return SMSResult.ContainFilterWord;
		if(outRetCode.equals("012"))
			return SMSResult.BlacklistUser4Sms;
		if(outRetCode.equals("013"))
			return SMSResult.DayLimit;
		if(outRetCode.equals("014"))
			return SMSResult.MonthLImit;
		if(outRetCode.equals("015"))
			return SMSResult.PartialLimit;
		if(outRetCode.equals("016"))
			return SMSResult.OkButOper;
		if(outRetCode.equals("017"))
			return SMSResult.NoFreeSms;
		if(outRetCode.equals("018"))
			return SMSResult.BlacklistIP;
		return SMSResult.Unknow;
	}
}
