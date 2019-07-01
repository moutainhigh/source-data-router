package com.globalegrow.fixed.scheduler;

import cn.hutool.core.date.DateUtil;
import com.globalegrow.fixed.queen.DyHdfsCheckExistsJobMessage;
import com.globalegrow.fixed.queen.FlinkBashJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.DelayQueue;

@Slf4j
@Component
public class GbSearchWordSkuRelByDay extends AbstractFlinkJobSerialScheduler{

    @Autowired
    @Deprecated
    private DelayQueue<DyHdfsCheckExistsJobMessage> flinkJobQueens;

    String hdfsPath = "/user/hive/warehouse/dw_proj.db/zaful_app_search_word_fourteen_days_report/add_time=";

    String commandLine = "/usr/local/services/flink/flink-yarn/flink-1.5.0/bin/flink run -d -m yarn-cluster -yn 1 -yjm 1024 -ytm 1024 -yqu root.flink -ynm search-words-skus-rel /usr/local/services/flink/search-words-skus-rel-0.1.jar --elastic-servers 172.31.47.84:9302,172.31.43.158:9302,172.31.55.231:9302 --index-name dy_gb_search_words_skus_rel --type-name log --cluster.name esearch-aws-dy --job.path.auto.end false --job.search.words.path ";

    @Override
    @Scheduled(cron = "0 15 5 * * ?")
    public void run() throws InterruptedException {
        log.info("gb 搜索词与 sku 关联关系任务运行");
        String lastDayPath = hdfsPath + DateUtil.yesterday().toString("yyyy-MM-dd");
        this.checkHdfsPath("hdfs://glbgnameservice" + lastDayPath);
        FlinkBashJob job = new FlinkBashJob("gb-search-word-sku-rel", this.commandLine + lastDayPath);
        this.flinkBashJobs.offer(job);
        this.runFlinkJob();
    }

}
