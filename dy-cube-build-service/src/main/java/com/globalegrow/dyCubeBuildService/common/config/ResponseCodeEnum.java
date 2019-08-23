package com.globalegrow.dyCubeBuildService.common.config;
/**
 * 返回操作码定义
 * @author dengpizheng
 *
 */
public enum ResponseCodeEnum {
	/**操作*/
	SUCCESS("200", "操作成功"),
	FAIL("500", "系统异常"),

	/**报表*/
	NOT_NUll_ERROR("10001","参数不能为空"),
	DATE_FORMAT_ERROR("10002","日期格式错误"),
	DATA_VALUE_ERROR("10003","分页数值不能小于零"),
	ID_VALUE_ERROR("10004","ID不能小于零"),

	TYPE_VALUE_ERROR("10005","TYPE必需为1或者2"),
	TIME_NULL_ERROR("10006","时间不能为空");
	/**
	 * 操作编码
	 */
	private String code;
	/**
	 * 操作消息
	 */
	private String message;

	private ResponseCodeEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
