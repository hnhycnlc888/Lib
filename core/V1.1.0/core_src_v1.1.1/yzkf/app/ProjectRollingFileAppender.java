package yzkf.app;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;

public class ProjectRollingFileAppender extends DailyRollingFileAppender {
	static final String PUBLIC_PROJECT = "public";
	private String project = PUBLIC_PROJECT;
	public ProjectRollingFileAppender(){
		super();
	}
	public ProjectRollingFileAppender(String project){
		super();
		this.project = project;
	}	
	public ProjectRollingFileAppender (Layout layout, String filename,
			   String datePattern) throws IOException {
		super(layout, filename, datePattern);
	}
	public ProjectRollingFileAppender (String project,Layout layout, String filename,
			   String datePattern) throws IOException {
		super(layout, filename.replace(PUBLIC_PROJECT, project), datePattern);
		this.project = project;
	}
	public void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
	                                                            throws IOException {
		if(!this.project.equals(PUBLIC_PROJECT))
			super.setFile(fileName.replace(PUBLIC_PROJECT, this.project), append, bufferedIO, bufferSize);
		else
			super.setFile(fileName, append, bufferedIO, bufferSize);
	}
	public void setFile(String file) {
		if(!this.project.equals(PUBLIC_PROJECT))
			super.setFile(file.replace("public", this.project));
		else
			super.setFile(file);
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
		if(!StringUtils.isEmpty(this.fileName))
			this.setFile(this.fileName);
	}
}
