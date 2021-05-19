package cn.richinfo.core.mybatis.extend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.util.Assert;

import cn.richinfo.core.mybatis.extend.vo.Record;

public class MybaitsDao extends SqlSessionTemplate {
	
	public MybaitsDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	protected final static String BASE_NAME_SPACE = "BaseMbFramework";
	protected final static String TABLE_P = "table_name";
	protected final static String COLUMNS_P = "table_columns";
	protected final static String PRIMARY_KEY_COLUMN_NAME_P = "primary_key_column_name";
	protected final static String PRIMARY_KEY_COLUMN_VALUE_P = "primary_key_column_value";
	protected final static String PRIMARY_KEY_COLUMN_VALUE_LIST_P = "primary_key_column_valuelist";
	
	/**
	 * 单条insert数据
	 * @param insertRecord
	 * @return 返回影响的行数
	 */
	public int insertDynamicTable(Record insertRecord){
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put(TABLE_P, insertRecord.getTableName());
		pm.put(COLUMNS_P, insertRecord.getRecordValues());
		return this.insert(BASE_NAME_SPACE + ".insertDynamicTable", pm);
	}
	
	/**
	 * 单条更新数据
	 * @param updateRecord
	 * @return 返回影响的行数
	 */
	public int updateDynamicTable(Record updateRecord){
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put(TABLE_P, updateRecord.getTableName());
		pm.put(COLUMNS_P, updateRecord.getRecordValues());
		pm.put(PRIMARY_KEY_COLUMN_NAME_P, updateRecord.getPrimaryKeyColumnName());
		pm.put(PRIMARY_KEY_COLUMN_VALUE_P, updateRecord.getPrimaryKeyValue());
		return this.update(BASE_NAME_SPACE + ".updateDynamicTable", pm);
	}
	
	/**
	 * 执行传入的sql查询语句，并返回结果集
	 * @param sql 查询的sql
	 * @param param 参数
	 * @return 返回数据结果
	 */
	public List<Map<String, Object>> querySqlList(String sql, Map<String, Object> param){
		Assert.hasText(sql, "sql参数不允许空");
		Map<String, Object> pm = new HashMap<String, Object>(1);
		if(param != null && !param.isEmpty()){
			pm.putAll(param);
		}
		pm.put("sql", sql);
		return this.selectList(BASE_NAME_SPACE + ".executeSql", pm);
	}
	
	/**
	 * 执行传入的sql查询语句，并返回结果
	 * @param sql
	 * @param param
	 * @return 返回数据结果
	 */
	public Map<String, Object> querySqlOne(String sql, Map<String, Object> param){
		Map<String, Object> pm = new HashMap<String, Object>(1);
		if(param != null && !param.isEmpty()){
			pm.putAll(param);
		}
		pm.put("sql", sql);
		return this.selectOne(BASE_NAME_SPACE + ".executeSql", pm);
	}
	
	public int deleteSql(String sql, Map<String, Object> param){
		Map<String, Object> pm = new HashMap<String, Object>(1);
		if(param != null && !param.isEmpty()){
			pm.putAll(param);
		}
		pm.put("sql", sql);
		return this.delete(BASE_NAME_SPACE + ".deleteSql", pm);
	}
	
	/**
	 * 根据传入的表名、主键名称、主键值集合动态删除数据
	 * @param tableName 表名
	 * @param primaryKey 主键名称
	 * @param primaryValueList 主键值集合
	 * @return 返回影响的行数
	 */
	public int deleteDynTable(String tableName, String primaryKey, List<?> primaryValueList){
		Assert.hasText(tableName, "table name  must not be empty");
		Assert.hasText(primaryKey, "primary key must not be empty");
		if(primaryValueList == null || primaryValueList.isEmpty())
			return 0;
		
		Map<String, Object> pm = new HashMap<String, Object>();
		pm.put(TABLE_P, tableName);
		pm.put(PRIMARY_KEY_COLUMN_NAME_P, primaryKey);
		pm.put(PRIMARY_KEY_COLUMN_VALUE_LIST_P, primaryValueList);
		return this.delete(BASE_NAME_SPACE + ".deleteDynTable", pm);
	}
	
