package com.globalegrow.fixed.scheduler;

import com.globalegrow.fixed.consumer.DelayQueenConsumer;
import com.globalegrow.fixed.queen.AbstractFlinkJobQueen;
import com.globalegrow.fixed.queen.FBADFeatureMessage;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.DelayQueue;

@Slf4j
@Component
public class FBADFeatureFlinkJobByHour {

    @Autowired
    private DelayQueue<AbstractFlinkJobQueen> flinkJobQueens;

    private static final String rootHdfsPath = "hdfs:///user/wuchao/dw_zaful_recommend/zaful_app_abset_id_user_fb_all_every_hour/add_time=${date_hour}/part-00000";

    private String flinkCommandLine = "/usr/local/services/flink/flink-yarn/flink-1.5.0/bin/flink run -d -m yarn-cluster -yn 1 -yjm 1024 -ytm 1024 /usr/local/services/flink/fb-ad-user-feature-es-0.1.jar --job.hdfs.path ${hdfs_path}";

    @PostConstruct
    public void init() {
        DelayQueenConsumer queenConsumer = new DelayQueenConsumer(flinkJobQueens);
        new Thread(queenConsumer).start();;
    }

    //private static
    @Scheduled(cron = "${app.cron.dfad-freatrue}")
    public void run() {
        // 首先运行检查 hdfs 文件是否存在，如果不存在则放入延时队列中
        String hdfsPath = CommonTextUtils.replaceOneParameter(rootHdfsPath, "date_hour", DateFormatUtils.format(new Date(), "yyyyMMddHH"));
        log.info("检查文件 {} 是否存在", hdfsPath);
        if (HdfsUtil.dyFileExist(hdfsPath)) {
            log.info("文件 {} 存在, 执行 flink 任务");

            String flinkRunCommandLine = CommonTextUtils.replaceOneParameter(flinkCommandLine, "hdfs_path", hdfsPath);

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
            } catch (IOException e) {
                e.printStackTrace();
            }



        }else {
            log.info("文件 {} 不存在，将任务放入延时队列中 ");
            AbstractFlinkJobQueen jobQueen = new FBADFeatureMessage(hdfsPath, System.currentTimeMillis(), 600000L);
            this.flinkJobQueens.offer(jobQueen);
        }
    }

}
