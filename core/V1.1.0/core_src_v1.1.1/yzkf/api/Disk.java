package yzkf.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;

import cn.richinfo.core.utils.web.HttpClient;
import yzkf.api.result.DiskResult;
import yzkf.app.Pattern;
import yzkf.config.ApiConfig;
import yzkf.config.ConfigFactory;
import yzkf.exception.ApiException;
import yzkf.exception.ParserConfigException;
import yzkf.model.DiskInfo;
import yzkf.model.UserInfo;
import yzkf.utils.Xml;

/**
 * 139邮箱网盘接口类
 * @author qiulw
 * @version V4.0.0
 */
public class Disk {
	private ApiConfig config;
	public Disk() throws ParserConfigException{
		ConfigFactory factory = ConfigFactory.getInstance();
		this.config = factory.newApiConfig();
	}
	public Disk(ApiConfig config){
		this.config = config;
	}
	private final static String LOGIN_DATA = "<?xml version=\"1.0\" encoding=\"{encoding}\"?><request><cmd>{cmd}</cmd><clientType>{clientType}</clientType><userName>{userName}</userName><password>{password}</password><channel>{channel}</channel></request>";
	
	public UserInfo userInfo;
	public String outMessage;
	/***
	 * 获取接口返回的用户信息
	 * @return
	 */
	public UserInfo getUserInfo() {
		return userInfo;
	}
	/**
	 * 网盘初始化接口
	 * @param account 用户手机号码
	 * @return
	 * @throws ApiException
	 */
	public DiskResult init(String account) throws ApiException{
		if(StringUtils.isEmpty(account))
			return DiskResult.EmptyAccount;	//空账户
		
		String url = config.Disk_Init_BaseUrl.replace("{account}", account);
		
		String out = null;
		try {
			URL client = new URL(url);			
			HttpURLConnection con = (HttpURLConnection) client.openConnection();		
			con.setConnectTimeout(30000);
			con.setReadTimeout(30000);
			con.setRequestProperty("Accept-Charset", config.Disk_Init_Encoding);
			con.setRequestProperty("Content-Type", "text/xml");		
			con.setUseCaches(false);			
			con.setDoOutput(true);
			//con.setRequestMethod("POST");
			con.setInstanceFollowRedirects(true);
			
			con.connect();
			//读取返回内容		
			InputStream input = con.getInputStream();
			out = HttpClient.readString(input,config.Disk_Init_Encoding);
				
		} catch (IOException e) {
			ApiException ex = new ApiException("彩云网盘Init接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		String outRetCode = "";
		String outRetMsg = "";
		JSONObject json = JSONObject.parseObject(out);
		outRetCode = json.getString("code");
		outRetMsg = json.getString("summary");
		if(outRetCode.equalsIgnoreCase("S_OK")){
			JSONObject jsonInfo = json.getJSONObject("var");
			DiskInfo diskInfo = new DiskInfo();
			diskInfo.setMcloud(jsonInfo.getIntValue("isMcloud") != 0 );
			diskInfo.setShareSiChuan(jsonInfo.getIntValue("isShareSiChuan") != 0);
			JSONObject baseInfo = jsonInfo.getJSONObject("baseInfo");
			diskInfo.setTotalSize(baseInfo.getIntValue("totalSize"));
			diskInfo.setUseSize(baseInfo.getIntValue("useSize"));
			diskInfo.setFreeSize(baseInfo.getIntValue("freeSize"));
			diskInfo.setFileNum(baseInfo.getIntValue("fileNum"));
			diskInfo.setFileMaxSize(baseInfo.getIntValue("fileMaxSize"));
			diskInfo.setShareNum(baseInfo.getIntValue("shareNum"));
			diskInfo.setRootId(baseInfo.getString("rootId"));
			diskInfo.setMailId(baseInfo.getString("139MailId"));
			diskInfo.setRootDirType(baseInfo.getIntValue("rootDirType"));

			return DiskResult.OK.setValue(diskInfo);
		}else{
			return DiskResult.Other.setValue(outRetMsg);
		}		
	}
	private static final String UPLOAD_DATA = "<object> <array name=\"netFiles\"><object><string name=\"url\">{0}</string><string name=\"directoryId\">{1}</string><int name=\"dirType\">{2}</int><string name=\"fileName\">{3}</string><int name=\"fileSize\">{4}</int></object></array></object>";
	/**
	 * 彩云网盘上传接口
	 * @param account 手机号码
	 * @param fileUrl 要上传文件的URL路径
	 * @param directoryId 用户根目录Id：网盘init接口返回的rootId
	 * @param dirType 目标文件夹类型: 1为普通类型 3 相册类型 4音乐类型
	 * @param fileName 文件名
	 * @param fileSize 文件大小，字节
	 * @return
	 * @throws ApiException
	 */
	public DiskResult upload(String account,String fileUrl,String directoryId,int dirType,String fileName,String fileSize) throws ApiException{
		if(StringUtils.isEmpty(account))
			return DiskResult.EmptyAccount;	//空账户
		
		String url = config.Disk_Upload_BaseUrl.replace("{account}", account);
		String strRequestData = MessageFormat.format(UPLOAD_DATA,fileUrl,directoryId,dirType,fileName,fileSize);
		
		String out = null;
		try {
			URL client = new URL(url);			
			HttpURLConnection con = (HttpURLConnection) client.openConnection();		
			con.setConnectTimeout(30000);
			con.setReadTimeout(30000);
			con.setRequestProperty("Accept-Charset", config.Disk_Init_Encoding);
			con.setRequestProperty("Content-Type", "text/xml");		
			con.setUseCaches(false);			
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setInstanceFollowRedirects(true);
			
			con.setRequestProperty("Content-Length", String.valueOf(strRequestData.getBytes().length));
			OutputStreamWriter outStream = new OutputStreamWriter(con.getOutputStream());
			outStream.write(strRequestData);
			outStream.flush();
			
			outStream.close();
			con.connect();
			//读取返回内容		
			InputStream input = con.getInputStream();
			out = HttpClient.readString(input,config.Disk_Init_Encoding);
				
		} catch (IOException e) {
			ApiException ex = new ApiException("彩云网盘Init接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		String outRetCode = "";
		String outRetMsg = "";
		JSONObject json = JSONObject.parseObject(out);
		outRetCode = json.getString("code");
		outRetMsg = json.getString("summary");
		if(outRetCode.equalsIgnoreCase("S_OK")){
			return DiskResult.OK;
		}else{
			return DiskResult.Other.setValue(outRetMsg);
		}		
	}
	/**
	 * 虚拟网盘登录校验
	 * @param account 用户账号
	 * @param password 用户密码
	 * @param clientIP 用户IP地址
	 * @return 接口执行结果枚举 {@link DiskResult}
	 * @throws ApiException
	 * @throws ParserConfigException
	 */
	public DiskResult validateLogin(String account,String password,String clientIP) throws ApiException, ParserConfigException{
		if(StringUtils.isEmpty(account))
			return DiskResult.EmptyAccount;	//空账户
		if(StringUtils.isEmpty(password))
			return DiskResult.EmptyPassword;	//空密码
		
		if(!Pattern.isMobile139alias(account))
			return DiskResult.InvalidAccount;	//无效账号
		if(!Pattern.isMailpassword(password))
			return DiskResult.InvalidPassword;	//无效错误	
		
		String strRequestData = LOGIN_DATA.replace("{encoding}", config.Disk_Login_Encoding)
                .replace("{cmd}", config.Disk_Login_Cmd)
                .replace("{clientType}", config.Disk_Login_ClienType)
                .replace("{userName}", account)
                .replace("{password}", password)
                .replace("{channel}", config.Disk_Login_Channel);
		

		String out = null;
		String rmkey = "";
		try {
			URL client = new URL(config.Disk_Login_BaseUrl);
			HttpURLConnection con = (HttpURLConnection) client.openConnection();		
			con.setConnectTimeout(30000);
			con.setReadTimeout(30000);
			con.setRequestProperty("Accept-Charset", config.Disk_Login_Encoding);
			con.setRequestProperty("Content-Type", "text/xml");
			con.setRequestProperty("Richinfo-Client-IP", clientIP);			
			con.setUseCaches(false);			
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setInstanceFollowRedirects(true);		
							
			con.setRequestProperty("Content-Length", String.valueOf(strRequestData.getBytes().length));
			OutputStreamWriter outStream = new OutputStreamWriter(con.getOutputStream());
			outStream.write(strRequestData);
			outStream.flush();
			outStream.close();
			
			con.connect();
			//读取返回内容		
			InputStream input = con.getInputStream();
			out = HttpClient.readString(input,config.Disk_Login_Encoding);
			//读取cookie
			Map<String, List<String>> _outHeaders = con.getHeaderFields();
			for(String _key : _outHeaders.keySet()){
				if(_key == null || !_key.equalsIgnoreCase("Set-Cookie")) continue;
				String cookieStr = _outHeaders.get("Set-Cookie").toString();
				rmkey = Pattern.getFirstMatch("RMKEY=([0-9a-z]+?);", cookieStr);
				break;
			}			
			
			//Utility.getCookieValue(request, name) con.getHeaderField("Set-Cookie")
			//out = HttpClient.send(config.Disk_Login_BaseUrl, strRequestData, config.Disk_Login_Encoding, true, requestHeaders);
		} catch (IOException e) {
			ApiException ex = new ApiException("Disk登录验证时发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		Xml outXml = null;
		try {
			outXml = Xml.parseXml(out);
		}catch(Exception e){
			ApiException ex = new ApiException("网盘登录接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}
		String outRetCode = "";
		try {
			outRetCode = outXml.evaluate("/return/result");
			outMessage = outXml.evaluate("/return/description");
			String outSid = outXml.evaluate("/return/ssoid");			
			String outMobile = outXml.evaluate("/return/userNumber");	

	        /// [000]获取用户属性成功
			if(outRetCode.equals("0")){		
				this.userInfo = new UserInfo();
				if(Pattern.isMobile(account))
					userInfo.setMobile(account);
				if(Pattern.isMail139alias(account))
					userInfo.setAlias(account);
				if(!StringUtils.isEmpty(outMobile)){
					if(outMobile.startsWith("86"))
						outMobile = outMobile.substring(2);
					userInfo.setMobile(outMobile);
				}
				userInfo.setSid(outSid);
				userInfo.setRemkey(rmkey);
				return DiskResult.OK.setValue(userInfo);
			}
			return DiskResult.Other.setValue(outMessage); 
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("获取网盘登录接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}        
	}
}
