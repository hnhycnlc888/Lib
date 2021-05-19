package cn.richinfo.core.job;

import java.util.Collection;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cn.richinfo.core.spring.SpringContext;
import cn.richinfo.core.utils.id.CUIDHexGenerator;
import cn.richinfo.core.utils.lang.CollectionHelper;

public abstract class AbstractJob implements Job {
	
	private final String JOB_STATE_RUN = "RUN";
	private final String JOB_STATE_END = "END";
	private final String JOB_STATE_ERROR = "ERROR";
	
	protected abstract void run(JobExecutionContext jobCtx);
	
	protected void beginJobLog(String id, String code, JobExecutionContext jobCtx){
		Collection<IJobLog> jobLogs = getJobLogBean();
		if(CollectionHelper.isNotEmpty(jobLogs)){
			for(IJobLog log : jobLogs){
				log.logJob(id, JOB_STATE_RUN, jobCtx);
			}
		}
	}
	
	protected void endJobLog(String id, String state, JobExecutionContext jobCtx){
		Collection<IJobLog> jobLogs = getJobLogBean();
		if(CollectionHelper.isNotEmpty(jobLogs)){
			for(IJobLog log : jobLogs){
				log.logJob(id, state, jobCtx);
			}
		}
	}
	
	protected void exceptionJobLog(String id, String state, Exception e, JobExecutionContext jobCtx){
		Collection<IJobLog> jobLogs = getJobLogBean();
		if(CollectionHelper.isNotEmpty(jobLogs)){
			for(IJobLog log : jobLogs){
				log.logJobException(id, state, jobCtx, e);
			}
		}
	}

	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		String code = jobCtx.getJobDetail().getKey().getName();
		String id = CUIDHexGenerator.getInstance().generate();
		this.beginJobLog(id, code, jobCtx);
		try {
			this.run(jobCtx);
			this.endJobLog(id, JOB_STATE_END, jobCtx);
		} catch(Exception e){
			this.exceptionJobLog(id, JOB_STATE_ERROR, e, jobCtx);
		}
	}
	
	public Collection<IJobLog> getJobLogBean(){
		return SpringContext.getBeanByType(IJobLog.class);
	}

}
