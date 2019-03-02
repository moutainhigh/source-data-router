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

public class PCZafulRecommendReportConfigGerenate {

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

        rule.setReportName("BTS_ZAFUL_ORDER_PC_RECOMMEND_REC");
        rule.setDescription("zaful pc 端购物车推荐报表指标配置");
        ZafulPcRecommendReportModel searchRecommendReportQuotaModel = new ZafulPcRecommendReportModel();

        rule.setReportDefaultValues(DyBeanUtils.objToMap(searchRecommendReportQuotaModel));


        ReportKafkaConfig reportKafkaConfig = new ReportKafkaConfig();
        reportKafkaConfig.setBootstrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setDataSourceTopic("glbg-analitic");
        reportKafkaConfig.setBootstrapGroupId("dy_bts_zaful_pc_recommend_rec_report");
        reportKafkaConfig.setReportStrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setReportDataTopic("dy_bts_zaful_pc_recommend_rec_report");

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
        JsonLogFilter tie = new JsonLogFilter();
        tie.setJsonPath("$.glb_t");
        tie.setValueFilter("ie");
        specimenFilter.add(tie);
        specimen.setJsonLogFilters(specimenFilter);

        reportQuotaFieldConfigs.add(specimen);

        // 商品曝光数
        List<JsonLogFilter> expFilter = new ArrayList<>();
        expFilter.add(tie);
        JsonLogFilter mpFilter = new JsonLogFilter();
        mpFilter.setJsonPath("$.glb_pm");
        mpFilter.setValueFilter("mr");
        expFilter.add(mpFilter);
        JsonLogFilter bFilter = new JsonLogFilter();
        bFilter.setJsonPath("$.glb_b");
        bFilter.setValueFilter("d");
        expFilter.add(bFilter);
        JsonLogFilter mrlcExp = new JsonLogFilter();
        mrlcExp.setJsonPath("$.glb_ubcta[0].mrlc");
        mrlcExp.setValueFilter("T_8");
        mrlcExp.setFilterRule("array_field_filter");
        expFilter.add(mrlcExp);
        ReportQuotaFieldConfig expPv = new ReportQuotaFieldConfig();
        expPv.setQuotaFieldName("good_exp_pv");
        expPv.setDefaultValue(0);
        expPv.setExtractValueJsonPath("$.glb_ubcta");
        expPv.setValueEnum("countListWithFilter");
        expPv.setJsonLogFilters(expFilter);
        reportQuotaFieldConfigs.add(expPv);
        //查看商品UV
        ReportQuotaFieldConfig expUv = new ReportQuotaFieldConfig();
        expUv.setQuotaFieldName("good_exp_uv");
        expUv.setDefaultValue("_skip");
        expUv.setExtractValueJsonPath("$.glb_od");
        expUv.setValueEnum("quotaStringValueExtractFromLog");
        expUv.setJsonLogFilters(expFilter);
        reportQuotaFieldConfigs.add(expUv);

        // 商品点击 数
        ReportQuotaFieldConfig gcPv = new ReportQuotaFieldConfig();
        gcPv.setQuotaFieldName("good_cli_pv");
        gcPv.setDefaultValue(0);
        gcPv.setExtractValueJsonPath("$.glb_ubcta");
        gcPv.setValueEnum("countOneWithFilter");
        //点击用户量
        ReportQuotaFieldConfig gcUv = new ReportQuotaFieldConfig();
        gcUv.setQuotaFieldName("good_cli_uv");
        gcUv.setDefaultValue("_skip");
        gcUv.setExtractValueJsonPath("$.glb_od");
        gcUv.setValueEnum("quotaStringValueExtractFromLog");

