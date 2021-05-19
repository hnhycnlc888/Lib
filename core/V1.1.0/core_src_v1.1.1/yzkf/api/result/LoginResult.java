package yzkf.api.result;

import yzkf.config.EnumConfig;

public enum LoginResult implements Result {

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
	 * 所在省份不允许参加
	 */
	ProvNotAllow,
	/**
	 * 所在地区不允许参加
	 */
	AreaNotAllow,
	/**
	 * 非中国移动号码（联通/电信）
	 */
	NotChinaMobile,
	/**
	 * 活动未开始
	 */
	NotStart,
	/**
	 * 活动已结束
	 */
	GameOver,
	/**
	 * 登录失败次数过多，禁止登录
	 */
	FailedTooMuch,
	/**
	 * 其它信息，参考接口返回信息
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
	public LoginResult setValue(Object value) {
		this.value = value;
		return this;
	}

}
