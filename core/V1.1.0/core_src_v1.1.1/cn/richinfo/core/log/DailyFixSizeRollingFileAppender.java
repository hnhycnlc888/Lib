package cn.richinfo.core.log;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

public class DailyFixSizeRollingFileAppender extends RollingFileAppender {
	
	private long newRollbackTimestamp = 0;
	private final static SimpleDateFormat timeFormator = new SimpleDateFormat("HHmmss");
	private final static String backFilePrefix = "log_";
	private final static String backFileSufix = ".txt";
	
	public DailyFixSizeRollingFileAppender(){
		this.setNextRollbackTimestamp(new Date());
	}
	
	private void setNextRollbackTimestamp(Date date){
		Date nextDate = DateUtils.addDays(date, 1);
		nextDate = DateUtils.truncate(nextDate, Calendar.DAY_OF_MONTH);
		this.newRollbackTimestamp = nextDate.getTime();
	}
	
	@Override
	public void activateOptions() {
		super.activateOptions();
		this.init();
	}

	private void init(){
		if(StringUtils.isEmpty(this.fileName)){
			throw new RuntimeException("文件日志缺少保存路径");
		}
		File originLogFile = new File(this.fileName);
		if(!originLogFile.exists()){
			return;
		}
		Date currentDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		Date fileLastModifyDate = DateUtils.truncate(new Date(originLogFile.lastModified()), Calendar.DAY_OF_MONTH);
		if(currentDate.compareTo(fileLastModifyDate) > 0){
			this.rollOver();
		}
	}
	
	@Override
	public void rollOver() {
		if (qw != null) {
			long size = ((CountingQuietWriter) qw).getCount();
			LogLog.debug("rolling over count=" + size);
		}
		
		File logFile = new File(fileName);
		String parentFilePath = logFile.getParent();
		
		Calendar cal = Calendar.getInstance();
		String backLogFileParentPath = parentFilePath + "/" + cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DAY_OF_MONTH);
		
		File backLogFileParentFile = new File(backLogFileParentPath);
		if(!backLogFileParentFile.exists()){
			backLogFileParentFile.mkdirs();
		}
		
		String backLogFileName = backFilePrefix + timeFormator.format(cal.getTime()) + backFileSufix;
		String backLogFilePath = backLogFileParentPath + File.separator + backLogFileName;
		
		boolean isSuccess = false;
		
		try {
			super.closeFile();
			FileUtils.copyFile(logFile, new File(backLogFilePath), true);
			isSuccess = true;
		} catch(Exception e) {
			if (e instanceof InterruptedIOException) {
				Thread.currentThread().interrupt();
			}
			LogLog.error("backup file ("+fileName+", false) call failed.", e);
		} finally {
			if(isSuccess){
				logFile.delete();
			}
			try {
				this.setFile(fileName, false, bufferedIO, bufferSize);
			} catch (IOException e) {
				if (e instanceof InterruptedIOException) {
					Thread.currentThread().interrupt();
				}
				LogLog.error("setFile("+fileName+", false) call failed.", e);
			}
		}
	}
	
	@Override
	protected void subAppend(LoggingEvent event) {
		if(System.currentTimeMillis() > this.newRollbackTimestamp){
			this.rollOver();
			this.setNextRollbackTimestamp(new Date());
		}
		super.subAppend(event);
	}
	
}
