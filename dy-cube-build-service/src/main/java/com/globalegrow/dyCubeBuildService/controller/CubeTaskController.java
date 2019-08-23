package com.globalegrow.dyCubeBuildService.controller;

import com.globalegrow.dyCubeBuildService.bean.KylinBuildTaskBean;
import com.globalegrow.dyCubeBuildService.bean.ResponseDTO;
import com.globalegrow.dyCubeBuildService.service.KylinBuildTaskService;
import com.globalegrow.dyCubeBuildService.thread.CubeBuildThread;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@RestController
@RequestMapping("cube-build")
public class CubeTaskController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ThreadPoolTaskScheduler threadPoolTaskScheduler;
	@Autowired
	private ConcurrentHashMap<String, ScheduledFuture<?>> scheduleTaskContainer;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private KylinBuildTaskService kylinBuildTaskService;

	@RequestMapping(value = "task", method = RequestMethod.POST)
	public String addBuildTask(@RequestParam String cubeUrl, @RequestParam String taskName,
			@RequestParam String triggerCron) {
		String username = "ADMIN";
		String password = "Dy@ai2018";
		cubeUrl=cubeUrl.trim();
		String cubeUrlStr = cubeUrl + "/kylin/api/cubes/" + taskName + "/build2";
		if(StringUtils.isNotBlank(taskName)&& taskName.toLowerCase().contains("_hive")){
			taskName=taskName.substring(0,taskName.toLowerCase().indexOf("_hive"));
			cubeUrlStr = cubeUrl + "/kylin/api/cubes/" + taskName + "/build";
		}
		taskName=taskName.trim();
		if (this.checkTaskName(taskName)) {
			logger.info("正在停止任务: {}", taskName);
			ScheduledFuture<?> future = this.scheduleTaskContainer.get(taskName);
			future.cancel(false);
		}
		logger.info("新增任务: {}", taskName);
		this.scheduleTaskContainer.put(taskName, threadPoolTaskScheduler.schedule(
				new CubeBuildThread(cubeUrlStr, username, password, restTemplate), new CronTrigger(triggerCron)));
		kylinBuildTaskService.updateOrAddBuildTask(cubeUrl, taskName, triggerCron);
		return "success";
	}

	@RequestMapping(value = "task-remove")
	public String removeTask(String taskName) {
		if(StringUtils.isNotBlank(taskName)&& taskName.toLowerCase().contains("_hive")){
			taskName=taskName.substring(0,taskName.toLowerCase().indexOf("_hive"));
		}
		taskName=taskName.trim();
		if (this.checkTaskName(taskName)) {
			logger.info("正在停止任务: {}", taskName);
			ScheduledFuture<?> future = this.scheduleTaskContainer.get(taskName);
			future.cancel(false);
			this.scheduleTaskContainer.remove(taskName);
		}
		kylinBuildTaskService.deleteKylinBuildTaskByTaskName(taskName);
		return "success";
	}

	@RequestMapping("task-name")
	public Boolean checkTaskName(String taskName) {
		if(StringUtils.isNotBlank(taskName)&& taskName.toLowerCase().contains("_hive")){
			taskName=taskName.substring(0,taskName.toLowerCase().indexOf("_hive"));
		}
		taskName=taskName.trim();
		return scheduleTaskContainer.containsKey(taskName);
	}

	@RequestMapping("tasks")
	public List<String> currentTasks() {
		List<String> list = new ArrayList<>();
		this.scheduleTaskContainer.entrySet().stream().forEach(e -> list.add(e.getKey()));
		return list;
	}

	@RequestMapping(value = "getTasks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<KylinBuildTaskBean> getTasks(@RequestParam Integer page, @RequestParam Integer size) {
		return kylinBuildTaskService.getKylinBuildTasks(page, size);
	}


    @RequestMapping(value = "manual", method = RequestMethod.POST)
    public ResponseDTO<String> addBuildTask(@RequestParam(required = true) String taskName, @RequestParam(required = true) String type,
                                            @RequestParam(required = false) String startTime, @RequestParam(required = false) String endTime) {
        return kylinBuildTaskService.manualBuildTask( taskName,  type ,startTime, endTime);
    }

}
