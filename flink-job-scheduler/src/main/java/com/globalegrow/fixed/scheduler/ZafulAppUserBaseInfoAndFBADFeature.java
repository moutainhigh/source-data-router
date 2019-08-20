package com.globalegrow.fixed.scheduler;

import cn.hutool.core.date.DateUtil;
import com.globalegrow.fixed.queen.FlinkBashJob;
import com.globalegrow.hdfs.utils.HdfsUtil;
import com.globalegrow.util.CommonTextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ZafulAppUserBaseInfoAndFBADFeature extends AbstractFlinkJobSerialScheduler{

//    @Autowired
//    private DelayQueue<DyHdfsCheckExistsJobMessage> flinkJobQueens;

    //private String hdfsPath = "hdfs:///user/wuchao/dw_zaful_recommend/zaful_app_abset_id_user_fb_cookieid_fb/add_time=${last_day}/part-00000";
    private String hdfsPath = "/user/hive/warehouse/dw_zaful_recommend.db/zaful_app_abset_id_user_fb_cookieid_fb/add_time=${last_day}/";

    private String flinkCommandLine = "/usr/local/services/flink/flink-yarn/flink-1.5.0/bin/flink run -d -m yarn-cluster -yn 1 -yjm 1024 -ytm 1024 -yqu root.flink -ynm zaful-app-user-feature-es /usr/local/services/flink/app-user-feature-es-0.1.jar --job.hdfs.path ${hdfs_path} --index-name dy_app_zaful_user_feature";

    @Override
    @Scheduled(cron = "${app.cron.zaful-app-user-fb-freatrue}")
    public void run() throws InterruptedException {
        log.info("zaful app 用户基本信息与用户 Facebook 特征");
        String hdfsPathLastDay = CommonTextUtils.replaceOneParameter(this.hdfsPath, "last_day", DateUtil.yesterday().toString("yyyyMMdd"));

        String checkPhpPath = "hdfs://glbgnameservice" + hdfsPathLastDay;
        String hdfsPathCommandLine = HdfsUtil.getBigDataActiveNamenode() + hdfsPathLastDay;
        String yesterdayCommandLine = CommonTextUtils.replaceOneParameter(this.flinkCommandLine, "hdfs_path", hdfsPathCommandLine);
        log.info("检查文件 {} 是否存在", checkPhpPath);
        this.checkHdfsPath(checkPhpPath);
        FlinkBashJob job = new FlinkBashJob("zaful-user-feature-with-facebook-ad-feature", yesterdayCommandLine);
        log.info("flink job to run : {}", job);
        this.flinkBashJobs.offer(job);
        this.runFlinkJob();
    }

}
