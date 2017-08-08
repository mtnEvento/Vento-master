package com.mtn.evento.utils;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Summy on 8/8/2017.
 */

public class SmartGet {

    public static String getDateTimeFromTimeStamp(String timeStamp){
        String pattern = "dd-MM-yyyy HH:mm:ss a";
        Timestamp stamp = new Timestamp(Long.parseLong(timeStamp));
        Date date = new Date(stamp.getTime());
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date).toString();
    }

    public static String getDateOnyFromTimeStamp(String timeStamp){
        String pattern = "dd-MM-yyyy";
        Timestamp stamp = new Timestamp(Long.getLong(timeStamp));
        Date date = new Date(stamp.getTime());
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date).toString();
    }

    public static String getTimeOnyFromTimeStamp(String timeStamp){
        String pattern = "HH:mm:ss a";
        Timestamp stamp = new Timestamp(Long.getLong(timeStamp));
        Date date = new Date(stamp.getTime());
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date).toString();
    }
}
