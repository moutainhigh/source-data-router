package com.globalegrow.test;

import com.globalegrow.util.GsonUtil;
import org.elasticsearch.common.MacAddressProvider;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DBOrderTest {

    String order = "{id=261955.0, order_sn=18100700981420582609, parent_order_sn=18100700981420582609, site_code=GB, pipeline_code=GB, user_id=1.6216559E7, logistics_code=NLPOST1, logistics_group_id=132.0, logistics_level=1.0, coupon_deduct_amount=0.0, logistics_coupon_deduct_amount=0.0, integral_deduct_amount=0.0, pay_deduct_amount=0.0, goods_amount=13.64, order_amount=15.76, paid_amount=0.0, shipping_amount=2.12, insurance_amount=0.0, taxes_amount=0.0, discount_amount=0.0, order_currency=BRL, order_rate=4.23, exponent=2.0, order_currency_amount=66.68, order_language=po, order_type=0.0, order_status=0.0, stock_status=0.0, pay_status=0.0, pay_type=0.0, order_pay_sn=, oms_pay_status=0.0, order_logistics_status=0.0, distribute_status=0.0, deleted_status=0.0, case_status=0.0, rma_status=0.0, pay_channel=null, created_time=1.538963939E9, completed_time=0.0, logistics_confirm_time=0.0, whole_delivery_time=0.0, canceling_time=0.0, canceled_time=0.0, expire_time=1.540263539E9, process_version=0.0, update_time=1.538963939E9, sys_update_time=2018-10-08 01:58:59}";

    @Test
    public void test() {
        //Map<String, Object> m
    }

}
