package com.mtn.evento;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.mtn.evento.config.FontsOverride;
import com.mtn.evento.database.DatabaseHandler;
import static com.mtn.evento.data.Constants.PREFS_NAME;

public class Evento extends MultiDexApplication {

    private SharedPreferences settings= null;
    private DatabaseHandler databaseHandler;
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
                databaseHandler = new DatabaseHandler(getApplicationContext());
            }
        }).start();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(Evento.this);
    }

    public DatabaseHandler getDatabaseHandler() {
        return databaseHandler;
    }
    public SharedPreferences getSettings() {
        return settings;
    }
}