	/**
	 * 批量数据INSERT
	 * @param recList
	 * @return 返回影响的行数
	 */
	public int insertDynTableBatch(List<Record> recList){
		List<Map<String, Object>> pmList = new ArrayList<Map<String,Object>>(recList.size());
		for(Record insertRecord : recList){
			Map<String, Object> pm = new HashMap<String, Object>();
			pm.put(TABLE_P, insertRecord.getTableName());
			pm.put(COLUMNS_P, insertRecord.getRecordValues());
			pmList.add(pm);
		}
		return this.batExecute(BASE_NAME_SPACE + ".insertDynamicTable", pmList);
	}
	
	/**
	 * 批量数据UPDATE
	 * @param updateRecList
	 * @return 返回影响的行数
	 */
	public int updateDynTableBatch(List<Record> updateRecList){
		List<Map<String, Object>> pmList = new ArrayList<Map<String,Object>>(updateRecList.size());
		for(Record updateRecord : updateRecList){
			Map<String, Object> pm = new HashMap<String, Object>();
			pm.put(TABLE_P, updateRecord.getTableName());
			pm.put(COLUMNS_P, updateRecord.getRecordValues());
			pm.put(PRIMARY_KEY_COLUMN_NAME_P, updateRecord.getPrimaryKeyColumnName());
			pm.put(PRIMARY_KEY_COLUMN_VALUE_P, updateRecord.getPrimaryKeyValue());
			pmList.add(pm);
		}
		return this.batExecute(BASE_NAME_SPACE + ".updateDynamicTable", pmList);
	}
	
	private int batExecute(String statement, List<?> pmList){
		MappedStatement ms = this.getConfiguration().getMappedStatement(statement);
		SqlCommandType commandType = ms.getSqlCommandType();
		if(!commandType.equals(SqlCommandType.DELETE) && !commandType.equals(SqlCommandType.INSERT) && !commandType.equals(SqlCommandType.UPDATE)){
			throw new RuntimeException("批量执行异常，仅支持delete、insert、update");
		}
		Map<String, List<BoundSql>> sql2BoundSqlList = new LinkedHashMap<String, List<BoundSql>>();
		Map<BoundSql, ParameterHandler> boundSql2ParamHandlerMap = new HashMap<BoundSql, ParameterHandler>();
		for(Object pmObj : pmList){
			BoundSql boundSql = ms.getBoundSql(pmObj);
			String sql = boundSql.getSql();
			List<BoundSql> boundSqlList = sql2BoundSqlList.get(sql);
			if(boundSqlList == null){
				boundSqlList = new ArrayList<BoundSql>();
				sql2BoundSqlList.put(sql, boundSqlList);
			}
			boundSqlList.add(boundSql);
			
			ParameterHandler parameterHandler = this.getConfiguration().newParameterHandler(ms, pmObj, boundSql);
			boundSql2ParamHandlerMap.put(boundSql, parameterHandler);
		}
		return doBatchExecute(sql2BoundSqlList, boundSql2ParamHandlerMap);
	}
	
	private int doBatchExecute(Map<String, List<BoundSql>> sql2BoundSqlList, Map<BoundSql, ParameterHandler> boundSql2ParamHandlerMap){
		Connection conn = this.getConnection();
		PreparedStatement ps = null;
		boolean isFinishExecute = false;
		int totalAffectRows = 0;
		
		for(Map.Entry<String, List<BoundSql>> entry : sql2BoundSqlList.entrySet()){
			List<BoundSql> boundSqlList = entry.getValue();
			try {
				ps = conn.prepareStatement(entry.getKey());
				int executeCount = 1;
				
				for(int idx = 0; idx < boundSqlList.size(); idx++){
					BoundSql boundSql = boundSqlList.get(idx);
					ParameterHandler paramHandler = boundSql2ParamHandlerMap.get(boundSql);
					paramHandler.setParameters(ps);
					ps.addBatch();
					executeCount++;
					isFinishExecute = false;
					
					if(executeCount % 100 == 0){
						int[] affects = ps.executeBatch();
						validateBatch(affects);
						totalAffectRows += ps.getUpdateCount();
						ps.clearBatch();
						isFinishExecute = true;
					}
				}
				
				if(!isFinishExecute){
					int[] affects = ps.executeBatch();
					validateBatch(affects);
					totalAffectRows += ps.getUpdateCount();
					isFinishExecute = true;
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return totalAffectRows;
	}
	
	private int validateBatch(int[] affects){
		int totalAffectRows = 0;
		for(int idx = 0; idx < affects.length; idx++){
			if(affects[idx] == PreparedStatement.EXECUTE_FAILED){
				throw new RuntimeException("批量执行异常，index=" + idx);
			}
		}
		return totalAffectRows;
	}

}
