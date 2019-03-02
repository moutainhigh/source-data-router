package com.globalegrow.dy.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public abstract class EsPageResponse extends CommonResponse {

    /**
     * 数据总数
     */
    private Long totalCount;

    /**
     * es 分页 scroll id
     */
    private String requestId;

}
