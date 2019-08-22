package com.globalegrow.burylogcollectservice.common.constant;

import java.util.Arrays;
import java.util.List;
/**
 * 常量类
 * @author dengpizheng
 *
 */
public final class GlobalConstants {
	/**
	 * 埋点分割符
	 */
	public static final String LOG_DIVISION = "\\^A\\^";
	/**
	 * 正则表达式 截取用户行为埋点值，对应url里面的参数对
	 */
	public static final String PARAM_FORMAT = "(.*?)=(.*?)&";
	/**
	 * 目前支持的参数范围，多出来的暂时抛弃，减少数据量
	 */
	public final static List<String> LOG_FIELD_LIST= Arrays.asList(
			"glb_olk",//linkid
			"glb_od",//cookie id
			"glb_osr",//外部来源和着陆页详情url组合
			"glb_oi", //用户会话id
			"glb_u", //user_id
			"glb_d", //域名对应的编号
			"glb_b", //页面大类
			"glb_s", //页面小类
			"glb_p", //页面编码
			"glb_t", //行为类型 fv是首次访问，ic是点击，ie是曝光
			"glb_ubcta", //子事件属性
			"glb_x", //子事件详情
			"glb_tm",//当前时间戳
			"glb_w", //页面停留时间
			"glb_cl",//当前页面url
			"glb_dc",//国家对应的编号
			"glb_skuinfo",//商品详情
			"glb_skuinfos",//
			"glb_pl",//上个页面的url
			"glb_pm", //页面模块
			"glb_sc", //搜索分类
			"glb_sckw",//搜索结果词
			"glb_siws",//搜索输入词
			"glb_sk", //搜索类型
			"glb_sl", //搜索点击位置
			"glb_ksku", //页面sku
			"glb_filter",//页面的商品展示数量、页码、排序
			"glb_pagemodule",//活动模板
			"glb_k",//仓库信息
			"glb_plf",//记录不同平台的数据，区分PC和M版的数据
			"glb_osr_referrer",//
			"glb_osr_landing",//
			"glb_bts"
			);
	/**
	 * 双引号
	 */
    public static final String QUOTE_X22 = "\\x22";	
	public static final String QUOTE = "\"";
	/**
	 * sessionID
	 */
	public static final String SESSION_ID = "glb_oi";
	/**
	 * cookie ID
	 */
	public static final String COOKIE_ID = "glb_od";
	/**
	 * 页面大类
	 */
	public static final String PAGE_BIG_CATEGORY = "glb_b";
	/**
	 * 国家编号
	 */
	public static final String COUTRY_NO = "glb_dc";
	
	/**
	 * 平台类别
	 */
	public static final String 	PLATFORM_TYPE = "glb_plf";
	
	/**
	 * 行为类型，fv是首次访问，ic是点击，ie是曝光
	 */
	public static final String BEHAVIOR_TYPE = "glb_t";
	/**
	 * 点击事件
	 */
	public static final String CLICK_EVENT = "ic";
	/**
	 * UTF8编码
	 */
	public static final String UTF_8 = "UTF-8";
	/**
	 * 来源域名编码
	 */
	public static final String SOURCE_SITE_CODE = "glb_d";
	/**
	 * zaful域名编码
	 */
	public static final String ZAFUL_CODE = "10013";
}
