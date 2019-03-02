package com.globalegrow.dy.dto;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户基本信息参数
 */
@Data
@ToString
public class UserBaseInfoRequest extends EsPageRequest {

    private String cookieId;

    private List<String> platform = new ArrayList<>();
    @Max(1000)
    @Min(0)
    @NotNull
    private Integer size = 100;

    @NotBlank(message = "网站源不能为空")
    private String site;

    private Long startDate;

}
