package com.globalegrow.fixed.scheduler;

import cn.hutool.core.date.DateUtil;
import com.globalegrow.constants.BtsRecommendReportId;
import com.globalegrow.fixed.queen.FlinkBashJob;
import com.globalegrow.hdfs.utils.HdfsUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 天池推荐报表
 */
@Component
@Data
@Slf4j
public class ZafulRecommendReport extends AbstractFlinkJobSerialScheduler {

    private String zafulOrderInfoPath = "hdfs://glbgnameservice/bigdata/ods/zaful/ods_m_zaful_eload_order_info/dt=";
    private String zafulOrderGoodsInfoPath = "hdfs://glbgnameservice/bigdata/ods/zaful/ods_m_zaful_eload_order_goods/dt=";

    private String baseCommand = "/usr/local/services/flink/flink-yarn/flink-1.5.0/bin/flink run -d -m yarn-cluster -yqu root.flink -yjm 1024 -ytm 4096 /usr/local/services/flink/dy_ai_recommend_report-0.1.jar --bury.platform ";

    private String baseParameterCommand = " --bury.date _bury_date --order.table.date _order_table_date --site zaful --job.parallelism 1 ";

    private String pcJobCommand = baseCommand + "pc" + baseParameterCommand;

    private String appJobCommand = baseCommand + "app" + baseParameterCommand;

    private String pcBtsCommand = baseCommand.replace("8192", "4096") + "pc_zaful_flatten_bts_pc" + baseParameterCommand + " --bts.planid ";

    private String appBtsCommand = baseCommand.replace("8192", "4096") + "pc_zaful_flatten_bts_app" + baseParameterCommand + " --bts.planid ";

    private String pcAndAppOrderRecountCommand = baseCommand.replace("8192", "1024") + "recount_order" + baseParameterCommand;

    private String pcBtsOrderRecountCommand = baseCommand.replace("8192", "1024") + "recount_order_bts_pc" + baseParameterCommand + " --bts.planid ";

    private String appBtsOrderRecountCommand = baseCommand.replace("8192", "1024") + "recount_order_bts_app" + baseParameterCommand + " --bts.planid ";

    private volatile String cart14JobId;

    private LinkedBlockingDeque<FlinkBashJob> allQuoteJobs = new LinkedBlockingDeque<>();
    private LinkedBlockingDeque<FlinkBashJob> allQuoteBtsJobs = new LinkedBlockingDeque<>();
    //private LinkedBlockingDeque<FlinkBashJob> recountOrderJobs = new LinkedBlockingDeque<>();
    private LinkedBlockingDeque<FlinkBashJob> recountOrderBtsJobs = new LinkedBlockingDeque<>();

    private Map<String, FlinkBashJob> cart14OrderJobs = new ConcurrentHashMap<>();
    private Map<String, FlinkBashJob> currentBtsOrderRecountJobs = new ConcurrentHashMap<>();
    private AtomicInteger cart14Status = new AtomicInteger(0);

    private String cart14JobCommand = "/usr/local/services/flink/flink-yarn/flink-1.5.0/bin/flink run -d -m yarn-cluster -yqu root.flink -yjm 1024 -ytm 16384 /usr/local/services/flink/zaful_last14_cart_and_current_order_rel-0.1.jar --bury.date _bury_date --job.parallelism 1";

    private String currentBtsOrderRecountJobId = "";
    private AtomicInteger currentBtsOrderRecountJobStatus = new AtomicInteger(0);

