package com.globalegrow.fixed.scheduler;

import com.globalegrow.fixed.queen.FlinkBuryLogDataJob;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Data
@Slf4j
public class BuryLogDataFlinkJob {

    @Autowired
    private LinkedBlockingDeque<FlinkBuryLogDataJob> flinkBuryLogDataJobs;

    @Autowired
    BuryLogDataSyncByDay buryLogDataSyncByDay;

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Scheduled(fixedDelay = 60000)
    public void runFlinkJobFixed() throws InterruptedException {
        if (this.flinkBuryLogDataJobs.size() > 0 && StringUtils.isEmpty(buryLogDataSyncByDay.getCurrentJobId()) && this.atomicInteger.get() == 0) {
            this.atomicInteger.set(1);
            log.info("当前任务 id 为空，提交新任务");
            FlinkBuryLogDataJob job = this.flinkBuryLogDataJobs.take();
            log.info("任务信息:{}", job);
            Process process = null;
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
                            String jobId = lines[lines.length - 1];
                            log.info("{} 任务 id {}", job.getJobName(), jobId);
                            this.buryLogDataSyncByDay.setCurrentJobId(jobId);
                            Thread.sleep(1000);
                            this.atomicInteger.set(0);

                        }

                    }
                }


                try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line = "";
                    while ((line = input.readLine()) != null) {
                        //processList.add(line);
                        log.info("任务错误信息: {}", line);

                       /* if (line.indexOf("Job has been submitted with JobID") > 0) {
                            String[] lines = line.substring(line.indexOf("Job has been submitted with JobID")).split(" ");
                            String jobId = lines[lines.length - 1];
                            log.info("{} 任务 id {}", job.getJobName(), jobId);
                            this.buryLogDataSyncByDay.setCurrentJobId(jobId);
                            Thread.sleep(1000);
                            this.atomicInteger.set(0);

                        }*/
                    }
                }
            } catch (Exception e) {
                log.error("flink 任务出错", e);
            }

        }
    }
}
