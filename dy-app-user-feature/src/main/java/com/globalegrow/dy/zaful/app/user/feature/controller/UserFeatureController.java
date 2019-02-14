package com.globalegrow.dy.zaful.app.user.feature.controller;

import com.globalegrow.dy.controller.CommonController;
import com.globalegrow.dy.zaful.app.user.feature.dto.AppUserFeatureRequest;
import com.globalegrow.dy.zaful.app.user.feature.dto.AppUserFeatureResponse;
import com.globalegrow.dy.zaful.app.user.feature.service.AppUserFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("app-user-feature")
public class UserFeatureController extends CommonController {

    @Autowired
    private AppUserFeatureService zafulAppUserFeatureService;

    @PostMapping(produces = "application/json;charset=UTF-8")
    public AppUserFeatureResponse getUserFeature(@Validated @RequestBody AppUserFeatureRequest request) {
        return this.zafulAppUserFeatureService.appUserFeatures(request);
    }

}
