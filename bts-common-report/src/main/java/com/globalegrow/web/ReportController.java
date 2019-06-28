package com.globalegrow.web;

import com.globalegrow.report.LogDataCache;
import com.globalegrow.report.ReportBuildRule;
import com.globalegrow.report.ReportHandleRunnable;
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

@RestController
@RequestMapping("report")
public class ReportController {

    @Autowired
    private LogDataCache logDataCache;

    @Autowired
    @Qualifier("executorServiceMap")
    private Map<String, ExecutorService> executorServiceMap;

    @RequestMapping(produces = "application/json;charset=UTF-8", method = RequestMethod.POST, value = "json")
    public Object addReportByJson(@RequestBody ReportBuildRule reportBuildRule) {

        ExecutorService old = this.executorServiceMap.get(reportBuildRule.getReportName());
        if (old != null) {
            old.shutdownNow();
        }
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new ReportHandleRunnable(this.logDataCache, reportBuildRule));
        executorServiceMap.put(reportBuildRule.getReportName(), executorService);

        return reportBuildRule;
    }

    @GetMapping
    @Deprecated
    public String addReport(String configPath) throws Exception {
        String config = FileUtils.readFileToString(new File(configPath), "utf-8");
        ReportBuildRule reportBuildRule = JacksonUtil.readValue(config, ReportBuildRule.class);

        ExecutorService old = this.executorServiceMap.get(reportBuildRule.getReportName());
        if (old != null) {
            old.shutdownNow();
        }
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new ReportHandleRunnable(this.logDataCache, reportBuildRule));
        executorServiceMap.put(reportBuildRule.getReportName(), executorService);
        /*
         Thread oldReport = this.reportThreads.get(reportBuildRule.getReportName());
        if (oldReport != null) {
            oldReport.interrupt();
        }
        Thread thread = new Thread();
        thread.run();
        reportThreads.put(reportBuildRule.getReportName(), thread);*/
        return config;
    }



    @GetMapping("remove")
    public String removeReportTask(String reportName) {
        ExecutorService old = this.executorServiceMap.get(reportName);
        if (old != null) {
            old.shutdownNow();
        }
        this.executorServiceMap.remove(reportName);
        return "success";
    }

    @GetMapping("threads")
    public Set<String> reportTasks() {
        return this.executorServiceMap.keySet();
    }

}
