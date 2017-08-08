package com.mtn.evento.data;

import java.io.Serializable;

/**
 * Created by Summy on 8/8/2017.
 */

public class Ticket implements Serializable{
    private String name;
    private String amount;

    public Ticket(){}

    public Ticket(String name, String amount ){
       this.name = name ;
       this.amount = amount ;
    }

}
