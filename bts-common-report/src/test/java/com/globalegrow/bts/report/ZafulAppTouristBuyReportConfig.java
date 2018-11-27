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

public class ZafulAppTouristBuyReportConfig {

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

        globaleFilters.add(btsFilter);

        /*JsonLogFilter siteFilter = new JsonLogFilter();
        siteFilter.setJsonPath("$.glb_d");
        siteFilter.setValueFilter("10013");
        globaleFilters.add(siteFilter);*/

        rule.setGlobaleJsonFilters(globaleFilters);

        rule.setReportName("BTS_ZAFUL_TOURIST_BUY_REPORT_APP");
        rule.setDescription("zaful APP 游客购买报表");

        // 默认值
        BtsZafulAppTouristBuyReportQuota searchRecommendReportQuotaModel = new BtsZafulAppTouristBuyReportQuota();
        searchRecommendReportQuotaModel.setBts(bts);
        rule.setReportDefaultValues(DyBeanUtils.objToMap(searchRecommendReportQuotaModel));


        ReportKafkaConfig reportKafkaConfig = new ReportKafkaConfig();
        reportKafkaConfig.setBootstrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setDataSourceTopic("glbg-analitic");
        reportKafkaConfig.setBootstrapGroupId("dy_bts_zaful_app_tour_buy_report");
        reportKafkaConfig.setReportStrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setReportDataTopic("dy_bts_zaful_app_tour_buy_report");

        rule.setReportFromKafka(reportKafkaConfig);


        List<ReportQuotaFieldConfig> reportQuotaFieldConfigs = new ArrayList<>();

        quota(reportQuotaFieldConfigs);

        rule.setReportQuotaFieldConfigs(reportQuotaFieldConfigs);

