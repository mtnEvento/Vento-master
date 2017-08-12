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

    public static String printToFile(Activity activity, Bitmap bitmap){

            String path = Saver.saveBitmap(bitmap,activity);
            if(path != null) {
                File file = new File(path);

                try {

                    // Share Intent
                    Intent share = new Intent(Intent.ACTION_SEND);

                    // Type of file to share
                    share.setType("image/jpeg");

                    FileOutputStream output = new FileOutputStream(file);

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
                    return file.getAbsolutePath();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }

            }

        return  null;
    }
}
