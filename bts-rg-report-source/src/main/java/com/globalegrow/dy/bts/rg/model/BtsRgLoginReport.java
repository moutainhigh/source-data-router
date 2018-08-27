package com.globalegrow.dy.bts.rg.model;

import com.globalegrow.bts.model.BtsReport;

public class BtsRgLoginReport extends BtsReport {

    private String pageUv = skip;

    private String signUpClickUv = skip;

    private String signInClickUv = skip;

    private String signInFbUv = skip;

    private String signInGoogleUv = skip;

    private String forgetPasswdClickUv = skip;

    public String getPageUv() {
        return pageUv;
    }

    public void setPageUv(String pageUv) {
        this.pageUv = pageUv;
    }

    public String getSignUpClickUv() {
        return signUpClickUv;
    }

    public void setSignUpClickUv(String signUpClickUv) {
        this.signUpClickUv = signUpClickUv;
    }

    public String getSignInClickUv() {
        return signInClickUv;
    }

    public void setSignInClickUv(String signInClickUv) {
        this.signInClickUv = signInClickUv;
    }

    public String getSignInFbUv() {
        return signInFbUv;
    }

    public void setSignInFbUv(String signInFbUv) {
        this.signInFbUv = signInFbUv;
    }

    public String getSignInGoogleUv() {
        return signInGoogleUv;
    }

    public void setSignInGoogleUv(String signInGoogleUv) {
        this.signInGoogleUv = signInGoogleUv;
    }

    public String getForgetPasswdClickUv() {
        return forgetPasswdClickUv;
    }

    public void setForgetPasswdClickUv(String forgetPasswdClickUv) {
        this.forgetPasswdClickUv = forgetPasswdClickUv;
    }
}
