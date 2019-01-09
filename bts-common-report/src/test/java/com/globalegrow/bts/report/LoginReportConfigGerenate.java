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
 * @ClassName LoginReportConfigGerenate
 * @Description zaful PC 快速登录报表取数指标
 * @Author tangliuyi
 * @Date 2018/11/5 10:35
 * @Version 1.0
 */
public class LoginReportConfigGerenate {

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

        rule.setReportName("BTS_ZAFUL_PC_QUICK_LOGIN");
        rule.setDescription("zaful PC 快速登录AB测试报表取数指标配置");
        QuickLoginReportQuotaModel quickLoginReportQuotaModel=new QuickLoginReportQuotaModel();

        rule.setReportDefaultValues(DyBeanUtils.objToMap(quickLoginReportQuotaModel));


        ReportKafkaConfig reportKafkaConfig = new ReportKafkaConfig();
        reportKafkaConfig.setBootstrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setDataSourceTopic("glbg-analitic");//埋点上报的topic
        reportKafkaConfig.setBootstrapGroupId("dy_bts_quick_login_report");
        reportKafkaConfig.setReportStrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setReportDataTopic("dy_bts_quick_login_report");

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

//        JsonLogFilter tie = new JsonLogFilter();
//        tie.setJsonPath("$.glb_t");
//        tie.setValueFilter("ie");
//        specimenFilter.add(tie);

//        JsonLogFilter glbs = new JsonLogFilter();
//        glbs.setJsonPath("$.glb_s");
//        glbs.setValueFilter("b02");
//        specimenFilter.add(glbs);

//        JsonLogFilter ubc = new JsonLogFilter();
//        ubc.setJsonPath("$.glb_ubcta");
//        ubc.setFilterRule("null");
//        specimenFilter.add(ubc);
         specimen.setJsonLogFilters(specimenFilter);

        reportQuotaFieldConfigs.add(specimen);

        // 购物车 UV
        ReportQuotaFieldConfig shoppingCarUv = new ReportQuotaFieldConfig();
        shoppingCarUv.setQuotaFieldName("shopping_car_uv");
        shoppingCarUv.setDefaultValue("_skip");
        shoppingCarUv.setExtractValueJsonPath("$.glb_od");
        shoppingCarUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件
        List<JsonLogFilter> carUvFilter = new ArrayList<>();
        JsonLogFilter pageSubType = new JsonLogFilter();
        pageSubType.setJsonPath("$.glb_s");
        pageSubType.setValueFilter("d01");
        carUvFilter.add(pageSubType);

        JsonLogFilter platform = new JsonLogFilter();
        platform.setJsonPath("$.glb_plf");
        platform.setValueFilter("pc");
        carUvFilter.add(platform);

        shoppingCarUv.setJsonLogFilters(carUvFilter);
        reportQuotaFieldConfigs.add(shoppingCarUv);

        // 购物车 PV
        ReportQuotaFieldConfig shoppingCarPv = new ReportQuotaFieldConfig();
        shoppingCarPv.setQuotaFieldName("shopping_car_pv");
        shoppingCarPv.setDefaultValue(0);
        shoppingCarPv.setExtractValueJsonPath("$.glb_od");
        shoppingCarPv.setValueEnum("countOneWithFilter");
        shoppingCarPv.setJsonLogFilters(carUvFilter);
        reportQuotaFieldConfigs.add(shoppingCarPv);

        // check out点击PV
        ReportQuotaFieldConfig checkOutPv = new ReportQuotaFieldConfig();
        checkOutPv.setQuotaFieldName("check_out_pv");
        checkOutPv.setDefaultValue(0);
        checkOutPv.setExtractValueJsonPath("$.glb_od");
        checkOutPv.setValueEnum("countOneWithFilter");
        //过滤条件
        List<JsonLogFilter> checkOutPvFilter = new ArrayList<>();
        //page_sub_type=d01，platform=pc，sub_event_info=checkout，统计cookie_id数量
        JsonLogFilter eventInfo = new JsonLogFilter();
        eventInfo.setJsonPath("$.glb_x");
        eventInfo.setValueFilter("checkout");
        checkOutPvFilter.add(pageSubType);
        checkOutPvFilter.add(platform);
        checkOutPvFilter.add(eventInfo);

