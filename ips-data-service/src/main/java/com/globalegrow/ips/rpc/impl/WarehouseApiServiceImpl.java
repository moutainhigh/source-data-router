package com.globalegrow.ips.rpc.impl;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.globalegrow.ips.bean.CdpWarehouseRet;
import com.globalegrow.ips.bean.PageData;
import com.globalegrow.ips.rpc.WarehouseApiService;
import com.globalegrow.ips.utils.EncryptUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;

@Service
public class WarehouseApiServiceImpl implements WarehouseApiService {
	
	private Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	private static final Client CLIENT = Client.create();

	@Value("${cdp.domain}")
	private String domain;

	@Override
	public CdpWarehouseRet getWarehouseInfos(int page, int per_page) {	
		PageData data = new PageData();
		data.setPage(page);
		data.setPer_page(per_page);
		String token = EncryptUtil
				.sign("de56be8fa19339d679efa6232455f342" + gson.toJson(data), "MD5");
		String resp = CLIENT.resource(domain).path("interface/warehouse/get").queryParam("sn", "ips")
				.queryParam("data", gson.toJson(data)).queryParam("token", token)
				.accept(MediaType.APPLICATION_JSON_TYPE).post(String.class);
		return gson.fromJson(resp, new TypeToken<CdpWarehouseRet>() {
		}.getType());
	}

}
