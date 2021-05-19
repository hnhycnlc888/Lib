package yzkf.config;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yzkf.exception.ParserConfigException;

public class ProjectConfig extends Configuration {
	private static final Logger LOG = LoggerFactory.getLogger(ProjectConfig.class.getName());
	private final static String PATH_DEFAULT = "/project.xml";
	private static ProjectConfig instance;
	private String name;
	private String code;
	private Date start = null;
	private Date end = null;
	private String provAllow;
	private String provForbid;
	private String areaAllow;
	private String areaForbid;
	private String notCMCCProv;
	private String loginProcedure;
	private int loginMinFailedTimes;
	private int loginMaxFailedTimes;
	private String loginUsertag;
	private int loginSessionTimeout;
	private String smsVerifySpnumber;
	private int smsVerifyExpire;
	private String smsVerifyContent;
	
	public static ProjectConfig getInstance() {
		if(instance == null)
			instance = new ProjectConfig();
		return instance;
	}
	private ProjectConfig(){
		ClassLoader standardClassloader = Thread.currentThread().getContextClassLoader();
        URL url = null;
        if (standardClassloader != null) {
            url = standardClassloader.getResource(PATH_DEFAULT);
        }
        if (url == null) {
            url = ProjectConfig.class.getResource(PATH_DEFAULT);
        }
        if (url != null) {
            LOG.debug("项目配置文件project.xml配置文件路径: " + url);
            super.setXml(url);
            initProps();
        }
	}
	/**
	 * 初始化属性
	 * @throws ParserConfigException
	 */
	private void initProps() throws ParserConfigException{
		code = getXPathValue("project/code");
		name = getXPathValue("project/name");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(!StringUtils.isEmpty(getXPathValue("project/start"))){
			try {
				start = formatter.parse(getXPathValue("project/start"));
			} catch (ParseException e) {
				LOG.debug("项目配置 开始时间 配置错误: " + getXPathValue("project/start"));
			}
		}
		if(!StringUtils.isEmpty(getXPathValue("project/end"))){
			try {
				end = formatter.parse(getXPathValue("project/end"));
			} catch (ParseException e) {
				LOG.debug("项目配置 开始时间 配置错误: " + getXPathValue("project/end"));
			}
		}
		provAllow = getXPathValue("project/prov[@type=1]");
		provForbid = getXPathValue("project/prov[@type=0]");
		areaAllow = getXPathValue("project/area[@type=1]");
		areaForbid = getXPathValue("project/area[@type=0]");
		notCMCCProv = getXPathValue("project/notcmccprov");
		loginProcedure = getXPathValue("project/login/procedure");
		loginMinFailedTimes = Integer.valueOf(getXPathValue("project/login/minfailedtimes"));
		loginMaxFailedTimes = Integer.valueOf(getXPathValue("project/login/maxfailedtimes"));
		loginSessionTimeout = Integer.valueOf(getXPathValue("project/login/sessiontimeout"));
		loginUsertag = getXPathValue("project/login/usertag");		
		smsVerifySpnumber = getXPathValue("project/smsverify/spnumber");	
		smsVerifyExpire = Integer.valueOf(getXPathValue("project/smsverify/expire"));	
		smsVerifyContent = getXPathValue("project/smsverify/content");	
	}
	/**
	 * 获取项目编号
	 * @return
	 */
	public String getCode(){
		return this.code;
	}
	/**
	 * 获取项目名称
	 * @return
	 */
	public String getName(){
		return this.name;
	}
	/**
	 * 活动开始时间，未配置返回null
	 * @return
	 */
	public Date getStart(){
		return this.start;
	}
	/**
	 * 活动结束时间，未配置返回null
	 * @return
	 */
	public Date getEnd(){
		return this.end;
	}
	
