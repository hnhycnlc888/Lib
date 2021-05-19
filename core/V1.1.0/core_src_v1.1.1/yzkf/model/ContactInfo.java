package yzkf.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ContactInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4348228754355598884L;	
	/**
	 * 联系人ID
	 */
	private long serialId;
	/**
	 * 用户编号
	 */
	private String userNumber;
	/**
	 * 联系人姓
	 * @see #getName()
	 */
	private String addrFirstName;
	/**
	 * 联系人名
	 */
	private String addrSecondName;
	/**
	 * 移动电话(个人)
	 */
	private String mobilePhone;
	/**
	 * 商务电话(移动)
	 */
	private String businessMobile;
	/**
	 * 其它电话(多个电话以,分开)
	 */
	private String otherMobilePhone;
	/**
	 * 个人邮箱
	 */
	private String familyEmail;
	/**
	 * 商务邮箱
	 */
	private String businessEmail;
	/**
	 * 其它邮箱
	 */
	private String otherEmail;
	/**
	 * 姓名对应的首字母
	 */
	private String firstNameword;
	/**
	 * 姓名全拼
	 */
	private String fullNameword;
	/**
	 * 姓名简拼
	 */
	private String firstWord;
	/**
	 * 头像地址
	 */
	private String imageUrl;
	/**
	 * 头像地址的根路径
	 */
	public static String ImageUrlBase;
	/**
	 * 最近/紧密联系人类型：F：传真，M：手机号码，E：电子邮件
	 */
	private String addrType;
	/**
	 * 生日
	 */
	private String birDay;
	/**
	 * 家庭住址
	 */
	private String homeAddress;
	
	/**
	 * @return 联系人ID
	 */
	public long getSerialId() {
		return serialId;
	}
	/**
	 * @param serialId 联系人ID
	 */
	public void setSerialId(long serialId) {
		this.serialId = serialId;
	}
	/**
	 * 
	 * @return 用户编号/号码
	 */
	public String getUserNumber() {
		return userNumber;
	}
	/**
	 * 
	 * @param userNumber 用户编号/号码
	 */
	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}
	/**
	 * @return 联系人姓
	 *  @see #getName()
	 */
	public String getAddrFirstName() {
		return addrFirstName;
	}
	/**
	 * @param addrFirstName 联系人姓 
	 */
	public void setAddrFirstName(String addrFirstName) {
		this.addrFirstName = addrFirstName;
	}
	/**
	 * @return 联系人名
	 *  @see #getName()
	 */
	public String getAddrSecondName() {
		return addrSecondName;
	}
	/**
	 * @param addrSecondName 联系人名 
	 */
	public void setAddrSecondName(String addrSecondName) {
		this.addrSecondName = addrSecondName;
	}
	/**
	 * @return 移动电话(个人)
	 * @see #getMobile();
	 */
	public String getMobilePhone() {
		return mobilePhone;
	}
	/**
	 * @param mobilePhone 移动电话(个人) 
	 * @see #setMobile(String)
	 */
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	/**
	 * @return 商务电话(移动)
	 * @see #getMobile();
	 */
	public String getBusinessMobile() {
		return businessMobile;
	}
	/**
	 * @param businessMobile 商务电话(移动) 
	 */
	public void setBusinessMobile(String businessMobile) {
		this.businessMobile = businessMobile;
	}
	/**
	 * @return 其它电话(多个电话以,分开)
	 * @see #getMobile();
	 */
	public String getOtherMobilePhone() {
		return otherMobilePhone;
	}
	/**
	 * @param otherMobilePhone 其它电话(多个电话以,分开) 
	 */
	public void setOtherMobilePhone(String otherMobilePhone) {
		this.otherMobilePhone = otherMobilePhone;
	}
	/**
	 * @return 个人邮箱
	 * @see #getEmail()
	 */
	public String getFamilyEmail() {
		return familyEmail;
	}
	/**
	 * @param familyEmail 个人邮箱 
	 */
	public void setFamilyEmail(String familyEmail) {
		this.familyEmail = familyEmail;
	}
	/**
	 * @return 商务邮箱
	 * @see #getEmail()
	 */
	public String getBusinessEmail() {
		return businessEmail;
	}
	/**
	 * @param businessEmail 商务邮箱 
	 */
	public void setBusinessEmail(String businessEmail) {
		this.businessEmail = businessEmail;
	}
	/**
	 * @return 其它邮箱
	 * @see #getEmail()
	 */
	public String getOtherEmail() {
		return otherEmail;
	}
	/**
	 * @param otherEmail 其它邮箱 
	 */
	public void setOtherEmail(String otherEmail) {
		this.otherEmail = otherEmail;
	}
	/**
	 * @return 姓名对应的首字母
	 */
	public String getFirstNameword() {
		return firstNameword;
	}
	/**
	 * @param firstNameword 姓名对应的首字母 
	 */
	public void setFirstNameword(String firstNameword) {
		this.firstNameword = firstNameword;
	}
	/**
	 * @return 姓名全拼
	 */
	public String getFullNameword() {
		return fullNameword;
	}
	/**
	 * @param fullNameword 姓名全拼 
	 */
	public void setFullNameword(String fullNameword) {
		this.fullNameword = fullNameword;
	}
	/**
	 * @return 姓名简拼
	 */
	public String getFirstWord() {
		return firstWord;
	}
	/**
	 * @param firstWord 姓名简拼 
	 */
	public void setFirstWord(String firstWord) {
		this.firstWord = firstWord;
	}
	/**
	 * @return 头像地址
	 */
	public String getImageUrl() {
		if(imageUrl!=null && !imageUrl.startsWith("http"))
			return ImageUrlBase+imageUrl;
		return imageUrl;
	}
	/**
	 * @param imageUrl 头像地址 
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	/**
	 * 获取通讯录中的手机号码
	 * @return 依次从 mobilePhone、businessMobile、otherMobilePhone中读取第一个非空属性
	 */
	public String getMobile(){
		if(mobilePhone != null && !mobilePhone.isEmpty())
			return mobilePhone;
		if(userNumber != null && !userNumber.isEmpty()){
			if(userNumber.startsWith("86"))
				return userNumber.substring(2);
			return userNumber;
		}
		if(businessMobile != null && !businessMobile.isEmpty())
			return businessMobile;
		if(otherMobilePhone != null && !otherMobilePhone.isEmpty())
			return otherMobilePhone;
		return null;
	}
	/**
	 * 设置通讯录号码
	 * @param mobile
	 */
	public void setMobile(String mobile){
		this.mobilePhone = mobile;
	}
	/**
	 * 获取通讯录中的邮件地址
	 * @return 依次从 familyEmail、businessEmail、otherEmail中读取第一个非空属性
	 */
	public String getEmail(){
		if(familyEmail != null && !familyEmail.isEmpty())
			return familyEmail;
		if(businessEmail != null && !businessEmail.isEmpty())
			return businessEmail;
		if(otherEmail != null && !otherEmail.isEmpty())
			return otherEmail;
		if(getMobile() != null)
			return getMobile() + "@139.com";
		return null;
	}
	/**
	 * 设置通讯录邮箱地址
	 * @param email
	 */
	public void setEmail(String email){
		this.familyEmail = email;
	}
	/**
	 * 获取联系人名称。
	 * @return addrFirstName + addrSecondName 或者 {@link #getMobile()}
	 */
	public String getName() {
		if((addrFirstName != null && !addrFirstName.isEmpty()) || (addrSecondName !=null && !addrSecondName.isEmpty()))
			return (addrFirstName==null?"":addrFirstName) + (addrSecondName==null?"":addrSecondName);
		if(getMobile() != null)
			return getMobile();
		return null;
	}
	/**
	 * 设置通讯录名称
	 * @param name
	 */
	public void setName(String name){
		this.addrSecondName = name;
	}
	/**
	 * @return the 获取最近/紧密联系人类型：F：传真，M：手机号码，E：电子邮件
	 */
	public String getAddrType() {
		return addrType;
	}
	/**
	 * 设置最近/紧密联系人类型
	 * @param addrType  F：传真，M：手机号码，E：电子邮件
	 */
	public void setAddrType(String addrType) {
		this.addrType = addrType;
	}
	/**
	 * 获取生日信息
	 * <p>注意：需通过能够返回所有字段通讯录接口(如{@link yzkf.api.Contacts#getAllWithDetail(String, int, int)})才能获得该属性<br/>
	 * 精简字段接口({@link yzkf.api.Contacts#getAll(String, int, int)}))中始终返回空</p>
	 * @return 生日信息，格式yyyy-MM-dd
	 */
	public String getBirDay() {
		return birDay;
	}
	/**
	 * 设置生日信息
	 * @param birDay 格式yyyy-MM-dd
	 */
	public void setBirDay(String birDay) {
		this.birDay = birDay;
	}
	/**
	 * @return 生日信息的时间对象，该方法将  {@link #getBirDay()} 转换为时间对象
	 */
	public Date getBirthday(){
		if(birDay == null || birDay.length() == 0)
			return null;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(birDay);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 用户昵称（非精简字段）
	 */
	private String addrNickName;
	/**
	 * 用户性别（非精简字段）
	 */
	private int userSex;
	/**
	 * 
	 * @return 用户昵称
	 */
	public String getAddrNickName() {
		return addrNickName;
	}
	/**
	 * 
	 * @param addrNickName 用户昵称
	 */
	public void setAddrNickName(String addrNickName) {
		this.addrNickName = addrNickName;
	}
	/**
	 * 
	 * @return 用户性别
	 */
	public int getUserSex() {
		return userSex;
	}
	/**
	 * 
	 * @param userSex 用户性别
	 */
	public void setUserSex(int userSex) {
		this.userSex = userSex;
	}
	/**
	 * 
	 * @return 家庭住址
	 */
	public String getHomeAddress() {
		return homeAddress;
	}
	/**
	 * 
	 * @param homeAddress 家庭住址
	 */
	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}
	
}
