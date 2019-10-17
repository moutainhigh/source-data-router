package com.globalegrow.kylinBuild;

import cn.hutool.http.HttpUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zhougenggeng createTime  2019/10/16
 */
public class KylinTest {
    public static void main(String[] args) {

        Calendar calendarA = Calendar.getInstance();
        calendarA.setTime(new Date());
        calendarA.add(Calendar.DATE, -33);
        Date day33 = calendarA.getTime();
        Calendar  calendarB = Calendar.getInstance();
        calendarB.setTime(new Date());
        calendarB.add(Calendar.DATE, -1);
        Date  yesterday = calendarB.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        for (Date temp = day33; !yesterday.before(temp); temp = DateUtil.addDateTime(temp, Calendar.DATE, 1)) {

            try {
                System.out.println(df.format(temp));
                Calendar  calendarC = Calendar.getInstance();
                calendarC.setTime(temp);
                calendarC.add(Calendar.DATE, 1);
                Date tomorrow  = calendarC.getTime();
                System.out.println(df.format(tomorrow)+"-----------");
                String kylinUrl = "http://35.153.241.61:8095/cube-build/manual";
                String kylinTaskName = "dy_gb_goods_modify_cube";
                String kylinType = "2";
                String kylinStartTime = df.format(temp) + " 00:00:00";
                String kylinEndTime = df.format(tomorrow) + " 00:00:00";
                Map<String, Object> paramMap = new LinkedHashMap<>();
                paramMap.put("taskName",kylinTaskName);
                paramMap.put("type",kylinType);
                paramMap.put("startTime",kylinStartTime);
                paramMap.put("endTime",kylinEndTime);
                HttpUtil.post(kylinUrl,paramMap);
                //Thread.sleep(5000);
                Thread.sleep(900000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



    }
}