    @Scheduled(cron = "${app.cron.bury-log-data:0 1 0 * * ?}")
    public void cart14JobRun() throws InterruptedException {
        // 检查 pc app 的 目录是否存在
        String currentDay = DateUtil.yesterday().toString("yyyy/MM/dd");
        checkHdfsPath("hdfs://glbgnameservice" + this.rootPcPath.replace("current_day", currentDay) + SUCCESS_FULL_FILE);
        checkHdfsPath("hdfs://glbgnameservice" + this.rootAppPath.replace("current_day", currentDay) + SUCCESS_FULL_FILE);
        //  cookie 与 用户 id 关联关系是否更新完成
        checkHdfsPath("hdfs://glbgnameservice" + "/user/hive/warehouse/dw_proj.db/cookieid_userid_ods_zf_gb_rg_dl/add_time=" + currentDay.replaceAll("/", ""));

        String commandLine = this.cart14JobCommand.replaceAll("_bury_date", currentDay);

        FlinkBashJob flinkBashJob = new FlinkBashJob("zaful_cart14", commandLine);

        String jobId = this.execFlinkJob(flinkBashJob);
        this.setCart14JobId(jobId);
        this.cart14Status.set(1);
        this.cart14OrderJobs.put(jobId, flinkBashJob);
    }

