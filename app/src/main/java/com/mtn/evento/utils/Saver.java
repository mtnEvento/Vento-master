package com.mtn.evento.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.mtn.evento.data.Constants.LOGMESSAGE;

/**
 * Created by Summy on 8/10/2017.
 */

public class Saver {

    public static Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void openScreenshot(File imageFile, Activity a) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        a.startActivity(intent);
    }
    public static void openScreenshot(String  imageFilePath, Activity a) {

        File f = new File(imageFilePath);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(f);
        intent.setDataAndType(uri, "image/*");
        a.startActivity(intent);
    }
    public static Bitmap takeScreenShot(View v){
        Bitmap screenShot = null;
        try
        {
           int width  = v.getMeasuredWidth();
           int height  = v.getMeasuredHeight();
           screenShot = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
           Canvas c = new Canvas(screenShot);
           v.draw(c);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return screenShot;
    }
    public static String saveBitmap(Bitmap bitmap, Activity a) {
        String absolutePath = null;
        try {

            File mFolder = new File(Environment.getExternalStorageDirectory().toString() + "/evento/event-barcode/" ); //give a name for the folder
            File imagePath = new File(mFolder +  String.valueOf(System.currentTimeMillis())+ "_ticket.png");
            if (!mFolder.exists()) {
                mFolder.mkdirs();
            }

            FileOutputStream fos=null;
            ByteArrayOutputStream bao = null;

            if(bitmap != null){
//
                bao = new ByteArrayOutputStream();
                fos = new FileOutputStream(imagePath);
                int quality = 80;

                bitmap.compress(Bitmap.CompressFormat.PNG, quality, bao);
                fos.write(bao.toByteArray());
                fos.flush();
                fos.close();
                absolutePath = imagePath.getAbsolutePath();

            }else
            {
                Log.e(LOGMESSAGE," bitmap is null");
            }
        } catch (FileNotFoundException e) {
            Log.e(LOGMESSAGE, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(LOGMESSAGE, e.getMessage(), e);
        }

        return absolutePath;
    }
    public static String shareTicket(Activity activity,String path){


        if(path != null) {
            try {
                File file = new File(path);
                Uri uri = Uri.fromFile(file);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                activity.startActivity(Intent.createChooser(share, "Share Image"));
                return file.getAbsolutePath();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }

        }

        return  null;
    }
}
