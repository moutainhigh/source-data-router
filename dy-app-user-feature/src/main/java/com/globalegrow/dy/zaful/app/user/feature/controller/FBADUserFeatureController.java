package com.globalegrow.dy.zaful.app.user.feature.controller;

import com.globalegrow.dy.controller.CommonController;
import com.globalegrow.dy.zaful.app.user.feature.dto.FbADFeatureRequest;
import com.globalegrow.dy.zaful.app.user.feature.dto.FbADFeatureResponse;
import com.globalegrow.dy.zaful.app.user.feature.service.FBADUserFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("fb-ad-user-feature")
public class FBADUserFeatureController extends CommonController {

    @Autowired
    private FBADUserFeatureService fbadUserFeatureService;

    @PostMapping(produces = "application/json;charset=UTF-8")
    public FbADFeatureResponse getAdUserFeatureDataById(@Validated @RequestBody FbADFeatureRequest request){
        return this.fbadUserFeatureService.getAdUserFeatureDataById(request);
    }

}
