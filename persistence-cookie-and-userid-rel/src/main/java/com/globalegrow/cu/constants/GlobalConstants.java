package com.globalegrow.cu.constants;

import java.util.Arrays;
import java.util.List;
/**
 * 常量类
 * @author dengpizheng
 *
 */
public final class GlobalConstants {

	public static final String COLUMN_FAMILY="cookie_userid";
	
	public static final String CU_TABLE_NAME="dy_cookie_userid_rel";
	
	public static final String LOG_REDIS_KEY_START="dy_zaful_adt_";
	
	/**
	 * 目前支持的参数范围，多出来的暂时抛弃，减少数据量
	 */
	public final static List<String> LOG_FIELD_LIST= Arrays.asList(
			"glb_od",//cookie
			//"glb_u", //user_id
			"glb_b", //页面大类
			"geoip_country_name",//国家
			"stat_group_minutes",
			"glb_tm",//当前时间戳
			"glb_dc",//国家对应的编号
			"glb_plf",//记录不同平台的数据，区分PC和M版的数据
			"glb_mrlc",
		    "is_cart", 
		    "is_order", 
		    "is_purchase", 
		    "is_pay_amount", 
		    "glb_sku",
		    "glb_ubcta",
		    "glb_fmd",
		    "is_lp_order",
		    "glb_ubcta_sckw",
		    "glb_filter_sort",
		    "glb_s",
		    "glb_pm"
			);
	
}
