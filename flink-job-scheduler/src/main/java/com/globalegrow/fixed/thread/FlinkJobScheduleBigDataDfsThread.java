package com.globalegrow.fixed.thread;

import com.globalegrow.hdfs.utils.HdfsUtil;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

@Data
@ToString
@Slf4j
public class FlinkJobScheduleBigDataDfsThread implements Runnable {

    // 命令中需要被替换的日期对象
    private static final String DATE_VAR = "dy_var_current_date";
    private static final String BIG_DATA_HDFS_PATH_VAR = "dy_var_big_data_path";

    private String flinkJobCommandLine;

    private String jobName;

    private int checkCount = 0;

    private int maxCheckCount = 10;
    // 路径中需包含
    private String hdfsPath;

    private String dateFormat;

    private Boolean needSuccessNotice;

    @Override
    public void run() {

        String currentDay = DateFormatUtils.format(new Date(), this.dateFormat);

        String jobHdfSourcePath = HdfsUtil.getBigDataActiveNamenode() + this.hdfsPath.replace(DATE_VAR, currentDay);

        if (HdfsUtil.bigDataFileExist(jobHdfSourcePath)) {
            log.info("文件 {} 存在，开始执行 flink job");

        }else {
            log.info("埋点文件目录 {} 不存在, 开始每 5 分钟的检查", jobHdfSourcePath);
            try {
                Thread.sleep(360000);
                checkCount: while (!HdfsUtil.bigDataFileExist(jobHdfSourcePath)) {
                    this.checkCount++;
                    log.info("埋点文件目录 {} 不存在, 开始每 5 分钟的检查 {}", jobHdfSourcePath, this.checkCount);
                    if (this.checkCount > this.maxCheckCount) {
                        log.info("任务执行超时，未达到执行任务的先决条件");
                        break checkCount;
                    }
                    Thread.sleep(360000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (this.checkCount < this.maxCheckCount) {

            try {
                Process process = Runtime.getRuntime().exec(this.flinkJobCommandLine.replace(BIG_DATA_HDFS_PATH_VAR, jobHdfSourcePath).replaceAll(DATE_VAR, currentDay));
                try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line = "";
                    while ((line = input.readLine()) != null) {
                        //processList.add(line);
                        log.info(line);

                        if (line.indexOf("Job has been submitted with JobID") > 0) {
                            String[] lines = line.substring(line.indexOf("Job has been submitted with JobID")).split(" ");


                            String jobId = lines[lines.length - 1];
                            log.info("flink 任务提交成功 flink job id {}", jobId);


                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }



    }

}
