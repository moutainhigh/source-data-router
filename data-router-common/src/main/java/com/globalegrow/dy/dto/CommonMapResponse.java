package com.globalegrow.dy.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
@ApiModel("响应对象，类型为 map")
public class CommonMapResponse extends CommonPatternResponse<Map<String,Object>>{
}
