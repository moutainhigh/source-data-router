package com.globalegrow.bts.report;

import com.globalegrow.bts.report.model.CommonReportWithBuryLog;
import com.globalegrow.bts.report.util.PCMJsonFilterUtil;
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

public class RGPCAndMBaseReport {


    private Map<String, String> bts = new HashMap() {

        private static final long serialVersionUID = 3456809019097454769L;

        {
            put("planid", "_skip");
            put("versionid", "_skip");
            put("bucketid", "_skip");
            put("plancode", "_skip");
            put("policy", "_skip");
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

        // policy
        JsonLogFilter btsValuepolicyNotNullFilter = new JsonLogFilter();
        btsValuepolicyNotNullFilter.setJsonPath("$.glb_bts.policy");
        btsValuepolicyNotNullFilter.setFilterRule("not_null");
        globaleFilters.add(btsValuepolicyNotNullFilter);

        // plancode
        JsonLogFilter btsValueplancodeNotNullFilter = new JsonLogFilter();
        btsValueplancodeNotNullFilter.setJsonPath("$.glb_bts.plancode");
        btsValueplancodeNotNullFilter.setFilterRule("not_null");
        globaleFilters.add(btsValueplancodeNotNullFilter);

        // 分类页埋点字段 glb_p : category-17


        rule.setGlobaleJsonFilters(globaleFilters);

        rule.setReportName("DY_BTS_FULL_LINK_TEST_PC_M_REPORT");
        rule.setDescription("分类页实验指标_PC_M");
        CommonReportWithBuryLog searchRecommendReportQuotaModel = new CommonReportWithBuryLog();
        searchRecommendReportQuotaModel.setBts(bts);
        rule.setReportDefaultValues(DyBeanUtils.objToMap(searchRecommendReportQuotaModel));

        ReportKafkaConfig reportKafkaConfig = new ReportKafkaConfig();
        reportKafkaConfig.setBootstrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092,172.31.53.157:9092,172.31.54.122:9092");
        reportKafkaConfig.setDataSourceTopic("glbg-analitic");
        reportKafkaConfig.setBootstrapGroupId("dy_bts_full_link_test_pc_and_m");
        reportKafkaConfig.setReportStrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092,172.31.53.157:9092,172.31.54.122:9092");
        reportKafkaConfig.setReportDataTopic("dy_bts_full_link_test_pc_and_m");
        reportKafkaConfig.setFromStartOffset(false);

        rule.setReportFromKafka(reportKafkaConfig);


        List<ReportQuotaFieldConfig> reportQuotaFieldConfigs = new ArrayList<>();

        quota(reportQuotaFieldConfigs);

        rule.setReportQuotaFieldConfigs(reportQuotaFieldConfigs);

        System.out.println(JacksonUtil.toJSon(rule));
    }

    private void quota(List<ReportQuotaFieldConfig> reportQuotaFieldConfigs) {

        // 样本量
        ReportQuotaFieldConfig specimen = new ReportQuotaFieldConfig();
        specimen.setQuotaFieldName("specimen");
        specimen.setDefaultValue("_skip");
        specimen.setExtractValueJsonPath("$.glb_od");
        specimen.setValueEnum("quotaStringValueExtractFromLog");

        // 过滤条件
        List<JsonLogFilter> specimenFilter = new ArrayList<>();
        specimenFilter.add(PCMJsonFilterUtil.tie());
        specimenFilter.add(PCMJsonFilterUtil.glb_bB());
        specimenFilter.add(PCMJsonFilterUtil.glb_pmMp());
        specimenFilter.add(PCMJsonFilterUtil.ubcNotNull());
        specimenFilter.add(PCMJsonFilterUtil.glb_p_Category());

        specimen.setJsonLogFilters(specimenFilter);

        reportQuotaFieldConfigs.add(specimen);

        // pv uv
        ReportQuotaFieldConfig pv = new ReportQuotaFieldConfig();
        pv.setQuotaFieldName("pv");
        pv.setDefaultValue(0);
        pv.setValueEnum("countOneWithFilter");

        ReportQuotaFieldConfig uv = new ReportQuotaFieldConfig();
        uv.setQuotaFieldName("uv");
        uv.setDefaultValue("_skip");
        uv.setExtractValueJsonPath("$.glb_od");
        uv.setValueEnum("quotaStringValueExtractFromLog");

        List<JsonLogFilter> pvAndUvFilters = new ArrayList<>();
        pvAndUvFilters.add(PCMJsonFilterUtil.glb_p_Category());
        pvAndUvFilters.add(PCMJsonFilterUtil.glb_filterRec());
        pvAndUvFilters.add(PCMJsonFilterUtil.glb_pmNull());
        pvAndUvFilters.add(PCMJsonFilterUtil.glb_bB());

        pv.setJsonLogFilters(pvAndUvFilters);
        uv.setJsonLogFilters(pvAndUvFilters);

        reportQuotaFieldConfigs.add(pv);
        reportQuotaFieldConfigs.add(uv);

        // 曝光数 曝光 uv
        ReportQuotaFieldConfig exp_num = new ReportQuotaFieldConfig();
        exp_num.setQuotaFieldName("exp_num");
        exp_num.setDefaultValue(0);
        exp_num.setExtractValueJsonPath("$.glb_ubcta");
        exp_num.setValueEnum("countListWithFilter");

        ReportQuotaFieldConfig exp_uv = new ReportQuotaFieldConfig();
        exp_uv.setQuotaFieldName("exp_uv");
        exp_uv.setDefaultValue("_skip");
        exp_uv.setExtractValueJsonPath("$.glb_od");
        exp_uv.setValueEnum("quotaStringValueExtractFromLog");

        List<JsonLogFilter> exp_num_uvFilters = new ArrayList<>();
        exp_num_uvFilters.add(PCMJsonFilterUtil.glb_p_Category());
        exp_num_uvFilters.add(PCMJsonFilterUtil.glb_filterRec());
        exp_num_uvFilters.add(PCMJsonFilterUtil.tie());
        exp_num_uvFilters.add(PCMJsonFilterUtil.glb_bB());
        exp_num_uvFilters.add(PCMJsonFilterUtil.ubcNotNull());


        exp_num.setJsonLogFilters(exp_num_uvFilters);
        exp_uv.setJsonLogFilters(exp_num_uvFilters);

        reportQuotaFieldConfigs.add(exp_num);
        reportQuotaFieldConfigs.add(exp_uv);


        // 点击数、点击uv
        ReportQuotaFieldConfig sku_click_num = new ReportQuotaFieldConfig();
        sku_click_num.setQuotaFieldName("sku_click_num");
        sku_click_num.setDefaultValue(0);
        sku_click_num.setValueEnum("countOneWithFilter");

        ReportQuotaFieldConfig sku_click_uv = new ReportQuotaFieldConfig();
        sku_click_uv.setQuotaFieldName("sku_click_uv");
        sku_click_uv.setDefaultValue("_skip");
        sku_click_uv.setExtractValueJsonPath("$.glb_od");
        sku_click_uv.setValueEnum("quotaStringValueExtractFromLog");

        List<JsonLogFilter> clickFilters = new ArrayList<>();
        clickFilters.add(PCMJsonFilterUtil.glb_p_Category());
        clickFilters.add(PCMJsonFilterUtil.glb_filterRec());
        clickFilters.add(PCMJsonFilterUtil.tic());
        clickFilters.add(PCMJsonFilterUtil.glb_bB());
        clickFilters.add(PCMJsonFilterUtil.ubcNotNull());
        clickFilters.add(PCMJsonFilterUtil.glb_pmMp());
        clickFilters.add(PCMJsonFilterUtil.skuClick());

        sku_click_num.setJsonLogFilters(clickFilters);
        sku_click_uv.setJsonLogFilters(clickFilters);
        reportQuotaFieldConfigs.add(sku_click_num);
        reportQuotaFieldConfigs.add(sku_click_uv);

        // 加购数，uv
        // cartNum.setExtractValueJsonPath("$.glb_skuinfo.pam");
        // cartNum.setValueEnum("quotaIntValueExtractFromLog");
        ReportQuotaFieldConfig sku_cart_num = new ReportQuotaFieldConfig();
        sku_cart_num.setQuotaFieldName("sku_cart_num");
        sku_cart_num.setDefaultValue(0);
        sku_cart_num.setExtractValueJsonPath("$.glb_skuinfo.pam");
        sku_cart_num.setValueEnum("quotaIntValueExtractFromLog");

        ReportQuotaFieldConfig sku_cart_uv = new ReportQuotaFieldConfig();
        sku_cart_uv.setQuotaFieldName("sku_cart_uv");
        sku_cart_uv.setDefaultValue("_skip");
        sku_cart_uv.setExtractValueJsonPath("$.glb_od");
        sku_cart_uv.setValueEnum("quotaStringValueExtractFromLog");

        List<JsonLogFilter> cartFilters = new ArrayList<>();
        cartFilters.add(PCMJsonFilterUtil.tic());
        cartFilters.add(PCMJsonFilterUtil.ubcNotNull());
        cartFilters.add(PCMJsonFilterUtil.glb_ubcta_p_Category());
        cartFilters.add(PCMJsonFilterUtil.glb_ubcta_fmd_mp());
        cartFilters.add(PCMJsonFilterUtil.glb_ubctaRec());
        cartFilters.add(PCMJsonFilterUtil.glb_xADT());

        sku_cart_num.setJsonLogFilters(cartFilters);
        sku_cart_uv.setJsonLogFilters(cartFilters);
        reportQuotaFieldConfigs.add(sku_cart_num);
        reportQuotaFieldConfigs.add(sku_cart_uv);

        // 加收藏数，uv
        ReportQuotaFieldConfig sku_collect_num = new ReportQuotaFieldConfig();
        sku_collect_num.setQuotaFieldName("sku_collect_num");
        sku_collect_num.setDefaultValue(0);
        // sku_collect_num.setExtractValueJsonPath("$.glb_skuinfo.pam");
        sku_collect_num.setValueEnum("countOneWithFilter");

        ReportQuotaFieldConfig sku_collect_uv = new ReportQuotaFieldConfig();
        sku_collect_uv.setQuotaFieldName("sku_collect_uv");
        sku_collect_uv.setDefaultValue("_skip");
        sku_collect_uv.setExtractValueJsonPath("$.glb_od");
        sku_collect_uv.setValueEnum("quotaStringValueExtractFromLog");

        List<JsonLogFilter> sku_collect_uvFilters = new ArrayList<>();
        sku_collect_uvFilters.add(PCMJsonFilterUtil.tic());
        sku_collect_uvFilters.add(PCMJsonFilterUtil.ubcNotNull());
        sku_collect_uvFilters.add(PCMJsonFilterUtil.glb_ubcta_p_Category());
        sku_collect_uvFilters.add(PCMJsonFilterUtil.glb_ubcta_fmd_mp());
        sku_collect_uvFilters.add(PCMJsonFilterUtil.glb_ubctaRec());
        sku_collect_uvFilters.add(PCMJsonFilterUtil.glb_xADF());

        sku_collect_num.setJsonLogFilters(sku_collect_uvFilters);
        sku_collect_uv.setJsonLogFilters(sku_collect_uvFilters);
        reportQuotaFieldConfigs.add(sku_collect_num);
        reportQuotaFieldConfigs.add(sku_collect_uv);



        ReportQuotaFieldConfig btsQuota = new ReportQuotaFieldConfig();
        btsQuota.setQuotaFieldName("bts");
        btsQuota.setDefaultValue(bts);
        btsQuota.setExtractValueJsonPath("$.glb_bts");
        btsQuota.setValueEnum("extractMapValueFromLog");

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
