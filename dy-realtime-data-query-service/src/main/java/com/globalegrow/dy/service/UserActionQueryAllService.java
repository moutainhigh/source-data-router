package com.globalegrow.dy.service;

import com.globalegrow.dy.dto.UserActionQueryAllRequest;
import com.globalegrow.dy.dto.UserActionQueryAllResponse;

public interface UserActionQueryAllService {

    UserActionQueryAllResponse getAllUserActions(UserActionQueryAllRequest request);

}
