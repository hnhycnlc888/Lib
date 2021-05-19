package yzkf.api.result;

import yzkf.config.EnumConfig;

/**
 * 日历接口返回结果
 * <p>如果 {@link #isOK()} == true，则可以通过 {@link #getValue()}相应获取返回值，不同的接口返回值的类型不同，详细查看接口方法的注释</p>
 * @author Leo
 *
 */
public enum CalendarResult implements Result  {
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
	 * 手机号为空
	 */
	EmptyMobile,
	/**
	 * 无效的手机号
	 */
	InvalidMobile,
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
	public CalendarResult setValue(Object value) {
		this.value = value;
		return this;
	}
}
