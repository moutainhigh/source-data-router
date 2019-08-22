package com.globalegrow.burypointcollect.service.impl;

import com.globalegrow.burypointcollect.bean.KylinBuildTaskBean;
import com.globalegrow.burypointcollect.bean.ResponseDTO;
import com.globalegrow.burypointcollect.common.config.ResponseCodeEnum;
import com.globalegrow.burypointcollect.exception.BusinessException;
import com.globalegrow.burypointcollect.mapper.KylinBuildTaskMapper;
import com.globalegrow.burypointcollect.service.KylinBuildTaskService;
import com.globalegrow.burypointcollect.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

import java.util.*;

@Service
public class KylinBuildTaskServiceImpl implements KylinBuildTaskService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Long sourceOffsetStart = 0L;

    private Long sourceOffsetEnd = 9223372036854775807L;

    @Autowired
    private RestTemplate restTemplate;

	@Autowired
	private KylinBuildTaskMapper kylinBuildTaskMapper;
	@Value("${kylin.url:http://172.31.25.19:7070}")
	String url;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateOrAddBuildTask(String cubeUrl, String taskName, String triggerCron) {
		KylinBuildTaskBean kylinBuildTask = kylinBuildTaskMapper.getKylinBuildTask(taskName);
		if(kylinBuildTask==null) {
			kylinBuildTask =new KylinBuildTaskBean();
			kylinBuildTask.setCubeUrl(cubeUrl);
			kylinBuildTask.setTaskName(taskName);
			kylinBuildTask.setTriggerCron(triggerCron);
			kylinBuildTask.setUpdateTime(new Date());
			kylinBuildTaskMapper.saveKylinBuildTask(kylinBuildTask);
		}else {
			kylinBuildTask.setCubeUrl(cubeUrl);
			kylinBuildTask.setTaskName(taskName);
			kylinBuildTask.setTriggerCron(triggerCron);
			kylinBuildTask.setUpdateTime(new Date());
			kylinBuildTaskMapper.updateKylinBuildTask(kylinBuildTask);
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<KylinBuildTaskBean> getKylinBuildTasks(Integer page, Integer size) {
		// TODO Auto-generated method stub
		return kylinBuildTaskMapper.getKylinBuildTasks(size * (page - 1), size);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public KylinBuildTaskBean getKylinBuildTask(String taskName) {
		// TODO Auto-generated method stub
		return kylinBuildTaskMapper.getKylinBuildTask(taskName);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateKylinBuildTask(KylinBuildTaskBean kylinBuildTaskBean) {
		// TODO Auto-generated method stub
		kylinBuildTaskMapper.updateKylinBuildTask(kylinBuildTaskBean);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveKylinBuildTask(KylinBuildTaskBean kylinBuildTaskBean) {
		// TODO Auto-generated method stub
		kylinBuildTaskMapper.saveKylinBuildTask(kylinBuildTaskBean);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteKylinBuildTaskByTaskName(String taskName) {
		kylinBuildTaskMapper.deleteKylinBuildTaskByTaskName(taskName);
	}


    @Override
    public ResponseDTO<String> manualBuildTask(String taskName, String type, String startTime, String endTime) {
        ResponseDTO<String> result = new ResponseDTO<String>();
	    if(!"1".equals(type)&&!"2".equals(type)){
            result.setCode(ResponseCodeEnum.TYPE_VALUE_ERROR.getCode());
            result.setMessage(ResponseCodeEnum.TYPE_VALUE_ERROR.getMessage());
            return result;
        }
        if("2".equals(type)&&(StringUtils.isBlank(startTime)||StringUtils.isBlank(endTime))){
            result.setCode(ResponseCodeEnum.TIME_NULL_ERROR.getCode());
            result.setMessage(ResponseCodeEnum.TIME_NULL_ERROR.getMessage());
            return result;
        }else if("2".equals(type)&&(!DateUtils.isValidDate(startTime,"yyyy-MM-dd HH:mm:ss")||!DateUtils.isValidDate(endTime,"yyyy-MM-dd HH:mm:ss"))){
            result.setCode(ResponseCodeEnum.DATE_FORMAT_ERROR.getCode());
            result.setMessage(ResponseCodeEnum.DATE_FORMAT_ERROR.getMessage());
            return result;
        }
        String kylinUsername = "ADMIN";
        String kylinPassword = "Dy@ai2018";
//        String url = "http://52.20.83.154:7070";
 //       String url = "http://172.31.25.19:7070";
        String cubeUrl = url+"/kylin/api/cubes/" + taskName + "/build2";
        if("2".equals(type)){
            cubeUrl = url+ "/kylin/api/cubes/" + taskName + "/build";
        }

        String base64up = new BASE64Encoder().encode((kylinUsername + ":" + kylinPassword).getBytes());
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        requestHeaders.add("Authorization", "Basic " + base64up);

        Map<String, Object> params = new HashMap<String, Object>();
        if(StringUtils.isNotBlank(cubeUrl)&& cubeUrl.contains("build2")){
            params.put("sourceOffsetStart", sourceOffsetStart);
            params.put("sourceOffsetEnd", sourceOffsetEnd);

        }else{
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Long startTimeL = DateUtils.parseDate(startTime).getTime();
            Long endTimeL = DateUtils.parseDate(endTime).getTime();
            params.put("startTime", startTimeL);
            params.put("endTime", endTimeL);
        }
        params.put("buildType", "BUILD");
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<Map<String, Object>>(params, requestHeaders);
        try {
           logger.info("cubeBuild,url:{}", cubeUrl);
           restTemplate.exchange(cubeUrl, HttpMethod.PUT, requestEntity, String.class);
        } catch (Exception e) {
           logger.error("cube build submit error,{}", cubeUrl, e);
            throw new BusinessException(ResponseCodeEnum.FAIL.getCode(), ResponseCodeEnum.FAIL.getMessage()+",cube 构建任务提交失败:"+e.toString());
        }
        return result;
    }
}
