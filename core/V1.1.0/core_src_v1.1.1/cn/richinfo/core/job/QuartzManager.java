package cn.richinfo.core.job;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import cn.richinfo.core.utils.lang.CollectionHelper;

public class QuartzManager {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Scheduler scheduler;
	private String groupName;
	private boolean isRunJob;
	private Resource[] jobConfigResources;
	private final String startDateNow = "now";
	private final String endDateAlways = "always";
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public void init() {
		if(!isRunJob){
			return;
		}
		List<JobConfig> jobConfigList = this.parseXmlConfig();
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		try {
			scheduler = schedulerFactory.getScheduler();
			this.buildJob(jobConfigList);
			scheduler.start();
			logger.info("定时任务已启动");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void destory() {
		if(this.scheduler != null){
			try {
				if(this.scheduler.isStarted()){
					this.scheduler.shutdown();
					Thread.sleep(500);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void buildJob(List<JobConfig> jobConfigList){
		if(CollectionHelper.isEmpty(jobConfigList))
			return;
		for(JobConfig jobConfig : jobConfigList){
			if(!jobConfig.isRun()){
				continue;
			}
			jobConfig.validate();
			JobKey jobKey = new JobKey(jobConfig.getJobKey(), groupName);
			TriggerKey triggerKey = new TriggerKey(jobKey.getName(), groupName);
			Class<? extends Job> jobClazz = null;
			try {
				jobClazz = (Class<? extends Job>) Class.forName(jobConfig.getJobClass());
			} catch (Exception e) {
				throw new RuntimeException("根据类名获取类对象失败，请确保是否有这个类：" + jobConfig.getJobClass() + "." + e.getMessage());
			}
			
			JobDetail jobDetail = JobBuilder.newJob(jobClazz).withIdentity(jobKey).usingJobData(new JobDataMap(jobConfig.getJobDataMap())).build();
			TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger().withIdentity(triggerKey).forJob(jobDetail);
			if(StringUtils.isNotEmpty(jobConfig.getRemark())){
				triggerBuilder.withDescription(jobConfig.getRemark());
			}
			if(startDateNow.equals(jobConfig.getStartDate())){
				triggerBuilder.startNow();
			} else {
				try {
					Date startDate = dateFormat.parse(jobConfig.getStartDate());
					triggerBuilder.startAt(startDate);
				} catch(Exception e){
					throw new RuntimeException("解析字符串时间错误：" + jobConfig.getStartDate() + ",jobkey=" + jobConfig.getJobKey() + ",reason:" + e.getMessage());
				}
			}
			
			if(!endDateAlways.equals(jobConfig.getEndDate())){
				try {
					Date endDate = dateFormat.parse(jobConfig.getEndDate());
					triggerBuilder.endAt(endDate);
				} catch(Exception e){
					throw new RuntimeException("解析字符串时间错误：" + jobConfig.getStartDate() + ",jobkey=" + jobConfig.getJobKey() + ",reason:" + e.getMessage());
				}
			}
			
			triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(jobConfig.getCron()));
			
			Trigger trigger = triggerBuilder.build();
			
			try {
				scheduler.scheduleJob(jobDetail, trigger);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private List<JobConfig> parseXmlConfig(){
		List<JobConfig> jobConfigList = new ArrayList<JobConfig>();
		if(this.jobConfigResources == null || this.jobConfigResources.length == 0){
			logger.info("无定时任务配置文件");
			return jobConfigList;
		}
		SAXReader saxReader = new SAXReader();
		InputStream ism = null;
		for(Resource res : jobConfigResources){
			logger.info("正在解析job配置文件：" + res.getFilename());
			try {
				ism = res.getInputStream();
				Document dom = saxReader.read(ism);
				List<JobConfig> jobConfigSubList = this.doParseXml(dom);
				if(CollectionHelper.isNotEmpty(jobConfigSubList)){
					jobConfigList.addAll(jobConfigSubList);
				}
			} catch (Exception e) {
				logger.debug("解析job配置文件异常：" + res.getFilename(), e);
				throw new RuntimeException("解析job配置文件异常：" + res.getFilename() + e.getMessage());
			} finally {
				IOUtils.closeQuietly(ism);
			}
		}
		return jobConfigList;
	}
	
	@SuppressWarnings("unchecked")
	private List<JobConfig> doParseXml(Document dom){
		List<JobConfig> jobConfigList = new ArrayList<JobConfig>();
		List<Element> jobEles = dom.selectNodes("//tpl/job");
		for(Element jobEl : jobEles){
			JobConfig jobConfig = new JobConfig();
			jobConfig.setJobKey(jobEl.attributeValue("jobKey"));
			jobConfig.setJobClass(jobEl.attributeValue("jobClass"));
			jobConfig.setCron(jobEl.attributeValue("cron"));
			jobConfig.setRemark(jobEl.attributeValue("remark"));
			jobConfig.setRun("true".equals(jobEl.attributeValue("isRun")));
			Element startDateEl = jobEl.element("startDate");
			Element endDateEl = jobEl.element("endDate");
			List<Element> jobDataElList = jobEl.elements("jobData");
			if(startDateEl != null){
				jobConfig.setStartDate(startDateEl.getTextTrim());
			}
			if(endDateEl != null){
				jobConfig.setEndDate(endDateEl.getTextTrim());
			}
			if(CollectionHelper.isNotEmpty(jobDataElList)){
				for(Element jobDataEl : jobDataElList){
					String key = jobDataEl.attributeValue("key");
					String value = jobDataEl.getTextTrim();
					jobConfig.setJobData(key, value);
				}
			}
			jobConfigList.add(jobConfig);
		}
		
		return jobConfigList;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public void setIsRunJob(boolean isRunJob) {
		this.isRunJob = isRunJob;
	}
	
	public void setJobConfigResources(Resource[] jobConfigResources) {
		this.jobConfigResources = jobConfigResources;
	}

}
