package yzkf.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import yzkf.enums.NotifyType;
import yzkf.enums.UserType;
/**
 * 用户信息类
 * @author qiulw
 * @version V4.0.0 
 */
public class UserInfo implements Serializable {
	private static final long serialVersionUID = 7085765361482507009L;
	private long loginId;	
	private String loginProject;
	private String mobile;
	private String alias;
	private String sid;
	private long timestamp;
	private String password;
	private int provCode;
	private String provDesc;
	private int areaCode;
	private String areaDesc;	
	private String cardType;
	private UserType userType;
	private String phoneModel;
	private Date lastOpenTime;
	private String orderType;
	private String orderStatus;
	private String cancelDate;
	private String cancelType;
	private int presentSMS;
	private int presentMMS;
	private int presentNMS;
	private int presentFax;
	private String serviceCode;
	private String bindTypeId;
	private int notifyBeginHour;
	private int notifyEndHour;
	private NotifyType notifyType;	
	private MsInfo smsInfo;
	private MsInfo mmsInfo;	
	private String remkey;
	private int integralEffect;	//用户当前可以使用的积分
	private String userLevel;	//用户等级
	private int integralUse;	//用户已经消耗的积分
	private int integralActive;	//用户当前可以使用的活动积分
	private int unreadMailNum;//未读邮件数
	private int totalMailNum;//总邮件数

	/**
	 * @return 当前登录会话的标识
	 */
	public long getLoginId() {
		return loginId;
	}
	/**
	 * @param loginId 当前登录会话的标识
	 */
	public void setLoginId(long loginId) {
		this.loginId = loginId;
	}
	
