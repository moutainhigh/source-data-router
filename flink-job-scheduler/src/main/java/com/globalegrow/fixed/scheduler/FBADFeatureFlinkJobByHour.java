package com.globalegrow.fixed.scheduler;

import cn.hutool.core.date.DateUtil;
import com.globalegrow.fixed.consumer.DelayQueenConsumer;
import com.globalegrow.fixed.queen.AbstractFlinkJobQueen;
import com.globalegrow.fixed.queen.DyHdfsCheckExistsJobMessage;
import com.globalegrow.hdfs.utils.HdfsUtil;
import com.globalegrow.util.CommonTextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.DelayQueue;

@Slf4j
@Component
public class FBADFeatureFlinkJobByHour {

    @Autowired
    private DelayQueue<DyHdfsCheckExistsJobMessage> flinkJobQueens;

    private static final String rootHdfsPath = "/user/hive/warehouse/dw_zaful_recommend.db/zaful_app_abset_id_user_fb_all_every_hour/add_time=2019032103";

    private String flinkCommandLine = "/usr/local/services/flink/flink-yarn/flink-1.5.0/bin/flink run -d -m yarn-cluster -yn 1 -yjm 1024 -ytm 1024 /usr/local/services/flink/fb-ad-user-feature-es-0.1.jar --job.hdfs.path ${hdfs_path}";

    @PostConstruct
    public void init() {
        DelayQueenConsumer queenConsumer = new DelayQueenConsumer(flinkJobQueens);
        new Thread(queenConsumer).start();;
    }

    //private static
    @Scheduled(cron = "${app.cron.fbad-freatrue}")
    public void run() {
        log.info("zaful app 用户 Facebook 广告信息任务");
        // 首先运行检查 hdfs 文件是否存在，如果不存在则放入延时队列中
        //int thisHour = DateUtil.thisHour(true);
        //log.info("当前小时数： {} ", thisHour);
        /*String fileDatePath = "";
        if (thisHour == 0) {
            log.info("{} 点跑前一天 23 点数据", thisHour);
            fileDatePath = DateUtil.yesterday().toString("yyyyMMdd") + "23";
        }else {
            if ((thisHour -1) < 10) {
                fileDatePath = DateFormatUtils.format(new Date(), "yyyyMMdd") + "0" + (thisHour - 1);
            }else {
                fileDatePath = DateFormatUtils.format(new Date(), "yyyyMMdd") + (thisHour - 1);
            }

        }*/
        String hdfsPath =  HdfsUtil.getBigDataActiveNamenode() + rootHdfsPath;//CommonTextUtils.replaceOneParameter(rootHdfsPath, "date_hour", fileDatePath);
        String flinkRunCommandLine = CommonTextUtils.replaceOneParameter(flinkCommandLine, "hdfs_path", hdfsPath);
        //log.info("检查文件 {} 是否存在", hdfsPath);
        //if (HdfsUtil.dyFileExist(hdfsPath)) {
            //log.info("文件 {} 存在, 执行 flink 任务", hdfsPath);
            Process process = null;
            //List<String> processList = new ArrayList<String>();
            try {
                process = Runtime.getRuntime().exec(flinkRunCommandLine);
                try(BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))){
                    String line = "";
                    while ((line = input.readLine()) != null) {
                        //processList.add(line);
                        log.info(line);
                    }
                }

                try(BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()))){
                    String line = "";
                    while ((line = input.readLine()) != null) {
                        //processList.add(line);
                        log.info(line);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }



        /*}else {
            log.info("文件 {} 不存在，将任务放入延时队列中 ", hdfsPath);
            DyHdfsCheckExistsJobMessage jobQueen = new DyHdfsCheckExistsJobMessage(hdfsPath, System.currentTimeMillis(), 300000L, flinkRunCommandLine);
            this.flinkJobQueens.offer(jobQueen);
        }*/
    }

}
