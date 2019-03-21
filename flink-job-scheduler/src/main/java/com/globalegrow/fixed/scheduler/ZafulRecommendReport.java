package com.globalegrow.fixed.scheduler;

import cn.hutool.core.date.DateUtil;
import com.globalegrow.fixed.queen.FlinkBashJob;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 天池推荐报表
 */
@Component
@Data
@Slf4j
public class ZafulRecommendReport extends AbstractFlinkJobSerialScheduler {

    private String baseCommand = "/usr/local/services/flink/flink-yarn/flink-1.5.0/bin/flink run -d -m yarn-cluster -yqu root.flink -yjm 1024 -ytm 8192 /usr/local/services/flink/dy_ai_recommend_report-0.1.jar --bury.platform ";

    private String baseParameterCommand = " --bury.date _bury_date --order.table.date _order_table_date --site zaful --job.parallelism 1 ";

    private String pcJobCommand = baseCommand +  "pc" + baseParameterCommand;

    private String appJobCommand = baseCommand +  "app" + baseParameterCommand;

    private String pcBtsCommand = baseCommand.replace("8192", "4096") +  "pc_zaful_flatten_bts_pc" + baseParameterCommand;

    private String appBtsCommand = baseCommand.replace("8192", "4096") +  "pc_zaful_flatten_bts_app" + baseParameterCommand;

    private String pcAndAppOrderRecountCommand = baseCommand.replace("8192", "1024") +  "recount_order"  + baseParameterCommand;

    private String pcBtsOrderRecountCommand = baseCommand.replace("8192", "1024") +  "recount_order_bts_pc"  + baseParameterCommand;

    private String appBtsOrderRecountCommand = baseCommand.replace("8192", "1024") +  "recount_order_bts_app"  + baseParameterCommand;

    private volatile String cart14JobId;

    private String cart14JobCommand = "/usr/local/services/flink/flink-yarn/flink-1.5.0/bin/flink run -d -m yarn-cluster -yqu root.flink -yjm 1024 -ytm 16384 /usr/local/services/flink/zaful_last14_cart_and_current_order_rel-0.1.jar --bury.date _bury_date --job.parallelism 1";

    @Scheduled(cron = "${app.cron.bury-log-data:0 1 0 * * ?}")
    public void cart14JobRun() throws InterruptedException {
        // 检查 pc app 的 目录是否存在
        String currentDay = DateUtil.yesterday().toString("yyyy/MM/dd");
        checkHdfsPath( "hdfs://glbgnameservice" + this.rootPcPath.replace("current_day", currentDay) + SUCCESS_FULL_FILE);
        checkHdfsPath("hdfs://glbgnameservice" + this.rootAppPath.replace("current_day", currentDay) + SUCCESS_FULL_FILE);
        String commandLine = this.cart14JobCommand.replaceAll("_bury_date", currentDay);

        //FlinkBashJob flinkBashJob = new FlinkBashJob("zaful_cart14", this.cart14JobCommand);
        Process process;
        String jobId;
        try {
            process = Runtime.getRuntime().exec(commandLine);
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

    }

    @Scheduled(cron = "${app.cron.cart14-data}")
    public void initJobs() throws InterruptedException {



    }

    @Scheduled(fixedDelay = 10000)
    @Override
    void run() throws InterruptedException {

        this.runFlinkJob();

    }


}
