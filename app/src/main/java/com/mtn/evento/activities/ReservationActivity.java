package com.mtn.evento.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mtn.evento.R;
import com.mtn.evento.data.Constants;
import com.mtn.evento.data.DisplayTicket;
import com.mtn.evento.fragments.ReserveSeatFragment;
import com.mtn.evento.ticket_view.ViewableTicket;

import java.util.List;

public class ReservationActivity extends AppCompatActivity implements ReserveSeatFragment.PaymentListener{

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
        seatFragment.setCallback(this);
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
                Intent i = new Intent(this,HomeScreenActivity.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void payed(final String transactionId, final boolean status,final List<DisplayTicket> displayTickets) {

       runOnUiThread(new Runnable() {
           @Override
           public void run() {
               if(status){
                   Intent i = new Intent(ReservationActivity.this,ViewReservationActivity.class);
                   Bundle bag = new Bundle();
                   bag.putSerializable(Constants.VIEWABLE_TICKET,new ViewableTicket(transactionId,status,displayTickets));
                   i.putExtra(Constants.VIEW_TICKET_BUNBLE,bag);
                   startActivity(i);
               }
           }
       });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this,HomeScreenActivity.class);
        startActivity(i);
    }
}
