package com.mtn.evento.ticket_view;

import com.mtn.evento.data.DisplayTicket;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Summy on 9/13/2017.
 */

public class ViewableTicket implements Serializable {

    private String transactionId;
    private boolean status;
    private List<DisplayTicket> displayTickets;

    public ViewableTicket(String transactionId, boolean status, List<DisplayTicket> displayTickets) {
        this.transactionId = transactionId;
        this.status = status;
        this.displayTickets = displayTickets;
    }

    public ViewableTicket(String transactionId, List<DisplayTicket> displayTickets) {
        this.transactionId = transactionId;
        this.displayTickets = displayTickets;
    }

    public ViewableTicket() {
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<DisplayTicket> getDisplayTickets() {
        return displayTickets;
    }

    public void setDisplayTickets(List<DisplayTicket> displayTickets) {
        this.displayTickets = displayTickets;
    }
}
