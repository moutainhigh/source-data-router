package com.globalegrow.fixed.scheduler;

import com.globalegrow.fixed.queen.FlinkBashJob;
import com.globalegrow.hdfs.utils.HdfsUtil;
import com.globalegrow.utils.FlinkJobStatusCheckUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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

    protected LinkedBlockingDeque<FlinkBashJob> flinkBashJobs = new LinkedBlockingDeque<>();

    Map<String, FlinkBashJob> currentBuryLogJobs = new ConcurrentHashMap<>();

    protected String flinkJobHistoryServer = "http://bts-master:8082/jobs/";

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Autowired
    protected RestTemplate restTemplate;

    public abstract void run() throws InterruptedException;

    //@Scheduled(fixedDelay = 60000)
    public void runFlinkJob() throws InterruptedException {

        if (this.atomicInteger.get() == 0 && this.flinkBashJobs.size() > 0) {
            FlinkBashJob job = this.flinkBashJobs.take();
            log.info("FlinkBashJob", job.getJobName()+job.getFlinkCommandLine());
            String jobId = this.execFlinkJob(job);
            this.currentBuryLogJobs.put(jobId, job);
            this.atomicInteger.set(1);
            Thread.sleep(1000);
        }


    }

    public String execFlinkJob(FlinkBashJob job) {
        log.info("?????? flink job {}", job);
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
                    log.info("????????????:{}", line);

                    //log.info("??????????????????: {}", line.indexOf("Job has been submitted with JobID"));
                    if (line.indexOf("Job has been submitted with JobID") >= 0) {
                        String[] lines = line.substring(line.indexOf("Job has been submitted with JobID")).split(" ");
                        jobId = lines[lines.length - 1];
                        log.info("{} ?????? id {}", job.getJobName(), jobId);
                    }

                }
            }

            StringBuilder error = new StringBuilder();
            try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line = "";
                while ((line = input.readLine()) != null) {
                    //processList.add(line);
                    log.info("??????????????????: {}", line);

                    error.append(line);
                }
            }

        } catch (Exception e) {
            log.error("flink ????????????", e);
        }
        return jobId;
    }

    @Scheduled(fixedDelay = 15000)
    public void jobStatusCheck() throws InterruptedException {

        log.info("??????????????????????????????, ???????????? {} ??? {}", this.currentBuryLogJobs, this.currentBuryLogJobs.size());

        List<String> finished = new ArrayList<>();

        this.currentBuryLogJobs.entrySet().forEach(stringFlinkBashJobEntry -> {

            log.info("?????? flink job {} ?????????, ???????????? {}", stringFlinkBashJobEntry.getKey(), stringFlinkBashJobEntry.getValue());
            try {
                if ((System.currentTimeMillis() - stringFlinkBashJobEntry.getValue().getStartCheckTime()) > 86400000 * 3) {
                    log.error("flink ?????? id: {}, ??????: {} ,?????? 3 h ????????????????????????");
                    this.atomicInteger.set(0);
                    finished.add(stringFlinkBashJobEntry.getKey());
                }

                String status = checkJobStatus(stringFlinkBashJobEntry.getKey());
                if (!FlinkJobStatusCheckUtils.NOT_FOUND.equals(status)) {
                    if ("FINISHED".equals(status)) {
                        log.info("?????? {} ????????????????????????????????? id", stringFlinkBashJobEntry.getKey());
                        finished.add(stringFlinkBashJobEntry.getKey());
                        this.atomicInteger.set(0);
                    }
                    if ("FAILED".equals(status)) {
                        log.error("{} ?????????????????????????????????????????????", this.currentBuryLogJobs.get(stringFlinkBashJobEntry.getKey()));
                        this.flinkBashJobs.offer(this.currentBuryLogJobs.get(stringFlinkBashJobEntry.getKey()));
                        finished.add(stringFlinkBashJobEntry.getKey());
                        this.atomicInteger.set(0);
                    }
                    if (this.flinkBashJobs.size() == 0) {
                        log.info("??????????????????????????????????????????");
                        this.atomicInteger.set(0);
                    }
                }


            } catch (Exception e) {
                if (e instanceof HttpClientErrorException) {
                    HttpClientErrorException httpClientErrorException = (HttpClientErrorException) e;
                    log.info("????????????????????????: {}", httpClientErrorException.getStatusText());

                } else {
                    // ????????????
                    log.error("????????????????????????", e);
                    this.atomicInteger.set(0);
                }

            }

        });

        finished.forEach(s -> currentBuryLogJobs.remove(s));

    }

    public String checkJobStatus(String jobId) {
        try {
//            Map<String, Object> result = this.restTemplate.getForObject(this.flinkJobHistoryServer + jobId, Map.class);
//            if (result != null && StringUtils.isNotEmpty((String) result.get("state"))) {
//                return (String) result.get("state");
//            }
            return FlinkJobStatusCheckUtils.getJobStatusByJobId(jobId);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return "";
    }


    public void checkHdfsPath(String checkPath) throws InterruptedException {

        while (!HdfsUtil.bigDataFileExist(checkPath)) {

            log.info("{} ?????????????????? 10 ??????", checkPath);
            Thread.sleep(600000);

        }
        log.info("{} ?????? ,?????? flink ??????", checkPath);

    }

}
