package com.globalegrow;

import com.globalegrow.util.NginxLogConvertUtil;
import io.searchbox.client.JestClient;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

//@Component
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

    @Autowired
    @Qualifier("myJestClient")
    private JestClient jestClient;

    @Value("${enable.customer.statistics:false}")
    private Boolean enableCounter = false;

    @Value("${app.redis.readtime.prefix:dy_real_time_}")
    private String redisKeyPrefix;

    private Integer maxBatchSize = 1000;

    @Autowired
    private DataLocalBuffer dataLocalBuffer;

    private TransportClient client;

    @PostConstruct
    private void before() throws UnknownHostException {
        Settings settings = Settings.builder().put("cluster.name","test-wang").build();
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("172.31.19.189"), 9300))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("172.31.47.22"), 9300));
    }

    protected static final Logger logger = LoggerFactory.getLogger(AppDataToElasticsearch.class);

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
                BulkRequest request = new BulkRequest();



                list.parallelStream().forEach(m -> {

                    request.add(new IndexRequest("bts-app-dopamine-" + DateFormatUtils.format((Long) m.get(NginxLogConvertUtil.TIMESTAMP_KEY), "yyyy-MM-dd"), "log")
                            .source(m));
                    /*Index index = new Index.Builder(m).index("bts-app-dopamine-" + DateFormatUtils.format((Long) m.get(NginxLogConvertUtil.TIMESTAMP_KEY), "yyyy-MM-dd")).type("log").build();

                    try {
                        jestClient.execute(index);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                });

                client.bulk(request);
            }
        }
    }

    @KafkaListener(id = "app0",topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"0"})}, groupId = "app-burry-data")
    public void listen0(String logString)  {
        try {

            this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(id = "app1",topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"1"})}, groupId = "app-burry-data")
    public void listen1(String logString)  {
        try {

            this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(id = "app2",topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"2"})}, groupId = "app-burry-data")
    public void listen2(String logString)  {
        try {

            this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(id = "app3",topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"3"})}, groupId = "app-burry-data")
    public void listen3(String logString)  {
        try {

            this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque3);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(id = "app4",topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"4"})}, groupId = "app-burry-data")
    public void listen4(String logString)  {
        try {

            this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque4);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(id = "app5",topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"5"})}, groupId = "app-burry-data")
    public void listen5(String logString)  {
        try {

            this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque5);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(id = "app6",topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"6"})}, groupId = "app-burry-data")
    public void listen6(String logString)  {
        try {

            this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque6);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(id = "app7",topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"7"})}, groupId = "app-burry-data")
    public void listen7(String logString)  {
        try {

            this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque7);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(id = "app8",topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"8"})}, groupId = "app-burry-data")
    public void listen8(String logString)  {
        try {
            this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(id = "app9",topicPartitions = {@TopicPartition(topic = "${log-source}", partitions = {"9"})}, groupId = "app-burry-data")
    public void listen9(String logString)  {
        try {
            this.dataLocalBuffer.handleMsgAsync(logString, linkedBlockingDeque9);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void customerCounter(int thread, int counter, long startTime, long endTime) {
        if ((endTime - startTime) > 1000) {
            logger.debug("线程 {} 每秒消费消息数：{}", thread, counter);
            counter = 0;
        }
    }

}
