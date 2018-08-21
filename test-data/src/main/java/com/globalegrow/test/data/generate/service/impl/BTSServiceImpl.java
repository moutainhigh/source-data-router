package com.globalegrow.test.data.generate.service.impl;

import com.globalegrow.test.data.generate.service.IBTSService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("btsService")
public class BTSServiceImpl implements IBTSService {

	@Override
	public void extField(Map<String, String> map) {
		/**这里举例而已*/
		map.put("btsextfield", "kylin bts");
	}

}
