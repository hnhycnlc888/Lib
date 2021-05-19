package yzkf.enums;

/**
 * 【新版】单点登录到邮箱页面的枚举值
 * <p>指定单点登录成功后要打开/显示的标签页</p>
 * <p>示例：</p>
 * <p>SSOFlag.Compose 写邮件标签</p>
 * <p>SSOFlag.Other.setValue("20") 其它未在枚举体现的标签，自定义编号</p>
 * @author qiulw
 * 
 * 
 */
public enum SSOFlag {
	/**
	 * 通讯录
	 */
	Contacts("0"),
	/**
	 * １：写邮件
	 */
	Compose("1"),
	/**
	 * ２：收信箱
	 */
	Inbox("2"),
	/**
	 * ３：用户邮箱主页面。
	 */
	Index("3"),
	/**
	 * 4：邮件传真
	 */
	Fax("4"),
	/**
	 * 5：短信百宝箱
	 */	
	SMSTreasureBox("5"),
	/**
	 * 6：彩信仓库
	 */
	MMSWarehouse("6"),
	/**
	 * 7:彩信明信片
	 */
	MMSPostcard("7"),
	/**
	 * 8：通讯录
	 */
	AddressBook("8"),
	/**
	 * 9: 手机网盘
	 */
	NetworkDisk("9"),
	/**
	 * 10，日程提醒，
	 */
	Calendar("10"),
	/**
	 * 11，资讯中心，
	 */
	RSSCenter("11"),
	/**
	 * 12.网络书签
	 */
	Bookmark("12"),
	/**
	 * 13.电子名片，
	 */
	NameCard("13"),
	/**
	 * 14.发彩信
	 */
	MMS("14"),
	/**
	 * 15，读具体邮件，
	 */
	ReadMail("15"),
	/**
	 * 16，晒吧
	 */	
	ShowBa("16"),
	/**
	 * 其它
	 * <p></p>
	 */
	Other("0");
	
	private String value;
    public String getValue() {
        return value;
    }    
    public SSOFlag setValue(String value) {
		this.value = value;
		return this;
	}

	SSOFlag(String value) {
        this.value = value;
    }
}
