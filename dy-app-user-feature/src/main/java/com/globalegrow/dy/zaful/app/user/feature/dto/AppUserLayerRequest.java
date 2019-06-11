package com.globalegrow.dy.zaful.app.user.feature.dto;

import com.globalegrow.dy.dto.DyRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@ApiModel("用户分层数据请求对象")
public class AppUserLayerRequest extends DyRequest {

    @Length(min = 1, max = 256, message = "cookie id 长度必须在 0-256 之间")
    @NotBlank(message = "用户 cookie id 不能为空")
    @ApiModelProperty("app 埋点 appsflyer_device_id ， 必填")
    private String device_id;

}
