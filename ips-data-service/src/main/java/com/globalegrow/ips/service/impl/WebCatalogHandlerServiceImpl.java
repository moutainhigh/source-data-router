package com.globalegrow.ips.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.globalegrow.ips.bean.WebCategory;
import com.globalegrow.ips.constants.IpsCatalogContant;
import com.globalegrow.ips.mapper.pub.WebCatMapper;
import com.globalegrow.ips.service.WebCatalogHandlerService;
import com.globalegrow.util.SpringRedisUtil;

@Service
public class WebCatalogHandlerServiceImpl implements WebCatalogHandlerService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${catalog.redis.expire.seconds}")
	private long expireSeconds;

	@Resource
	private WebCatMapper webCatMapper;
	
	@Override
	@Transactional(value = "pubTransactionManager", propagation = Propagation.REQUIRED)
	public void singleWebCatalogNameSaveRedis() {
		logger.info("singleWebCatalogNameSaveRedis beginnig!");
		int pstart = 0;
		int psize = 100;
		boolean isContinue = false;
		do {
			isContinue = false;
			List<WebCategory> webCategorys = webCatMapper.getWebCats(pstart, psize);
			if (webCategorys != null && !webCategorys.isEmpty()) {
				isContinue = true;
				for (WebCategory webCategory : webCategorys) {
					try {
						Long catalogId = webCategory.getCatId();
						String webSiteCode = webCategory.getWebsiteCode();
						String value = webCategory.getCatParentId() + "," + webCategory.getCatLevel() + ","
								+ webCategory.getCatName();
						SpringRedisUtil.put(IpsCatalogContant.WEB_CATALOG_ID_PREFIX + catalogId + webSiteCode, value,
								expireSeconds);
					} catch (Exception e) {
						logger.error("singleWebCatalogNameSaveRedis save fail!");
					}

				}
				pstart = pstart + psize;
			}
		} while (isContinue);

	}

	@Override
	@Transactional(value = "pubTransactionManager", propagation = Propagation.REQUIRED)
	public void webCatalogNameSaveRedis() {
		logger.info("webCatalogNameSaveRedis beginnig!");
		int pstart = 0;
		int psize = 50;
		boolean isContinue = false;
		do {
			isContinue = false;
			List<WebCategory> webCategorys = webCatMapper.getWebCats(pstart, psize);
			if (webCategorys != null && !webCategorys.isEmpty()) {
				isContinue = true;
				for (WebCategory webCategory : webCategorys) {
					try {
						Map<Integer, String> catNameMap = new HashMap<>();
						Long catalogId = webCategory.getCatId();
						String webSiteCode = webCategory.getWebsiteCode();
						Long catLevel = webCategory.getCatLevel();
						catNameMap.put(0, webCategory.getCatName());
						catNameMap = saveWebCatalogNameHandler(webCategory.getCatParentId(), catLevel, webSiteCode, catNameMap, 0);
						StringBuilder webCatName = new StringBuilder();
						int catNameSize = catNameMap.size();
						webCatName.append(catNameMap.get(catNameSize - 1));
						for (int i = catNameSize - 2; i >= 0; i--) {
							webCatName.append(",");
							webCatName.append(catNameMap.get(i));
						}
						logger.info("webCatalogNameSaveRedis :"+catalogId + webSiteCode);
						SpringRedisUtil.put(IpsCatalogContant.WEB_CATALOG_PATH_PREFIX + catalogId + webSiteCode,
								webCatName.toString(), expireSeconds);
					} catch (Exception e) {
						logger.error("webCatalogNameSaveRedis save fail!");
					}

				}
				pstart = pstart + psize;
			}
		} while (isContinue);

	}

	/**
	 * 递归获取网站分类名字
	 * 
	 * @param catParentId
	 * @param webSiteCode
	 * @param catNameMap
	 * @param key
	 * @return
	 */
	public Map<Integer, String> saveWebCatalogNameHandler(Long catParentId, Long catLevel, String webSiteCode,
			Map<Integer, String> catNameMap, Integer key) {
		key++;
		// 到达一级分类退出
		if (catLevel == 1) {
			return catNameMap;
		} else {
			String redisValue = SpringRedisUtil
					.getStringValue(IpsCatalogContant.WEB_CATALOG_ID_PREFIX + catParentId + webSiteCode);
			String[] redisValues = redisValue.split(",");
			catNameMap.put(key, redisValues[2]);
			return saveWebCatalogNameHandler(Long.valueOf(redisValues[0]), Long.valueOf(redisValues[1]), webSiteCode,
					catNameMap, key);
		}
	}
}
