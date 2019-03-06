package com.globalegrow.fixed.scheduler;

import cn.hutool.core.date.DateUtil;
import com.globalegrow.fixed.queen.FlinkBashJob;
import com.globalegrow.hdfs.utils.HdfsUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

@Component
@Data
@Slf4j
public class BuryLogDataSyncByDay {

    private String rootPcPath = "/bigdata/ods/log_clean/ods_pc_burial_log/current_day/";
    private String rootAppPath = "/bigdata/ods/log_clean/ods_app_burial_log/current_day/";
    private String rootPHPPath = "/bigdata/ods/log_clean/ods_php_burial_log/current_day/";

    private String flinkJobHistoryServer = "http://bts-master:8082/jobs/";

    @Autowired
    private LinkedBlockingDeque<FlinkBashJob> flinkBashJobs;

    @Autowired
    Map<String, FlinkBashJob> currentBuryLogJobs;

    private String dyDfsPcRootPath = "hdfs:///user/hadoop/bumblebee/web/site/current_day/";
    private String dyDfsAppRootPath = "hdfs:///user/hadoop/bumblebee/app/site/current_day/";

    private volatile String currentJobId;

    private String jobCommandLinePc24 = "/usr/local/services/flink/flink-yarn/flink-1.5.0/bin/flink run -d -m yarn-cluster -yqu root.flink -yn 1 -yjm 8192 -ytm 2048 /usr/local/services/flink/remote-orc-hdfs-to-local-hdfs-avro-sink-0.1.jar --orc.schema struct<behaviour_type:string,user_name:string,search_input_word:string,search_type:string,click_times:int,search_result_word:string,referer:string,cookie_id:string,page_module:string,page_sku:string,osr_landing_url:string,page_sub_type:string,time_local:string,user_ip:string,user_agent:string,real_client_ip:string,accept_language:string,activity_template:int,sub_event_info:string,session_id:string,platform:string,country_code:string,search_suk_type:string,page_info:string,url_suffix:string,page_stay_time:string,skuinfo:string,country_name:string,login_status:string,sku_warehouse_info:string,country_number:string,page_main_type:string,last_page_url:string,link_id:string,sent_bytes_size:int,user_id:string,time_stamp:bigint,sub_event_field:string,search_click_position:int,osr_referer_url:string,current_page_url:string,site_code:string,page_code:string,log_id:string,other:string,bts:string,fingerprint:string,unix_time:string> --filePath job_bigdata_bury_path --out.path dy_hdfs_path --schema.table-name Ods_pc_burial_log --job.parallelism 8 -job-name pc_bury_log_site_data";
    private String jobCommandLinePc8 = "/usr/local/services/flink/flink-yarn/flink-1.5.0/bin/flink run -d -m yarn-cluster -yqu root.flink -yn 1 -yjm 1024 -ytm 1024 /usr/local/services/flink/remote-orc-hdfs-to-local-hdfs-avro-sink-0.1.jar --orc.schema struct<behaviour_type:string,user_name:string,search_input_word:string,search_type:string,click_times:int,search_result_word:string,referer:string,cookie_id:string,page_module:string,page_sku:string,osr_landing_url:string,page_sub_type:string,time_local:string,user_ip:string,user_agent:string,real_client_ip:string,accept_language:string,activity_template:int,sub_event_info:string,session_id:string,platform:string,country_code:string,search_suk_type:string,page_info:string,url_suffix:string,page_stay_time:string,skuinfo:string,country_name:string,login_status:string,sku_warehouse_info:string,country_number:string,page_main_type:string,last_page_url:string,link_id:string,sent_bytes_size:int,user_id:string,time_stamp:bigint,sub_event_field:string,search_click_position:int,osr_referer_url:string,current_page_url:string,site_code:string,page_code:string,log_id:string,other:string,bts:string,fingerprint:string,unix_time:string> --filePath job_bigdata_bury_path --out.path dy_hdfs_path --schema.table-name Ods_pc_burial_log --job.parallelism 8 -job-name pc_bury_log_site_data";

