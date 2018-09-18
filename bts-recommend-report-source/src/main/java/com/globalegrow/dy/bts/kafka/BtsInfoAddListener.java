package com.globalegrow.dy.bts.kafka;

import com.globalegrow.bts.BtsPlanInfoQuery;
import com.globalegrow.bts.RecommendTypeUtil;
import com.globalegrow.util.GsonUtil;
import com.globalegrow.util.JacksonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Component
@ConfigurationProperties(prefix = "app.kafka")
public class BtsInfoAddListener {

    private String logJsonTopic;

    private String logOutputTopic;

    @Autowired
    private BtsPlanInfoQuery btsPlanInfoQuery;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    public final CountDownLatch countDownLatch1 = new CountDownLatch(1);

   public enum filedKeys {
        glb_mrlc, glb_fmd, glb_plf, glb_b, glb_od, bts
    }

    public enum products {
        ZF
    }

    // {"fmd":"mr_T_3","versionid":"23","bucketid":"5","planid":"9"}
    public  enum zafulBts{
        versionid, bucketid, planid
    }
    public enum bts {
        bucket_id, plan_id, version_id
    }

    @KafkaListener(topics = {"${app.kafka.log-json-topic}"}, groupId = "bts-log-dimensions_zafu_recommend")
    public void listen(ConsumerRecord<String, String> record) {

        String mysqlBinLog = record.value();
        this.logger.debug("mysql event: {}", mysqlBinLog);
        try {
            Map<String, Object> dataMap = JacksonUtil.readValue(mysqlBinLog, Map.class);
            Map<String, String> bts = this.btsPlanInfoQuery.queryBtsInfo(dataMap);
            String recommendType = this.getRecommendType(dataMap);
            String cookie = String.valueOf(dataMap.get(filedKeys.glb_od.name()));


            /*if (bts == null) {
                if (StringUtils.isNotEmpty(recommendType)) {
                    this.logger.debug("埋点中无 bts 信息，根据 cookie 推荐位从 redis 中查询");
                    this.logger.debug("推荐位：{}", recommendType);
                    bts = this.btsPlanInfoQuery.queryBtsInfo(cookie, products.ZF.name(), recommendType);
                }
            }*/

            if (bts != null) {
                dataMap.put(filedKeys.bts.name(), bts);
                this.send(this.logOutputTopic, JacksonUtil.toJSon(dataMap));
            } else {
                this.logger.warn("未找到 bts 绑定信息, cookie: {},recommendType: {}, calculator type: {}",  cookie, recommendType, CalculatorTypeUtil.getCalculatorType(dataMap));
            }
        } catch (Exception e) {
            this.logger.error("mysql binlog 处理异常 log：{}", mysqlBinLog, e);
        }
        this.countDownLatch1.countDown();

    }

    private String getRecommendType(Map<String, Object> dataMap) {
        String fmd = String.valueOf(dataMap.get(filedKeys.glb_fmd.name()));
        String mrlc = String.valueOf(dataMap.get(filedKeys.glb_mrlc.name()));
        this.logger.debug("推荐位信息 fmd: {}, mrlc: {}", fmd, mrlc);
        String recommendType = "";
        if (StringUtils.isNotEmpty(fmd)) {
            recommendType =  RecommendTypeUtil.getRecommendTypeByFmd(fmd);
        }
        if (StringUtils.isEmpty(recommendType) && StringUtils.isNotEmpty(mrlc)) {
            recommendType = RecommendTypeUtil.getRecommendTypeByMrlc(mrlc);
        }
        if (StringUtils.isEmpty(recommendType)){
            String plf = String.valueOf(dataMap.get(filedKeys.glb_plf.name()));
            String b = String.valueOf(dataMap.get(filedKeys.glb_b.name()));
            return RecommendTypeUtil.getRecommendTypeByPlfAndB(plf, b);
        }
        return recommendType;
    }

    private Map<String, String> getBtsInfoFromUbcta(String ubcta) {
        this.logger.debug("uncta 信息: {}", ubcta);
        if (ubcta.startsWith("[")) {
            List<Map<String, String>> mapList = GsonUtil.readValue(ubcta, List.class);
            if (mapList.size() > 0) {
                return this.buildBtsMap(mapList.get(0));
            }
        }
        if (ubcta.startsWith("{")) {
            return this.buildBtsMap(GsonUtil.readValue(ubcta, Map.class));
        }
        return null;
    }

    private Map<String, String> buildBtsMap(Map<String, String> ubMap) {
        this.logger.debug("埋点中的 ubcta 信息: {}", ubMap);
        String planid = ubMap.get(zafulBts.planid.name());
        String versionId = ubMap.get(zafulBts.versionid.name());
        String bucketid = ubMap.get(zafulBts.bucketid.name());
        if (StringUtils.isNotEmpty(planid) && StringUtils.isNotEmpty(versionId) && StringUtils.isNotEmpty(bucketid)) {
            Map<String, String> btsMap = new HashMap<>();
            btsMap.put(bts.plan_id.name(), planid);
            btsMap.put(bts.version_id.name(), versionId);
            btsMap.put(bts.bucket_id.name(), bucketid);
            return btsMap;
        }
        return null;
    }

    private void send(String topic, String json) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, json);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                logger.debug("log json with dimensions send success!");
            }

            @Override
            public void onFailure(Throwable ex) {
                logger.error("log json with dimensions send failed! msg: {}", json, ex);
            }
        });
    }


    public String getLogJsonTopic() {
        return logJsonTopic;
    }

    public void setLogJsonTopic(String logJsonTopic) {
        this.logJsonTopic = logJsonTopic;
    }

    public String getLogOutputTopic() {
        return logOutputTopic;
    }

    public void setLogOutputTopic(String logOutputTopic) {
        this.logOutputTopic = logOutputTopic;
    }
}