	/**
	 * @return 本次登录信息来源的项目编号
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
	 * 获取用户账号，手机号或别名
	 * @return 当手机号不为空时，返回手机号，否则返回别名
	 */
	public String getAccount(){
		String account = getMobile();
		if(StringUtils.isEmpty(account))
			account = getAlias();
		return account;
	}
	/**
	 * 获取手机号码
	 * @return
	 */
	public String getMobile() {
		return mobile;
	}
	/**
	 * 设置手机号码
	 * @param mobile 手机号码，不带86
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	/**
	 * 获取邮箱别名
	 * @return
	 */
	public String getAlias() {
		return alias;
	}
	/**
	 * 设置邮箱别名
	 * @param alias
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}
	/**
	 * 获取用户登录邮箱获得的虚拟SID
	 * @return
	 */
	public String getSid() {
		return sid;
	}
	/**
	 * 设置用户登录邮箱获得的虚拟SID
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
	/**
	 * 获取用户密码
	 * @return
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * 设置用户密码
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * 获取用户号码省份编号
	 * @return
	 */
	public int getProvCode() {
		return provCode;
	}
	/**
	 * 设置用户号码省份编号
	 * @param provCode
	 */
	public void setProvCode(int provCode) {
		this.provCode = provCode;
	}
	/**
	 * 获取用户号码地区编号
	 * @return
	 */
	public int getAreaCode() {
		return areaCode;
	}
	/**
	 * 设置用户号码地区编号
	 * @param areaCode
	 */
	public void setAreaCode(int areaCode) {
		this.areaCode = areaCode;
	}
	/**
	 * 获取用户号码省份描述
	 * @return
	 */
	public String getProvDesc() {
		return provDesc;
	}
	/**
	 * 设置用户号码省份描述
	 * @param provDesc
	 */
	public void setProvDesc(String provDesc) {
		this.provDesc = provDesc;
	}
	/**
	 * 获取用户号码地区描述
	 * @return
	 */
	public String getAreaDesc() {
		return areaDesc;
	}
	/**
	 * 设置用户号码地区描述
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
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	/**
	 * 设置手机型号
	 * @return
	 */
	public String getPhoneModel() {
		return phoneModel;
	}
	/**
	 * 获取手机型号
	 * @param phoneModel
	 */
	public void setPhoneModel(String phoneModel) {
		this.phoneModel = phoneModel;
	}
	/**
	 * 获取用户类型
	 * @return yzkf.enums.UserType 用户类型：1 一般用户，2 红名单用户，3 黑名单用户
	 */
	public UserType getUserType() {
		return userType;
	}
	/**
	 * 设置用户类型
	 * @param userType yzkf.enums.UserType 用户类型：1 一般用户，2 红名单用户，3 黑名单用户
	 */
	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	/**
	 * 获取最后开通日期
	 * @return
	 */
	public Date getLastOpenTime() {
		return lastOpenTime;
	}
	/**
	 * 设置最后开通日期
	 * @param lastOpenTime
	 */
	public void setLastOpenTime(Date lastOpenTime) {
		this.lastOpenTime = lastOpenTime;
	}
	/**
	 * 获取开通方式
	 * @return 开通方式(1-Sms，2-Web，3-WAP，4-运营，5-客服，6-MISC，7-17288用户，8-接口，9-中央平台，17-boss，1000-中央平台)
	 */
	public String getOrderType() {
		return orderType;
	}
	/**
	 * 设置开通方式
	 * @param orderType 开通方式(1-Sms，2-Web，3-WAP，4-运营，5-客服，6-MISC，7-17288用户，8-接口，9-中央平台，17-boss，1000-中央平台)
	 */
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	/**
	 * 获取当前订购状态
	 * @return 当前定购状态(0-收费用户,1-已经退定（不能使用业务，可以注册）,2-待激活用户(可以使用业务，可以注册),3-冻结用户（不能使用业务，可以注册）,4-体验用户)
	 */
	public String getOrderStatus() {
		return orderStatus;
	}
	/**
	 * 设置当前订购状态
	 * @param orderStatus 当前定购状态(0-收费用户,1-已经退定（不能使用业务，可以注册）,2-待激活用户(可以使用业务，可以注册),3-冻结用户（不能使用业务，可以注册）,4-体验用户)
	 */
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	/**
	 * 获取注销日期
	 * @return
	 */
	public String getCancelDate() {
		return cancelDate;
	}
	/**
	 * 设置注销日期
	 * @param cancelDate
	 */
	public void setCancelDate(String cancelDate) {
		this.cancelDate = cancelDate;
	}
	/**
	 * 获取注销方式
	 * @return
	 */
	public String getCancelType() {
		return cancelType;
	}
	/**
	 * 设置注销方式
	 * @param cancelType
	 */
	public void setCancelType(String cancelType) {
		this.cancelType = cancelType;
	}
	/**
	 * 获取赠送SMS条数
	 * @return
	 */
	public int getPresentSMS() {
		return presentSMS;
	}
	/**
	 * 设置赠送SMS条数
	 * @param presentSMS
	 */
	public void setPresentSMS(int presentSMS) {
		this.presentSMS = presentSMS;
	}
	/**
	 * 获取赠送MMS条数
	 * @return
	 */
	public int getPresentMMS() {
		return presentMMS;
	}
	/**
	 * 设置赠送MMS条数
	 * @param presentMMS
	 */
	public void setPresentMMS(int presentMMS) {
		this.presentMMS = presentMMS;
	}
	/**
	 * 获取赠送邮箱到达通知条数
	 * @return
	 */
	public int getPresentNMS() {
		return presentNMS;
	}
	/**
	 * 设置赠送邮箱到达通知条数
	 * @param presentNMS
	 */
	public void setPresentNMS(int presentNMS) {
		this.presentNMS = presentNMS;
	}
	/**
	 * 获取赠送同城传真页数
	 * @return
	 */
	public int getPresentFax() {
		return presentFax;
	}
	/**
	 * 设置赠送同城传真页数
	 * @param presentFax
	 */
	public void setPresentFax(int presentFax) {
		this.presentFax = presentFax;
	}
	/**
	 * 获取邮箱类型
	 * @return 邮箱类型，如0010、0015
	 */
	public String getServiceCode() {
		return serviceCode;
	}
	/**
	 * 设置邮箱类型
	 * @param serviceCode 邮箱类型，如0010、0015
	 */
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	/**
	 * 获取用户订购业务绑定的活动号
	 * @return 
	 */
	public String getBindTypeId() {
		return bindTypeId;
	}
	/**
	 * 设置用户订购业务绑定的活动号
	 * @param bindTypeId
	 */
	public void setBindTypeId(String bindTypeId) {
		this.bindTypeId = bindTypeId;
	}
	