	/**
	 * @return 记录登录信息的存储过程
	 */
	public String getLoginProcedure() {
		return loginProcedure;
	}
	/**
	 * @return 登录失败次数，每天当失败次数大于该次数时，需要输入图形验证码
	 */
	public int getLoginMinFailedTimes() {
		return loginMinFailedTimes;
	}
	/**
	 * @return 一天最多登录失败次数，当失败次数大于该次数时，当天禁止登录
	 */
	public int getLoginMaxFailedTimes() {
		return loginMaxFailedTimes;
	}
	/**
	 * @return 会话超时时间（单位：分钟）
	 */
	public int getLoginSessionTimeout() {
		return loginSessionTimeout;
	}
	/**
	 * 
	 * @return 登录行为上报的用户标签
	 */
	public String getLoginUsertag(){
		return loginUsertag;
	}
	/**
	 * 
	 * @return 短信验证码下发端口号
	 */
	public String getSmsVerifySpnumber() {
		return smsVerifySpnumber;
	}
	/**
	 * 短信验证码有效时长，单位：分
	 * @return
	 */
	public int getSmsVerifyExpire() {
		return smsVerifyExpire;
	}
	/**
	 * 短信验证码文本模板
	 * @return
	 */
	public String getSmsVerifyContent() {
		return smsVerifyContent;
	}
	/**
	 * 活动是否在线
	 * <br>
	 * 在线需同时满足{@link #isStart isStart}=true，且{@link #isEnd isEnd}=false
	 * @return true 在线，false 不在线(未开始或已结束)
	 */
	public boolean isOnline(){
		if(isStart() == false || isEnd() == true)
			return false;
		return true;
	}
	/**
	 * 活动是否已开始
	 * @return true 已开始，false 未开始
	 */
	public boolean isStart(){
		Date now = Calendar.getInstance().getTime();
		if(start != null && start.after(now))
			return false;
		return true;
	}
	/**
	 * 活动是否已结束
	 * @return true 已结束，false 未结束
	 */
	public boolean isEnd(){
		Date now = Calendar.getInstance().getTime();
		if(end != null && end.before(now))
			return true;
		return false;
	}
	/**
	 * 所在地区省份是否允许参与活动
	 * <br>
	 * 允许参与必须同时满足{@link #checkArea checkArea}=true，且{@link #checkProv checkProv}=true
	 * @param provCode 省份编号
	 * @param areaCode 地区编号	
	 * @return true 允许，false 不允许
	 */
	public boolean checkProvArea(int provCode,int areaCode){
		if(checkProv(provCode) && checkArea(areaCode))
			return true;
		return false;
	}
	/**
	 * 所在省份是否允许参与活动
	 * @param provCode 省份编号
	 * @return true 允许，false 不允许
	 */
	public boolean checkProv(int provCode){
		if(!StringUtils.isEmpty(provAllow)){
			if(!(","+provAllow+",").contains(","+provCode+","))
				return false;
		}
		if(!StringUtils.isEmpty(provForbid)){
			if((","+provForbid+",").contains(","+provCode+","))
				return false;
		}
		return true;
	}
	/**
	 * 所在地区是否允许参与活动
	 * @param areaCode 地区编号
	 * @return true 允许，false 不允许
	 */
	public boolean checkArea(int areaCode){
		if(!StringUtils.isEmpty(areaAllow)){
			if(!(","+areaAllow+",").contains(","+areaCode+","))
				return false;
		}
		if(!StringUtils.isEmpty(areaForbid)){
			if((","+areaForbid+",").contains(","+areaCode+","))
				return false;
		}
		return true;
	}
	/**
	 * 是否中国移动号码，通过省份判断
	 * @param provcode 用户省份编号
	 * @return
	 */
	public boolean isChinaMobile(int provcode){
		if(!StringUtils.isEmpty(notCMCCProv)){
			if((","+notCMCCProv+",").contains(","+provcode+","))
				return false;
		}
		return true;
	}
	/**
	 * 获取自定义配置节点内容
	 * @param key 自定义配置节点名，是appsetting的子节点
	 * @return 自定义appsetting子节点的内容
	 * @example
	 * <pre>
String appsetting = yzkf.config.ProjectConfig.getInstance().getAppSetting("test");
System.out.println(appsetting);
	 * <pre>
	 */
	public String getAppSetting(String key){
		return getXPathValue("project/appsetting/"+key);	
	}
}
