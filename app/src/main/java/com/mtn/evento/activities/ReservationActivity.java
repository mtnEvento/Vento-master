package com.mtn.evento.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.mtn.evento.R;
import com.mtn.evento.data.Constants;
import com.mtn.evento.fragments.ReserveSeatFragment;

public class ReservationActivity extends AppCompatActivity {

    Toolbar toolbar;
    ReserveSeatFragment seatFragment ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        seatFragment = new ReserveSeatFragment();
        seatFragment.setArguments(getIntent().getBundleExtra(Constants.BUNDLE));
        getSupportFragmentManager().beginTransaction().add(R.id.container,seatFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.seat_type_count,menu);
        seatFragment.setCartView(menu.findItem(R.id.seatTypeCount));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
