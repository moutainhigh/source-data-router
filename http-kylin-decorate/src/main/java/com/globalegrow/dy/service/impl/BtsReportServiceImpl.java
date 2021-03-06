package com.globalegrow.dy.service.impl;

import com.globalegrow.dy.dto.BtsReportParameterDto;
import com.globalegrow.dy.dto.ReportPageDto;
import com.globalegrow.dy.enums.QueryConditions;
import com.globalegrow.dy.enums.ReportServerType;
import com.globalegrow.dy.model.BtsReportKylinConfig;
import com.globalegrow.dy.service.BtsReportConfigService;
import com.globalegrow.dy.service.BtsReportService;
import com.globalegrow.util.JacksonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BtsReportServiceImpl implements BtsReportService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BtsReportConfigService btsReportConfigService;

    @Autowired
    private RestTemplate restTemplate;
    /**
     * bts_plan_id,bts_version_id,bts_bucket_id
     */
    static final Map<String, String> BTS_FIELDS = new HashMap() {
        private static final long serialVersionUID = -2564539190025132306L;

        {
            put("bts_plan_id", "bts_planid");
            put("bts_version_id", "bts_versionid");
            put("bts_bucket_id", "bts_bucketid");
            put("bts_policy", "bts_policy");
            put("bts_plancode", "bts_plancode");
        }
    };

    static final Map<String, String> BTS_FIELDS_FLIP = new HashMap() {
        private static final long serialVersionUID = 5448417541998224281L;

        {
            put("bts_planid", "bts_plan_id");
            put("bts_versionid", "bts_version_id");
            put("bts_bucketid", "bts_bucket_id");
            put("bts_policy", "bts_policy");
            put("bts_plancode", "bts_plancode");
        }
    };

    /**
     * @param btsReportParameterDto
     * @return
     */
    // /*, sync = true*//*condition = "#btsReportKylinConfig.getData().size() > 0",#result != null && */
    @Override
    //@Cacheable(cacheNames = "bts_report_data_cache", key = "#btsReportParameterDto.getCacheKey()", unless = "#result.data.size() == 0")
    public ReportPageDto btsReport(BtsReportParameterDto btsReportParameterDto) throws Exception {
        BtsReportKylinConfig btsReportKylinConfig = this.btsReportConfigService.configMixedQuery(btsReportParameterDto);
        if (btsReportKylinConfig != null) {
            return this.reportPageDto(btsReportKylinConfig, btsReportParameterDto);
        }
        this.logger.error("bts kylin ????????????");
        return new ReportPageDto();
    }

    @Override
    //@Cacheable(cacheNames = "bts_report_cache_2", key = "#btsReportParameterDto.getCacheKey()",/*condition = "#btsReportKylinConfig.getData().size() > 0",#result != null && */ unless = "#result.data.size() == 0")
    public ReportPageDto btsReport(BtsReportKylinConfig btsReportKylinConfig, BtsReportParameterDto btsReportParameterDto) throws Exception {
        return this.reportPageDto(btsReportKylinConfig, btsReportParameterDto);
    }

    private ReportPageDto reportPageDto(BtsReportKylinConfig btsReportKylinConfig, BtsReportParameterDto btsReportParameterDto) throws Exception {
        Long start = System.currentTimeMillis();
        ReportPageDto mapReportPageDto = new ReportPageDto();
        mapReportPageDto.setCurrentPage(btsReportParameterDto.getStartPage());
        if (btsReportParameterDto.getPageSize() != null) {
            mapReportPageDto.setPageSize(btsReportParameterDto.getPageSize());
        }
        //BtsReportKylinConfig btsReportKylinConfig = this.btsReportConfigService.getConfigByBtsPlanId(btsReportParameterDto.getPlanId());
        if (btsReportKylinConfig != null) {
            if (ReportServerType.EMP.name().equals(btsReportKylinConfig.getServerType())) {
                this.logger.info("EMP ??????????????????");
                mapReportPageDto.setData(this.empReport(btsReportParameterDto, btsReportKylinConfig));
                return mapReportPageDto;
            }
            this.logger.debug("bts report config info: {}", btsReportKylinConfig);
             String sourceSql = btsReportKylinConfig.getKylinQuerySql();
            Map valuesMap = new HashMap();
            this.logger.debug("????????????");
            if (btsReportParameterDto.getGroupByFields() != null && btsReportParameterDto.getGroupByFields().size() > 0) {
                valuesMap.put(QueryConditions.groupByFields.name(), this.groupFields(btsReportParameterDto, btsReportKylinConfig));
            }
            this.logger.debug("?????? where ??????");
            valuesMap.put(QueryConditions.whereFields.name(), this.whereFields(btsReportParameterDto, btsReportKylinConfig));

            if (StringUtils.isNotBlank(btsReportParameterDto.getDateType())) {
                this.logger.debug("??????dateType");
                valuesMap.put(QueryConditions.dateType.name(),btsReportParameterDto.getDateType());
            }
            this.logger.debug("??????????????????");
            StringBuilder orderBy = new StringBuilder();
            if (btsReportParameterDto.getOrderFields() != null && btsReportParameterDto.getOrderFields().size() > 0) {
                orderBy.append(" order by ");
                int i = 0;
                btsReportParameterDto.getOrderFields().entrySet().forEach(entry -> {
                    if (i == 0) {
                        orderBy.append(" " + this.compatibilityBtsFields(entry.getKey(), btsReportKylinConfig) + " " + entry.getValue());
                    } else {
                        orderBy.append(", " + this.compatibilityBtsFields(entry.getKey(), btsReportKylinConfig) + " " + entry.getValue());
                    }
                });

            }
            // ???????????????
            /*else {
                orderBy.append(" day_start desc ");
            }*/

            valuesMap.put(QueryConditions.orderByFields.name(), orderBy.toString());


            this.logger.debug("?????? sql");
            StringSubstitutor sub = new StringSubstitutor(valuesMap);
            String resolvedString = sub.replace(sourceSql);
            this.logger.debug("?????? kylin ?????????, url: {}, sql: {}", btsReportKylinConfig.getKylinQueryAdress(), resolvedString);
            Map<String, Object> postParameters = new HashMap<>();
            postParameters.put("project", btsReportKylinConfig.getKylinProjectName());
            postParameters.put("sql", resolvedString);
            if (btsReportParameterDto.getStartPage() == 0) {
                this.logger.debug("???????????????");
            } else {
                this.logger.debug("????????????");
                postParameters.put("limit", btsReportParameterDto.getPageSize());
                postParameters.put("offset", (btsReportParameterDto.getStartPage() - 1) * btsReportParameterDto.getPageSize());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json;charset=UTF-8");
            headers.add("Authorization", btsReportKylinConfig.getKylinUserNamePassword());
            HttpEntity<Map<String, Object>> params = new HttpEntity<>(postParameters, headers);
            Map<String, Object> result = null;
            try {
                result = this.restTemplate.postForObject(btsReportKylinConfig.getKylinQueryAdress(), params, Map.class);
            } catch (RestClientException e) {
                this.logger.error("kylin ????????????", e);
                return mapReportPageDto;
            }
            //this.logger.debug("??????????????????: {}", result);
            List<Map<String, Object>> columnMetas = (List<Map<String, Object>>) result.get("columnMetas");
            List<List<Object>> data = (List<List<Object>>) result.get("results");
            data.forEach(report -> {
                Map<String, Object> reportData = new HashMap<>();
                for (int i = 0; i < report.size(); i++) {
                    String label = String.valueOf(columnMetas.get(i).get("label"));
                    if (label.startsWith("BTS_")) {
                        if (btsReportKylinConfig.getKylinQuerySql().contains("BTS_RG_CART_REPORT") || btsReportKylinConfig.getKylinQuerySql().contains("BTS_ZAFUL_RECOMMEND_REPORT")) {
                            label = BTS_FIELDS.get(label.toLowerCase()).toUpperCase();
                        }
                    }
                    reportData.put(label, report.get(i));
                }
                mapReportPageDto.getData().add(reportData);
            });
            if (btsReportParameterDto.getStartPage() > 0) {
                this.logger.debug("???????????????");
                String countSql = "select count(*) from (" + resolvedString + ")";
                this.logger.debug("?????????????????? sql: {}", countSql);
                Map<String, Object> postParametersCount = new HashMap<>();
                postParametersCount.put("project", btsReportKylinConfig.getKylinProjectName());
                postParametersCount.put("sql", countSql);
                postParametersCount.put("limit", 50000);
                postParametersCount.put("offset", 0);
                HttpEntity<Map<String, Object>> countParams = new HttpEntity<>(postParametersCount, headers);
                Map<String, Object> countResult = this.restTemplate.postForObject(btsReportKylinConfig.getKylinQueryAdress(), countParams, Map.class);
                //this.logger.debug("count ??????????????????: {}", countResult);
                if (countResult != null) {
                    List<List<Object>> countResultValues = (List<List<Object>>) countResult.get("results");
                    this.logger.debug("count result: {}", countResultValues);
                    String count = String.valueOf(countResultValues.get(0).get(0));
                    if (StringUtils.isNotBlank(count)) {
                        Long totalCount = Long.valueOf(count);
                        this.logger.debug("total count: {}", totalCount);
                        mapReportPageDto.setTotalCount(totalCount);
                        mapReportPageDto.setTotalPage(totalCount % mapReportPageDto.getPageSize() == 0 ? totalCount / mapReportPageDto.getPageSize() : (totalCount / mapReportPageDto.getPageSize() + 1));
                    }
                }
            }

        }
        this.logger.info("query from kylin costs:{}", System.currentTimeMillis() - start);
        return mapReportPageDto;
    }

    /**
     * ??????????????????
     * BTS_RG_CART_REPORT,BTS_ZAFUL_RECOMMEND_REPORT????????????????????????????????????
     *
     * @param btsReportParameterDto
     * @param btsReportKylinConfig
     * @return
     */
    private String groupFields(BtsReportParameterDto btsReportParameterDto, BtsReportKylinConfig btsReportKylinConfig) {
        if (btsReportParameterDto.getGroupByFields() != null && btsReportParameterDto.getGroupByFields().size() > 0) {
            List<String> groups = btsReportParameterDto.getGroupByFields();
            this.logger.debug("??????????????????????????????");
            StringBuilder stringBuilder = new StringBuilder();
            if (groups.size() == 1) {
                stringBuilder.append(this.compatibilityBtsFields(groups.get(0), btsReportKylinConfig));
            } else {
                for (int i = 0; i < groups.size(); i++) {
                    if (i == 0) {
                        stringBuilder.append(this.compatibilityBtsFields(groups.get(i), btsReportKylinConfig));
                    } else {
                        stringBuilder.append("," + this.compatibilityBtsFields(groups.get(i), btsReportKylinConfig));
                    }
                }
            }
            return stringBuilder.toString();
        } else {
            this.logger.debug("????????????");
            if (btsReportKylinConfig.getKylinQuerySql().contains("BTS_RG_CART_REPORT") || btsReportKylinConfig.getKylinQuerySql().contains("BTS_ZAFUL_RECOMMEND_REPORT")) {
                return " day_start,bts_plan_id,bts_version_id,bts_bucket_id ";
            }
            return " day_start,bts_planid,bts_versionid,bts_bucketid ";
        }
    }

    /**
     * ???????????? bts ??????????????????????????????????????????
     *
     * @param condition
     * @param btsReportKylinConfig
     * @return
     */
    private String compatibilityBtsFields(String condition, BtsReportKylinConfig btsReportKylinConfig) {
        if (condition.startsWith("bts_")) {
            if (btsReportKylinConfig.getKylinQuerySql().contains("BTS_RG_CART_REPORT") || btsReportKylinConfig.getKylinQuerySql().contains("BTS_ZAFUL_RECOMMEND_REPORT")) {
                return BTS_FIELDS_FLIP.get(condition);
            }
        }
        return condition;
    }

    /**
     * where ????????????
     *
     * @param btsReportParameterDto
     * @param btsReportKylinConfig
     * @return
     */
    private String whereFields(BtsReportParameterDto btsReportParameterDto, BtsReportKylinConfig btsReportKylinConfig) {
        StringBuilder where = new StringBuilder();
        if (btsReportParameterDto.getBetweenFields() != null && btsReportParameterDto.getBetweenFields().size() > 0) {
            this.logger.debug("?????? between and ??????");
            Map<String, Map<String, String>> between = btsReportParameterDto.getBetweenFields();
            between.entrySet().forEach(entry -> {
                where.append(entry.getKey() + " between '" + entry.getValue().get("min") + "' and '" + entry.getValue().get("max") + "'");
            });
        }
        if (btsReportParameterDto.getWhereFields() != null && btsReportParameterDto.getWhereFields().size() > 0) {
            this.logger.debug("?????? where value = ??????");
            Map<String, String> whereCondition = btsReportParameterDto.getWhereFields();

            whereCondition.entrySet().forEach(entry -> {
                if ("day_start".equals(entry.getKey())) {
                    this.logger.debug("between ??????????????????");
                    if (StringUtils.isNotEmpty(where.toString())) {
                        where.append(" and " + entry.getKey() + "= date '" + entry.getValue() + "' ");
                    } else {
                        where.append(" " + entry.getKey() + "= date '" + entry.getValue() + "' ");
                    }
                } else if ("MINUTE_START".equals(entry.getKey())) {
                    //TIMESTAMP
                    this.logger.debug("between ??????????????????");
                    if (StringUtils.isNotEmpty(where.toString())) {
                        where.append(" and " + entry.getKey() + "= TIMESTAMP '" + entry.getValue() + "' ");
                    } else {
                        where.append(" " + entry.getKey() + "= TIMESTAMP '" + entry.getValue() + "' ");
                    }
                } else {
                    if (StringUtils.isNotEmpty(where.toString())) {
                        where.append(" and " + this.compatibilityBtsFields(entry.getKey(), btsReportKylinConfig) + "= '" + entry.getValue() + "' ");
                    } else {
                        where.append(" " + this.compatibilityBtsFields(entry.getKey(), btsReportKylinConfig) + "= '" + entry.getValue() + "' ");
                    }
                }
            });

        }
        return where.toString();
    }

    /**
     * EMP ??????????????????
     *
     * @param btsReportParameterDto
     * @param btsReportKylinConfig
     */
    public List<Object> empReport(BtsReportParameterDto btsReportParameterDto, BtsReportKylinConfig btsReportKylinConfig) throws Exception {
        List<String> groupByFields = btsReportParameterDto.getGroupByFields();
        StringBuilder stringBuilder = new StringBuilder(btsReportKylinConfig.getKylinQueryAdress());
        /*Map<String, Object> getParameters = new HashMap<>();
        getParameters.put("module_name", "marketing_email");*/
        stringBuilder.append("&plan_id=" + btsReportParameterDto.getPlanId());
        Map<String, Map<String, String>> betweenFields = btsReportParameterDto.getBetweenFields();
        if (groupByFields == null || groupByFields.size() == 0 || (groupByFields.contains("bts_planid") && groupByFields.contains("bts_versionid")
                && groupByFields.contains("day_start") && betweenFields != null && betweenFields.size() > 0)) {
            stringBuilder.append("&data_flag=4");
        } else if (groupByFields == null || groupByFields.size() == 0 || (groupByFields.contains("bts_planid") && groupByFields.contains("bts_versionid") && groupByFields.contains("day_start"))) {
            stringBuilder.append("&data_flag=3");
        } else if (groupByFields.contains("bts_planid") && groupByFields.contains("bts_versionid") && !groupByFields.contains("day_start")) {
            stringBuilder.append("&data_flag=2");
        } else if (groupByFields.contains("bts_planid") && !groupByFields.contains("bts_versionid")) {
            stringBuilder.append("&data_flag=1");
        }
        if (betweenFields != null && betweenFields.size() > 0) {
            Map<String, String> dayBetween = betweenFields.get("day_start");
            if (dayBetween != null && dayBetween.size() > 0) {
                String day = dayBetween.get("min") + "_" + dayBetween.get("max");
                stringBuilder.append("&day=" + day);
            }
        }
        String o = this.restTemplate.getForObject(stringBuilder.toString(), String.class);
        System.out.println(o);
        Map<String, Object> result = JacksonUtil.readValue(o, Map.class);

        List<Map<String, String>> data = (List<Map<String, String>>) result.get("data");
        List<Object> outReportData = new ArrayList<>();
        try {
            if (data != null && data.size() > 0) {
                for (Map<String, String> reportData : data) {
                    Map<String, String> rowData = new HashMap<>();
                    reportData.entrySet().forEach(e -> {
                        if ("plan_id".equals(e.getKey())) {
                            rowData.put("BTS_PLANID", String.valueOf(e.getValue()));
                        } else if ("version_id".equals(e.getKey())) {
                            rowData.put("BTS_VERSIONID", String.valueOf(e.getValue()));
                        } else if ("day".equals(e.getKey())) {
                            rowData.put("DAY_START", e.getValue());
                        } else {
                            rowData.put("send_ok_count".equals(e.getKey()) ? "SPECIMEN" : "SUM_" + e.getKey().toUpperCase(), e.getValue());
                        }

                    });
                    // ???????????????
                    // ?????????
                    rowData.put("SUM_DELIVER_RATE", divPer(rowData.get("SPECIMEN"), rowData.get("SUM_TOTAL_COUNT")));
                    // ?????????
                    rowData.put("SUM_OPEN_RATE", divPer(rowData.get("SUM_OPEN_COUNT"), rowData.get("SPECIMEN")));
                    // ???????????????
                    rowData.put("SUM_TRANS_RATE", divPer(rowData.get("SUM_CLICK_COUNT"), rowData.get("SUM_OPEN_COUNT")));
                    // ???????????????
                    rowData.put("SUM_ORDER_TRANS_RATE", divPer(rowData.get("SUM_ORDER_USER"), rowData.get("SUM_CLICK_COUNT")));
                    // ?????????
                    rowData.put("SUM_ORDER_RATE", divPer(rowData.get("SUM_ORDER_USER"), rowData.get("SPECIMEN")));
                    // ???????????????
                    rowData.put("SUM_ORDER_USER_AVG", divLongFloat(rowData.get("SUM_ORDER_USER"), rowData.get("SUM_ORDER_MONEY")));
                    // ???????????????
                    rowData.put("SUM_PAYED_ORDER_RATE", divPer(rowData.get("SUM_PAYED_ORDER_NUMS"), rowData.get("SUM_ORDER_NUMS")));
                    // ???????????????
                    rowData.put("SUM_PAY_AMOUNT_RATE", divFloatFloatPer(rowData.get("SUM_PAYED_ORDER_MONEY"), rowData.get("SUM_ORDER_MONEY")));
                    // ???????????????
                    rowData.put("SUM_ORDER_USER_AVG_PRICE", divFloatLong(rowData.get("SUM_PAYED_ORDER_MONEY"), rowData.get("SUM_PAYED_USER")));
                    outReportData.add(rowData);
                }
            }
            // ????????????&????????????
            if ("query".equals(btsReportParameterDto.getType())) {
                List<Object> avgReport = new ArrayList<>();
                outReportData.stream().forEach(ro -> {
                    Map<String, String> m = (Map<String, String>) ro;
                    Map<String, String> avgRow = new HashMap<>();
                    handleEmpReportResult(avgReport, m, avgRow);
                });
                return avgReport;
            } else if ("all".equals(btsReportParameterDto.getType())) {
                List<Object> allReport = new ArrayList<>();
                outReportData.stream().forEach(ro -> {
                    Map<String, String> m = (Map<String, String>) ro;
                    Map<String, String> avgRow = new HashMap<>();
                    avgRow.putAll(m);
                    handleEmpReportResult(allReport, m, avgRow);
                });
                return allReport;
            }

        } catch (Exception e) {
            logger.error("?????? EMP ??????????????????:{}", o, e);
        }
        return outReportData;
    }

    private void handleEmpReportResult(List<Object> allReport, Map<String, String> m, Map<String, String> avgRow) {
        m.entrySet().forEach(e -> {
            if ("SPECIMEN".equals(e.getKey()) || e.getKey().contains("BTS") || "DAY_START".equals(e.getKey())) {
                avgRow.put(e.getKey(), e.getValue());
            } else if (e.getKey().endsWith("_RATE")) {
                avgRow.put(e.getKey().replace("SUM_", "AVG_"), e.getValue());
            } else {
                avgRow.put(e.getKey().replace("SUM_", "AVG_"), formatDivResult(Float.valueOf(e.getValue()) / Float.valueOf(m.get("SPECIMEN"))));
            }
        });
        allReport.add(avgRow);
    }

    private String divLongFloat(String top, String bottom) {
        try {
            if (Float.valueOf(bottom) > 0) {
                return formatDivResult(Long.valueOf(top) / Float.valueOf(bottom));
            }
        } catch (Exception e) {
            //
        }
        return "0";
    }

    private String divFloatLong(String top, String bottom) {
        try {
            if (Float.valueOf(bottom) > 0) {
                return formatDivResult(Float.valueOf(top) / Float.valueOf(bottom));
            }
        } catch (Exception e) {
            //
        }
        return "0";
    }

    private String divFloatFloatPer(String top, String bottom) {
        try {
            if (Float.valueOf(bottom) > 0) {
                return formatDivResult((Float.valueOf(top) / Float.valueOf(bottom)) * 100);
            }
        } catch (Exception e) {
            //
        }
        return "0";
    }

    private String divLongFloatPer(String top, String bottom) {
        if (Float.valueOf(bottom) > 0) {
            return formatDivResult((Long.valueOf(top) / Float.valueOf(bottom)) * 100);
        }
        return "0";
    }

    private String divFloatLongPer(String top, String bottom) {
        try {
            if (Float.valueOf(bottom) > 0) {
                return formatDivResult((Float.valueOf(top) / Float.valueOf(bottom)) * 100);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return "0";
    }

    private String divPer(String top, String bottom) {
        try {
            if (Float.valueOf(bottom) > 0) {
                return formatDivResult((Long.valueOf(top) / Float.valueOf(bottom)) * 100);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return "0";
    }

    private String formatDivResult(Object dresult) {
        if (dresult != null) {
            DecimalFormat decimalFormat = new DecimalFormat("0.000");
            return decimalFormat.format(dresult);
        }
        return "0";
    }

}
