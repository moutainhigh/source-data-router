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

public class ReportConfigGerenate {

    private Map<String, String> bts = new HashMap() {

        private static final long serialVersionUID = 3456809019097454769L;

        {
            put("planid", "_skip");
            put("versionid", "_skip");
            put("bucketid", "_skip");
        }
    };

    @Test
    public void stringTest() {
        String s = "$.skuInfo.wrwer";
        String s1 = s.substring(s.indexOf("$.") + 2);
        if (s1.contains(".")) {
            s1 = s1.substring(0, s1.indexOf("."));
            System.out.println(s1);
        }
        System.out.println();
    }

    @Test
    public void reportJson() throws Exception {
        SearchRecommendReportQuotaModel searchRecommendReportQuotaModel = new SearchRecommendReportQuotaModel();
        searchRecommendReportQuotaModel.setTimestamp(System.currentTimeMillis());
        System.out.println(JacksonUtil.toJSon(searchRecommendReportQuotaModel));
    }


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
        siteFilter.setValueFilter("10013");
        globaleFilters.add(siteFilter);

        rule.setGlobaleJsonFilters(globaleFilters);

        rule.setReportName("BTS_ZAFUL_ORDER_SEARCH_REC");
        rule.setDescription("zaful ???????????? ab ??????????????????????????????");
        SearchRecommendReportQuotaModel searchRecommendReportQuotaModel = new SearchRecommendReportQuotaModel();

        rule.setReportDefaultValues(DyBeanUtils.objToMap(searchRecommendReportQuotaModel));


        ReportKafkaConfig reportKafkaConfig = new ReportKafkaConfig();
        reportKafkaConfig.setBootstrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setDataSourceTopic("glbg-analitic");
        reportKafkaConfig.setBootstrapGroupId("dy_bts_search_rec_report");
        reportKafkaConfig.setReportStrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setReportDataTopic("dy_bts_search_rec_report");

        rule.setReportFromKafka(reportKafkaConfig);


        List<ReportQuotaFieldConfig> reportQuotaFieldConfigs = new ArrayList<>();

        quota(reportQuotaFieldConfigs);

        rule.setReportQuotaFieldConfigs(reportQuotaFieldConfigs);

