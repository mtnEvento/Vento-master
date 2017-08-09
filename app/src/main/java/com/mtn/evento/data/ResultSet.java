package com.mtn.evento.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Summy on 8/9/2017.
 */

public class ResultSet implements Serializable {
    private ArrayList<Event> mEvent;
    private List<DisplayTicket> mDisplayTickets;

    public ResultSet(ArrayList<Event> mEvent, List<DisplayTicket> mDisplayTickets) {
        this.mEvent = mEvent;
        this.mDisplayTickets = mDisplayTickets;
    }

    public ArrayList<Event> getmEvent() {
        return mEvent;
    }

    public void setmEvent(ArrayList<Event> mEvent) {
        this.mEvent = mEvent;
    }

    public List<DisplayTicket> getmDisplayTickets() {
        return mDisplayTickets;
    }

    public void setmDisplayTickets(List<DisplayTicket> mDisplayTickets) {
        this.mDisplayTickets = mDisplayTickets;
    }

}
