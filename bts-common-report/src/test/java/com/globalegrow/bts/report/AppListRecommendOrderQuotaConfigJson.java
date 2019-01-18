package com.globalegrow.bts.report;

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

/**
 * zaful app 分类页规则2019-01-17
 */
public class AppListRecommendOrderQuotaConfigJson {

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
        btsFilter.setJsonPath("$.event_value.af_plan_id");
        btsFilter.setFilterRule("not_null");

        JsonLogFilter btsFilter2 = new JsonLogFilter();
        btsFilter2.setJsonPath("$.event_value.af_version_id");
        btsFilter2.setFilterRule("not_null");
        globaleFilters.add(btsFilter2);

        JsonLogFilter btsFilter3 = new JsonLogFilter();
        btsFilter3.setJsonPath("$.event_value.af_bucket_id");
        btsFilter3.setFilterRule("not_null");
        globaleFilters.add(btsFilter3);

        JsonLogFilter btsFilter4 = new JsonLogFilter();
        btsFilter4.setJsonPath("$.event_value.af_inner_mediasource");
        btsFilter4.setValueFilter("category_");
        btsFilter4.setFilterRule("contains");
        globaleFilters.add(btsFilter4);

        JsonLogFilter btsFilter5 = new JsonLogFilter();
        btsFilter5.setJsonPath("$.event_value.af_sort");
        btsFilter5.setValueFilter("recommend");
        globaleFilters.add(btsFilter5);

        JsonLogFilter cartFilter = new JsonLogFilter();
        cartFilter.setJsonPath("$.event_name");
        cartFilter.setValueFilter("af_add_to_bag");
        JsonLogFilter reportFilter = new JsonLogFilter();
        reportFilter.setJsonPath("$.db_order_info.report_name");
        reportFilter.setValueFilter("BTS_ZAFUL_ORDER_LIST_RECOMMEND_APP");

        globaleFilters.add(reportFilter);
        globaleFilters.add(cartFilter);
        globaleFilters.add(btsFilter);

        rule.setGlobaleJsonFilters(globaleFilters);

        rule.setReportName("BTS_ZAFUL_ORDER_LIST2_RECOMMEND_APP");
        rule.setDescription("zaful APP 分类页 推荐报表");

        // 默认值
        BtsAppListRecommendReportQuota searchRecommendReportQuotaModel = new BtsAppListRecommendReportQuota();
        searchRecommendReportQuotaModel.setBts(bts);
        rule.setReportDefaultValues(DyBeanUtils.objToMap(searchRecommendReportQuotaModel));


        ReportKafkaConfig reportKafkaConfig = new ReportKafkaConfig();
        reportKafkaConfig.setBootstrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setDataSourceTopic("dy_log_cart_order_info");
        reportKafkaConfig.setBootstrapGroupId("dy_bts_app_list_recommend_report_order");
        reportKafkaConfig.setReportStrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setReportDataTopic("dy_bts_app_list_recommend_report");

        rule.setReportFromKafka(reportKafkaConfig);


        List<ReportQuotaFieldConfig> reportQuotaFieldConfigs = new ArrayList<>();

        quota(reportQuotaFieldConfigs);

        rule.setReportQuotaFieldConfigs(reportQuotaFieldConfigs);

