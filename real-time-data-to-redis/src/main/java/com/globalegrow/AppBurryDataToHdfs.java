package com.globalegrow;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

//@Component
public class AppBurryDataToHdfs {

    @KafkaListener(topics = "${log-source}", groupId = "app-burry-data-hdfs")
    public void listen0(String logString)  {

    }

}
