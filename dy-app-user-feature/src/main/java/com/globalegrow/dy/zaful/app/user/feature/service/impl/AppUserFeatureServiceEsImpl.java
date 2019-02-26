package com.globalegrow.dy.zaful.app.user.feature.service.impl;

import com.globalegrow.dy.costants.DyConstants;
import com.globalegrow.dy.es.ElasticSearchRepository;
import com.globalegrow.dy.zaful.app.user.feature.dto.AppUserFeatureRequest;
import com.globalegrow.dy.zaful.app.user.feature.dto.AppUserFeatureResponse;
import com.globalegrow.dy.zaful.app.user.feature.service.AppUserFeatureService;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Data
@Service
@Slf4j
public class AppUserFeatureServiceEsImpl implements AppUserFeatureService {

    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    @Value("${app.es.user-feature.index:dy_app_&&_user_feature}")
    private String userFeatureIndexName;

    @Value("${app.es.user-feature.index-type:user_feature}")
    private String indexType;

    @Override
    public AppUserFeatureResponse appUserFeatures(AppUserFeatureRequest request) {
        AppUserFeatureResponse response = new AppUserFeatureResponse();
        log.debug("入参请求：{}", request);

        response.setData(this.elasticSearchRepository.idInSearch(this.userFeatureIndexName.replace(DyConstants.ES_INDEX_NAME_SITE_SPLIT, request.getSite().toLowerCase()), this.indexType, request.getDevice_id(), 1000));

        return response;
    }
}
