package com.mtn.evento;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.FirebaseDatabase;
import com.mtn.evento.config.FontsOverride;

import static com.mtn.evento.data.Constants.PREFS_NAME;


public class Evento extends Application {

    private SharedPreferences settings= null;

//
    @Override
    public void onCreate() {
        super.onCreate();


        new Thread(new Runnable() {
            @Override
            public void run() {
                FontsOverride.setDefaultFont(Evento.this, "DEFAULT", "NexaLight.ttf");
                FontsOverride.setDefaultFont(Evento.this, "MONOSPACE", "NexaLight.ttf");
                FontsOverride.setDefaultFont(Evento.this, "SERIF", "NexaBold.ttf");
                FontsOverride.setDefaultFont(Evento.this, "SANS_SERIF", "NexaBold.ttf");
                settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


            }
        }).start();
    }

    public SharedPreferences getSettings() {
        return settings;
    }
}
