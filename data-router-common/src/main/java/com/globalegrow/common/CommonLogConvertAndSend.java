package com.globalegrow.common;

import com.globalegrow.util.GsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

public class CommonLogConvertAndSend extends CommonLogConvert {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    protected void sendToKafka(String topic, Object msg) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, GsonUtil.toJson(msg));
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                logger.debug("log json with dimensions send success!");
            }

            @Override
            public void onFailure(Throwable ex) {
                logger.error("log json with dimensions send failed! msg: {}", msg, ex);
            }
        });
    }

}
