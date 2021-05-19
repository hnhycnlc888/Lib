package cn.richinfo.core.mybatis.extend.vo;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import oracle.sql.TIMESTAMP;

@MappedTypes(TIMESTAMP.class)
public class OracleTimeStampTypeHandler extends BaseTypeHandler<Date> {

	@Override
	public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
		Timestamp timestamp = rs.getTimestamp(columnName);
		if(timestamp == null){
			return null;
		} else {
			return new Date(timestamp.getTime());
		}
	}

	@Override
	public Date getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		Timestamp timestamp = rs.getTimestamp(columnIndex);
		if(timestamp == null){
			return null;
		} else {
			return new Date(timestamp.getTime());
		}
	}

	@Override
	public Date getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		Timestamp timestamp = cs.getTimestamp(columnIndex);
		if(timestamp == null){
			return null;
		} else {
			return new Date(timestamp.getTime());
		}
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType) throws SQLException {
		ps.setTimestamp(i, new Timestamp(parameter.getTime()));
	}
	
}
