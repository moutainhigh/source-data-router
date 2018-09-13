package com.globalegrow.dy.service.impl;

import com.globalegrow.dy.dto.BtsReportParameterDto;
import com.globalegrow.dy.dto.KylinBtsReportDto;
import com.globalegrow.dy.dto.ReportPageDto;
import com.globalegrow.dy.enums.BtsQueryConditions;
import com.globalegrow.dy.model.BtsReportKylinConfig;
import com.globalegrow.dy.service.BtsReportConfigService;
import com.globalegrow.dy.service.BtsReportService;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
     *
     * @param btsReportParameterDto
     * @return
     */
    // /*, sync = true*//*condition = "#btsReportKylinConfig.getData().size() > 0",#result != null && */
    @Override
    @Cacheable(cacheNames = "bts_report_data_cache", key = "#btsReportParameterDto.getCacheKey()" ,unless = "#result.data.size() == 0"  )
    public ReportPageDto btsReport(BtsReportParameterDto btsReportParameterDto) {
        BtsReportKylinConfig btsReportKylinConfig = this.btsReportConfigService.getBtsReportKylinConfig(btsReportParameterDto.getPlanId(), btsReportParameterDto.getProductLineCode(), btsReportParameterDto.getType());
        if (btsReportKylinConfig != null) {
            return this.reportPageDto(btsReportKylinConfig, btsReportParameterDto);
        }
        this.logger.error("bts kylin 配置为空");
      return new ReportPageDto();
    }

    @Override
    @Cacheable(cacheNames = "bts_report_cache_1", key = "#btsReportParameterDto.getCacheKey()",/*condition = "#btsReportKylinConfig.getData().size() > 0",#result != null && */ unless = "#result.data.size() > 0")
    public ReportPageDto btsReport(BtsReportKylinConfig btsReportKylinConfig, BtsReportParameterDto btsReportParameterDto) {
        return this.reportPageDto(btsReportKylinConfig, btsReportParameterDto);
    }

    private ReportPageDto reportPageDto(BtsReportKylinConfig btsReportKylinConfig, BtsReportParameterDto btsReportParameterDto) {
        Long start = System.currentTimeMillis();
        ReportPageDto mapReportPageDto = new ReportPageDto();
        mapReportPageDto.setCurrentPage(btsReportParameterDto.getStartPage());
        if (btsReportParameterDto.getPageSize() != null) {
            mapReportPageDto.setPageSize(btsReportParameterDto.getPageSize());
        }
        //BtsReportKylinConfig btsReportKylinConfig = this.btsReportConfigService.getConfigByBtsPlanId(btsReportParameterDto.getPlanId());
        if (btsReportKylinConfig != null) {
            this.logger.debug("bts report config info: {}", btsReportKylinConfig);
            String sourceSql = btsReportKylinConfig.getKylinQuerySql();
            Map valuesMap = new HashMap();
            this.logger.debug("处理分组");
            valuesMap.put(BtsQueryConditions.groupByFields.name(), this.groupFields(btsReportParameterDto, btsReportKylinConfig));
            this.logger.debug("处理 where 条件");
            valuesMap.put(BtsQueryConditions.whereFields.name(), this.whereFields(btsReportParameterDto, btsReportKylinConfig));

            this.logger.debug("处理排序字段");
            StringBuilder orderBy = new StringBuilder();
            if (btsReportParameterDto.getOrderFields() != null && btsReportParameterDto.getOrderFields().size() > 0) {
                orderBy.append(" order by ");
                int i = 0;
                btsReportParameterDto.getOrderFields().entrySet().forEach(entry -> {
                    if (i == 0) {
                        orderBy.append(" " + entry.getKey() + " " + entry.getValue());
                    } else {
                        orderBy.append(", " + entry.getKey() + " " + entry.getValue());
                    }
                });

            }
            // 默认无排序
            /*else {
                orderBy.append(" day_start desc ");
            }*/

            valuesMap.put(BtsQueryConditions.orderByFields.name(), orderBy.toString());

            this.logger.debug("组装 sql");
            StringSubstitutor sub = new StringSubstitutor(valuesMap);
            String resolvedString = sub.replace(sourceSql);
            this.logger.debug("请求 kylin 服务端, url: {}, sql: {}", btsReportKylinConfig.getKylinQueryAdress(), resolvedString);
            Map<String, Object> postParameters = new HashMap<>();
            postParameters.put("project", btsReportKylinConfig.getKylinProjectName());
            postParameters.put("sql", resolvedString);
            if (btsReportParameterDto.getStartPage() == 0) {
                this.logger.debug("不分页请求");
            } else {
                this.logger.debug("分页请求");
                postParameters.put("limit", btsReportParameterDto.getPageSize());
                postParameters.put("offset", (btsReportParameterDto.getStartPage() - 1) * btsReportParameterDto.getPageSize());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json;charset=UTF-8");
            headers.add("Authorization", btsReportKylinConfig.getKylinUserNamePassword());
            HttpEntity<Map<String, Object>> params = new HttpEntity<>(postParameters, headers);
            Map<String, Object> result = this.restTemplate.postForObject(btsReportKylinConfig.getKylinQueryAdress(), params, Map.class);
            //this.logger.debug("报表返回结果: {}", result);
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
                this.logger.debug("请求总条数");
                String countSql = "select count(*) from (" + resolvedString + ")";
                this.logger.debug("请求数据总数 sql: {}", countSql);
                Map<String, Object> postParametersCount = new HashMap<>();
                postParametersCount.put("project", btsReportKylinConfig.getKylinProjectName());
                postParametersCount.put("sql", countSql);
                postParametersCount.put("limit", 50000);
                postParametersCount.put("offset", 0);
                HttpEntity<Map<String, Object>> countParams = new HttpEntity<>(postParametersCount, headers);
                Map<String, Object> countResult = this.restTemplate.postForObject(btsReportKylinConfig.getKylinQueryAdress(), countParams, Map.class);
                //this.logger.debug("count 请求返回结果: {}", countResult);
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
     * 分组条件处理
     * BTS_RG_CART_REPORT,BTS_ZAFUL_RECOMMEND_REPORT，两张表的字段需特殊处理
     *
     * @param btsReportParameterDto
     * @param btsReportKylinConfig
     * @return
     */
    private String groupFields(BtsReportParameterDto btsReportParameterDto, BtsReportKylinConfig btsReportKylinConfig) {
        if (btsReportParameterDto.getGroupByFields() != null && btsReportParameterDto.getGroupByFields().size() > 0) {
            List<String> groups = btsReportParameterDto.getGroupByFields();
            this.logger.debug("根据传入分组条件设置");
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
            this.logger.debug("默认分组");
            if (btsReportKylinConfig.getKylinQuerySql().contains("BTS_RG_CART_REPORT") || btsReportKylinConfig.getKylinQuerySql().contains("BTS_ZAFUL_RECOMMEND_REPORT")) {
                return " day_start,bts_plan_id,bts_version_id,bts_bucket_id ";
            }
            return " day_start,bts_planid,bts_versionid,bts_bucketid ";
        }
    }

    /**
     * 部分报表 bts 字段名称不同，此处做一下兼容
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
     * where 条件处理
     *
     * @param btsReportParameterDto
     * @param btsReportKylinConfig
     * @return
     */
    private String whereFields(BtsReportParameterDto btsReportParameterDto, BtsReportKylinConfig btsReportKylinConfig) {
        StringBuilder where = new StringBuilder();
        if (btsReportParameterDto.getBetweenFields() != null && btsReportParameterDto.getBetweenFields().size() > 0) {
            this.logger.debug("处理 between and 条件");
            Map<String, Map<String, String>> between = btsReportParameterDto.getBetweenFields();
            between.entrySet().forEach(entry -> {
                where.append(entry.getKey() + " between '" + entry.getValue().get("min") + "' and '" + entry.getValue().get("max") + "'");
            });
        }
        if (btsReportParameterDto.getWhereFields() != null && btsReportParameterDto.getWhereFields().size() > 0) {
            this.logger.debug("处理 where value = 条件");
            Map<String, String> whereCondition = btsReportParameterDto.getWhereFields();

            whereCondition.entrySet().forEach(entry -> {
                if ("day_start".equals(entry.getKey())) {
                    this.logger.debug("between 日期类型处理");
                    if (StringUtils.isNotEmpty(where.toString())) {
                        where.append(" and " + entry.getKey() + "= date '" + entry.getValue() + "' ");
                    } else {
                        where.append(" " + entry.getKey() + "= date '" + entry.getValue() + "' ");
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


}
