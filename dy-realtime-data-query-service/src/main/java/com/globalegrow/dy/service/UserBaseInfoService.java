package com.globalegrow.dy.service;

import com.globalegrow.dy.dto.UserBaseInfoRequest;
import com.globalegrow.dy.dto.UserBaseInfoResponse;

public interface UserBaseInfoService {

    UserBaseInfoResponse getUsersBaseInfo(UserBaseInfoRequest request);

}
