package cn.richinfo.core.mybatis.extend.vo;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.Assert;

import cn.richinfo.core.utils.id.CUIDHexGenerator;

public class Record {
	
	private String tableName = null;
	private String primaryKeyColumnName = null;
	private Object primaryKeyValue = null;
	private Map<String, RecordValue> columnName2RecValue = new LinkedHashMap<String, RecordValue>();
	
	public static Record buildInsertRecord(String tableName){
		return new Record(tableName, null, null);
	}
	
	public static Record buildUpdateRecord(String tableName, String primaryKeyColName, Object primaryKeyValue){
		Assert.notNull(primaryKeyValue, "primary key value do not allow blank");
		Assert.hasText(primaryKeyColName, "primary key column name do not allow blank");
		return new Record(tableName, primaryKeyColName, primaryKeyValue);
	}
	
	private Record(String tableName, String primaryKeyColumnName, Object primaryKeyValue) {
		Assert.hasText(tableName, "tableName do not allow blank");
		this.tableName = tableName;
		this.primaryKeyColumnName = primaryKeyColumnName;
		this.primaryKeyValue = primaryKeyValue;
	}

	public void addColValue(String colName, Object value) {
		Assert.hasText(colName, "column name do not allow blank");
		RecordValue rcv = new RecordValue(colName, value);
		columnName2RecValue.put(colName, rcv);
	}

	public void addColValueNotNull(String colName, Object value) {
		if(value != null){
			this.addColValue(colName, value);
		}
	}
	
	public void addColValueContain(String colName, Map<String, ?> map){
		this.addColValueContain(colName, map, colName);
	}
	
	public void addColValueContain(String colName, Map<String, ?> map, String mapKeyName){
		if(map != null){
			if(map.containsKey(mapKeyName)){
				this.addColValue(colName, map.get(mapKeyName));
			}
		}
	}
	
	public void addColSqlValue(String colName, String subsql){
		Assert.hasText(colName, "column name do not allow blank");
		Assert.hasText(subsql, "sub-sql do not allow blank");
		RecordValue rcv = new RecordValue(colName, subsql);
		columnName2RecValue.put(colName, rcv);
	}
	
	public void addColSqlValueContain(String colName, Map<String, Object> map, String subsql){
		this.addColSqlValueContain(colName, map, colName, subsql);
	}
	
	public void addColSqlValueContain(String colName, Map<String, Object> map, String mapkey, String subsql){
		if(map != null) {
			if(map.containsKey(mapkey)) {
				this.addColSqlValue(colName, subsql);
			}
		}
	}
	
	public String addPrimaryKeyValue(){
		return this.addPrimaryKeyValue("CUID");
	}
	
	public String addPrimaryKeyValue(String primaryKeyColName){
		String cuid = CUIDHexGenerator.getInstance().generate(tableName);
		this.addColValue(primaryKeyColName, cuid);
		return cuid;
	}
	
	public RecordValue removeColumn(String colName) {
		return this.columnName2RecValue.remove(colName);
	}

	public String getTableName() {
		return this.tableName;
	}

	public Collection<RecordValue> getRecordValues(){
		return this.columnName2RecValue.values();
	}

	public Object getPrimaryKeyValue() {
		return primaryKeyValue;
	}

	public String getPrimaryKeyColumnName() {
		return primaryKeyColumnName;
	}
}
