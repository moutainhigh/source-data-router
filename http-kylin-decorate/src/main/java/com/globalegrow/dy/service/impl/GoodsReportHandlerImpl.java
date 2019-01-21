package com.globalegrow.dy.service.impl;

import com.globalegrow.dy.dto.GoodsReportParameterDto;
import com.globalegrow.dy.dto.ReportPageDto;
import com.globalegrow.dy.mapper.DyReportKylinConfigMapper;
import com.globalegrow.dy.model.DyReportKylinConfig;
import com.globalegrow.dy.service.GoodsReportHandler;
import org.apache.commons.lang3.StringUtils;
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
public class GoodsReportHandlerImpl implements GoodsReportHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DyReportKylinConfigMapper dyReportKylinConfigMapper;



    @Override
    @Cacheable(cacheNames = "goods_report_count_cache", key = "#goodsReportParameterDto.getCacheKey()")
    public ReportPageDto reportCount(GoodsReportParameterDto goodsReportParameterDto, DyReportKylinConfig dyReportKylinConfig, String resolvedString, ReportPageDto mapReportPageDto) {
        ReportPageDto reportPageDto = new ReportPageDto();
        this.logger.debug("请求总条数");
        String countSql = "select count(*) from (" + resolvedString + ")";
        this.logger.debug("请求数据总数 sql: {}", countSql);
        Map<String, Object> postParametersCount = new HashMap<>();
        postParametersCount.put("project", dyReportKylinConfig.getKylinProjectName());
        postParametersCount.put("sql", countSql);
//        postParametersCount.put("limit", 1);
//        postParametersCount.put("offset", 0);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("Authorization", dyReportKylinConfig.getKylinUserNamePassword());
        HttpEntity<Map<String, Object>> countParams = new HttpEntity<>(postParametersCount, headers);
        Map<String, Object> countResult = this.restTemplate.postForObject(dyReportKylinConfig.getKylinQueryAdress(), countParams, Map.class);
        if (countResult != null) {
            List<List<Object>> countResultValues = (List<List<Object>>) countResult.get("results");
            this.logger.debug("count result: {}", countResultValues);
            String count = String.valueOf(countResultValues.get(0).get(0));
            if (StringUtils.isNotBlank(count)) {
                Long  totalCount= Long.valueOf(count);
                this.logger.debug("total count: {}", totalCount);
                Long totalPage= totalCount % mapReportPageDto.getPageSize() == 0 ? totalCount / mapReportPageDto.getPageSize() : (totalCount / mapReportPageDto.getPageSize() + 1);
                reportPageDto.setTotalPage(totalPage);
                reportPageDto.setTotalCount(totalCount);
            }
        }
        return reportPageDto;
    }
}
