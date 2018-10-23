package com.globalegrow;

import com.globalegrow.dy.dto.UserActionParameterDto;
import com.globalegrow.dy.utils.JacksonUtil;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ParameterTest {

    @Test
    public void test() throws Exception {
        UserActionParameterDto userActionParameterDto = new UserActionParameterDto();
        userActionParameterDto.setCookieId("1539741356098-1041367");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        /*userActionParameterDto.setStartDate(format.parse("2018-10-17 00:00:00").getTime());
        userActionParameterDto.setEndDate(format.parse("2018-10-17 23:59:59").getTime());*/

        System.out.println(JacksonUtil.toJSon(userActionParameterDto));
    }

}
