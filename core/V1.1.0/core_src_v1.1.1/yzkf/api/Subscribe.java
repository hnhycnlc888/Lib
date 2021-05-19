package yzkf.api;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;

import cn.richinfo.core.utils.web.HttpClient;
import yzkf.api.result.SubscribeResult;
import yzkf.app.Pattern;
import yzkf.config.ApiConfig;
import yzkf.config.ConfigFactory;
import yzkf.exception.ApiException;
import yzkf.exception.ParserConfigException;
import yzkf.utils.Utility;

public class Subscribe {
	private ApiConfig config;
	public Subscribe() throws ParserConfigException{
		ConfigFactory factory = ConfigFactory.getInstance();
		this.config = factory.newApiConfig();
	}
	public Subscribe(ApiConfig config){
		this.config = config;
	}
	/**
	 * 云邮局订阅接口
	 * @param mobile 用户手机号码（不含86）
	 * @param columnId 栏目ID
	 * @return 订阅接口结果{@link SubscribeResult}
	 * @throws ApiException
	 * @示例
	 * <pre>
Subscribe api = new Subscribe();
SubscribeResult result = null;
try {
	result = api.subscrie("13730124054", "19497");
} catch (ApiException e) {
	e.printStackTrace();
}
if(result.isOK()){
	System.out.println("订阅成功");
}else if(result==SubscribeResult.Failed){
	System.out.println("订阅失败");
}else{
	System.out.println("接口执行失败："+result.getDescr());
}
	 * </pre>
	 */
	public SubscribeResult subscribe(String mobile,String columnId) throws ApiException{
		return subscribeColumn(mobile, columnId, false);
	}
	/**
	 * 云邮局退订接口
	 * @param mobile 手机号码（不含86）
	 * @param columnId 要退订的栏目ID
	 * @return 退订接口结果{@link SubscribeResult}
	 * @throws ApiException
	 * @示例
	 * <pre>
Subscribe api = new Subscribe();
SubscribeResult result = null;
try {
	result = api.unsubscribe("13730124054", "19497");
} catch (ApiException e) {
	e.printStackTrace();
}
if(result.isOK()){
	System.out.println("退订成功");
}else if(result==SubscribeResult.Failed){
	System.out.println("退订失败");
}else{
	System.out.println("接口执行失败："+result.getDescr());
}
	 * </pre>
	 */
	public SubscribeResult unsubscribe(String mobile,String columnId) throws ApiException{
		return unsubscribeColumn(mobile, columnId, false);
	}
	/**
	 * 云邮局收费栏目订阅接口
	 * @param mobile 用户手机号码（不含86）
	 * @param columnId 栏目ID
	 * @return 订阅接口结果{@link SubscribeResult}
	 * @throws ApiException
	 * @示例
	 * <pre>
Subscribe api = new Subscribe();
SubscribeResult result = null;
try {
	result = api.feeSubscribe("13730124054", "38596");
} catch (ApiException e) {
	e.printStackTrace();
}
if(result.isOK()){
	System.out.println("订阅成功");
}else if(result==SubscribeResult.Failed){
	System.out.println("订阅失败");
}else{
	System.out.println("接口执行失败："+result.getDescr());
}
	 * </pre>
	 */
	public SubscribeResult feeSubscribe(String mobile,String columnId) throws ApiException{
		return subscribeColumn(mobile, columnId, true);
	}
	/**
	 * 云邮局收费栏目退订接口
	 * @param mobile 手机号码（不含86）
	 * @param columnId 要退订的栏目ID
	 * @return 退订接口结果{@link SubscribeResult}
	 * @throws ApiException
	 * @示例
	 * <pre>
Subscribe api = new Subscribe();
SubscribeResult result = null;
try {
	result = api.feeUnsubscribe("13730124054", "38596");
} catch (ApiException e) {
	e.printStackTrace();
}
if(result.isOK()){
	System.out.println("退订成功");
}else if(result==SubscribeResult.Failed){
	System.out.println("退订失败");
}else{
	System.out.println("接口执行失败："+result.getDescr());
}
	 * </pre>
	 */
	public SubscribeResult feeUnsubscribe(String mobile,String columnId) throws ApiException{
		return unsubscribeColumn(mobile, columnId, true);
	}
	/**
	 * 云邮局订阅接口
	 * @param mobile 用户手机号码（不含86）
	 * @param columnId 栏目ID
	 * @param isFee 是否收费栏目：true 收费，false 免费
	 * @return 订阅接口结果{@link SubscribeResult}
	 * @throws ApiException
	 * 
	 */
	private SubscribeResult subscribeColumn(String mobile,String columnId,boolean isFee) throws ApiException{
		if(StringUtils.isEmpty(mobile))
			return SubscribeResult.EmptyMobile;	//空账户		
		if(!Pattern.isMobile139alias(mobile))
			return SubscribeResult.InvalidMobile;	//无效账号

		String verifyID = "";//UUID.randomUUID().toString();
		String currDate=  Utility.formatDate(new Date(), "yyyy-MM-dd");
		StringBuffer xmlMsg =  new StringBuffer();
		xmlMsg.append("{\"Message\":{\"header\":{\"verify\":\"").append(verifyID).append("\",\"clientId\":")
				.append(config.JPDY_Clientid).append(",\"operatorId\":").append(config.JPDY_Operatorid)
		 		.append(",\"operateTime\":\""+currDate+"\"}").append(",\"body\":{\"columnId\":").append(columnId)
		 		.append(",\"userNumber\":\"").append("86"+mobile).append("\",\"channel\":").append(config.JPDY_Channel)
		 		.append(",\"comeFrom\":").append(config.JPDY_Comefrom).append(",\"filterPattern\":1").append("}}}");
		String strRequestData = xmlMsg.toString();
		
		String  url =  config.JPDY_Baseurl+(isFee?config.JPDY_Feesubscribe:config.JPDY_Subscribe);
		String out = null;
		try {
			out = HttpClient.post(url,strRequestData,config.JPDY_Encoding);
		} catch (IOException e) {
			ApiException ex = new ApiException("云邮局订阅接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		try{
			JSONObject outJson = JSONObject.parseObject(out);
			
			JSONObject message = outJson.getJSONObject("Message");
			JSONObject header = message.getJSONObject("header");
			String outRetCode = header.getString("resultCode");
			String outRetMsg = header.getString("description");
//			String outVerify = header.getString("verify");
//			if(!outVerify.equalsIgnoreCase(verifyID)){			
//				return SubscribeResult.ApiBusy;
//			}
	
			if(outRetCode.equalsIgnoreCase("0")){
				JSONObject body = message.getJSONObject("body");
				int outSubResult = body.getIntValue("returnCode");
				if(outSubResult == 10 || outSubResult == 11)
					return SubscribeResult.OK;
				else
					return SubscribeResult.Failed;
			}else{
				//返回未知结果
				return SubscribeResult.Other.setValue(outRetMsg);
			}
		}catch(Exception je){
			ApiException ex = new ApiException("云邮局订阅接口返回值转换为JSON时发生异常，返回值："+out);				
			ex.initCause(je);
			throw ex;
		}
	}
	/**
	 * 云邮局退订接口
	 * @param mobile 手机号码（不含86）
	 * @param columnId 要退订的栏目ID
	 * @param isFee 是否收费栏目：true 收费，false 免费
	 * @return 退订接口结果{@link SubscribeResult}
	 * @throws ApiException
	 */
	private SubscribeResult unsubscribeColumn(String mobile,String columnId,boolean isFee) throws ApiException{
		if(StringUtils.isEmpty(mobile))
			return SubscribeResult.EmptyMobile;	//空账户		
		if(!Pattern.isMobile139alias(mobile))
			return SubscribeResult.InvalidMobile;	//无效账号

		String verifyID = "";//UUID.randomUUID().toString();
		String currDate=  Utility.formatDate(new Date(), "yyyy-MM-dd");
		StringBuffer xmlMsg =  new StringBuffer();
		xmlMsg.append("{\"Message\":{\"header\":{\"verify\":\"").append(verifyID).append("\",\"clientId\":")
				.append(config.JPDY_Clientid).append(",\"operatorId\":").append(config.JPDY_Operatorid)
		 		.append(",\"operateTime\":\""+currDate+"\"}").append(",\"body\":{\"columnId\":").append(columnId)
		 		.append(",\"userNumber\":\"").append("86"+mobile).append("\",\"channel\":").append(config.JPDY_Channel)
		 		.append(",\"comeFrom\":").append(config.JPDY_Comefrom).append(",\"filterPattern\":1").append("}}}");
		 
		String strRequestData = xmlMsg.toString();
		
		String  url =  config.JPDY_Baseurl+(isFee?config.JPDY_Feeunsubscribe:config.JPDY_Unsubscribe);
		String out = null;
		try {
			out = HttpClient.post(url,strRequestData,config.JPDY_Encoding);
		} catch (IOException e) {
			ApiException ex = new ApiException("云邮局退订接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		try{
			JSONObject outJson = JSONObject.parseObject(out);
			
			JSONObject message = outJson.getJSONObject("Message");
			JSONObject header = message.getJSONObject("header");
			String outRetCode = header.getString("resultCode");
			String outRetMsg = header.getString("description");
//			String outVerify = header.getString("verify");
//			if(!outVerify.equalsIgnoreCase(verifyID)){			
//				return SubscribeResult.ApiBusy;
//			}
			
			if(outRetCode.equalsIgnoreCase("0")){
				JSONObject body = message.getJSONObject("body");
				int outSubResult = body.getIntValue("returnCode");
				if(outSubResult == 20 || outSubResult == 21)
					return SubscribeResult.OK;
				else
					return SubscribeResult.Failed;
			}else{
				//返回未知结果
				return SubscribeResult.Other.setValue(outRetMsg);
			}
		}catch(Exception je){
			ApiException ex = new ApiException("云邮局退订接口返回值转换为JSON时发生异常，返回值："+out);				
			ex.initCause(je);
			throw ex;
		}
	}
	

//	public SubscribeResult setNotify(){
//		return SubscribeResult.OK;
//	}
//	public SubscribeResult queryUserSetting(){
//		return SubscribeResult.OK;
//	}
}
