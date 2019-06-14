package com.globalegrow.fixed.scheduler;

import cn.hutool.core.date.DateUtil;
import com.globalegrow.fixed.queen.FlinkBashJob;
import com.globalegrow.hdfs.utils.HdfsUtil;
import com.globalegrow.utils.FlinkJobStatusCheckUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
@Component
@Data
public class ZafulGoodsInfoByDay {

    private String goodsInfoFlinkJobByDay = "/usr/local/services/flink/flink-yarn/flink-1.5.0/bin/flink run -d -m yarn-cluster -yqu root.flink -yn 1 -yjm 1024 -ytm 1024 /usr/local/services/flink/zaful-goods-base-info-es-0.1.jar";

    private String goodsBaseInfoFilePath = "/user/hive/warehouse/temp_zaful_recommend.db/feature_items_base_info_ods_desc/";

    private String goodsStatisticsFilePath = "/user/hive/warehouse/dw_zaful_recommend.db/feature_items_v2_2_ods_info/";
    private String goodsStatistics3FilePath = "/user/hive/warehouse/dw_zaful_recommend.db/feature_items_v2_2_ods_info_three_days/";
    private String goodsStatistics7FilePath = "/user/hive/warehouse/dw_zaful_recommend.db/feature_items_v2_2_ods_info_seven_days/";
    private String goodsStatistics15FilePath = "/user/hive/warehouse/dw_zaful_recommend.db/feature_items_v2_2_ods_info_fifteen_days/";
    private String goodsStatistics30FilePath = "/user/hive/warehouse/dw_zaful_recommend.db/feature_items_v2_2_ods_info_thirty_days/";

    private String goodsCountryStatisticsFilePath = "/user/hive/warehouse/dw_zaful_recommend.db/feature_items_country_v2_2_ods_info/";
    private String goodsCountryStatistics3FilePath = "/user/hive/warehouse/dw_zaful_recommend.db/feature_items_country_v2_2_ods_info_three_days/";
    private String goodsCountryStatistics7FilePath = "/user/hive/warehouse/dw_zaful_recommend.db/feature_items_country_v2_2_ods_info_seven_days/";
    private String goodsCountryStatistics15FilePath = "/user/hive/warehouse/dw_zaful_recommend.db/feature_items_country_v2_2_ods_info_fifteen_days/";
    private String goodsCountryStatistics30FilePath = "/user/hive/warehouse/dw_zaful_recommend.db/feature_items_country_v2_2_ods_info_thirty_days/";

    private LinkedBlockingDeque<FlinkBashJob> flinkBashJobs = new LinkedBlockingDeque<>();

    Map<String, FlinkBashJob> currentBuryLogJobs = new ConcurrentHashMap<>();

    private String flinkJobHistoryServer = "http://bts-master:8082/jobs/";

    @Autowired
    private RestTemplate restTemplate;

    private final String flinkJobCommandPrefix = "/usr/local/services/flink/flink-yarn/flink-1.5.0/bin/flink run -d -m yarn-cluster -yqu root.flink -yn 1 -yjm 1024 -ytm 1024";

    private final String zafulGoodsStatusJar = " /usr/local/services/flink/zaful-goods-statistics-es-0.1.jar ";

