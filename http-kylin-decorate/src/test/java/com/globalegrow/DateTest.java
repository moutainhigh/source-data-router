package com.globalegrow;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Test;

import java.text.ParseException;

public class DateTest {


    @Test
    public void test() throws ParseException {
        System.out.println(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.parse("2018-10-22").getTime());
        System.out.println(DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.parse("2018-10-29" + "T23:59:59").getTime());
    }

}