        System.out.println(JacksonUtil.toJSon(rule));
    }

    private void quota(List<ReportQuotaFieldConfig> reportQuotaFieldConfigs) {
        // ?????????
        ReportQuotaFieldConfig specimen = new ReportQuotaFieldConfig();
        specimen.setQuotaFieldName("specimen");
        specimen.setDefaultValue("_skip");
        specimen.setExtractValueJsonPath("$.glb_od");
        specimen.setValueEnum("quotaStringValueExtractFromLog");
        // ????????????
        List<JsonLogFilter> specimenFilter = new ArrayList<>();
        JsonLogFilter plf = new JsonLogFilter();
        plf.setJsonPath("$.glb_plf");
        plf.setValueFilter("pc");
        specimenFilter.add(plf);

        JsonLogFilter tie = new JsonLogFilter();
        tie.setJsonPath("$.glb_t");
        tie.setValueFilter("ie");
        specimenFilter.add(tie);

        JsonLogFilter glbs = new JsonLogFilter();
        glbs.setJsonPath("$.glb_s");
        glbs.setValueFilter("b02");
        specimenFilter.add(glbs);

        JsonLogFilter ubc = new JsonLogFilter();
        ubc.setJsonPath("$.glb_ubcta");
        ubc.setFilterRule("null");
        specimenFilter.add(ubc);

        JsonLogFilter glbFilterRecommend = new JsonLogFilter();
        glbFilterRecommend.setJsonPath("$.glb_filter.sort");
        glbFilterRecommend.setValueFilter("recommend");
        specimenFilter.add(glbFilterRecommend);

        specimen.setJsonLogFilters(specimenFilter);

        reportQuotaFieldConfigs.add(specimen);

        // ????????? UV
        ReportQuotaFieldConfig searchPageUv = new ReportQuotaFieldConfig();
        searchPageUv.setQuotaFieldName("search_rec_uv");
        searchPageUv.setDefaultValue("_skip");
        searchPageUv.setExtractValueJsonPath("$.glb_od");
        searchPageUv.setValueEnum("quotaStringValueExtractFromLog");
        searchPageUv.setJsonLogFilters(specimenFilter);
        reportQuotaFieldConfigs.add(searchPageUv);

        // ????????? PV
        ReportQuotaFieldConfig searchPagePv = new ReportQuotaFieldConfig();
        searchPagePv.setQuotaFieldName("search_rec_pv");
        searchPagePv.setDefaultValue(0);
        searchPagePv.setExtractValueJsonPath("$.glb_od");
        searchPagePv.setValueEnum("countOneWithFilter");
        searchPagePv.setJsonLogFilters(specimenFilter);
        reportQuotaFieldConfigs.add(searchPagePv);

        // ???????????? pv & uv
        ReportQuotaFieldConfig goodExpUv = new ReportQuotaFieldConfig();
        goodExpUv.setQuotaFieldName("good_exp_uv");
        goodExpUv.setDefaultValue("_skip");
        goodExpUv.setExtractValueJsonPath("$.glb_od");
        goodExpUv.setValueEnum("quotaStringValueExtractFromLog");

        List<JsonLogFilter> expFilter = new ArrayList<>();
        expFilter.add(plf);
        expFilter.add(tie);
        expFilter.add(glbs);
        expFilter.add(glbFilterRecommend);

        JsonLogFilter mpFilter = new JsonLogFilter();
        mpFilter.setJsonPath("$.glb_pm");
        mpFilter.setValueFilter("mp");

        expFilter.add(mpFilter);

        goodExpUv.setJsonLogFilters(expFilter);

        reportQuotaFieldConfigs.add(goodExpUv);

        ReportQuotaFieldConfig expPv = new ReportQuotaFieldConfig();
        expPv.setQuotaFieldName("good_exp_pv");
        expPv.setDefaultValue(0);
        expPv.setExtractValueJsonPath("$.glb_ubcta");
        expPv.setValueEnum("countListWithFilter");
        expPv.setJsonLogFilters(expFilter);

        reportQuotaFieldConfigs.add(expPv);

        // ???????????? pv uv
        ReportQuotaFieldConfig gcPv = new ReportQuotaFieldConfig();
        gcPv.setQuotaFieldName("good_cli_pv");
        gcPv.setDefaultValue(0);
        gcPv.setExtractValueJsonPath("$.glb_ubcta");
        gcPv.setValueEnum("countOneWithFilter");

        ReportQuotaFieldConfig gcUv = new ReportQuotaFieldConfig();
        gcUv.setQuotaFieldName("good_cli_uv");
        gcUv.setDefaultValue("_skip");
        gcUv.setExtractValueJsonPath("$.glb_od");
        gcUv.setValueEnum("quotaStringValueExtractFromLog");

        List<JsonLogFilter> goodClickFilter = new ArrayList<>();
        goodClickFilter.add(glbs);
        goodClickFilter.add(plf);
        goodClickFilter.add(mpFilter);
        goodClickFilter.add(glbFilterRecommend);

        JsonLogFilter ic = new JsonLogFilter();
        ic.setJsonPath("$.glb_t");
        ic.setValueFilter("ic");
        goodClickFilter.add(ic);

        JsonLogFilter sku = new JsonLogFilter();
        sku.setJsonPath("$.glb_x");
        sku.setValueFilter("sku,addtobag");
        sku.setFilterRule("contains");
        goodClickFilter.add(sku);

        JsonLogFilter sckwNotNull = new JsonLogFilter();
        sckwNotNull.setJsonPath("$.glb_ubcta.sckw");
        sckwNotNull.setFilterRule("not_null");
        goodClickFilter.add(sckwNotNull);

        gcPv.setJsonLogFilters(goodClickFilter);
        gcUv.setJsonLogFilters(goodClickFilter);

        reportQuotaFieldConfigs.add(gcPv);
        reportQuotaFieldConfigs.add(gcUv);

        // ?????????????????? uv
        ReportQuotaFieldConfig cartUv = new ReportQuotaFieldConfig();
        cartUv.setQuotaFieldName("good_cart_uv");
        cartUv.setDefaultValue("_skip");
        cartUv.setExtractValueJsonPath("$.glb_od");
        cartUv.setValueEnum("quotaStringValueExtractFromLog");

        List<JsonLogFilter> cartFilter = new ArrayList<>();
        //cartFilter.add(glbFilterRecommend);
        cartFilter.add(plf);
        cartFilter.add(sckwNotNull);
        cartFilter.add(ic);
        //cartFilter.add(mpFilter);

        JsonLogFilter fmdMp = new JsonLogFilter();
        fmdMp.setJsonPath("$.glb_ubcta.fmd");
        fmdMp.setValueFilter("mp");
        cartFilter.add(fmdMp);

        JsonLogFilter ubcSort = new JsonLogFilter();
        ubcSort.setJsonPath("$.glb_ubcta.sort");
        ubcSort.setValueFilter("recommend");
        cartFilter.add(ubcSort);

        JsonLogFilter cartJsonFilter = new JsonLogFilter();
        cartJsonFilter.setJsonPath("$.glb_x");
        cartJsonFilter.setValueFilter("ADT");
        cartFilter.add(cartJsonFilter);

        cartUv.setJsonLogFilters(cartFilter);

        reportQuotaFieldConfigs.add(cartUv);

        ReportQuotaFieldConfig cartNum = new ReportQuotaFieldConfig();
        cartNum.setQuotaFieldName("good_cart_num");
        cartNum.setDefaultValue(0);
        cartNum.setExtractValueJsonPath("$.glb_skuinfo.pam");
        cartNum.setValueEnum("quotaIntValueExtractFromLog");
        cartNum.setJsonLogFilters(cartFilter);
        cartNum.setCacheData(true);

        reportQuotaFieldConfigs.add(cartNum);

        ReportQuotaFieldConfig btsQuota = new ReportQuotaFieldConfig();
        btsQuota.setQuotaFieldName("bts");
        btsQuota.setDefaultValue(bts);
        btsQuota.setExtractValueJsonPath("$.glb_bts");
        btsQuota.setValueEnum("extractMapValueFromLog");

        reportQuotaFieldConfigs.add(btsQuota);

        // ???????????????
        ReportQuotaFieldConfig timestamp = new ReportQuotaFieldConfig();
        timestamp.setQuotaFieldName("timestamp");
        timestamp.setDefaultValue(0);
        timestamp.setExtractValueJsonPath("$.timestamp");
        timestamp.setValueEnum("quotaLongValueExtractFromLog");

        reportQuotaFieldConfigs.add(timestamp);

    }

}
