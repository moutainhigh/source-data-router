package com.globalegrow.ips.bean;

public class WebCategory {

	private long webCateId;

	private long catId;

	private long catParentId;

	private String websiteCode;

	private String catName;

	private long catLevel;

	private long categoryStatus;

	public long getWebCateId() {
		return webCateId;
	}

	public void setWebCateId(long webCateId) {
		this.webCateId = webCateId;
	}

	public long getCatId() {
		return catId;
	}

	public void setCatId(long catId) {
		this.catId = catId;
	}

	public long getCatParentId() {
		return catParentId;
	}

	public void setCatParentId(long catParentId) {
		this.catParentId = catParentId;
	}

	public String getCatName() {
		return catName;
	}

	public void setCatName(String catName) {
		this.catName = catName;
	}

	public long getCatLevel() {
		return catLevel;
	}

	public void setCatLevel(long catLevel) {
		this.catLevel = catLevel;
	}

	public long getCategoryStatus() {
		return categoryStatus;
	}

	public void setCategoryStatus(long categoryStatus) {
		this.categoryStatus = categoryStatus;
	}

	public String getWebsiteCode() {
		return websiteCode;
	}

	public void setWebsiteCode(String websiteCode) {
		this.websiteCode = websiteCode;
	}

}
