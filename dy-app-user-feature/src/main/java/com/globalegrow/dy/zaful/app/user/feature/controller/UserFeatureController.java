package com.globalegrow.dy.zaful.app.user.feature.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.globalegrow.dy.controller.CommonController;
import com.globalegrow.dy.zaful.app.user.feature.dto.AppUserFeatureRequest;
import com.globalegrow.dy.zaful.app.user.feature.dto.AppUserFeatureResponse;
import com.globalegrow.dy.zaful.app.user.feature.service.AppUserFeatureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "用户基本信息与 facebook 广告特征数据接口")
@RestController
@RequestMapping("app-user-feature")
@Slf4j
public class UserFeatureController extends CommonController {

    @Autowired
    private AppUserFeatureService zafulAppUserFeatureService;

    @ApiOperation(value = "根据用户 appsflyer_device_id 获取用户基本信息与 广告特征")
    @SentinelResource(value = "user_base_info_fbad_feature", blockHandler = "failed", fallback = "failed")
    @PostMapping(produces = "application/json;charset=UTF-8")
    public AppUserFeatureResponse getUserFeature(@Validated @RequestBody AppUserFeatureRequest request) {
        return this.zafulAppUserFeatureService.appUserFeatures(request);
    }

    public AppUserFeatureResponse failed() {
        AppUserFeatureResponse response = new AppUserFeatureResponse();
        response.setSuccess(false);
        return response;
    }

}
