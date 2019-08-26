package com.globalegrow;

import cn.hutool.core.date.DateUtil;
import com.globalegrow.hdfs.utils.HdfsUtil;
import com.globalegrow.util.CommonTextUtils;
import org.junit.Test;

/**
 * @Auther: joker
 * @Date: 2019/8/19 16:44
 * @Description:
 */
public class test {
    String hdfsPath = "/bigdata/ods/log_clean/ods_app_burial_log/$last_day/gearbest";

    String commandLine = "/usr/local/services/flink/flink-yarn/flink-1.7.2/bin/flink run -m yarn-cluster -yn 1 -yjm 1024 -ytm 1024 -ynm gb-app-user-event-init-everyday -s 1 -d /usr/local/services/flink/hdfs-orc-file-es-0.1.jar --elastic-servers 172.31.47.84:9302,172.31.43.158:9302,172.31.55.231:9302 --index-name dy_app_gb_event --type-name log --cluster.name esearch-aws-dy --es.eventIndex true --es.routing true --job-name history-data-2018-01 --hdfs.server hdfs://172.31.20.96:8020/ --filePath hdfs://172.31.20.96:8020//bigdata/ods/log_clean/ods_app_burial_log/2019/08/18/gearbest --query.sql \"select event_name,event_value,appsflyer_device_id,customer_user_id,event_time ,app_name,platform from user_event where event_name in('af_view_product','af_add_to_bag','af_add_to_wishlist','af_create_order_success','af_search','af_purchase') and lower(app_name) like '%gearbest%'\" --job.parallelism 1 ";

    @Test
    public  void test() throws Exception {

        String yesterdayhdfsPath = CommonTextUtils.replaceOneParameter(hdfsPath, "last_day",
                DateUtil.yesterday().toString("yyyy-MM-dd"));
        checkHdfsPath("hdfs://glbgnameservice" + yesterdayhdfsPath);
        System.out.println(yesterdayhdfsPath);
        String bigdatahdfsPath = HdfsUtil.getBigDataActiveNamenode();
        System.out.println(bigdatahdfsPath);
        String hdfsCommad = CommonTextUtils.replaceOneParameter(commandLine, "name_node_server1",
                bigdatahdfsPath);
        System.out.println(hdfsCommad);
        String finalCommad = CommonTextUtils.replaceOneParameter(hdfsCommad, "yestoday_file1", yesterdayhdfsPath);
        System.out.println(finalCommad);
    }

    public  void checkHdfsPath(String checkPath) throws InterruptedException {

        while (!HdfsUtil.bigDataFileExist(checkPath)) {


            Thread.sleep(600000);

        }


    }
}
