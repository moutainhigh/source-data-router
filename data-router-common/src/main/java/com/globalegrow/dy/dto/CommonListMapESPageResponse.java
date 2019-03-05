package com.globalegrow.dy.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@ToString
public class CommonListMapESPageResponse extends CommonPatternResponse<List<Map<String,Object>>>{

    private String requestId;

    private Long total;

}
