package com.globalegrow;

import com.globalegrow.util.NginxLogConvertUtil;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

@Component
public class GbDataToEs {

    private LinkedBlockingDeque<Map> linkedBlockingDeque = new LinkedBlockingDeque<>();

    @Autowired
    private JestClient jestClient;

    private Integer maxBatchSize = 10000;

    @Autowired
    @Qualifier("gbRecommendReportData")
    private ReportDataLocalBuffer dataLocalBuffer;

    protected static final Logger logger = LoggerFactory.getLogger(KafkaAppLogCustomer.class);

    @Scheduled(fixedDelay = 1000)
    public void sendToRedis() {
        this.sendDataToRedis(linkedBlockingDeque, 0, System.currentTimeMillis());
    }


    private void sendDataToRedis(LinkedBlockingDeque<Map> linkedBlockingDeque, int thread, long startTime) {
        if (linkedBlockingDeque.size() > 0) {
            List<Map> list = new ArrayList<>();
            logger.debug("max_size:{}", maxBatchSize);
            linkedBlockingDeque.drainTo(list, maxBatchSize);
            if (list.size() > 0) {
                logger.debug("list_model:{}, {}", list, list.size());
                list.parallelStream().forEach(m -> {
                    Index index = new Index.Builder(m).index("bts-pc-burry-data-gb" + DateFormatUtils.format((Long) m.get(NginxLogConvertUtil.TIMESTAMP_KEY), "yyyy-MM-dd")).type("log").build();
                    try {
                        logger.debug("good_detail_event_to_es");
                        jestClient.execute(index);
                    } catch (IOException e) {
                        logger.error("数据发送到 es 失败{}", m, e);
                    }
                });
            }
        }
    }

    @KafkaListener(topics = "${log-source}", groupId = "gb-report-1")
    public void listen0(String logString) throws Exception {
        List<Map> maps = this.dataLocalBuffer.logWriteToRedis(logString);
        maps.stream().forEach(m ->{
            Index index = new Index.Builder(m).index("bts-pc-burry-data-gb" + DateFormatUtils.format((Long) m.get(NginxLogConvertUtil.TIMESTAMP_KEY), "yyyy-MM-dd")).type("log").build();
            try {
                logger.debug("good_detail_event_to_es");
                jestClient.execute(index);
            } catch (IOException e) {
                logger.error("数据发送到 es 失败{}", m, e);
            }
        });
    }

}
