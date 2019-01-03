package com.globalegrow.dy.service.impl;

import com.globalegrow.dy.dto.ListPageReportParameterDto;
import com.globalegrow.dy.dto.ReportPageDto;
import com.globalegrow.dy.enums.ListPageQueryConditions;
import com.globalegrow.dy.mapper.DyReportKylinConfigMapper;
import com.globalegrow.dy.model.DyReportKylinConfig;
import com.globalegrow.dy.model.DyReportKylinConfigExample;
import com.globalegrow.dy.service.ListPageReportHandler;
import com.globalegrow.dy.service.ListPageReportService;
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
public class ListPageReportServiceImpl implements ListPageReportService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ListPageReportHandler listPageReportHandler;

    @Autowired
    private DyReportKylinConfigMapper dyReportKylinConfigMapper;

    private final static  int LIMITSIZE = 50000;

    @Override
    @Cacheable(cacheNames = "listpage_report_cache", key = "#listPageReportParameterDto.getCacheKey()", unless = "#result.data.size() == 0")
    public ReportPageDto listPageReport(ListPageReportParameterDto listPageReportParameterDto) {
//        ResponseDTO<ReportPageDto> responseRet = new ResponseDTO<>();
        this.logger.debug("查询 列表页报表 kylin 配置");
        DyReportKylinConfigExample example = new DyReportKylinConfigExample();
        example.createCriteria().andReportIdEqualTo(listPageReportParameterDto.getReportId()).andWebsiteCodeEqualTo(listPageReportParameterDto.getWebsiteCode()).andQueryTypeEqualTo("query");
        List<DyReportKylinConfig> dyReportKylinConfigs = this.dyReportKylinConfigMapper.selectByExampleWithBLOBs(example);
        if (dyReportKylinConfigs.size() <= 0) {
            return null;
        }
        DyReportKylinConfig dyReportKylinConfig = dyReportKylinConfigs.get(0);
        ReportPageDto mapReportPageDto = new ReportPageDto();
        mapReportPageDto.setCurrentPage(listPageReportParameterDto.getStartPage());
        if (listPageReportParameterDto.getPageSize() != null) {
            mapReportPageDto.setPageSize(listPageReportParameterDto.getPageSize()>LIMITSIZE ? LIMITSIZE:listPageReportParameterDto.getPageSize());
        }
        if(listPageReportParameterDto.getStartPage() != null){
            mapReportPageDto.setCurrentPage(listPageReportParameterDto.getStartPage());
        }

        if (dyReportKylinConfig != null) {
            this.logger.debug("list page report config info: {}", dyReportKylinConfig);
            String sourceSql = dyReportKylinConfig.getKylinQuerySql();
            Map<String, Object> valuesMap = new HashMap();
            valuesMap.put(ListPageQueryConditions.glbPlf.name(), "\'"+listPageReportParameterDto.getGlbPlf()+"\'");
            valuesMap.put(ListPageQueryConditions.hourStart.name(), "\'"+listPageReportParameterDto.getHourStart()+"\'");
            valuesMap.put(ListPageQueryConditions.hourEnd.name(), "\'"+listPageReportParameterDto.getHourEnd()+"\'");
            StringSubstitutor sub = new StringSubstitutor(valuesMap);
            String resolvedString = sub.replace(sourceSql);
            this.logger.debug("请求 kylin 服务端, url: {}, sql: {}", dyReportKylinConfig.getKylinQueryAdress(), resolvedString);
            Map<String, Object> postParameters = new HashMap<>();
            postParameters.put("project", dyReportKylinConfig.getKylinProjectName());
            postParameters.put("sql", resolvedString);
            if (listPageReportParameterDto.getStartPage() == 0) {
                this.logger.debug("不分页请求");
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
//            try {
                result = this.restTemplate.postForObject(dyReportKylinConfig.getKylinQueryAdress(), params, Map.class);
//            } catch (RestClientException e) {
//                this.logger.error("kylin 查询异常", e);
//                responseRet.setCode("500");
//                responseRet.setMessage("kylin 查询异常:" + e);
//                return responseRet;
//            }
            //this.logger.debug("报表返回结果: {}", result);
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
            if (listPageReportParameterDto.getStartPage() > 0) {
                listPageReportParameterDto.setStartPage(-1);
                ReportPageDto reportPageDto = listPageReportHandler.reportCount(listPageReportParameterDto,dyReportKylinConfig,  resolvedString,  mapReportPageDto);
                mapReportPageDto.setTotalCount(reportPageDto.getTotalCount());
                mapReportPageDto.setTotalPage(reportPageDto.getTotalPage());
            }
//            responseRet.setData(mapReportPageDto);
            return mapReportPageDto;
        }
        return mapReportPageDto;
    }

}
