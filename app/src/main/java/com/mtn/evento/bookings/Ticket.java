package com.mtn.evento.bookings;

import java.io.Serializable;

/**
 * Created by Summy on 8/9/2017.
 */
//TODO: come back to me
public class Ticket implements Serializable{

    private String type;
    private String secrets;
    private String timestamp;

    public Ticket(String type, String secrets, String timestamp) {
        this.type = type;
        this.secrets = secrets;
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Ticket(){}

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
