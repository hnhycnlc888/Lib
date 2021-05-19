package yzkf.enums;
/**
 * 【新版】WapSSO2单点登录到邮箱页面的枚举值
 * <p>指定单点登录成功后要显示的功能页</p>
 * <p>示例：</p>
 * <p>WapSSOFlag.Index 写邮件标签</p>
 * <p>WapSSOFlag.Other.setValue("20") 其它未在枚举体现的标签，自定义编号</p>
 * @author qiulw
 * @version 1.3.0 2014.05.21
 * 
 */
public enum WapSSOFlag {
	/**
	 * 邮箱首页
	 */
	Index("0"),
	/**
	 * １：写邮件
	 */
	Compose("1"),
	/**
	 * ２：收件箱
	 */
	Inbox("2"),
	/**
	 * ３：发短信
	 */
	SMS("3"),
	/**
	 * 4：读邮件
	 */
	Read("4"),
	/**
	 * 5：网盘
	 */	
	Disk("5"),
	/**
	 * 其它
	 * <p></p>
	 */
	Other("0");
	
	private String value;
    public String getValue() {
        return value;
    }    
    public WapSSOFlag setValue(String value) {
		this.value = value;
		return this;
	}

    WapSSOFlag(String value) {
        this.value = value;
    }
}
