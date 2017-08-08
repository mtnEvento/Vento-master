package com.mtn.evento.data;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by user on 8/4/2017.
 */

public class Event implements Serializable {
    private String event_id;
    private String event_publisher;
    private String title;
    private String event_type;
    private String description;
    private String region;
    private String date_published;
    private String event_date;
    private String venue;
    private String banner;
    private String total_seats;
    private Ticket_Type ticket_type;
    private Location location;
    private CustomBitmap bitmap;

    public CustomBitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(CustomBitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getEvent_publisher() {
        return event_publisher;
    }

    public void setEvent_publisher(String event_publisher) {
        this.event_publisher = event_publisher;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDate_published() {
        return date_published;
    }

    public void setDate_published(String date_published) {
        this.date_published = date_published;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getTotal_seats() {
        return total_seats;
    }

    public void setTotal_seats(String total_seats) {
        this.total_seats = total_seats;
    }

    public Ticket_Type getTicket_type() {
        return ticket_type;
    }

    public void setTicket_type(Ticket_Type ticket_type) {
        this.ticket_type = ticket_type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
