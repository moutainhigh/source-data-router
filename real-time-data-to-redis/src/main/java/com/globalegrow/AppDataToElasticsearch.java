package com.globalegrow;

import com.globalegrow.util.NginxLogConvertUtil;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

@Component
public class AppDataToElasticsearch {

    private LinkedBlockingDeque<Map> linkedBlockingDeque = new LinkedBlockingDeque<>();
    private LinkedBlockingDeque<Map> linkedBlockingDeque1 = new LinkedBlockingDeque<>();
    private LinkedBlockingDeque<Map> linkedBlockingDeque2 = new LinkedBlockingDeque<>();
    private LinkedBlockingDeque<Map> linkedBlockingDeque3 = new LinkedBlockingDeque<>();
    private LinkedBlockingDeque<Map> linkedBlockingDeque4 = new LinkedBlockingDeque<>();
    private LinkedBlockingDeque<Map> linkedBlockingDeque5 = new LinkedBlockingDeque<>();
    private LinkedBlockingDeque<Map> linkedBlockingDeque6 = new LinkedBlockingDeque<>();
    private LinkedBlockingDeque<Map> linkedBlockingDeque7 = new LinkedBlockingDeque<>();
    private LinkedBlockingDeque<Map> linkedBlockingDeque8 = new LinkedBlockingDeque<>();
    private LinkedBlockingDeque<Map> linkedBlockingDeque9 = new LinkedBlockingDeque<>();

    private int customerThreadCounter = 0;
    private int customerThreadCounter1 = 0;
    private int customerThreadCounter2 = 0;
    private int customerThreadCounter3 = 0;
    private int customerThreadCounter4 = 0;
    private int customerThreadCounter5 = 0;
    private int customerThreadCounter6 = 0;
    private int customerThreadCounter7 = 0;
    private int customerThreadCounter8 = 0;
    private int customerThreadCounter9 = 0;

    @Autowired
    private JestClient jestClient;

    @Value("${enable.customer.statistics:false}")
    private Boolean enableCounter = false;

    @Value("${app.redis.readtime.prefix:dy_real_time_}")
    private String redisKeyPrefix;

    private Integer maxBatchSize = 10000;

    @Autowired
    private DataLocalBuffer dataLocalBuffer;

    protected static final Logger logger = LoggerFactory.getLogger(KafkaAppLogCustomer.class);

    @Scheduled(fixedDelay = 1000)
    public void sendToRedis() {
        this.sendDataToRedis(linkedBlockingDeque, 0, System.currentTimeMillis());
    }

    @Scheduled(fixedDelay = 1100)
    public void sendToRedis1() {
        this.sendDataToRedis(linkedBlockingDeque1, 1, System.currentTimeMillis());
    }

    @Scheduled(fixedDelay = 1200)
    public void sendToRedis2() {
        this.sendDataToRedis(linkedBlockingDeque2, 2, System.currentTimeMillis());
    }

    @Scheduled(fixedDelay = 1300)
    public void sendToRedis3() {
        this.sendDataToRedis(linkedBlockingDeque3, 3, System.currentTimeMillis());
    }

    @Scheduled(fixedDelay = 1000)
    public void sendToRedis4() {
        this.sendDataToRedis(linkedBlockingDeque4, 4, System.currentTimeMillis());
    }

    @Scheduled(fixedDelay = 1000)
    public void sendToRedis5() {
        this.sendDataToRedis(linkedBlockingDeque5, 5, System.currentTimeMillis());
    }

    @Scheduled(fixedDelay = 1600)
    public void sendToRedis6() {
        this.sendDataToRedis(linkedBlockingDeque6, 6, System.currentTimeMillis());
    }

    @Scheduled(fixedDelay = 1700)
    public void sendToRedis7() {
        this.sendDataToRedis(linkedBlockingDeque7, 7, System.currentTimeMillis());
    }

    @Scheduled(fixedDelay = 1800)
    public void sendToRedis8() {
        this.sendDataToRedis(linkedBlockingDeque8, 8, System.currentTimeMillis());
    }

    @Scheduled(fixedDelay = 1900)
    public void sendToRedis9() {
        this.sendDataToRedis(linkedBlockingDeque9, 9, System.currentTimeMillis());
    }

