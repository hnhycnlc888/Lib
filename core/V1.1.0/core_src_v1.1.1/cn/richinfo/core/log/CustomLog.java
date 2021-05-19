package cn.richinfo.core.log;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.springframework.beans.factory.InitializingBean;

import yzkf.config.ConfigFactory;
import yzkf.config.ProjectConfig;

/**
 * 遵循YZKF的架构标准，对项目日志的输出进行适当扩展修改
 * 
 */
public class CustomLog implements InitializingBean {
	
	private String maxFileSize = "100MB";
	private String projectLayoutPattern = "[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%p] [%-c:%L] [%t] [%X{userInfo}] %m%n";

	@Override
	public void afterPropertiesSet() throws Exception {
		String logFilePath = ConfigFactory.getInstance().getLogFilePath();
		if(StringUtils.isEmpty(logFilePath)){
			throw new RuntimeException("缺少日志文件的保存路径，请检查yzkf.xml yzkf/log4jFile/path的配置");
		}
		
		//获取以项目名称命名的LOG
		Logger projectLog = Logger.getLogger(ProjectConfig.getInstance().getCode());
		//设置该LOG的级别为DEBUG
		projectLog.setLevel(Level.DEBUG);
		//获取项目日志的输出源appender
		Appender projectAppender = projectLog.getAppender(ProjectConfig.getInstance().getCode());
		
		//获取root日志
		Logger rootLogger = LogManager.getRootLogger();
		Appender consoleAppender = rootLogger.getAppender("console");
		
		//重修修改控制台输出的格式
		if(consoleAppender != null){
			Layout layout = consoleAppender.getLayout();
			if(layout instanceof PatternLayout){
				((PatternLayout) layout).setConversionPattern(projectLayoutPattern);
			}
		}
		
		if(projectAppender == null){
			//由于yzkf生成的项目日志的输出源appender是DailyRollingFileAppender，
			//该类是配置每天卷动项目日志，目前项目日志可能会比较大，不适合使用该类，
			//故改用RollingFileAppender，超过指定大小自动卷动日志
			DailyFixSizeRollingFileAppender appender = new DailyFixSizeRollingFileAppender();
			//设置appender名称为项目编号
			appender.setName(ProjectConfig.getInstance().getCode());
			//设置文件内容追加
			appender.setAppend(true);
			//设置日志文件路径
			appender.setFile(logFilePath);
			//设置编码
			appender.setEncoding("utf-8");
			//设置日志最低输出的级别
			appender.setThreshold(Level.DEBUG);
			//设置日志文件最大文件大小，用于卷动日志
			appender.setMaxFileSize(maxFileSize);
			
			//设置日志输出格式，该格式可以修改spring配置文件的方式注入
			PatternLayout layout = new PatternLayout(projectLayoutPattern);
			appender.setLayout(layout);
			
			//激活当前日志输出源的配置
			appender.activateOptions();
			
			//移除项目日志的输出源
			projectLog.removeAllAppenders();
			//添加配置好的输出源到当前项目日志
			projectLog.addAppender(appender);
			
			//控制rootLogger也将日志输出到项目日志文件里面
			rootLogger.addAppender(appender);
		}
	}

	public void setMaxFileSize(String maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public void setProjectLayoutPattern(String projectLayoutPattern) {
		this.projectLayoutPattern = projectLayoutPattern;
	}

}
