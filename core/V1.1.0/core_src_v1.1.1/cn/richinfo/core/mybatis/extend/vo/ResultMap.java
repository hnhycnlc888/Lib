package cn.richinfo.core.mybatis.extend.vo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

public class ResultMap extends HashMap<String, Object> {
	private static final long serialVersionUID = 9155660212774034975L;

	public Object put(String key, Object value) {
		if(key instanceof String){
			key = key.toUpperCase();
		}
       return super.put(key, value);
    }
	
	public Boolean getBooleanValue(String key){
		Object value = this.get(key);
		if(value == null)
			return null;
		if(value instanceof Boolean){
			return (Boolean) value;
		}
		return BooleanUtils.toBoolean(value.toString());
	}
	
	public boolean getBoolean(String key){
		Boolean value = getBooleanValue(key);
		return value != null ? value : false;
	}
	
	public Integer getIntegerValue(String key) {
		Object value = this.get(key);
		if(value == null) return null;
		if(value instanceof String) {
			if(StringUtils.isBlank(value+"")){
				return null;
			} else {
				try {
					return Integer.parseInt(((String) value));
				} catch (Exception e) {
					throw new RuntimeException("无法将key【"+key+"】,value【"+value+"】转换为Integer类型！");
				}
			}
		} else if(value instanceof Number) {
			return ((Number) value).intValue();
		} else {
			throw new RuntimeException("无法将key【"+key+"】,value【"+value+"】转换为Integer类型！");
		}
	}
	
	public int getInt(String key){
		Integer value = this.getIntegerValue(key);
		return value != null ? value : 0;
	}
	
	public Long getLongValue(String key) {
		Object value = this.get(key);
		if (value == null) {
			return null;
		}
		if (value instanceof Number) {
			return ((Number) value).longValue();
		} else if(value instanceof String) {
			if(StringUtils.isBlank(value+"")) {
				return null;
			} else {
				try {
					return Long.parseLong(value+"");
				}catch(Exception e) {
					throw new RuntimeException("无法将key【"+key+"】,value【"+value+"】转换为Long类型！");
				}
			}
		} else {
			throw new RuntimeException("无法将key【"+key+"】,value【"+value+"】转换为Long类型！");
		}
	}
	
	public long getLong(String key){
		Long value = this.getLongValue(key);
		return value != null ? value : 0;
	}
	
	public Float getFloatValue(String key){
		Object value = this.get(key);
		if(value == null)
			return null;
		if(value instanceof Number){
			return ((Number) value).floatValue();
		} else if(value instanceof String){
			if(StringUtils.isBlank(value.toString())){
				return null;
			} else {
				try {
					return Float.parseFloat(value.toString());
				} catch(Exception e){
					throw new RuntimeException("无法将key【"+key+"】,value【"+value+"】转换为Float类型！");
				}
			}
		} else {
			throw new RuntimeException("无法将key【"+key+"】,value【"+value+"】转换为Float类型！");
		}
	}
	
	public float getFloat(String key){
		Float value = this.getFloatValue(key);
		return value != null ? value : 0;
	}
	
	public Double getDoubleValue(String key){
		Object value = this.get(key);
		if(value == null){
			return null;
		}
		if(value instanceof Number){
			return ((Number) value).doubleValue();
		} else if(value instanceof String){
			if(StringUtils.isBlank(value.toString())){
				return null;
			} else {
				try {
					return Double.parseDouble(value.toString());
				} catch (Exception e) {
					throw new RuntimeException("无法将key【"+key+"】,value【"+value+"】转换为Double类型！");
				}
			}
		} else {
			throw new RuntimeException("无法将key【"+key+"】,value【"+value+"】转换为Double类型！");
		}
	}
	
	public double getDouble(String key){
		Double value = this.getDoubleValue(key);
		return value != null ? value : 0;
	}
	
	public BigDecimal getBigDecimalValue(String key){
		Object value = this.get(key);
		if(value == null)
			return null;
		if(value instanceof BigDecimal){
			return (BigDecimal) value;
		}
		if(value instanceof BigInteger){
			return new BigDecimal((BigInteger) value);
		}
		if(StringUtils.isNotBlank(value.toString())){
			return new BigDecimal(value.toString());
		}
		return null;
	}
	
	public BigInteger getBigIntegerValue(String key){
		Object value = this.get(key);
		if(value == null)
			return null;
		if(value instanceof BigInteger){
			return (BigInteger) value;
		}
		if(value instanceof Float || value instanceof Double) {
			return BigInteger.valueOf(((Number) value).longValue());
		}
		if(StringUtils.isNotBlank(value.toString())){
			return new BigInteger(value.toString());
		}
		return null;
	}
	
	public Date getDateValue(String key){
		Object value = this.get(key);
		if(value == null) 
			return null;
		
		if(value instanceof Calendar) {
			return ((Calendar) value).getTime();
		}
		
		if(value instanceof Date) {
			return (Date) value;
		}
		
		if(value instanceof Number) {
			long longValue = ((Number) value).longValue();
			return new Date(longValue);
		}
		
		throw new ClassCastException("日期类型转换异常，value=" + value + ", 原始类型为：" + value.getClass().getCanonicalName());
	}
	
	public String getStringValue(String key) {
		Object value = this.get(key);
		if(value == null) {
			return null;
		}
		if(value instanceof String) {
			return ((String) value);
		} else if (value instanceof Clob) {
			return clob2Str((Clob) value);
		} else {
			return value.toString();
		}
	}
	
	private String clob2Str(Clob clob) {
		try {
			return (clob != null ? clob.getSubString(1, (int) clob.length())
					: null);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
