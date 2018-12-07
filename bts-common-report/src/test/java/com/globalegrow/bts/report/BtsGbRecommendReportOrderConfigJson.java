package com.globalegrow.bts.report;

import com.globalegrow.bts.model.BtsGbRecommendReport;
import com.globalegrow.report.JsonLogFilter;
import com.globalegrow.report.ReportBuildRule;
import com.globalegrow.report.ReportKafkaConfig;
import com.globalegrow.report.ReportQuotaFieldConfig;
import com.globalegrow.util.DyBeanUtils;
import com.globalegrow.util.JacksonUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BtsGbRecommendReportOrderConfigJson {

    private Map<String, String> bts = new HashMap() {

        private static final long serialVersionUID = 3456809019097454769L;

        {
            put("planid", "_skip");
            put("versionid", "_skip");
            put("bucketid", "_skip");
        }
    };

    @Test
    public void generate() throws Exception {
        ReportBuildRule rule = new ReportBuildRule();
        rule.setGlobaleFilter(true);

        List<JsonLogFilter> globaleFilters = new ArrayList<>();

        JsonLogFilter btsFilter = new JsonLogFilter();
        btsFilter.setJsonPath("$.glb_bts");
        btsFilter.setFilterRule("not_null");
        globaleFilters.add(btsFilter);

        JsonLogFilter siteFilter = new JsonLogFilter();
        siteFilter.setJsonPath("$.glb_d");
        siteFilter.setValueFilter("10002");
        globaleFilters.add(siteFilter);

        JsonLogFilter reportFilter = new JsonLogFilter();
        reportFilter.setJsonPath("$.db_order_info.report_name");
        reportFilter.setValueFilter("BTS_GB_ORDER_RECOMMEND_REPORT");

        globaleFilters.add(reportFilter);

        rule.setGlobaleJsonFilters(globaleFilters);

        rule.setReportName("BTS_GB_RECOMMEND_REPORT_ORDER");
        rule.setDescription("GB 推荐位报表指标配置");
        BtsGbRecommendReport btsGbRecommendReport = new BtsGbRecommendReport();
        btsGbRecommendReport.setBts(bts);
        rule.setReportDefaultValues(DyBeanUtils.objToMap(btsGbRecommendReport));


        ReportKafkaConfig reportKafkaConfig = new ReportKafkaConfig();
        reportKafkaConfig.setBootstrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setDataSourceTopic("dy_log_cart_order_info");
        reportKafkaConfig.setBootstrapGroupId("dy_bts_gb_gd_rec_report_order");
        reportKafkaConfig.setReportStrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setReportDataTopic("dy_bts_gb_gd_rec_report");

        rule.setReportFromKafka(reportKafkaConfig);


        List<ReportQuotaFieldConfig> reportQuotaFieldConfigs = new ArrayList<>();

        quota(reportQuotaFieldConfigs);

        rule.setReportQuotaFieldConfigs(reportQuotaFieldConfigs);

        System.out.println(JacksonUtil.toJSon(rule));
    }

    private void quota(List<ReportQuotaFieldConfig> reportQuotaFieldConfigs) {

        // 生单 sku 数
        ReportQuotaFieldConfig orderSkuNum = new ReportQuotaFieldConfig();
        orderSkuNum.setQuotaFieldName("skuOrder");
        orderSkuNum.setDefaultValue(0);
        orderSkuNum.setValueEnum("countOneWithFilter");

        List<JsonLogFilter> orderSkuNumFilters = new ArrayList<>();
        JsonLogFilter orderStatus0 = new JsonLogFilter();
        // 订单状态为 0 算下单
        orderStatus0.setJsonPath("$.db_order_info.order_status");
        orderStatus0.setValueFilter("0");
        orderSkuNumFilters.add(orderStatus0);
        // 只取订单商品数据
        JsonLogFilter orderFalse = new JsonLogFilter();
        orderFalse.setJsonPath("$.db_order_info.order_data");
        orderFalse.setFilterRule("false");
        orderSkuNumFilters.add(orderFalse);


        orderSkuNum.setJsonLogFilters(orderSkuNumFilters);
        reportQuotaFieldConfigs.add(orderSkuNum);

        // 付款 sku 数
        ReportQuotaFieldConfig paidOrderSkuNum = new ReportQuotaFieldConfig();
        paidOrderSkuNum.setQuotaFieldName("skuOrderPaid");
        paidOrderSkuNum.setDefaultValue(0);
        paidOrderSkuNum.setValueEnum("countOneWithFilter");

        List<JsonLogFilter> payGoodsFilter = new ArrayList<>();
        payGoodsFilter.add(orderFalse);

        JsonLogFilter status18 = new JsonLogFilter();
        status18.setJsonPath("$.db_order_info.order_status");
        status18.setValueFilter("1,3");
        status18.setFilterRule("or");

        payGoodsFilter.add(status18);

        paidOrderSkuNum.setJsonLogFilters(payGoodsFilter);
        reportQuotaFieldConfigs.add(paidOrderSkuNum);

        // 付款 GMV
        ReportQuotaFieldConfig amount = new ReportQuotaFieldConfig();
        amount.setQuotaFieldName("amount");
        amount.setDefaultValue(0);
        amount.setExtractValueJsonPath("$.db_order_info.gmv");
        amount.setValueEnum("quotaIntValueExtractFromLog");

        amount.setJsonLogFilters(payGoodsFilter);

        reportQuotaFieldConfigs.add(amount);


        // bts 字段 extractBtsMapValueFromLog
        ReportQuotaFieldConfig btsQuota = new ReportQuotaFieldConfig();
        btsQuota.setQuotaFieldName("bts");
        btsQuota.setDefaultValue(bts);
        btsQuota.setExtractValueJsonPath("$.glb_bts");
        btsQuota.setValueEnum("extractBtsMapValueFromLog");

        reportQuotaFieldConfigs.add(btsQuota);

        // 时间戳字段
        ReportQuotaFieldConfig timestamp = new ReportQuotaFieldConfig();
        timestamp.setQuotaFieldName("timestamp");
        timestamp.setDefaultValue(0);
        timestamp.setExtractValueJsonPath("$.timestamp");
        timestamp.setValueEnum("quotaLongValueExtractFromLog");

        reportQuotaFieldConfigs.add(timestamp);

    }


}