	/**
	 * 获取邮件提醒开始时间，0-24之间的整数
	 * @return 0-24之间整数
	 */	
	public int getNotifyBeginHour() {
		return notifyBeginHour;
	}
	/**
	 * 设置邮件提醒开始时间，0-24之间的整数
	 * @param notifyBeginHour 0-24之间整数
	 */
	public void setNotifyBeginHour(int notifyBeginHour) {
		this.notifyBeginHour = notifyBeginHour;
	}
	/**
	 * 获取邮件提醒结束时间，0-24之间的整数
	 * @return 0-24之间的整数
	 */
	public int getNotifyEndHour() {
		return notifyEndHour;
	}
	/**
	 * 设置邮件提醒结束时间，0-24之间的整数
	 * @param notifyEndHour 0-24之间的整数
	 */
	public void setNotifyEndHour(int notifyEndHour) {
		this.notifyEndHour = notifyEndHour;
	}
	/**
	 * 获取邮件提醒类型
	 * @return
	 */
	public NotifyType getNotifyType() {
		return notifyType;
	}
	/**
	 * 设置邮件提醒类型
	 * @param notifyType
	 */
	public void setNotifyType(NotifyType notifyType) {
		this.notifyType = notifyType;
	}
	/**
	 * 获取短信使用情况
	 * @return
	 */
	public MsInfo getSmsInfo() {
		return smsInfo;
	}
	/**
	 * 设置短信使用情况
	 * @param smsInfo
	 */
	public void setSmsInfo(MsInfo smsInfo) {
		this.smsInfo = smsInfo;
	}
	/**
	 * 获取彩信使用情况
	 * @return
	 */
	public MsInfo getMmsInfo() {
		return mmsInfo;
	}
	/**
	 * 设置彩信使用情况
	 * @param mmsInfo
	 */
	public void setMmsInfo(MsInfo mmsInfo) {
		this.mmsInfo = mmsInfo;
	}
	/**
	 * 用户登录校验cookie值
	 * @return
	 */
	public String getRemkey() {
		return remkey;
	}
	/**
	 * 用户登录校验cookie值
	 * @return
	 */
	public void setRemkey(String remkey) {
		this.remkey = remkey;
	}
	/**
	 * 获取用户当前可以使用的积分（邮箱积分，运营活动中用于显示的积分数）
	 * @return
	 */
	public int getIntegralEffect() {
		return integralEffect;
	}
	/**
	 * 设置用户当前可以使用的积分
	 * @param effectIntegral
	 */
	public void setIntegralEffect(int integralEffect) {
		this.integralEffect = integralEffect;
	}
	/**
	 * 获取用户等级（来自积分接口）
	 * @return
	 */
	public String getUserLevel() {
		return userLevel;
	}
	/**
	 * 设置用户等级（来自积分接口）
	 * @param userLevel
	 */
	public void setUserLevel(String userLevel) {
		this.userLevel = userLevel;
	}
	/**
	 * 获取用户已经消耗的积分
	 * @return
	 */
	public int getIntegralUse() {
		return integralUse;
	}
	/**
	 * 设置用户已经消耗的积分
	 * @param useIntegral
	 */
	public void setIntegralUse(int integralUse) {
		this.integralUse = integralUse;
	}
	/**
	 * 获取用户当前可以使用的活动积分（邮箱产品为特定活动定义的积分，非邮箱积分）
	 * @return
	 */
	public int getIntegralActive() {
		return integralActive;
	}
	/**
	 * 设置用户当前可以使用的活动积分
	 * @param activeIntegral
	 */
	public void setIntegralActive(int integralActive) {
		this.integralActive = integralActive;
	}
	/**
	 * 获取未读邮件数，需要调用{@link yzkf.api.Mail#getMailNum(String)}才会有数据
	 * @return 未读邮件数
	 */
	public int getUnreadMailNum() {
		return unreadMailNum;
	}
	/**
	 * 设置未读邮件数，仅供类库使用
	 * @param unreadMailNum 未读邮件数
	 */
	public void setUnreadMailNum(int unreadMailNum) {
		this.unreadMailNum = unreadMailNum;
	}
	/**
	 * 获取邮件总数，需要调用{@link yzkf.api.Mail#getMailNum(String)}才会有数据
	 * @return 邮件总数
	 */
	public int getTotalMailNum() {
		return totalMailNum;
	}
	/**
	 * 设置邮件总数，仅供类库使用
	 * @param totalMailNum 邮件总数
	 */
	public void setTotalMailNum(int totalMailNum) {
		this.totalMailNum = totalMailNum;
	}
	
	
}
