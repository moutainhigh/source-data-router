package com.globalegrow.dy.dto;

import lombok.Data;
import lombok.ToString;

/**
 * es 分页对象
 */
@Data
@ToString
public abstract class EsPageRequest extends DyRequest{

    /**
     * es 分页 scroll id
     */
    private String requestId;

}
