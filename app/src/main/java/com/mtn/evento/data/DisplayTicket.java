package com.mtn.evento.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class DisplayTicket implements Serializable {
    private Bitmap qrCode;
    private String name;
    private String transactionId;
    List<DisplayTicket> displayTickets;

    public List<DisplayTicket> getDisplayTickets() {
        return displayTickets;
    }

    public void setDisplayTickets(List<DisplayTicket> displayTickets) {
        this.displayTickets = displayTickets;
    }


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
