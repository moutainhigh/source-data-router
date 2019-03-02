package com.globalegrow.util;

import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.Map;

public class CommonTextUtils {

    public static String replaceOneParameter(String source, String key, String value) {
        Map valuesMap = new HashMap();
        valuesMap.put(key, value);
        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        return sub.replace(source);
    }

}
