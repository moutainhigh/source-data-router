package com.globalegrow.dy.goods.info.dto;

import com.globalegrow.dy.dto.DyRequest;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品扩展信息查询
 */
@Data
@ToString
public class GoodsExtendInfoRequest extends DyRequest {

    /**
     * 商品信息种类，
     * gb_obs_label : gb 商品标签信息
     */
    private List<String> species = new ArrayList<>();
    /**
     * 商品 sku
     */
    private String good_sn;
    /**
     * 语言
     */
    private String lang;
    /**
     * 渠道 GB、GBES
     */
    private String pipeline_code;
    /**
     * 虚拟仓编码
     */
    private String warecode;
    /**
     * 其他参数信息
     */
    private Map<String, Object> otherParamers = new HashMap<>();

}