    private String jobCommandLineApp24 = "/usr/local/services/flink/flink-yarn/flink-1.5.0/bin/flink run -d -m yarn-cluster -yqu root.flink -yn 1 -yjm 8192 -ytm 2048 /usr/local/services/flink/remote-orc-hdfs-to-local-hdfs-avro-sink-0.1.jar --orc.schema struct<attributed_touch_type:string,attributed_touch_time:string,event_type:string,attribution_type:string,click_time:string,download_time:string,install_time:string,media_source:string,agency:string,af_channel:string,af_keywords:string,campaign:string,af_c_id:string,af_adset:string,af_adset_id:string,af_ad:string,af_ad_id:string,fb_campaign_name:string,fb_campaign_id:string,fb_adset_name:string,fb_adset_id:string,fb_adgroup_name:string,fb_adgroup_id:string,af_ad_type:string,af_siteid:string,af_sub1:string,af_sub2:string,af_sub3:string,af_sub4:string,af_sub5:string,http_referrer:string,click_url:string,af_cost_model:string,af_cost_value:string,af_cost_currency:string,cost_per_install:string,is_retargeting:string,re_targeting_conversion_type:string,country_code:string,city:string,ip:string,wifi:string,mac:string,operator:string,carrier:string,language:string,appsflyer_device_id:string,advertising_id:string,android_id:string,customer_user_id:string,imei:string,idfa:string,platform:string,device_brand:string,device_model:string,os_version:string,app_version:string,sdk_version:string,app_id:string,app_name:string,bundle_id:string,event_time:string,event_name:string,event_value:string,currency:string,download_time_selected_timezone:string,click_time_selected_timezone:string,install_time_selected_timezone:string,event_time_selected_timezone:string,selected_currency:string,revenue_in_selected_currency:string,cost_in_selected_currency:string,other:string,unix_time:string> --filePath job_bigdata_bury_path --out.path dy_hdfs_path --schema.table-name Ods_app_burial_log --job.parallelism 8 --job-name app_bury_log_site_data";

    private String jobCommandLinePHP1 = "/usr/local/services/flink/flink-yarn/flink-1.5.0/bin/flink run -d -m yarn-cluster -yqu root.flink -yn 1 -yjm 1024 -ytm 1024 -s 1 /usr/local/services/flink/remote-orc-hdfs-to-local-hdfs-avro-sink-0.1.jar --orc.schema struct<session_id:string,user_id:string,last_page_url:string,site_code:string,time_stamp:bigint,behaviour_type:string,current_page_url:string,sub_event_field:string,skuinfo:string,sub_event_info:string,user_agent:string,real_client_ip:string,link_id:string,cookie_id:string,osr_landing_url:string,page_sub_type:string,page_main_type:string,country_number:string,platform:string,other:string,osr_referer_url:string,unix_time:string,bts:string> --filePath job_bigdata_bury_path  --out.path dy_hdfs_path --schema.table-name Ods_php_burial_log --job.parallelism 1 --job-name php_bury_log_site_data";

    private static final String BIGDATA_PATH_VAR = "job_bigdata_bury_path";
    private static final String DY_PATH_VAR = "dy_hdfs_path";
    private static final String JOB_NAME_VAR = "bury_log_site";

    // 等待大数据取数临界点
    private Integer jobCountCylins = 10;

    @Autowired
    private RestTemplate restTemplate;

