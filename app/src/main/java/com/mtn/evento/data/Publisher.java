package com.mtn.evento.data;

import java.io.Serializable;

/**
 * Created by user on 8/4/2017.
 */

public class Publisher implements Serializable {
    private int publisher_id;
    private String username;
    private String picture;

    public Publisher(){}
    public int getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(int publisher_id) {
        this.publisher_id = publisher_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
