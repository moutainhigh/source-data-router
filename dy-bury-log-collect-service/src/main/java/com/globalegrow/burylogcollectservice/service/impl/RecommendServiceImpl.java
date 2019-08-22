package com.globalegrow.burylogcollectservice.service.impl;

import com.globalegrow.burylogcollectservice.common.constant.GlobalConstants;
import com.globalegrow.burylogcollectservice.service.IRecommendService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("recommendService")
public class RecommendServiceImpl implements IRecommendService {

	@Override
	public void extField(Map<String, Object> map) {
		if (MapUtils.isNotEmpty(map)) {
			String value = (String) map.get(GlobalConstants.SOURCE_SITE_CODE);
			if (GlobalConstants.ZAFUL_CODE.equals(value)) {
				Integer isClick = 0;
				Integer isPv = 0;
				Integer isUv = 0;
				Integer isExposure = 0;
				Integer isCart = 0;
				Integer isCollect = 0;
				Integer isOrder = 0;
				Integer isPurchase = 0;
				Integer isPayAmount = 0;
				Integer isLpOrder = 0;
				String glbT = (String) map.get("glb_t");
				String glbPm = (String) map.get("glb_pm");
				String glbX = (String) map.get("glb_x");
				String glbU = (String) map.get("glb_u");
				String glbFmd = (String) map.get("glb_fmd");
				String glbDc = (String) map.get("glb_dc");
//				String glbFilterSort = (String) map.get("glb_filter_sort");
				String glbUbctaSckw = (String) map.get("glb_ubcta_sckw");
				String glbSkuinfo = String.valueOf(map.get("glb_skuinfo"));
				glbSkuinfo = "null".equals(glbSkuinfo) ? "" : glbSkuinfo;
				String glbUbcta = String.valueOf(map.get("glb_ubcta"));
				glbUbcta = "null".equals(glbUbcta) ? "" : glbUbcta;
				// glb_t = 'ic' AND glb_pm = 'mr' AND glb_x = 'sku' AND glb_skuinfo <> '' AND
				// glb_ubcta <> ''
				if ("ic".equals(glbT) && "mr".equals(glbPm) && "sku".equals(glbX) && StringUtils.isNotBlank(glbSkuinfo)
						&& StringUtils.isNotBlank(glbUbcta)) {
					isClick = 1;
				}
				// glb_t = 'ie' AND glb_ubcta = ''
				// glb_t = 'ie' AND glb_ubcta = ''
				if ("ie".equals(glbT) && StringUtils.isBlank(glbUbcta)) {
					isPv = 1;
					isUv = 1;
				}
				// glb_t = 'ie' AND glb_pm = 'mr' AND glb_ubcta <> ''
				if ("ie".equals(glbT) && "mr".equals(glbPm) && StringUtils.isNotBlank(glbUbcta)) {
					isExposure = 1;
				}
				// glb_t = 'ic' AND glb_x = 'ADT'
				if ("ic".equals(glbT) && "ADT".equals(glbX)) {
					isCart = 1;
				}
				// glb_t = 'ic' AND glb_x = 'ADF' AND glb_u <> ''
				if ("ic".equals(glbT) && "ADF".equals(glbX) && StringUtils.isNotBlank(glbU)) {
					isCollect = 1;
				}
				// glb_t = 'ic' AND glb_x = 'ADT' AND glb_skuinfo <> '' AND glb_ubcta <> ''
				if ("ic".equals(glbT) && "ADT".equals(glbX) && StringUtils.isNotBlank(glbSkuinfo)
						&& StringUtils.isNotBlank(glbUbcta)) {
					isOrder = 1;
					isPurchase = 1;
					isPayAmount = 1;
				}
				// glb_t = 'ic' AND glb_x = 'ADT' AND glb_fmd = 'mp' AND glb_skuinfo <> '' AND
				// glb_ubcta <> '' AND glb_ubcta_sckw is null and glb_dc ='1301'
				if ("ic".equals(glbT) && "ADT".equals(glbX) && "mp".equals(glbFmd) && "1301".equals(glbDc)
						&& StringUtils.isNotBlank(glbSkuinfo)&& StringUtils.isNotBlank(glbUbcta) && StringUtils.isBlank(glbUbctaSckw)) {
					isLpOrder = 1;
				}
				map.put("is_lp_order", isLpOrder);
				map.put("is_click", isClick);
				map.put("is_pv", isPv);
				map.put("is_uv", isUv);
				map.put("is_exposure", isExposure);
				map.put("is_cart", isCart);
				map.put("is_collect", isCollect);
				map.put("is_order", isOrder);
				map.put("is_purchase", isPurchase);
				map.put("is_pay_amount", isPayAmount);
				//减少放入kafka数据
				this.dataReduce(map);
			} else {
				map.clear();
			}
		}
	}
	/**
	 * 减少放入kafka数据
	 * @param logMap
	 */
	private void dataReduce(Map<String, Object> logMap) {
		logMap.put("glb_osr_referrer", "");
		logMap.put("glb_osr_landing", "");
		logMap.put("body_bytes_sent", "");
		logMap.put("glb_w", "");
		logMap.put("glb_filter", "");
		logMap.put("remote_user", "");
		logMap.put("glb_osr", "");	
		logMap.put("glb_pagemodule", "");
		logMap.put("glb_ksku", "");
		logMap.put("glb_oi", "");
		logMap.put("remote_addr", "");
		logMap.put("glb_cl", "");
		logMap.put("time_local", "");
		logMap.put("glb_siws", "");
		logMap.put("glb_pl", "");
		logMap.put("http_user_agent", "");
		logMap.put("http_true_client_ip", "");
		logMap.put("http_accept_language", "");
		logMap.put("http_referer", "");
		logMap.put("http_x_forwarded_for", "");
	}
}
