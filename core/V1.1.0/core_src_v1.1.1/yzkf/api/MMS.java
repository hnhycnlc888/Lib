package yzkf.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.richinfo.core.utils.MD5;
import cn.richinfo.core.utils.web.HttpClient;
import sun.misc.BASE64Encoder;

import yzkf.api.result.MMSResult;
import yzkf.app.Pattern;
import yzkf.config.ApiConfig;
import yzkf.config.ConfigFactory;
import yzkf.exception.ApiException;
import yzkf.exception.ParserConfigException;
import yzkf.utils.Guid;
import yzkf.utils.Utility;
import yzkf.utils.Xml;

/**
 * 彩信接口处理类
 * 
 * @author qiulw
 * @version V4.0.0 2012.02.06
 * @需要改进 
 * 1.文件名由客户端保证其唯一性，服务器端同一
 * 日期同一客户端ID所发的同名文件只保存一个。
 * 当多项目间出现同名的文件时，无法保证唯一性。<br/>
 * 2.不能使用网络文件。<br/>
 * 3.分布式部署时，如果该程序环境和调用个环境不在同一机器，文件路径将找不到，许使用网络文件地址。<br/>
 * 4.彩信内容加入文本，不能直接传入字符串，需要传入txt文件。<br/>
 * 5.没有加入文件过虑：仅限使用 gif, jpg, jpeg, txt, smil, vcf, wbmp, mid, amr, wav, mmf, imy
 */
public class MMS {
	private static final Logger LOG = LoggerFactory.getLogger(MMS.class.getName());
	private ApiConfig config;
	public MMS() throws ParserConfigException{
		ConfigFactory factory = ConfigFactory.getInstance();
		this.config = factory.newApiConfig();
	}
	public MMS(ApiConfig config){
		this.config = config;
	}
	/**
	 * 彩信文件最大大小
	 */
	private final static long FileSizeLimit = 100 * 1024; 
	/**
	 * 请求端名称，标识客户端ID,缺省为16运营自写彩信
	 */
	private String clientID = "16";
	/**
	 * 发送方手机号，不带86
	 */
	private String sendMobile;
	/**
	 * 网关id，默认广东网关
	 * <br/>yzkf.enums.SpsId属性
	 */
	private String spsId = yzkf.enums.SpsId.GD.getValue();
	/**
	 * 开始发送时间
	 * <br/>格式为：YYYYMMDDHHmmss 如：20080319010101
	 */
	private String startSendTime;
	/**
	 * 彩信标题，最长256字符
	 */
	private String subject;
	/**
	 * 是否需要状态报告，默认不需要
	 */
	private String statusReport = "0";
	/**
	 * 计费标识(1计费，0免费)
	 */
	private String feeflag = "0";
	/**
	 * 接收方号码，最多100个
	 */
	private Vector<String> receiveMobiles = new Vector<String>();
	/**
	 * 彩信内容中的文件
	 */
	private Vector<String> files = new Vector<String>();	

