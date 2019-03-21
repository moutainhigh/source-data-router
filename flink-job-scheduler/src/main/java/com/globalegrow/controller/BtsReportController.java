package com.globalegrow.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("bts-report")
@Data
@Slf4j
public class BtsReportController {




    /**
     * web 界面
     * @return
     */
    @GetMapping
    public String index(Model model) {
        return "index";
    }

    @PostMapping
    public String addBtsPlanId(String planId, String platform) {
        return "success";
    }

    @DeleteMapping
    public String removeBtsPlanId(String planId) {
        return "success";
    }



}