    @Scheduled(cron = "${app.cron.zaful-goods-info:0 15 5 * * ?}")
    public void run() throws InterruptedException {
        log.info("zaful 商品信息，开始组装 flink 任务");
        try {
            String yesterday = DateUtil.yesterday().toString("yyyy-MM-dd");

            String[] lastDay = yesterday.split("-");

            String pathDate = "year=" + lastDay[0] + "/month=" + lastDay[1] + "/day=" + lastDay[2];
            // 商品基本信息
            String goodsBaseInfo = "hdfs://glbgnameservice" + this.goodsBaseInfoFilePath + pathDate;
            this.checkHdfsPath(goodsBaseInfo);
            // 提交商品基本信息 flink 任务
            FlinkBashJob goodsBaseInfoJob = new FlinkBashJob("zaful_goods_baseInfo", this.goodsInfoFlinkJobByDay);
            log.info("zaful 商品基本信息 {}", goodsBaseInfoJob);
            this.flinkBashJobs.offer(goodsBaseInfoJob);

            // 商品统计信息 1 天
            String gdstat1 = "hdfs://glbgnameservice" + this.goodsStatisticsFilePath + pathDate;
            this.checkHdfsPath(gdstat1);
            FlinkBashJob goodsState1 = new FlinkBashJob("zaful_goods_statistics_1", this.flinkJobCommandPrefix + " -ynm feature_items_v2_2_ods_info " + this.zafulGoodsStatusJar + " --filePath " + this.goodsStatisticsFilePath + " --good.dimension 1 --index-name dy_zaful_goods_statistics_1 --es.source.fields item_id,pv_cnt,pv_uv,pv_per_cnt,ipv_cnt,ipv_uv,ipv_per_cnt,bag_cnt,bag_uv,bag_per_cnt,favorite_cnt,favorite_uv,favorite_per_cnt,order_item_cnt,order_uv,order_per_cnt,cvr,uv_cvr,order_cnt,order_income,ctr,uv_ctr,platform" + " --file.date " + yesterday);
            log.info("zaful 商品统计信息 1 天 {}", goodsState1);
            this.flinkBashJobs.offer(goodsState1);

            String gdstat3 = "hdfs://glbgnameservice" + this.goodsStatistics3FilePath + pathDate;
            this.checkHdfsPath(gdstat3);
            FlinkBashJob job_gdstat3 = new FlinkBashJob("zaful_goods_statistics_3", this.flinkJobCommandPrefix + "  -ynm feature_items_v2_2_ods_info_three_days " + this.zafulGoodsStatusJar + " --filePath " + this.goodsStatistics3FilePath + " --good.dimension 3 --index-name dy_zaful_goods_statistics_greater_than_1 --es.source.fields item_id,pv_cnt,pv_uv,pv_per_cnt,ipv_cnt,ipv_uv,ipv_per_cnt,bag_cnt,bag_uv,bag_per_cnt,favorite_cnt,favorite_uv,favorite_per_cnt,order_item_cnt,order_uv,order_per_cnt,cvr,uv_cvr,order_cnt,order_income,ctr,uv_ctr,platform --job-name zaful-goods-statistics-es_3_ " + " --file.date " + yesterday);
            log.info("zaful 商品统计信息 3 天 {}", job_gdstat3);
            this.flinkBashJobs.offer(job_gdstat3);

            String gdstat7 = "hdfs://glbgnameservice" + this.goodsStatistics7FilePath + pathDate;
            this.checkHdfsPath(gdstat7);
            FlinkBashJob goodsState3 = new FlinkBashJob("zaful_goods_statistics_7", this.flinkJobCommandPrefix + "  -ynm feature_items_v2_2_ods_info_seven_days " + this.zafulGoodsStatusJar + " --filePath " + this.goodsStatistics7FilePath + " --good.dimension 7 --index-name dy_zaful_goods_statistics_greater_than_1 --es.source.fields item_id,pv_cnt,pv_uv,pv_per_cnt,ipv_cnt,ipv_uv,ipv_per_cnt,bag_cnt,bag_uv,bag_per_cnt,favorite_cnt,favorite_uv,favorite_per_cnt,order_item_cnt,order_uv,order_per_cnt,cvr,uv_cvr,order_cnt,order_income,ctr,uv_ctr,platform --job-name zaful-goods-statistics-es_7_" + " --file.date " + yesterday);
            log.info("zaful 商品统计信息 3 天 {}", goodsState3);
            this.flinkBashJobs.offer(goodsState3);

            String gdstat15 = "hdfs://glbgnameservice" + this.goodsStatistics15FilePath + pathDate;
            this.checkHdfsPath(gdstat15);
            FlinkBashJob jobgdstat15 = new FlinkBashJob("zaful_goods_statistics_15", this.flinkJobCommandPrefix + "  -ynm feature_items_v2_2_ods_info_fifteen_days " + this.zafulGoodsStatusJar + " --filePath " + this.goodsStatistics15FilePath + " --good.dimension 15 --index-name dy_zaful_goods_statistics_greater_than_1 --es.source.fields item_id,pv_cnt,pv_uv,pv_per_cnt,ipv_cnt,ipv_uv,ipv_per_cnt,bag_cnt,bag_uv,bag_per_cnt,favorite_cnt,favorite_uv,favorite_per_cnt,order_item_cnt,order_uv,order_per_cnt,cvr,uv_cvr,order_cnt,order_income,ctr,uv_ctr,platform --job-name zaful-goods-statistics-es_15_" + " --file.date " + yesterday);
            log.info("zaful 商品统计信息 15 天 {}", gdstat15);
            this.flinkBashJobs.offer(jobgdstat15);

            String gdstat30 = "hdfs://glbgnameservice" + this.goodsStatistics30FilePath + pathDate;
            this.checkHdfsPath(gdstat30);
            FlinkBashJob job_gdstat30 = new FlinkBashJob("zaful_goods_statistics_30", this.flinkJobCommandPrefix + "  -ynm feature_items_v2_2_ods_info_thirty_days " + this.zafulGoodsStatusJar + " --filePath " + this.goodsStatistics30FilePath + " --good.dimension 30 --index-name dy_zaful_goods_statistics_greater_than_1 --es.source.fields item_id,pv_cnt,pv_uv,pv_per_cnt,ipv_cnt,ipv_uv,ipv_per_cnt,bag_cnt,bag_uv,bag_per_cnt,favorite_cnt,favorite_uv,favorite_per_cnt,order_item_cnt,order_uv,order_per_cnt,cvr,uv_cvr,order_cnt,order_income,ctr,uv_ctr,platform --job-name zaful-goods-statistics-es_30_" + " --file.date " + yesterday);
            log.info("zaful 商品统计信息 30 天 {}", job_gdstat30);
            this.flinkBashJobs.offer(job_gdstat30);

            // 商品统计信息 国家
            String gdstatCountry1 = "hdfs://glbgnameservice" + this.goodsCountryStatisticsFilePath + pathDate;
            this.checkHdfsPath(gdstatCountry1);
            FlinkBashJob job_gdstatCountry1 = new FlinkBashJob("zaful_goods_country_stat_1", this.flinkJobCommandPrefix + "  -ynm feature_items_country_v2_2_ods_info " + this.zafulGoodsStatusJar + " --filePath " + this.goodsCountryStatisticsFilePath + " --good.dimension 1 --index-name dy_zaful_country_goods_statistics_1 --es.source.fields item_id,country,pv_cnt,pv_uv,pv_per_cnt,ipv_cnt,ipv_uv,ipv_per_cnt,bag_cnt,bag_uv,bag_per_cnt,favorite_cnt,favorite_uv,favorite_per_cnt,order_item_cnt,order_uv,order_per_cnt,cvr,uv_cvr,order_cnt,order_income,ctr,uv_ctr,platform --job-name zaful-goods-country-statistics-es_1_" + " --file.date " + yesterday);
            log.info("zaful 商品统计信息国家 1 天 {}", job_gdstatCountry1);
            this.flinkBashJobs.offer(job_gdstatCountry1);

            String gdstatCountry3 = "hdfs://glbgnameservice" + this.goodsCountryStatistics3FilePath + pathDate;
            this.checkHdfsPath(gdstatCountry3);
            FlinkBashJob job_gdstatCountry3 = new FlinkBashJob("zaful_goods_country_stat_3", this.flinkJobCommandPrefix + "  -ynm feature_items_country_v2_2_ods_info_three_days " + this.zafulGoodsStatusJar + " --filePath " + this.goodsCountryStatistics3FilePath + " --good.dimension 3 --index-name dy_zaful_country_goods_statistics_greater_than_1 --es.source.fields item_id,country,pv_cnt,pv_uv,pv_per_cnt,ipv_cnt,ipv_uv,ipv_per_cnt,bag_cnt,bag_uv,bag_per_cnt,favorite_cnt,favorite_uv,favorite_per_cnt,order_item_cnt,order_uv,order_per_cnt,cvr,uv_cvr,order_cnt,order_income,ctr,uv_ctr,platform --job-name zaful-goods-country-statistics-es_3_" + " --file.date " + yesterday);
            log.info("zaful 商品统计信息国家 3 天 {}", job_gdstatCountry3);
            this.flinkBashJobs.offer(job_gdstatCountry3);

            String gdstatCountry7 = "hdfs://glbgnameservice" + this.goodsCountryStatistics7FilePath + pathDate;
            this.checkHdfsPath(gdstatCountry7);
            FlinkBashJob job_gdstatCountry7 = new FlinkBashJob("zaful_goods_country_stat_7", this.flinkJobCommandPrefix + "  -ynm feature_items_country_v2_2_ods_info_seven_days " + this.zafulGoodsStatusJar + " --filePath " + this.goodsCountryStatistics7FilePath + " --good.dimension 7 --index-name dy_zaful_country_goods_statistics_greater_than_1 --es.source.fields item_id,country,pv_cnt,pv_uv,pv_per_cnt,ipv_cnt,ipv_uv,ipv_per_cnt,bag_cnt,bag_uv,bag_per_cnt,favorite_cnt,favorite_uv,favorite_per_cnt,order_item_cnt,order_uv,order_per_cnt,cvr,uv_cvr,order_cnt,order_income,ctr,uv_ctr,platform --job-name zaful-goods-country-statistics-es_7_" + " --file.date " + yesterday);
            log.info("zaful 商品统计信息国家 7 天 {}", job_gdstatCountry7);
            this.flinkBashJobs.offer(job_gdstatCountry7);

            String gdstatCountry15 = "hdfs://glbgnameservice" + this.goodsCountryStatistics15FilePath + pathDate;
            this.checkHdfsPath(gdstatCountry15);
            FlinkBashJob job_gdstatCountry15 = new FlinkBashJob("zaful_goods_stat_15", this.flinkJobCommandPrefix + "  -ynm feature_items_country_v2_2_ods_info_fifteen_days " + this.zafulGoodsStatusJar + " --filePath  " + this.goodsCountryStatistics15FilePath + "  --good.dimension 15 --index-name dy_zaful_country_goods_statistics_greater_than_1 --es.source.fields item_id,country,pv_cnt,pv_uv,pv_per_cnt,ipv_cnt,ipv_uv,ipv_per_cnt,bag_cnt,bag_uv,bag_per_cnt,favorite_cnt,favorite_uv,favorite_per_cnt,order_item_cnt,order_uv,order_per_cnt,cvr,uv_cvr,order_cnt,order_income,ctr,uv_ctr,platform --job-name zaful-goods-country-statistics-es_15_" + " --file.date " + yesterday);
            log.info("zaful 商品统计信息国家 15 天 {}", job_gdstatCountry15);
            this.flinkBashJobs.offer(job_gdstatCountry15);


            String gdstatCountry30 = "hdfs://glbgnameservice" + this.goodsCountryStatistics30FilePath + pathDate;
            this.checkHdfsPath(gdstatCountry30);
            FlinkBashJob job_gdstatCountry30 = new FlinkBashJob("zaful_goods_stat_30", this.flinkJobCommandPrefix + "  -ynm feature_items_country_v2_2_ods_info_thirty_days " + this.zafulGoodsStatusJar + " --filePath " + this.goodsCountryStatistics30FilePath + " --good.dimension 30 --index-name dy_zaful_country_goods_statistics_greater_than_1 --es.source.fields item_id,country,pv_cnt,pv_uv,pv_per_cnt,ipv_cnt,ipv_uv,ipv_per_cnt,bag_cnt,bag_uv,bag_per_cnt,favorite_cnt,favorite_uv,favorite_per_cnt,order_item_cnt,order_uv,order_per_cnt,cvr,uv_cvr,order_cnt,order_income,ctr,uv_ctr,platform --job-name zaful-goods-country-statistics-es_30_" + " --file.date " + yesterday);
            log.info("zaful 商品统计信息国家 30 天 {}", job_gdstatCountry30);
            this.flinkBashJobs.offer(job_gdstatCountry30);

            String goodsTendency = "hdfs://glbgnameservice" + "/user/hive/warehouse/dw_zaful_recommend.db/feature_items_v2_2_ods_info_new_label/" + pathDate;
            this.checkHdfsPath(goodsTendency);
            FlinkBashJob job_goodsTendency = new FlinkBashJob("zaful-goods-statistics-es_tendency", this.flinkJobCommandPrefix + "  -ynm feature_items_v2_2_ods_info_new_label " + this.zafulGoodsStatusJar + " --filePath /user/hive/warehouse/dw_zaful_recommend.db/feature_items_v2_2_ods_info_new_label/ --good.dimension -1 --index-name dy_zaful_goods_statistics_tendency --es.source.fields item_id,ctr_rising_tendency,cvr_rising_tendency,ctr_stability_index,cvr_stability_index,heat_index,platform --job-name zaful-goods-statistics-es_tendency" + " --file.date " + yesterday);
            log.info("zaful 商品趋势信息 {}", job_goodsTendency);
            this.flinkBashJobs.offer(job_goodsTendency);
            String goodsTendencyCountry = "hdfs://glbgnameservice" + "/user/hive/warehouse/dw_zaful_recommend.db/feature_items_v2_2_ods_info_new_label_country/" + pathDate;
            this.checkHdfsPath(goodsTendencyCountry);
            FlinkBashJob job_goodsTendencyCountry = new FlinkBashJob("zaful-goods-statistics-tendency_country", this.flinkJobCommandPrefix + "  -ynm feature_items_v2_2_ods_info_new_label_country " + this.zafulGoodsStatusJar + " --filePath /user/hive/warehouse/dw_zaful_recommend.db/feature_items_v2_2_ods_info_new_label_country/ --good.dimension -1 --index-name dy_zaful_goods_statistics_tendency_country --es.source.fields item_id,ctr_rising_tendency,cvr_rising_tendency,ctr_stability_index,cvr_stability_index,heat_index,country,platform --job-name zaful-goods-statistics-tendency_country" + " --file.date " + yesterday);
            log.info("zaful 商品趋势信息分国家 {}", job_goodsTendencyCountry);
            this.flinkBashJobs.offer(job_goodsTendencyCountry);
        } catch (InterruptedException e) {
            log.error("zaful 商品信息执行出错, {}", e);
        }
    }

