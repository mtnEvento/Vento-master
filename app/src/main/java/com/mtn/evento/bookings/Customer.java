package com.mtn.evento.bookings;

import java.io.Serializable;

/**
 * Created by Summy on 9/4/2017.
 */

public class Customer implements Serializable {
    private String name;
    private String email;
    private String mobileMobileNetwork;
    private String mobileMobileyNumber;
    private String mobileNumber;
    private String amount;
    private String itemDescription;

    public Customer(){}
    public String getMobileMobileNetwork() {
        return mobileMobileNetwork;
    }

    public void setMobileMobileNetwork(String mobileMobileNetwork) {
        this.mobileMobileNetwork = mobileMobileNetwork;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileMobileyNumber() {
        return mobileMobileyNumber;
    }

    public void setMobileMobileyNumber(String mobileMobileyNumber) {
        this.mobileMobileyNumber = mobileMobileyNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }
}
