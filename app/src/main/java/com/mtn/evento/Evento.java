package com.mtn.evento;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.mtn.evento.config.FontsOverride;


public class Evento extends Application {
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
            }
        }).start();
    }
}
