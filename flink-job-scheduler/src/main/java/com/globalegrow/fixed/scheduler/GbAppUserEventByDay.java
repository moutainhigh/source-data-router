package com.globalegrow.fixed.scheduler;

import cn.hutool.core.date.DateUtil;
import com.globalegrow.fixed.queen.DyHdfsCheckExistsJobMessage;
import com.globalegrow.fixed.queen.FlinkBashJob;
import com.globalegrow.hdfs.utils.HdfsUtil;
import com.globalegrow.util.CommonTextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.DelayQueue;

/**
 * 每天将 gb app 用户行为中的 点击、加购、加收藏、下单、支付 事件初始化至 es 索引中
 * 数据来源，大数据 app 埋点
 */
@Slf4j
@Component
public class GbAppUserEventByDay extends AbstractFlinkJobSerialScheduler {


    @Autowired
    @Deprecated
    private DelayQueue<DyHdfsCheckExistsJobMessage> flinkJobQueens;

    String hdfsPath = "/bigdata/ods/log_clean/ods_app_burial_log/${last_day}/gearbest";

    String commandLine = "/usr/local/services/flink/flink-yarn/flink-1.5.0/bin/flink run -d -m yarn-cluster -yn 1 -yjm 1024 -ytm 1024 -yqu root.flink -ynm gb-app-user-event-init-everyday /usr/local/services/flink/hdfs-orc-file-es-0.1.jar --elastic-servers 172.31.47.84:9302,172.31.43.158:9302,172.31.55.231:9302 --index-name dy_app_gb_event --type-name log --cluster.name esearch-aws-dy --es.eventIndex true --es.routing true --hdfs.server ${name_node_server1} --filePath ${yestoday_file1} --query.sql \"select event_name,event_value,appsflyer_device_id,customer_user_id,event_time ,app_name,platform from user_event where event_name in('af_view_product','af_add_to_bag','af_add_to_wishlist','af_create_order_success','af_search','af_purchase') and lower(app_name) like '%gearbest%'\" --job.parallelism 1 ";


    /**
     * 每天 3:10 运行
     */
    @Override
    @Scheduled(cron = "${app.cron.gb-app-user-event:0 10 3 * * ?}")
    public void run() throws InterruptedException {
        log.info("gb app用户event 任务运行");
        String yesterdayhdfsPath = CommonTextUtils.replaceOneParameter(this.hdfsPath, "last_day",
                DateUtil.yesterday().toString("yyyy/MM/dd"));
        this.checkHdfsPath("hdfs://glbgnameservice" + yesterdayhdfsPath);
        String bigdatahdfsPath = HdfsUtil.getBigDataActiveNamenode();
        String hdfsCommad = CommonTextUtils.replaceOneParameter(this.commandLine, "name_node_server1",
                bigdatahdfsPath);
        String finalCommad = CommonTextUtils.replaceOneParameter(hdfsCommad, "yestoday_file1",
                bigdatahdfsPath.concat(yesterdayhdfsPath));
        FlinkBashJob job = new FlinkBashJob("gb-app-user-event-init-everyday", finalCommad);
        this.flinkBashJobs.offer(job);
        this.runFlinkJob();

    }

}
