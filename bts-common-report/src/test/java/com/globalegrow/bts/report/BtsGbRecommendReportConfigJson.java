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

public class BtsGbRecommendReportConfigJson {

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

        JsonLogFilter btsValuePlanIdNotNullFilter = new JsonLogFilter();
        btsValuePlanIdNotNullFilter.setJsonPath("$.glb_bts.planid");
        btsValuePlanIdNotNullFilter.setFilterRule("not_null");
        globaleFilters.add(btsValuePlanIdNotNullFilter);

        JsonLogFilter btsValueversionidNotNullFilter = new JsonLogFilter();
        btsValueversionidNotNullFilter.setJsonPath("$.glb_bts.versionid");
        btsValueversionidNotNullFilter.setFilterRule("not_null");
        globaleFilters.add(btsValueversionidNotNullFilter);

        JsonLogFilter btsValuebucketidNotNullFilter = new JsonLogFilter();
        btsValuebucketidNotNullFilter.setJsonPath("$.glb_bts.bucketid");
        btsValuebucketidNotNullFilter.setFilterRule("not_null");
        globaleFilters.add(btsValuebucketidNotNullFilter);


        JsonLogFilter siteFilter = new JsonLogFilter();
        siteFilter.setJsonPath("$.glb_d");
        siteFilter.setValueFilter("10002");
        globaleFilters.add(siteFilter);

        rule.setGlobaleJsonFilters(globaleFilters);

        rule.setReportName("BTS_GB_ORDER_RECOMMEND_REPORT");
        rule.setDescription("GB ???????????????????????????");
        BtsGbRecommendReport btsGbRecommendReport = new BtsGbRecommendReport();
        btsGbRecommendReport.setBts(bts);
        rule.setReportDefaultValues(DyBeanUtils.objToMap(btsGbRecommendReport));


        ReportKafkaConfig reportKafkaConfig = new ReportKafkaConfig();
        reportKafkaConfig.setBootstrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setDataSourceTopic("glbg-analitic");
        reportKafkaConfig.setBootstrapGroupId("dy_bts_gb_gd_rec_report");
        reportKafkaConfig.setReportStrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setReportDataTopic("dy_bts_gb_gd_rec_report");

        rule.setReportFromKafka(reportKafkaConfig);


        List<ReportQuotaFieldConfig> reportQuotaFieldConfigs = new ArrayList<>();

        quota(reportQuotaFieldConfigs);

        rule.setReportQuotaFieldConfigs(reportQuotaFieldConfigs);

