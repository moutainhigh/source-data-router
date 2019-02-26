package com.globalegrow.fixed.scheduler;

import cn.hutool.core.date.DateUtil;
import com.globalegrow.fixed.queen.AbstractFlinkJobQueen;
import com.globalegrow.fixed.queen.DyHdfsCheckExistsJobMessage;
import com.globalegrow.hdfs.utils.HdfsUtil;
import com.globalegrow.util.CommonTextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.DelayQueue;

@Slf4j
@Component
public class ZafulAppUserBaseInfoAndFBADFeature {

    @Autowired
    private DelayQueue<DyHdfsCheckExistsJobMessage> flinkJobQueens;

    private String hdfsPath = "hdfs:///user/wuchao/dw_zaful_recommend/zaful_app_abset_id_user_fb_cookieid_fb/add_time=${last_day}/part-00000";

    private String flinkCommandLine = "/usr/local/services/flink/flink-yarn/flink-1.5.0/bin/flink run -d -m yarn-cluster -yn 1 -yjm 1024 -ytm 1024 -s 1 -nm app-user-feature-es /usr/local/services/flink/app-user-feature-es-0.1.jar --job.hdfs.path ${hdfs_path} --index-name dy_app_zaful_user_feature";

    @Scheduled(cron = "${app.cron.zaful-app-user-fb-freatrue}")
    public void run(){
        String hdfsPathLastDay = CommonTextUtils.replaceOneParameter(this.hdfsPath, "last_day", DateUtil.yesterday().toString("yyyyMMdd"));
        String yesterdayCommandLine = CommonTextUtils.replaceOneParameter(this.flinkCommandLine, "hdfs_path", hdfsPathLastDay);
        log.info("检查文件 {} 是否存在", hdfsPathLastDay);
        if (HdfsUtil.dyFileExist(hdfsPathLastDay)) {
            log.info("文件 {} 存在，执行任务", yesterdayCommandLine);

            Process process = null;
            //List<String> processList = new ArrayList<String>();
            try {
                process = Runtime.getRuntime().exec(yesterdayCommandLine);
                try(BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))){
                    String line = "";
                    while ((line = input.readLine()) != null) {
                        //processList.add(line);
                        log.info(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            log.info("文件不存在, 放入延时队列中");
            DyHdfsCheckExistsJobMessage jobQueen = new DyHdfsCheckExistsJobMessage(hdfsPath, System.currentTimeMillis(), 300000L, yesterdayCommandLine);
            this.flinkJobQueens.offer(jobQueen);
        }
    }

}
