package com.globalegrow.message.consumer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.globalegrow.bean.GoodsImgInfo;
import com.globalegrow.bean.GoodsInfo;
import com.globalegrow.bean.GoodsMustInfo;
import com.globalegrow.bean.ProductInfo;
import com.globalegrow.message.util.BeanMapper;
import com.globalegrow.message.util.GsonUtil;
import com.globalegrow.service.ProductService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

@Component
public class PdmDataBatchConsumer {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private BeanMapper beanMapper;

	@Autowired
	private ProductService productService;

	@Value("${save.db.sum}")
	private int sdm;

	private static final String P_INFO_MAP = "p_info_map";

	private volatile int sum = 0;

	private Map<String, List<ProductInfo>> productInfoMap = new ConcurrentHashMap<>();

	@RabbitListener(queues = "goodsInfo_DY", containerFactory = "myFactory")
	public void processMessage(Message message) {
		String msg = new String(message.getBody());
		GoodsInfo goodsInfo = null;
		GoodsMustInfo goodsMustInfo = null;
		ProductInfo productInfo = null;
		Gson gson = GsonUtil.getGson();
		try {
			goodsInfo = gson.fromJson(msg, GoodsInfo.class);
			productInfo = beanMapper.mapObjectByType(goodsInfo, ProductInfo.class);
//			logger.info("use goodsInfo and sku is:" + goodsInfo.getGoods_sn());
		} catch (JsonSyntaxException e) {
			goodsMustInfo = gson.fromJson(msg, GoodsMustInfo.class);
			productInfo = beanMapper.mapObjectByType(goodsMustInfo, ProductInfo.class);
		}
		if (goodsInfo != null) {
			Map<Integer, List<GoodsImgInfo>> goods_img = goodsInfo.getGoods_img();
			if (goods_img != null && !goods_img.isEmpty()) {
				for (Integer key : goods_img.keySet()) {
					List<GoodsImgInfo> goodsImgInfos = goods_img.get(key);
					for (GoodsImgInfo goodsImgInfo : goodsImgInfos) {
						if ("1".equals(goodsImgInfo.getImg_default())) {
							productInfo.setImg_original(goodsImgInfo.getImg_original());
							productInfo.setImg_height(goodsImgInfo.getImg_height());
							productInfo.setImg_width(goodsImgInfo.getImg_width());
							break;
						}
					}
					break;
				}
			}
		}
		if (productInfo != null) {
			String product_label = productInfo.getProduct_label();
			if ("0000014".equals(product_label)) {
				productInfo.setSell_out(1);
			} else {
				productInfo.setSell_out(2);
			}
			productInfo.setUpdateDate(new Date());
			if (StringUtils.isBlank(productInfo.getBrand_code())) {
				productInfo.setBrand_code("");
			}
			if (StringUtils.isBlank(productInfo.getColor())) {
				productInfo.setColor("");
			}
			if (StringUtils.isBlank(productInfo.getColor_cn())) {
				productInfo.setColor_cn("");
			}
			if (StringUtils.isBlank(productInfo.getSupplier_sn())) {
				productInfo.setSupplier_sn("");
			}
			if (StringUtils.isBlank(productInfo.getResearcher())) {
				productInfo.setResearcher("");
			}
			if (StringUtils.isBlank(productInfo.getShelf_time())) {
				productInfo.setShelf_time("");
			}
			if (StringUtils.isBlank(productInfo.getProduct_name())) {
				productInfo.setProduct_name("");
			}
			if (StringUtils.isBlank(productInfo.getSale_weight())) {
				productInfo.setSaleWeight(0.0);
			} else {
				productInfo.setSaleWeight(Double.valueOf(productInfo.getSale_weight()));
			}
			if (StringUtils.isBlank(productInfo.getPurchase_price())) {
				productInfo.setPurchasePrice(0.0);
			} else {
				productInfo.setPurchasePrice(Double.valueOf(productInfo.getPurchase_price()));
			}
			if (StringUtils.isBlank(productInfo.getImg_original())) {
				productInfo.setImg_original("");
			}
			if (productInfo.getCreate_time() == null) {
				productInfo.setCreate_time(new Date());
			}
			synchronized (this) {
				List<ProductInfo> proinfos = productInfoMap.get(P_INFO_MAP);
				if (proinfos == null) {
					proinfos = new ArrayList<>();
				}
				proinfos.add(productInfo);
				productInfoMap.put(P_INFO_MAP, proinfos);
				sum++;
				if (sum % sdm == 0) {
					logger.info("+++++++++sum+++++++++++" + sum);
					productService.saveProductInfoHandler(proinfos);
					List<ProductInfo> pinfos = new ArrayList<>();
					productInfoMap.put(P_INFO_MAP, pinfos);
				}

			}
		}

	}

}
