package com.globalegrow;

import com.globalegrow.util.AppLogConvertUtil;
import com.globalegrow.util.JacksonUtil;
import com.globalegrow.util.NginxLogConvertUtil;
import com.globalegrow.util.SpringRedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.redisson.Redisson;
import org.redisson.api.BatchOptions;
import org.redisson.api.RBatch;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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

    private RedissonClient redisson;

    @Value("${redis.type}")
    private String redisType;
    @Value("${redis.master}")
    private String master;
    @Value("${redis.nodes}")
    private String nodes;
    @Value("${redis.password}")
    private String redisPassword;
   @PostConstruct
   public void before() {
       Config config = new Config();
       if ("cluster".equals(redisType)) {
           ClusterServersConfig clusterServersConfig = config.useClusterServers();
           clusterServersConfig.addNodeAddress(nodes.split(","));
           clusterServersConfig.setPassword(redisPassword);
       } else if ("sentinel".equals(redisType)) {
           SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
           sentinelServersConfig.setMasterName(master);
           sentinelServersConfig.addSentinelAddress(nodes.split(","));
           sentinelServersConfig.setPassword(redisPassword);
       }

       redisson = Redisson.create(config);
   }

    private void sendDataToRedis(LinkedBlockingDeque<QueenModel> linkedBlockingDeque) {
        if (linkedBlockingDeque.size() > 0) {
            List<QueenModel> list = new ArrayList<>();
            logger.debug("max_size:{}", maxBatchSize);
            linkedBlockingDeque.drainTo(list, maxBatchSize);
            if (list.size() > 0) {
                //list.stream()
                /*Map<String, List<QueenModel>> mapList =*/
                /*list.stream().collect(Collectors.groupingBy(QueenModel::getKey)).entrySet().parallelStream().forEach(e -> {
                    String redisKey = e.getKey();
                    List<QueenModel> models = e.getValue();
                    List<String> list1 = models.stream().map(model -> model.getValue()).collect(Collectors.toList());
                    SpringRedisUtil.putSet(redisKey, list1.toArray(new String[list1.size()]));
                });*/

                Map<String, List<QueenModel>> grouped = list.stream().collect(Collectors.groupingBy(QueenModel::getKey));
                logger.debug("list_model:{}, {}", list, list.size());
                RBatch batch = redisson.createBatch(BatchOptions.defaults());
                grouped.entrySet().forEach(e -> {
                    String redisKey = e.getKey();
                    List<QueenModel> models = e.getValue();
                    List<String> list1 = models.stream().map(model -> model.getValue()).collect(Collectors.toList());
                    logger.debug("list_size: {}, key,: {}, value: {}", list1.size(), redisKey, list1);
                    batch.getSet(redisKey).addAllAsync(list1);
                });

                batch.executeAsync();

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
