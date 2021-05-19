package cn.richinfo.login.pojo;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 用户信息类
 * @author yanghua
 * @version V1.0.0 
 */
public class UserInfo implements Serializable {
	private static final long serialVersionUID = 7085765361482507009L;
	//登录ID
	private long loginid;
	private String loginProject;
	private int passPortId;
	private String sid;
	private long timestamp;
	private String password;
	private int provCode;
	private String provDesc;
	private int areaCode;
	private String areaDesc;	
	private String cardType;
	private String uin;
	private String userNumber;
	private String rmkey;
	private String maildomain;
	private String serviceItem;
	private String serviceIds;
	private String serviceItems;
	private String defaultSender;
	private String lastlogintime;
	private String lastloginip;
	private String userLevel;
	private String levelimg;
	private String effectIntergal;
	private String userType;
	private String userAttrType;
	private String trueName;
	private String aliase;
	private long userId;
	private String issso;
	
	/**
	 * 获取用户ID
	 * @return
	 */
	public long getUserId() {
		return userId;
	}
	/**
	 * 设置用户ID
	 * @param userId
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}
	/**
	 * 当前登录回话标识
	 * @return
	 */
	public long getLoginid() {
		return loginid;
	}
	/**
	 * @param loginid 当前登录会话标识
	 */
	public void setLoginid(long loginid) {
		this.loginid = loginid;
	}
	/**
	 * 本次登录信息来源的项目编号
	 * @return
	 */
	public String getLoginProject() {
		return loginProject;
	}
	/**
	 * 设置本次登录信息来源的项目编号
	 * @param loginProject 项目编号
	 */
	public void setLoginProject(String loginProject) {
		this.loginProject = loginProject;
	}
	/**
	 * 获取通行证号码
	 * @return
	 */
	public int getPassPortId() {
		return passPortId;
	}
	/**
	 * 设置通行证号码
	 * @param passPortId
	 */
	public void setPassPortId(int passPortId) {
		this.passPortId = passPortId;
	}
	/**
	 * 获取用户sid
	 * @return
	 */

