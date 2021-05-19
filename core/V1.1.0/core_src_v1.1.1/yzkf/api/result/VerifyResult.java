package yzkf.api.result;

import yzkf.config.EnumConfig;

public enum VerifyResult implements Result {
	OK{
		public boolean isOK(){
			return true;
		}
	},
	/**
	 * 未知错误
	 */
	Unknow,
	/**
	 * 验证码错误
	 */
	Invalid,
	/**
	 * 空验证码
	 */
	Empty,
	/**
	 * 空AgentID
	 */
	NullAgent,
	/**
	 * 已过期
	 */
	Expires,
	/**
	 * 操作频繁
	 */
	Illegal,
	/**
	 * 获取验证码失败
	 */
	Failed,
	/**
	 * 服务器繁忙
	 */
	ServerBusy,
	/**
	 * 接口返回错误描述，使用{@link #getValue()}来获取提示信息
	 */
	Other{
		@Override
		public String getDescr(){
			return this.getValue(String.class);
		}
	};
	
	/**
	 * 获取枚举是否等于OK
	 * @return
	 */
	@Override
	public boolean isOK(){
		return false;
		//return this == OK;
	}
	/**
	 * 获取枚举的描述
	 * @return
	 */
	@Override
	public String getDescr(){
		return Describe.query(this);
	}
	/**
	 * 获取枚举描述，说明从配置对象中读取
	 * @param config 包含返回值描述的配置对象
	 * @return
	 * @throws ParserConfigException
	 */
	@Override
	public String getDescr(EnumConfig config){
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
	public VerifyResult setValue(Object value) {
		this.value = value;
		return this;
	}
}
