package com.globalegrow.bts.report;

import java.util.HashMap;
import java.util.Map;

public class DQuickLoginReportQuotaModel {


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
    private int car_login_pv=0;
    private String car_login_uv="_skip";
    private int login_pv=0;
    private String login_uv="_skip";
    private int car_register_pv=0;
    private String car_register_uv="_skip";
    private int register_pv=0;
    private String register_uv="_skip";
    private int car_fb_login_pv=0;
    private String car_fb_login_uv="_skip";
    private int fb_login_click_pv=0;
    private String fb_login_click_uv="_skip";
    private int car_google_login_pv=0;
    private String car_google_login_uv="_skip";
    private int google_login_click_pv=0;
    private String google_login_click_uv="_skip";
    private int car_forgot_pwd_pv=0;
    private String car_forgot_pwd_uv="_skip";
    private int forgot_pwd_pv=0;
    private String forgot_pwd_uv="_skip";

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

    public int getCar_login_pv() {
        return car_login_pv;
    }

    public void setCar_login_pv(int car_login_pv) {
        this.car_login_pv = car_login_pv;
    }

    public String getCar_login_uv() {
        return car_login_uv;
    }

    public void setCar_login_uv(String car_login_uv) {
        this.car_login_uv = car_login_uv;
    }

    public int getCar_register_pv() {
        return car_register_pv;
    }

    public void setCar_register_pv(int car_register_pv) {
        this.car_register_pv = car_register_pv;
    }

    public String getCar_register_uv() {
        return car_register_uv;
    }

    public void setCar_register_uv(String car_register_uv) {
        this.car_register_uv = car_register_uv;
    }

    public int getCar_fb_login_pv() {
        return car_fb_login_pv;
    }

    public void setCar_fb_login_pv(int car_fb_login_pv) {
        this.car_fb_login_pv = car_fb_login_pv;
    }

    public String getCar_fb_login_uv() {
        return car_fb_login_uv;
    }

    public void setCar_fb_login_uv(String car_fb_login_uv) {
        this.car_fb_login_uv = car_fb_login_uv;
    }

    public int getFb_login_click_pv() {
        return fb_login_click_pv;
    }

    public void setFb_login_click_pv(int fb_login_click_pv) {
        this.fb_login_click_pv = fb_login_click_pv;
    }

    public String getFb_login_click_uv() {
        return fb_login_click_uv;
    }

    public void setFb_login_click_uv(String fb_login_click_uv) {
        this.fb_login_click_uv = fb_login_click_uv;
    }

    public int getCar_google_login_pv() {
        return car_google_login_pv;
    }

    public void setCar_google_login_pv(int car_google_login_pv) {
        this.car_google_login_pv = car_google_login_pv;
    }

    public String getCar_google_login_uv() {
        return car_google_login_uv;
    }

    public void setCar_google_login_uv(String car_google_login_uv) {
        this.car_google_login_uv = car_google_login_uv;
    }

    public int getGoogle_login_click_pv() {
        return google_login_click_pv;
    }

    public void setGoogle_login_click_pv(int google_login_click_pv) {
        this.google_login_click_pv = google_login_click_pv;
    }

    public String getGoogle_login_click_uv() {
        return google_login_click_uv;
    }

    public void setGoogle_login_click_uv(String google_login_click_uv) {
        this.google_login_click_uv = google_login_click_uv;
    }

    public int getCar_forgot_pwd_pv() {
        return car_forgot_pwd_pv;
    }

    public void setCar_forgot_pwd_pv(int car_forgot_pwd_pv) {
        this.car_forgot_pwd_pv = car_forgot_pwd_pv;
    }

    public String getCar_forgot_pwd_uv() {
        return car_forgot_pwd_uv;
    }

    public void setCar_forgot_pwd_uv(String car_forgot_pwd_uv) {
        this.car_forgot_pwd_uv = car_forgot_pwd_uv;
    }

    public int getForgot_pwd_pv() {
        return forgot_pwd_pv;
    }

    public void setForgot_pwd_pv(int forgot_pwd_pv) {
        this.forgot_pwd_pv = forgot_pwd_pv;
    }

    public String getForgot_pwd_uv() {
        return forgot_pwd_uv;
    }

    public void setForgot_pwd_uv(String forgot_pwd_uv) {
        this.forgot_pwd_uv = forgot_pwd_uv;
    }
}
