package com.globalegrow.dy.service.impl;

import com.globalegrow.dy.dto.BtsReportFieldConfigDto;
import com.globalegrow.dy.mapper.BtsReportFieldConfigMapper;
import com.globalegrow.dy.mapper.BtsReportKylinConfigMapper;
import com.globalegrow.dy.model.BtsReportFieldConfig;
import com.globalegrow.dy.model.BtsReportFieldConfigExample;
import com.globalegrow.dy.model.BtsReportKylinConfig;
import com.globalegrow.dy.model.BtsReportKylinConfigExample;
import com.globalegrow.dy.service.BtsReportConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@CacheConfig(cacheNames = "bts_report_config_string")
public class BtsReportConfigServiceImpl implements BtsReportConfigService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BtsReportKylinConfigMapper btsReportKylinConfigMapper;

    @Autowired
    private BtsReportFieldConfigMapper btsReportFieldConfigMapper;

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

    /**
     * 查询指定实验报表配置
     *
     * @param planId          实验id
     * @param productLineCode 产品线
     * @param queryType       查询类型
     * @return
     */
    @Override
    @Cacheable(value = "kylin_config_3", key = "T(String).valueOf(#planId) + #productLineCode + #queryType")
    public BtsReportKylinConfig getBtsReportKylinConfig(Long planId, String productLineCode, String queryType) {
        this.logger.debug("查询 bts 报表 kylin 配置");
        BtsReportKylinConfigExample example = new BtsReportKylinConfigExample();
        example.createCriteria().andBtsPlanIdEqualTo(planId).andBtsProductLineCodeEqualTo(productLineCode).andQueryTypeEqualTo(queryType);
        List<BtsReportKylinConfig> btsReportKylinConfigs = this.btsReportKylinConfigMapper.selectByExampleWithBLOBs(example);
        if (btsReportKylinConfigs.size() > 0) {
            return btsReportKylinConfigs.get(0);
        }
        return null;
    }

    /**
     * 报表字段名配置
     *
     * @param planId
     * @param productLineCode
     * @return
     */
    @Override
    @Cacheable(cacheNames = "field_config_3", key = "T(String).valueOf(#planId) + #productLineCode")
    public List<BtsReportFieldConfigDto> btsReportFieldConfig(Long planId, String productLineCode) {
        this.logger.debug("查询 bts 报表字段配置");
        BtsReportFieldConfigExample example = new BtsReportFieldConfigExample();
        example.createCriteria().andBtsPlanIdEqualTo(planId).andBtsProductLineCodeEqualTo(productLineCode);
        List<BtsReportFieldConfig> configs = this.btsReportFieldConfigMapper.selectByExample(example);
        if (!configs.isEmpty()) {
            List<BtsReportFieldConfigDto> fieldConfigDtos = new ArrayList<>();
            configs.forEach(c -> {
                fieldConfigDtos.add(new BtsReportFieldConfigDto(c.getBtsPlanId(), c.getReportFieldCode(), c.getReportFieldName()));
            });
            return fieldConfigDtos;
        }
        return Collections.emptyList();
    }

    @Override
    @CacheEvict(cacheNames = "kylin_config_3", allEntries = false, beforeInvocation = true, key = "T(String).valueOf(#planId) + #productLineCode + #queryType")
    public void removeBtsReportKylinConfig(Long planId, String productLineCode, String queryType) {

    }

    @Override
    @CacheEvict(cacheNames = "field_config_3", allEntries = false, beforeInvocation = true, key = "T(String).valueOf(#planId) + #productLineCode")
    public void removeBtsReportFieldConfig(Long planId, String productLineCode) {

    }
}