    @Scheduled(fixedDelay = 600000)
    public void runFlinkJob() throws InterruptedException {

        FlinkBashJob job = this.flinkBashJobs.take();
        log.info("zaful 商品统计信息 flink job to run {}", job);
        Process process;
        String jobId;
        //List<String> processList = new ArrayList<String>();
        try {
            process = Runtime.getRuntime().exec(job.getFlinkCommandLine());
            //process.waitFor();

            try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = "";
                while ((line = input.readLine()) != null) {
                    //processList.add(line);
                    log.info("任务信息:{}", line);

                    //log.info("任务提交状态: {}", line.indexOf("Job has been submitted with JobID"));
                    if (line.indexOf("Job has been submitted with JobID") >= 0) {
                        String[] lines = line.substring(line.indexOf("Job has been submitted with JobID")).split(" ");
                        jobId = lines[lines.length - 1];
                        log.info("{} 任务 id {}", job.getJobName(), jobId);
                        this.currentBuryLogJobs.put(jobId, job);
                        Thread.sleep(1000);
                    }

                }
            }

            StringBuilder error = new StringBuilder();
            try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line = "";
                while ((line = input.readLine()) != null) {
                    //processList.add(line);
                    log.error("任务错误信息: {}, job_info", line, job);
                    this.flinkBashJobs.offer(job);
                    error.append(line);
                }
            }

        } catch (Exception e) {
            log.error("flink 任务出错", e);
        }


    }

    @Scheduled(fixedDelay = 15000)
    public void jobStatusCheck() throws InterruptedException {

        log.info("开始检查 zaful 商品信息任务执行状态, 当前任务 {} 数 {}", this.currentBuryLogJobs, this.currentBuryLogJobs.size());

        List<String> finished = new ArrayList<>();

        this.currentBuryLogJobs.entrySet().forEach(stringFlinkBashJobEntry -> {

            log.info("检查 flink job {} 的状态", stringFlinkBashJobEntry.getKey());
            try {
                //Map<String, Object> result = this.restTemplate.getForObject(this.flinkJobHistoryServer + stringFlinkBashJobEntry.getKey(), Map.class);

                if ((System.currentTimeMillis() - stringFlinkBashJobEntry.getValue().getStartCheckTime()) > 86400000 * 3) {
                    log.error("flink 任务 id: {}, 详情: {} ,超过 3 h 未检查到任务状态");
                    finished.add(stringFlinkBashJobEntry.getKey());
                }

                String status = FlinkJobStatusCheckUtils.getJobStatusByJobId(stringFlinkBashJobEntry.getKey());
                if (!FlinkJobStatusCheckUtils.NOT_FOUND.equals(status)) {

                    if ("FINISHED".equals(status)) {
                        log.info("任务 {} 执行成功，清空当前任务 id", stringFlinkBashJobEntry.getKey());
                        finished.add(stringFlinkBashJobEntry.getKey());
                    }
                    if ("FAILED".equals(status)) {
                        log.error("{} 任务执行失败，重新放入队列执行", this.currentBuryLogJobs.get(stringFlinkBashJobEntry.getKey()));
                        this.flinkBashJobs.offer(this.currentBuryLogJobs.get(stringFlinkBashJobEntry.getKey()));
                        finished.add(stringFlinkBashJobEntry.getKey());
                    }

                }

                if (this.flinkBashJobs.size() == 0) {
                    log.info("所有任务执行完毕，通知第三方");
                }

            } catch (Exception e) {
                if (e instanceof HttpClientErrorException) {
                    HttpClientErrorException httpClientErrorException = (HttpClientErrorException) e;
                    log.info("任务状态检查结果: {}", httpClientErrorException.getStatusText());

                } else {
                    // 发送邮件
                    log.error("任务状态检查出错", e);
                }

            }

        });

        finished.forEach(s -> currentBuryLogJobs.remove(s));

    }


    private void checkHdfsPath(String checkPath) throws InterruptedException {
        long start = System.currentTimeMillis();
        while (!HdfsUtil.bigDataFileExist(checkPath)) {
            if ((System.currentTimeMillis() - start) > 86400000) {
                log.error("zaful 商品信息，文件 {} , 超过一天未生成, 开始时间 {}", checkPath, DateFormatUtils.format(start, "yyyy-MM-dd HH:mm:ss"));
                break;
            }
            log.info("{} 不存在，等待 10 分钟", checkPath);
            Thread.sleep(600000);
        }
        log.info("{} 存在 ,提交 flink 任务", checkPath);

    }

}
