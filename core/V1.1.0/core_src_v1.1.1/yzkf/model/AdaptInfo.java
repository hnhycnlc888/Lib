package yzkf.model;

import java.io.Serializable;

import yzkf.enums.MailVersion;

public class AdaptInfo implements Serializable {
	private static final long serialVersionUID = 6195902790440385481L;
	/**
	 * 邮箱版本
	 */
	private MailVersion mailVersion;
	/**
	 * 允许的版本，多个用英文逗号分开
	 */
	private String allowVersion;
	/**
	 * 网络类型：1：CMWAP 2：CMNET 3：WIFI 4：PC 宽带
	 */
	private String netType;
	/**
	 * 屏幕分辨率
	 */
	private String screenSize;
	/**
	 * 操作系统
	 */
	private String os;
	/**
	 * 操作系统版本
	 */
	private String osVersion;
	/**
	 * 浏览器
	 */
	private String browser;
	/**
	 * 浏览器版本
	 */
	private String browserVersion;
	/**
	 * 手机型号
	 */
	private String phoneModel;
	/**
	 * 手机厂商
	 */
	private String vendor;
	/**
	 * @return 邮箱版本
	 */
	public MailVersion getMailVersion() {
		return mailVersion;
	}
	/**
	 * @param mailVersion 邮箱版本
	 */
	public void setMailVersion(MailVersion mailVersion) {
		this.mailVersion = mailVersion;
	}
	/**
	 * @return 允许的版本，多个用英文逗号分开
	 */
	public String getAllowVersion() {
		return allowVersion;
	}
	/**
	 * @param allowVersion 允许的版本，多个用英文逗号分开
	 */
	public void setAllowVersion(String allowVersion) {
		this.allowVersion = allowVersion;
	}
	/**
	 * @return 网络类型：1：CMWAP 2：CMNET 3：WIFI 4：PC 宽带
	 */
	public String getNetType() {
		return netType;
	}
	/**
	 * @param netType 网络类型：1：CMWAP 2：CMNET 3：WIFI 4：PC 宽带
	 */
	public void setNetType(String netType) {
		this.netType = netType;
	}
	/**
	 * @return 屏幕分辨率
	 */
	public String getScreenSize() {
		return screenSize;
	}
	/**
	 * @param screenSize 屏幕分辨率
	 */
	public void setScreenSize(String screenSize) {
		this.screenSize = screenSize;
	}
	/**
	 * @return the 操作系统名称
	 */
	public String getOs() {
		return os;
	}
	/**
	 * @param os 操作系统名称
	 */
	public void setOs(String os) {
		this.os = os;
	}
	/**
	 * @return 操作系统版本号
	 */
	public String getOsVersion() {
		return osVersion;
	}
	/**
	 * @param osVersion 操作系统版本号
	 */
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
	/**
	 * @return 浏览器名称
	 */
	public String getBrowser() {
		return browser;
	}
	/**
	 * @param browser 浏览器名称
	 */
	public void setBrowser(String browser) {
		this.browser = browser;
	}
	/**
	 * @return 浏览器版本号
	 */
	public String getBrowserVersion() {
		return browserVersion;
	}
	/**
	 * @param browserVersion 浏览器版本号
	 */
	public void setBrowserVersion(String browserVersion) {
		this.browserVersion = browserVersion;
	}
	/**
	 * @return 手机型号
	 */
	public String getPhoneModel() {
		return phoneModel;
	}
	/**
	 * @param phoneModel 手机型号
	 */
	public void setPhoneModel(String phoneModel) {
		this.phoneModel = phoneModel;
	}
	/**
	 * @return 手机厂商
	 */
	public String getVendor() {
		return vendor;
	}
	/**
	 * @param vendor 手机厂商
	 */
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	

}
