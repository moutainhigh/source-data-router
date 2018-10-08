package com.globalegrow.test;

import org.junit.Test;

public class NumberTest {

    @Test
    public void test() {
        String a = "3.0";
        System.out.println(a.substring(0, a.indexOf(".")));
    }

}
