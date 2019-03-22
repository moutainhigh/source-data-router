package com.globalegrow.fixed.scheduler;

import com.globalegrow.fixed.queen.FlinkBashJob;
import com.globalegrow.hdfs.utils.HdfsUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Data
public abstract class AbstractFlinkJobSerialScheduler {

    protected String rootPcPath = "/bigdata/ods/log_clean/ods_pc_burial_log/current_day/";
    protected String rootAppPath = "/bigdata/ods/log_clean/ods_app_burial_log/current_day/";

    public static final String SUCCESS_FULL_FILE = "_SUCCESS";

    private LinkedBlockingDeque<FlinkBashJob> flinkBashJobs = new LinkedBlockingDeque<>();

    Map<String, FlinkBashJob> currentBuryLogJobs = new ConcurrentHashMap<>();

    protected String flinkJobHistoryServer = "http://bts-master:8082/jobs/";

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Autowired
    protected RestTemplate restTemplate;

    public abstract void run() throws InterruptedException;

    //@Scheduled(fixedDelay = 60000)
    public void runFlinkJob() throws InterruptedException {

        if (atomicInteger.get() == 0 && this.flinkBashJobs.size() > 0) {
            FlinkBashJob job = this.flinkBashJobs.take();
            String jobId = this.execFlinkJob(job);
            this.currentBuryLogJobs.put(jobId, job);
            this.atomicInteger.set(1);
            Thread.sleep(1000);
        }


    }

    public String execFlinkJob(FlinkBashJob job) {
        Process process;
        String jobId = null;
        //List<String> processList = new ArrayList<String>();
        try {
            process = Runtime.getRuntime().exec(job.getFlinkCommandLine());
            //process.waitFor();

            try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = "";
                while ((line = input.readLine()) != null) {
                    //processList.add(line);
                    log.info("任务信息:{}", line);

                    //log.info("任务提交状态: {}", line.indexOf("Job has been submitted with JobID"));
                    if (line.indexOf("Job has been submitted with JobID") >= 0) {
                        String[] lines = line.substring(line.indexOf("Job has been submitted with JobID")).split(" ");
                        jobId = lines[lines.length - 1];
                        log.info("{} 任务 id {}", job.getJobName(), jobId);
                    }

                }
            }

            StringBuilder error = new StringBuilder();
            try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line = "";
                while ((line = input.readLine()) != null) {
                    //processList.add(line);
                    log.info("任务错误信息: {}", line);

                    error.append(line);
                }
            }

        } catch (Exception e) {
            log.error("flink 任务出错", e);
        }
        return jobId;
    }

    @Scheduled(fixedDelay = 15000)
    public void jobStatusCheck() throws InterruptedException {

        //log.info("开始检查任务执行状态");

        List<String> finished = new ArrayList<>();

        this.currentBuryLogJobs.entrySet().forEach(stringFlinkBashJobEntry -> {

            log.info("检查 flink job {} 的状态", stringFlinkBashJobEntry.getKey());
            try {
                    String status = checkJobStatus(stringFlinkBashJobEntry.getKey());
                    if ("FINISHED".equals(status)) {
                        log.info("任务 {} 执行成功，清空当前任务 id", stringFlinkBashJobEntry.getKey());
                        finished.add(stringFlinkBashJobEntry.getKey());
                        this.atomicInteger.set(0);
                    }
                    if ("FAILED".equals(status)) {
                        log.error("{} 任务执行失败，重新放入队列执行", this.currentBuryLogJobs.get(stringFlinkBashJobEntry.getKey()));
                        this.flinkBashJobs.offer(this.currentBuryLogJobs.get(stringFlinkBashJobEntry.getKey()));
                        finished.add(stringFlinkBashJobEntry.getKey());
                    }
                    if (this.flinkBashJobs.size() == 0) {
                        log.info("所有任务执行完毕，通知第三方");
                    }

            } catch (Exception e) {
                if (e instanceof HttpClientErrorException) {
                    HttpClientErrorException httpClientErrorException = (HttpClientErrorException) e;
                    log.info("任务状态检查结果: {}", httpClientErrorException.getStatusText());

                } else {
                    // 发送邮件
                    log.error("任务状态检查出错", e);
                }

            }

        });

        finished.forEach(s -> currentBuryLogJobs.remove(s));

    }

    public String checkJobStatus(String jobId) {
        try {
            Map<String, Object> result = this.restTemplate.getForObject(this.flinkJobHistoryServer + jobId, Map.class);
            if (result != null && StringUtils.isNotEmpty((String) result.get("state"))) {
                return (String) result.get("state");
            }
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return "";
    }


    public void checkHdfsPath(String checkPath) throws InterruptedException {

        while (!HdfsUtil.bigDataFileExist(checkPath)) {

            log.info("{} 不存在，等待 10 分钟", checkPath);
            Thread.sleep(600000);

        }
        log.info("{} 存在 ,提交 flink 任务", checkPath);

    }

}
