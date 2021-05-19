package yzkf.api.result;

import yzkf.config.EnumConfig;

public enum MailResult implements Result {

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
	 * API服务器繁忙
	 */
	ApiBusy,
	/**
	 * 空账号
	 */
	EmptyAccount,
	/**
	 * 空号码
	 */
	EmptyMobile,
	/**
	 * 空密码
	 */
	EmptyPassword,
	/**
	 * 空sid
	 */
	EmptySid,
	/**
	 * 无效账号
	 */
	InvalidAccount,
	/**
	 * 无效密码
	 */
	InvalidPassword,
	/**
	 * 账号错误
	 */
	WrongAccount,
	/**
	 * 密码错误
	 */
	WrongPassword,
	/**
	 * 账户已冻结
	 */
	Frozen,
	/**
	 * 账户不存在
	 */
	Inexistent,
	/**
	 * 账户已注销
	 */
	Cancelled,
	/**
	 * 无效的手机号码
	 */
	InvalidMobile,
	/**
	 * 非中国移动号码
	 */	
	NotChinaMobile,
	/**
	 * 手机号和省份编号不匹配
	 */
	MismatchProv,
	/**
	 * 手机号和城市编号不匹配
	 */
	MismatchArea,
	/**
	 * 无效的省份编号
	 */
	InvalidProvCode,
	/**
	 * 无效的城市编号
	 */
	InvalidAreaCode,
	/**
	 * 开户ServiceCode非法
	 */
	InvalidServiceCode,
	/**
	 * 账户已存在
	 */
	ExistsYet,
	/**
	 * 已过期
	 */
	Expires,
	/**
	 * 接口执行失败
	 */
	ApiFailed,
	/**
	 * 注册失败
	 */
	RegisterFailed,
	/**
	 * 用户不在线
	 */
	OffLine,
	/**
	 * 已开通账单
	 */
	BillOpened,
	/**
	 * 未开通账单
	 */
	BillNotOpen,
	/**
	 * 已开通邮件提醒
	 */
	NotifyOpened,
	/**
	 * 未开通邮件提醒
	 */
	NotifyNotOpen,
	/**
	 * 已开通业务
	 */
	Opened,
	/**
	 * 未开通业务
	 */
	NotOpen,
	/**
	 * 短信内容超长
	 */
	InvalidSmsBody,
	/**
	 * 开通账单失败
	 */
	OpenBillFailed,
	/**
	 * 开通增值业务失败
	 */
	OpenServiceFailed,
	/**
	 * 部分成功
	 */
	PartOK,
	/**
	 * 接口返回错误描述，使用{@link #getValue()}来获取提示信息
	 */
	Other{
		@Override
		public String getDescr(){
			return this.getValue(String.class);
		}
	};
	
	@Override
	public boolean isOK() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDescr() {
		return Describe.query(this);
	}

	@Override
	public String getDescr(EnumConfig config) {
		return Describe.query(config, this);
	}
	private Object value;
	/**
	 * 获取返回值
	 */
	@Override
	public Object getValue() {
		return this.value;
	}
	/**
	 * 获取接口返回值
	 * @param type
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getValue(Class<T> type) {
		return (T) this.value;
	}
	/**
	 * 设置返回值
	 */
	@Override
	public MailResult setValue(Object value) {
		this.value = value;
		return this;
	}
}