        checkOutPv.setJsonLogFilters(checkOutPvFilter);
        reportQuotaFieldConfigs.add(checkOutPv);

        // check out点击UV
        ReportQuotaFieldConfig checkOutUv = new ReportQuotaFieldConfig();
        checkOutUv.setQuotaFieldName("check_out_uv");
        checkOutUv.setDefaultValue("_skip");
        checkOutUv.setExtractValueJsonPath("$.glb_od");
        checkOutUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件 page_sub_type=d01，platform=pc，sub_event_info=checkout，统计cookie_id去重后数量
        checkOutUv.setJsonLogFilters(checkOutPvFilter);
        reportQuotaFieldConfigs.add(checkOutUv);


        // 登录点击PV 原始版本
        ReportQuotaFieldConfig oldLoginPv = new ReportQuotaFieldConfig();
        oldLoginPv.setQuotaFieldName("login_pv");
        oldLoginPv.setDefaultValue(0);
        oldLoginPv.setExtractValueJsonPath("$.glb_od");
        oldLoginPv.setValueEnum("countOneWithFilter");
        //过滤条件 原始版本：page_main_type=f，platform=pc，sub_event_info=SIGNIN-B，
        // 且last_page_url=https://cart.zaful.com/shopping-cart.html统计cookie_id数量
        List<JsonLogFilter> oldLoginPvFilter = new ArrayList<>();
        JsonLogFilter pageMainType = new JsonLogFilter();
        pageMainType.setJsonPath("$.glb_b");
        pageMainType.setValueFilter("f");
        oldLoginPvFilter.add(pageMainType);
        oldLoginPvFilter.add(platform);

        JsonLogFilter subEventInfoB = new JsonLogFilter();
        subEventInfoB.setJsonPath("$.glb_x");
        subEventInfoB.setValueFilter("SIGNIN-B");
        oldLoginPvFilter.add(subEventInfoB);
        JsonLogFilter pageUrl=new JsonLogFilter();
        pageUrl.setJsonPath("$.glb_pl");
        pageUrl.setValueFilter("https://cart.zaful.com/shopping-cart.html");
        oldLoginPvFilter.add(pageUrl);
        oldLoginPv.setJsonLogFilters(oldLoginPvFilter);
        reportQuotaFieldConfigs.add(oldLoginPv);

        // 登录点击PV 新版本
        ReportQuotaFieldConfig newLoginPv = new ReportQuotaFieldConfig();
        newLoginPv.setQuotaFieldName("login_pv");
        newLoginPv.setDefaultValue(0);
        newLoginPv.setExtractValueJsonPath("$.glb_od");
        newLoginPv.setValueEnum("countOneWithFilter");
        //过滤条件 新版本：page_sub_type=d01，platform=pc，sub_event_info=SIGNIN-A，统计cookie_id数量
        List<JsonLogFilter> newLoginPvFilter = new ArrayList<>();
        newLoginPvFilter.add(pageSubType);
        newLoginPvFilter.add(platform);

        JsonLogFilter subEventInfoA = new JsonLogFilter();
        subEventInfoA.setJsonPath("$.glb_x");
        subEventInfoA.setValueFilter("SIGNIN-A");
        newLoginPvFilter.add(subEventInfoA);
        newLoginPv.setJsonLogFilters(newLoginPvFilter);
        reportQuotaFieldConfigs.add(newLoginPv);

