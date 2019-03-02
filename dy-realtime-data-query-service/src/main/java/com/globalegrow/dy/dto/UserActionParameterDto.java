package com.globalegrow.dy.dto;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class UserActionParameterDto extends DyRequest{

    @NotNull(message = "cookieid 不能为空")
    @Length(min = 10, max = 256, message = "cookieid 长度为 10-256")
    private String cookieId;

    private List<String> type;

    //@NotNull
    private Long startDate;

    //@NotNull
    private Long endDate;

    @NotBlank(message = "网站标识不能为空")
    private String site;

    //private List<String> platform = new ArrayList<>();

    /**
     * 每页数量
     */
    @Max(1000)
    @Min(0)
    @NotNull
    private Integer size = 1000;

}