	/**
	 * [可选]设置请求端名称
	 * @param clientID 标识客户端ID,缺省为16运营自写彩信
	 */
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}
	/**
	 * [必须]设置发送方号码，不带86
	 * 
	 * @param sendMobile 手机号码，不带86
	 */
	public void setSendMobile(String sendMobile) {
		this.sendMobile = sendMobile;
	}
	/**
	 * [可选]设置网关ID , 默认广东网关
	 * @param spsId yzkf.enums.SpsId
	 */
	public void setSpsId(yzkf.enums.SpsId spsId) {
		this.spsId = spsId.getValue();
	}
	/**
	 * [可选]设置网关ID , 默认广东网关
	 * @param spsId yzkf.enums.SpsId.getValue();
	 */
	public void setSpsId(String spsId) {
		this.spsId = spsId;
	}
	/**
	 * [可选]设置发送时间，默认即时发送
	 * @param startSendTime
	 */
	public void setStartSendTime(Date startSendTime) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(startSendTime == null){
			this.startSendTime = df.format(new Date());;
		}else{
			
			this.startSendTime = df.format(startSendTime);
		}
	}
	/**
	 * [必须]设置彩信标题
	 * @param subject 最长256字符
	 */
	public void setSubject(String subject) {
		if(StringUtils.isEmpty(subject))
			throw new NullPointerException("参数subject为null");
//		else if(subject.length() > 256)
//			throw new ApiException("参数长度太长");
		this.subject = subject;
	}
	/**
	 * [可选]设置是否需要状态报告，默认不需要
	 * @param statusReport 默认false
	 */	
	public void setStatusReport(boolean statusReport) {
		this.statusReport = statusReport ? "1" : "0";
	}
	/**
	 * [可选]设置是否计费，默认免费
	 * @param free 默认false
	 */
	public void setFree(boolean free) {
		this.feeflag = free ? "1" : "0";
	}
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
	 * 获取以逗号分隔的收件方号码
	 * @return
	 */
	private String getReceives(){
		Iterator<String> iter = this.receiveMobiles.iterator();
		StringBuilder sb = new StringBuilder();
		while(iter.hasNext()){
			sb.append(iter.next());
			sb.append(',');
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	 /**
	 * 添加接收方号码
	 * @param mobile 手机号码，不带86
	 * @return true 添加成功；false 添加失败或已存在或已超过限制
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
	 * 获取接收方号码个数
	 */
	private int getReceiveNumber(){
		return this.receiveMobiles.size();
	}
	
	/**
	 * 添加彩信文件
	 * @param filePath 文件路径
	 * @return true 添加成功；false 添加失败或已存在
	 */
	public boolean addFile(String filePath){		
		if(this.files.indexOf(filePath) < 0){
			return this.files.add(filePath);
		}
		return false;			
	}
	/**
	 * 删除彩信文件
	 * @param filePath 文件路径
	 * @return true 删除成功；false 删除失败或不存在
	 */
	public boolean removeFile(String filePath){
		if(this.files.indexOf(filePath) > 0){
			return this.receiveMobiles.remove(filePath);
		}
		return false;	
	}
	/**
	 * 获取文件转换为传输xml格式后的字符串
	 * @return 所有文件经过Base64转换成字符串组合成xml后的数据
	 * @throws ApiException 文件操作出现异常
	 */
	private String getFiles() throws ApiException{
		StringBuilder dataInfo = new StringBuilder();
		  //文件转换
	    BASE64Encoder encoder = new BASE64Encoder(); 
	    Iterator<String> iter = this.files.iterator();
	    File file = null;
	    FileInputStream fis = null;
	    byte[] buffer = null;
	    long fileSize;
	    long totalSize = 0;
	    while(iter.hasNext()){
	    	try{
	    		file= new File(iter.next());
				fis = new FileInputStream(file);
				fileSize = file.length();
				totalSize += fileSize;
				if(totalSize > FileSizeLimit){
					throw new ApiException("彩信体过大(当前彩信不能超过100K)");
				}
				buffer = new byte[(int)file.length()];		
				fis.read(buffer);
				fis.close();
				dataInfo.append("<DataInfo>");
				dataInfo.append("<FileName>"+file.getName()+"</FileName>");
				dataInfo.append("<Content>"+encoder.encode(buffer)+"</Content>");
				dataInfo.append("</DataInfo>");
			} catch (IOException e) {
				ApiException ex = new ApiException("读取彩信文件时发生IO异常");				
				ex.initCause(e);
				throw ex;
			}finally{	    		
				try {
					if(fis != null)
						fis.close();
				} catch (IOException e) {
					LOG.warn("读取彩信关闭文件异常", e);
				}
	    	}
	    }
	    return dataInfo.toString();
	}
	public MMSResult send() throws ApiException, ParserConfigException{
		//发送方的手机号码不能为空
        if (!Pattern.isMobile86(this.sendMobile))
            return MMSResult.InvalidSender;
        //发送的内容不能为空
        if(StringUtils.isEmpty(this.subject))
        	throw new NullPointerException("下发彩信标题为空");
        //短信内容长度超过限制
        if (!Pattern.isMmsSubject(this.subject))
        	return MMSResult.InvalidMmsSubject;
        //接收方的手机号码不能为空
        if (this.getReceiveNumber() <= 0)
        	throw new NullPointerException("接收方手机号码不能为空");
        if (this.getReceiveNumber() > 100)
        	throw new ApiException("接收方手机号码大于100个");
        if(this.files.size() <= 0 )
        	throw new ApiException("彩信文件为空");
        String reqSN = new Guid().newGuid();
        String strMKey =MD5.encode(reqSN+this.config.MMS_Send_MD5Key+this.sendMobile);        
	    String postUrl = this.config.MMS_Send_BaseUrl + "?mkey=" + strMKey; 
	    
        String postData = "<?xml version=’1.0’ encoding=’utf-8’?>"
		+"<Request>"
			+"< Head >"
				+"<ReqSN>"+reqSN+"</ReqSN>"
				+"<SendTime>"+(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date())+"</SendTime>"
				+"<ExpiredTime>"+Utility.getTimeSpan(this.config.MMS_Send_TimeExpires)+"</ExpiredTime>"
				+"<ClientID>"+this.clientID+"</ClientID>"
			+"< /Head >"
			+"<Body>"
				+"<UserNumber>"+this.sendMobile+"</UserNumber>"
				+"<Subject>"+this.subject+"</Subject>"
				+"<StartSendDate>"+this.startSendTime+"</StartSendDate>"
				+"<SPID>"+this.spsId+"</ SPID>"
				+"<StatusReport>"+this.statusReport+"</StatusReport>"
				+"<Feeflag>"+this.feeflag+"</Feeflag>"
				+"<Receivers>"+this.getReceives()+"</Receivers>"
				+this.getFiles()			
			+"</Body>"
		+"</Request>";
        
        /**
         * 发起请求
         */
        String out = null;
		try {
			out = HttpClient.post(postUrl, postData,this.config.MMS_Send_Encoding);
		} catch (IOException e) {
			ApiException ex = new ApiException("发送彩信接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		Xml outXml = null;
		try {
			outXml = Xml.parseXml(out);
		}catch(Exception e){
			ApiException ex = new ApiException("发送彩信接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}
		String outRetCode = "";
		try {
			outRetCode = outXml.evaluate("/Response/Head/ResultCode");
			String outResSN = outXml.evaluate("/Response/Head/ResSN ");
			if(!outResSN.equals(reqSN))
				throw new ApiException("发送彩信接口异常，发送reqSN和返回ResSN不相符");
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("发送彩信接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}
		if(outRetCode.equals("000"))
			return MMSResult.OK;
		if(outRetCode.equals("1001"))
			throw new ApiException("[1001]包过期，请求时间戳超时");
		if(outRetCode.equals("1002"))
			throw new ApiException("[1002]数据签名错误");
		if(outRetCode.equals("1003"))
			throw new ApiException("[1003]SPID--省份出错");
		if(outRetCode.equals("1004"))
			throw new ApiException("[1004]接收方号码为空");
		if(outRetCode.equals("1005"))
			throw new ApiException("[1005]彩信体过大(当前彩信不能超过100K)");
		if(outRetCode.equals("1006"))
			throw new ApiException("[1006]彩信体为空");
		if(outRetCode.equals("1007"))
			throw new ApiException("[1007]处理请求失败");
		if(outRetCode.equals("1008"))
			throw new ApiException("[1008]发送方和接收方不在同一省内");
		if(outRetCode.equals("1009"))
			throw new ApiException("[1009]报文格式错误");
		if(outRetCode.equals("1010"))
			throw new ApiException("[1010]客户端ID无效");
		if(outRetCode.equals("1011"))
			throw new ApiException("[1011]接收用户大于100个");
		if(outRetCode.equals("1012"))
			throw new ApiException("[1012]用于发送彩信的文件类型不正确");
		if(outRetCode.equals("1013"))
			throw new ApiException("[1013]彩信接口必选参数为空");
		if(outRetCode.equals("1014"))
			throw new ApiException("[1014]参数类型错误");
		if(outRetCode.equals("1015"))
			return MMSResult.InvalidMmsSubject;
		if(outRetCode.equals("9999"))
			throw new ApiException("[9999]彩信平台内部错误");
        return MMSResult.Unknow;
	}
}
