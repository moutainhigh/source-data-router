package com.globalegrow.dyCubeBuildService.service;

import com.globalegrow.dyCubeBuildService.bean.KylinBuildTaskBean;
import com.globalegrow.dyCubeBuildService.bean.ResponseDTO;

import java.util.List;

public interface KylinBuildTaskService {

    List<KylinBuildTaskBean> getKylinBuildTasks(Integer page, Integer size);

    KylinBuildTaskBean getKylinBuildTask(String taskName);

    void updateKylinBuildTask(KylinBuildTaskBean kylinBuildTaskBean);

    void saveKylinBuildTask(KylinBuildTaskBean kylinBuildTaskBean);

    void deleteKylinBuildTaskByTaskName(String taskName);

    void updateOrAddBuildTask(String cubeUrl, String taskName, String triggerCron);

    ResponseDTO<String> manualBuildTask(String taskName, String type, String startTime, String endTime);
}