    @Scheduled(cron = "${app.cron.bury-log-data}")
    public void run() throws InterruptedException {

        String currentDay = DateUtil.yesterday().toString("yyyy/MM/dd");
        log.info("组装任务 Map");
        log.info("开始同步埋点数据，检查当天 pc 埋点目录是否存在");

        String currentDayPcPath = this.rootPcPath.replace("current_day", currentDay);

        String hdfsPath = HdfsUtil.getBigDataActiveNamenode() + currentDayPcPath;
        String checkPath = "hdfs://glbgnameservice" + currentDayPcPath;
        if (HdfsUtil.bigDataFileExist(checkPath)) {
            log.info("埋点文件目录 {} 存在，开始组装任务", checkPath);
        } else {
            log.info("埋点文件目录 {} 不存在, 开始每 5 分钟的检查", checkPath);
            int pcCount = 1;
            Thread.sleep(360000);
            while (!HdfsUtil.bigDataFileExist(checkPath)) {
                pcCount++;
                log.info("埋点文件目录 {} 不存在, 开始每 5 分钟的检查 {}", checkPath, pcCount);
                Thread.sleep(360000);
            }
        }
        // 组装 pc 埋点表任务
        // zaful pc 站
        this.flinkBashJobs.offer(this.flinkPcBuryLogDataJob(currentDayPcPath, currentDay, "zaful", "zaful_pc_bury_log_", this.jobCommandLinePc24));
        this.flinkBashJobs.offer(this.flinkPcBuryLogDataJob(currentDayPcPath, currentDay, "gearbest", "gearbest_pc_bury_log_", this.jobCommandLinePc24));
        this.flinkBashJobs.offer(this.flinkPcBuryLogDataJob(currentDayPcPath, currentDay, "rosegal", "rosegal_pc_bury_log_", this.jobCommandLinePc24));
        this.flinkBashJobs.offer(this.flinkPcBuryLogDataJob(currentDayPcPath, currentDay, "rosewholesale", "rosewholesale_pc_bury_log_", this.jobCommandLinePc8));
        this.flinkBashJobs.offer(this.flinkPcBuryLogDataJob(currentDayPcPath, currentDay, "dresslily", "dresslily_pc_bury_log_", this.jobCommandLinePc24));

        // app 埋点数据
        String currentDayAppPath = this.rootAppPath.replace("current_day", currentDay);
        String appPath = HdfsUtil.getBigDataActiveNamenode() + currentDayAppPath;
        String checkAppPath = "hdfs://glbgnameservice" + currentDayAppPath;
        if (HdfsUtil.bigDataFileExist(checkAppPath)) {
            log.info("埋点文件目录 {} 存在，开始组装任务", checkAppPath);
        } else {
            log.info("埋点文件目录 {} 不存在, 开始每 5 分钟的检查", checkAppPath);
            int pcCount = 1;
            Thread.sleep(360000);
            while (!HdfsUtil.bigDataFileExist(appPath)) {
                pcCount++;
                log.info("埋点文件目录 {} 不存在, 开始每 5 分钟的检查 {}", checkAppPath, pcCount);
                Thread.sleep(360000);

            }
        }

        this.flinkBashJobs.offer(this.flinkAppBuryLogDataJob(currentDayAppPath, currentDay, "zaful", "zaful_zpp_", this.jobCommandLineApp24));
        this.flinkBashJobs.offer(this.flinkAppBuryLogDataJob(currentDayAppPath, currentDay, "gearbest", "gearbest_zpp_", this.jobCommandLineApp24));

        // php 埋点数据
        String php = HdfsUtil.getBigDataActiveNamenode() + this.rootPHPPath.replace("current_day", currentDay);
        String checkPhpPath = "hdfs://glbgnameservice" + this.rootPHPPath.replace("current_day", currentDay);
        //String phpPath = HdfsUtil.getBigDataActiveNamenode() + currentDayAppPath;
        if (HdfsUtil.bigDataFileExist(checkPhpPath)) {
            log.info("埋点文件目录 {} 存在，开始组装任务", checkPhpPath);
        } else {
            log.info("埋点文件目录 {} 不存在, 开始每 5 分钟的检查", checkPhpPath);
            int pcCount = 1;
            Thread.sleep(360000);
            while (!HdfsUtil.bigDataFileExist(checkPhpPath)) {
                pcCount++;
                log.info("埋点文件目录 {} 不存在, 开始每 5 分钟的检查 {}", checkPhpPath, pcCount);
                Thread.sleep(360000);

            }
        }
        String phpCommandLine = this.jobCommandLinePHP1.replace(BIGDATA_PATH_VAR, this.rootPHPPath.replace("current_day", currentDay))
                .replace(DY_PATH_VAR, this.dyDfsPcRootPath.replace("site", "php_burry_log").replace("current_day", currentDay)).replace(JOB_NAME_VAR, "php_burry_log");
        this.flinkBashJobs.offer(new FlinkBashJob("php_log_" + currentDay, phpCommandLine));

        log.info("任务初始化完成，任务队列 {}, 任务数 {}", this.flinkBashJobs, this.flinkBashJobs.size());
    }

