package com.globalegrow.burylogcollectservice.service.impl;

import com.globalegrow.burylogcollectservice.common.constant.GlobalConstants;
import com.globalegrow.burylogcollectservice.service.IBTSService;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("btsService")
public class BTSServiceImpl implements IBTSService {

	@Override
	public void extField(Map<String, String> map) {
		if (MapUtils.isNotEmpty(map)) {
			String value = map.get(GlobalConstants.SOURCE_SITE_CODE);
			if (GlobalConstants.ZAFUL_CODE.equals(value)) {
				/** 这里举例而已 */
				map.put("btsextfield", "kylin bts");	
			}else {
				map.clear();			
			}
		}
	}

}
