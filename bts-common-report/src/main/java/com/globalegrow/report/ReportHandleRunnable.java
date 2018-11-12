package com.globalegrow.report;

import com.globalegrow.util.JacksonUtil;
import com.globalegrow.util.NginxLogConvertUtil;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportHandleRunnable implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ReportBuildRule reportBuildRule;

    private KafkaConsumer<String, String> consumer;

    private KafkaProducer<String, String> kafkaProducer;

    private String reportDataTopic;

    public static final String BAD_JSON_PATTERN = "\"([\\d]+_?)\":";

    public static final String BAD_QUOTE_PATTERN = "([\"\"]){2,10}";

    public static final String BAD_JSON_PATTERN2 = "\"([\\d]+_?)\":.([\\d]+_?)";
    public static final String BAD_JSON_PATTERN3 = "\"([\\d]+_?)\":([\\d]+_?).([\\d]+_?)";

    public ReportHandleRunnable(ReportBuildRule reportBuildRule) {
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
            while (true) {
                try {
                    ConsumerRecords<String, String> records = consumer.poll(100);

                    customerEach:
                    for (ConsumerRecord<String, String> consumerRecord : records) {
                        boolean countReport = true;

                        String source = consumerRecord.value();
                        if (!source.contains("_ubc.gif")) {
                            continue customerEach;
                        }

                        String value = this.finalJsonLog(source);

                        ReadContext ctx = null;
                        if (countReport) {
                            try {
                                ctx = JsonPath.parse(value);
                            } catch (Exception e) {
                                this.logger.error("{} 报表数据转换失败, log: {}, after: {}", this.reportBuildRule.getReportName(), source, value, e);
                                continue customerEach;
                            }
                        }


                        if (this.reportBuildRule.getGlobaleFilter()) {
                            this.logger.debug("已开启全局过滤, 过滤规则: {}", this.reportBuildRule.getGlobaleJsonFilters());
                            List<JsonLogFilter> globaleFilter = this.reportBuildRule.getGlobaleJsonFilters();
                            for (JsonLogFilter filter : globaleFilter) {
                                try {
                                    Object filterValue = ctx.read(filter.getJsonPath());
                                    if (filterValue == null) {
                                        continue customerEach;
                                    }
                                    if ("not_null".equals(filter.getValueFilter())) {

                                        if ((ctx.read(filter.getJsonPath()) == null) || StringUtils.isEmpty(ctx.read(filter.getJsonPath(), String.class))) {
                                            continue customerEach;
                                        }
                                    }
                                    if (!String.valueOf(filterValue).equals(filter.getValueFilter())) {
                                        continue customerEach;
                                    }
                                } catch (Exception e) {
                                    logger.error("{} 全局条件过滤处理异常: {}, data: {}", this.reportBuildRule.getReportName(), filter, value, e);
                                    continue customerEach;
                                }
                            }

                        }


                        // 报表指标处理
                        Map<String, Object> reportDefaultValues = new HashMap<>();
                        reportDefaultValues.putAll(this.reportBuildRule.getReportDefaultValues());
                        List<ReportQuotaFieldConfig> list = this.reportBuildRule.getReportQuotaFieldConfigs();

                        for (ReportQuotaFieldConfig quotaFieldConfig : list) {

                            try {
                                ReportBaseQuotaValues baseQuotaValues = ReportBaseQuotaValues.valueOf(quotaFieldConfig.getValueEnum());
                                Object quotaValue = baseQuotaValues.getReportValueFromSourceLog(quotaFieldConfig, ctx, value);

                                if (quotaValue != null) {

                                    Map<String, Object> quota = new HashMap<>();

                                    this.logger.debug("{} 处理报表指标 {}，指标值 {}", this.reportBuildRule.getReportName(), quotaFieldConfig.getQuotaFieldName(), quotaValue);

                                    quota.put(quotaFieldConfig.getQuotaFieldName(), quotaValue);

                                    reportDefaultValues.putAll(quota);
                                    // 加购事件缓存至 redis，key 为 userId + sku，数据结构，string，前缀为报表名称
                                    if (quotaFieldConfig.getCacheData()) {
                                        this.logger.debug("{} 报表指标 {} 将缓存", this.reportBuildRule.getReportName(), quotaFieldConfig.getQuotaFieldName());

                                    }

                                }
                            } catch (Exception e) {
                                this.logger.error("报表指标 {} 处理异常", quotaFieldConfig, e);
                            }

                        }

                        String reportJsonData = JacksonUtil.toJSon(reportDefaultValues);

                        this.logger.debug("{} 报表指标处理完毕, 将指标发送到 kafka ,json data: {}", this.reportBuildRule.getReportName(), reportJsonData);

                        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(this.reportDataTopic, reportJsonData);

                        this.logger.debug("{} 开始发送报表处理后数据到目标 topic : {}", this.reportBuildRule.getReportName(), this.reportDataTopic);

                        this.kafkaProducer.send(producerRecord);

                        this.logger.debug("{} 报表数据发送完成", this.reportBuildRule.getReportName());


                    }
                } catch (Exception e) {
                    this.logger.error("报表 {} 数据处理 error", this.reportBuildRule.getReportName(), e);
                }

            }
        } finally {
            this.consumer.close();
            ;
        }
    }

    private String finalJsonLog(String source) throws Exception {
        Map<String, Object> sourceMap = NginxLogConvertUtil.getNginxLogParameters(source);;
        Map<String,Object> finalMap = new HashMap<>();
        sourceMap.entrySet().stream().forEach(e -> {
            String value = String.valueOf(e.getValue());
            if (value.startsWith("{")) {
                try {
                    if (!value.contains(":")) {
                        logger.warn("{} 报表异常埋点数据 {}", this.reportBuildRule.getReportName(), value);
                    }else {
                        finalMap.put(e.getKey(), JacksonUtil.readValue(value, Map.class));
                    }
                } catch (Exception e1) {
                    logger.error("{} 报表埋点字段 map 转换失败source: {} map: {}", this.reportBuildRule.getReportName(), source, value, e);
                }
            } else if (value.startsWith("[") && !value.startsWith("[ETA]") && value.endsWith("]")) {
                try {
                    finalMap.put(e.getKey(), JacksonUtil.readValue(value, List.class));
                } catch (Exception e1) {
                    logger.error("{} 报表埋点字段 list 转换失败： {}", this.reportBuildRule.getReportName(), value, e);
                }
            }else {
                finalMap.put(e.getKey(), value);
            }
        });

        return JacksonUtil.toJSon(finalMap);
    }

    private String jsonPreHandle(String source) {
        String value = source.replaceAll("HTTP\"}", "").replaceAll("\"'", "\"").replaceAll("'\"", "\"")
                .replaceAll("'at'", "\"at\"")/*.replaceAll("HTTP\\\"", "").replaceAll("\"\\{", "{").replaceAll("}\"", "}")
                .replaceAll("\"\\[", "[").replaceAll("]\"", "]").replaceAll(" ", "")
                .replaceAll("\\\\", "")*/;

        Pattern p0 = Pattern.compile(BAD_JSON_PATTERN2);
        Matcher m0 = p0.matcher(value);
        List<String> key0 = new ArrayList<>();
        while (m0.find()) {
            String badName = m0.group();
            key0.add(badName);
        }
        System.out.println(key0);
        for (String badName : key0) {
            value = value.replaceAll(badName, ":" +  badName.split(":")[0].replaceAll("\"","") + badName.split(":")[1]);
        }

        Pattern p1 = Pattern.compile(BAD_JSON_PATTERN3);
        Matcher m1 = p1.matcher(value);
        List<String> key1 = new ArrayList<>();
        while (m1.find()) {
            String badName = m1.group();
            key1.add(badName);
        }
        System.out.println(key1);
        for (String badName : key1) {
            value = value.replaceAll(badName, ":" +  badName.split(":")[0].replaceAll("\"","") + badName.split(":")[1]);
        }

        Pattern p = Pattern.compile(BAD_JSON_PATTERN);
        Matcher m = p.matcher(value);
        List<String> key = new ArrayList<>();
        while (m.find()) {
            String badName = m.group();
            key.add(badName);
        }
        for (String badName : key) {
            value = value.replaceAll(badName, ":" + badName.replaceAll(":", ""));
        }

        return value;
    }

    private String handleBadQuoteJson(String json) {
        String badJson = json;
        Pattern p = Pattern.compile(BAD_QUOTE_PATTERN);
        Matcher m = p.matcher(badJson);
        List<String> key = new ArrayList<>();
        while (m.find()) {
            String badName = m.group();
            key.add(badName);
        }
        System.out.println(key);
        for (String badName : key) {
            badJson = badJson.replaceAll(badName, "\"");
        }
        return badJson;
    }
}
