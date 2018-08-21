package com.globalegrow;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class MaxKeyTest {


    @Test
    public void testString() {
        System.out.println("dy_zaful_bts_recommend_order_info".indexOf("bts"));
    }


    @Test
    public void floatTest() {
        Float f = 19.23F;
        Float f1 = f * 100;
        System.out.println(f1.intValue());
    }

    @Test
    public void generateTimestamp() {
        String prefix = "dy_zaful_sku_userid_";
        for (int i = 0; i < 10; i++) {
            System.out.println(prefix + System.currentTimeMillis());

        }
    }

    @Test
    public void testMaxKeyByTimestamp() throws InterruptedException {
        Set<String> strings = new HashSet<>();
        String prefix = "dy_zaful_sku_userid_";
        for (int i = 0; i < 10; i++) {
            Long t = System.currentTimeMillis();
            String s = prefix + System.currentTimeMillis();
            System.out.println(s + "-------" + DateFormatUtils.format(t, "hh:mm:ss:SSS"));
            Thread.sleep(100);
            strings.add(s);
        }
        String max = strings.stream().max((o1, o2) -> {
            String timestamp1 = o1.substring(o1.lastIndexOf("_") + 1);
            String timestamp2 = o2.substring(o2.lastIndexOf("_") + 1);
            System.out.println(timestamp1 + "-" + timestamp2);
            if (NumberUtils.isDigits(timestamp1) && NumberUtils.isDigits(timestamp2)) {
                Long t1 = Long.valueOf(timestamp1);
                Long t2 = Long.valueOf(timestamp2);

                if (t1 > t2) {
                    return 1;
                }
                if (t1 < t2) {
                    return -1;
                }
            }
            return -1;
        }).orElse(null);
        System.out.println(max);
    }

}
