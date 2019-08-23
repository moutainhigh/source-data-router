package com.globalegrow.dyCubeBuildService.thread;

import com.globalegrow.dyCubeBuildService.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class CubeBuildThread implements Runnable {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String cubeUrl;

    private String kylinUsername;

    private String kylinPassword;

    private Long sourceOffsetStart = 0L;

    private Long sourceOffsetEnd = 9223372036854775807L;

    private RestTemplate restTemplate;

    public CubeBuildThread(String cubeUrl, String kylinUsername, String kylinPassword, RestTemplate restTemplate) {
        this.cubeUrl = cubeUrl;
        this.kylinUsername = kylinUsername;
        this.kylinPassword = kylinPassword;
        this.restTemplate = restTemplate;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        String base64up = new BASE64Encoder().encode((kylinUsername + ":" + kylinPassword).getBytes());
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        requestHeaders.add("Authorization", "Basic " + base64up);

        Map<String, Object> params = new HashMap<String, Object>();
        //build2代表流式构建
        if (StringUtils.isNotBlank(cubeUrl) && cubeUrl.contains("build2")) {
            params.put("sourceOffsetStart", sourceOffsetStart);
            params.put("sourceOffsetEnd", sourceOffsetEnd);
        } else {
            //build代表正常构建
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Long startTime = DateUtils.parseDate(DateUtils.formatDate() + " 00:00:00").getTime();
            Long endTime = DateUtils.parseDate(DateUtils.formatDate(1) + " 00:00:00").getTime();
            params.put("startTime", startTime);
            params.put("endTime", endTime);
        }
        params.put("buildType", "BUILD");
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<Map<String, Object>>(params, requestHeaders);
        try {
            logger.info("cubeBuild,url:{}", cubeUrl);
            restTemplate.exchange(cubeUrl, HttpMethod.PUT, requestEntity, String.class);
        } catch (Exception e) {
            logger.error("cube build submit error,{}", cubeUrl, e);
        }
    }

    public String getCubeUrl() {
        return cubeUrl;
    }

    public void setCubeUrl(String cubeUrl) {
        this.cubeUrl = cubeUrl;
    }

    public String getKylinUsername() {
        return kylinUsername;
    }

    public void setKylinUsername(String kylinUsername) {
        this.kylinUsername = kylinUsername;
    }

    public String getKylinPassword() {
        return kylinPassword;
    }

    public void setKylinPassword(String kylinPassword) {
        this.kylinPassword = kylinPassword;
    }

    public Long getSourceOffsetStart() {
        return sourceOffsetStart;
    }

    public void setSourceOffsetStart(Long sourceOffsetStart) {
        this.sourceOffsetStart = sourceOffsetStart;
    }

    public Long getSourceOffsetEnd() {
        return sourceOffsetEnd;
    }

    public void setSourceOffsetEnd(Long sourceOffsetEnd) {
        this.sourceOffsetEnd = sourceOffsetEnd;
    }
}
