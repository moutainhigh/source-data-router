package com.globalegrow.dy.zaful.app.user.feature.dto;

import com.globalegrow.dy.dto.CommonPatternResponse;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@ApiModel("用户层级数据响应对象，用户不存在时返回空字符串，data 类型为 string，\n" +
        "000：老用户，无回访 <br>" +
        "010：老用户，有回访 <br>" +
        "100：新用户，无回访 <br>" +
        "110：新用户，有回访 <br>" +
        "111：游客回访")
public class AppUserLayerResponse extends CommonPatternResponse<String> {
}
