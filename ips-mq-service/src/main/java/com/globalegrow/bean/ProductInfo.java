package com.globalegrow.bean;

import java.util.Date;

public class ProductInfo extends GoodsMustInfo{
	
	private Long id;

    private String img_original;
	
	private String img_width;
	
	private String img_height;
	
	private Integer sell_out;
	
	private Date updateDate;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getSell_out() {
		return sell_out;
	}

	public void setSell_out(Integer sell_out) {
		this.sell_out = sell_out;
	}

	public String getImg_original() {
		return img_original;
	}

	public void setImg_original(String img_original) {
		this.img_original = img_original;
	}

	public String getImg_width() {
		return img_width;
	}

	public void setImg_width(String img_width) {
		this.img_width = img_width;
	}

	public String getImg_height() {
		return img_height;
	}

	public void setImg_height(String img_height) {
		this.img_height = img_height;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	

}
