package cn.richinfo.core.job;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.InitializingBean;

import yzkf.config.ProjectConfig;

public class JobDynLogger implements InitializingBean, IJobLog {
	private Logger jobLogger;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		String projectCode = ProjectConfig.getInstance().getCode();
		Logger projectLogger = LogManager.getLogger(projectCode);
		Appender appender = projectLogger.getAppender(projectCode);
		if(appender != null && appender instanceof FileAppender){
			FileAppender fileAppender = (FileAppender) appender;
			String appenderFile = fileAppender.getFile();
			if(StringUtils.isNotEmpty(appenderFile)){
				Appender newAppender = copyAppender(fileAppender);
				jobLogger = Logger.getLogger("quartz.job");
				jobLogger.setLevel(Level.DEBUG);
				jobLogger.addAppender(newAppender);
			}
		}
	}
	
	private Appender copyAppender(FileAppender fileAppender){
		FileAppender newAppender = null;
		if(StringUtils.isEmpty(fileAppender.getFile()))
			return newAppender;
		String logFileName = fileAppender.getFile();
		File file = new File(logFileName);
		String jobLogFileName = file.getParent() + File.separator + "jobLog.txt";
		
		if(fileAppender instanceof DailyRollingFileAppender){
			DailyRollingFileAppender dailyRollingAppender = (DailyRollingFileAppender) fileAppender;
			try {
				DailyRollingFileAppender appender = new DailyRollingFileAppender(fileAppender.getLayout(), jobLogFileName, dailyRollingAppender.getDatePattern());
				appender.setDatePattern(dailyRollingAppender.getDatePattern());
				newAppender = appender;
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		} else if(fileAppender instanceof RollingFileAppender){
			try {
				RollingFileAppender appender = new RollingFileAppender(fileAppender.getLayout(), jobLogFileName);
				newAppender = appender;
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		} else {
			throw new RuntimeException("未定义appender类型");
		}
		newAppender.setThreshold(fileAppender.getThreshold());
		newAppender.setAppend(true);
		return newAppender;
	}
	
	public void logJob(String id, String state, JobExecutionContext jobCtx){
		jobLogger.info(jobCtx.getJobDetail().getJobClass().getName() + "-id:" + id +"-state:" + state);
	}

	@Override
	public void logJobException(String id, String state, JobExecutionContext jobCtx, Exception e) {
		jobLogger.error(jobCtx.getJobDetail().getJobClass().getName() + "-id:" + id +"-state:" + state, e);
	}

}
