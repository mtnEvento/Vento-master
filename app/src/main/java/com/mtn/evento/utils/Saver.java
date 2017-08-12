package com.mtn.evento.utils;

import android.app.Activity;
import android.graphics.Bitmap;
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

/**
 * Created by Summy on 8/10/2017.
 */

public class Saver {
//
//  //  linearLayout = (LinearLayout) findViewById(R.id.screenshots); //say   for eg: this is the main layout id wich holds everything(images etc)
//    //use a button to call this method.
//    private void saveLayout(Activity activity) {
//        // View v1 = getWindow().getDecorView().getRootView();
//        View v1 = linearLayout.getRootView();
//        v1.setDrawingCacheEnabled(true);
//        myBitmap = v1.getDrawingCache();
//        if (myBitmap != null) {
//            Toast.makeText(activity, "Bitmap not null",
//                    Toast.LENGTH_SHORT).show();
//            saveBitmap(myBitmap);
//        } else {
//            Toast.makeText(MainScreen.this, "Bitmap null", Toast.LENGTH_SHORT).show();
//        }
//    }

    public static String saveBitmap(Bitmap bitmap, Activity a) {
        String absolutePath = null;
        try {
            File mFolder = new File(a.getFilesDir() + "/event-barcode/"); //give a name for the folder
            File imagePath = new File(mFolder +  String.valueOf(System.currentTimeMillis())+ "-ticket.png");
            if (!mFolder.exists()) {
                mFolder.mkdirs();
            }
            if (!imagePath.exists()) {
                imagePath.createNewFile();
            }
            FileOutputStream fos=null;

            fos = new FileOutputStream(imagePath);
            // bitmap.compress(CompressFormat.PNG, 100, fos);
            bitmap.compress(Bitmap.CompressFormat.PNG, 60, fos);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encodedByte = Base64.encodeToString(byteArray,Base64.DEFAULT);
            Log.e("encodeByte", encodedByte);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(a.getContentResolver(), bitmap,"Screen", "screen");

            absolutePath = imagePath.getAbsolutePath();

        } catch (FileNotFoundException e) {
            Log.e("no file", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("io", e.getMessage(), e);
        }

        return absolutePath;
    }
}
