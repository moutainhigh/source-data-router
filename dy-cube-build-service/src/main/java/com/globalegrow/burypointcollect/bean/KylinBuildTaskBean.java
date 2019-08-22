package com.globalegrow.burypointcollect.bean;

import java.util.Date;

public class KylinBuildTaskBean {
	
	private Long id;

	private String taskName;
	
	private String cubeUrl;
	
	private String triggerCron;
	
	private Date updateTime;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getCubeUrl() {
		return cubeUrl;
	}

	public void setCubeUrl(String cubeUrl) {
		this.cubeUrl = cubeUrl;
	}

	public String getTriggerCron() {
		return triggerCron;
	}

	public void setTriggerCron(String triggerCron) {
		this.triggerCron = triggerCron;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	
}
