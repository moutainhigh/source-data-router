package com.globalegrow;

import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;

import java.util.Date;

public class DateTest {

    @Test
    public void test() {
        System.out.println(DateUtil.thisYear() + "" + DateUtil.thisMonth() + "" + DateUtil.thisDayOfMonth()+ "" + DateUtil.thisHour(true) + "");
        System.out.println(DateUtil.thisYear() + DateUtil.thisMonth() + DateUtil.thisDayOfMonth() + DateUtil.thisHour(true));
        System.out.println(DateUtil.month(new Date()));
        System.out.println(DateFormatUtils.format(new Date(), "yyyyMMddHH"));
        System.out.println(DateUtil.thisHour(true));
    }

}
