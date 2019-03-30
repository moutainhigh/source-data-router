package com.globalegrow.bts.report;/**
 * Created by tangliuyi on 2019/1/9.
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
 * @ClassName BtsGbSearchReportConfigJson
 * @Description GB搜索实验指标
 * @Author tangliuyi
 * @Date 2019/1/9 9:44
 * @Version 1.0
 */
public class BtsGbSearchReportConfigJson {
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

        JsonLogFilter siteFilter = new JsonLogFilter();
        siteFilter.setJsonPath("$.glb_d");
        siteFilter.setValueFilter("10002");
        globaleFilters.add(siteFilter);

        rule.setGlobaleJsonFilters(globaleFilters);

        rule.setReportName("BTS_GB_SEARCH_REPORT");
        rule.setDescription("GB搜索AB测试报表取数指标配置");
        GbSearchReportQuota gbSearchReportQuota=new GbSearchReportQuota();

        rule.setReportDefaultValues(DyBeanUtils.objToMap(gbSearchReportQuota));


        ReportKafkaConfig reportKafkaConfig = new ReportKafkaConfig();
        reportKafkaConfig.setBootstrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setDataSourceTopic("glbg-analitic");//埋点上报的topic
        reportKafkaConfig.setBootstrapGroupId("gb_search_report");
        reportKafkaConfig.setReportStrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setReportDataTopic("gb_search_report");

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
        JsonLogFilter plf = new JsonLogFilter();
        plf.setJsonPath("$.glb_plf");
        plf.setValueFilter("pc");
        specimenFilter.add(plf);

        JsonLogFilter country = new JsonLogFilter();
        country.setJsonPath("$.glb_dc");
        country.setValueFilter("1301");
        specimenFilter.add(country);

        specimen.setJsonLogFilters(specimenFilter);
        reportQuotaFieldConfigs.add(specimen);

        //搜索页relevance PV glb_plf=pc,glb_t=ie, glb_cl中含有 -_gear，glb_ubcta为空，且glb_filter的json字段中sort='relevance',glb_plf=pc,glb_dc=1301,计算glb_oi的数量
        ReportQuotaFieldConfig relevancePv = new ReportQuotaFieldConfig();
        relevancePv.setQuotaFieldName("relevance_pv");
        relevancePv.setDefaultValue(0);
        relevancePv.setExtractValueJsonPath("$.glb_od");
        relevancePv.setValueEnum("countOneWithFilter");
        List<JsonLogFilter> relevancePvFilter = new ArrayList<>();
        relevancePvFilter.add(plf);
        relevancePvFilter.add(country);

        JsonLogFilter glbT=new JsonLogFilter();
        glbT.setJsonPath("$.glb_t");
        glbT.setValueFilter("ie");
        relevancePvFilter.add(glbT);

        JsonLogFilter glbCl=new JsonLogFilter();
        glbCl.setJsonPath("$.glb_cl");
        glbCl.setFilterRule("contains_backward");
        glbCl.setValueFilter("-_gear");

        relevancePvFilter.add(glbCl);

        JsonLogFilter glbUbcta=new JsonLogFilter();
        glbUbcta.setJsonPath("$.glb_ubcta");
        glbUbcta.setFilterRule("null");
        relevancePvFilter.add(glbUbcta);

        JsonLogFilter glbFilter=new JsonLogFilter();
        glbFilter.setJsonPath("$.glb_filter.sort");
        glbFilter.setValueFilter("relevance");
        relevancePvFilter.add(glbFilter);
        relevancePv.setJsonLogFilters(relevancePvFilter);
        reportQuotaFieldConfigs.add(relevancePv);
        //搜索页relevance UV	glb_plf=pc,glb_t=ie,glb_cl中含有 -_gear，glb_ubcta为空，且glb_filter的json字段中sort='relevance',glb_plf=pc,glb_dc=1301,计算glb_oi去重后的数量
        ReportQuotaFieldConfig relevanceUv = new ReportQuotaFieldConfig();
        relevanceUv.setQuotaFieldName("relevance_uv");
        relevanceUv.setDefaultValue("_skip");
        relevanceUv.setExtractValueJsonPath("$.glb_od");
        relevanceUv.setValueEnum("quotaStringValueExtractFromLog");
        relevanceUv.setJsonLogFilters(relevancePvFilter);
        reportQuotaFieldConfigs.add(relevanceUv);
        //商品曝光 PV	glb_plf=pc,glb_t=ie,glb_cl中含有 -_gear,且glb_filter的json字段中sort='relevance'，glb_pm=mp,glb_plf=pc,glb_dc=1301,取bts的json中的 policy字段，计算glb_ubcta中json字段sku的数量
        ReportQuotaFieldConfig expPv = new ReportQuotaFieldConfig();
        expPv.setQuotaFieldName("exp_pv");
        expPv.setDefaultValue(0);
        expPv.setExtractValueJsonPath("$.glb_od");
        expPv.setValueEnum("countOneWithFilter");
        List<JsonLogFilter> expPvFilter = new ArrayList<>();
        expPvFilter.add(plf);
        expPvFilter.add(country);
        expPvFilter.add(glbT);
        expPvFilter.add(glbCl);
        expPvFilter.add(glbFilter);

