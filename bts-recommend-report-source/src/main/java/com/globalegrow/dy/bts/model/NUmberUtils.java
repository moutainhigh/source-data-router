package com.globalegrow.dy.bts.model;

public class NUmberUtils {

    public static Long jsonValueToLong(Object jsonValue) {
        if (jsonValue instanceof Double) {
            return ((Double) jsonValue).longValue();
        }
        if (jsonValue instanceof Long) {
            return (Long)jsonValue;
        }
        if (jsonValue instanceof Float) {
            return ((Float) jsonValue).longValue();
        }
        String orderId = String.valueOf(jsonValue);
        if (orderId.contains(".")) {
            orderId = orderId.substring(0, orderId.lastIndexOf("."));
        }
        return Long.valueOf(orderId);
    }


    public static Integer jsonValueToInt(Object jsonValue) {
        if (jsonValue instanceof Double) {
            return ((Double) jsonValue).intValue();
        }
        if (jsonValue instanceof Long) {
            return (Integer) jsonValue;
        }
        if (jsonValue instanceof Float) {
            return ((Float) jsonValue).intValue();
        }
        String orderId = String.valueOf(jsonValue);
        if (orderId.contains(".")) {
            orderId = orderId.substring(0, orderId.lastIndexOf("."));
        }
        return Integer.valueOf(orderId);
    }

}
