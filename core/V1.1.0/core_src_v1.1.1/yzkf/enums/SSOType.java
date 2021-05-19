package yzkf.enums;

/**
 * 单点登录到邮箱页面的枚举值
 * <br/>
 * 指定单点登录成功后要打开/显示的标签页
 * 
 * @author qiulw
 * @deprecated 已弃用，改用{@link SSOFlag}
 */
public enum SSOType {
	
	/**
	 * 邮箱首页
	 */
	Mail("00"),
	/**
	 * 账单文件夹
	 */
	Bill("00"),
	/**
	 * [01]自写短信，单独的短信发送页面。
	 */
	SinceWrittenSMS("01"),
	/**
	 * [02]短信百宝箱
	 */
	SMSTreasureBox("02"),
	/**
	 * [03]自写彩信，单独的彩信发送页面。
	 */
	SinceWrittenMMS("03"),
	/**
	 * [04]彩信仓库-彩信珍藏夹
	 */
	MMSWarehouse_Favorites("04"),
	/**
	 * [05]彩信明信片
	 */
	MMSPostcard("05"),
	/**
	 * [06]通讯录
	 */
	AddressBook("06"),
	/**
	 * [07]网盘
	 */
	NetworkDisk("07"),
	/**
	 * [08]彩信仓库-创意贺卡
	 */
	MMSWarehouse_CreativeCards("08"),
	/**
	 * [09]邮箱自写短信，是邮箱内嵌短信发送页面。
	 */
	Mail139_SinceWrittenSMS("09"),
	/**
	 * [10]邮箱自写彩信，是邮箱内嵌彩信发送页面。
	 */
	Mail139_SinceWrittenMMS("10"),
	/**
	 * [11]139邮箱_传真
	 */
	Mail139_Fax("11"),
	/**
	 * [12]139邮箱_发件箱
	 */
	Mail139_SendBox("12"),
	/**
	 * [13]139邮箱_收件箱
	 */
	Mail139_InBox("13"),
	/**
	 * [14]139邮箱_邮件搜索
	 */
	Mail139_MailSearch("14"),
	/**
	 * [15]139邮箱_设置-》邮件伴侣
	 */
	Mail139_MailPartner("15"),
	/**
	 * [16]139邮箱_网络收藏夹
	 */
	Mail139_NetFavor("16"),
	/**
	 * [17]139邮箱_邀请好友
	 */
	Mail139_InviteFrd("17"),
	/**
	 * [18]139邮箱_设置-》修改密码
	 */
	Mail139_ModifyPass("18"),
	/**
	 * [19]139邮箱_日程提醒
	 */
	Mail139_Calendar("19"),
	/**
	 * [20]139邮箱_RSS资讯中心
	 */
	Mail139_RSSCenter("20"),
	/**
	 * [21]139邮箱_设置-》别名设置
	 */
	Mail139_AliaSet("21"),
	/**
	 * [22]139邮箱_收发传真
	 */
	Mail139_RecvFax("22"),
	/**
	 * [24]139邮箱_精品订阅
	 */
	Mail139_JPDY("24"),
	/**
	 * [25]139邮箱_新版手机网盘
	 */
	Mail139_NewNetDisk("25"),
	/**
	 * [26]139邮箱_明信片
	 */
	Mail139_PostCard("26"),
	/**
	 * [28]139邮箱_视频邮件
	 */
	Mail139_VideoMail("28"),
	/**
	 * [29]139邮箱_文件快递
	 */
	Mail139_FileExpress("29");
	
	private String value;
    public String getValue() {
        return value;
    }
    SSOType(String value) {
        this.value = value;
    }
}

