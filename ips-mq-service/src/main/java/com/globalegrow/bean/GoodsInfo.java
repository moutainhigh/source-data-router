package com.globalegrow.bean;

import java.util.List;
import java.util.Map;

public class GoodsInfo extends GoodsMustInfo{

	private Map<Integer,List<GoodsImgInfo>> goods_img;

	public Map<Integer, List<GoodsImgInfo>> getGoods_img() {
		return goods_img;
	}

	public void setGoods_img(Map<Integer, List<GoodsImgInfo>> goods_img) {
		this.goods_img = goods_img;
	}

}
