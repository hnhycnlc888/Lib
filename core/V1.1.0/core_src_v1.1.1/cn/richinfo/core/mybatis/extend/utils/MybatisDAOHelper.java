package cn.richinfo.core.mybatis.extend.utils;

import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang.StringUtils;

@SuppressWarnings("rawtypes")
public class MybatisDAOHelper {
	public static boolean getBooleanValue(Map map, String key) {
		boolean val = false;
		if(map != null) {
			Object value = map.get(key);
			if(value != null) {
				String v = StringUtils.trimToEmpty(value.toString());
				if("1".equals(v)){
					return true;
				}else if("0".equals(v)){
					return false;
				}else{
					val = Boolean.parseBoolean(v);
				}
			}
		}
		return val;
	}
	
	public static String getStringValue(Map map, String key) {
		if(map == null) return null;
		
		Object value = map.get(key);
		if (value == null) {
			return null;
		}
		if (value instanceof String) {
			return StringUtils.trimToEmpty((String) value);
		} else if (value instanceof Clob) {
			return clob2Str((Clob) value);
		} else {
			return value.toString();
		}
	}

	public static String clob2Str(Clob clob) {
		try {
			return (clob != null ? clob.getSubString(1, (int) clob.length())
					: null);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Long getLongValue(Map map, String key) {
		if(map == null) return 0l;
		
		Object value = map.get(key);
		if (value == null) {
			return null;
		}
		if (value instanceof BigDecimal) {
			return ((BigDecimal) value).longValue();
		}else if(value instanceof Long){
			return (Long)value;
		}else if(value instanceof Short){
			return ((Short)value).longValue(); 
		}else if(value instanceof Integer){
			return ((Integer)value).longValue();
		}else if(value instanceof String){
			if(StringUtils.isBlank(value+"")) {
				return 0l;
			}else {
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
	
	public static int getIntValue(Map map, String key) {
		if(map == null) return 0;
		
		Object value = map.get(key);
		if (value == null) {
			return 0;
		}
		if (value instanceof BigDecimal) {
			return ((BigDecimal) value).intValue();
		}else if(value instanceof Long){
			return ((Long)value).intValue();
		}else if(value instanceof Short){
			return ((Short)value).intValue(); 
		}else if(value instanceof Integer){
			return ((Integer)value).intValue();
		}else if(value instanceof Double){
			return ((Double)value).intValue();
		}else if(value instanceof String){
			if(StringUtils.isBlank(value+"")) {
				return 0;
			}else {
				try {
					return Integer.parseInt(value+"");
				}catch(Exception e) {
					throw new RuntimeException("无法将key【"+key+"】,value【"+value+"】转换为Integer类型！");
				}
			}
		} else {
			throw new RuntimeException("无法将key【"+key+"】,value【"+value+"】转换为Integer类型！");
		}
	}
	
	public static Integer getIntegerValue(Map map, String key) {
		if(map == null) return null;
		
		Object value = map.get(key);
		if(value == null) return null;
		if(value instanceof String) {
			if(StringUtils.isBlank(value+"")) return null;
		}
		
		return MybatisDAOHelper.getIntValue(map, key);
	}
	
	public static Date getDateValue(Map map, String key){
		if(map == null) return null;
		Object value = map.get(key);
		if(value == null) return null;
		
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
	
	public static float getFloatValue(Map map, String key) {
		if(map == null) return 0f;
		
		Object value = map.get(key);
		if (value == null) {
			return 0f;
		}
		if (value instanceof Float) {
			return (Float) value;
		}else if (value instanceof BigDecimal) {
			return ((BigDecimal) value).floatValue();
		}else if(value instanceof Long){
			return ((Long)value).floatValue();
		}else if(value instanceof Short){
			return ((Short)value).floatValue(); 
		}else if(value instanceof Integer){
			return ((Integer)value).floatValue();
		}else if(value instanceof Double){
			return ((Double)value).floatValue();
		}else if(value instanceof String){
			if(StringUtils.isBlank(value+"")) {
				return 0f;
			}else {
				try {
					return Float.parseFloat(value+"");
				}catch(Exception e) {
					throw new RuntimeException("无法将key【"+key+"】,value【"+value+"】转换为Float类型！");
				}
			}
		} else {
			throw new RuntimeException("无法将key【"+key+"】,value【"+value+"】转换为Float类型！");
		}
	}
	
	public static Double getDoubleValue(Map map, String key) {
		if(map == null) return null;
		
		Object value = map.get(key);
		if (value == null) {
			return null;
		}
		if(value instanceof Double) {
			return (Double) value;
		}else if (value instanceof Float) {
			return ((Float) value).doubleValue();
		}else if (value instanceof BigDecimal) {
			return ((BigDecimal) value).doubleValue();
		}else if(value instanceof Long){
			return ((Long)value).doubleValue();
		}else if(value instanceof Short){
			return ((Short)value).doubleValue(); 
		}else if(value instanceof Integer){
			return ((Integer)value).doubleValue();
		}else if(value instanceof String){
			if(StringUtils.isBlank(value+"")) {
				return null;
			}else {
				try {
					return Double.parseDouble(value+"");
				}catch(Exception e) {
					throw new RuntimeException("无法将key【"+key+"】,value【"+value+"】转换为Double类型！");
				}
			}
		} else {
			throw new RuntimeException("无法将key【"+key+"】,value【"+value+"】转换为Double类型！");
		}
	}
	
	public static List<String> skipEmptyForList(List<String> list){
		List<String> alist = new ArrayList<String>();
		if(!list.isEmpty()){
			for(String str : list){
				if(StringUtils.isNotBlank(str)){
					alist.add(str);
				}
			}
		}
		return alist;
	}
	public static List<Map<String, List<String>>> getIdGroup(List<Map<String, Object>> list, String idKey) {
		int groupIdx = 1;
		List<Map<String, List<String>>> idGroup = new ArrayList<Map<String, List<String>>>();
		for(Map<String, Object> obj : list) {
			String portid = MybatisDAOHelper.getStringValue(obj, idKey);
			List<String> ptpCuidList = null;
			if(idGroup.size() < groupIdx) {
				Map<String, List<String>> map = new HashMap<String, List<String>>();
				ptpCuidList = new ArrayList<String>();
				map.put("list", ptpCuidList);
				idGroup.add(map);
			}else {
				Map<String, List<String>> map = idGroup.get(groupIdx - 1);
				ptpCuidList = map.get("list");
			}
			ptpCuidList.add(portid);
			if(ptpCuidList.size() >= 1000) {
				groupIdx++;
			}
		}
		return idGroup;
	}
	
	public static Map<String, Map<String, Object>> parseList2Map(List<Map<String, Object>> list, String idKey) {
		Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
		for(Map<String, Object> obj : list) {
			String key = MybatisDAOHelper.getStringValue(obj, idKey);
			if(map.get(key) == null) {
				map.put(key, obj);
			}
		}
		return map;
	}
	
	public static Map<String, List<Map<String, Object>>> parseList2GroupMap(List<Map<String, Object>> list, String groupKey) {
		Map<String, List<Map<String, Object>>> groupMap = new HashMap<String, List<Map<String, Object>>>();
		for(Map<String, Object> obj : list) {
			String group = MybatisDAOHelper.getStringValue(obj, groupKey);
			List<Map<String, Object>> subList = groupMap.get(group);
			if(subList == null) {
				subList = new ArrayList<Map<String, Object>>();
				groupMap.put(group, subList);
			}
			subList.add(obj);
		}
		return groupMap;
	}
	
	/**
	 * <isNotEmpty property="cuidGroup">
			<iterate prepend="AND" property="cuidGroup" conjunction="OR" open="(" close=")">
				<iterate prepend="T.CUID IN" property="cuidGroup[].list" conjunction="," open="(" close=")">
					#cuidGroup[].list[]#
				</iterate>
			</iterate>
		</isNotEmpty>
	 * @param list
	 * @param groupNum
	 * @return
	 */
	public static List<Map<String, List<String>>> pareseGroupList(List<String> list,int groupNum){
		if(groupNum==0)throw new RuntimeException("分组数必须大于0，建议取值600至1200");
		List<Map<String, List<String>>> cuidGroup = new ArrayList<Map<String, List<String>>>();
		for(int cuidIdx = 0; cuidIdx < list.size(); cuidIdx++){
			String cuid = list.get(cuidIdx);
			if(cuidIdx == 0 || (cuidIdx+1) % groupNum == 0) {
				List<String> idList = new ArrayList<String>();
				Map<String, List<String>> map = new HashMap<String, List<String>>();
				map.put("list", idList);
				cuidGroup.add(map);
			}
			Map<String, List<String>> map = cuidGroup.get(cuidIdx/groupNum);
			List<String> idList = map.get("list");
			idList.add(cuid);
		}
		return cuidGroup;
	}
	
}
