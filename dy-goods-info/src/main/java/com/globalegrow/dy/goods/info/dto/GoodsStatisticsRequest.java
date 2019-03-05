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

    @Max(value = 1000, message = "size 最大为 1000")
    @Min(value = 1, message = "size 最小为 1")
    @NotNull(message = "size 不能为空")
    private Integer size = 1000;
    /**
     * 业务参数逻辑校验，必须为 1、3、7、15、30
     */
    @Max(value = 30, message = "维度最大为 30")
    @Min(value = 1, message = "维度最小为 1")
    @NotNull(message = "维度不能为空")
    private Integer dimension = 1;
    /**
     * 按 1 天维度查询时传参
     */
    private List<String> days = new ArrayList<>();


}
