package com.globalegrow.dyCubeBuildService.task.impl;

import com.globalegrow.dyCubeBuildService.bean.KylinBuildTaskBean;
import com.globalegrow.dyCubeBuildService.service.KylinBuildTaskService;
import com.globalegrow.dyCubeBuildService.thread.CubeBuildThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
public class MakeSureCreateTask {

	@Autowired
	private ThreadPoolTaskScheduler threadPoolTaskScheduler;
	
	@Autowired
	private ConcurrentHashMap<String, ScheduledFuture<?>> scheduleTaskContainer;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private KylinBuildTaskService kylinBuildTaskService;

	public void makeSureCreateTaskHandler() {
		String username = "ADMIN";
		String password = "Dy@ai2018";
		//注意这里URL等等是从数据库拿的，配置文件里面的配置可能是本地调试使用的
		List<KylinBuildTaskBean> kylinBuildTasks = kylinBuildTaskService.getKylinBuildTasks(1, 1000);
		for(KylinBuildTaskBean kylinBuildTaskBean :kylinBuildTasks) {
			String taskName = kylinBuildTaskBean.getTaskName();
			String cubeUrl = kylinBuildTaskBean.getCubeUrl();
			String triggerCron = kylinBuildTaskBean.getTriggerCron();
			String cubeUrlStr = cubeUrl + "/kylin/api/cubes/" + taskName + "/build2";
			if (this.checkTaskName(taskName)) {
				ScheduledFuture<?> future = this.scheduleTaskContainer.get(taskName);
				future.cancel(false);
			}
			this.scheduleTaskContainer.put(taskName, threadPoolTaskScheduler.schedule(
					new CubeBuildThread(cubeUrlStr, username, password, restTemplate), new CronTrigger(triggerCron)));
		}		
		
	}
	
	public Boolean checkTaskName(String taskName) {
		return scheduleTaskContainer.containsKey(taskName);
	}
	
}
