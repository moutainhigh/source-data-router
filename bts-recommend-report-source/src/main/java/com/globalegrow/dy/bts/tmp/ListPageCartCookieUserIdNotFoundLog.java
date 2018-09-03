package com.globalegrow.dy.bts.tmp;

import com.globalegrow.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ListPageCartCookieUserIdNotFoundLog {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void logData(Object o) {

        this.logger.info("{}", o);

    }

}
