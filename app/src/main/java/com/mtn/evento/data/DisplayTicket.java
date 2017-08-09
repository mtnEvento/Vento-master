package com.mtn.evento.data;

import android.graphics.Bitmap;


public class DisplayTicket {
    private Bitmap qrCode;
    private String name;
    private String transactionId;

    public Bitmap getQrCode() {
        return qrCode;
        
    }

    public void setQrCode(Bitmap qrCode) {
        this.qrCode = qrCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
