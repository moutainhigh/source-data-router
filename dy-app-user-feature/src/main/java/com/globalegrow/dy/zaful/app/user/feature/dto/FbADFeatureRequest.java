package com.globalegrow.dy.zaful.app.user.feature.dto;

import com.globalegrow.dy.dto.DyRequest;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 广告特征请求
 */
public class FbADFeatureRequest extends DyRequest {

    @NotBlank(message = "fb 广告 id 不能为空")
    @Length(min = 1, max = 256, message = "广告 id 长度必须在 0-256 之间")
    private String fb_adset_id;

    public String getFb_adset_id() {
        return fb_adset_id;
    }

    public void setFb_adset_id(String fb_adset_id) {
        this.fb_adset_id = fb_adset_id;
    }
}
