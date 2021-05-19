package cn.richinfo.core.job;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

public class JobConfig {
	private String jobKey;
	private String jobClass;
	private String cron;
	private String remark;
	private boolean isRun = false;
	private String startDate;
	private String endDate;
	private Map<String, Object> jobDataMap = new HashMap<String, Object>();
	
	public String getJobKey() {
		return jobKey;
	}
	public void setJobKey(String jobKey) {
		this.jobKey = jobKey;
	}
	public String getJobClass() {
		return jobClass;
	}
	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public boolean isRun() {
		return isRun;
	}
	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public Map<String, Object> getJobDataMap() {
		return jobDataMap;
	}
	public void setJobData(String key, Object value){
		if(StringUtils.isNotEmpty(key))
			this.jobDataMap.put(key, value);
	}
	
	public void validate(){
		Assert.hasText(jobKey, "缺少job的key,该key为job的ID");
		Assert.hasText(jobClass, "缺少job的具体执行类");
		Assert.hasText(cron, "缺少定时任务表达式");
	}
	
}