    private void sendDataToRedis(LinkedBlockingDeque<Map> linkedBlockingDeque, int thread, long startTime) {
        if (linkedBlockingDeque.size() > 0) {
            List<Map> list = new ArrayList<>();
            logger.debug("max_size:{}", maxBatchSize);
            linkedBlockingDeque.drainTo(list, maxBatchSize);
            if (list.size() > 0) {
                logger.debug("list_model:{}, {}", list, list.size());
                list.parallelStream().forEach(m -> {
                    Index index = new Index.Builder(m).index("bts-app-dopamine-" + DateFormatUtils.format((Long) m.get(NginxLogConvertUtil.TIMESTAMP_KEY), "yyyy-MM-dd")).type("log").build();
                    try {
                        jestClient.execute(index);
                    } catch (IOException e) {
                        logger.error("数据发送到 es 失败{}", m, e);
                    }
                });
                if (enableCounter) {
                    logger.info("线程 {} 每秒发送到 redis 消息数：{}", thread, list.size());
                }
            }
        }
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"0"})})
    public void listen0(String logString) throws Exception {
        long startTime = System.currentTimeMillis();
        this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque);
        if (this.enableCounter) {
            customerThreadCounter++;
            this.customerCounter(0, customerThreadCounter, startTime, System.currentTimeMillis());
        }
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"1"})})
    public void listen1(String logString) throws Exception {
        long startTime = System.currentTimeMillis();
        this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque1);
        if (this.enableCounter) {
            customerThreadCounter1++;
            this.customerCounter(1, customerThreadCounter1, startTime, System.currentTimeMillis());
        }
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"2"})})
    public void listen2(String logString) throws Exception {
        long startTime = System.currentTimeMillis();
        this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque2);
        if (this.enableCounter) {
            customerThreadCounter2++;
            this.customerCounter(2, customerThreadCounter2, startTime, System.currentTimeMillis());
        }
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"3"})})
    public void listen3(String logString) throws Exception {
        long startTime = System.currentTimeMillis();
        this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque3);
        if (this.enableCounter) {
            customerThreadCounter3++;
            this.customerCounter(3, customerThreadCounter3, startTime, System.currentTimeMillis());
        }
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"4"})})
    public void listen4(String logString) throws Exception {
        long startTime = System.currentTimeMillis();
        this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque4);
        if (this.enableCounter) {
            customerThreadCounter4++;
            this.customerCounter(4, customerThreadCounter4, startTime, System.currentTimeMillis());
        }
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"5"})})
    public void listen5(String logString) throws Exception {
        long startTime = System.currentTimeMillis();
        this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque5);
        if (this.enableCounter) {
            customerThreadCounter5++;
            this.customerCounter(5, customerThreadCounter5, startTime, System.currentTimeMillis());
        }
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"6"})})
    public void listen6(String logString) throws Exception {
        long startTime = System.currentTimeMillis();
        this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque6);
        if (this.enableCounter) {
            customerThreadCounter6++;
            this.customerCounter(6, customerThreadCounter6, startTime, System.currentTimeMillis());
        }
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"7"})})
    public void listen7(String logString) throws Exception {
        long startTime = System.currentTimeMillis();
        this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque7);
        if (this.enableCounter) {
            customerThreadCounter7++;
            this.customerCounter(7, customerThreadCounter7, startTime, System.currentTimeMillis());
        }
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"8"})})
    public void listen8(String logString) throws Exception {
        long startTime = System.currentTimeMillis();
        this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque8);
        if (this.enableCounter) {
            customerThreadCounter8++;
            this.customerCounter(8, customerThreadCounter8, startTime, System.currentTimeMillis());
        }
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"9"})})
    public void listen9(String logString) throws Exception {
        long startTime = System.currentTimeMillis();
        this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque9);
        if (this.enableCounter) {
            customerThreadCounter9++;
            this.customerCounter(9, customerThreadCounter9, startTime, System.currentTimeMillis());
        }
    }

    private void customerCounter(int thread, int counter, long startTime, long endTime) {
        if ((endTime - startTime) > 1000) {
            logger.debug("线程 {} 每秒消费消息数：{}", thread, counter);
            counter = 0;
        }
    }

}
