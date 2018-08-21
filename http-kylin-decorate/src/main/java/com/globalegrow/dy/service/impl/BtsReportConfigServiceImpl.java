package com.globalegrow.dy.service.impl;

import com.globalegrow.dy.mapper.BtsReportKylinConfigMapper;
import com.globalegrow.dy.model.BtsReportKylinConfig;
import com.globalegrow.dy.model.BtsReportKylinConfigExample;
import com.globalegrow.dy.service.BtsReportConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BtsReportConfigServiceImpl implements BtsReportConfigService {

    @Autowired
    private BtsReportKylinConfigMapper btsReportKylinConfigMapper;

    /**
     * 根据实验查找 kylin 相关配置
     *
     * @param planId
     * @return
     */
    @Override
    public BtsReportKylinConfig getConfigByBtsPlanId(Long planId) {
        BtsReportKylinConfigExample example = new BtsReportKylinConfigExample();
        example.createCriteria().andBtsPlanIdEqualTo(planId);
        List<BtsReportKylinConfig> btsReportKylinConfigs = this.btsReportKylinConfigMapper.selectByExampleWithBLOBs(example);
        if (btsReportKylinConfigs.size() > 0) {
            return btsReportKylinConfigs.get(0);
        }
        return null;
    }
}
