package com.globalegrow.dy.report.enums;

public enum RecommendQuotaFields {
    exposure("is_exposure", RecommendReportFields.exp_num, ValueType.num),
    skuClick("is_click", RecommendReportFields.click_num, ValueType.num) ,
    skuAddCart("is_cart", RecommendReportFields.add_cart_num, ValueType.num) ,
    userOrder("is_order", RecommendReportFields.sku_order_num, ValueType.num) , paidOrder("isPaidOrder", RecommendReportFields.paid_order_num, ValueType.num),
    payAmount("is_pay_amount", RecommendReportFields.paid_amount, ValueType.num),specimen("glb_od", RecommendReportFields.specimen, ValueType.value);

    RecommendQuotaFields(String quota, RecommendReportFields recommendReportFields, ValueType valueType) {
        this.quota = quota;
        this.recommendReportFields = recommendReportFields;
        this.valueType = valueType;
    }

    public String quota;
    public ValueType valueType;
    public RecommendReportFields recommendReportFields;


}
