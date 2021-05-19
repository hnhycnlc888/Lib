package yzkf.api.result;

import yzkf.config.EnumConfig;

public enum SubscribeResult implements Result {
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
	 * 空号码
	 */
	EmptyMobile,
	/**
	 * 无效号码
	 */
	InvalidMobile,
	/**
	 * 参数为空
	 */
	EmptyParam,
	/**
	 * 订阅失败
	 */
	Failed,
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
	public Object getValue() {
		return this.value;
	}
	/**
	 * 获取返回值
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue(Class<T> type) {
		return (T) this.value;
	}
	/**
	 * 设置返回值
	 */
	public SubscribeResult setValue(Object value) {
		this.value = value;
		return this;
	}

}