package com.globalegrow.dy.bts.kafka;

import java.util.Map;

public class CalculatorTypeUtil {

    public static String getCalculatorType(Map<String, Object> log) {
        try {
            if ((Integer)log.get("is_pv") == 1) {
                return "is_pv";
            }
            if ((Integer)log.get("is_click") == 1) {
                return "is_click";
            }
            if ((Integer)log.get("is_uv") == 1) {
                return "is_uv";
            }
            if ((Integer)log.get("is_exposure") == 1) {
                return "is_exposure";
            }
            if ((Integer)log.get("is_cart") == 1) {
                return "is_cart";
            }
            if ((Integer)log.get("is_order") == 1) {
                return "is_order";
            }
            if ((Integer)log.get("is_purchase") == 1) {
                return "is_purchase";
            }
            if ((Integer)log.get("is_pay_amount") == 1) {
                return "is_pay_amount";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "other";
    }

}
