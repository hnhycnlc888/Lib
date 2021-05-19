package yzkf.api.result;

import yzkf.config.EnumConfig;

/**
 * 网盘接口返回结果枚举
 * @author Leo
 *
 */
public enum DiskResult implements Result {
	
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
	public DiskResult setValue(Object value) {
		this.value = value;
		return this;
	}
}
