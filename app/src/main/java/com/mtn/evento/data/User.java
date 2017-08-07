package com.mtn.evento.data;

import java.io.Serializable;

/**
 * Created by Summy on 8/6/2017.
 */

public class User implements Serializable{

    private String username;
    private String email;

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

    public  User(){
    }
}
