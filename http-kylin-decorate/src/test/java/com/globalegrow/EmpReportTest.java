package com.globalegrow;

import com.globalegrow.dy.dto.BtsReportParameterDto;
import com.globalegrow.dy.model.BtsReportKylinConfig;
import com.globalegrow.util.GsonUtil;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmpReportTest {

    private RestTemplate restTemplate = restTemplate();

    BtsReportKylinConfig btsReportKylinConfig = new BtsReportKylinConfig();
    BtsReportParameterDto btsReportParameterDto = new BtsReportParameterDto();

    @Before
    public void before() {
        btsReportKylinConfig.setKylinQueryAdress("http://ems.appinthestore.com.ems_auto_marketing.php5.egomsl.com/marketing/api-bts-email-info/get-email-order-info?module_name=marketing_email");
        btsReportParameterDto.setPlanId(1L);
        btsReportParameterDto.setType("query");
        List<String> groupBy = new ArrayList<>();
        groupBy.add("bts_planid");
        groupBy.add("bts_versionid");
        groupBy.add("day_start");
        btsReportParameterDto.setGroupByFields(groupBy);
        Map<String, Map<String, String>> betweenFields = new HashMap<>();
        Map<String, String> dayBetween = new HashMap<>();
        dayBetween.put("min", "2018-09-01");
        dayBetween.put("max", "2018-09-30");
        betweenFields.put("day_start", dayBetween);
        btsReportParameterDto.setBetweenFields(betweenFields);
    }

    @Test
    public void empReport() {
        String queryType = "query";
        List<String> groupByFields = btsReportParameterDto.getGroupByFields();
        StringBuilder stringBuilder = new StringBuilder(btsReportKylinConfig.getKylinQueryAdress());
        /*Map<String, Object> getParameters = new HashMap<>();
        getParameters.put("module_name", "marketing_email");*/
        stringBuilder.append("&plan_id=" + btsReportParameterDto.getPlanId());
        if (groupByFields == null || groupByFields.size() == 0 || (groupByFields.contains("bts_planid") && groupByFields.contains("bts_versionid") && groupByFields.contains("day_start"))) {
            stringBuilder.append("&data_flag=3");
        } else if (groupByFields.contains("bts_planid") && groupByFields.contains("bts_versionid") && !groupByFields.contains("day_start")) {
            stringBuilder.append("&data_flag=2");
        } else if (groupByFields.contains("bts_planid") && !groupByFields.contains("bts_versionid")) {
            stringBuilder.append("&data_flag=1");
        }
        Map<String, Map<String, String>> betweenFields = btsReportParameterDto.getBetweenFields();
        if (betweenFields != null && betweenFields.size() > 0) {
            Map<String, String> dayBetween = betweenFields.get("day_start");
            if (dayBetween != null && dayBetween.size() > 0) {
                String day = dayBetween.get("min") + "_" + dayBetween.get("max");
                stringBuilder.append("&day=" + day);
            }
        }
        String o = this.restTemplate.getForObject(stringBuilder.toString(), String.class);
        System.out.println(o);
        Map<String, Object> result = GsonUtil.readValue(o, Map.class);

        List<Map<String, String>> data = (List<Map<String, String>>) result.get("data");
        List<Map<String, String>> outReportData = new ArrayList<>();
        if (data != null && data.size() > 0) {
            for (Map<String, String> reportData : data) {
                Map<String, String> rowData = new HashMap<>();
                reportData.entrySet().forEach(e -> {
                    if ("plan_id".equals(e.getKey())) {
                        rowData.put("BTS_PLANID", e.getValue());
                    } else if ("version_id".equals(e.getKey())) {
                        rowData.put("BTS_VERSIONID", e.getValue());
                    } else if ("day".equals(e.getKey())) {
                        rowData.put("DAY_START", e.getValue());
                    } else {
                        rowData.put("send_ok_count".equals(e.getKey()) ? "SPECIMEN" : "SUM_" + e.getKey().toUpperCase(), e.getValue());
                    }

                });
                // 转换率处理
                // 送达率
                rowData.put("SUM_DELIVER_RATE", divPer(rowData.get("SPECIMEN"), rowData.get("SUM_TOTAL_COUNT")));
                // 打开率
                rowData.put("SUM_OPEN_RATE", divPer(rowData.get("SUM_OPEN_COUNT"), rowData.get("SPECIMEN")));
                // 点击转化率
                rowData.put("SUM_TRANS_RATE", divPer(rowData.get("SUM_CLICK_COUNT"), rowData.get("SUM_OPEN_COUNT")));
                // 下单转化率
                rowData.put("SUM_ORDER_TRANS_RATE", divPer(rowData.get("SUM_ORDER_USER"), rowData.get("SUM_CLICK_COUNT")));
                // 下单率
                rowData.put("SUM_ORDER_RATE", divPer(rowData.get("SUM_ORDER_USER"), rowData.get("SPECIMEN")));
                // 生单客均价
                rowData.put("SUM_ORDER_USER_AVG", divLongFloat(rowData.get("SUM_ORDER_USER"), rowData.get("SUM_ORDER_MONEY")));
                // 付款订单率
                rowData.put("SUM_PAYED_ORDER_RATE", divPer(rowData.get("SUM_PAYED_ORDER_NUMS"), rowData.get("SUM_ORDER_NUMS")));
                // 付款金额率
                rowData.put("SUM_PAY_AMOUNT_RATE", divFloatFloatPer(rowData.get("SUM_PAYED_ORDER_MONEY"), rowData.get("SUM_ORDER_MONEY")));
                // 付款客均价
                rowData.put("SUM_ORDER_USER_AVG_PRICE", divFloatLong(rowData.get("SUM_PAYED_ORDER_MONEY"), rowData.get("SUM_PAYED_USER")));
                outReportData.add(rowData);
            }
        }
        System.out.println(GsonUtil.toJson(outReportData));
        // 均值处理&总值处理
        if ("query".equals(btsReportParameterDto.getType())) {
            List<Map<String, String>> avgReport = new ArrayList<>();
            outReportData.stream().forEach(m -> {
                Map<String, String> avgRow = new HashMap<>();
                m.entrySet().forEach(e -> {
                    if ("SPECIMEN".equals(e.getKey()) || e.getKey().contains("BTS") || "DAY_START".equals(e.getKey())) {
                        avgRow.put(e.getKey(), e.getValue());
                    } else if (e.getKey().endsWith("_RATE")) {
                        avgRow.put(e.getKey().replace("SUM_", "AVG_"), e.getValue());
                    } else {
                        avgRow.put(e.getKey().replace("SUM_", "AVG_"), formatDivResult(Float.valueOf(e.getValue()) / Float.valueOf(m.get("SPECIMEN"))));
                    }
                    avgReport.add(avgRow);
                });
            });
            System.out.println(GsonUtil.toJson(avgReport));
        } else if ("all".equals(btsReportParameterDto.getType())) {
            List<Map<String, String>> allReport = new ArrayList<>();
            outReportData.stream().forEach(m -> {
                Map<String, String> avgRow = new HashMap<>();
                avgRow.putAll(m);
                m.entrySet().forEach(e -> {
                    if ("SPECIMEN".equals(e.getKey()) || e.getKey().contains("BTS") || "DAY_START".equals(e.getKey())) {
                        avgRow.put(e.getKey(), e.getValue());
                    } else if (e.getKey().endsWith("_RATE")) {
                        avgRow.put(e.getKey().replace("SUM_", "AVG_"), e.getValue());
                    } else {
                        avgRow.put(e.getKey().replace("SUM_", "AVG_"), formatDivResult(Float.valueOf(e.getValue()) / Float.valueOf(m.get("SPECIMEN"))));
                    }
                    allReport.add(avgRow);
                });
            });
            System.out.println(GsonUtil.toJson(allReport));
        }

    }

    private String divLongFloat(String top, String bottom) {
        if (Float.valueOf(bottom) > 0) {
            return formatDivResult(Long.valueOf(top) / Float.valueOf(bottom));
        }
        return "0";
    }

    private String divFloatLong(String top, String bottom) {
        if (Float.valueOf(bottom) > 0) {
            return formatDivResult(Float.valueOf(top) / Float.valueOf(bottom));
        }
        return "0";
    }

    private String divFloatFloatPer(String top, String bottom) {
        if (Float.valueOf(bottom) > 0) {
            return formatDivResult((Float.valueOf(top) / Float.valueOf(bottom)) * 100);
        }
        return "0";
    }

    private String divLongFloatPer(String top, String bottom) {
        if (Float.valueOf(bottom) > 0) {
            return formatDivResult((Long.valueOf(top) / Float.valueOf(bottom)) * 100);
        }
        return "0";
    }

    private String divFloatLongPer(String top, String bottom) {
        if (Float.valueOf(bottom) > 0) {
            return formatDivResult((Float.valueOf(top) / Float.valueOf(bottom)) * 100);
        }
        return "0";
    }

    private String divPer(String top, String bottom) {
        if (Float.valueOf(bottom) > 0) {
            return formatDivResult((Long.valueOf(top) / Float.valueOf(bottom)) * 100);
        }
        return "0";
    }

    private String formatDivResult(Object dresult) {
        DecimalFormat decimalFormat = new DecimalFormat("0.000");
        return decimalFormat.format(dresult);
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(httpRequestFactory());
    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        HttpComponentsClientHttpRequestFactory componentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient());
        componentsClientHttpRequestFactory.setReadTimeout(20000);
        return componentsClientHttpRequestFactory;

    }

    @Bean
    public HttpClient httpClient() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        //设置整个连接池最大连接数 根据自己的场景决定
        connectionManager.setMaxTotal(1000);
        //路由是对maxTotal的细分
        connectionManager.setDefaultMaxPerRoute(300);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(20000) //服务器返回数据(response)的时间，超过该时间抛出read timeout
                .setConnectTimeout(20000)//连接上服务器(握手成功)的时间，超出该时间抛出connect timeout
                .setConnectionRequestTimeout(20000)//从连接池中获取连接的超时时间，超过该时间未拿到可用连接，会抛出org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
                .build();
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .build();
    }

}