package com.globalegrow.report;

import com.globalegrow.util.AppLogConvertUtil;
import com.globalegrow.util.JacksonUtil;
import com.globalegrow.util.NginxLogConvertUtil;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.InterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ReportHandleRunnable implements Runnable {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected LogDataCache logDataCache;

    protected ReportBuildRule reportBuildRule;

    protected KafkaConsumer<String, String> consumer;

    protected KafkaProducer<String, String> kafkaProducer;

    protected String reportDataTopic;

    public static final String BAD_JSON_PATTERN = "\"([\\d]+_?)\":";

//    public static final String BAD_QUOTE_PATTERN = "([\"\"]){2,10}";
//
//    public static final String BAD_JSON_PATTERN2 = "\"([\\d]+_?)\":.([\\d]+_?)";
//    public static final String BAD_JSON_PATTERN3 = "\"([\\d]+_?)\":([\\d]+_?).([\\d]+_?)";

    /*public ReportHandleRunnable(ReportBuildRule reportBuildRule) {
        this.reportBuildRule = reportBuildRule;
        ReportKafkaConfig reportKafkaConfig = this.reportBuildRule.getReportFromKafka();
        Properties customerProperties = new Properties();
        customerProperties.put("bootstrap.servers", reportKafkaConfig.getBootstrapServers());
        customerProperties.put("group.id", reportKafkaConfig.getBootstrapGroupId());
        customerProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        customerProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumer = new KafkaConsumer<>(customerProperties);
        this.consumer.subscribe(Collections.singletonList(reportKafkaConfig.getDataSourceTopic()));

        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", reportKafkaConfig.getReportStrapServers());
        producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.kafkaProducer = new KafkaProducer<>(producerProperties);
        this.reportDataTopic = reportKafkaConfig.getReportDataTopic();
    }*/

    public ReportHandleRunnable(LogDataCache logDataCache, ReportBuildRule reportBuildRule) {
        this.logDataCache = logDataCache;
        this.reportBuildRule = reportBuildRule;

        ReportKafkaConfig reportKafkaConfig = this.reportBuildRule.getReportFromKafka();
        Properties customerProperties = new Properties();

        if (reportKafkaConfig.getFromStartOffset()) {
            customerProperties.put("auto.offset.reset", "earliest");
        }

        customerProperties.put("bootstrap.servers", reportKafkaConfig.getBootstrapServers());
        customerProperties.put("group.id", reportKafkaConfig.getBootstrapGroupId());
        customerProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        customerProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumer = new KafkaConsumer<>(customerProperties);
        this.consumer.subscribe(Collections.singletonList(reportKafkaConfig.getDataSourceTopic()));

        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", reportKafkaConfig.getReportStrapServers());
        producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.kafkaProducer = new KafkaProducer<>(producerProperties);
        this.reportDataTopic = reportKafkaConfig.getReportDataTopic();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

        try {
            kafkaCustomer: while (true) {
                try {
                    ConsumerRecords<String, String> records = consumer.poll(100);

                    customerEach:
                    for (ConsumerRecord<String, String> consumerRecord : records) {
                        String source = consumerRecord.value();

                      /*  if (!this.canCount(source)) {
                            continue customerEach;
                        }*/

                        List<Map<String, Object>> finalJsonMapList = this.finalJsonMapList(source);

                        for (Map<String, Object> finalJsonMap : finalJsonMapList) {

                            //Map<String, Object> finalJsonMap = this.finalJsonMap(source);

                            if (finalJsonMap == null || finalJsonMap.size() == 0) {
                                continue customerEach;
                            }

                            String value = JacksonUtil.toJSon(finalJsonMap);

                            ReadContext ctx  = JsonPath.parse(value);


                            if (this.reportBuildRule.getGlobaleFilter()) {
                                this.logger.debug("?????????????????????, ????????????: {}", this.reportBuildRule.getGlobaleJsonFilters());
                                List<JsonLogFilter> globaleFilter = this.reportBuildRule.getGlobaleJsonFilters();
                                for (JsonLogFilter filter : globaleFilter) {
                                    try {
                                        Object filterValue = ctx.read(filter.getJsonPath());
                                        if (filterValue == null) {
                                            continue customerEach;
                                        }
                                        if ("not_null".equals(filter.getFilterRule())) {

                                            if ((ctx.read(filter.getJsonPath()) == null) || StringUtils.isEmpty(String.valueOf(ctx.read(filter.getJsonPath(), Object.class)))) {
                                                continue customerEach;
                                            }

                                        }
                                        else if("equals".equals(filter.getFilterRule())){
                                            if (!String.valueOf(filterValue).equals(filter.getValueFilter())) {
                                                continue customerEach;
                                            }
                                        }

                                    } catch (Exception e) {
                                        if (e instanceof PathNotFoundException) {
                                            logger.debug("{} ??????????????????????????????,????????????????????????: {}, data: {}",
                                                    this.reportBuildRule.getReportName(), filter, source);
                                            continue customerEach;
                                        }
                                        logger.error("{} ??????????????????????????????: {}, data: {}", this.reportBuildRule.getReportName(), filter, source, e);
                                        continue customerEach;
                                    }
                                }

                            }


                            // ??????????????????
                            Map<String, Object> reportDefaultValues = new HashMap<>();
                            reportDefaultValues.putAll(this.reportBuildRule.getReportDefaultValues());
                            List<ReportQuotaFieldConfig> list = this.reportBuildRule.getReportQuotaFieldConfigs();
                            int hasValueQuotaCount = 0;
                            for (ReportQuotaFieldConfig quotaFieldConfig : list) {

                                try {
                                    ReportBaseQuotaValues baseQuotaValues = ReportBaseQuotaValues.valueOf(quotaFieldConfig.getValueEnum());
                                    Object quotaValue = baseQuotaValues.getReportValueFromSourceLog(quotaFieldConfig, ctx, value);

                                    if (quotaValue != null) {
                                        hasValueQuotaCount++;
                                        Map<String, Object> quota = new HashMap<>();

                                        this.logger.debug("{} ?????????????????? {}???????????? {}", this.reportBuildRule.getReportName(), quotaFieldConfig.getQuotaFieldName(), quotaValue);

                                        quota.put(quotaFieldConfig.getQuotaFieldName(), quotaValue);

                                        reportDefaultValues.putAll(quota);
                                        // ????????????????????? redis???key ??? userId + sku??????????????????string????????????????????????
                                        if (quotaFieldConfig.getCacheData() && finalJsonMap != null) {
                                            this.logger.debug("{} ???????????? {} ?????????", this.reportBuildRule.getReportName(), quotaFieldConfig.getQuotaFieldName());
                                            try {
                                                this.logDataCache.cacheData(this.reportBuildRule.getReportName(), finalJsonMap, quotaFieldConfig.getExpireSeconds());
                                            } catch (Exception e) {
                                                logger.warn("????????????????????????", e);
                                            }

                                        }

                                    }
                                } catch (Exception e) {
                                    this.logger.error("???????????? {} ????????????", quotaFieldConfig, e);
                                }

                            }

                            if (hasValueQuotaCount > 0) {

                                String reportJsonData = JacksonUtil.toJSon(reportDefaultValues);

                                this.logger.debug("{} ????????????????????????, ?????????????????? kafka ,json data: {}", this.reportBuildRule.getReportName(), reportJsonData);

                                ProducerRecord<String, String> producerRecord = new ProducerRecord<>(this.reportDataTopic, reportJsonData);

                                this.logger.debug("{} ?????????????????????????????????????????? topic : {}", this.reportBuildRule.getReportName(), this.reportDataTopic);

                                this.kafkaProducer.send(producerRecord);

                                this.logger.debug("{} ????????????????????????", this.reportBuildRule.getReportName());

                            } else {

                                this.logger.debug("???????????? {} ???????????????????????????", value);

                            }



                        }



                    }
                } catch (Exception e) {
                    if (e instanceof InterruptException) {
                        this.logger.info("?????? {} ????????????", this.reportBuildRule.getReportName());
                        break kafkaCustomer;
                    }
                    this.logger.error("?????? {} ???????????? error", this.reportBuildRule.getReportName(), e);

                }

            }
        } finally {
            this.consumer.close();
        }
    }

    List<Map<String, Object>> finalJsonMapList(String source) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> finalJsonMap = this.finalJsonMap(source);

        if (finalJsonMap != null && finalJsonMap.size() > 0) {

            if ("BTS".equals(finalJsonMap.get("glb_x"))) {

                if (finalJsonMap.get("glb_skuinfo") instanceof List) {

                    List skuInfos = (List) finalJsonMap.get("glb_skuinfo");

                    skuInfos.forEach(lo -> {
                        Map<String, Object> logMap = new HashMap<>();

                        logMap.putAll(finalJsonMap);
                        logMap.put("glb_skuinfo", lo);

                        list.add(logMap);

                    });

                }else {

                    list.add(finalJsonMap);

                }




            }else {

                list.add(finalJsonMap);

            }

        }

        return flatBtsArray(list);
    }

    private List<Map<String, Object>> flatBtsArray(List<Map<String, Object>> list){
        List<Map<String, Object>> flatBts = new ArrayList<>();

        for (Map<String, Object> map : list) {
            Object o = map.get("glb_bts");

            if (o != null) {

                if (o instanceof List) {
                    List btss = (List)o;

                    for (Object bts : btss) {
                        Map<String, Object> mspBts = new HashMap<>();
                        mspBts.putAll(map);
                        mspBts.put("glb_bts", bts);
                        flatBts.add(mspBts);
                    }

                }

            }

        }

        if (flatBts.size() == 0) {
            return list;
        }

        return flatBts;
    }


    public Map<String, Object> finalJsonMap(String source) throws Exception {
        Map<String, Object> sourceMap = null;
        if (this.reportBuildRule.getReportName().endsWith(LogDataRedisCache.APP_REPORT_END_FLAG)) {
            sourceMap = AppLogConvertUtil.getAppLogParameters(source);
        }else{
            sourceMap = NginxLogConvertUtil.getNginxLogParameters(source);
        }
        Map<String, Object> finalMap = new HashMap<>();

        if (sourceMap != null) {

            sourceMap.entrySet().stream().forEach(e -> {
                String value = String.valueOf(e.getValue());
                if (value.startsWith("{")) {
                    try {
                        if (!value.contains(":") && !"{}".equals(value)) {
                            logger.warn("{} ???????????????????????? key: {} {}, source: {}", this.reportBuildRule.getReportName(),
                                    e.getKey(),
                                    value, source);
                        } else {
                            finalMap.put(e.getKey(), JacksonUtil.readValue(value, Map.class));
                        }
                    } catch (Exception e1) {
                        logger.error("{} ?????????????????? map ????????????source: {} map: {}", this.reportBuildRule.getReportName(), source, value, e);
                    }
                } else if (value.startsWith("[") && !value.startsWith("[ETA]") && value.endsWith("]")) {
                    try {
                        finalMap.put(e.getKey(), JacksonUtil.readValue(value, List.class));
                    } catch (Exception e1) {
                        logger.error("{} ?????????????????? list ??????????????? {}", this.reportBuildRule.getReportName(), value, e);
                    }
                } else {
                    finalMap.put(e.getKey(), value);
                }
            });

        }

        return finalMap;
    }

/*
    public String finalJsonLog(String source) throws Exception {
        return JacksonUtil.toJSon(this.finalJsonMap(source));
    }
*/

}
