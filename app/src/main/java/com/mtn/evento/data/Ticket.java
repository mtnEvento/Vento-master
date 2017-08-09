package com.mtn.evento.data;

import java.io.Serializable;

/**
 * Created by Summy on 8/8/2017.
 */

public class Ticket implements Serializable{
    private String name;
    private String amount;
    private String total_seats;
    private String available_seats;

    public Ticket(){}

    public Ticket(String name, String amount ){
       this.name = name ;
       this.amount = amount ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTotal_seats() {
        return total_seats;
    }

    public void setTotal_seats(String total_seats) {
        this.total_seats = total_seats;
    }

    public String getAvailable_seats() {
        return available_seats;
    }

    public void setAvailable_seats(String available_seats) {
        this.available_seats = available_seats;
    }
}
