package com.globalegrow.bts.report;

import java.util.HashMap;
import java.util.Map;

public class QuickLoginReportQuotaModel {


    private Map<String, String> bts = new HashMap() {

        private static final long serialVersionUID = 3456809019097454769L;

        {
            put("planid", "_skip");
            put("versionid", "_skip");
            put("bucketid", "_skip");
        }
    };

    private String specimen = "_skip";

    private Long timestamp = 0L;
    private int shopping_car_pv=0;
    private String shopping_car_uv="_skip";
    private int check_out_pv=0;
    private String check_out_uv="_skip";
    private int login_pv=0;
    private String login_uv="_skip";
    private int register_pv=0;
    private String register_uv="_skip";
    private int tourist_click_pv=0;
    private String tourist_click_uv="_skip";
    private int order_confirm_pv=0;
    private String order_confirm_uv="_skip";
    private int placeorder_pv=0;
    private String placeorder_uv="_skip";

    public int getShopping_car_pv() {
        return shopping_car_pv;
    }
    public void setShopping_car_pv(int shopping_car_pv) {
        this.shopping_car_pv = shopping_car_pv;
    }

    public String getShopping_car_uv() {
        return shopping_car_uv;
    }

    public void setShopping_car_uv(String shopping_car_uv) {
        this.shopping_car_uv = shopping_car_uv;
    }

    public int getCheck_out_pv() {
        return check_out_pv;
    }

    public void setCheck_out_pv(int check_out_pv) {
        this.check_out_pv = check_out_pv;
    }

    public String getCheck_out_uv() {
        return check_out_uv;
    }

    public void setCheck_out_uv(String check_out_uv) {
        this.check_out_uv = check_out_uv;
    }

    public int getTourist_click_pv() {
        return tourist_click_pv;
    }

    public void setTourist_click_pv(int tourist_click_pv) {
        this.tourist_click_pv = tourist_click_pv;
    }

    public String getTourist_click_uv() {
        return tourist_click_uv;
    }

    public void setTourist_click_uv(String tourist_click_uv) {
        this.tourist_click_uv = tourist_click_uv;
    }

    public int getLogin_pv() {
        return login_pv;
    }

    public void setLogin_pv(int login_pv) {
        this.login_pv = login_pv;
    }

    public String getLogin_uv() {
        return login_uv;
    }

    public void setLogin_uv(String login_uv) {
        this.login_uv = login_uv;
    }

    public int getRegister_pv() {
        return register_pv;
    }

    public void setRegister_pv(int register_pv) {
        this.register_pv = register_pv;
    }

    public String getRegister_uv() {
        return register_uv;
    }

    public void setRegister_uv(String register_uv) {
        this.register_uv = register_uv;
    }

    public int getOrder_confirm_pv() {
        return order_confirm_pv;
    }

    public void setOrder_confirm_pv(int order_confirm_pv) {
        this.order_confirm_pv = order_confirm_pv;
    }

    public String getOrder_confirm_uv() {
        return order_confirm_uv;
    }

    public void setOrder_confirm_uv(String order_confirm_uv) {
        this.order_confirm_uv = order_confirm_uv;
    }

    public int getPlaceorder_pv() {
        return placeorder_pv;
    }

    public void setPlaceorder_pv(int placeorder_pv) {
        this.placeorder_pv = placeorder_pv;
    }

    public String getPlaceorder_uv() {
        return placeorder_uv;
    }

    public void setPlaceorder_uv(String placeorder_uv) {
        this.placeorder_uv = placeorder_uv;
    }

    public Map<String, String> getBts() {
        return bts;
    }

    public void setBts(Map<String, String> bts) {
        this.bts = bts;
    }

    public String getSpecimen() {
        return specimen;
    }

    public void setSpecimen(String specimen) {
        this.specimen = specimen;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
