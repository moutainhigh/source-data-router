package com.globalegrow.dy.controller;

import com.globalegrow.dy.dto.GoodsReportParameterDto;
import com.globalegrow.dy.dto.ListPageReportParameterDto;
import com.globalegrow.dy.dto.ReportPageDto;
import com.globalegrow.dy.dto.ResponseDTO;
import com.globalegrow.dy.service.GoodsReportService;
import com.globalegrow.dy.service.ListPageReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("report/dy")
public class KylinDyReportController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ListPageReportService listPageReportService;

    @Autowired
    private GoodsReportService goodsReportService;

    @RequestMapping(value = "listPage", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public ResponseDTO<ReportPageDto> listPageReport(@RequestBody @Validated ListPageReportParameterDto listPageReportParameterDto, BindingResult bindingResult) {
        this.logger.debug("报表请求参数: {}", listPageReportParameterDto);
        // 如果用户输入的值满足要求才能进行进一步操作
        if (bindingResult.hasErrors()) {
            ResponseDTO<ReportPageDto> result = new ResponseDTO<>();
            result.setCode("-1");
            // 获取验证信息
            result.setMessage(bindingResult.getFieldError().getDefaultMessage());
            return result;
        }
        ResponseDTO<ReportPageDto> result = new ResponseDTO<>();
        result.setData(this.listPageReportService.listPageReport(listPageReportParameterDto));
        return result;
    }

    @RequestMapping(value = "goodsReport", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public ResponseDTO<ReportPageDto> goodsReport(@RequestBody @Validated GoodsReportParameterDto goodsReportParameterDto, BindingResult bindingResult) {
        this.logger.debug("报表请求参数: {}", goodsReportParameterDto);
        // 如果用户输入的值满足要求才能进行进一步操作
        if (bindingResult.hasErrors()) {
            ResponseDTO<ReportPageDto> result = new ResponseDTO<>();
            result.setCode("-1");
            // 获取验证信息
            result.setMessage(bindingResult.getFieldError().getDefaultMessage());
            return result;
        }
        ResponseDTO<ReportPageDto> result = new ResponseDTO<>();
        result.setData(this.goodsReportService.goodsReport(goodsReportParameterDto));
        return result;
    }

}
