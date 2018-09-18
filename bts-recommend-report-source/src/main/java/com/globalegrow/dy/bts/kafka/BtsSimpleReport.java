package com.globalegrow.dy.bts.kafka;

import com.globalegrow.bts.BtsPlanInfoQuery;
import com.globalegrow.dy.bts.tmp.ClickDataLog;
import com.globalegrow.dy.report.DataTypeConvert;
import com.globalegrow.dy.report.enums.RecommendQuotaFields;
import com.globalegrow.dy.report.enums.ValueType;
import com.globalegrow.util.GsonUtil;
import com.globalegrow.util.JacksonUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.zip.DataFormatException;

@Component
public class BtsSimpleReport {

    @Autowired
    private BtsPlanInfoQuery btsPlanInfoQuery;

    @Value("${app.kafka.log-zaful-out-topic}")
    private String logZafulOutTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ClickDataLog clickDataLog;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    public final CountDownLatch countDownLatch1 = new CountDownLatch(1);

    @KafkaListener(topics = {"${app.kafka.log-json-topic}"}, groupId = "bts_quick_zaful_report")
    public void listen(ConsumerRecord<String, String> record) throws Exception {
        String logString = record.value();
        this.logger.debug("received msg: {}", logString);
        Map<String, Object> dataMap = GsonUtil.readValue(logString, Map.class);
        if (dataMap != null) {
            if (DataTypeConvert.calculatorType(dataMap, RecommendQuotaFields.skuClick.quota) == 1) {
                this.clickDataLog.logData(logString);
            }
            Map<String, String> bts = this.btsPlanInfoQuery.queryBtsInfo(dataMap);
            if (bts != null) {
                Map<String, Object> outJson = new HashMap<>();
                outJson.put("bts", bts);
                outJson.put(RecommendQuotaFields.specimen.recommendReportFields.name(), dataMap.get(RecommendQuotaFields.specimen.quota));
                for (RecommendQuotaFields reportFields : RecommendQuotaFields.values()) {
                    /*if (ValueType.value.equals(reportFields.valueType.name())) {
                        outJson.put(reportFields.recommendReportFields.name(), dataMap.get(reportFields.quota));
                    }*/
                    if (ValueType.num.name().equals(reportFields.valueType.name())) {

                        if (dataMap.get(reportFields.quota) != null && DataTypeConvert.calculatorType(dataMap, reportFields.quota) == 1) {
                            if ("is_exposure".equals(reportFields.quota)) {
                                outJson.put(reportFields.recommendReportFields.name(), DataTypeConvert.numToInt(dataMap.getOrDefault("exposure_count", 0)));
                            }else {
                                outJson.put(reportFields.recommendReportFields.name(), 1);
                            }
                        }else {
                            outJson.put(reportFields.recommendReportFields.name(), 0);
                        }

                    }

                }
                outJson.put(RecommendQuotaFields.userOrder.recommendReportFields.name(), 0);
                outJson.put(RecommendQuotaFields.paidOrder.recommendReportFields.name(), 0);
                outJson.put(RecommendQuotaFields.payAmount.recommendReportFields.name(), 0);

                outJson.put("timestamp", dataMap.get("glb_tm"));
                ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(logZafulOutTopic, JacksonUtil.toJSon(outJson));
                future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                    @Override
                    public void onSuccess(SendResult<String, String> result) {
                        logger.debug("log json with dimensions send success!");
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        logger.error("log json with dimensions send failed! msg: {}", outJson, ex);
                    }
                });
            }else {
                this.logger.debug("埋点中无 bts 实验信息：{}", dataMap.get("glb_ubcta"));
            }
        }
        this.countDownLatch1.countDown();
    }


}
