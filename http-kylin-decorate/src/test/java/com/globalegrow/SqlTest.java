package com.globalegrow;

import com.globalegrow.dy.dto.BtsReportParameterDto;
import com.globalegrow.util.GsonUtil;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.text.StringSubstitutor;
import org.junit.Test;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SqlTest {

    @Test
    public void floatTest() {
        DecimalFormat decimalFormat=new DecimalFormat("0.000");
        System.out.println(decimalFormat.format(3f/10));
        System.out.println(decimalFormat.format(10f/3));
        System.out.println(decimalFormat.format(100f/3));
        System.out.println(decimalFormat.format(1000f/3));
        System.out.println(DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
    }

    @Test
    public void testDb() {
        String sql = "select sum(exp_num) exp_num, sum(click_num) click_num, sum(ADD_CART_NUM) cart_num, sum(sku_order_num) sku_order, sum(paid_order_num) paid_order, sum(paid_amount) paid_amount, count(distinct specimen) specimen, CASE  WHEN sum(paid_amount) > 0 THEN ROUND(CAST(sum(paid_amount) AS FLOAT) / 100, 2) ELSE 0 END amount, CASE  WHEN sum(exp_num) > 0 THEN ROUND(CAST(sum(click_num) AS FLOAT) / sum(exp_num), 5) ELSE 0 END exp_rate, CASE  WHEN sum(click_num) > 0 THEN ROUND(CAST(sum(ADD_CART_NUM) AS FLOAT) / sum(click_num), 5) ELSE 0 END click_rate, CASE WHEN sum(ADD_CART_NUM) > 0 THEN ROUND(CAST(sum(sku_order_num) AS FLOAT) / sum(ADD_CART_NUM), 5) ELSE 0 END order_rate, CASE  WHEN sum(sku_order_num) > 0 THEN ROUND(CAST(sum(paid_order_num) AS FLOAT) / sum(sku_order_num), 5) ELSE 0 END pay_rate, CASE  WHEN sum(exp_num)  > 0 THEN ROUND(CAST(sum(paid_order_num) AS FLOAT) / sum(exp_num) , 5) ELSE 0 END total_rate, ${groupByFields} from BTS_ZAFUL_RECOMMEND_REPORT WHERE ${whereFields} ${betweenFields} group by ${groupByFields} order by ${orderByFields}";
        Map valuesMap = new HashMap();
        valuesMap.put("groupByFields", " day_start,bts_plan_id,bts_version_id,bts_bucket_id ");
        valuesMap.put("whereFields", " day_start = date '2018-08-09' and bts_plan_id = '13' ");
        valuesMap.put("betweenFields", "");
        valuesMap.put("orderByFields", " day_start desc ");
        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String resolvedString = sub.replace(sql);
        System.out.println(resolvedString);
    }

    @Test
    public void testIntPlus() {
        int i = 0;
        i++;
        System.out.println(i);
    }

    @Test
    public void testParameter() {
        BtsReportParameterDto dto = new BtsReportParameterDto();
        dto.setPlanId(13L);
        dto.setStartPage(1);
        dto.setPageSize(10);
        dto.getGroupByFields().add("day_start");
        dto.getGroupByFields().add("bts_plan_id");
        dto.getGroupByFields().add("bts_version_id");

        dto.getWhereFields().put("day_start", "2018-08-10");
        dto.getWhereFields().put("bts_plan_id", "13");

        System.out.println(GsonUtil.toJson(dto));
    }

}
