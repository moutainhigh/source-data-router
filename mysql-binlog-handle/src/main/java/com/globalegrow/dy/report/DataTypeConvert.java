package com.globalegrow.dy.report;

import java.util.Map;

public class DataTypeConvert {

    public static int calculatorType(Map<String, Object> dataMap, String quota){
        Object o = dataMap.get(quota);
        if (o instanceof Double) {
            return ((Double) o).intValue();
        }
        if (o instanceof Float) {
            return ((Float) o).intValue();
        }
        if (o instanceof Integer) {
            return (Integer) o;
        }
        if (o instanceof String) {
            String a = String.valueOf(o);
            if (a.indexOf(".") > 0) {
                return Integer.valueOf(a.substring(0, a.indexOf(".")));
            }
        }
        return 0;
    }

    public static int numToInt(Object o) {
        if (o instanceof Double) {
            return ((Double) o).intValue();
        }
        if (o instanceof Float) {
            return ((Float) o).intValue();
        }
        if (o instanceof Integer) {
            return (Integer) o;
        }
        if (o instanceof String) {
            String a = String.valueOf(o);
            if (a.indexOf(".") > 0) {
                return Integer.valueOf(a.substring(0, a.indexOf(".")));
            }
        }
        return 0;
    }


}
