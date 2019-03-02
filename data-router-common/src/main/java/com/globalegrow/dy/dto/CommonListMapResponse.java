package com.globalegrow.dy.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@ToString
public class CommonListMapResponse extends CommonPatternResponse<List<Map<String,Object>>>{
}
