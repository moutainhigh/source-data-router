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
                String currentValue = String.valueOf(entry.get(key));
                if (valueVersion.contains(".") || currentValue.contains(".")) {
                    entry.put(key, (Float.valueOf(currentValue) + Float.valueOf(valueVersion)) + "");
                }else {
                    this.logger.debug("report data value handle: {},{}", key, valueVersion);
                    entry.put(key, (Integer.valueOf(currentValue) + Integer.valueOf(valueVersion)) + "");
                }
            }

        }else {
            entry.put(key, 0);
        }
    }

    @RequestMapping(value = "config", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
    public ReportFieldConfigResultDto fieldConfigDtos(@RequestBody FieldConfigParameterDto dto) {
        return new ReportFieldConfigResultDto(this.btsReportConfigService.btsReportFieldConfigMixedQuery(dto));
    }

 /*   public static void main(String[] args) {
        System.out.println(FIXED_FIELDS.contains("BTS_PLANID"));
        System.out.println(FIXED_FIELDS.contains("BTS_PLANID".toLowerCase()));
    }*/

}
