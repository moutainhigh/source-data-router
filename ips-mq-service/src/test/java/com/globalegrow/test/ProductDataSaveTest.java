package com.globalegrow.test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.javassist.bytecode.analysis.Type;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.globalegrow.ServiceStart;
import com.globalegrow.bean.GoodsImgInfo;
import com.globalegrow.bean.GoodsInfo;
import com.globalegrow.bean.GoodsMustInfo;
import com.globalegrow.bean.ProductInfo;
import com.globalegrow.message.util.BeanMapper;
import com.globalegrow.message.util.GsonUtil;
import com.globalegrow.service.ProductService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceStart.class)
public class ProductDataSaveTest {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private BeanMapper beanMapper;

	@Autowired
	private ProductService productService;

//	@org.junit.Test
	public void test() {
//		String msg = "{\"goods_name\":\"7Pcs Cosmetic Foundation Brushes Tool Set\",\"shelf_time\":null,\"product_spec\":\"\",\"product_name\":\"\\u4e03\\u53ea\\u6e10\\u53d8\\u8272\\u5316\\u5986\\u5237\\u7ec4\\u5408\",\"product_sn\":\"2022553\",\"goods_sn\":\"202255301\",\"catalog_id\":\"80000\",\"company_catalog_id\":\"3095\",\"title\":\"7Pcs Cosmetic Foundation Brushes Tool Set\",\"sale_price_base\":\"32.44\",\"brand_code\":\"Ulefone\",\"create_user\":\"liule\",\"create_time\":null,\"researcher\":\"chenwen\",\"charger_spec\":\"1\",\"edit_status\":\"4\",\"is_price_limit\":\"0\",\"sale_price_limit\":\"0.00\",\"recommend_level\":\"b\",\"is_making\":\"0\",\"catalog_name\":\"\\u5316\\u5986\\u5de5\\u5177\",\"company_catalog_name\":\"\\u5316\\u5986\\u5237\",\"brand_name\":\"Ulefone\",\"brand_en_name\":\"\",\"department_code\":[{\"id\":\"9576073\",\"unit_code\":\"SYB0004\",\"type\":\"1\",\"researcher\":\"chenwen\"}],\"product_source\":\"1\",\"declare_name_cn\":\"\\u5316\\u5986\\u5de5\\u5177\",\"declare_name_en\":\"Makeup Tools\",\"custom_sn\":\"8203200000\",\"battery_type\":\"\",\"product_character_ids\":\"1\",\"battery_info\":\"\",\"declare_s_name\":\"77777\",\"hs_code\":\"88888\",\"supplier_sn\":\"3210998\",\"purchase_price\":\"22.00000\",\"purchase_price_base\":\"22.00000\",\"purchase_price_unit\":\"CNY\",\"goods_resource_status\":\"1\",\"purchaser\":\"xiangxiaoxia\",\"goods_length\":\"0.000\",\"goods_width\":\"0.000\",\"goods_height\":\"0.000\",\"goods_weight\":\"0.0050\",\"actual_weight\":\"0.0052\",\"sale_weight\":\"0.0052\",\"volume_weight\":\"0.0052\",\"goods_package_length\":\"1.000\",\"goods_package_width\":\"1.000\",\"goods_package_height\":\"1.000\",\"sale_package_length\":\"1.000\",\"sale_package_width\":\"1.000\",\"sale_package_height\":\"1.000\",\"size_chart\":{\"name\":null,\"unit\":null,\"col_info\":[],\"data_info\":[],\"max_row\":0,\"max_col\":0},\"relate_size_chart\":{\"name\":null,\"unit\":null,\"col_info\":[],\"data_info\":[],\"max_row\":0,\"max_col\":0},\"goods_attr\":{\"108\":{\"id\":null,\"name_cn\":null,\"name_en\":null,\"attrs\":[{\"id\":\"374\",\"name_cn\":\"\\u7c7b\\u578b\",\"name_en\":\"Category\",\"display_type\":\"2\",\"attrValues\":\"Makeup Brushes Set\"},{\"id\":\"304\",\"name_cn\":\"\\u5237\\u6bdb\\u6750\\u8d28\",\"name_en\":\"Brush Hair Material\",\"display_type\":\"1\",\"attrValues\":\"Synthetic Hair\"},{\"id\":\"748\",\"name_cn\":\"\\u7279\\u8272\",\"name_en\":\"Features\",\"display_type\":\"2\",\"attrValues\":\"Professional\"},{\"id\":\"1871\",\"name_cn\":\"\\u9002\\u7528\\u5b63\\u8282\",\"name_en\":\"Season\",\"display_type\":\"1\",\"attrValues\":\"Spring,Summer,Fall,Winter\"},{\"id\":\"2379\",\"name_cn\":\"\\u91cd\\u91cf\",\"name_en\":\"Weightss\",\"display_type\":\"4\",\"attrValues\":\"0.0052kg\"},{\"id\":\"1532\",\"name_cn\":\"\\u82f1\\u6587\\u5305\\u88c5\\u6e05\\u5355\",\"name_en\":\"Package Contents\",\"display_type\":\"4\",\"attrValues\":\"7Pcs x Makeup Brush\"}]}},\"goods_img\":{\"1\":[{\"thumb_url\":\"uploads\\/pdm-product-pic\\/Clothing\\/2018\\/06\\/27\\/thumb-img\\/1530062975991017373.jpg\",\"img_original\":\"uploads\\/pdm-product-pic\\/Clothing\\/2018\\/06\\/27\\/source-img\\/20180627172915_96411.JPG\",\"img_type\":\"1\",\"img_default\":1,\"img_desc\":\"\",\"img_width\":\"1430\",\"img_height\":\"1000\"},{\"thumb_url\":\"uploads\\/pdm-product-pic\\/Clothing\\/2018\\/06\\/27\\/thumb-img\\/1530062975822149520.jpg\",\"img_original\":\"uploads\\/pdm-product-pic\\/Clothing\\/2018\\/06\\/27\\/source-img\\/20180627172915_41577.JPG\",\"img_type\":\"1\",\"img_default\":0,\"img_desc\":\"\",\"img_width\":\"1430\",\"img_height\":\"1000\"},{\"thumb_url\":\"uploads\\/pdm-product-pic\\/Clothing\\/2018\\/06\\/27\\/thumb-img\\/1530062975976596451.jpg\",\"img_original\":\"uploads\\/pdm-product-pic\\/Clothing\\/2018\\/06\\/27\\/source-img\\/20180627172915_72797.JPG\",\"img_type\":\"1\",\"img_default\":0,\"img_desc\":\"\",\"img_width\":\"1430\",\"img_height\":\"1000\"},{\"thumb_url\":\"uploads\\/pdm-product-pic\\/Clothing\\/2018\\/06\\/27\\/thumb-img\\/1530062975330800778.jpg\",\"img_original\":\"uploads\\/pdm-product-pic\\/Clothing\\/2018\\/06\\/27\\/source-img\\/20180627172916_39424.JPG\",\"img_type\":\"1\",\"img_default\":0,\"img_desc\":\"\",\"img_width\":\"1430\",\"img_height\":\"1000\"}]},\"description\":\"\",\"feature\":\"<b>Feature:<\\/b><br \\/>- An ombre effect with a gradient from pink to light green<br \\/>-Seven makeup brushes, including foundation brush, powder brush, eyeshadow brush, eyebrow brush etc<br \\/>- Must-have daily cosmetic tools for girls or women to makeup\",\"researcher_leader\":\"\",\"color\":\"A1\",\"size\":\"\",\"color_cn\":\"\\u591a\\u8272\",\"size_cn\":\"\",\"sales_platform\":\"[]\",\"sales_area\":\"{\\\"select_type\\\":\\\"1\\\",\\\"country_acronym\\\":\\\"\\\"}\",\"platform_limit_price\":[],\"cooperation_type\":0,\"delivery_country_code\":\"\",\"delivery_country_name_cn\":\"\",\"delivery_country_name_en\":\"\",\"product_label\":\"\",\"id\":5160957}";
		String msg = "{\"goods_name\":\"7 inch Tablet\",\"shelf_time\":null,\"product_spec\":\"\",\"product_name\":\"\\u3010\\u76f4\\u4e0a\\u3011\\u6d4b\\u8bd5 7 \\u82f1\\u5bf8 wuke\\u5e73\\u677f\",\"product_sn\":\"2016666\",\"goods_sn\":\"201666602\",\"catalog_id\":\"1056\",\"company_catalog_id\":\"69\",\"title\":\"7 inch Tablet\",\"sale_price_base\":\"700.00\",\"brand_code\":\"Wuke\",\"create_user\":\"weinengyu\",\"create_time\":\"2018-03-12 10:25:38\",\"researcher\":\"weinengyu\",\"charger_spec\":\"1\",\"edit_status\":\"11\",\"is_price_limit\":\"0\",\"sale_price_limit\":\"0.00\",\"recommend_level\":\"g\",\"is_making\":\"0\",\"catalog_name\":\"7\\u82f1\\u5bf8\\u5e73\\u677f\",\"company_catalog_name\":\"\\u5e73\\u677f\\u7535\\u8111\",\"brand_name\":\"Wuke\",\"brand_en_name\":\"Wuke\",\"department_code\":[{\"id\":\"9562971\",\"unit_code\":\"SYB0002\",\"type\":\"1\",\"researcher\":\"weinengyu\"}],\"product_source\":\"4\",\"declare_name_cn\":\"7\\u82f1\\u5bf8\\u5e73\\u677f\",\"declare_name_en\":\"7 inch Tablet\",\"custom_sn\":\"8471301000\",\"battery_type\":\"PI966\",\"product_character_ids\":\"7\",\"battery_info\":\"[{\\\"battery_type\\\":\\\"PI966\\\",\\\"battery_info\\\":[{\\\"battery_brand\\\":\\\"5\\\",\\\"battery_voltage\\\":\\\"5\\\",\\\"battery_current\\\":\\\"5\\\",\\\"battery_power\\\":\\\"5\\\",\\\"battery_capacity\\\":\\\"5\\\",\\\"material\\\":\\\"5\\\",\\\"msds_cert\\\":\\\"5\\\",\\\"battery_component\\\":0,\\\"battery_component_other\\\":\\\"\\\",\\\"battery_image_type\\\":\\\"0\\\",\\\"battery_image\\\":[]}]}]\",\"declare_s_name\":\"33\",\"hs_code\":\"44\",\"supplier_sn\":\"LSP0013\",\"purchase_price\":\"700.00000\",\"purchase_price_base\":\"700.00000\",\"purchase_price_unit\":\"CNY\",\"goods_resource_status\":\"2\",\"purchaser\":\"chenjunjie\",\"goods_length\":\"12.000\",\"goods_width\":\"12.000\",\"goods_height\":\"35.000\",\"goods_weight\":\"1.4000\",\"actual_weight\":\"1.6000\",\"sale_weight\":\"1.6000\",\"volume_weight\":\"1.6650\",\"goods_package_length\":\"15.000\",\"goods_package_width\":\"15.000\",\"goods_package_height\":\"37.000\",\"sale_package_length\":\"15.000\",\"sale_package_width\":\"15.000\",\"sale_package_height\":\"37.000\",\"size_chart\":{\"name\":null,\"unit\":null,\"col_info\":[],\"data_info\":[],\"max_row\":0,\"max_col\":0},\"relate_size_chart\":{\"name\":null,\"unit\":null,\"col_info\":[],\"data_info\":[],\"max_row\":0,\"max_col\":0},\"goods_attr\":{\"10\":{\"id\":\"10\",\"name_cn\":\"\\u57fa\\u672c\\u5c5e\\u6027\",\"name_en\":\"Basic Information\"},\"123\":{\"id\":\"123\",\"name_cn\":\"\\u5bb9\\u91cf\",\"name_en\":\"Storage\"},\"81\":{\"id\":\"81\",\"name_cn\":\"\\u7f51\\u7edc\",\"name_en\":\"Network\"},\"37\":{\"id\":\"37\",\"name_cn\":\"\\u663e\\u793a\\u5668\",\"name_en\":\"Display\"},\"16\":{\"id\":\"16\",\"name_cn\":\"\\u76f8\\u673a\",\"name_en\":\"Camera\"},\"27\":{\"id\":\"27\",\"name_cn\":\"\\u901a\\u4fe1\\u8fde\\u63a5\",\"name_en\":\"Connectivity \"},\"55\":{\"id\":\"55\",\"name_cn\":\"\\u901a\\u7528\",\"name_en\":\"General\"},\"72\":{\"id\":\"72\",\"name_cn\":\"\\u591a\\u5a92\\u4f53\\u683c\\u5f0f\",\"name_en\":\"Media Formats \"},\"65\":{\"id\":\"65\",\"name_cn\":\"\\u8bed\\u8a00\",\"name_en\":\"Languages\"},\"4\":{\"id\":\"4\",\"name_cn\":\"\\u9644\\u52a0\\u529f\\u80fd\",\"name_en\":\"Additional Features\"},\"35\":{\"id\":\"35\",\"name_cn\":\"\\u89c4\\u683c\",\"name_en\":\"Dimensions\",\"attrs\":[{\"id\":\"1680\",\"name_cn\":\"\\u4ea7\\u54c1\\u5c3a\\u5bf8\",\"name_en\":\"Product size\",\"display_type\":\"4\",\"attrValues\":\"12.00 x 12.00 x 35.00 cm \\/ 4.72 x 4.72 x 13.78 inches\"},{\"id\":\"1536\",\"name_cn\":\"\\u5305\\u88c5\\u91cd\\u91cf\",\"name_en\":\"Package size\",\"display_type\":\"4\",\"attrValues\":\"15.00 x 15.00 x 37.00 cm \\/ 5.91 x 5.91 x 14.57 inches\"},{\"id\":\"1685\",\"name_cn\":\"\\u4ea7\\u54c1\\u91cd\\u91cf999\",\"name_en\":\"Product weight\",\"display_type\":\"4\",\"attrValues\":\"1.4000 kg\"}]},\"92\":{\"id\":\"92\",\"name_cn\":\"\\u5305\\u88c5\\u914d\\u4ef6\",\"name_en\":\"Package Contents\"}},\"goods_img\":[],\"description\":\"\",\"feature\":\"<B><span style=\\\"font-size:14px;\\\">Features:<\\/span><\\/B>\\r\\n- asdffg\\r\\n- sdfgggk\\r\\n- bnmjh\",\"researcher_leader\":\"\",\"color\":\"\\u9ed1\\u8272\",\"size\":\"UK Plug\",\"color_cn\":\"\\u9ed1\\u8272\",\"size_cn\":\"UK Plug\",\"sales_platform\":\"[]\",\"sales_area\":\"[]\",\"platform_limit_price\":[],\"cooperation_type\":3,\"delivery_country_code\":\"\",\"delivery_country_name_cn\":\"\",\"delivery_country_name_en\":\"\",\"product_label\":\"000005,0000097,0000098\",\"id\":1969912}";
		GoodsInfo goodsInfo = null;
		GoodsMustInfo goodsMustInfo = null;
		ProductInfo productInfo = null;
		Gson gson = GsonUtil.getGson();
		try {
			goodsInfo = gson.fromJson(msg, GoodsInfo.class);
			productInfo = beanMapper.mapObjectByType(goodsInfo, ProductInfo.class);
			logger.info("use goodsInfo and sku is:" + goodsInfo.getGoods_sn());
		} catch (JsonSyntaxException e) {
			goodsMustInfo = gson.fromJson(msg, GoodsMustInfo.class);
			productInfo = beanMapper.mapObjectByType(goodsMustInfo, ProductInfo.class);
			logger.info("use goodsMustInfo and sku is:" + goodsInfo.getGoods_sn());
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
			productService.saveProductInfoHandler(productInfo);
		}
	}
}