	@JSONField(name="sid")
	public String getSid() {
		return sid;
	}
	/**
	 * 设置用户sid
	 * @param sid
	 */
	public void setSid(String sid) {
		this.sid = sid;
	}
	/**
	 * 获取登录时的时间戳
	 * @return
	 */
	public long getTimestamp() {
		return timestamp;
	}
	/**
	 * 设置登录时的时间戳
	 * @param timestamp
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * 获取用户省份编号
	 * @return
	 */
	public int getProvCode() {
		return provCode;
	}
	/**
	 * 设置用户省份编号
	 * @param provCode
	 */
	@JSONField(name="prov")
	public void setProvCode(int provCode) {
		this.provCode = provCode;
	}
	/**
	 * 获取用户省份描述
	 * @return
	 */
	public String getProvDesc() {
		return provDesc;
	}
	/**
	 * 设置用户省份描述
	 * @param provDesc
	 */
	public void setProvDesc(String provDesc) {
		this.provDesc = provDesc;
	}
	/**
	 * 获取用户地市编号
	 * @return
	 */
	public int getAreaCode() {
		return areaCode;
	}
	/**
	 * 设置用户地市编号
	 * @param areaCode
	 */
	@JSONField(name="areacode")
	public void setAreaCode(int areaCode) {
		this.areaCode = areaCode;
	}
	/**
	 * 获取用户地市描述
	 * @return
	 */
	public String getAreaDesc() {
		return areaDesc;
	}
	/**
	 * 设置用户地市描述
	 * @param areaDesc
	 */
	public void setAreaDesc(String areaDesc) {
		this.areaDesc = areaDesc;
	}
	/**
	 * 获取用户手机品牌：0-未知1-全球通2-动感地带3-神州行4-大众卡 5-神州行旅通卡 6-神州行畅听卡
	 * @return
	 */
	public String getCardType() {
		return cardType;
	}
	/**
	 * 设置用户手机品牌：0-未知1-全球通2-动感地带3-神州行4-大众卡 5-神州行旅通卡 6-神州行畅听卡
	 * @param cardType
	 */
	@JSONField(name="cardType")
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	/**
	 * 获取用户唯一标识
	 * @return
	 */
	public String getUin() {
		return uin;
	}
	/**
	 * 设置用户唯一标识
	 * @param uin
	 */
	@JSONField(name="uin")
	public void setUin(String uin) {
		this.uin = uin;
	}
	/**
	 * 获取用户手机号码
	 * @return
	 */
	public String getUserNumber() {
		return userNumber;
	}
	/**
	 * 设置用户手机号码
	 * @param userNumber
	 */
	@JSONField(name="userNumber")
	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}
	/**
	 * 用于标识客户端的身份值，与sid属于多对一关系
	 * @return
	 */
	public String getRmkey() {
		return rmkey;
	}
	/**
	 * 设置用户rmkey
	 * @param rmkey
	 */
	@JSONField(name="rmkey")
	public void setRmkey(String rmkey) {
		this.rmkey = rmkey;
	}
	/**
	 * 获取邮箱的域名
	 * @return
	 */
	public String getMaildomain() {
		return maildomain;
	}
	/**
	 * 设置邮箱的域名
	 * @param maildomain
	 */
	@JSONField(name="maildomain")
	public void setMaildomain(String maildomain) {
		this.maildomain = maildomain;
	}
	/**
	 * 获取用户套餐类型
	 * @return
	 */
	public String getServiceItem() {
		return serviceItem;
	}
	/**
	 * 设置用户套餐类型
	 * @param serviceItem
	 */
	@JSONField(name="serviceItem")
	public void setServiceItem(String serviceItem) {
		this.serviceItem = serviceItem;
	}
	/**
	 * 获取该用户年拥有的所有定购产品ID
	 * @return
	 */
	public String getServiceIds() {
		return serviceIds;
	}
	/**
	 * 设置该用户年拥有的所有订购产品ID
	 * @param serviceIds
	 */
	@JSONField(name="serviceIds")
	public void setServiceIds(String serviceIds) {
		this.serviceIds = serviceIds;
	}
	/**
	 * 该用户拥有的所有定购关系，以逗号间隔
	 * @return
	 */
	public String getServiceItems() {
		return serviceItems;
	}
	/**
	 * 设置该用户拥有的所有定购关系，以逗号间隔
	 * @param serviceItems
	 */
	@JSONField(name="serviceitems")
	public void setServiceItems(String serviceItems) {
		this.serviceItems = serviceItems;
	}
	/**
	 * 获取默认发件人账号
	 * @return
	 */
	public String getDefaultSender() {
		return defaultSender;
	}
	/**
	 * 设置默认发件人账号
	 * @param defaultSender
	 */
	@JSONField(name="defaultSender")
	public void setDefaultSender(String defaultSender) {
		this.defaultSender = defaultSender;
	}
	/**
	 * 获取上次登录时间
	 * @return
	 */
	public String getLastlogintime() {
		return lastlogintime;
	}
	/**
	 * 设置上次登录时间
	 * @param lastlogintime
	 */
	@JSONField(name="lastlogintime")
	public void setLastlogintime(String lastlogintime) {
		this.lastlogintime = lastlogintime;
	}
	/**
	 * 获取上次登录的IP
	 * @return
	 */
	public String getLastloginip() {
		return lastloginip;
	}
	/**
	 * 设置上次登录的IP
	 * @param lastloginip
	 */
	@JSONField(name="lastloginip")
	public void setLastloginip(String lastloginip) {
		this.lastloginip = lastloginip;
	}
	/**
	 * 获取用户等级 
	 * @return
	 */
	public String getUserLevel() {
		return userLevel;
	}
	/**
	 * 设置用户等级
	 * @param userLevel
	 */
	@JSONField(name="userLevel")
	public void setUserLevel(String userLevel) {
		this.userLevel = userLevel;
	}
	/**
	 * 获取用户等级名称
	 * @return
	 */
	public String getLevelimg() {
		return levelimg;
	}
	/**
	 * 设置用户等级名称
	 * @param levelimg
	 */
	@JSONField(name="levelimg")
	public void setLevelimg(String levelimg) {
		this.levelimg = levelimg;
	}
	/**
	 * 获取用户积分
	 * @return
	 */
	public String getEffectIntergal() {
		return effectIntergal;
	}
	/**
	 * 设置用户积分
	 * @param effectIntergal
	 */
	@JSONField(name="effectIntergal")
	public void setEffectIntergal(String effectIntergal) {
		this.effectIntergal = effectIntergal;
	}
	/**
	 * 获取用户类型：1、一般用户；2、红名单用户；3、黑名单用户；9、企业邮箱管理员；
	 * @return
	 */
	public String getUserType() {
		return userType;
	}
	/**
	 * 设置用户类型：1、一般用户；2、红名单用户；3、黑名单用户；9、企业邮箱管理员；
	 * @param userType
	 */
	@JSONField(name="userType")
	public void setUserType(String userType) {
		this.userType = userType;
	}
	/**
	 * 获取用户属性类型：1、移动用户；2、联通用户；3、电信用户；4、互联网用户；
	 * @return
	 */
	public String getUserAttrType() {
		return userAttrType;
	}
	/**
	 * 设置用户属性类型：1、移动用户；2、联通用户；3、电信用户；4、互联网用户；
	 * @param userAttrType
	 */
	@JSONField(name="userAttrType")
	public void setUserAttrType(String userAttrType) {
		this.userAttrType = userAttrType;
	}
	/**
	 * 获取用户真实姓名
	 * @return
	 */
	public String getTrueName() {
		return trueName;
	}
	/**
	 * 设置用户真是姓名
	 * @param trueName
	 */
	@JSONField(name="trueName")
	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}
	/**
	 * 获取用户别名
	 * @return
	 */
	public String getAliase() {
		return aliase;
	}
	/**
	 * 设置用户别名
	 * @param aliases
	 */
	public void setAliase(String aliase) {
		this.aliase = aliase;
	}
	/**
	 * 获取用户是否单点登录,0为非单点登录，1为邮箱单点登录，其他为活动单点登录
	 * @return
	 */
	public String getIssso() {
		return issso;
	}
	/**
	 * 设置用户是否单点登录
	 * @param issso
	 */
	public void setIssso(String issso) {
		this.issso = issso;
	}
	
}
