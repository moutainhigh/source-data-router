package com.globalegrow.dy.enums;
/**
 * 返回操作码定义
 * @author dengpizheng
 *
 */
public enum ResponseCodeEnum {
	/**操作*/
	SUCCESS("200", "操作成功"),
	FAIL("600", "系统异常"),

	/**报表*/
	NOT_NUll_ERROR("10001","参数不能为空"),
	DATE_FORMAT_ERROR("10002","日期格式错误"),
	DATA_VALUE_ERROR("10003","分页数值不能小于零"),
	ID_VALUE_ERROR("10004","ID不能小于零"),

	/**实验*/
	PLAN_NAME_EXIST("20001", "实验名称已存在"),
	PLAN_NAME_VALUE("20002","实验名称长度必须为1~50个字符"),
	PLAN_DESC_VALUE("20003", "实验信息长度不能超过50个字符"),
	PLAN_SERVICE_VALUE("20004", "服务参数值有误"),
	PLAN_BUCKET_VALUE("20005", "桶参数值有误"),
	PLAN_QUERY_PARAM_ERROR("20006", "查询参数错误"),
	/**用户管理*/
	USER_PWD_NOT_PASS("30001", "帐户或密码错误！"),
	OLD_PWD_NOT_PASS("30002", "原密码不正确"),
	USER_IS_EXIST("30003", "用户已存在"),
	NEED_RE_LOGIN("30004", "请重新登录"),
	NOT_PERMISSION("30005", "没有操作权限"),
	USER_NOT_FOUND("30006","用户不存在"),
	PASSWORD_IS_NOT_VALID("30007","新密码不符合要求"),
	PRODUCT_LINE_IS_NOT_VALID("30008","产品线数据错误"),
	ROLE_IS_NOT_VALID("30009","角色数据错误"),
	USERACCOUNT_IS_NOT_VALID("30010","账号不符合要求");

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
