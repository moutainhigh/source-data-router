package com.globalegrow.dy.bts.kafka;

import com.globalegrow.common.CommonLogConvert;
import com.globalegrow.hbase.HbaseQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Component
public class GbCookieAndUserIdPersistence extends CommonLogConvert {

    public final CountDownLatch countDownLatch1 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch2 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch3 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch4 = new CountDownLatch(1);
    public final CountDownLatch countDownLatch5 = new CountDownLatch(1);

    @Autowired
    private HbaseQuery hbaseQuery;

    @Value("${app.hbase.gb-cookie-userid-table}")
    private String hbaseTableName;

    @Value("${app.hbase.gb-cookie-user-clumn-family}")
    private String columnFamily;

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"6", "7"})}, groupId = "bts_gb_cookie_user_rel")
    public void listen1(String logString) throws Exception {
        this.logger.debug("customer thread 1");
        this.saveCookieAndUserRel(logString);
        this.countDownLatch1.countDown();
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"8", "9"})}, groupId = "bts_gb_cookie_user_rel")
    public void listen2(String logString) throws Exception {
        this.logger.debug("customer thread 2");
        this.saveCookieAndUserRel(logString);
        this.countDownLatch2.countDown();
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"4", "5"})}, groupId = "bts_gb_cookie_user_rel")
    public void listen3(String logString) throws Exception {
        this.logger.debug("customer thread 3");
        this.saveCookieAndUserRel(logString);
        this.countDownLatch3.countDown();
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"2", "3"})}, groupId = "bts_gb_cookie_user_rel")
    public void listen4(String logString) throws Exception {
        this.logger.debug("customer thread 4");
        this.saveCookieAndUserRel(logString);
        this.countDownLatch4.countDown();
    }

    /**
     * @throws Exception
     */
    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${app.kafka.log-source-topic}", partitions = {"0", "1"})}, groupId = "bts_gb_cookie_user_rel")
    public void listen5(String logString) throws Exception {
        this.logger.debug("customer thread 5");
        this.saveCookieAndUserRel(logString);
        this.countDownLatch5.countDown();
    }

    public void saveCookieAndUserRel(String logString) {
        this.logger.debug("save_rel_source:{}" ,logString);
        Map<String, Object> logMap = this.logToMap(logString);
        this.logger.debug("save_rel_map:{}" ,logMap);
        if (logMap != null && logMap.size() > 0) {

            String cookie = String.valueOf(logMap.get("glb_od"));
            String userId = String.valueOf(logMap.get("glb_u"));
            if (StringUtils.isNotBlank(cookie) && StringUtils.isNotBlank(userId) && !"null".equals(cookie) && !"null".equals(userId)) {
                this.logger.debug("save_rel_hbase 保存 cookie userid 的关系: {},{}", cookie, userId);
                Map<String, Object> data = new HashMap<>();
                //data.put("cookie", cookie);
                data.put("userid", userId);
                this.hbaseQuery.insertData(this.hbaseTableName, data, cookie,
                        this.columnFamily);
            }

        }

    }

}
