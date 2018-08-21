package com.globalegrow.ips.bean;

import java.util.List;

public class CdpWarehouseRet {

	private String total;

	private int page;

	private int per_page;

	private List<WarehouseInfo> list;

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPer_page() {
		return per_page;
	}

	public void setPer_page(int per_page) {
		this.per_page = per_page;
	}

	public List<WarehouseInfo> getList() {
		return list;
	}

	public void setList(List<WarehouseInfo> list) {
		this.list = list;
	}

}