    private FlinkBashJob flinkPcBuryLogDataJob(String currentDayPcPath, String currentDay, String site, String jobName, String baseCommandLine) {
        String dfsPcZaful = currentDayPcPath + site;
        String dyOutPathPcZaful = this.dyDfsPcRootPath.replace("site", site).replace("current_day", currentDay);
        String zafulPcComandLine = baseCommandLine.replace(BIGDATA_PATH_VAR, dfsPcZaful).replace(DY_PATH_VAR, dyOutPathPcZaful).replace(JOB_NAME_VAR, site);
        FlinkBashJob pcZaful = new FlinkBashJob(jobName + currentDay, zafulPcComandLine);
        return pcZaful;
    }

    private FlinkBashJob flinkAppBuryLogDataJob(String currentDayAppPath, String currentDay, String site, String jobName, String baseCommandLine) {
        String dfsPcZaful = currentDayAppPath + site;
        String dyOutPathPcZaful = this.dyDfsAppRootPath.replace("site", site).replace("current_day", currentDay);
        String zafulPcComandLine = baseCommandLine.replace(BIGDATA_PATH_VAR, dfsPcZaful).replace(DY_PATH_VAR, dyOutPathPcZaful).replace(JOB_NAME_VAR, site);
        FlinkBashJob pcZaful = new FlinkBashJob(jobName + currentDay, zafulPcComandLine);
        return pcZaful;
    }

    //@Scheduled(cron = "${app.cron.bury-log-data-flink-job-status}")
    @Scheduled(fixedDelay = 15000)
    public void jobStatusCheck() throws InterruptedException {

        //log.info("开始检查任务执行状态");


        if (StringUtils.isNotEmpty(this.getCurrentJobId())) {
            log.info("检查 flink job {} 的状态", this.getCurrentJobId());
            try {
                Map<String, Object> result = this.restTemplate.getForObject(this.flinkJobHistoryServer + this.getCurrentJobId(), Map.class);
                if (result != null && StringUtils.isNotEmpty((String) result.get("state"))) {
                    String status = (String) result.get("state");
                    if ("FINISHED".equals(status)) {
                        log.info("任务 {} 执行成功，清空当前任务 id", this.getCurrentJobId());
                        this.currentBuryLogJobs.remove(this.getCurrentJobId());
                        this.setCurrentJobId("");
                    }
                    if ("FAILED".equals(status)) {
                        log.error("{} 任务执行失败，重新放入队列执行", this.currentBuryLogJobs.get(this.getCurrentJobId()));
                        this.flinkBashJobs.offer(this.currentBuryLogJobs.get(this.getCurrentJobId()));
                        this.currentBuryLogJobs.remove(this.getCurrentJobId());
                        this.setCurrentJobId("");
                    }
                    if (this.flinkBashJobs.size() == 0) {
                        log.info("所有任务执行完毕，通知第三方");
                    }
                }
            } catch (Exception e) {
                if (e instanceof HttpClientErrorException) {
                    HttpClientErrorException httpClientErrorException = (HttpClientErrorException) e;
                    log.info("任务状态检查结果: {}", httpClientErrorException.getStatusText());

                }else {
                    // 发送邮件
                    log.error("任务状态检查出错", e);
                }

            }


        }


    }

}
