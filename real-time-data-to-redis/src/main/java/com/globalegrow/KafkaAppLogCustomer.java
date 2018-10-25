package com.globalegrow;

import com.globalegrow.util.AppLogConvertUtil;
import com.globalegrow.util.JacksonUtil;
import com.globalegrow.util.NginxLogConvertUtil;
import com.globalegrow.util.SpringRedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

@Component
public class KafkaAppLogCustomer {

    public Map<String, LinkedBlockingDeque<QueenModel>> linkedBlockingDequeMap = new HashMap<>();

    private LinkedBlockingDeque<QueenModel> linkedBlockingDeque = new LinkedBlockingDeque<>();
    private LinkedBlockingDeque<QueenModel> linkedBlockingDeque1 = new LinkedBlockingDeque<>();
    private LinkedBlockingDeque<QueenModel> linkedBlockingDeque2 = new LinkedBlockingDeque<>();
    private LinkedBlockingDeque<QueenModel> linkedBlockingDeque3 = new LinkedBlockingDeque<>();
    private LinkedBlockingDeque<QueenModel> linkedBlockingDeque4 = new LinkedBlockingDeque<>();
    private LinkedBlockingDeque<QueenModel> linkedBlockingDeque5 = new LinkedBlockingDeque<>();
    private LinkedBlockingDeque<QueenModel> linkedBlockingDeque6 = new LinkedBlockingDeque<>();
    private LinkedBlockingDeque<QueenModel> linkedBlockingDeque7 = new LinkedBlockingDeque<>();
    private LinkedBlockingDeque<QueenModel> linkedBlockingDeque8 = new LinkedBlockingDeque<>();
    private LinkedBlockingDeque<QueenModel> linkedBlockingDeque9 = new LinkedBlockingDeque<>();

    @Value("${app.redis.readtime.prefix:dy_real_time_}")
    private String redisKeyPrefix;

    @Value("${max-batch-size:10000}")
    private Integer maxBatchSize;

    @Autowired
    private DataLocalBuffer dataLocalBuffer;

    protected static final Logger logger = LoggerFactory.getLogger(KafkaAppLogCustomer.class);

    @Scheduled(fixedDelay = 1000)
    public void sendToRedis() {
        this.sendDataToRedis(linkedBlockingDeque);
    }

    @Scheduled(fixedDelay = 1100)
    public void sendToRedis1() {
        this.sendDataToRedis(linkedBlockingDeque1);
    }

    @Scheduled(fixedDelay = 1200)
    public void sendToRedis2() {
        this.sendDataToRedis(linkedBlockingDeque2);
    }

    @Scheduled(fixedDelay = 1300)
    public void sendToRedis3() {
        this.sendDataToRedis(linkedBlockingDeque3);
    }

    @Scheduled(fixedDelay = 1000)
    public void sendToRedis4() {
        this.sendDataToRedis(linkedBlockingDeque4);
    }

    @Scheduled(fixedDelay = 1000)
    public void sendToRedis5() {
        this.sendDataToRedis(linkedBlockingDeque5);
    }

    @Scheduled(fixedDelay = 1600)
    public void sendToRedis6() {
        this.sendDataToRedis(linkedBlockingDeque6);
    }

    @Scheduled(fixedDelay = 1700)
    public void sendToRedis7() {
        this.sendDataToRedis(linkedBlockingDeque7);
    }

    @Scheduled(fixedDelay = 1800)
    public void sendToRedis8() {
        this.sendDataToRedis(linkedBlockingDeque8);
    }

    @Scheduled(fixedDelay = 1900)
    public void sendToRedis9() {
        this.sendDataToRedis(linkedBlockingDeque9);
    }

    private void sendDataToRedis(LinkedBlockingDeque<QueenModel> linkedBlockingDeque) {
        if (linkedBlockingDeque.size() > 0) {
            List<QueenModel> list = new ArrayList<>();
            linkedBlockingDeque.drainTo(list, maxBatchSize);
            if (list.size() > 0) {
                //list.stream()
                /*Map<String, List<QueenModel>> mapList =*/
                list.stream().collect(Collectors.groupingBy(QueenModel::getKey)).entrySet().parallelStream().forEach(e -> {
                    String redisKey = e.getKey();
                    List<QueenModel> models = e.getValue();
                    List<String> list1 = models.stream().map(model -> model.getValue()).collect(Collectors.toList());
                    SpringRedisUtil.putSet(redisKey, list1.toArray(new String[list1.size()]));
                });
            }
        }
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"0"})})
    public void listen0(String logString) throws Exception {
        this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque);
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"1"})})
    public void listen1(String logString) throws Exception {
        this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque1);
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"2"})})
    public void listen2(String logString) throws Exception {
        this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque2);
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"3"})})
    public void listen3(String logString) throws Exception {
        this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque3);
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"4"})})
    public void listen4(String logString) throws Exception {
        this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque4);
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"5"})})
    public void listen5(String logString) throws Exception {
        this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque5);
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"6"})})
    public void listen6(String logString) throws Exception {
        this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque6);
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"7"})})
    public void listen7(String logString) throws Exception {
        this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque7);
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"8"})})
    public void listen8(String logString) throws Exception {
        this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque8);
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"9"})})
    public void listen9(String logString) throws Exception {
        this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque9);
    }

}
