package com.globalegrow.dy.zaful.app.user.feature.service.impl;

import com.globalegrow.dy.costants.DyConstants;
import com.globalegrow.dy.es.ElasticSearchRepository;
import com.globalegrow.dy.zaful.app.user.feature.dto.AppUserFeatureRequest;
import com.globalegrow.dy.zaful.app.user.feature.dto.AppUserFeatureResponse;
import com.globalegrow.dy.zaful.app.user.feature.service.AppUserFeatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AppUserFeatureServiceEsImpl implements AppUserFeatureService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    @Value("${app.es.user-feature.index:dy_app_&&_user_feature}")
    private String userFeatureIndexName;

    @Value("${app.es.user-feature.index-type:user_feature}")
    private String indexType;

    @Override
    public AppUserFeatureResponse appUserFeatures(AppUserFeatureRequest request) {
        AppUserFeatureResponse response = new AppUserFeatureResponse();
        this.logger.debug("入参请求：{}", request);

        response.setData(this.elasticSearchRepository.idInSearch(this.userFeatureIndexName.replace(DyConstants.ES_INDEX_NAME_SITE_SPLIT, request.getSite().toLowerCase()), this.indexType, request.getDevice_id()));

        return response;
    }
}
