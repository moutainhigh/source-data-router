package com.globalegrow.fixed.scheduler;

import com.globalegrow.dy.es.ElasticSearchRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ZafulGoodsInfoByDay {

    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

}