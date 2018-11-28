package com.globalegrow.bts.report;

import com.globalegrow.bts.model.BtsGbRecommendReport;
import com.globalegrow.report.JsonLogFilter;
import com.globalegrow.report.ReportBuildRule;
import com.globalegrow.report.ReportKafkaConfig;
import com.globalegrow.report.ReportQuotaFieldConfig;
import com.globalegrow.util.DyBeanUtils;
import com.globalegrow.util.JacksonUtil;
import kafka.utils.Json;
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

        JsonLogFilter siteFilter = new JsonLogFilter();
        siteFilter.setJsonPath("$.glb_d");
        siteFilter.setValueFilter("10002");
        globaleFilters.add(siteFilter);

        rule.setGlobaleJsonFilters(globaleFilters);

        rule.setReportName("BTS_GB_ORDER_RECOMMEND_REPORT");
        rule.setDescription("GB 推荐位报表指标配置");
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

        // 商详 pv 数
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

        // 商详页 uv
        ReportQuotaFieldConfig goodDetailUv = new ReportQuotaFieldConfig();
        goodDetailUv.setQuotaFieldName("goodDetailUv");
        goodDetailUv.setDefaultValue("_skip");
        goodDetailUv.setExtractValueJsonPath("$.glb_od");
        goodDetailUv.setValueEnum("quotaStringValueExtractFromLog");

        goodDetailUv.setJsonLogFilters(goodPvFilter);

        reportQuotaFieldConfigs.add(goodDetailUv);

        // 曝光 sku 数
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

        expNum.setJsonLogFilters(expNumFilters);

        reportQuotaFieldConfigs.add(expNum);

        // 曝光 pv 数
        ReportQuotaFieldConfig recommendTypeExposurePv = new ReportQuotaFieldConfig();
        recommendTypeExposurePv.setQuotaFieldName("recommendTypeExposurePv");
        recommendTypeExposurePv.setDefaultValue(0);
        recommendTypeExposurePv.setValueEnum("countOneWithFilter");

        recommendTypeExposurePv.setJsonLogFilters(expNumFilters);

        reportQuotaFieldConfigs.add(recommendTypeExposurePv);

        // 点击 sku 数
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

        // 加购 sku 数
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

        // bts 字段 extractBtsMapValueFromLog
        ReportQuotaFieldConfig btsQuota = new ReportQuotaFieldConfig();
        btsQuota.setQuotaFieldName("bts");
        btsQuota.setDefaultValue(bts);
        btsQuota.setExtractValueJsonPath("$.glb_bts");
        btsQuota.setValueEnum("extractBtsMapValueFromLog");

        reportQuotaFieldConfigs.add(btsQuota);

        // 样本量
        ReportQuotaFieldConfig specimen = new ReportQuotaFieldConfig();
        specimen.setQuotaFieldName("specimen");
        specimen.setDefaultValue("_skip");
        specimen.setExtractValueJsonPath("$.glb_od");
        specimen.setValueEnum("quotaStringValueExtractFromLog");

        reportQuotaFieldConfigs.add(specimen);

        // 时间戳字段
        ReportQuotaFieldConfig timestamp = new ReportQuotaFieldConfig();
        timestamp.setQuotaFieldName("timestamp");
        timestamp.setDefaultValue(0);
        timestamp.setExtractValueJsonPath("$.timestamp");
        timestamp.setValueEnum("quotaLongValueExtractFromLog");

        reportQuotaFieldConfigs.add(timestamp);

    }


}
