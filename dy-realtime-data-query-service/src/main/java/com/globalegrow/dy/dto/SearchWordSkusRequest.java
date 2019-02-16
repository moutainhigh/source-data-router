package com.globalegrow.dy.dto;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

@Data
@ToString
public class SearchWordSkusRequest extends DyRequest{

    @NotBlank(message = "搜索词不能为空")
    private String word;

    @NotBlank(message = "站点不能为空")
    private String site;

}