        JsonLogFilter glbPm=new JsonLogFilter();
        glbPm.setJsonPath("glb_pm");
        glbPm.setValueFilter("mp");
        expPvFilter.add(glbPm);
        expPv.setJsonLogFilters(expPvFilter);
        reportQuotaFieldConfigs.add(expPv);
        //商品曝光 UV	glb_plf=pc,glb_t=ie,glb_cl中含有 -_gear,且glb_filter的json字段中sort='relevance'，glb_pm=mp,glb_plf=pc,glb_dc=1301,取bts的json中的 policy字段，计算glb_oi去重后的数量，
        ReportQuotaFieldConfig expUv=new ReportQuotaFieldConfig();
        expUv.setQuotaFieldName("exp_uv");
        expUv.setDefaultValue("_skip");
        expUv.setExtractValueJsonPath("$.glb_od");
        expUv.setValueEnum("quotaStringValueExtractFromLog");
        expUv.setJsonLogFilters(expPvFilter);
        reportQuotaFieldConfigs.add(expUv);

        //商品点击 PV	glb_plf=pc,glb_t=ic,glb_cl中含有 -_gear,glb_pm=mp,且glb_filter的json字段中sort='relevance'，且glb_ubcta字段中有sckw字段值,glb_plf=pc,glb_dc=1301，取bts的json中的 policy字段，计算glb_oi的数量
        ReportQuotaFieldConfig goodsClickPv=new ReportQuotaFieldConfig();
        goodsClickPv.setQuotaFieldName("goods_click_pv");
        goodsClickPv.setDefaultValue(0);
        goodsClickPv.setExtractValueJsonPath("$.glb_od");
        goodsClickPv.setValueEnum("countOneWithFilter");
        List<JsonLogFilter> goodsClickPvFilter = new ArrayList<>();
        goodsClickPvFilter.add(plf);
        JsonLogFilter glbTic=new JsonLogFilter();
        glbTic.setJsonPath("$.glb_t");
        glbTic.setValueFilter("ic");
        goodsClickPvFilter.add(glbTic);
        goodsClickPvFilter.add(glbCl);
        goodsClickPvFilter.add(glbPm);
        goodsClickPvFilter.add(glbFilter);
        JsonLogFilter glbUbctaNotnull=new JsonLogFilter();
        glbUbctaNotnull.setJsonPath("$.glb_ubcta.sckw");
        glbUbctaNotnull.setFilterRule("not_null");
        goodsClickPvFilter.add(glbUbctaNotnull);
        goodsClickPv.setJsonLogFilters(goodsClickPvFilter);
        reportQuotaFieldConfigs.add(goodsClickPv);
        // 商品点击 UV	glb_plf=pc,glb_t=ic,glb_cl中含有 -_gear,glb_pm=mp,且glb_filter的json字段中sort='relevance'，且glb_ubcta字段中有sckw字段值,glb_plf=pc,glb_dc=1301，取bts的json中的 policy字段，计算glb_oi去重后的数量
        ReportQuotaFieldConfig goodsClickUv=new ReportQuotaFieldConfig();
        goodsClickUv.setQuotaFieldName("goods_click_uv");
        goodsClickUv.setDefaultValue("_skip");
        goodsClickUv.setExtractValueJsonPath("$.glb_od");
        goodsClickUv.setValueEnum("quotaStringValueExtractFromLog");

