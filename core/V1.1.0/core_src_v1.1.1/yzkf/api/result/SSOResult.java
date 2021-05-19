package yzkf.api.result;

import yzkf.config.EnumConfig;

/**
 * 单点登录接口结果枚举
 * 
 * @author qiulw
 *
 */
public enum SSOResult implements Result {
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
	 * 无效的签名
	 */
	InvalidKey,
	/**
	 * 参数为空
	 */
	ParamterEmpty,
	/**
	 * 已过期
	 */
	Expires,
	/**
	 * 接口调用失败
	 */
	Failed,
	/**
	 * IP不合法
	 */
	InvalidIP,
	/**
	 * 用户未登录/不在线
	 */
	OffLine,
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
	/**
	 * 获取返回值
	 */
	public Object getValue() {
		return this.value;
	}
	/**
	 * 设置返回值
	 */
	public SSOResult setValue(Object value) {
		this.value = value;
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getValue(Class<T> type) {
		return (T) this.value;
	}
}
