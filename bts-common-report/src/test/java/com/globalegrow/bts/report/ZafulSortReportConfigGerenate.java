package com.globalegrow.bts.report;/**
 * Created by tangliuyi on 2019/3/7.
 */

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
 * @ClassName ZafulSortReportConfigGerenate
 * @Description TODO
 * @Author tangliuyi
 * @Date 2019/3/7 15:04
 * @Version 1.0
 */
public class ZafulSortReportConfigGerenate {

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

        //终端
//        JsonLogFilter btsPlatformNotNullFilter = new JsonLogFilter();
//        btsPlatformNotNullFilter.setJsonPath("$.glb_plf");
//        btsPlatformNotNullFilter.setFilterRule("not_null");
//        globaleFilters.add(btsPlatformNotNullFilter);

//        JsonLogFilter btsValuebucketidNotNullFilter = new JsonLogFilter();
//        btsValuebucketidNotNullFilter.setJsonPath("$.glb_bts.bucketid");
//        btsValuebucketidNotNullFilter.setFilterRule("not_null");
//        globaleFilters.add(btsValuebucketidNotNullFilter);

        JsonLogFilter siteFilter = new JsonLogFilter();
        siteFilter.setJsonPath("$.glb_d");
        siteFilter.setValueFilter("10013");
        globaleFilters.add(siteFilter);

        rule.setGlobaleJsonFilters(globaleFilters);

        rule.setReportName("BTS_ZAFUL_SORT_REPORT");
        rule.setDescription("ZAFUL PC+M类目页AB测试报表取数指标配置");
        ZafulSortReportQuotaModel zafulSortReportQuotaModel=new ZafulSortReportQuotaModel();

        rule.setReportDefaultValues(DyBeanUtils.objToMap(zafulSortReportQuotaModel));


        ReportKafkaConfig reportKafkaConfig = new ReportKafkaConfig();
        reportKafkaConfig.setBootstrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setDataSourceTopic("glbg-analitic");//埋点上报的topic
        reportKafkaConfig.setBootstrapGroupId("dy_bts_app_list_recommend_report_new");
        reportKafkaConfig.setReportStrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setReportDataTopic("dy_bts_app_list_recommend_report_new");

        rule.setReportFromKafka(reportKafkaConfig);


        List<ReportQuotaFieldConfig> reportQuotaFieldConfigs = new ArrayList<>();

        quota(reportQuotaFieldConfigs);

        rule.setReportQuotaFieldConfigs(reportQuotaFieldConfigs);

