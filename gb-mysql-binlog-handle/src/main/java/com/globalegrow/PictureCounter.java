package com.globalegrow;


import com.globalegrow.bts.model.BtsReport;

import java.io.Serializable;

public class PictureCounter extends BtsReport implements Serializable {

    private static final long serialVersionUID = -5079190820997027855L;
    private int pv = 0;

    private int addCart = 0;

    private int skuOrder = 0;

    private int paidOrder = 0;

    private int amount = 0;

    public int getPv() {
        return pv;
    }

    public void setPv(int pv) {
        this.pv = pv;
    }

    public int getAddCart() {
        return addCart;
    }

    public void setAddCart(int addCart) {
        this.addCart = addCart;
    }

    public int getSkuOrder() {
        return skuOrder;
    }

    public void setSkuOrder(int skuOrder) {
        this.skuOrder = skuOrder;
    }

    public int getPaidOrder() {
        return paidOrder;
    }

    public void setPaidOrder(int paidOrder) {
        this.paidOrder = paidOrder;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
