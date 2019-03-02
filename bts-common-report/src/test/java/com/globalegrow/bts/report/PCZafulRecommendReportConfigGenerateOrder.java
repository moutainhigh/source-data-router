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

public class PCZafulRecommendReportConfigGenerateOrder {

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
        siteFilter.setValueFilter("10013");
        globaleFilters.add(siteFilter);


        List<JsonLogFilter> cartFilter = new ArrayList<>();
        JsonLogFilter ic = new JsonLogFilter();
        ic.setJsonPath("$.glb_t");
        ic.setValueFilter("ic");
        cartFilter.add(ic);

        JsonLogFilter cartJsonFilter = new JsonLogFilter();
        cartJsonFilter.setJsonPath("$.glb_x");
        cartJsonFilter.setValueFilter("ADT");
        cartFilter.add(cartJsonFilter);

        JsonLogFilter fmdFilter = new JsonLogFilter();
        fmdFilter.setJsonPath("$.glb_ubcta.fmd");
        fmdFilter.setValueFilter("mr_T_8");
        cartFilter.add(fmdFilter);

        JsonLogFilter reportFilter = new JsonLogFilter();
        reportFilter.setJsonPath("$.db_order_info.report_name");
        reportFilter.setValueFilter("BTS_ZAFUL_ORDER_PC_RECOMMEND_REC");

        globaleFilters.add(reportFilter);

        globaleFilters.addAll(cartFilter);

        rule.setGlobaleJsonFilters(globaleFilters);

        rule.setReportName("BTS_ZAFUL_PC_RECOMMEND_REC_ORDER");
        rule.setDescription("zaful pc 端购物车推荐报表取数指标配置,订单指标");
        ZafulPcRecommendReportModel searchRecommendReportQuotaModel = new ZafulPcRecommendReportModel();

        rule.setReportDefaultValues(DyBeanUtils.objToMap(searchRecommendReportQuotaModel));


        ReportKafkaConfig reportKafkaConfig = new ReportKafkaConfig();
        reportKafkaConfig.setBootstrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setDataSourceTopic("dy_log_cart_order_info");
        reportKafkaConfig.setBootstrapGroupId("dy_bts_zaful_pc_recommend_rec_order");
        reportKafkaConfig.setReportStrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setReportDataTopic("dy_bts_zaful_pc_recommend_rec_report");
        reportKafkaConfig.setFromStartOffset(true);

        rule.setReportFromKafka(reportKafkaConfig);


        List<ReportQuotaFieldConfig> reportQuotaFieldConfigs = new ArrayList<>();

        quota(reportQuotaFieldConfigs);

        rule.setReportQuotaFieldConfigs(reportQuotaFieldConfigs);

        System.out.println(JacksonUtil.toJSon(rule));
    }

    private void quota(List<ReportQuotaFieldConfig> reportQuotaFieldConfigs) {

        // 下单商品数
        ReportQuotaFieldConfig orderGoodsNum = new ReportQuotaFieldConfig();
        orderGoodsNum.setQuotaFieldName("good_order_num");
        orderGoodsNum.setDefaultValue(0);
        orderGoodsNum.setExtractValueJsonPath("$.db_order_info.goods_num");
        orderGoodsNum.setValueEnum("quotaIntValueExtractFromLog");
        //下单客户数
        ReportQuotaFieldConfig orderUv = new ReportQuotaFieldConfig();
        orderUv.setQuotaFieldName("good_order_uv");
        orderUv.setDefaultValue("_skip");
        orderUv.setExtractValueJsonPath("$.glb_od");
        orderUv.setValueEnum("quotaStringValueExtractFromLog");

        List<JsonLogFilter> orderGoodsNumJsonFilters = new ArrayList<>();
        JsonLogFilter orderStatus0 = new JsonLogFilter();
        // 订单状态为 0 算下单
        orderStatus0.setJsonPath("$.db_order_info.order_status");
        orderStatus0.setValueFilter("0");
        orderGoodsNumJsonFilters.add(orderStatus0);
        // 只取订单商品数据
        JsonLogFilter orderFalse = new JsonLogFilter();
        orderFalse.setJsonPath("$.db_order_info.order_data");
        orderFalse.setFilterRule("false");
        orderGoodsNumJsonFilters.add(orderFalse);

        orderGoodsNum.setJsonLogFilters(orderGoodsNumJsonFilters);

        reportQuotaFieldConfigs.add(orderGoodsNum);
        orderUv.setJsonLogFilters(orderGoodsNumJsonFilters);

        reportQuotaFieldConfigs.add(orderUv);
        // 下单金额
        ReportQuotaFieldConfig orderAmount = new ReportQuotaFieldConfig();
        orderAmount.setQuotaFieldName("order_amount");
        orderAmount.setDefaultValue(0);
        orderAmount.setExtractValueJsonPath("$.db_order_info.gmv");
        orderAmount.setValueEnum("quotaIntValueExtractFromLog");
        orderAmount.setJsonLogFilters(orderGoodsNumJsonFilters);
        reportQuotaFieldConfigs.add(orderAmount);

        List<JsonLogFilter> payGoodsFilter = new ArrayList<>();
        payGoodsFilter.add(orderFalse);

        JsonLogFilter status18 = new JsonLogFilter();
        status18.setJsonPath("$.db_order_info.order_status");
        status18.setValueFilter("1,8");
        status18.setFilterRule("or");

        payGoodsFilter.add(status18);

        // 购买金额
        ReportQuotaFieldConfig amount = new ReportQuotaFieldConfig();
        amount.setQuotaFieldName("gmv");
        amount.setDefaultValue(0);
        amount.setExtractValueJsonPath("$.db_order_info.gmv");
        amount.setValueEnum("quotaIntValueExtractFromLog");

        amount.setJsonLogFilters(payGoodsFilter);

        reportQuotaFieldConfigs.add(amount);

        // 支付订单数
//        ReportQuotaFieldConfig createOrder = new ReportQuotaFieldConfig();
//        createOrder.setQuotaFieldName("pay_order_num");
//        createOrder.setDefaultValue(0);
//        createOrder.setExtractValueJsonPath("$.db_order_info.order_id");
//        createOrder.setValueEnum("countOneWithFilter");
//        createOrder.setJsonLogFilters(payGoodsFilter);
//        reportQuotaFieldConfigs.add(createOrder);
        // 付款商品数
        ReportQuotaFieldConfig sales_amount = new ReportQuotaFieldConfig();
        sales_amount.setQuotaFieldName("sales_amount");
        sales_amount.setDefaultValue(0);
        sales_amount.setExtractValueJsonPath("$.db_order_info.goods_num");
        sales_amount.setValueEnum("quotaIntValueExtractFromLog");
        sales_amount.setJsonLogFilters(payGoodsFilter);
        reportQuotaFieldConfigs.add(sales_amount);
        //付款客户数
        ReportQuotaFieldConfig payUv = new ReportQuotaFieldConfig();
        payUv.setQuotaFieldName("sales_uv");
        payUv.setDefaultValue("_skip");
        payUv.setExtractValueJsonPath("$.glb_od");
        payUv.setValueEnum("quotaStringValueExtractFromLog");

        payUv.setJsonLogFilters(payGoodsFilter);

        reportQuotaFieldConfigs.add(payUv);

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
