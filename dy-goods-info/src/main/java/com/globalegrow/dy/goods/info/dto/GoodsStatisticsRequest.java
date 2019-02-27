package com.globalegrow.dy.goods.info.dto;

import com.globalegrow.dy.dto.EsPageRequest;
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
public class GoodsStatisticsRequest extends EsPageRequest {

    @NotBlank(message = "站点不能为空")
    @Length(min = 1, max = 256, message = "站点名长度必须在 0-256 之间")
    private String site;

    @NotBlank(message = "平台不能为空")
    @Length(min = 1, max = 256, message = "平台长度必须在 0-256 之间")
    private String platform;

    private String country;

    @Max(1000)
    @Min(1)
    @NotNull
    private Integer size = 1000;
    @Max(30)
    @Min(1)
    @NotNull
    private Integer dimension = 1;
    /**
     * 按 1 天维度查询时传参
     */
    private List<String> days = new ArrayList<>();


}
