package com.mtn.evento.data;

import java.io.Serializable;

/**
 * Created by Summy on 8/9/2017.
 */

public class Status implements Serializable {
    private String type_name;
    private String total;
    private String left;

    public Status(String type_name, String total,String left){
        this.type_name = type_name ;
        this.total =  total ;
        this.left = left ;

    }
    public Status(){}

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }
}
