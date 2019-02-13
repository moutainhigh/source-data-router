package com.globalegrow.dy.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.globalegrow.dy.dto.*;
import com.globalegrow.dy.enums.ResponseCodeEnum;
import com.globalegrow.dy.service.GoodsReportService;
import com.globalegrow.dy.service.ListPageReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("report/dy")
public class KylinDyReportController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ListPageReportService listPageReportService;

    @Autowired
    private GoodsReportService goodsReportService;


    @SentinelResource(value = "listpage_report",blockHandler = "listPageExceptionHandler",fallback = "listPageFallbackMethod")
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

    public ResponseDTO<ReportPageDto> listPageFallbackMethod(ListPageReportParameterDto listPageReportParameterDto, BindingResult bindingResult) {
        logger.warn("服务超时或繁忙");
        ResponseDTO<ReportPageDto> result = new ResponseDTO<>();
        result.setMessage("服务超时或繁忙");
        result.setCode(ResponseCodeEnum.FAIL.getCode());
        return result;
    }

    public ResponseDTO<ReportPageDto> listPageExceptionHandler(ListPageReportParameterDto listPageReportParameterDto, BindingResult bindingResult,BlockException ex) {
        logger.warn("服务超时或繁忙" + listPageReportParameterDto.toString());
        ResponseDTO<ReportPageDto> result = new ResponseDTO<>();
        result.setMessage("服务超时或繁忙");
        result.setCode(ResponseCodeEnum.FAIL.getCode());
        return result;
    }

    @SentinelResource(value = "goods_report",blockHandler = "goodsExceptionHandler",fallback = "goodsFallbackMethod")
    @RequestMapping(value = "goods", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
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

    public ResponseDTO<ReportPageDto> goodsFallbackMethod(GoodsReportParameterDto goodsReportParameterDto, BindingResult bindingResult) {
        logger.warn("服务超时或繁忙");
        ResponseDTO<ReportPageDto> result = new ResponseDTO<>();
        result.setMessage("服务超时或繁忙");
        result.setCode(ResponseCodeEnum.FAIL.getCode());
        return result;
    }

    @ExceptionHandler({Exception.class})
    public ResponseDTO<ReportPageDto> databaseError(HttpServletRequest req, Exception e) {
        logger.error("调用异常，入口：" + req.getRequestURI(), e);
        ResponseDTO<ReportPageDto> result = new ResponseDTO<>();
        result.setCode(ResponseCodeEnum.FAIL.getCode());
        result.setMessage(e.getMessage());
        return result;
    }

    public ResponseDTO<ReportPageDto> goodsExceptionHandler(GoodsReportParameterDto goodsReportParameterDto, BindingResult bindingResult,BlockException ex) {
        logger.warn("服务超时或繁忙" + goodsReportParameterDto.toString());
        ResponseDTO<ReportPageDto> result = new ResponseDTO<>();
        result.setMessage("服务超时或繁忙");
        result.setCode(ResponseCodeEnum.FAIL.getCode());
        return result;
    }

}
