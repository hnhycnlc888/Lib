package yzkf.enums;

public enum SmsComeFrom {

	/**日程提醒*/ Reminder(1),
	/**即时发送*/ Normal(2),
	/**定时发送*/ Timer(3),
	/**Wap下发的短信*/ Wap(4),
	/**Web下发的短信（如验证码）*/ Web(5),
	/**061390短信转发*/ Bulk(6),
	/**Sms下发的短信*/ Sms(7),
	/**短信转移项目帮用户转发的短信*/ Forward(8),
	/**推荐平台下发的推荐信息*/ Recommend(9),
	/**系统下发短信提示用户接收传真*/ Fax(41),
	/**回A读取邮件正文*/ ReplayA(49),
	/**开户成功下发短信*/ Register(51),
	/**注销成功下发短信*/ Cancel(52),
	/**用户找回密码成功下发的短信*/ LostPassword(53),
	/**容量变更下发的短信*/ ChangePassword(54),
	/**企业邮箱管理员发送短信*/ AdminSend(55),
	/**运营群发*/ OperationBulk(90),
	/**运营系统群发*/ SystemBulk(91),
	/**SPS CENTER*/ SpsCenter(100),
	/**用户用A读邮件*/ ReadMail(101),
	/**邮件到达通知*/ MailComing(102),
	/**运维监控报警短信*/ Alarm(300),
	/**139账本*/ BookKeeping(301),
	/**活跃用户下发短信*/ Active(302),
	/**家庭邮箱*/ Family(303);
	private int value;
	public int getValue() {
	    return this.value;
	}
	SmsComeFrom(int value) {
	    this.value = value;
	}
}
