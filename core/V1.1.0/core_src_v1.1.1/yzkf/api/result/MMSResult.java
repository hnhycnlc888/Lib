package yzkf.api.result;

import yzkf.config.EnumConfig;

public enum MMSResult implements Result {

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
	 * 彩信标题太长
	 */
	InvalidMmsSubject;
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
	public MMSResult setValue(Object value) {
		this.value = value;
		return this;
	}
}
