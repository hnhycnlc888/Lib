package cn.richinfo.core.datasource;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;

import cn.richinfo.core.utils.lang.CollectionHelper;
import yzkf.config.ConfigFactory;

public class DataSourceFactoryBean implements FactoryBean<DruidDataSource>, InitializingBean {

	private String dbType = "default";
	private DruidDataSource datasource;
	private List<Filter> proxyFilters;
	private String filters;
	
	@Override
	public DruidDataSource getObject() throws Exception {
		return this.datasource;
	}

	@Override
	public Class<DruidDataSource> getObjectType() {
		return DruidDataSource.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
	public void setDbType(String dbType) {
		if(StringUtils.isNotEmpty(dbType)){
			this.dbType = dbType;
		}
	}

	public void setProxyFilters(List<Filter> proxyFilters) {
		this.proxyFilters = proxyFilters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		String dbConfigPath = ConfigFactory.getInstance().getDbConfigPath();
		if(StringUtils.isEmpty(dbConfigPath)){
			throw new RuntimeException("缺少数据库配置文件的路径，请检查yzkf.xml yzkf/dbconfig/path");
		}
		File dbConfigFile = new File(dbConfigPath);
		if(!dbConfigFile.exists()){
			throw new RuntimeException("数据库配置文件不存在，路径：" + dbConfigPath);
		}
		
		SAXReader xmlReader = new SAXReader();
		Document doc = xmlReader.read(dbConfigFile);
		Element rootEl = doc.getRootElement();
		Element dbEl = (Element) rootEl.selectSingleNode("db[@type=\"" + dbType + "\"]");
		if(dbEl == null){
			throw new RuntimeException("db.xml 不存在type=" + dbType + "的配置");
		}
		
		this.datasource = new DruidDataSource();
		this.datasource.setDriverClassName("net.sf.log4jdbc.DriverSpy");
		
		String url = dbEl.elementTextTrim("url");
		String userName = dbEl.elementTextTrim("username");
		String password = dbEl.elementTextTrim("password");
		String maxActiveStr = dbEl.elementTextTrim("maxActive");
		String maxIdleStr = dbEl.elementTextTrim("maxIdle");
		String minIdleStr = dbEl.elementTextTrim("minIdle");
		String initialSizeStr = dbEl.elementTextTrim("initialSize");
		String maxWaitStr = dbEl.elementTextTrim("maxWait");
		String testOnBorrowStr = dbEl.elementTextTrim("testOnBorrow");
		String testOnReturnStr = dbEl.elementTextTrim("testOnReturn");
		String testWhileIdleStr = dbEl.elementTextTrim("testWhileIdle");
		String validationQuery = dbEl.elementTextTrim("validationQuery");
		String timeBetweenEvictionRunsMillisStr = dbEl.elementTextTrim("timeBetweenEvictionRunsMillis");
		String minEvictableIdleTimeMillisStr = dbEl.elementTextTrim("minEvictableIdleTimeMillis");
		String removeAbandonedStr = dbEl.elementTextTrim("removeAbandoned");
		String removeAbandonedTimeoutStr = dbEl.elementTextTrim("removeAbandonedTimeout");
		String logAbandonedStr = dbEl.elementTextTrim("logAbandoned");
		
		if(!url.contains("jdbc:log4jdbc:")){
			url = url.replace("jdbc:", "jdbc:log4jdbc:");
		}
		this.datasource.setUrl(url);
		this.datasource.setUsername(userName);
		this.datasource.setPassword(password);
		if(StringUtils.isNotEmpty(maxActiveStr)){
			this.datasource.setMaxActive(Integer.parseInt(maxActiveStr));
		}
		if(StringUtils.isNotEmpty(maxIdleStr)){
//			this.datasource.setMaxIdle(maxIdle);
		}
		if(StringUtils.isNotEmpty(minIdleStr)){
			this.datasource.setMinIdle(Integer.parseInt(minIdleStr));
		}
		if(StringUtils.isNotEmpty(initialSizeStr)){
			this.datasource.setInitialSize(Integer.parseInt(initialSizeStr));
		}
		if(StringUtils.isNotEmpty(maxWaitStr)){
			this.datasource.setMaxWait(Long.parseLong(maxWaitStr));
		}
		if(StringUtils.isNotEmpty(testOnBorrowStr)){
			this.datasource.setTestOnBorrow(BooleanUtils.toBoolean(testOnBorrowStr));
		}
		if(StringUtils.isNotEmpty(testOnReturnStr)){
			this.datasource.setTestOnReturn(BooleanUtils.toBoolean(testOnReturnStr));
		}
		if(StringUtils.isNotEmpty(testWhileIdleStr)){
			this.datasource.setTestWhileIdle(BooleanUtils.toBoolean(testWhileIdleStr));
		}
		if(StringUtils.isNotEmpty(validationQuery)){
			this.datasource.setValidationQuery(validationQuery);
		}
		if(StringUtils.isNotEmpty(timeBetweenEvictionRunsMillisStr)){
			this.datasource.setTimeBetweenEvictionRunsMillis(Long.parseLong(timeBetweenEvictionRunsMillisStr));
		}
		if(StringUtils.isNotEmpty(minEvictableIdleTimeMillisStr)){
			this.datasource.setMinEvictableIdleTimeMillis(Long.parseLong(minEvictableIdleTimeMillisStr));
		}
		if(StringUtils.isNotEmpty(removeAbandonedStr)){
			this.datasource.setRemoveAbandoned(BooleanUtils.toBoolean(removeAbandonedStr));
		}
		if(StringUtils.isNotEmpty(removeAbandonedTimeoutStr)){
			this.datasource.setRemoveAbandonedTimeout(Integer.parseInt(removeAbandonedTimeoutStr));
		}
		if(StringUtils.isNotEmpty(logAbandonedStr)){
			this.datasource.setLogAbandoned(BooleanUtils.toBoolean(logAbandonedStr));
		}
		
		if(CollectionHelper.isNotEmpty(proxyFilters)){
			this.datasource.setProxyFilters(proxyFilters);
		}
		
		if(StringUtils.isNotEmpty(filters)){
			this.datasource.setFilters(filters);
		}
	}
	
	public void close(){
		this.datasource.close();
	}

}
