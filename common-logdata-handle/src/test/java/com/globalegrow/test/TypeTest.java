package com.globalegrow.test;

import com.globalegrow.dy.report.DataTypeConvert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TypeTest {

    @Test
    public void test() {
        Map<String, Object> map = new HashMap<>();
        map.put("is_click", 1);
        map.put("is_exposure", 1);
        map.put("exposure_count", 1);
        System.out.println(DataTypeConvert.calculatorType(map, "is_exposure"));
        System.out.println(map.getOrDefault("exposure_count", 0));
    }

    @Test
    public void testNum() {
        Double d = 1.0;
        System.out.println(d.intValue());
    }

}
