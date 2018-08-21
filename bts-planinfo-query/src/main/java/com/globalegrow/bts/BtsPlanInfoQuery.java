package com.globalegrow.bts;

import java.util.Map;

public interface BtsPlanInfoQuery {

    /**
     * 根据推荐位和 cookie 查询
     * @param cookie
     * @param productCode 如：zaful ZF
     * @param planCode 如：推荐位 2020201
     * @return
     */
    Map<String, String> queryBtsInfo(String cookie, String productCode, String planCode);


    Map<String, String> queryBtsInfo(Map<String, Object> dataMap);

}
