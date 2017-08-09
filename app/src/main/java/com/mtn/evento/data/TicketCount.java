package com.mtn.evento.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Summy on 8/9/2017.
 */

public class TicketCount implements Serializable {
    private ArrayList<Status> ticket_types;

    public TicketCount (){}
    public TicketCount (ArrayList<Status> ticket_types){
        this.ticket_types =  ticket_types ;
    }

    public ArrayList<Status> getTicket_types() {
        return ticket_types;
    }

    public void setTicket_types(ArrayList<Status> ticket_types) {
        this.ticket_types = ticket_types;
    }
}
