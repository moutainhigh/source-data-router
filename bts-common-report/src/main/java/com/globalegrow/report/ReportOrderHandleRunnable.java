package com.globalegrow.report;

import com.globalegrow.util.JacksonUtil;
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

public class ReportOrderHandleRunnable implements Runnable {
    protected final Logger logger = LoggerFactory.getLogger(getClass());


    protected ReportBuildRule reportBuildRule;

    protected KafkaConsumer<String, String> consumer;

    protected KafkaProducer<String, String> kafkaProducer;

    protected String reportDataTopic;

    public ReportOrderHandleRunnable(ReportBuildRule reportBuildRule) {
        this.reportBuildRule = reportBuildRule;

        ReportKafkaConfig reportKafkaConfig = this.reportBuildRule.getReportFromKafka();
        Properties customerProperties = new Properties();

        if (reportKafkaConfig.getFromStartOffset()) {
            this.logger.debug("{} 报表从头消费", this.reportBuildRule.getReportName());
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
        this.logger.debug("{} 订单报表开始构建, 订单主题: {}", this.reportBuildRule.getReportName(),  this.reportDataTopic);
        try {
            kafkaCustomer: while (true) {
                try {
                    ConsumerRecords<String, String> records = consumer.poll(100);

                    customerEach:
                    for (ConsumerRecord<String, String> consumerRecord : records) {
                        String source = consumerRecord.value();
                        this.logger.debug("{} 报表消息: {}", this.reportBuildRule.getReportName(), source);
                        ReadContext ctx = JsonPath.parse(source);

                        if (this.reportBuildRule.getGlobaleFilter()) {
                            this.logger.debug("已开启全局过滤, 过滤规则: {}", this.reportBuildRule.getGlobaleJsonFilters());
                            List<JsonLogFilter> globaleFilter = this.reportBuildRule.getGlobaleJsonFilters();
                            for (JsonLogFilter filter : globaleFilter) {
                                try {
                                    this.logger.debug("{} 订单指标全局过滤", this.reportBuildRule.getReportName());
                                    Object filterValue = ctx.read(filter.getJsonPath());
                                    if (filterValue == null) {
                                        continue customerEach;
                                    }
                                    if ("not_null".equals(filter.getFilterRule())) {

                                        if ((ctx.read(filter.getJsonPath()) == null) || StringUtils.isEmpty(String.valueOf(ctx.read(filter.getJsonPath(), Object.class)))) {
                                            this.logger.debug("{} 订单指标全局过滤", this.reportBuildRule.getReportName());
                                            continue customerEach;
                                        }

                                    }else if("equals".equals(filter.getFilterRule())){
                                        if (!String.valueOf(filterValue).equals(filter.getValueFilter())) {

                                            continue customerEach;
                                        }
                                    }

                                } catch (Exception e) {
                                    if (e instanceof PathNotFoundException) {
                                        logger.debug("{} 全局条件过滤处理异常,过滤条件字段不存: {}, data: {}",
                                                this.reportBuildRule.getReportName(), filter, source);
                                        continue customerEach;
                                    }
                                    logger.error("{} 全局条件过滤处理异常: {}, data: {}", this.reportBuildRule.getReportName(), filter, source, e);
                                    continue customerEach;
                                }
                            }

                        }

                        this.logger.debug("{} 开始计算订单指标", this.reportBuildRule.getReportName());
                        // 报表指标处理
                        Map<String, Object> reportDefaultValues = new HashMap<>();
                        reportDefaultValues.putAll(this.reportBuildRule.getReportDefaultValues());
                        List<ReportQuotaFieldConfig> list = this.reportBuildRule.getReportQuotaFieldConfigs();
                        int hasValueQuotaCount = 0;
                        for (ReportQuotaFieldConfig quotaFieldConfig : list) {

                            try {
                                ReportBaseQuotaValues baseQuotaValues = ReportBaseQuotaValues.valueOf(quotaFieldConfig.getValueEnum());
                                Object quotaValue = baseQuotaValues.getReportValueFromSourceLog(quotaFieldConfig, ctx, source);

                                if (quotaValue != null) {
                                    hasValueQuotaCount++;
                                    Map<String, Object> quota = new HashMap<>();

                                    this.logger.debug("{} 处理报表指标 {}，指标值 {}", this.reportBuildRule.getReportName(), quotaFieldConfig.getQuotaFieldName(), quotaValue);

                                    quota.put(quotaFieldConfig.getQuotaFieldName(), quotaValue);

                                    reportDefaultValues.putAll(quota);

                                }
                            } catch (Exception e) {
                                this.logger.error("报表指标 {} 处理异常", quotaFieldConfig, e);
                            }

                        }

                        if (hasValueQuotaCount > 0) {

                            String reportJsonData = JacksonUtil.toJSon(reportDefaultValues);

                            this.logger.debug("{} 报表指标处理完毕, 将指标发送到 kafka ,json data: {}", this.reportBuildRule.getReportName(), reportJsonData);

                            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(this.reportDataTopic, reportJsonData);

                            this.logger.debug("{} 开始发送报表处理后数据到目标 topic : {}", this.reportBuildRule.getReportName(), this.reportDataTopic);

                            this.kafkaProducer.send(producerRecord);

                            this.logger.debug("{} 报表数据发送完成", this.reportBuildRule.getReportName());

                        } else {

                            this.logger.debug("埋点数据 {} 未找到需计算的指标", source);

                        }

                    }
                } catch (Exception e) {
                    if (e instanceof InterruptException) {
                        this.logger.info("报表 {} 构建终止", this.reportBuildRule.getReportName());
                        break kafkaCustomer;
                    }
                    this.logger.error("报表 {} 数据处理 error", this.reportBuildRule.getReportName(), e);
                }

            }
        } finally {
            this.consumer.close();
        }
    }

}