        System.out.println(JacksonUtil.toJSon(rule));
    }

    private void quota(List<ReportQuotaFieldConfig> reportQuotaFieldConfigs) {

        // 样本量指标
        ReportQuotaFieldConfig specimen = new ReportQuotaFieldConfig();
        specimen.setQuotaFieldName("specimen");
        specimen.setDefaultValue("_skip");
        specimen.setExtractValueJsonPath("$.appsflyer_device_id");
        specimen.setValueEnum("quotaStringValueExtractFromLog");

        reportQuotaFieldConfigs.add(specimen);

        // 购物车指标
        ReportQuotaFieldConfig cartPv = new ReportQuotaFieldConfig();
        cartPv.setQuotaFieldName("cart_pv");
        cartPv.setDefaultValue(0);
        cartPv.setValueEnum("countOneWithFilter");

        ReportQuotaFieldConfig cartUv = new ReportQuotaFieldConfig();
        cartUv.setQuotaFieldName("cart_uv");
        cartUv.setDefaultValue("_skip");
        cartUv.setExtractValueJsonPath("$.appsflyer_device_id");
        cartUv.setValueEnum("quotaStringValueExtractFromLog");

        List<JsonLogFilter> cartFilters = new ArrayList<>();

        JsonLogFilter cartFilter = new JsonLogFilter();
        cartFilter.setValueFilter("af_view_cartpage");
        cartFilter.setJsonPath("$.event_name");

        cartFilters.add(cartFilter);

        cartPv.setJsonLogFilters(cartFilters);
        cartUv.setJsonLogFilters(cartFilters);

        reportQuotaFieldConfigs.add(cartPv);
        reportQuotaFieldConfigs.add(cartUv);

        // af_checkout_btn_click
        ReportQuotaFieldConfig checkOut_btnPv = new ReportQuotaFieldConfig();
        checkOut_btnPv.setQuotaFieldName("checkout_btn_click_pv");
        checkOut_btnPv.setDefaultValue(0);
        checkOut_btnPv.setValueEnum("countOneWithFilter");

        ReportQuotaFieldConfig checkOut_btnUv = new ReportQuotaFieldConfig();
        checkOut_btnUv.setQuotaFieldName("checkout_btn_click_uv");
        checkOut_btnUv.setDefaultValue("_skip");
        checkOut_btnUv.setExtractValueJsonPath("$.appsflyer_device_id");
        checkOut_btnUv.setValueEnum("quotaStringValueExtractFromLog");

        List<JsonLogFilter> checkOut_btnFilters = new ArrayList<>();

        JsonLogFilter checkOut_btnFilter = new JsonLogFilter();
        checkOut_btnFilter.setValueFilter("af_checkout_btn_click");
        checkOut_btnFilter.setJsonPath("$.event_name");

        checkOut_btnFilters.add(checkOut_btnFilter);

        checkOut_btnUv.setJsonLogFilters(checkOut_btnFilters);
        checkOut_btnPv.setJsonLogFilters(checkOut_btnFilters);

        reportQuotaFieldConfigs.add(checkOut_btnUv);
        reportQuotaFieldConfigs.add(checkOut_btnPv);

        // af_view_checkout_page
        ReportQuotaFieldConfig af_view_checkout_pagePv = new ReportQuotaFieldConfig();
        af_view_checkout_pagePv.setQuotaFieldName("checkout_page_pv");
        af_view_checkout_pagePv.setDefaultValue(0);
        af_view_checkout_pagePv.setValueEnum("countOneWithFilter");

        ReportQuotaFieldConfig af_view_checkout_pageUv = new ReportQuotaFieldConfig();
        af_view_checkout_pageUv.setQuotaFieldName("checkout_page_uv");
        af_view_checkout_pageUv.setDefaultValue("_skip");
        af_view_checkout_pageUv.setExtractValueJsonPath("$.appsflyer_device_id");
        af_view_checkout_pageUv.setValueEnum("quotaStringValueExtractFromLog");

        List<JsonLogFilter> af_view_checkout_pageFilters = new ArrayList<>();

        JsonLogFilter af_view_checkout_pageFilter = new JsonLogFilter();
        af_view_checkout_pageFilter.setValueFilter("af_view_checkout_page");
        af_view_checkout_pageFilter.setJsonPath("$.event_name");

        af_view_checkout_pageFilters.add(af_view_checkout_pageFilter);

        af_view_checkout_pageUv.setJsonLogFilters(af_view_checkout_pageFilters);
        af_view_checkout_pagePv.setJsonLogFilters(af_view_checkout_pageFilters);

        reportQuotaFieldConfigs.add(af_view_checkout_pageUv);
        reportQuotaFieldConfigs.add(af_view_checkout_pagePv);

        // af_create_order_success
        ReportQuotaFieldConfig af_create_order_successPv = new ReportQuotaFieldConfig();
        af_create_order_successPv.setQuotaFieldName("create_order_success_pv");
        af_create_order_successPv.setDefaultValue(0);
        af_create_order_successPv.setValueEnum("countOneWithFilter");

        ReportQuotaFieldConfig af_create_order_successUv = new ReportQuotaFieldConfig();
        af_create_order_successUv.setQuotaFieldName("create_order_success_uv");
        af_create_order_successUv.setDefaultValue("_skip");
        af_create_order_successUv.setExtractValueJsonPath("$.appsflyer_device_id");
        af_create_order_successUv.setValueEnum("quotaStringValueExtractFromLog");

        List<JsonLogFilter> af_create_order_successFilters = new ArrayList<>();

        JsonLogFilter af_create_order_successFilter = new JsonLogFilter();
        af_create_order_successFilter.setValueFilter("af_view_checkout_page");
        af_create_order_successFilter.setJsonPath("$.event_name");

        af_create_order_successFilters.add(af_create_order_successFilter);

        af_create_order_successPv.setJsonLogFilters(af_view_checkout_pageFilters);
        af_create_order_successUv.setJsonLogFilters(af_view_checkout_pageFilters);

        reportQuotaFieldConfigs.add(af_create_order_successPv);
        reportQuotaFieldConfigs.add(af_create_order_successUv);

        // af_purchase
        ReportQuotaFieldConfig af_purchasePv = new ReportQuotaFieldConfig();
        af_purchasePv.setQuotaFieldName("purchase_pv");
        af_purchasePv.setDefaultValue(0);
        af_purchasePv.setValueEnum("countOneWithFilter");

        ReportQuotaFieldConfig af_purchaseUv = new ReportQuotaFieldConfig();
        af_purchaseUv.setQuotaFieldName("purchase_uv");
        af_purchaseUv.setDefaultValue("_skip");
        af_purchaseUv.setExtractValueJsonPath("$.appsflyer_device_id");
        af_purchaseUv.setValueEnum("quotaStringValueExtractFromLog");

        List<JsonLogFilter> af_purchaseFilters = new ArrayList<>();

        JsonLogFilter af_purchaseFilter = new JsonLogFilter();
        af_purchaseFilter.setValueFilter("af_purchase");
        af_purchaseFilter.setJsonPath("$.event_name");

        af_purchaseFilters.add(af_purchaseFilter);

        af_purchasePv.setJsonLogFilters(af_purchaseFilters);
        af_purchaseUv.setJsonLogFilters(af_purchaseFilters);

        reportQuotaFieldConfigs.add(af_purchasePv);
        reportQuotaFieldConfigs.add(af_purchaseUv);


        // bts 3 个字段
        ReportQuotaFieldConfig btsQuota = new ReportQuotaFieldConfig();
        btsQuota.setQuotaFieldName("bts");
        btsQuota.setDefaultValue(bts);
        btsQuota.setValueEnum("extractAppBtsValueFromLog");

        /*List<JsonLogFilter> btsFilter = new ArrayList<>();
        JsonLogFilter btsFilter1 = new JsonLogFilter();
        btsFilter1.setJsonPath("$.event_value.af_plan_id");
        btsFilter1.setFilterRule("not_null");
        btsFilter.add(btsFilter1);

        JsonLogFilter btsFilter2 = new JsonLogFilter();
        btsFilter2.setJsonPath("$.event_value.af_version_id");
        btsFilter2.setFilterRule("not_null");
        btsFilter.add(btsFilter2);

        JsonLogFilter btsFilter3 = new JsonLogFilter();
        btsFilter3.setJsonPath("$.event_value.af_bucket_id");
        btsFilter3.setFilterRule("not_null");
        btsFilter.add(btsFilter3);*/

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
