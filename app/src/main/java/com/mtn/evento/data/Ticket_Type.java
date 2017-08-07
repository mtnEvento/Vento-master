package com.mtn.evento.data;

import java.io.Serializable;

public class Ticket_Type implements Serializable{
    private String vip;
    private String vvip;
    private String regular;

    public Ticket_Type(){}

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getVvip() {
        return vvip;
    }

    public void setVvip(String vvip) {
        this.vvip = vvip;
    }

    public String getRegular() {
        return regular;
    }

    public void setRegular(String regular) {
        this.regular = regular;
    }
}
