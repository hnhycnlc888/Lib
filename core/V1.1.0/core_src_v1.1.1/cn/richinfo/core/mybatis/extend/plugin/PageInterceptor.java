package cn.richinfo.core.mybatis.extend.plugin;

import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import cn.richinfo.core.mybatis.extend.dialect.Dialect;

@Intercepts({
	@Signature(type=Executor.class, method="query", args={MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class PageInterceptor implements Interceptor, InitializingBean {
	
	private Dialect dialect;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object[] callArgs = invocation.getArgs();
		MappedStatement mappedStatement = (MappedStatement) callArgs[0];
		Object parameter = callArgs[1];
		RowBounds rowBounds = (RowBounds) callArgs[2];
		
		if(rowBounds != null && this.dialect.supportsLimit() 
				&& (!RowBounds.DEFAULT.equals(rowBounds) || 
						RowBounds.NO_ROW_OFFSET != rowBounds.getOffset() || 
						RowBounds.NO_ROW_LIMIT != rowBounds.getLimit())){
			
			BoundSql boundSql = mappedStatement.getBoundSql(parameter);
			String originSql = boundSql.getSql().trim();
			
			String pagedSql = null;
			if(this.dialect.supportsLimitOffset()){
				pagedSql = this.dialect.getLimitString(originSql, rowBounds.getOffset(), rowBounds.getLimit());
			} else {
				pagedSql = this.dialect.getLimitString(originSql, 0, rowBounds.getLimit());
			}
			BoundSql newBoundSql = copyBoundSql(mappedStatement, boundSql, pagedSql);
			MappedStatement newMs = copyMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));
			callArgs[0] = newMs;
			callArgs[2] = RowBounds.DEFAULT;
		}
		return invocation.proceed();
	}
	
	private BoundSql copyBoundSql(MappedStatement ms, BoundSql boundSql, String sql){
		BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, boundSql.getParameterMappings(), boundSql.getParameterObject());
		for (ParameterMapping mapping : boundSql.getParameterMappings()) {  
			String prop = mapping.getProperty();  
			if (boundSql.hasAdditionalParameter(prop)) {  
				newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));  
			}
		}
		return newBoundSql;
	}
	
	private MappedStatement copyMappedStatement(MappedStatement ms, SqlSource newSqlSource){
		Builder builder = new Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
		builder.resource(ms.getResource());  
		builder.fetchSize(ms.getFetchSize());  
		builder.statementType(ms.getStatementType());  
		builder.keyGenerator(ms.getKeyGenerator());  
		builder.keyProperty(this.joinString(ms.getKeyProperties()));  
		builder.timeout(ms.getTimeout());  
		builder.parameterMap(ms.getParameterMap());  
		builder.resultMaps(ms.getResultMaps());  
		builder.resultSetType(ms.getResultSetType());  
		builder.cache(ms.getCache());  
		builder.flushCacheRequired(ms.isFlushCacheRequired());  
		builder.useCache(ms.isUseCache());
		return builder.build();
	}
	
	private String joinString(String[] strArray){
		if(strArray == null)
			return null;
		StringBuilder sb = new StringBuilder();
		String comma = ",";
		for(int i = 0; i < strArray.length; i++){
			if(i > 0){
				sb.append(comma);
			}
			sb.append(strArray[i]);
		}
		return sb.toString();
	}

	@Override
	public Object plugin(Object target) {
		if(target instanceof Executor){
			return Plugin.wrap(target, this);
		} else {
			return target;
		}
	}

	@Override
	public void setProperties(Properties properties) {
		
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(dialect, "开启了mybatis分页代理，但是缺少方言dialect参数！");
	}
	
	private class BoundSqlSqlSource implements SqlSource {
		private BoundSql boundSql;
		
		public BoundSqlSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}

		@Override
		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}
	}
}
