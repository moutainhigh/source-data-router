package com.globalegrow.dy.zaful.app.user.feature.dto;

import com.globalegrow.dy.dto.DyRequest;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户基本信息与广告特征信息批量获取
 */
public class AppUserFeatureRequest extends DyRequest {

    @NotBlank(message = "站点不能为空")
    @Length(min = 1, max = 256, message = "站点名长度必须在 0-256 之间")
    private String site;

    @Size(min = 1, max = 1000, message = "用户 id 数量必须在 1-1000")
    private List<String> device_id = new ArrayList<>();

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public List<String> getDevice_id() {
        return device_id;
    }

    public void setDevice_id(List<String> device_id) {
        this.device_id = device_id;
    }
}
