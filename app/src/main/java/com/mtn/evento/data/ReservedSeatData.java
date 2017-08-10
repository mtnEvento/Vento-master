package com.mtn.evento.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Summy on 8/10/2017.
 */

public class ReservedSeatData implements Serializable {
    Event event;
    List<DisplayTicket> displayTickets;

    public ReservedSeatData(){}

    public ReservedSeatData(Event event, List<DisplayTicket> displayTickets) {
        this.event = event;
        this.displayTickets = displayTickets;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public List<DisplayTicket> getDisplayTickets() {
        return displayTickets;
    }

    public void setDisplayTickets(List<DisplayTicket> displayTickets) {
        this.displayTickets = displayTickets;
    }
}