       // 登录点击UV 原始版本
        ReportQuotaFieldConfig oldLoginUv = new ReportQuotaFieldConfig();
        oldLoginUv.setQuotaFieldName("login_uv");
        oldLoginUv.setDefaultValue("_skip");
        oldLoginUv.setExtractValueJsonPath("$.glb_od");
        oldLoginUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件 原始版本：page_main_type=f，platform=pc，sub_event_info=SIGNIN-B，
        // 且last_page_url=https://cart.zaful.com/shopping-cart.html统计cookie_id去重后的数量
        List<JsonLogFilter> oldLoginUvFilter = new ArrayList<>();
        oldLoginUvFilter.add(pageMainType);
        oldLoginUvFilter.add(platform);
        oldLoginUvFilter.add(subEventInfoB);
        oldLoginUvFilter.add(pageUrl);
        oldLoginUv.setJsonLogFilters(oldLoginUvFilter);
        reportQuotaFieldConfigs.add(oldLoginUv);

       // 登录点击UV 新版本
        ReportQuotaFieldConfig newLoginUv = new ReportQuotaFieldConfig();
        newLoginUv.setQuotaFieldName("login_uv");
        newLoginUv.setDefaultValue("_skip");
        newLoginUv.setExtractValueJsonPath("$.glb_od");
        newLoginUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件 page_sub_type=d01，platform=pc，sub_event_info=SIGNIN-A，统计cookie_id去重后的数量
        List<JsonLogFilter> newLoginUvFilter = new ArrayList<>();
        newLoginUvFilter.add(pageSubType);
        newLoginUvFilter.add(platform);
        newLoginUvFilter.add(subEventInfoA);
        newLoginUv.setJsonLogFilters(newLoginUvFilter);
        reportQuotaFieldConfigs.add(newLoginUv);

        //注册点击PV 原始版本
        ReportQuotaFieldConfig oldRegisterPv = new ReportQuotaFieldConfig();
        oldRegisterPv.setQuotaFieldName("register_pv");
        oldRegisterPv.setDefaultValue(0);
        oldRegisterPv.setExtractValueJsonPath("$.glb_od");
        oldRegisterPv.setValueEnum("countOneWithFilter");
        //过滤条件原始版本：page_main_type=f，platform=pc，sub_event_info=CRAAT，
        // 且last_page_url=https://cart.zaful.com/shopping-cart.html统计cookie_id数量
        List<JsonLogFilter> oldRegisterPvFilter = new ArrayList<>();
        oldRegisterPvFilter.add(pageMainType);
        oldRegisterPvFilter.add(platform);

        JsonLogFilter craatEventInfo = new JsonLogFilter();
        craatEventInfo.setJsonPath("$.glb_x");
        craatEventInfo.setValueFilter("CRAAT");

        oldRegisterPvFilter.add(craatEventInfo);
        oldRegisterPvFilter.add(pageUrl);
        oldRegisterPv.setJsonLogFilters(oldRegisterPvFilter);
        reportQuotaFieldConfigs.add(oldRegisterPv);

        //注册点击PV 新版本
        ReportQuotaFieldConfig newRegisterPv = new ReportQuotaFieldConfig();
        newRegisterPv.setQuotaFieldName("register_pv");
        newRegisterPv.setDefaultValue(0);
        newRegisterPv.setExtractValueJsonPath("$.glb_od");
        newRegisterPv.setValueEnum("countOneWithFilter");
        //过滤条件原始版本：page_sub_type=d01，platform=pc，sub_event_info=JOININ，统计cookie_id数量
        List<JsonLogFilter> newRegisterPvFilter = new ArrayList<>();
        newRegisterPvFilter.add(pageSubType);
        newRegisterPvFilter.add(platform);

        JsonLogFilter joinEventInfo = new JsonLogFilter();
        joinEventInfo.setJsonPath("$.glb_x");
        joinEventInfo.setValueFilter("JOININ");

        newRegisterPvFilter.add(joinEventInfo);
        newRegisterPv.setJsonLogFilters(newRegisterPvFilter);
        reportQuotaFieldConfigs.add(newRegisterPv);

