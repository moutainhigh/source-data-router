package com.globalegrow;

import cn.hutool.core.date.DateUtil;
import com.globalegrow.fixed.queen.FlinkBashJob;
import com.globalegrow.fixed.scheduler.AbstractFlinkJobSerialScheduler;
import com.globalegrow.util.CommonTextUtils;

/**
 * @Auther: joker
 * @Date: 2019/8/19 16:44
 * @Description:
 */
public class TestRunner extends AbstractFlinkJobSerialScheduler {
    String hdfsPath = "/bigdata/ods/log_clean/ods_app_burial_log/${last_day}/gearbest";

    String commandLine = "D:/work/flink/flink-1.7.2-bin-scala_2.11/flink-1.7.2/bin/flink.bat run " +
            "-d -m yarn-cluster -yn 1 -yjm 1024 -ytm 1024 -yqu root.flink -ynm gb-app-user-event-init-everyday /usr/local/services/flink/hdfs-orc-file-es-0.1.jar --elastic-servers 172.31.47.84:9302,172.31.43.158:9302,172.31.55.231:9302 --index-name dy_app_gb_event --type-name log --cluster.name esearch-aws-dy --es.eventIndex true --es.routing true --hdfs.server ${name_node_server1} --filePath ${yestoday_file1} --query.sql \"select event_name,event_value,appsflyer_device_id,customer_user_id,event_time ,app_name,platform from user_event where event_name in('af_view_product','af_add_to_bag','af_add_to_wishlist','af_create_order_success','af_search','af_purchase') and lower(app_name) like '%gearbest%'\" --job.parallelism 1 ";

    String commandLine2 = "D:/work/flink/flink-1.7.2-bin-scala_2.11/flink-1.7.2/bin/flink.bat run -d" +
            " -m " +
            "yarn-cluster -yn 1 -yjm 1024 -ytm 1024 -yqu root.flink -ynm gb-app-user-event-lastday /usr/local/services/flink/hdfs-orc-file-es-0.1.jar --elastic-servers 172.31.47.84:9302,172.31.43.158:9302,172.31.55.231:9302 --index-name dy_app_gb_event --type-name log --cluster.name esearch-aws-dy --es.eventIndex true --es.routing true --hdfs.server $name_node_server1 --filePath $yestoday_file1 --query.sql \"select event_name,event_value,appsflyer_device_id,customer_user_id,event_time ,app_name,platform from user_event where event_name in('af_view_product','af_add_to_bag','af_add_to_wishlist','af_create_order_success','af_search','af_purchase') and lower(app_name) like '%gearbest%'\" --job.parallelism 1";

    @Override
    public void run() throws InterruptedException {
        String yesterdayhdfsPath = CommonTextUtils.replaceOneParameter(hdfsPath, "last_day",
                DateUtil.yesterday().toString("yyyy-MM-dd"));

        String bigdatahdfsPath = "hdfs://172.31.57.86:8020";
        String hdfsCommad = CommonTextUtils.replaceOneParameter(this.commandLine, "name_node_server1", bigdatahdfsPath);
        String hdfsCommad2 = CommonTextUtils.replaceOneParameter(this.commandLine, "name_node_server1", bigdatahdfsPath);
        System.out.println("hdfsCommad"+hdfsCommad);
        System.out.println("hdfsCommad2"+hdfsCommad2);
        String hdfsPathCommandLine = bigdatahdfsPath + yesterdayhdfsPath;
        String finalCommad = CommonTextUtils.replaceOneParameter(hdfsCommad, "yestoday_file1", hdfsPathCommandLine);
        String finalCommad2 = CommonTextUtils.replaceOneParameter(hdfsCommad2, "yestoday_file1", hdfsPathCommandLine);
        System.out.println("finalCommad"+finalCommad);
        System.out.println("finalCommad2"+finalCommad2);
        FlinkBashJob job = new FlinkBashJob("gb-app-user-event-init-everyday", finalCommad);
        this.flinkBashJobs.offer(job);
        this.runFlinkJob();

    }
}
