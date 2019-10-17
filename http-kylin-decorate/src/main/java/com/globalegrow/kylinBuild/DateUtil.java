package com.globalegrow.kylinBuild;

import java.util.Calendar;
import java.util.Date;

/**
 * @author zhougenggeng createTime  2019/10/16
 */
public class DateUtil {
    /**
     * 返回对原始时间增加field段addnum值的时间
     *
     * @param date   时间
     * @param field  字段
     * @param addnum 值
     * @return 新的日期
     */
    public static Date addDateTime(Date date, int field, int addnum) {
        if (date == null) {
            return null;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(field, c.get(field) + addnum);
            return c.getTime();
        }
    }
}
