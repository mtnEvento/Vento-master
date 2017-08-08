package com.mtn.evento.data;

import java.io.Serializable;

/**
 * Created by Summy on 8/6/2017.
 */

public class User implements Serializable{

    private String id;
    private String username;
    private String email;
    private String phone;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public  User(String username, String email){
        this.username = username ;
        this.email = email;

    }

    public  User(String id, String username, String email){
        this.username = username ;
        this.email = email;
        this.id = id ;
    }

    public  User(String id, String username, String email,String phone){
        this.username = username ;
        this.email = email;
        this.id = id ;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public User(){
    }
}
