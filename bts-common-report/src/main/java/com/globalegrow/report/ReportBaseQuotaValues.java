package com.globalegrow.report;

import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ReportBaseQuotaValues {
    /**
     * 一般为 UV 或样本量计算, 或从 log 中取单一字段值
     */
    quotaStringValueExtractFromLog {
        @Override
        public Object getReportValueFromSourceLog(ReportQuotaFieldConfig reportQuotaFieldConfig, ReadContext ctx, String sourceJson) {
            logger.debug("样本量计算，指标: {}", reportQuotaFieldConfig);
            Object value = filter(reportQuotaFieldConfig, ctx, sourceJson);
            if (value == null) {
                logger.debug("样本量指标，指标值: {}, json: {}", ctx.read(reportQuotaFieldConfig.getExtractValueJsonPath(), String.class), sourceJson);
                return ctx.read(reportQuotaFieldConfig.getExtractValueJsonPath(), String.class);
            }

            logger.debug("未找到样本量计算 指标: {}，source: {}", reportQuotaFieldConfig, sourceJson);

            return null;
        }
    }, quotaIntValueExtractFromLog {
        @Override
        public Object getReportValueFromSourceLog(ReportQuotaFieldConfig reportQuotaFieldConfig, ReadContext ctx, String sourceJson) {
            Object value = filter(reportQuotaFieldConfig, ctx, sourceJson);
            if (value == null) {
                try {
                    return ctx.read(reportQuotaFieldConfig.getExtractValueJsonPath(), Integer.class);
                } catch (Exception e) {
                    // 忽略
                }
            }
            return null;
        }
    }, quotaLongValueExtractFromLog {
        @Override
        public Object getReportValueFromSourceLog(ReportQuotaFieldConfig reportQuotaFieldConfig, ReadContext ctx, String sourceJson) {
            Object value = filter(reportQuotaFieldConfig, ctx, sourceJson);
            if (value == null) {
                try {
                    return ctx.read(reportQuotaFieldConfig.getExtractValueJsonPath(), Long.class);
                } catch (Exception e) {
                    // 忽略
                }
            }
            return null;
        }
    },
    /**
     * 一般为 PV 或事件数计算
     */
    countOneWithFilter {
        @Override
        public Object getReportValueFromSourceLog(ReportQuotaFieldConfig reportQuotaFieldConfig, ReadContext ctx, String sourceJson) {
            Object value = filter(reportQuotaFieldConfig, ctx, sourceJson);
            if (value == null) {
                return 1;
            }
            return null;
        }
    },
    /**
     * 一般为曝光数计算
     */
    countListWithFilter {
        @Override
        public Object getReportValueFromSourceLog(ReportQuotaFieldConfig reportQuotaFieldConfig, ReadContext ctx, String sourceJson) {
            Object value = filter(reportQuotaFieldConfig, ctx, sourceJson);
            if (value == null) {
                List<String> list = ctx.read(reportQuotaFieldConfig.getExtractValueJsonPath(), List.class);
                if (list != null) {
                    return list.size();
                }
            }
            return null;
        }
    },
    /**
     * 计算逗号用字符串分隔的数量，一般为 app 端的曝光数
     */
    countStringWithComma {
        @Override
        public Object getReportValueFromSourceLog(ReportQuotaFieldConfig reportQuotaFieldConfig, ReadContext ctx, String sourceJson) {
            Object value = filter(reportQuotaFieldConfig, ctx, sourceJson);
            if (value == null) {
                String s = ctx.read(reportQuotaFieldConfig.getExtractValueJsonPath(), String.class);
                return s.split(",").length;
            }
            return null;
        }
    },
    /**
     * 直接从 log 中获取 map 值，如 bts 指标值
     */
    extractMapValueFromLog {
        @Override
        public Object getReportValueFromSourceLog(ReportQuotaFieldConfig reportQuotaFieldConfig, ReadContext ctx, String sourceJson) {
            Object value = filter(reportQuotaFieldConfig, ctx, sourceJson);
            if (value == null) {

                Map map = ctx.read(reportQuotaFieldConfig.getExtractValueJsonPath(), Map.class);
                if (map != null && map.size() > 0) {
                    return map;
                }

            }
            return null;
        }
    },extractBtsMapValueFromLog {
        @Override
        public Object getReportValueFromSourceLog(ReportQuotaFieldConfig reportQuotaFieldConfig, ReadContext ctx, String sourceJson) {
            Object value = filter(reportQuotaFieldConfig, ctx, sourceJson);
            if (value == null) {

                Map map = ctx.read(reportQuotaFieldConfig.getExtractValueJsonPath(), Map.class);
                if (map != null && map.size() > 0) {


                    String planid = String.valueOf(map.get("planid"));
                    String versionId = String.valueOf(map.get("versionid"));
                    String bucketid = String.valueOf(map.get("bucketid"));
                    if (StringUtils.isNotEmpty(planid) && StringUtils.isNotEmpty(versionId) && StringUtils.isNotEmpty(bucketid)) {

                        Map<String, String> btsMap = new HashMap<>();
                        btsMap.put("planid", planid);
                        btsMap.put("versionId", versionId);
                        btsMap.put("bucketid", bucketid);
                        return btsMap;

                    }

                }

            }
            return null;
        }
    }, extractAppBtsValueFromLog {
        @Override
        public Object getReportValueFromSourceLog(ReportQuotaFieldConfig reportQuotaFieldConfig, ReadContext ctx, String sourceJson) {
            Object value = filter(reportQuotaFieldConfig, ctx, sourceJson);

            if (value == null) {
                String planId = ctx.read("$.event_value.af_plan_id", String.class);
                String versionId = ctx.read("$.event_value.af_version_id", String.class);
                String bucketId = ctx.read("$.event_value.af_bucket_id", String.class);
                ;

                if (StringUtils.isNotEmpty(planId) && StringUtils.isNotEmpty(versionId) && StringUtils.isNotEmpty(bucketId)
                        && !"null".equals(planId) && !"null".equals(versionId) && !"null".equals(bucketId)) {
                    Map<String, String> bts = new HashMap<>();
                    bts.put("planid", planId);
                    bts.put("versionid", versionId);
                    bts.put("bucketid", bucketId);
                    return bts;
                }

            }

            return null;
        }
    };

    /**
     * 根据 filter 过滤条件判断是否需要计算报表指标
     * 目前支持 null , not null , equals, contains
     * 符合过滤条件返回 null，不符合过滤条件返回默认值
     *
     * @param reportQuotaFieldConfig
     * @param ctx
     * @return
     */
    private static Object filter(ReportQuotaFieldConfig reportQuotaFieldConfig, ReadContext ctx, String sourceJson) {

        if (reportQuotaFieldConfig.getJsonLogFilters() == null || reportQuotaFieldConfig.getJsonLogFilters().size() == 0) {
            return null;
        }

        for (JsonLogFilter jsonLogFilter : reportQuotaFieldConfig.getJsonLogFilters()) {

           /* String logField = jsonLogFilter.getJsonPath();
            String field1 = logField.substring(logField.indexOf("$.") + 2);
            if (field1.contains(".")) {
                field1 = field1.substring(0, field1.indexOf("."));
            }*/

            //if (sourceJson.contains(field1)) {

            if (handleQuotaFilter(jsonLogFilter, ctx) == null) {

                return reportQuotaFieldConfig.getDefaultValue();

            }
            //}else {
            //return reportQuotaFieldConfig.getDefaultValue();
            //}

        }
        return null;
    }

    /**
     * 过滤条件，不满足过滤条件返回 null，满足条件返回 not_null
     *
     * @param jsonLogFilter
     * @param ctx
     * @return
     */
    public static Object handleQuotaFilter(JsonLogFilter jsonLogFilter, ReadContext ctx) {
        // json 值为空时返回 true
        try {
            if ("null".equals(jsonLogFilter.getFilterRule())) {
                try {
                    if (!(ctx.read(jsonLogFilter.getJsonPath()) == null) || StringUtils.isNotEmpty(String.valueOf(ctx.read(jsonLogFilter.getJsonPath(), Object.class)))) {
                        return null;
                    }
                } catch (PathNotFoundException e) {
                    // null 条件为空报异常表明满足过滤条件
                    return "not_null";
                }
            } else

                // not null
                if ("not_null".equals(jsonLogFilter.getFilterRule())) {

                    if ((ctx.read(jsonLogFilter.getJsonPath()) == null) || StringUtils.isEmpty(String.valueOf(ctx.read(jsonLogFilter.getJsonPath(), Object.class)))) {
                        return null;
                    }
                } else if ("equals".equals(jsonLogFilter.getFilterRule())) {
                    // equals
                    if (!jsonLogFilter.getValueFilter().equals(ctx.read(jsonLogFilter.getJsonPath(), String.class))) {
                        return null;
                    }

                }
                /**
                 * 规则值包含 埋点值
                 */
                else if ("contains".contains(jsonLogFilter.getFilterRule())) {
                    // contains
                    if (!(jsonLogFilter.getValueFilter().contains(ctx.read(jsonLogFilter.getJsonPath(), String.class)))) {
                        return null;
                    }
                }
                /**
                 * 埋点值包含 输入值
                 */
                else if("contains_backward".contains(jsonLogFilter.getFilterRule())){
                    // contains
                    if (!(ctx.read(jsonLogFilter.getJsonPath(), String.class).contains(jsonLogFilter.getValueFilter()))) {
                        return null;
                    }
                }else
                    // 布尔值
                    if ("true".equals(jsonLogFilter.getFilterRule())) {
                        // contains
                        if (!(ctx.read(jsonLogFilter.getJsonPath(), Boolean.class))) {
                            return null;
                        }
                    } else if ("false".equals(jsonLogFilter.getFilterRule())) {
                        // contains
                        if ((ctx.read(jsonLogFilter.getJsonPath(), Boolean.class))) {
                            return null;
                        }
                    } else
                        // or
                        if ("or".equals(jsonLogFilter.getFilterRule())) {
                            List<String> strings = Arrays.asList(jsonLogFilter.getValueFilter().split(","));
                            String value = ctx.read(jsonLogFilter.getJsonPath(), String.class);
                            if (strings.stream().filter(s -> s.equals(value)).count() <= 0) {
                                return null;
                            }
                        }


        } catch (PathNotFoundException e) {
            logger.debug("未找到 json filter 配置项 {}", jsonLogFilter);
            return null;
        }
        return "not_null";
    }

    protected static final Logger logger = LoggerFactory.getLogger(ReportBaseQuotaValues.class);

    public abstract Object getReportValueFromSourceLog(ReportQuotaFieldConfig reportQuotaFieldConfig, ReadContext ctx, String sourceJson);
}
