package com.globalegrow.dy.service.impl;

import com.globalegrow.dy.dto.BtsReportFieldConfigDto;
import com.globalegrow.dy.dto.BtsReportParameterDto;
import com.globalegrow.dy.dto.FieldConfigParameterDto;
import com.globalegrow.dy.mapper.BtsReportFieldConfigMapper;
import com.globalegrow.dy.mapper.BtsReportKylinConfigMapper;
import com.globalegrow.dy.model.BtsReportFieldConfig;
import com.globalegrow.dy.model.BtsReportFieldConfigExample;
import com.globalegrow.dy.model.BtsReportKylinConfig;
import com.globalegrow.dy.model.BtsReportKylinConfigExample;
import com.globalegrow.dy.service.BtsReportConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
//@CacheConfig(cacheNames = "bts_report_config_string")
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
    //@Cacheable(value = "kylin_config_3", key = "T(String).valueOf(#planId) + #productLineCode + #queryType")
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
     * 现根据产品线及实验code查询，没有则根据实验 is 产品线查询
     *
     * @param btsReportParameterDto
     * @return
     */
    @Override
    public BtsReportKylinConfig configMixedQuery(BtsReportParameterDto btsReportParameterDto) {
        BtsReportKylinConfig btsReportKylinConfig = this.getBtsReportKylinConfig(btsReportParameterDto.getPlanId(), btsReportParameterDto.getProductLineCode(), btsReportParameterDto.getType());
        if (btsReportKylinConfig != null) {
            return btsReportKylinConfig;
        }
        if (btsReportKylinConfig == null && StringUtils.isNotEmpty(btsReportParameterDto.getPlanCode())) {
            this.logger.info("根据类型查询报表 kylin 配置");
            BtsReportKylinConfigExample example = new BtsReportKylinConfigExample();
            example.createCriteria().andBtsPlanCodeEqualTo(btsReportParameterDto.getPlanCode()).andBtsProductLineCodeEqualTo(btsReportParameterDto.getProductLineCode()).andQueryTypeEqualTo(btsReportParameterDto.getType());
            List<BtsReportKylinConfig> btsReportKylinConfigs = this.btsReportKylinConfigMapper.selectByExampleWithBLOBs(example);
            if (btsReportKylinConfigs.size() > 0) {
                return btsReportKylinConfigs.get(0);
            }
        }
        return null;
    }

    /**
     * 现根据产品线及实验code查询，没有则根据实验 is 产品线查询
     *
     * @param btsReportParameterDto
     * @return
     */
    @Override
    public List<BtsReportFieldConfigDto> btsReportFieldConfigMixedQuery(FieldConfigParameterDto btsReportParameterDto) {
        if (StringUtils.isNotEmpty(btsReportParameterDto.getPlanCode())) {
            BtsReportFieldConfigExample example = new BtsReportFieldConfigExample();
            example.createCriteria().andBtsProductLineCodeEqualTo(btsReportParameterDto.getProductLineCode()).andBtsPlanCodeEqualTo(btsReportParameterDto.getPlanCode());
            List<BtsReportFieldConfig> configs = this.btsReportFieldConfigMapper.selectByExample(example);
            if (!configs.isEmpty()) {
                List<BtsReportFieldConfigDto> fieldConfigDtos = new ArrayList<>();
                Set<BtsReportFieldConfigDto> set = new HashSet<>();
                configs.forEach(c -> {
                    set.add(new BtsReportFieldConfigDto(c.getBtsPlanId(), c.getReportFieldCode(), c.getReportFieldName()));
                });
                fieldConfigDtos.addAll(set);
                return fieldConfigDtos;
            }
        }
        return this.btsReportFieldConfig(btsReportParameterDto.getPlanId(), btsReportParameterDto.getProductLineCode());
    }

    /**
     * 报表字段名配置
     *
     * @param planId
     * @param productLineCode
     * @return
     */
    @Override
    //@Cacheable(cacheNames = "field_config_3", key = "T(String).valueOf(#planId) + #productLineCode")
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
    //@CacheEvict(cacheNames = "kylin_config_3", allEntries = false, beforeInvocation = true, key = "T(String).valueOf(#planId) + #productLineCode + #queryType")
    public void removeBtsReportKylinConfig(Long planId, String productLineCode, String queryType) {

    }

    @Override
    //@CacheEvict(cacheNames = "field_config_3", allEntries = false, beforeInvocation = true, key = "T(String).valueOf(#planId) + #productLineCode")
    public void removeBtsReportFieldConfig(Long planId, String productLineCode) {

    }
}
