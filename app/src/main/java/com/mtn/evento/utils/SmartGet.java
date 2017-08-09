package com.mtn.evento.utils;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
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
        return format.format(date);
    }

    public static void printToFile(Activity activity, Bitmap bitmap , String filename){
        Bitmap mBitmap;
        OutputStream output;

        mBitmap =  bitmap ;
        /*  // Retrieve the image from the res folder  BitmapFactory.decodeResource(getResources(),R.drawable.wallpaper);*/
        // Find the SD Card path
        File filepath = Environment.getExternalStorageDirectory();

        // Create a new folder AndroidBegin in SD Card
        File dir = new File(filepath.getAbsolutePath() + "/Evento-tickets/");
        dir.mkdirs();

        // Create a name for the saved image
        File file = new File(dir, filename+".png");

        try {

            // Share Intent
            Intent share = new Intent(Intent.ACTION_SEND);

            // Type of file to share
            share.setType("image/jpeg");

            output = new FileOutputStream(file);

            // Compress into png format image from 0% - 100%
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();

            // Locate the image to Share
            Uri uri = Uri.fromFile(file);

            // Pass the image into an Intnet
            share.putExtra(Intent.EXTRA_STREAM, uri);

            // Show the social share chooser list
            activity.startActivity(Intent.createChooser(share, "Share Image Tutorial"));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
