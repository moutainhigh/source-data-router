package com.globalegrow.dy.zaful.app.user.feature.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.globalegrow.dy.controller.CommonController;
import com.globalegrow.dy.es.ElasticSearchRepository;
import com.globalegrow.dy.zaful.app.user.feature.dto.AppUserFeatureResponse;
import com.globalegrow.dy.zaful.app.user.feature.dto.AppUserLayerRequest;
import com.globalegrow.dy.zaful.app.user.feature.dto.AppUserLayerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "用户分层数据接口")
@RestController
@RequestMapping("zaful-app-user-layer")
@Data
@Slf4j
public class UserLayerController extends CommonController {

    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    private String zafulAppLayerIndex = "dy_zaful_app_user_layer";

    @ApiOperation(value = "根据 app 埋点 appsflyer_device_id 查询用户分层数据 ")
    @SentinelResource(value = "zaful_app_user_layer", blockHandler = "failed", fallback = "failed")
    @PostMapping(produces = "application/json;charset=UTF-8")
    public AppUserLayerResponse getUserLayer(@Validated @RequestBody AppUserLayerRequest request) {
        AppUserLayerResponse response = new AppUserLayerResponse();
        response.setData(this.elasticSearchRepository.getSingleFieldById(this.zafulAppLayerIndex, "user", request.getDevice_id(), "layer_type"));
        return response;
    }

    public AppUserLayerResponse failed() {
        AppUserLayerResponse response = new AppUserLayerResponse();
        response.setSuccess(false);
        return response;
    }

}
