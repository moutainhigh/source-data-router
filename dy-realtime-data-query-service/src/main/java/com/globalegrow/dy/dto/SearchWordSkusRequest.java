package com.globalegrow.dy.dto;

import org.hibernate.validator.constraints.NotBlank;

public class SearchWordSkusRequest extends DyRequest{

    @NotBlank(message = "搜索词不能为空")
    private String word;

    @NotBlank(message = "站点不能为空")
    private String site;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
