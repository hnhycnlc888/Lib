package cn.richinfo.core.mybatis.extend.vo;

public class RecordValue {
	private String colName;
	private Object value;
	private String sqlValue;
	
	public RecordValue(String colName, Object value) {
		this.colName = colName;
		this.value = value;
	}
	
	public RecordValue(String colName, String sqlValue) {
		this.colName = colName;
		this.sqlValue = sqlValue;
	}
	
	public void clearValue(){
		this.value = null;
		this.sqlValue = null;
	}

	public String getColName() {
		return colName;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		clearValue();
		this.value = value;
	}
	public String getSqlValue() {
		return sqlValue;
	}
	public void setSqlValue(String sqlValue) {
		clearValue();
		this.sqlValue = sqlValue;
	}
	
}
