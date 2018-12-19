package com.globalegrow.bts.report;

import com.globalegrow.report.JsonLogFilter;
import com.globalegrow.report.ReportBuildRule;
import com.globalegrow.report.ReportKafkaConfig;
import com.globalegrow.report.ReportQuotaFieldConfig;
import com.globalegrow.util.DyBeanUtils;
import com.globalegrow.util.JacksonUtil;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportDresslilyConfigGerenate {

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
        DresslilyReportQuotaModel dresslilyReportQuotaModel = new DresslilyReportQuotaModel();
        dresslilyReportQuotaModel.setTimestamp(System.currentTimeMillis());
        System.out.println(JacksonUtil.toJSon(dresslilyReportQuotaModel));
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

        JsonLogFilter siteFilter = new JsonLogFilter();
        siteFilter.setJsonPath("$.glb_d");
        siteFilter.setValueFilter("10009");
        globaleFilters.add(siteFilter);

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

        rule.setGlobaleJsonFilters(globaleFilters);

        rule.setReportName("BTS_DL_QUICK_SHOP");
        rule.setDescription("D网商详页快速购买报表");
        DresslilyReportQuotaModel dresslilyReportQuotaModel = new DresslilyReportQuotaModel();

        rule.setReportDefaultValues(DyBeanUtils.objToMap(dresslilyReportQuotaModel));


        ReportKafkaConfig reportKafkaConfig = new ReportKafkaConfig();
        reportKafkaConfig.setBootstrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setDataSourceTopic("glbg-analitic");
        reportKafkaConfig.setBootstrapGroupId("dy_bts_dl_quick_shop_report");
        reportKafkaConfig.setReportStrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setReportDataTopic("dy_bts_dl_quick_shop_report");

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
        specimen.setJsonLogFilters(specimenFilter);
        reportQuotaFieldConfigs.add(specimen);

        // 商祥页PV
        ReportQuotaFieldConfig searchPagePv = new ReportQuotaFieldConfig();
        searchPagePv.setQuotaFieldName("search_page_pv");
        searchPagePv.setDefaultValue(0);
        searchPagePv.setExtractValueJsonPath("$.glb_od");
        searchPagePv.setValueEnum("countOneWithFilter");
        //过滤条件
        List<JsonLogFilter> searchPageFilter=new ArrayList<>();
        searchPageFilter.add(plf);
        JsonLogFilter b=new JsonLogFilter();
        b.setJsonPath("$.glb_b");
        b.setValueFilter("c");
        searchPageFilter.add(b);
        JsonLogFilter iet=new JsonLogFilter();
        iet.setJsonPath("$.glb_t");
        iet.setValueFilter("ie");
        searchPageFilter.add(iet);
        searchPagePv.setJsonLogFilters(searchPageFilter);
        reportQuotaFieldConfigs.add(searchPagePv);

        // 商祥页UV
        ReportQuotaFieldConfig searchPageUv = new ReportQuotaFieldConfig();
        searchPageUv.setQuotaFieldName("search_page_uv");
        searchPageUv.setDefaultValue("_skip");
        searchPageUv.setExtractValueJsonPath("$.glb_od");
        searchPageUv.setValueEnum("quotaStringValueExtractFromLog");
        searchPageUv.setJsonLogFilters(searchPageFilter);
        reportQuotaFieldConfigs.add(searchPageUv);

        // checkout按钮PV UV
        ReportQuotaFieldConfig cartPageUv = new ReportQuotaFieldConfig();
        cartPageUv.setQuotaFieldName("cart_page_uv");
        cartPageUv.setDefaultValue("_skip");
        cartPageUv.setExtractValueJsonPath("$.glb_od");
        cartPageUv.setValueEnum("quotaStringValueExtractFromLog");

        ReportQuotaFieldConfig cartPagePv = new ReportQuotaFieldConfig();
        cartPagePv.setQuotaFieldName("cart_page_pv");
        cartPagePv.setDefaultValue(0);
        cartPagePv.setExtractValueJsonPath("$.glb_od");
        cartPagePv.setValueEnum("countOneWithFilter");
        //过滤条件
        List<JsonLogFilter> cartFilter = new ArrayList<>();
        cartFilter.add(plf);
        JsonLogFilter d01Filter = new JsonLogFilter();
        d01Filter.setJsonPath("$.glb_s");
        d01Filter.setValueFilter("d01");
        cartFilter.add(d01Filter);
        JsonLogFilter ict=new JsonLogFilter();
        ict.setJsonPath("$.glb_t");
        ict.setValueFilter("ic");
        cartFilter.add(ict);
        JsonLogFilter checkoutx = new JsonLogFilter();
        checkoutx.setJsonPath("$.glb_x");
        checkoutx.setValueFilter("checkout");
        cartFilter.add(checkoutx);
        cartPageUv.setJsonLogFilters(cartFilter);
        reportQuotaFieldConfigs.add(cartPageUv);
        cartPagePv.setJsonLogFilters(cartFilter);
        reportQuotaFieldConfigs.add(cartPagePv);

        // PP快速付款 pv uv
        ReportQuotaFieldConfig quickPayPv = new ReportQuotaFieldConfig();
        quickPayPv.setQuotaFieldName("quick_pay_pv");
        quickPayPv.setDefaultValue(0);
        quickPayPv.setExtractValueJsonPath("$.glb_od");
        quickPayPv.setValueEnum("countOneWithFilter");

        ReportQuotaFieldConfig quickPayUv = new ReportQuotaFieldConfig();
        quickPayUv.setQuotaFieldName("quick_pay_uv");
        quickPayUv.setDefaultValue("_skip");
        quickPayUv.setExtractValueJsonPath("$.glb_od");
        quickPayUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件
        List<JsonLogFilter> quickPayUilter = new ArrayList<>();
        quickPayUilter.add(d01Filter);
        quickPayUilter.add(plf);
        JsonLogFilter CWPx = new JsonLogFilter();
        CWPx.setJsonPath("$.glb_x");
        CWPx.setValueFilter("CWP");
        quickPayUilter.add(CWPx);
        quickPayUilter.add(ict);
        quickPayPv.setJsonLogFilters(quickPayUilter);
        quickPayUv.setJsonLogFilters(quickPayUilter);
        reportQuotaFieldConfigs.add(quickPayPv);
        reportQuotaFieldConfigs.add(quickPayUv);

        // BUY NOW按钮点击 pv uv
        ReportQuotaFieldConfig buyNowBtnClickPv = new ReportQuotaFieldConfig();
        buyNowBtnClickPv.setQuotaFieldName("buy_now_btn_click_pv");
        buyNowBtnClickPv.setDefaultValue(0);
        buyNowBtnClickPv.setExtractValueJsonPath("$.glb_od");
        buyNowBtnClickPv.setValueEnum("countOneWithFilter");

        ReportQuotaFieldConfig buyNowBtnClickUv = new ReportQuotaFieldConfig();
        buyNowBtnClickUv.setQuotaFieldName("buy_now_btn_click_uv");
        buyNowBtnClickUv.setDefaultValue("_skip");
        buyNowBtnClickUv.setExtractValueJsonPath("$.glb_od");
        buyNowBtnClickUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件
        List<JsonLogFilter> buyNowBtnClickUilter = new ArrayList<>();
        buyNowBtnClickUilter.add(b);
        buyNowBtnClickUilter.add(plf);
        JsonLogFilter BDRx = new JsonLogFilter();
        BDRx.setJsonPath("$.glb_x");
        BDRx.setValueFilter("BDR");
        buyNowBtnClickUilter.add(BDRx);
        buyNowBtnClickUilter.add(ict);
        buyNowBtnClickPv.setJsonLogFilters(buyNowBtnClickUilter);
        buyNowBtnClickUv.setJsonLogFilters(buyNowBtnClickUilter);
        reportQuotaFieldConfigs.add(buyNowBtnClickPv);
        reportQuotaFieldConfigs.add(buyNowBtnClickUv);

        // add to cart按钮点击 pv uv
        ReportQuotaFieldConfig addCartBtnClickPv = new ReportQuotaFieldConfig();
        addCartBtnClickPv.setQuotaFieldName("add_cart_btn_click_pv");
        addCartBtnClickPv.setDefaultValue(0);
        addCartBtnClickPv.setExtractValueJsonPath("$.glb_od");
        addCartBtnClickPv.setValueEnum("countOneWithFilter");

        ReportQuotaFieldConfig addCartBtnClickUv = new ReportQuotaFieldConfig();
        addCartBtnClickUv.setQuotaFieldName("add_cart_btn_click_uv");
        addCartBtnClickUv.setDefaultValue("_skip");
        addCartBtnClickUv.setExtractValueJsonPath("$.glb_od");
        addCartBtnClickUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件
        List<JsonLogFilter> addCartBtnClickUilter = new ArrayList<>();
        JsonLogFilter ADTx = new JsonLogFilter();
        ADTx.setJsonPath("$.glb_x");
        ADTx.setValueFilter("ADT");
        addCartBtnClickUilter.add(ADTx);
        addCartBtnClickUilter.add(ict);
        addCartBtnClickUilter.add(b);
        addCartBtnClickUilter.add(plf);
        addCartBtnClickPv.setJsonLogFilters(addCartBtnClickUilter);
        addCartBtnClickUv.setJsonLogFilters(addCartBtnClickUilter);
        reportQuotaFieldConfigs.add(addCartBtnClickPv);
        reportQuotaFieldConfigs.add(addCartBtnClickUv);

        // 订单确认页 pv uv
        ReportQuotaFieldConfig orderPagePv = new ReportQuotaFieldConfig();
        orderPagePv.setQuotaFieldName("order_page_click_pv");
        orderPagePv.setDefaultValue(0);
        orderPagePv.setExtractValueJsonPath("$.glb_od");
        orderPagePv.setValueEnum("countOneWithFilter");

        ReportQuotaFieldConfig orderPageUv = new ReportQuotaFieldConfig();
        orderPageUv.setQuotaFieldName("order_page_click_uv");
        orderPageUv.setDefaultValue("_skip");
        orderPageUv.setExtractValueJsonPath("$.glb_od");
        orderPageUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件
        List<JsonLogFilter> orderPageUilter = new ArrayList<>();
        JsonLogFilter d03s = new JsonLogFilter();
        d03s.setJsonPath("$.glb_s");
        d03s.setValueFilter("d03");
        orderPageUilter.add(d03s);
        orderPageUilter.add(iet);
        orderPageUilter.add(plf);
        orderPagePv.setJsonLogFilters(orderPageUilter);
        orderPageUv.setJsonLogFilters(orderPageUilter);
        reportQuotaFieldConfigs.add(orderPagePv);
        reportQuotaFieldConfigs.add(orderPageUv);

        // place order按钮点击 pv uv
        ReportQuotaFieldConfig placeOrderBtnClickPv = new ReportQuotaFieldConfig();
        placeOrderBtnClickPv.setQuotaFieldName("place_order_btn_click_pv");
        placeOrderBtnClickPv.setDefaultValue(0);
        placeOrderBtnClickPv.setExtractValueJsonPath("$.glb_od");
        placeOrderBtnClickPv.setValueEnum("countOneWithFilter");

        ReportQuotaFieldConfig placeOrderBtnClickUv = new ReportQuotaFieldConfig();
        placeOrderBtnClickUv.setQuotaFieldName("place_order_btn_click_uv");
        placeOrderBtnClickUv.setDefaultValue("_skip");
        placeOrderBtnClickUv.setExtractValueJsonPath("$.glb_od");
        placeOrderBtnClickUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件
        List<JsonLogFilter> placeOrderBtnClickUilter = new ArrayList<>();
        JsonLogFilter placeorderx = new JsonLogFilter();
        placeorderx.setJsonPath("$.glb_x");
        placeorderx.setValueFilter("placeorder");
        placeOrderBtnClickUilter.add(placeorderx);
        placeOrderBtnClickUilter.add(ict);
        placeOrderBtnClickUilter.add(plf);
        placeOrderBtnClickUilter.add(d03s);
        placeOrderBtnClickPv.setJsonLogFilters(placeOrderBtnClickUilter);
        placeOrderBtnClickUv.setJsonLogFilters(placeOrderBtnClickUilter);
        reportQuotaFieldConfigs.add(placeOrderBtnClickPv);
        reportQuotaFieldConfigs.add(placeOrderBtnClickUv);
        // bts 3 个字段
        ReportQuotaFieldConfig btsQuota = new ReportQuotaFieldConfig();
        btsQuota.setQuotaFieldName("bts");
        btsQuota.setDefaultValue(bts);
        btsQuota.setExtractValueJsonPath("$.glb_bts");
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
