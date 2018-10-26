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

    @Value("${enable.customer.statistics:false}")
    private Boolean enableCounter = false;

    @Value("${app.redis.readtime.prefix:dy_real_time_}")
    private String redisKeyPrefix;

    @Value("${max-batch-size:10000}")
    private Integer maxBatchSize;

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

    private RedissonClient redisson;

    @Value("${redis.type}")
    private String redisType;
    @Value("${redis.master:none}")
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

    private void sendDataToRedis(LinkedBlockingDeque<QueenModel> linkedBlockingDeque, int thread, long startTime) {
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
