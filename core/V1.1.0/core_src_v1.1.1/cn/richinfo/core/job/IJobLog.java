package cn.richinfo.core.job;

import org.quartz.JobExecutionContext;

public interface IJobLog {
	
	public void logJob(String id, String state, JobExecutionContext jobCtx);
	
	public void logJobException(String id, String state, JobExecutionContext jobCtx, Exception e);

}
