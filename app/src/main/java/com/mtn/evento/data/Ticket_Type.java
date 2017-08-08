package com.mtn.evento.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ticket_Type implements Serializable{
    private ArrayList<Ticket> ticket;

    public Ticket_Type(ArrayList<Ticket> ticket)
    {
        this.ticket = ticket ;
    }

    public Ticket_Type( ){}
}
