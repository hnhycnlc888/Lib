package yzkf.api.result;

import yzkf.config.EnumConfig;

public enum SMSResult implements Result {

	OK{
		@Override
		public boolean isOK(){
			return true;
		}
	},
	/**
	 * 未知错误
	 */
	Unknow,
	/**
	 * 发送方号码有误
	 */
	InvalidSender,
	/**
	 * 计费方号码有误
	 */
	InvalidFeeMobile,
	/**
	 * 下发端口号有误
	 */
	InvalidSpNumber,
	/**
	 * 短信内容有误
	 */
	InvalidSmsBody,
	/**
	 * 有一个或多个接收方号码有误
	 */
	InvalidReceiver,
	/**
	 * 短信内容编码格式错误
	 */
	InvalidBodyEncode,
	/**
	 * 错误的发送时间
	 */
	InvalidSendTime,
	/**
	 * 错误的计费类型
	 */
	InvalidFeeType,
	/**
	 * 业务级别黑名单用户
	 */
	BlacklistUser4Business,
	/**
	 * 自写短信黑名单用户
	 */
	BlacklistUser4Sms,
	/**
	 * 黑名单IP
	 */
	BlacklistIP,
	/**
	 * 部分发送成功
	 */
	PartialOK,
	/**
	 * 部分成功，返回超过限制的手机号码
	 */
	PartialLimit,
	/**
	 * 接收方号码数超过限制
	 */
	TooManyReceiver,
	/**
	 * 包含被过滤的关键字
	 */
	ContainFilterWord,
	/**
	 * 超过每天允许发送的最大条数
	 */
	DayLimit,
	/**
	 * 超过每月允许发送的最大条数
	 */
	MonthLImit,
	/**
	 * 短信入库成功, 写行为统计失败
	 */
	OkButOper,
	/**
	 * 免费短信已用完
	 */
	NoFreeSms;
	@Override
	public boolean isOK() {
		return false;
	}

	@Override
	public String getDescr() {
		// TODO Auto-generated method stub
		return Describe.query(this);
	}

	@Override
	public String getDescr(EnumConfig config) {
		return Describe.query(config, this);
	}
	private Object value;

	@Override
	public Object getValue() {
		return this.value;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getValue(Class<T> type) {
		return (T) this.value;
	}
	
	@Override
	public SMSResult setValue(Object value) {
		this.value = value;
		return this;
	}
}