        //注册点击UV 原始版本
        ReportQuotaFieldConfig oldRegisterUv = new ReportQuotaFieldConfig();
        oldRegisterUv.setQuotaFieldName("register_uv");
        oldRegisterUv.setDefaultValue("_skip");
        oldRegisterUv.setExtractValueJsonPath("$.glb_od");
        oldRegisterUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件 page_main_type=f，platform=pc，sub_event_info=CRAAT，
        // 且last_page_url=https://cart.zaful.com/shopping-cart.html统计cookie_id去重后的数量
        List<JsonLogFilter> oldRegisterUvFilter = new ArrayList<>();
        oldRegisterUvFilter.add(pageMainType);
        oldRegisterUvFilter.add(platform);
        oldRegisterUvFilter.add(craatEventInfo);
        oldRegisterUvFilter.add(pageUrl);
        oldRegisterUv.setJsonLogFilters(oldRegisterUvFilter);
        reportQuotaFieldConfigs.add(oldRegisterUv);

        //注册点击UV 新版本
        ReportQuotaFieldConfig newRegisterUv = new ReportQuotaFieldConfig();
        newRegisterUv.setQuotaFieldName("register_uv");
        newRegisterUv.setDefaultValue("_skip");
        newRegisterUv.setExtractValueJsonPath("$.glb_od");
        newRegisterUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件 page_sub_type=d01，platform=pc，sub_event_info=JOININ，统计cookie_id去重后的数量
        List<JsonLogFilter> newRegisterUvFilter = new ArrayList<>();
        newRegisterUvFilter.add(pageSubType);
        newRegisterUvFilter.add(platform);
        newRegisterUvFilter.add(joinEventInfo);

        newRegisterUv.setJsonLogFilters(newRegisterUvFilter);
        reportQuotaFieldConfigs.add(newRegisterUv);

        //游客点击PV
        ReportQuotaFieldConfig touristClickPv = new ReportQuotaFieldConfig();
        touristClickPv.setQuotaFieldName("tourist_click_pv");
        touristClickPv.setDefaultValue(0);
        touristClickPv.setExtractValueJsonPath("$.glb_od");
        touristClickPv.setValueEnum("countOneWithFilter");
        //过滤条件原始版本：page_sub_type=d01，platform=pc，sub_event_info=youke，统计cookie_id数量
        List<JsonLogFilter> touristClickPvFilter = new ArrayList<>();
        touristClickPvFilter.add(pageSubType);
        touristClickPvFilter.add(platform);

        JsonLogFilter youkeEvent = new JsonLogFilter();
        youkeEvent.setJsonPath("$.glb_x");
        youkeEvent.setValueFilter("youke");
        touristClickPvFilter.add(youkeEvent);
        touristClickPv.setJsonLogFilters(touristClickPvFilter);
        reportQuotaFieldConfigs.add(touristClickPv);

        //游客点击UV
        ReportQuotaFieldConfig touristClickUv = new ReportQuotaFieldConfig();
        touristClickUv.setQuotaFieldName("tourist_click_uv");
        touristClickUv.setDefaultValue("_skip");
        touristClickUv.setExtractValueJsonPath("$.glb_od");
        touristClickUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件：page_sub_type=d01，platform=pc，sub_event_info=youke，统计cookie_id去重后数量
        List<JsonLogFilter> touristClickUvFilter = new ArrayList<>();
        touristClickUvFilter.add(pageSubType);
        touristClickUvFilter.add(platform);
        touristClickUvFilter.add(youkeEvent);
        touristClickUv.setJsonLogFilters(touristClickUvFilter);
        reportQuotaFieldConfigs.add(touristClickUv);

