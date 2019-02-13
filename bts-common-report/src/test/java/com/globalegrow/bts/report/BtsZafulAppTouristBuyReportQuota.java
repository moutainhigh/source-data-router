package com.globalegrow.bts.report;

/**
 * @ClassName BtsZafulAppTouristBuyReportQuota
 * @Description zafulAPP游客购买报表指标
 * @Author zhangwenjie
 * @Date 2018/11/5 10:35
 * @Version 1.0
 */
public class BtsZafulAppTouristBuyReportQuota extends BtsReport {
    private int cart_pv=0;//购物车pv
    private String cart_uv=skip;//购物车uv
    private int checkout_btn_click_pv=0;//check out 按钮点击PV
    private String checkout_btn_click_uv=skip;//check out 按钮点击UV
    private int checkout_page_pv=0;//订单确认页PV
    private String checkout_page_uv=skip;//订单确认页UV
    private int create_order_success_pv=0;//生成订单PV
    private String create_order_success_uv=skip;//生成订单UV
    private int purchase_pv=0;//订单支付pv
    private String purchase_uv=skip;//订单支付uv

    public int getCart_pv() {
        return cart_pv;
    }

    public void setCart_pv(int cart_pv) {
        this.cart_pv = cart_pv;
    }

    public String getCart_uv() {
        return cart_uv;
    }

    public void setCart_uv(String cart_uv) {
        this.cart_uv = cart_uv;
    }

    public int getCheckout_btn_click_pv() {
        return checkout_btn_click_pv;
    }

    public void setCheckout_btn_click_pv(int checkout_btn_click_pv) {
        this.checkout_btn_click_pv = checkout_btn_click_pv;
    }

    public String getCheckout_btn_click_uv() {
        return checkout_btn_click_uv;
    }

    public void setCheckout_btn_click_uv(String checkout_btn_click_uv) {
        this.checkout_btn_click_uv = checkout_btn_click_uv;
    }

    public int getCheckout_page_pv() {
        return checkout_page_pv;
    }

    public void setCheckout_page_pv(int checkout_page_pv) {
        this.checkout_page_pv = checkout_page_pv;
    }

    public String getCheckout_page_uv() {
        return checkout_page_uv;
    }

    public void setCheckout_page_uv(String checkout_page_uv) {
        this.checkout_page_uv = checkout_page_uv;
    }

    public int getCreate_order_success_pv() {
        return create_order_success_pv;
    }

    public void setCreate_order_success_pv(int create_order_success_pv) {
        this.create_order_success_pv = create_order_success_pv;
    }

    public String getCreate_order_success_uv() {
        return create_order_success_uv;
    }

    public void setCreate_order_success_uv(String create_order_success_uv) {
        this.create_order_success_uv = create_order_success_uv;
    }

    public int getPurchase_pv() {
        return purchase_pv;
    }

    public void setPurchase_pv(int purchase_pv) {
        this.purchase_pv = purchase_pv;
    }

    public String getPurchase_uv() {
        return purchase_uv;
    }

    public void setPurchase_uv(String purchase_uv) {
        this.purchase_uv = purchase_uv;
    }
}