        System.out.println(JacksonUtil.toJSon(rule));
    }

    private void quota(List<ReportQuotaFieldConfig> reportQuotaFieldConfigs) {

        // ?????? pv ???
        ReportQuotaFieldConfig goodDetailPv = new ReportQuotaFieldConfig();
        goodDetailPv.setQuotaFieldName("goodDetailPv");
        goodDetailPv.setDefaultValue(0);
        goodDetailPv.setValueEnum("countOneWithFilter");

        List<JsonLogFilter> goodPvFilter = new ArrayList<>();

        JsonLogFilter tie = new JsonLogFilter();
        tie.setJsonPath("$.glb_t");
        tie.setValueFilter("ie");
        goodPvFilter.add(tie);

        JsonLogFilter glbB = new JsonLogFilter();
        glbB.setJsonPath("$.glb_b");
        glbB.setValueFilter("c");
        goodPvFilter.add(glbB);

        JsonLogFilter glb_ksku = new JsonLogFilter();
        glb_ksku.setJsonPath("$.glb_ksku");
        glb_ksku.setFilterRule("not_null");
        goodPvFilter.add(glb_ksku);

        JsonLogFilter glb_cl = new JsonLogFilter();
        glb_cl.setJsonPath("$.glb_cl");
        glb_cl.setFilterRule("contains_backward");
        glb_cl.setValueFilter("/pp_");
        goodPvFilter.add(glb_cl);

        goodDetailPv.setJsonLogFilters(goodPvFilter);
        reportQuotaFieldConfigs.add(goodDetailPv);

        // ????????? uv
        ReportQuotaFieldConfig goodDetailUv = new ReportQuotaFieldConfig();
        goodDetailUv.setQuotaFieldName("goodDetailUv");
        goodDetailUv.setDefaultValue("_skip");
        goodDetailUv.setExtractValueJsonPath("$.glb_od");
        goodDetailUv.setValueEnum("quotaStringValueExtractFromLog");

        goodDetailUv.setJsonLogFilters(goodPvFilter);

        reportQuotaFieldConfigs.add(goodDetailUv);

        // ?????? sku ???
        ReportQuotaFieldConfig expNum = new ReportQuotaFieldConfig();
        expNum.setQuotaFieldName("skuExposure");
        expNum.setDefaultValue(0);
        expNum.setExtractValueJsonPath("$.glb_ubcta");
        expNum.setValueEnum("countListWithFilter");

        List<JsonLogFilter> expNumFilters = new ArrayList<>();
        expNumFilters.add(tie);
        expNumFilters.add(glbB);

        JsonLogFilter glb_pm = new JsonLogFilter();
        glb_pm.setJsonPath("$.glb_pm");
        glb_pm.setValueFilter("mr");
        expNumFilters.add(glb_pm);

        JsonLogFilter ubcNotNull = new JsonLogFilter();
        ubcNotNull.setJsonPath("$.glb_ubcta");
        ubcNotNull.setFilterRule("not_null");
        expNumFilters.add(ubcNotNull);

        expNum.setJsonLogFilters(expNumFilters);

        reportQuotaFieldConfigs.add(expNum);

        // ?????? pv ???
        ReportQuotaFieldConfig recommendTypeExposurePv = new ReportQuotaFieldConfig();
        recommendTypeExposurePv.setQuotaFieldName("recommendTypeExposurePv");
        recommendTypeExposurePv.setDefaultValue(0);
        recommendTypeExposurePv.setValueEnum("countOneWithFilter");

        recommendTypeExposurePv.setJsonLogFilters(expNumFilters);

        reportQuotaFieldConfigs.add(recommendTypeExposurePv);

        // ?????? sku ???
        ReportQuotaFieldConfig clickSkuNum = new ReportQuotaFieldConfig();
        clickSkuNum.setQuotaFieldName("skuClick");
        clickSkuNum.setDefaultValue(0);
        clickSkuNum.setValueEnum("countOneWithFilter");

        List<JsonLogFilter> clickFilters = new ArrayList<>();
        clickFilters.add(glbB);
        clickFilters.add(glb_pm);

        JsonLogFilter glb_t_ic = new JsonLogFilter();
        glb_t_ic.setJsonPath("$.glb_t");
        glb_t_ic.setValueFilter("ic");
        clickFilters.add(glb_t_ic);

        clickSkuNum.setJsonLogFilters(clickFilters);

        reportQuotaFieldConfigs.add(clickSkuNum);

        // ?????? sku ???
        ReportQuotaFieldConfig cartSkuNum = new ReportQuotaFieldConfig();
        cartSkuNum.setQuotaFieldName("skuAddCart");
        cartSkuNum.setDefaultValue(0);
        cartSkuNum.setValueEnum("countOneWithFilter");
        cartSkuNum.setCacheData(true);

        List<JsonLogFilter> cartFilters = new ArrayList<>();
        cartFilters.add(glb_t_ic);
        cartFilters.add(glbB);

        JsonLogFilter glb_pm_mb_mbt = new JsonLogFilter();
        glb_pm_mb_mbt.setJsonPath("$.glb_pm");
        glb_pm_mb_mbt.setValueFilter("mb,mbt");
        glb_pm_mb_mbt.setFilterRule("contains");
        cartFilters.add(glb_pm_mb_mbt);

        cartSkuNum.setJsonLogFilters(cartFilters);
        reportQuotaFieldConfigs.add(cartSkuNum);

        // bts ?????? extractBtsMapValueFromLog
        ReportQuotaFieldConfig btsQuota = new ReportQuotaFieldConfig();
        btsQuota.setQuotaFieldName("bts");
        btsQuota.setDefaultValue(bts);
        btsQuota.setExtractValueJsonPath("$.glb_bts");
        btsQuota.setValueEnum("extractBtsMapValueFromLog");

        reportQuotaFieldConfigs.add(btsQuota);

        // ?????????
        ReportQuotaFieldConfig specimen = new ReportQuotaFieldConfig();
        specimen.setQuotaFieldName("specimen");
        specimen.setDefaultValue("_skip");
        specimen.setExtractValueJsonPath("$.glb_od");
        specimen.setValueEnum("quotaStringValueExtractFromLog");

        reportQuotaFieldConfigs.add(specimen);

        // ???????????????
        ReportQuotaFieldConfig timestamp = new ReportQuotaFieldConfig();
        timestamp.setQuotaFieldName("timestamp");
        timestamp.setDefaultValue(0);
        timestamp.setExtractValueJsonPath("$.timestamp");
        timestamp.setValueEnum("quotaLongValueExtractFromLog");

        reportQuotaFieldConfigs.add(timestamp);

    }


}
