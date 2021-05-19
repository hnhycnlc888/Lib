package cn.richinfo.login.pojo;

public class Result {
	private boolean isOK;
	private String code;
	private String descr;
	private UserInfo userInfo;
	
	/**
	 * 获取是否登录成功，成功true，失败false
	 * @return
	 */
	public boolean isOK() {
		return isOK;
	}
	/**
	 * 是否登录成功，成功true，失败false
	 * @param isOK
	 */
	public void setOK(boolean isOK) {
		this.isOK = isOK;
	}
	/**
	 * 获取接口返回信息描述
	 * @return
	 */
	public String getDescr() {
		return descr;
	}
	/**
	 * 设置接口返回信息描述
	 * @param descr
	 */
	public void setDescr(String descr) {
		this.descr = descr;
	}
	/**
	 * 获取用户信息
	 * @return
	 */
	public UserInfo getUserInfo() {
		return userInfo;
	}
	/**
	 * 设置用户信息
	 * @param userInfo
	 */
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	/**
	 * 获取接口返回码
	 * @return
	 */
	public String getCode() {
		return code;
	}
	/**
	 * 设置接口返回码
	 * @param code
	 */
	public void setCode(String code) {
		this.code = code;
	}
	
}
