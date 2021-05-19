package yzkf.api.result;

import yzkf.config.EnumConfig;

public enum SMSVerifyResult implements Result {
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
	 * 无效的手机号码
	 */
	InvalidMobile,
	/**
	 * 非中国移动号码
	 */	
	NotChinaMobile,
	/**
	 * 获取验证码太频繁
	 */
	Illegal,
	/**
	 * 输入的验证码为空
	 */
	NullInput,
	/**
	 * 验证码输入错误
	 */
	Wrong,
	/**
	 * 验证码已过期
	 */
	Expired,
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
	public SMSVerifyResult setValue(Object value) {
		this.value = value;
		return this;
	}

}
