package com.globalegrow;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TimeTest {

    @Test
    public void test() {
        System.out.println(System.currentTimeMillis() - 86400000L*30);
    }

    @Test
    public void testTimestamp14() throws ParseException {
        String date1231 = "2018-12-31 23:59:59";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(simpleDateFormat.parse(date1231).getTime());
    }

}
