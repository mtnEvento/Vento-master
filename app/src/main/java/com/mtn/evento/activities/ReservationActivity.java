package com.mtn.evento.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.mtn.evento.R;
import com.mtn.evento.fragments.ReserveSeatFragment;

public class ReservationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        getSupportFragmentManager().beginTransaction().add(R.id.container,new ReserveSeatFragment()).commit();
    }
}
