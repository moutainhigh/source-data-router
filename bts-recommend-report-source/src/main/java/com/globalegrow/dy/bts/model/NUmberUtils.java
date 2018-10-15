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
        return Long.valueOf(String.valueOf(jsonValue));
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
        return Integer.valueOf(String.valueOf(jsonValue));
    }

}
