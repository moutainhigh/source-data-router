package com.globalegrow;

import com.globalegrow.fixed.scheduler.GbAppUserEventByDay;
import com.globalegrow.hdfs.utils.HdfsUtil;
import org.junit.Test;

/**
 * @Auther: joker
 * @Date: 2019/8/19 16:44
 * @Description:
 */
public class test {

    @Test
    public  void test() throws Exception {
        GbAppUserEventByDay gbAppUserEventByDay=new GbAppUserEventByDay();
        gbAppUserEventByDay.run();

//        String yesterdayhdfsPath = CommonTextUtils.replaceOneParameter(hdfsPath, "last_day",
//                DateUtil.yesterday().toString("yyyy-MM-dd"));
//        checkHdfsPath("hdfs://glbgnameservice" + yesterdayhdfsPath);
//        System.out.println(yesterdayhdfsPath);
//        String bigdatahdfsPath = HdfsUtil.getBigDataActiveNamenode();
//        System.out.println(bigdatahdfsPath);
//        String hdfsCommad = CommonTextUtils.replaceOneParameter(commandLine, "name_node_server1",
//                bigdatahdfsPath);
//        System.out.println(hdfsCommad);
//        String finalCommad = CommonTextUtils.replaceOneParameter(hdfsCommad, "yestoday_file1", yesterdayhdfsPath);
//        System.out.println(finalCommad);
    }

    public  void checkHdfsPath(String checkPath) throws InterruptedException {

        while (!HdfsUtil.bigDataFileExist(checkPath)) {


            Thread.sleep(600000);

        }


    }
}