        //订单确认页PV 原始版本
        ReportQuotaFieldConfig oldOrderConfirmPv = new ReportQuotaFieldConfig();
        oldOrderConfirmPv.setQuotaFieldName("order_confirm_pv");
        oldOrderConfirmPv.setDefaultValue(0);
        oldOrderConfirmPv.setExtractValueJsonPath("$.glb_od");
        oldOrderConfirmPv.setValueEnum("countOneWithFilter");
        //过滤条件原始版本：page_main_type=d03，platform=pc，统计cookie_id数量
        List<JsonLogFilter> oldOrderConfirmPvFilter = new ArrayList<>();
        JsonLogFilter pageMainType3 = new JsonLogFilter();
        pageMainType3.setJsonPath("$.glb_b");
        pageMainType3.setValueFilter("d03");
        oldOrderConfirmPvFilter.add(pageMainType3);
        oldOrderConfirmPvFilter.add(platform);

        oldOrderConfirmPv.setJsonLogFilters(oldOrderConfirmPvFilter);
        reportQuotaFieldConfigs.add(oldOrderConfirmPv);

        //订单确认页PV 新版本
        ReportQuotaFieldConfig newOrderConfirmPv = new ReportQuotaFieldConfig();
        newOrderConfirmPv.setQuotaFieldName("order_confirm_pv");
        newOrderConfirmPv.setDefaultValue(0);
        newOrderConfirmPv.setExtractValueJsonPath("$.glb_od");
        newOrderConfirmPv.setValueEnum("countOneWithFilter");
        //过滤条件新版本：page_sub_type=d03，platform=pc，，统计cookie_id数量
        List<JsonLogFilter> newOrderConfirmPvFilter = new ArrayList<>();
        JsonLogFilter pageSubType3 = new JsonLogFilter();
        pageSubType3.setJsonPath("$.glb_s");
        pageSubType3.setValueFilter("d03");
        newOrderConfirmPvFilter.add(pageSubType3);
        newOrderConfirmPvFilter.add(platform);

        newOrderConfirmPv.setJsonLogFilters(newOrderConfirmPvFilter);
        reportQuotaFieldConfigs.add(newOrderConfirmPv);


        // 订单确认页UV 原始版本
        ReportQuotaFieldConfig oldOrderConfirmUv = new ReportQuotaFieldConfig();
        oldOrderConfirmUv.setQuotaFieldName("order_confirm_uv");
        oldOrderConfirmUv.setDefaultValue("_skip");
        oldOrderConfirmUv.setExtractValueJsonPath("$.glb_od");
        oldOrderConfirmUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件原始版本：page_main_type=d03，platform=pc，统计cookie_id去重后的数量
        List<JsonLogFilter> oldOrderConfirmUvFilter = new ArrayList<>();
        oldOrderConfirmUvFilter.add(pageMainType3);
        oldOrderConfirmUvFilter.add(platform);
        oldOrderConfirmUv.setJsonLogFilters(oldOrderConfirmUvFilter);
        reportQuotaFieldConfigs.add(oldOrderConfirmUv);

        // 订单确认页UV 新版本
        ReportQuotaFieldConfig newOrderConfirmUv = new ReportQuotaFieldConfig();
        newOrderConfirmUv.setQuotaFieldName("order_confirm_uv");
        newOrderConfirmUv.setDefaultValue("_skip");
        newOrderConfirmUv.setExtractValueJsonPath("$.glb_od");
        newOrderConfirmUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件新版本：page_sub_type=d03，platform=pc，，统计cookie_id去重后的数量
        List<JsonLogFilter> newOrderConfirmUvFilter = new ArrayList<>();
        newOrderConfirmUvFilter.add(pageSubType3);
        newOrderConfirmUvFilter.add(platform);
        newOrderConfirmUv.setJsonLogFilters(newOrderConfirmUvFilter);
        reportQuotaFieldConfigs.add(newOrderConfirmUv);