        System.out.println(JacksonUtil.toJSon(rule));
    }

    private void quota(List<ReportQuotaFieldConfig> reportQuotaFieldConfigs) {
        //商品曝光数
        ReportQuotaFieldConfig af_impressionPv = new ReportQuotaFieldConfig();
        af_impressionPv.setQuotaFieldName("exposure_count");
        af_impressionPv.setDefaultValue(0);
        af_impressionPv.setExtractValueJsonPath("$.glb_ubcta");
        af_impressionPv.setValueEnum("countListWithFilter");
        //过滤条件 取数条件：behaviour_type=ie，page_main_type=b，page_module=mp
        List<JsonLogFilter> impressionPvFilter = new ArrayList<>();
        JsonLogFilter glbType = new JsonLogFilter();
        glbType.setJsonPath("$.glb_t");
        glbType.setValueFilter("ie");

        JsonLogFilter glbB = new JsonLogFilter();
        glbB.setJsonPath("$.glb_b");
        glbB.setValueFilter("b");

        JsonLogFilter glbPm=new JsonLogFilter();
        glbPm.setJsonPath("$.glb_pm");
        glbPm.setValueFilter("mp");

        impressionPvFilter.add(glbType);
        impressionPvFilter.add(glbB);
        impressionPvFilter.add(glbPm);
        af_impressionPv.setJsonLogFilters(impressionPvFilter);
        reportQuotaFieldConfigs.add(af_impressionPv);
        //查看商品UV
        ReportQuotaFieldConfig af_impressionUv = new ReportQuotaFieldConfig();
        af_impressionUv.setQuotaFieldName("exposure_uv");
        af_impressionUv.setDefaultValue("_skip");
        af_impressionUv.setExtractValueJsonPath("$.glb_od");
        af_impressionUv.setValueEnum("quotaStringValueExtractFromLog");

        //取数条件：behaviour_type=ie，page_main_type=b，page_module=mp，sub_event_field不为空，计算cookie_id去重后的数量
        List<JsonLogFilter> af_impressionFilters = new ArrayList<>();
        JsonLogFilter subEvent = new JsonLogFilter();
        subEvent.setJsonPath("$.glb_ubcta");
        subEvent.setFilterRule("not_null");
        af_impressionFilters.add(glbType);
        af_impressionFilters.add(glbB);
        af_impressionFilters.add(glbPm);
        af_impressionFilters.add(subEvent);

        af_impressionUv.setJsonLogFilters(af_impressionFilters);
        reportQuotaFieldConfigs.add(af_impressionUv);

        //商品点击数
        ReportQuotaFieldConfig af_view_productPv = new ReportQuotaFieldConfig();
        af_view_productPv.setQuotaFieldName("good_click");
        af_view_productPv.setDefaultValue(0);
        af_view_productPv.setExtractValueJsonPath("$.glb_od");
        af_view_productPv.setValueEnum("countOneWithFilter");

        //点击UV
        ReportQuotaFieldConfig af_view_productUv = new ReportQuotaFieldConfig();
        af_view_productUv.setQuotaFieldName("good_click_uv");
        af_view_productUv.setDefaultValue("_skip");
        af_view_productUv.setExtractValueJsonPath("$.glb_od");
        af_view_productUv.setValueEnum("quotaStringValueExtractFromLog");

        //过滤条件取数条件：behaviour_type=ic，page_main_type=b，sub_event_info=sku或者=addtobag，page_module=mp，计算cookie_id的数量
        List<JsonLogFilter> af_view_productFilters = new ArrayList<>();

        JsonLogFilter glbIc = new JsonLogFilter();
        glbIc.setValueFilter("ic");
        glbIc.setJsonPath("$.glb_t");
        af_view_productFilters.add(glbIc);
        af_view_productFilters.add(glbB);
        af_view_productFilters.add(glbPm);

        JsonLogFilter glbX=new JsonLogFilter();
        glbX.setJsonPath("$.glb_x");
        glbX.setFilterRule("contains");
        glbX.setValueFilter("sku,addtobag");
        af_view_productFilters.add(glbX);

        af_view_productPv.setJsonLogFilters(af_view_productFilters);
        af_view_productUv.setJsonLogFilters(af_view_productFilters);

        reportQuotaFieldConfigs.add(af_view_productPv);
        reportQuotaFieldConfigs.add(af_view_productUv);

        //商品加购数
        ReportQuotaFieldConfig af_add_to_bagPv = new ReportQuotaFieldConfig();
        af_add_to_bagPv.setQuotaFieldName("good_add_cart");
        af_add_to_bagPv.setDefaultValue(0);
        af_add_to_bagPv.setExtractValueJsonPath("$.glb_od");
        af_add_to_bagPv.setValueEnum("quotaIntValueExtractFromLog");
        //加购UV
        ReportQuotaFieldConfig af_add_to_bagUv = new ReportQuotaFieldConfig();
        af_add_to_bagUv.setQuotaFieldName("good_add_cart_uv");
        af_add_to_bagUv.setDefaultValue("_skip");
        af_add_to_bagUv.setExtractValueJsonPath("$.glb_od");
        af_add_to_bagUv.setValueEnum("quotaStringValueExtractFromLog");

        //取数条件：behaviour_type=ic，sub_event_info=ADT，sub_event_field的json字段fmd=mp
        List<JsonLogFilter> af_add_to_bagFilters = new ArrayList<>();
        JsonLogFilter glbAdt = new JsonLogFilter();
        glbAdt.setValueFilter("ADT");
        glbAdt.setJsonPath("$.glb_x");
        af_add_to_bagFilters.add(glbAdt);
        af_add_to_bagFilters.add(glbIc);

        JsonLogFilter fmd=new JsonLogFilter();
        fmd.setJsonPath("$.glb_ubcta.fmd");
        fmd.setValueFilter("mp");
        af_add_to_bagFilters.add(fmd);

        af_add_to_bagPv.setJsonLogFilters(af_add_to_bagFilters);
        af_add_to_bagUv.setJsonLogFilters(af_add_to_bagFilters);

        reportQuotaFieldConfigs.add(af_add_to_bagPv);
        reportQuotaFieldConfigs.add(af_add_to_bagUv);


        //商品收藏数
        ReportQuotaFieldConfig af_add_to_wishlistPv = new ReportQuotaFieldConfig();
        af_add_to_wishlistPv.setQuotaFieldName("good_collect");
        af_add_to_wishlistPv.setDefaultValue(0);
        af_add_to_wishlistPv.setExtractValueJsonPath("$.glb_od");
        af_add_to_wishlistPv.setValueEnum("countOneWithFilter");
        //商品收藏UV
        ReportQuotaFieldConfig af_add_to_wishlistUv = new ReportQuotaFieldConfig();
        af_add_to_wishlistUv.setQuotaFieldName("good_collect_uv");
        af_add_to_wishlistUv.setDefaultValue("_skip");
        af_add_to_wishlistUv.setExtractValueJsonPath("$.glb_od");
        af_add_to_wishlistUv.setValueEnum("quotaStringValueExtractFromLog");

        //behaviour_type=ic，sub_event_info=ADF，sub_event_field的json字段fmd=mp，统计cookie_id去重后的数量
        List<JsonLogFilter> af_add_to_wishlistFilters = new ArrayList<>();
        JsonLogFilter adf = new JsonLogFilter();
        adf.setValueFilter("ADF");
        adf.setJsonPath("$.glb_x");
        af_add_to_wishlistFilters.add(adf);
        af_add_to_wishlistFilters.add(glbIc);
        af_add_to_wishlistFilters.add(fmd);

        af_add_to_wishlistPv.setJsonLogFilters(af_add_to_wishlistFilters);
        af_add_to_wishlistUv.setJsonLogFilters(af_add_to_wishlistFilters);

        reportQuotaFieldConfigs.add(af_add_to_wishlistPv);
        reportQuotaFieldConfigs.add(af_add_to_wishlistUv);

        // 样本量指标
        ReportQuotaFieldConfig specimen = new ReportQuotaFieldConfig();
        specimen.setQuotaFieldName("specimen");
        specimen.setDefaultValue("_skip");
        specimen.setExtractValueJsonPath("$.glb_od");
        specimen.setValueEnum("quotaStringValueExtractFromLog");
        reportQuotaFieldConfigs.add(specimen);

        // bts 3 个字段
        ReportQuotaFieldConfig btsQuota = new ReportQuotaFieldConfig();
        btsQuota.setQuotaFieldName("bts");
        btsQuota.setDefaultValue(bts);
        btsQuota.setExtractValueJsonPath("$.glb_bts");
        //PC端extractBtsMapValueFromLog   APP端:extractMapValueFromLog
        btsQuota.setValueEnum("extractBtsMapValueFromLog");
        reportQuotaFieldConfigs.add(btsQuota);

        ReportQuotaFieldConfig platform=new ReportQuotaFieldConfig();
        platform.setQuotaFieldName("platform");
        platform.setDefaultValue("_skip");
        platform.setExtractValueJsonPath("$.glb_plf");
        platform.setValueEnum("quotaStringValueExtractFromLog");
        reportQuotaFieldConfigs.add(platform);

        // 时间戳字段
        ReportQuotaFieldConfig timestamp = new ReportQuotaFieldConfig();
        timestamp.setQuotaFieldName("timestamp");
        timestamp.setDefaultValue(0);
        timestamp.setExtractValueJsonPath("$.timestamp");
        timestamp.setValueEnum("quotaLongValueExtractFromLog");
        reportQuotaFieldConfigs.add(timestamp);

    }
}
