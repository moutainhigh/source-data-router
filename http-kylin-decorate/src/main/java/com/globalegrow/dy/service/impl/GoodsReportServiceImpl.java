package com.globalegrow.dy.service.impl;

import com.globalegrow.dy.dto.GoodsReportParameterDto;
import com.globalegrow.dy.dto.ReportPageDto;
import com.globalegrow.dy.enums.QueryConditions;
import com.globalegrow.dy.mapper.DyReportKylinConfigMapper;
import com.globalegrow.dy.model.DyReportKylinConfig;
import com.globalegrow.dy.model.DyReportKylinConfigExample;
import com.globalegrow.dy.service.GoodsReportHandler;
import com.globalegrow.dy.service.GoodsReportService;
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

@Service
public class GoodsReportServiceImpl implements GoodsReportService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GoodsReportHandler goodsReportHandler;

    @Autowired
    private DyReportKylinConfigMapper dyReportKylinConfigMapper;

    private final static int LIMITSIZE = 50000;

    @Override
    @Cacheable(cacheNames = "goods_report_cache", key = "#goodsReportParameterDto.getCacheKey()", unless = "#result.data.size() == 0")
    public ReportPageDto goodsReport(GoodsReportParameterDto goodsReportParameterDto) {
        this.logger.debug("查询 列表页报表 kylin 配置");
        DyReportKylinConfigExample example = new DyReportKylinConfigExample();
        example.createCriteria().andReportIdEqualTo(goodsReportParameterDto.getReportId()).andWebsiteCodeEqualTo(goodsReportParameterDto.getWebsiteCode()).andQueryTypeEqualTo("query");
        List<DyReportKylinConfig> dyReportKylinConfigs = this.dyReportKylinConfigMapper.selectByExampleWithBLOBs(example);
        if (dyReportKylinConfigs.size() <= 0) {
            return null;
        }
        DyReportKylinConfig dyReportKylinConfig = dyReportKylinConfigs.get(0);
        ReportPageDto mapReportPageDto = new ReportPageDto();
        if (goodsReportParameterDto.getPageSize() != null) {
            if (goodsReportParameterDto.getStartPage() <= 0 || mapReportPageDto.getPageSize() <= 0) {
                mapReportPageDto.setCurrentPage(1);
                mapReportPageDto.setPageSize(10);
            } else {
                mapReportPageDto.setCurrentPage(goodsReportParameterDto.getStartPage());
                mapReportPageDto.setPageSize(goodsReportParameterDto.getPageSize() > LIMITSIZE ? LIMITSIZE : goodsReportParameterDto.getPageSize());
            }
        }

        if (dyReportKylinConfig != null) {
            this.logger.debug("list page report config info: {}", dyReportKylinConfig);
            String sourceSql = dyReportKylinConfig.getKylinQuerySql();
            Map<String, Object> valuesMap = new HashMap();
            this.logger.debug("处理 where 条件");
            valuesMap.put(QueryConditions.whereFields.name(), this.whereFields(goodsReportParameterDto, dyReportKylinConfig));
            this.logger.debug("处理分组");
            valuesMap.put(QueryConditions.groupByFields.name(), this.groupFields(goodsReportParameterDto, dyReportKylinConfig));
            this.logger.debug("处理排序");
            valuesMap.put(QueryConditions.orderByFields.name(), this.orderByFields(goodsReportParameterDto, dyReportKylinConfig));

            StringSubstitutor sub = new StringSubstitutor(valuesMap);
            String resolvedString = sub.replace(sourceSql);
            this.logger.debug("请求 kylin 服务端, url: {}, sql: {}", dyReportKylinConfig.getKylinQueryAdress(), resolvedString);
            Map<String, Object> postParameters = new HashMap<>();
            postParameters.put("project", dyReportKylinConfig.getKylinProjectName());
            postParameters.put("sql", resolvedString);
            if (goodsReportParameterDto.getStartPage() <= 0 || mapReportPageDto.getPageSize() <= 0) {
                postParameters.put("limit", 10);
                postParameters.put("offset", 0);
            } else {
                this.logger.debug("分页请求");
                postParameters.put("limit", mapReportPageDto.getPageSize());
                postParameters.put("offset", (mapReportPageDto.getCurrentPage() - 1) * mapReportPageDto.getPageSize());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json;charset=UTF-8");
            headers.add("Authorization", dyReportKylinConfig.getKylinUserNamePassword());
            HttpEntity<Map<String, Object>> params = new HttpEntity<>(postParameters, headers);
            Map<String, Object> result = null;
            result = this.restTemplate.postForObject(dyReportKylinConfig.getKylinQueryAdress(), params, Map.class);
            List<Map<String, Object>> columnMetas = (List<Map<String, Object>>) result.get("columnMetas");
            List<List<Object>> data = (List<List<Object>>) result.get("results");
            data.forEach(report -> {
                Map<String, Object> reportData = new HashMap<>();
                for (int i = 0; i < report.size(); i++) {
                    String label = String.valueOf(columnMetas.get(i).get("label")).toLowerCase();
                    reportData.put(label, report.get(i));
                }
                mapReportPageDto.getData().add(reportData);
            });
            goodsReportParameterDto.setStartPage(-1);
            ReportPageDto reportPageDto = goodsReportHandler.reportCount(goodsReportParameterDto, dyReportKylinConfig, resolvedString, mapReportPageDto);
            mapReportPageDto.setTotalCount(reportPageDto.getTotalCount());
            mapReportPageDto.setTotalPage(reportPageDto.getTotalPage());

//            responseRet.setData(mapReportPageDto);
            return mapReportPageDto;
        }
        return mapReportPageDto;
    }

    /**
     * 分组条件处理
     *
     * @param goodsReportParameterDto
     * @param dyReportKylinConfig
     * @return
     */
    private String groupFields(GoodsReportParameterDto goodsReportParameterDto, DyReportKylinConfig dyReportKylinConfig) {
        if (goodsReportParameterDto.getGroupByFields() != null && goodsReportParameterDto.getGroupByFields().size() > 0) {
            List<String> groups = goodsReportParameterDto.getGroupByFields();
            this.logger.debug("根据传入分组条件设置");
            StringBuilder stringBuilder = new StringBuilder();
            if (groups.size() >= 0) {
                stringBuilder.append("group by ");
                for (int i = 0; i < groups.size(); i++) {
                    if (i == 0) {
                        stringBuilder.append(groups.get(i));
                    } else {
                        stringBuilder.append("," + groups.get(i));
                    }
                }
            }
            return stringBuilder.toString();
        } else {
            return "";
        }
    }

    /**
     * order by 处理
     *
     * @param goodsReportParameterDto
     * @param dyReportKylinConfig
     * @return
     */
    private String orderByFields(GoodsReportParameterDto goodsReportParameterDto, DyReportKylinConfig dyReportKylinConfig) {
        StringBuilder orderBy = new StringBuilder();
        if (goodsReportParameterDto.getOrderFields() != null && goodsReportParameterDto.getOrderFields().size() > 0) {
            orderBy.append("order by ");
            int i = 0;
            Map<String, String> entrySet = goodsReportParameterDto.getOrderFields();
            for (String key : entrySet.keySet()) {
                entrySet.get(key);
                if (i == 0) {
                    orderBy.append(key + " " + entrySet.get(key));
                    i++;
                } else {
                    orderBy.append(", " + key + " " + entrySet.get(key));
                }
            }
        }
        return orderBy.toString();
    }

    /**
     * where 条件处理
     *
     * @param goodsReportParameterDto
     * @param dyReportKylinConfig
     * @return
     */
    private String whereFields(GoodsReportParameterDto goodsReportParameterDto, DyReportKylinConfig dyReportKylinConfig) {
        StringBuilder where = new StringBuilder();
        if (goodsReportParameterDto.getBetweenFields() != null && goodsReportParameterDto.getBetweenFields().size() > 0) {
            where.append("where ");
            this.logger.debug("处理 between and 条件");
            Map<String, Map<String, String>> between = goodsReportParameterDto.getBetweenFields();
            between.entrySet().forEach(entry -> {
                where.append(entry.getKey() + " between '" + entry.getValue().get("min") + "' and '" + entry.getValue().get("max") + "'");
            });
        }
        if (goodsReportParameterDto.getWhereFields() != null && goodsReportParameterDto.getWhereFields().size() > 0) {
            this.logger.debug("处理 where value = 条件");
            Map<String, String> whereCondition = goodsReportParameterDto.getWhereFields();

            whereCondition.entrySet().forEach(entry -> {
//                if ("day_start".equals(entry.getKey())) {
//                    this.logger.debug("between 日期类型处理");
//                    if (StringUtils.isNotEmpty(where.toString())) {
//                        where.append(" and " + entry.getKey() + "= date '" + entry.getValue() + "' ");
//                    } else {
//                        where.append(" " + entry.getKey() + "= date '" + entry.getValue() + "' ");
//                    }
//                } else {
                if (StringUtils.isNotEmpty(where.toString())) {
                    where.append(" and " + entry.getKey() + "= '" + entry.getValue() + "' ");
                } else {
                    where.append("where " + entry.getKey() + "= '" + entry.getValue() + "' ");
                }
//                }
            });

        }
        return where.toString();
    }

}
