package com.globalegrow;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DyBeanUtils {

    private static final Logger logger = LoggerFactory.getLogger(DyBeanUtils.class);

    public static Map<String, Object> objToMap(Object o) {
        Map beanMap = new BeanMap(o);
        Map<String, Object> map = new HashMap<>();
        if (MapUtils.isNotEmpty(beanMap)) {
            // 去除class属性
            for (Object key : beanMap.keySet()) {
                if (!"class".equals(key) && !"skip".equals(key)) {
                    // System.out.println(key);
                    map.put(String.valueOf(key), beanMap.get(key));
                }
            }
            return map;
        }
        return null;
    }

}
