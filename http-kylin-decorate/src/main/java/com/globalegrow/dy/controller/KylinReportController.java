package com.globalegrow.dy.controller;

import com.globalegrow.dy.dto.*;
import com.globalegrow.dy.service.BtsReportConfigService;
import com.globalegrow.dy.service.BtsReportService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.util.*;

@RestController
@RequestMapping("report/bts")
public class KylinReportController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BtsReportService btsReportService;

    @Autowired
    private BtsReportConfigService btsReportConfigService;

    static final List<String> FIXED_FIELDS = Arrays.asList("bts_planid", "bts_versionid", "bts_bucketid", "bts_policy", "bts_plancode", "day_start");

/*    *//**
     * BtsReportParameterDto{planId=56, groupByFields=[bts_planid], whereFields={bts_planid=56}, betweenFields={day_start={min=2018-09-04, max=2099-01-01}}, orderFields={}, startPage=0, pageSize=10, type='query', productLineCode='RG'}
     * BtsReportParameterDto{planId=56, groupByFields=[bts_planid, bts_versionid], whereFields={bts_planid=56}, betweenFields={day_start={min=2018-09-04, max=2099-01-01}}, orderFields={}, startPage=0, pageSize=10, type='query', productLineCode='RG'}
     * BtsReportParameterDto{planId=56, groupByFields=[day_start, bts_planid, bts_versionid], whereFields={bts_planid=56}, betweenFields={day_start={min=2018-09-04, max=2018-09-14}}, orderFields={day_start=desc}, startPage=0, pageSize=10, type='query', productLineCode='RG'}
     * BtsReportParameterDto{planId=56, groupByFields=[bts_planid, bts_versionid], whereFields={bts_planid=56}, betweenFields={day_start={min=2018-09-04, max=2099-01-01}}, orderFields={}, startPage=0, pageSize=10, type='all', productLineCode='RG'}
     *//*
    @Scheduled(fixedDelay = 50000)
    public void rgScheduling() {
        this.logger.info("query rg login cache ahead");
        List<String> groupFields = new ArrayList<>();
        groupFields.add("day_start");
        groupFields.add("bts_planid");
        groupFields.add("bts_versionid");
        Map<String, String> whereFields = new HashMap<>();
        whereFields.put("bts_planid","56");
        Map<String, Map<String, String>> betweenFields = new HashMap<>();
        Map<String, String> minAndMax = new HashMap<>();
        minAndMax.put("min", "2018-09-04");
        minAndMax.put("max", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
        betweenFields.put("day_start", minAndMax);
        Map<String, String> orderFields = new HashMap<>();
        orderFields.put("day_start", "desc");
        BtsReportParameterDto btsReportParameterDto = new BtsReportParameterDto();
        btsReportParameterDto.setBetweenFields(betweenFields);
        btsReportParameterDto.setType("query");
        btsReportParameterDto.setPlanId(56L);
        btsReportParameterDto.setWhereFields(whereFields);
        btsReportParameterDto.setProductLineCode("RG");
        btsReportParameterDto.setStartPage(0);
        btsReportParameterDto.setPageSize(10);
        btsReportParameterDto.setOrderFields(orderFields);
        this.btsReportService.btsReport(*//*this.btsReportConfigService.getBtsReportKylinConfig(btsReportParameterDto.getPlanId(), btsReportParameterDto.getProductLineCode(), btsReportParameterDto.getType()),*//*btsReportParameterDto);
        BtsReportParameterDto btsReportParameterDto2 = new BtsReportParameterDto();
        btsReportParameterDto2.setType("query");
        btsReportParameterDto2.setPlanId(56L);
        Map<String, Map<String, String>> betweenFields2 = new HashMap<>();
        Map<String, String> minAndMax2 = new HashMap<>();
        minAndMax2.put("min", "2018-09-04");
        minAndMax2.put("max", "2099-01-01");
        betweenFields.put("day_start", minAndMax2);
        btsReportParameterDto2.setWhereFields(whereFields);
        btsReportParameterDto2.setBetweenFields(betweenFields2);
        btsReportParameterDto2.setProductLineCode("RG");
        btsReportParameterDto2.setStartPage(0);
        //btsReportParameterDto2.setType("query");
        this.btsReportService.btsReport(*//*this.btsReportConfigService.getBtsReportKylinConfig(btsReportParameterDto2.getPlanId(), btsReportParameterDto2.getProductLineCode(), btsReportParameterDto2.getType()),*//*btsReportParameterDto2);


        BtsReportParameterDto btsReportParameterDto3 = new BtsReportParameterDto();
        //btsReportParameterDto3.setType("query");
        btsReportParameterDto3.setPlanId(56L);
        Map<String, Map<String, String>> betweenFields3 = new HashMap<>();
        Map<String, String> minAndMax3 = new HashMap<>();
        minAndMax2.put("min", "2018-09-04");
        minAndMax2.put("max", "2099-01-01");
        betweenFields.put("day_start", minAndMax3);
        btsReportParameterDto3.setWhereFields(whereFields);
        btsReportParameterDto3.setBetweenFields(betweenFields3);
        btsReportParameterDto3.setProductLineCode("RG");
        btsReportParameterDto3.setStartPage(0);
        btsReportParameterDto3.setType("all");
        this.btsReportService.btsReport(*//*this.btsReportConfigService.getBtsReportKylinConfig(btsReportParameterDto3.getPlanId(), btsReportParameterDto3.getProductLineCode(), btsReportParameterDto3.getType()),*//*btsReportParameterDto3);
    }*/

    @RequestMapping(produces="application/json;charset=UTF-8", method = RequestMethod.POST)
    public ReportPageDto btsReport(@RequestBody BtsReportParameterDto btsReportParameterDto) {
        this.logger.debug("报表请求参数: {}", btsReportParameterDto);
        List<String> groupString = btsReportParameterDto.getGroupByFields();
        boolean groupByVersion = groupString.contains("bts_versionid");
        boolean groupByDay = groupString.contains("day_start");
        if (!groupByDay) {
            groupString.add("day_start");
        }
        if (!groupByVersion) {
            groupString.add("bts_versionid");
        }
        //ReportPageDto reportPageDto = this.btsReportService.btsReport(this.btsReportConfigService.getBtsReportKylinConfig(btsReportParameterDto.getPlanId(), btsReportParameterDto.getProductLineCode(), btsReportParameterDto.getType()),btsReportParameterDto);
        ReportPageDto reportPageDto = this.btsReportService.btsReport(btsReportParameterDto);
        List<Object> data = reportPageDto.getData();

        if (!groupByDay && !groupByVersion) {
            List<Map<String, Object>> dataConvert = new ArrayList<>();
            Map<String, List<Map<String, Object>>> versionGroups = new HashMap<>();
            data.stream().forEach(o -> {
                Map<String, Object> map = (Map<String, Object>) o;
                if (versionGroups.get(/*map.get("BTS_VERSIONID") + "_" + map.get("DAY_START") + "_" +*/ map.get("BTS_PLANID")) == null) {
                    versionGroups.put(/*String.valueOf(map.get("BTS_VERSIONID") + "_" + map.get("DAY_START") + "_" + */String.valueOf(map.get("BTS_PLANID")), new ArrayList<>());
                }
                versionGroups.get(/*map.get("BTS_VERSIONID") + "_" + map.get("DAY_START") + "_" + */map.get("BTS_PLANID")).add(map);
            });
            versionGroups.entrySet().forEach(e -> {
                Map<String, Object> entry = new HashMap<>();
                e.getValue().stream().forEach(m -> {
                    m.entrySet().forEach(e1 -> {
                        String key = e1.getKey();
                        String valueVersion = String.valueOf(e1.getValue());
                        //logger.debug("报表转换: {}, {}", key, valueVersion);
                        if (FIXED_FIELDS.contains(key.toLowerCase()) && !"DAY_START".equals(key) && !"BTS_VERSIONID".equals(key)) {
                            entry.put(key, valueVersion);
                        }
                        else if(!FIXED_FIELDS.contains(key.toLowerCase())){
                            handleGroupValue(entry, key, valueVersion);
                        }
                    });
                });
                handleFloatValue(e, entry);
                dataConvert.add(entry);
            });
            List<Object> objects = new ArrayList<>();
            dataConvert.stream().forEach(e -> objects.add(e));
            reportPageDto.setData(objects);
        }else
        if (!groupByVersion && groupByDay) {
            List<Map<String, Object>> dataConvert = new ArrayList<>();
            Map<String, List<Map<String, Object>>> versionGroups = new HashMap<>();
            data.stream().forEach(o -> {
                Map<String, Object> map = (Map<String, Object>) o;
                if (versionGroups.get(map.get("DAY_START")) == null) {
                    versionGroups.put(String.valueOf(map.get("DAY_START")), new ArrayList<>());
                }
                versionGroups.get(String.valueOf(map.get("DAY_START"))).add(map);
            });
            versionGroups.entrySet().forEach(e -> {
                Map<String, Object> entry = new HashMap<>();
                e.getValue().stream().forEach(m -> {
                    m.entrySet().forEach(e1 -> {
                        String key = e1.getKey();
                        String valueVersion = String.valueOf(e1.getValue());
                        //logger.debug("报表转换: {}, {}", key, valueVersion);
                        if (FIXED_FIELDS.contains(key.toLowerCase()) && !"BTS_VERSIONID".equals(key)) {
                            entry.put(key, valueVersion);
                        }
                        else if(!FIXED_FIELDS.contains(key.toLowerCase())){
                            handleGroupValue(entry, key, valueVersion);
                        }
                    });
                });
                handleFloatValue(e, entry);
                dataConvert.add(entry);
            });
            List<Object> objects = new ArrayList<>();
            dataConvert.stream().forEach(e -> objects.add(e));
            reportPageDto.setData(objects);
        }else
        if (!groupByDay && groupByVersion) {
            List<Map<String, Object>> dataConvert = new ArrayList<>();
            Map<String, List<Map<String, Object>>> versionGroups = new HashMap<>();
            data.stream().forEach(o -> {
                Map<String, Object> map = (Map<String, Object>) o;
                if (versionGroups.get(map.get("BTS_VERSIONID")) == null) {
                    versionGroups.put(String.valueOf(map.get("BTS_VERSIONID")), new ArrayList<>());
                }
                //logger.debug("list信息以及 data {} : {}", list, map);
                // logger.debug("按照 版本 分组信息: {}", versionGroups);
                versionGroups.get(String.valueOf(map.get("BTS_VERSIONID"))).add(map);
            });
            versionGroups.entrySet().forEach(e -> {
                Map<String, Object> entry = new HashMap<>();
                e.getValue().stream().forEach(m -> {
                    m.entrySet().forEach(e1 -> {
                        String key = e1.getKey();
                        String valueVersion = String.valueOf(e1.getValue());
                        //logger.debug("报表转换: {}, {}", key, valueVersion);
                        if (FIXED_FIELDS.contains(key.toLowerCase()) && !"DAY_START".equals(key)) {
                            entry.put(key, valueVersion);
                        }else if(!FIXED_FIELDS.contains(key.toLowerCase())){
                            handleGroupValue(entry, key, valueVersion);
                        }

                    });

                });
                handleFloatValue(e, entry);
                dataConvert.add(entry);
            });
            List<Object> objects = new ArrayList<>();
            dataConvert.stream().forEach(e -> objects.add(e));
            reportPageDto.setData(objects);
        }
        //this.logger.debug("报表转换结果: {}", reportPageDto);
        return reportPageDto;
    }

    /**
     * 小数去均值
     * @param e
     * @param entry
     */
    private void handleFloatValue(Map.Entry<String, List<Map<String, Object>>> e, Map<String, Object> entry) {
        DecimalFormat decimalFormat=new DecimalFormat("0.000");
        entry.entrySet().forEach(report -> {
            if (String.valueOf(report.getValue()).contains(".")) {
                entry.put(report.getKey(), decimalFormat.format(Float.valueOf(String.valueOf(report.getValue()))/e.getValue().size()));
            }
        });
    }

    private void handleGroupValue(Map<String, Object> entry, String key, String valueVersion) {
        if (valueVersion != null && !"null".equals(valueVersion)) {
            if (entry.get(key) == null) {
                entry.put(key, valueVersion);
            }else {
                if (valueVersion.contains(".")) {
                    entry.put(key, (Float.valueOf(String.valueOf(entry.get(key))) + Float.valueOf(valueVersion)) + "");
                }else {
                    entry.put(key, (Integer.valueOf(String.valueOf(entry.get(key))) + Integer.valueOf(valueVersion)) + "");
                }
            }

        }else {
            entry.put(key, 0);
        }
    }

    @RequestMapping(value = "config", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
    public ReportFieldConfigResultDto fieldConfigDtos(@RequestBody FieldConfigParameterDto dto) {
        return new ReportFieldConfigResultDto(this.btsReportConfigService.btsReportFieldConfig(dto.getPlanId(), dto.getProductLineCode()));
    }

 /*   public static void main(String[] args) {
        System.out.println(FIXED_FIELDS.contains("BTS_PLANID"));
        System.out.println(FIXED_FIELDS.contains("BTS_PLANID".toLowerCase()));
    }*/

}
