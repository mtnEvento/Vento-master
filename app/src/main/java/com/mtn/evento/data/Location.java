package com.mtn.evento.data;

import java.io.Serializable;

/**
 * Created by user on 8/4/2017.
 */

public class Location implements Serializable {

    private double latitude;
    private double longitude;


    public  Location(){

    }
    public Location(double latitude,double longitude){
        this.latitude = latitude ;
        this.longitude = longitude ;
    }
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}