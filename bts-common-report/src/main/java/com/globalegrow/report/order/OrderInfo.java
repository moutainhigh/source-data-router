package com.globalegrow.report.order;

import java.io.Serializable;

public class OrderInfo implements Serializable {

    public static final String ORDER_ID = "order_id";
    public static final String ORDER_STATUS = "order_status";
    public static final String USER_ID = "user_id";
    private static final long serialVersionUID = 6088046639090546149L;


    private String orderId;

    private String userId;

    private String orderStatus = "0";

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {

        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