        //placeorder按钮点击PV 原始版本
        ReportQuotaFieldConfig oldPlaceOrderPv = new ReportQuotaFieldConfig();
        oldPlaceOrderPv.setQuotaFieldName("placeorder_pv");
        oldPlaceOrderPv.setDefaultValue(0);
        oldPlaceOrderPv.setExtractValueJsonPath("$.glb_od");
        oldPlaceOrderPv.setValueEnum("countOneWithFilter");
        //过滤条件原始版本：page_main_type=d03，platform=pc，sub_event_info=placeorder，统计cookie_id数量
        List<JsonLogFilter> oldPlaceOrderPvFilter = new ArrayList<>();
        oldPlaceOrderPvFilter.add(pageMainType3);
        oldPlaceOrderPvFilter.add(platform);

        JsonLogFilter placeorderEvent = new JsonLogFilter();
        placeorderEvent.setJsonPath("$.glb_x");
        placeorderEvent.setValueFilter("placeorder");

        oldPlaceOrderPvFilter.add(placeorderEvent);
        oldPlaceOrderPv.setJsonLogFilters(oldPlaceOrderPvFilter);
        reportQuotaFieldConfigs.add(oldPlaceOrderPv);

        //placeorder按钮点击PV 新版本
        ReportQuotaFieldConfig newPlaceOrderPv = new ReportQuotaFieldConfig();
        newPlaceOrderPv.setQuotaFieldName("placeorder_pv");
        newPlaceOrderPv.setDefaultValue(0);
        newPlaceOrderPv.setExtractValueJsonPath("$.glb_od");
        newPlaceOrderPv.setValueEnum("countOneWithFilter");
        //过滤条件新版本：page_sub_type=d03，platform=pc，sub_event_info=placeorder，统计cookie_id数量
        List<JsonLogFilter> newPlaceOrderPvFilter = new ArrayList<>();
        newPlaceOrderPvFilter.add(pageSubType3);
        newPlaceOrderPvFilter.add(platform);
        newPlaceOrderPvFilter.add(placeorderEvent);

        newPlaceOrderPv.setJsonLogFilters(newPlaceOrderPvFilter);
        reportQuotaFieldConfigs.add(newPlaceOrderPv);

        //placeorder按钮点击UV 原始版本
        ReportQuotaFieldConfig oldPlaceOrderUv = new ReportQuotaFieldConfig();
        oldPlaceOrderUv.setQuotaFieldName("placeorder_uv");
        oldPlaceOrderUv.setDefaultValue("_skip");
        oldPlaceOrderUv.setExtractValueJsonPath("$.glb_od");
        oldPlaceOrderUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件 原始版本：page_main_type=d03，platform=pc，sub_event_info=placeorder，统计cookie_id去重后的数量
        List<JsonLogFilter> oldPlaceOrderUvFilter = new ArrayList<>();
        oldPlaceOrderUvFilter.add(pageMainType3);
        oldPlaceOrderUvFilter.add(platform);
        oldPlaceOrderUvFilter.add(placeorderEvent);

        oldPlaceOrderUv.setJsonLogFilters(oldPlaceOrderUvFilter);
        reportQuotaFieldConfigs.add(oldPlaceOrderUv);

        //placeorder按钮点击UV 新版本
        ReportQuotaFieldConfig newPlaceOrderUv = new ReportQuotaFieldConfig();
        newPlaceOrderUv.setQuotaFieldName("placeorder_uv");
        newPlaceOrderUv.setDefaultValue("_skip");
        newPlaceOrderUv.setExtractValueJsonPath("$.glb_od");
        newPlaceOrderUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件 新版本：page_sub_type=d03，platform=pc，sub_event_info=placeorder，统计cookie_id去重后的数量
        List<JsonLogFilter> newPlaceOrderUvFilter = new ArrayList<>();
        newPlaceOrderUvFilter.add(pageSubType3);
        newPlaceOrderUvFilter.add(platform);
        newPlaceOrderUvFilter.add(placeorderEvent);

        newPlaceOrderUv.setJsonLogFilters(newPlaceOrderUvFilter);
        reportQuotaFieldConfigs.add(newPlaceOrderUv);

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
