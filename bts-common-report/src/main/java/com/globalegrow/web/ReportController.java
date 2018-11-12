package com.globalegrow.web;

import com.globalegrow.report.ReportBuildRule;
import com.globalegrow.report.ReportExecutorService;
import com.globalegrow.report.ReportHandleRunnable;
import com.globalegrow.util.JacksonUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("report")
public class ReportController {

    @Autowired
    private Map<String, ExecutorService> executorServiceMap;

    @Autowired
    private Map<String, ReportExecutorService> reportExecutorServiceMap;

    @GetMapping
    public String addReport(String configPath) throws Exception {
        String config = FileUtils.readFileToString(new File(configPath), "utf-8");
        ReportBuildRule reportBuildRule = JacksonUtil.readValue(config, ReportBuildRule.class);

        ExecutorService old = this.executorServiceMap.get(reportBuildRule.getReportName());
        if (old != null) {
            old.shutdown();
        }
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new ReportHandleRunnable(reportBuildRule));
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
            old.shutdown();
        }
        return "success";
    }

    @GetMapping("threads")
    public Set<String> reportTasks() {
        return this.executorServiceMap.keySet();
    }

}
