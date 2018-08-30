package com.globalegrow.common;

import com.globalegrow.util.NginxLogConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class CommonLogConvert {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * kafka 消息转换为 map 对象
     * @param logString
     * @return
     */
    protected Map<String, Object> logToMap(String logString) {
        return NginxLogConvertUtil.getNginxLogParameters(logString);
    }

    protected CommonLogModel commonLogModel(Map<String, Object> logMap) {
        return new CommonLogModel(logMap);
    }

}
