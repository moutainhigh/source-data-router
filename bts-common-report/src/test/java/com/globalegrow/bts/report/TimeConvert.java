package com.globalegrow.bts.report;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class TimeConvert {

    @Test
    public void test() {
        Date date = new Date(1545497556000L);
        //TimeZone timeZone = new SimpleTimeZone();


        SimpleDateFormat bjSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     // 北京
        bjSdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        System.out.println(bjSdf.format(date));

//        System.out.println(TimeZone.getTimeZone("Asia/Beijing"));
//        System.out.println(TimeZone.getTimeZone("Asia/Beijing").getID());
//        System.out.println(TimeZone.getTimeZone("Asia/Beijing").getRawOffset());

        System.out.println(TimeZone.getDefault().getID());

      /*
        String[] ids = TimeZone.getAvailableIDs();
        for (String id:ids)
            System.out.println(id+", ");*/
    }

}
