package com.mtn.evento.data;

import java.io.Serializable;

/**
 * Created by Summy on 8/10/2017.
 */

public class SinglePurchaseData implements Serializable {
    private String type;
    private String quantity;
    private String price;


    public SinglePurchaseData() {
    }

    public SinglePurchaseData(String type, String quantity, String price) {
        this.type = type;
        this.quantity = quantity;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