        List<JsonLogFilter> goodClickFilter = new ArrayList<>();
        goodClickFilter.add(mpFilter);
        goodClickFilter.add(bFilter);
        JsonLogFilter mrlc = new JsonLogFilter();
        mrlc.setJsonPath("$.glb_ubcta.mrlc");
        mrlc.setValueFilter("T_8");
        goodClickFilter.add(mrlc);
        JsonLogFilter ic = new JsonLogFilter();
        ic.setJsonPath("$.glb_t");
        ic.setValueFilter("ic");
        goodClickFilter.add(ic);

        JsonLogFilter sku = new JsonLogFilter();
        sku.setJsonPath("$.glb_x");
        sku.setValueFilter("sku");
        goodClickFilter.add(sku);

        gcPv.setJsonLogFilters(goodClickFilter);
        gcUv.setJsonLogFilters(goodClickFilter);
        reportQuotaFieldConfigs.add(gcPv);
        reportQuotaFieldConfigs.add(gcUv);

        // 加购数
        List<JsonLogFilter> cartFilter = new ArrayList<>();
        cartFilter.add(ic);
        JsonLogFilter fmdFilter = new JsonLogFilter();
        fmdFilter.setJsonPath("$.glb_ubcta.fmd");
        fmdFilter.setValueFilter("mr_T_8");
        cartFilter.add(fmdFilter);

        JsonLogFilter cartJsonFilter = new JsonLogFilter();
        cartJsonFilter.setJsonPath("$.glb_x");
        cartJsonFilter.setValueFilter("ADT");
        cartFilter.add(cartJsonFilter);

        ReportQuotaFieldConfig cartNum = new ReportQuotaFieldConfig();
        cartNum.setQuotaFieldName("good_cart_num");
        cartNum.setDefaultValue(0);
        cartNum.setExtractValueJsonPath("$.glb_skuinfo.pam");
        cartNum.setValueEnum("countOneWithFilter");
        cartNum.setJsonLogFilters(cartFilter);
        cartNum.setCacheData(true);
        //加购用户数
        ReportQuotaFieldConfig cartUv = new ReportQuotaFieldConfig();
        cartUv.setQuotaFieldName("good_cart_uv");
        cartUv.setDefaultValue("_skip");
        cartUv.setExtractValueJsonPath("$.glb_od");
        cartUv.setValueEnum("quotaStringValueExtractFromLog");
        cartUv.setJsonLogFilters(cartFilter);

        reportQuotaFieldConfigs.add(cartNum);
        reportQuotaFieldConfigs.add(cartUv);

        //商品收藏数
        ReportQuotaFieldConfig goodCollectNum=new ReportQuotaFieldConfig();
        goodCollectNum.setQuotaFieldName("good_collect_num");
        goodCollectNum.setDefaultValue(0);
        goodCollectNum.setExtractValueJsonPath("$.glb_t");
        goodCollectNum.setValueEnum("countOneWithFilter");
        //加收用户数
        ReportQuotaFieldConfig goodCollectUV=new ReportQuotaFieldConfig();
        goodCollectUV.setQuotaFieldName("good_collect_uv");
        goodCollectUV.setDefaultValue("_skip");
        goodCollectUV.setExtractValueJsonPath("$.glb_od");
        goodCollectUV.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件
        List<JsonLogFilter> goodCollectFilter = new ArrayList<>();
        goodCollectFilter.add(ic);
        goodCollectFilter.add(fmdFilter);
        JsonLogFilter adfJsonFilter = new JsonLogFilter();
        adfJsonFilter.setJsonPath("$.glb_x");
        adfJsonFilter.setValueFilter("ADF");
        goodCollectFilter.add(adfJsonFilter);
        JsonLogFilter uFilter = new JsonLogFilter();
        uFilter.setJsonPath("$.glb_u");
        uFilter.setFilterRule("not_null");
        goodCollectFilter.add(uFilter);

        goodCollectNum.setJsonLogFilters(goodCollectFilter);
        reportQuotaFieldConfigs.add(goodCollectNum);
        goodCollectUV.setJsonLogFilters(goodCollectFilter);
        reportQuotaFieldConfigs.add(goodCollectUV);

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
