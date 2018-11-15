package com.globalegrow.web;

import com.globalegrow.report.LogDataCache;
import com.globalegrow.report.ReportBuildRule;
import com.globalegrow.report.ReportOrderHandleRunnable;
import com.globalegrow.util.JacksonUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 订单相关配置，主要为加购埋点数据前缀
 */
@RestController
@RequestMapping("order")
public class ReportOrderController {

    @Autowired
    private LogDataCache logDataCache;

    @Autowired
    @Qualifier("orderTaskExecutorServiceMap")
    private Map<String, ExecutorService> orderTaskExecutorServiceMap;

    @RequestMapping(produces = "application/json;charset=UTF-8", method = RequestMethod.POST, value = "json")
    public Object addReportByJson(@RequestBody ReportBuildRule reportBuildRule) {

        ExecutorService old = this.orderTaskExecutorServiceMap.get(reportBuildRule.getReportName());
        if (old != null) {
            old.shutdown();
        }
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new ReportOrderHandleRunnable(reportBuildRule));
        orderTaskExecutorServiceMap.put(reportBuildRule.getReportName(), executorService);

        return reportBuildRule;
    }

    @GetMapping
    public String addReport(String configPath) throws Exception {
        String config = FileUtils.readFileToString(new File(configPath), "utf-8");
        ReportBuildRule reportBuildRule = JacksonUtil.readValue(config, ReportBuildRule.class);

        ExecutorService old = this.orderTaskExecutorServiceMap.get(reportBuildRule.getReportName());
        if (old != null) {
            old.shutdown();
        }
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new ReportOrderHandleRunnable(reportBuildRule));
        orderTaskExecutorServiceMap.put(reportBuildRule.getReportName(), executorService);


        return config;
    }

    @GetMapping("remove")
    public String removeReportTask(String reportName) {
        ExecutorService old = this.orderTaskExecutorServiceMap.get(reportName);
        if (old != null) {
            old.shutdown();
        }
        this.orderTaskExecutorServiceMap.remove(reportName);
        return "success";
    }

    @GetMapping("threads")
    public Set<String> reportTasks() {
        return this.orderTaskExecutorServiceMap.keySet();
    }

}
