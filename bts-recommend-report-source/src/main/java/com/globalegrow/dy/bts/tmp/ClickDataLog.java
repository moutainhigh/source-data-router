package com.globalegrow.dy.bts.tmp;

import com.globalegrow.util.GsonUtil;
import com.globalegrow.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClickDataLog {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void logData(Object o) throws Exception {

        this.logger.info("{}", JacksonUtil.toJSon(o));

    }

}
