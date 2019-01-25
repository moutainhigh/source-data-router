package com.globalegrow;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateParser;
import org.apache.commons.lang3.time.FastDateParser;
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


        String date1231 = "2019-01-24 07:00:00";// 22 上午8点
        String date1232= "2019-01-24 07:59:59";// 23 号上午8点
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //DateParser dateParser = new FastDateParser();
        //1546617452749-9082441235731332735
        System.out.println(simpleDateFormat.parse(date1231).getTime());
        System.out.println(simpleDateFormat.parse(date1232).getTime());
    }

    @Test
    public void testString() {
        System.out.println("1543755070000\\u0001ES".replaceAll("\\\\u0001ES",""));
    }

}
