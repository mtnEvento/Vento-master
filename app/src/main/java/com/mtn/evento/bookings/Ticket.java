package com.mtn.evento.bookings;

import java.io.Serializable;

/**
 * Created by Summy on 8/9/2017.
 */
//TODO: come back to me
public class Ticket implements Serializable{

    private String type;
    private String secrets;

    public Ticket(){}
    public Ticket(String type, String secrets){
        this.type = type ;
        this.secrets = secrets ;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSecrets() {
        return secrets;
    }

    public void setSecrets(String secrets) {
        this.secrets = secrets;
    }
}
