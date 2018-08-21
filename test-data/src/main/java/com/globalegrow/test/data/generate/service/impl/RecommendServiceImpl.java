package com.globalegrow.test.data.generate.service.impl;

import com.globalegrow.test.data.generate.service.IRecommendService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("recommendService")
public class RecommendServiceImpl implements IRecommendService {

	@Override
	public void extField(Map<String, String> map) {
		/**这里举例而已**/
		map.put("recommandExtField", "kylin recommand");
	}

}
