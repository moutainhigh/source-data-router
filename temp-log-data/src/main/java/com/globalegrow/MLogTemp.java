package com.globalegrow;

import com.globalegrow.util.GsonUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MLogTemp {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @KafkaListener(topics = { "${zaful-json-source-topic}" }, groupId = "temp_log_file")
    public void listenerGetUser(String record) {
        this.logger.debug(record);
        Map<String, Object> logMap = GsonUtil.readValue(record, Map.class);
        if ("m".equals(String.valueOf(logMap.get("glb_plf"))) && "1".equals(String.valueOf(logMap.get("is_order")))) {
            this.logger.info(record);
        }
    }

}
