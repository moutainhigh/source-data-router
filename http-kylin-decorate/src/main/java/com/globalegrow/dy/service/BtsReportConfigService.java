package com.globalegrow.dy.service;

import com.globalegrow.dy.model.BtsReportKylinConfig;

public interface BtsReportConfigService {

    /**
     * 根据实验查找 kylin 相关配置
     * @param planId
     * @return
     */
    BtsReportKylinConfig getConfigByBtsPlanId(Long planId);

}
