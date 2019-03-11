package com.globalegrow.dy.goods.info.dto;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@ToString
public class GoodsTendencyRequest {

    @Size(min = 1, max = 2000, message = "sku 数量必须在 1-2000")
    private List<String> item_ids;

    @NotBlank(message = "站点不能为空")
    @Length(min = 1, max = 256, message = "站点名长度必须在 0-256 之间")
    private String site;

    private String country;

    @Max(value = 2000, message = "size 最大为 1000")
    @Min(value = 1, message = "size 最小为 1")
    @NotNull(message = "size 不能为空")
    private Integer size = 1000;

    @NotBlank(message = "平台不能为空")
    @Length(min = 1, max = 256, message = "平台长度必须在 0-256 之间")
    private String platform;

    public List<String> getIds() {
        List<String> list = new ArrayList<>();
        this.item_ids.stream().forEach(s -> list.add(s + "_" + this.platform));
        return list;
    }

}
