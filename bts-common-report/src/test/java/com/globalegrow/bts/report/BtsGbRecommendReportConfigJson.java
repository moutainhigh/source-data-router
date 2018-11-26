package com.globalegrow.bts.report;

import com.globalegrow.report.JsonLogFilter;
import com.globalegrow.report.ReportBuildRule;
import com.globalegrow.report.ReportKafkaConfig;
import com.globalegrow.report.ReportQuotaFieldConfig;
import com.globalegrow.util.DyBeanUtils;
import com.globalegrow.util.JacksonUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BtsGbRecommendReportConfigJson {

    private Map<String, String> bts = new HashMap() {

        private static final long serialVersionUID = 3456809019097454769L;

        {
            put("planid", "_skip");
            put("versionid", "_skip");
            put("bucketid", "_skip");
        }
    };

    @Test
    public void generate() throws Exception {
        ReportBuildRule rule = new ReportBuildRule();
        rule.setGlobaleFilter(true);

        List<JsonLogFilter> globaleFilters = new ArrayList<>();

        JsonLogFilter btsFilter = new JsonLogFilter();
        btsFilter.setJsonPath("$.glb_bts");
        btsFilter.setFilterRule("not_null");
        globaleFilters.add(btsFilter);

        JsonLogFilter siteFilter = new JsonLogFilter();
        siteFilter.setJsonPath("$.glb_d");
        siteFilter.setValueFilter("10013");
        globaleFilters.add(siteFilter);

        rule.setGlobaleJsonFilters(globaleFilters);

        rule.setReportName("BTS_GB_ORDER_SEARCH_REC");
        rule.setDescription("zaful 搜索算法 ab 测试报表取数指标配置");
        SearchRecommendReportQuotaModel searchRecommendReportQuotaModel = new SearchRecommendReportQuotaModel();

        rule.setReportDefaultValues(DyBeanUtils.objToMap(searchRecommendReportQuotaModel));


        ReportKafkaConfig reportKafkaConfig = new ReportKafkaConfig();
        reportKafkaConfig.setBootstrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setDataSourceTopic("glbg-analitic");
        reportKafkaConfig.setBootstrapGroupId("dy_bts_search_rec_report");
        reportKafkaConfig.setReportStrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setReportDataTopic("dy_bts_search_rec_report");

        rule.setReportFromKafka(reportKafkaConfig);


        List<ReportQuotaFieldConfig> reportQuotaFieldConfigs = new ArrayList<>();

        quota(reportQuotaFieldConfigs);

        rule.setReportQuotaFieldConfigs(reportQuotaFieldConfigs);

        System.out.println(JacksonUtil.toJSon(rule));
    }

    private void quota(List<ReportQuotaFieldConfig> reportQuotaFieldConfigs) {

    }


}
