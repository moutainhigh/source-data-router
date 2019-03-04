package com.globalegrow.fixed.scheduler;

import com.globalegrow.dy.es.ElasticSearchRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ZafulGoodsInfoByDay {

    private String goodsInfoFlinkJobByDay = "/usr/local/services/flink/flink-yarn/flink-1.5.0/bin/flink run -d -m yarn-cluster -yn 1 -yjm 1024 -ytm 1024 /usr/local/services/flink/zaful-goods-base-info-es-0.1.jar";

    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

}
