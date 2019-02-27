package com.globalegrow.dy.goods.info.dto;

import com.globalegrow.dy.dto.DyRequest;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@ToString
public class GoodsBaseInfoRequest extends DyRequest {

    @NotBlank(message = "站点不能为空")
    @Length(min = 1, max = 256, message = "站点名长度必须在 0-256 之间")
    private String site;

    @Size(min = 1, max = 2000, message = "sku 数量必须在 1-2000")
    private List<String> item_ids;



}
