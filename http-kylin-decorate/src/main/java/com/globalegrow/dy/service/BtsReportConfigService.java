package com.globalegrow.dy.service;

import com.globalegrow.dy.dto.BtsReportFieldConfigDto;
import com.globalegrow.dy.dto.BtsReportParameterDto;
import com.globalegrow.dy.dto.FieldConfigParameterDto;
import com.globalegrow.dy.model.BtsReportKylinConfig;

import java.util.List;

public interface BtsReportConfigService {

    /**
     * 根据实验查找 kylin 相关配置
     * @param planId
     * @return
     */
    BtsReportKylinConfig getConfigByBtsPlanId(Long planId);

    /**
     * 查询指定实验报表配置
     * @param planId 实验id
     * @param productLineCode 产品线
     * @param queryType 查询类型
     * @return
     */
    BtsReportKylinConfig getBtsReportKylinConfig(Long planId, String productLineCode, String queryType);

    /**
     * 现根据产品线及实验code查询，没有则根据实验 is 产品线查询
     * @param btsReportParameterDto
     * @return
     */
    BtsReportKylinConfig configMixedQuery(BtsReportParameterDto btsReportParameterDto);

    /**
     * 现根据产品线及实验code查询，没有则根据实验 is 产品线查询
     * @param btsReportParameterDto
     * @return
     */
    List<BtsReportFieldConfigDto> btsReportFieldConfigMixedQuery(FieldConfigParameterDto btsReportParameterDto);

    /**
     * 报表字段名配置
     * @param planId
     * @param produceLineCode
     * @return
     */
    List<BtsReportFieldConfigDto> btsReportFieldConfig(Long planId, String produceLineCode);

    void removeBtsReportKylinConfig(Long planId, String productLineCode, String queryType);

    void removeBtsReportFieldConfig(Long planId, String produceLineCode);
}
