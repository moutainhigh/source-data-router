package com.globalegrow.ips.rpc;

import com.globalegrow.ips.bean.CdpWarehouseRet;

public interface WarehouseApiService {

	CdpWarehouseRet getWarehouseInfos(int page, int per_page);
}
