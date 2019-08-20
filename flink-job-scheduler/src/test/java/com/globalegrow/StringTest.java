package com.globalegrow;

import cn.hutool.core.date.DateUtil;
import org.junit.Test;

public class StringTest {


    @Test
    public void test() {
//        String s = "2019-02-27 00:30:08.774  INFO 115734 --- [TaskScheduler-4] c.g.f.s.FBADFeatureFlinkJobByHour        : Job has been submitted with JobID 51c9090c6441cb8b79f5345c7485be97";
//        System.out.println(s.indexOf("Job has been submitted with JobID"));
//        System.out.println(s.substring(s.indexOf("Job has been submitted with JobID")));
//        String[] ss = s.substring(s.indexOf("Job has been submitted with JobID")).split(" ");
//        for (String s1 : ss) {
//            System.out.println(s1);
//        }
        String s = DateUtil.yesterday().toString("yyyy/MM/dd");
        System.out.println(s);
    }
}
