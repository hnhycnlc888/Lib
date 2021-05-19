package yzkf.enums;

/**
 * 活跃行为上报活跃行为枚举
 * 
 * @author qiulw
 *
 */
public enum ActiveFunction {
	/** [0]无活跃行为**/
    Nothing(0),
    /** [1001]WEB登录**/
    WebLogin(1001),
    /** [1002]WAP登录**/
    WapLogin(1002),
    /** [1003]SSO登录**/
    SSOLogin(1003),
    /** [1004]SSO转出**/
    SSOLogot(1004),
    /** [1005]发邮件**/
    SendMail(1005),
    /** [1006]发短信**/
    SendSMS(1006),
    /** [1007]发彩信**/
    SendMMS(1007),
    /** [1008]发Wappush**/
    SendWappush(1008),
    /** [1009]开户**/
    RegUser(1009),
    /** [1010]操作通讯录**/
    Directories(1010),
    /** [1011]操作网盘**/
    NetDisk(1011),
    /** [1012]设置个邮积分**/
    Integral(1012),
    /** [1013]设置邮箱伴侣**/
    Partner(1013),
    /** [1014]账单投递**/
    SendBill(1014),
    /** [1015]上行短信**/
    UploadSMS(1015),
    /** [1016]发送贺卡**/
    SendGreetingCard(1016),
    /** [1017]发送明信片**/
    SendPostcard(1017);

	private int value;
	ActiveFunction(int value){
		this.value = value;
	}
	public int getValue(){
		return this.value;
	}
}
