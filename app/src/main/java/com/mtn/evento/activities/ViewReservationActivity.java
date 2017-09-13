package com.mtn.evento.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.mtn.evento.R;
import com.mtn.evento.activities.HomeScreenActivity;
import com.mtn.evento.data.Constants;
import com.mtn.evento.fragments.FinishFragment;
import com.mtn.evento.ticket_view.ViewableTicket;

import java.io.Serializable;

public class ViewReservationActivity extends AppCompatActivity {

    private Intent intent;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reservation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FinishFragment finishFragment = new FinishFragment();

        intent = getIntent();
        if(intent != null){
            Bundle bag =  intent.getBundleExtra(Constants.VIEW_TICKET_BUNBLE);
            if (bag != null) {
               Serializable ser = bag.getSerializable(Constants.VIEWABLE_TICKET);
               if(ser != null){
                  ViewableTicket viewableTicket = (ViewableTicket) ser;
                   finishFragment.setValues(viewableTicket.getTransactionId(), viewableTicket.getDisplayTickets());
                   getSupportFragmentManager().beginTransaction().add(R.id.container,finishFragment).commit();
               }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this,HomeScreenActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent i = new Intent(this,HomeScreenActivity.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
