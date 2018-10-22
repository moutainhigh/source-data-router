package com.globalegrow.dy.enums;

/**
 * 查询日期类型枚举(0:近5天，1:当月，2:近两月，3:近三月，6:近六月)
 */
public enum SkuRequestTypeEnum {

	ZERO(0, "近5天"),
	ONE(1, "当月"),
	TWO(2,"近两月"),
	THREE(3,"近三月"),
	SIX(6,"近六月");

	private Integer code;

	private String value;

	private SkuRequestTypeEnum(Integer code, String value) {
		this.code = code;
		this.value = value;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
