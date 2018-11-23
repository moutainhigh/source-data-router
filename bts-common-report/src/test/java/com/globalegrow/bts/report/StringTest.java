package com.globalegrow.bts.report;

import org.junit.Test;

public class StringTest {

    @Test
    public void test() {
        String s = "%22sckw%22:%22%22}&glb_filter={%22";
        System.out.println(s.contains("="));
    }

}