    @Scheduled(cron = "${app.cron.cart14-data:0 30 0 * * ?}")
    public void checkCart14Status() throws InterruptedException {
        String currentDay = DateUtil.yesterday().toString("yyyy/MM/dd");
        String orderTableDate = currentDay.replaceAll("/", "");
        while (this.cart14Status.get() == 1) {

            log.info("检查 zaful 最近 14 天的加购数据是否运行成功");
            try {
                Map<String, Object> result = this.restTemplate.getForObject(this.flinkJobHistoryServer + this.getCart14JobId(), Map.class);
                if (result != null && StringUtils.isNotEmpty((String) result.get("state"))) {
                    String status = (String) result.get("state");
                    if ("FINISHED".equals(status)) {
                        log.info("任务 {} 执行成功，清空当前任务 id，提交全量任务", this.getCart14JobId());
                        this.cart14Status.set(0);
                        // 整站指标与推荐位指标
                        // pc _bury_date --order.table.date _order_table_date
                        this.checkOrderInfo();
                        FlinkBashJob pcJob = new FlinkBashJob("pc_zaful_report_site_rec_" + currentDay, this.pcJobCommand.replace("_bury_date", currentDay).replace("_order_table_date", orderTableDate));
                        this.allQuoteJobs.offer(pcJob);
                        // app
                        FlinkBashJob appJob = new FlinkBashJob("pc_zaful_report_site_rec_" + currentDay, this.appJobCommand.replace("_bury_date", currentDay).replace("_order_table_date", orderTableDate));
                        this.allQuoteJobs.offer(appJob);
                        // bts 指标， 读取 bts 实验 id
                        String pcPlanId = HdfsUtil.getDyFileContentString(BtsRecommendReportId.planIdPc.getFilePath());
                        if (StringUtils.isNotEmpty(pcPlanId)) {
                            for (String s : pcPlanId.split(",")) {
                                if (StringUtils.isNotEmpty(s)) {
                                    FlinkBashJob job = new FlinkBashJob("zaful_pc_bts_report_" + pcPlanId, this.pcBtsCommand.replace("_bury_date", currentDay).replace("_order_table_date", orderTableDate) + s);
                                    this.allQuoteBtsJobs.offer(job);
                                }

                            }
                        }
                        String appPlanId = HdfsUtil.getDyFileContentString(BtsRecommendReportId.planIdApp.getFilePath());
                        if (StringUtils.isNotEmpty(appPlanId)) {
                            for (String s : appPlanId.split(",")) {
                                if (StringUtils.isNotEmpty(s)) {
                                    FlinkBashJob job = new FlinkBashJob("zaful_app_bts_report_" + pcPlanId, this.appBtsCommand.replace("_bury_date", currentDay).replace("_order_table_date", orderTableDate) + s);
                                    this.allQuoteBtsJobs.offer(job);
                                }

                            }
                        }
                    }
                    if ("FAILED".equals(status)) {
                        log.error("{} 任务执行失败，重新放入队列执行", this.cart14OrderJobs.get(this.getCart14JobId()));
                        String jobId = this.execFlinkJob(this.cart14OrderJobs.get(this.getCart14JobId()));
                        this.setCart14JobId(jobId);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            Thread.sleep(360000);
        }

    }

    /**
     * 近 6 天订单重计算
     *
     * @throws InterruptedException
     */
    @Scheduled(cron = "${app.cron.cart14-data:0 2 0 * * ?}")
    public void initOrderRecounterJobs() throws InterruptedException, ParseException {
        this.checkOrderInfo();
        String buryDate = DateUtil.yesterday().toString("yyyy/MM/dd");

        for (String day : last6Days(buryDate)) {

            if (HdfsUtil.dyFileExist("hdfs://glbgnameservice/user/hadoop/report/app/" + day)
                    && HdfsUtil.dyFileExist("hdfs://glbgnameservice/user/hadoop/report/pc/" + day)) {
                FlinkBashJob orderRecountJob = new FlinkBashJob("zaful_order_recount_" + day, this.pcAndAppOrderRecountCommand.replace("_bury_date", buryDate).replace("_order_table_date", buryDate.replaceAll("/", "")));
                this.execFlinkJob(orderRecountJob);

                // bts 订单重计算
                String pcPlanId = HdfsUtil.getDyFileContentString(BtsRecommendReportId.planIdPc.getFilePath());
                if (StringUtils.isNotEmpty(pcPlanId)) {
                    for (String s : pcPlanId.split(",")) {
                        if (StringUtils.isNotEmpty(s)) {
                            FlinkBashJob job = new FlinkBashJob("zaful_pc_bts_report_" + s, this.pcBtsOrderRecountCommand.replace("_bury_date", day).replace("_order_table_date", buryDate) + s);
                            this.recountOrderBtsJobs.offer(job);
                        }

                    }
                }

                String appPlanId = HdfsUtil.getDyFileContentString(BtsRecommendReportId.planIdApp.getFilePath());
                if (StringUtils.isNotEmpty(appPlanId)) {
                    for (String s : appPlanId.split(",")) {
                        if (StringUtils.isNotEmpty(s)) {
                            FlinkBashJob job = new FlinkBashJob("zaful_pc_bts_report_" + s, this.appBtsOrderRecountCommand.replace("_bury_date", day).replace("_order_table_date", buryDate) + s);
                            this.recountOrderBtsJobs.offer(job);
                        }

                    }
                }

            }


        }

    }

    @Scheduled(fixedDelay = 10000)
    public void runBtsOrderRecount() throws InterruptedException {

        if (this.recountOrderBtsJobs.size() > 0 && this.currentBtsOrderRecountJobStatus.get() == 0 && StringUtils.isEmpty(this.currentBtsOrderRecountJobId)) {
            this.currentBtsOrderRecountJobStatus.set(1);
            FlinkBashJob flinkBashJob = this.recountOrderBtsJobs.take();
            if (flinkBashJob != null) {

                String jobId = this.execFlinkJob(flinkBashJob);
                this.currentBtsOrderRecountJobs.put(jobId, flinkBashJob);
                this.currentBtsOrderRecountJobStatus.set(0);
                this.setCurrentBtsOrderRecountJobId(jobId);

            }

        }

    }

    @Scheduled(fixedDelay = 10000)
    public void checkOrderRecountBts() {

        String status = this.checkJobStatus(this.currentBtsOrderRecountJobId);

        if ("FINISHED".equals(status)) {
            log.info("{} 执行成功", this.currentBtsOrderRecountJobId);
            this.setCurrentBtsOrderRecountJobId("");
        }
        if ("FAILED".equals(status)) {
            log.error("{} 执行失败，重新执行", this.currentBtsOrderRecountJobId);
            this.recountOrderBtsJobs.offer(this.currentBtsOrderRecountJobs.get(this.currentBtsOrderRecountJobId));
        }

    }

    /**
     * 全部任务执行完成后 给表添加分区
     */
    @Scheduled(cron = "${app.cron.cart14-data:0 30 1 * * ?}")
    public void checkJobStatus() throws InterruptedException {

        String yestoday = DateUtil.yesterday().toString("yyyy/MM/dd");

        String[] yestodays = yestoday.split("/");

        while (this.allQuoteJobs.size() > 0 && this.allQuoteBtsJobs.size() > 0 && this.recountOrderBtsJobs.size() > 0 && this.getCurrentBuryLogJobs().size() >= 0) {
            log.info("任务未完成，等待十分钟，当前任务数，全量指标：{}，bts 全量指标：{}，bts 前 7 天订单重算：{}", this.allQuoteJobs.size(), this.allQuoteBtsJobs.size(), this.recountOrderBtsJobs.size());
            Thread.sleep(600000);
        }

        // alter table report.fact_recommend_data_app add PARTITION(year='2019',month='01',day='01') location 'hdfs:///user/hadoop/bumblebee/report/app';
        // alter table report.fact_recommend_data_pcm add PARTITION(year='2019',month='01',day='01') location 'hdfs:///user/hadoop/bumblebee/report/pc';
        String appTable = "hive -e alter table report.fact_recommend_data_app add PARTITION(year='" + yestodays[0] + "',month='" + yestodays[1] + "',day='" + yestodays[20] + "') location 'hdfs:///user/hadoop/bumblebee/report/app/" + yestoday + "'";
        String pcTable = "hive -e alter table report.fact_recommend_data_pcm add PARTITION(year='" + yestodays[0] + "',month='" + yestodays[1] + "',day='" + yestodays[20] + "') location 'hdfs:///user/hadoop/bumblebee/report/app/" + yestoday + "'";
        try {
            Process processPc = Runtime.getRuntime().exec(appTable);


            StringBuilder error = new StringBuilder();
            try (BufferedReader input = new BufferedReader(new InputStreamReader(processPc.getErrorStream()))) {
                String line = "";
                while ((line = input.readLine()) != null) {
                    //processList.add(line);
                    log.info("任务错误信息: {}", line);

                    error.append(line);
                }
            }

            Process processApp = Runtime.getRuntime().exec(pcTable);

            StringBuilder errorApp = new StringBuilder();
            try (BufferedReader input = new BufferedReader(new InputStreamReader(processApp.getErrorStream()))) {
                String line = "";
                while ((line = input.readLine()) != null) {
                    //processList.add(line);
                    log.info("任务错误信息: {}", line);

                    errorApp.append(line);
                }
            }
        } catch (IOException e) {
            log.error("表添加分区失败{}，{}", appTable, pcTable, e);
        }

    }

    /**
     * 失败的任务放到基类当前任务中，按顺序执行
     * @throws InterruptedException
     */
    @Scheduled(fixedDelay = 10000)
    public void runAllQuoteAndBtsQuote() throws InterruptedException {

        if (this.allQuoteJobs.size() > 0) {
            FlinkBashJob allQuoteJob = this.allQuoteJobs.take();
            String jobId = this.execFlinkJob(allQuoteJob);
            this.getCurrentBuryLogJobs().put(jobId, allQuoteJob);
        }

        if (this.allQuoteBtsJobs.size() > 0) {
            FlinkBashJob allQuoteJob = this.allQuoteBtsJobs.take();
//            String jobId = this.execFlinkJob(allQuoteJob);
//            this.getCurrentBuryLogJobs().put(jobId, allQuoteJob);
            super.getFlinkBashJobs().offer(allQuoteJob);
        }

    }

    private void checkOrderInfo() throws InterruptedException {
        String ytd = DateUtil.yesterday().toString("yyyy/MM/dd").replaceAll("/", "");
        this.checkHdfsPath(this.zafulOrderInfoPath + ytd + "/" + SUCCESS_FULL_FILE);
        this.checkHdfsPath(this.zafulOrderGoodsInfoPath + ytd + "/" + SUCCESS_FULL_FILE);
    }

    @Scheduled(fixedDelay = 10000)
    @Override
    public void run() throws InterruptedException {

        this.runFlinkJob();

    }


    public static List<String> last6Days(String buryDate) throws ParseException {
        List<String> list = new ArrayList<>();
        DateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fmt.parse(buryDate));
        for (int i = 0; i < 5; i++) {
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1);
            String date = DateFormatUtils.format(calendar.getTime(), "yyyy/MM/dd");
            list.add(date);
        }
        return list;
    }


}
