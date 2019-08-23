package com.globalegrow.dyCubeBuildService.task.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.globalegrow.dyCubeBuildService.task.IBTSCubeBuildTaskService;
@Service
@EnableScheduling
public class BTSCubeBuildTaskServiceImpl implements IBTSCubeBuildTaskService {
	@Value("${kylin.cube.url}")
	private String cubeUrl;
	@Value("${kylin.cube.authorization}")
	private String authorization;
	@Value("${bts.report.cubenames}")
	private String cubeNames;
	@Scheduled(cron = "${bts.report.cubebuild.cron}")
	public void task() {
		IBTSCubeBuildTaskService.cubeBuild(cubeNames,cubeUrl,authorization, getClass());
	}
}
