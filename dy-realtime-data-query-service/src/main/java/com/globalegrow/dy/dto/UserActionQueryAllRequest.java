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
 * 查询全部用户事件序列
 */
@Data
@ToString
public class UserActionQueryAllRequest extends EsPageRequest {

    private String cookieId;

    @NotBlank(message = "网站源不能为空")
    private String site;

    /**
     * 事件开始时间
     */
    private Long startDate;
    /**
     * 平台，app 接口只对应 ios android
     */
    private List<String> platform = new ArrayList<>();
    /**
     * 事件类型
     */
    private List<String> type = new ArrayList<>();
    @Max(1000)
    @Min(0)
    @NotNull
    private Integer size = 100;

}
