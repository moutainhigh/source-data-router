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

public class AppPushReportConfigJson {

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

        rule.setGlobaleJsonFilters(globaleFilters);

        rule.setReportName("BTS_ZAFUL_ORDER_APP_PUSH_REPORT_APP");
        rule.setDescription("zaful APP PUSH 推荐报表");

        // 默认值
        AppPushRecReportQuota searchRecommendReportQuotaModel = new AppPushRecReportQuota();
        searchRecommendReportQuotaModel.setBts(bts);
        rule.setReportDefaultValues(DyBeanUtils.objToMap(searchRecommendReportQuotaModel));


        ReportKafkaConfig reportKafkaConfig = new ReportKafkaConfig();
        reportKafkaConfig.setBootstrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setDataSourceTopic("glbg-analitic");
        reportKafkaConfig.setBootstrapGroupId("dy_bts_app_push_report");
        reportKafkaConfig.setReportStrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setReportDataTopic("dy_bts_app_push_report");

        rule.setReportFromKafka(reportKafkaConfig);


        List<ReportQuotaFieldConfig> reportQuotaFieldConfigs = new ArrayList<>();

        quota(reportQuotaFieldConfigs);

        rule.setReportQuotaFieldConfigs(reportQuotaFieldConfigs);

        System.out.println(JacksonUtil.toJSon(rule));
    }

    private void quota(List<ReportQuotaFieldConfig> reportQuotaFieldConfigs) {

        // push 落地页 pv uv
        ReportQuotaFieldConfig appPushPv = new ReportQuotaFieldConfig();
        appPushPv.setQuotaFieldName("view_push_page");
        appPushPv.setDefaultValue(0);
        appPushPv.setValueEnum("countOneWithFilter");

        ReportQuotaFieldConfig appPushUv = new ReportQuotaFieldConfig();
        appPushUv.setQuotaFieldName("view_push_uv");
        appPushUv.setDefaultValue("_skip");
        appPushUv.setExtractValueJsonPath("$.appsflyer_device_id");
        appPushUv.setValueEnum("quotaStringValueExtractFromLog");

        List<JsonLogFilter> pushFilters = new ArrayList<>();
        JsonLogFilter pushFilter = new JsonLogFilter();
        pushFilter.setValueFilter("af_view_pushpage");
        pushFilter.setJsonPath("$.event_name");
        pushFilters.add(pushFilter);

        reportQuotaFieldConfigs.add(appPushPv);
        reportQuotaFieldConfigs.add(appPushUv);

        // 曝光数 pv uv
        ReportQuotaFieldConfig af_impressionPv = new ReportQuotaFieldConfig();
        af_impressionPv.setQuotaFieldName("exposure_count");
        af_impressionPv.setDefaultValue(0);
        af_impressionPv.setValueEnum("countOneWithFilter");

        ReportQuotaFieldConfig af_impressionUv = new ReportQuotaFieldConfig();
        af_impressionUv.setQuotaFieldName("good_view_uv");
        af_impressionUv.setDefaultValue("_skip");
        af_impressionUv.setExtractValueJsonPath("$.appsflyer_device_id");
        af_impressionUv.setValueEnum("quotaStringValueExtractFromLog");

        List<JsonLogFilter> af_impressionFilters = new ArrayList<>();
        JsonLogFilter af_impressionFilter = new JsonLogFilter();
        af_impressionFilter.setValueFilter("af_impression");
        af_impressionFilter.setJsonPath("$.event_name");
        af_impressionFilters.add(af_impressionFilter);

        reportQuotaFieldConfigs.add(af_impressionPv);
        reportQuotaFieldConfigs.add(af_impressionUv);

        // 商品点击 pv uv
        ReportQuotaFieldConfig af_view_productPv = new ReportQuotaFieldConfig();
        af_view_productPv.setQuotaFieldName("good_click");
        af_view_productPv.setDefaultValue(0);
        af_view_productPv.setValueEnum("countOneWithFilter");

        ReportQuotaFieldConfig af_view_productUv = new ReportQuotaFieldConfig();
        af_view_productUv.setQuotaFieldName("good_click_uv");
        af_view_productUv.setDefaultValue("_skip");
        af_view_productUv.setExtractValueJsonPath("$.appsflyer_device_id");
        af_view_productUv.setValueEnum("quotaStringValueExtractFromLog");

        List<JsonLogFilter> af_view_productFilters = new ArrayList<>();
        JsonLogFilter af_view_productFilter = new JsonLogFilter();
        af_view_productFilter.setValueFilter("af_view_product");
        af_view_productFilter.setJsonPath("$.event_name");
        af_view_productFilters.add(af_view_productFilter);

        JsonLogFilter colorFilter = new JsonLogFilter();
        colorFilter.setValueFilter("0");
        colorFilter.setJsonPath("$.event_value.af_changed_size_or_color");
        af_view_productFilters.add(colorFilter);

        reportQuotaFieldConfigs.add(af_view_productPv);
        reportQuotaFieldConfigs.add(af_view_productUv);

        // 商品加购 pv uv
        ReportQuotaFieldConfig af_add_to_bagPv = new ReportQuotaFieldConfig();
        af_add_to_bagPv.setQuotaFieldName("good_add_cart");
        af_add_to_bagPv.setDefaultValue(0);
        af_add_to_bagPv.setExtractValueJsonPath("$.event_value.af_quantity");
        af_add_to_bagPv.setValueEnum("quotaIntValueExtractFromLog");
        af_add_to_bagPv.setCacheData(true);

        ReportQuotaFieldConfig af_add_to_bagUv = new ReportQuotaFieldConfig();
        af_add_to_bagUv.setQuotaFieldName("good_add_cart_uv");
        af_add_to_bagUv.setDefaultValue("_skip");
        af_add_to_bagUv.setExtractValueJsonPath("$.appsflyer_device_id");
        af_add_to_bagUv.setValueEnum("quotaStringValueExtractFromLog");

        List<JsonLogFilter> af_add_to_bagFilters = new ArrayList<>();
        JsonLogFilter af_add_to_bagFilter = new JsonLogFilter();
        af_add_to_bagFilter.setValueFilter("af_add_to_bag");
        af_add_to_bagFilter.setJsonPath("$.event_name");
        af_add_to_bagFilters.add(af_add_to_bagFilter);

        reportQuotaFieldConfigs.add(af_add_to_bagPv);
        reportQuotaFieldConfigs.add(af_add_to_bagUv);


        // 商品收藏 pv uv
        ReportQuotaFieldConfig af_add_to_wishlistPv = new ReportQuotaFieldConfig();
        af_add_to_wishlistPv.setQuotaFieldName("good_collect");
        af_add_to_wishlistPv.setDefaultValue(0);
        af_add_to_wishlistPv.setValueEnum("countOneWithFilter");

        ReportQuotaFieldConfig af_add_to_wishlistUv = new ReportQuotaFieldConfig();
        af_add_to_wishlistUv.setQuotaFieldName("good_collect_uv");
        af_add_to_wishlistUv.setDefaultValue("_skip");
        af_add_to_wishlistUv.setExtractValueJsonPath("$.appsflyer_device_id");
        af_add_to_wishlistUv.setValueEnum("quotaStringValueExtractFromLog");

        List<JsonLogFilter> af_add_to_wishlistFilters = new ArrayList<>();
        JsonLogFilter af_add_to_wishlistFilter = new JsonLogFilter();
        af_add_to_wishlistFilter.setValueFilter("af_add_to_wishlist");
        af_add_to_wishlistFilter.setJsonPath("$.event_name");
        af_add_to_wishlistFilters.add(af_add_to_wishlistFilter);

        reportQuotaFieldConfigs.add(af_add_to_wishlistPv);
        reportQuotaFieldConfigs.add(af_add_to_wishlistUv);

        // 样本量指标
        ReportQuotaFieldConfig specimen = new ReportQuotaFieldConfig();
        specimen.setQuotaFieldName("specimen");
        specimen.setDefaultValue("_skip");
        specimen.setExtractValueJsonPath("$.appsflyer_device_id");
        specimen.setValueEnum("quotaStringValueExtractFromLog");

        reportQuotaFieldConfigs.add(specimen);

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
