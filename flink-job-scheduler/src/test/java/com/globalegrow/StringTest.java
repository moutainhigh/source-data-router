package com.globalegrow;

import cn.hutool.core.date.DateUtil;
import com.globalegrow.util.CommonTextUtils;
import org.junit.Test;

public class StringTest {
    String hdfsPath = "/bigdata/ods/log_clean/ods_app_burial_log/${last_day}/gearbest";

    String commandLine = "/usr/local/services/flink/flink-yarn/flink-1.5.0/bin/flink run -d -m yarn-cluster -yn 1 -yjm 1024 -ytm 1024 -yqu root.flink -ynm gb-app-user-event-init-everyday /usr/local/services/flink/hdfs-orc-file-es-0.1.jar --elastic-servers 172.31.47.84:9302,172.31.43.158:9302,172.31.55.231:9302 --index-name dy_app_gb_event --type-name log --cluster.name esearch-aws-dy --es.eventIndex true --es.routing true --hdfs.server ${name_node_server1} --filePath ${yestoday_file1} --query.sql \"select event_name,event_value,appsflyer_device_id,customer_user_id,event_time ,app_name,platform from user_event where event_name in('af_view_product','af_add_to_bag','af_add_to_wishlist','af_create_order_success','af_search','af_purchase') and lower(app_name) like '%gearbest%'\" --job.parallelism 1 ";


    @Test
    public void test() {
//        String s = "2019-02-27 00:30:08.774  INFO 115734 --- [TaskScheduler-4] c.g.f.s.FBADFeatureFlinkJobByHour        : Job has been submitted with JobID 51c9090c6441cb8b79f5345c7485be97";
//        System.out.println(s.indexOf("Job has been submitted with JobID"));
//        System.out.println(s.substring(s.indexOf("Job has been submitted with JobID")));
//        String[] ss = s.substring(s.indexOf("Job has been submitted with JobID")).split(" ");
//        for (String s1 : ss) {
//            System.out.println(s1);
//        }

        String yesterdayhdfsPath = CommonTextUtils.replaceOneParameter(this.hdfsPath, "last_day",
                DateUtil.yesterday().toString("yyyy/MM/dd"));
        String bigdatahdfsPath = "hdfs://172.31.57.86:8020/";
        String hdfsCommad = CommonTextUtils.replaceOneParameter(this.commandLine, "name_node_server1",
                bigdatahdfsPath);
        String finalCommad = CommonTextUtils.replaceOneParameter(hdfsCommad, "yestoday_file1",
                bigdatahdfsPath.concat(yesterdayhdfsPath));

        String s = DateUtil.yesterday().toString("yyyy/MM/dd");
        System.out.println(s);
    }
}
