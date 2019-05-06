package com.globalegrow.dy.zaful.app.user.feature.dto;

import com.globalegrow.dy.dto.CommonPatternResponse;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@ApiModel("用户层级数据响应对象，用户不存在时返回空字符串，data 类型为 string，\n" +
        "00：老用户，无回访 \n" +
        "01：老用户，有回访 \n" +
        "10：新用户，无回访 \n" +
        "11：新用户，有回访")
public class AppUserLayerResponse extends CommonPatternResponse<String> {
}
