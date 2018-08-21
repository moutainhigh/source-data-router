package com.globalegrow.dy.service;

import com.globalegrow.dy.model.BtsReportKylinConfig;

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
}