        System.out.println(JacksonUtil.toJSon(rule));
    }

    private void quota(List<ReportQuotaFieldConfig> reportQuotaFieldConfigs) {

        // 下单数 order  生单金额 order_amount 生单用户数 order_uv whole_order_uv 整体下单金额 whole_order_amount // order_sku
        List<JsonLogFilter> orderFilters = new ArrayList<>();
        JsonLogFilter orderStatus0 = new JsonLogFilter();
        // 订单状态为 0 算下单
        orderStatus0.setJsonPath("$.db_order_info.order_status");
        orderStatus0.setValueFilter("0");
        orderFilters.add(orderStatus0);
        // 只取订单商品数据
        JsonLogFilter orderFalse = new JsonLogFilter();
        orderFalse.setJsonPath("$.db_order_info.order_data");
        orderFalse.setFilterRule("false");
        orderFilters.add(orderFalse);
        //下单商品数
        ReportQuotaFieldConfig createOrder_order_sku = new ReportQuotaFieldConfig();
        createOrder_order_sku.setQuotaFieldName("order_sku");
        createOrder_order_sku.setDefaultValue(0);
        createOrder_order_sku.setExtractValueJsonPath("$.db_order_info.goods_num");
        createOrder_order_sku.setValueEnum("quotaIntValueExtractFromLog");
        createOrder_order_sku.setJsonLogFilters(orderFilters);
        reportQuotaFieldConfigs.add(createOrder_order_sku);

        // 下单金额
        ReportQuotaFieldConfig orderAmount = new ReportQuotaFieldConfig();
        orderAmount.setQuotaFieldName("order_amount");
        orderAmount.setDefaultValue(0);
        orderAmount.setExtractValueJsonPath("$.db_order_info.gmv");
        orderAmount.setValueEnum("quotaIntValueExtractFromLog");
        orderAmount.setJsonLogFilters(orderFilters);
        reportQuotaFieldConfigs.add(orderAmount);


        //下单客户数
        ReportQuotaFieldConfig orderUv = new ReportQuotaFieldConfig();
        orderUv.setQuotaFieldName("order_uv");
        orderUv.setDefaultValue("_skip");
        orderUv.setExtractValueJsonPath("$.appsflyer_device_id");
        orderUv.setValueEnum("quotaStringValueExtractFromLog");
        orderUv.setJsonLogFilters(orderFilters);
        reportQuotaFieldConfigs.add(orderUv);


        List<JsonLogFilter> paidOrderFilters = new ArrayList<>();
        paidOrderFilters.add(orderFalse);

        JsonLogFilter status18 = new JsonLogFilter();
        status18.setJsonPath("$.db_order_info.order_status");
        status18.setValueFilter("1,8");
        status18.setFilterRule("or");

        paidOrderFilters.add(status18);

        //付款客户数
        ReportQuotaFieldConfig paid_uvorderUv = new ReportQuotaFieldConfig();
        paid_uvorderUv.setQuotaFieldName("paid_uv");
        paid_uvorderUv.setDefaultValue("_skip");
        paid_uvorderUv.setExtractValueJsonPath("$.appsflyer_device_id");
        paid_uvorderUv.setValueEnum("quotaStringValueExtractFromLog");
        paid_uvorderUv.setJsonLogFilters(paidOrderFilters);
        reportQuotaFieldConfigs.add(paid_uvorderUv);

        //付款金额
        ReportQuotaFieldConfig amount = new ReportQuotaFieldConfig();
        amount.setQuotaFieldName("amount");
        amount.setDefaultValue(0);
        amount.setExtractValueJsonPath("$.db_order_info.gmv");
        amount.setValueEnum("quotaIntValueExtractFromLog");
        amount.setJsonLogFilters(paidOrderFilters);
        reportQuotaFieldConfigs.add(amount);

        //付款商品数
        ReportQuotaFieldConfig sales_amount = new ReportQuotaFieldConfig();
        sales_amount.setQuotaFieldName("sales_amount");
        sales_amount.setDefaultValue(0);
        sales_amount.setExtractValueJsonPath("$.db_order_info.goods_num");
        sales_amount.setValueEnum("quotaIntValueExtractFromLog");
        sales_amount.setJsonLogFilters(paidOrderFilters);
        reportQuotaFieldConfigs.add(sales_amount);

        // bts 3 个字段
        ReportQuotaFieldConfig btsQuota = new ReportQuotaFieldConfig();
        btsQuota.setQuotaFieldName("bts");
        btsQuota.setDefaultValue(bts);
        btsQuota.setValueEnum("extractAppBtsValueFromLog");

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