        goodsClickUv.setJsonLogFilters(goodsClickPvFilter);
        reportQuotaFieldConfigs.add(goodsClickUv);
        //商品加购次数	glb_t=ic,(glb_cl中含有 -_gear 且 x = ADT) 或者 (glb_pl中含有 -_gear 且 glb_x=ADT或BDR) , glb_ubcta中json字段fmd=mp且json字段有sckw值,glb_plf=pc,glb_dc=1301，取bts的json中的 policy字段，计算glb_oi的数量
        ReportQuotaFieldConfig goodsCarPv=new ReportQuotaFieldConfig();
        goodsCarPv.setQuotaFieldName("goods_car_pv");
        goodsCarPv.setDefaultValue(0);
        goodsCarPv.setExtractValueJsonPath("$.glb_od");
        goodsCarPv.setValueEnum("countOneWithFilter");
        List<JsonLogFilter> goodsCarPvFilter = new ArrayList<>();
        goodsCarPvFilter.add(plf);
        goodsCarPvFilter.add(country);
        goodsCarPvFilter.add(glbTic);
        goodsCarPvFilter.add(glbCl);
        JsonLogFilter glbX=new JsonLogFilter();
        glbX.setJsonPath("$.glb_x");
        glbX.setValueFilter("ADT");
        goodsCarPvFilter.add(glbX);
        JsonLogFilter ubcFmd=new JsonLogFilter();
        ubcFmd.setJsonPath("$.glb_ubcta.fmd");
        ubcFmd.setValueFilter("mp");
        goodsCarPvFilter.add(ubcFmd);
        goodsCarPvFilter.add(glbUbctaNotnull);

        goodsCarPv.setJsonLogFilters(goodsCarPvFilter);
        reportQuotaFieldConfigs.add(goodsCarPv);

        ReportQuotaFieldConfig goodsCarPv2=new ReportQuotaFieldConfig();
        goodsCarPv2.setQuotaFieldName("goods_car_pv");
        goodsCarPv2.setDefaultValue(0);
        goodsCarPv2.setExtractValueJsonPath("$.glb_od");
        goodsCarPv2.setValueEnum("countOneWithFilter");
        List<JsonLogFilter> goodsCarPv2Filter = new ArrayList<>();
        goodsCarPv2Filter.add(plf);
        goodsCarPv2Filter.add(country);
        goodsCarPv2Filter.add(glbTic);

        JsonLogFilter glbPl=new JsonLogFilter();
        glbPl.setJsonPath("$.glb_pl");
        glbPl.setFilterRule("contains_backward");
        glbPl.setValueFilter("-_gear");
        goodsCarPv2Filter.add(glbPl);

        JsonLogFilter glbX2=new JsonLogFilter();
        glbX2.setJsonPath("$.glb_x");
        glbX2.setFilterRule("or");
        glbX2.setValueFilter("ADT,BDR");
        goodsCarPv2Filter.add(glbX2);

        goodsCarPv2Filter.add(glbUbctaNotnull);
        goodsCarPv2Filter.add(ubcFmd);
        goodsCarPv2.setJsonLogFilters(goodsCarPv2Filter);
        reportQuotaFieldConfigs.add(goodsCarPv2);

        //加购UV	glb_t=ic,(glb_cl中含有 -_gear 且 x = ADT) 或者 (glb_pl中含有 -_gear 且 glb_x=ADT或BDR) , glb_ubcta中json字段fmd=mp且json字段有sckw值,glb_plf=pc,glb_dc=1301，取bts的json中的 policy字段，计算glb_oi的去重数量
        ReportQuotaFieldConfig goodsCarUv=new ReportQuotaFieldConfig();
        goodsCarUv.setQuotaFieldName("goods_car_uv");
        goodsCarUv.setDefaultValue("_skip");
        goodsCarUv.setExtractValueJsonPath("$.glb_od");
        goodsCarUv.setValueEnum("quotaStringValueExtractFromLog");
        goodsCarUv.setJsonLogFilters(goodsCarPvFilter);
        reportQuotaFieldConfigs.add(goodsCarUv);

        ReportQuotaFieldConfig goodsCarUv2=new ReportQuotaFieldConfig();
        goodsCarUv2.setQuotaFieldName("goods_car_uv");
        goodsCarUv2.setDefaultValue("_skip");
        goodsCarUv2.setExtractValueJsonPath("$.glb_od");
        goodsCarUv2.setValueEnum("quotaStringValueExtractFromLog");
        goodsCarUv2.setJsonLogFilters(goodsCarPv2Filter);
        reportQuotaFieldConfigs.add(goodsCarUv2);

        ReportQuotaFieldConfig btsQuota = new ReportQuotaFieldConfig();
        btsQuota.setQuotaFieldName("bts");
        btsQuota.setDefaultValue(bts);
        btsQuota.setExtractValueJsonPath("$.glb_bts");
        //extractBtsMapValueFromLog PC端  APP端:extractMapValueFromLog
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
