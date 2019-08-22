package com.globalegrow.burypointcollect.bean;

import com.globalegrow.burypointcollect.common.config.ResponseCodeEnum;
import org.springframework.util.StringUtils;

import java.io.Serializable;

public class ResponseDTO<T> implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -4601125703145955374L;
	/**
	 * 返回代码 200：成功 其他：失败
	 */
	private String code="200";
	/**
	 * 提示信息
	 */
	private String message="调用成功";

	public ResponseDTO() {

	}

	public ResponseDTO(String code, String desc) {
		super();
		this.code = StringUtils.isEmpty(code)? ResponseCodeEnum.SUCCESS.getCode():code;
		this.message = StringUtils.isEmpty(desc)? ResponseCodeEnum.SUCCESS.getMessage():desc;
	}
	/**
     * &#x8fd4;&#x56de;&#x6570;&#x636e;
     */
	private T data;
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
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
}
