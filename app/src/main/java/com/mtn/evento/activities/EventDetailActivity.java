package com.mtn.evento.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.mtn.evento.R;
import com.mtn.evento.data.Constants;
import com.mtn.evento.data.Event;
import com.mtn.evento.data.Ticket;

import java.io.Serializable;

import static com.mtn.evento.data.Constants.LOGMESSAGE;

public class EventDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    Intent mIntent;
    Event event;
    MenuItem item;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        init();
        mAuth = FirebaseAuth.getInstance();
        this.mIntent =  getIntent() ;
        Bundle ser =  this.mIntent.getBundleExtra(Constants.BUNDLE);
        Serializable serz =   ser.getSerializable(Constants.EVENT);
        event = (Event) serz;

         Glide.with(this)
                .load(event.getBanner())
                .asBitmap()
                .into(( (ImageView)findViewById(R.id.event_banner))) ;

        //TODO: change the current url to the event banner url
        ((TextView)findViewById(R.id.evt_name)).setText(event.getTitle());
         String date = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date (Long.parseLong(event.getEvent_date())*1000));
        ((TextView)findViewById(R.id.evt_date)).setText(date);
        // TODO: 8/10/2017  set available seats
        LinearLayout seatLinearLayout = (LinearLayout) findViewById(R.id.layout_seat_available);
        LinearLayout pricesLinearLayout = (LinearLayout) findViewById(R.id.layout_seat_prices);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 10, 0, 10);

        for(Ticket ticket : event.getTicket_type())
        {
            TextView view = (TextView) getLayoutInflater().inflate(R.layout.seats_available,null);
            TextView view2 = (TextView) getLayoutInflater().inflate(R.layout.seats_available, null);
            view.setText(ticket.getName().toUpperCase() + " :  " + ticket.getAvailable_seats());
            view2.setText(ticket.getName().toUpperCase() + " :  GHS " + ticket.getAmount());
            seatLinearLayout.addView(view, params);
            pricesLinearLayout.addView(view2, params);
        }
        ( (TextView)findViewById(R.id.evt_venue)).setText((event.getVenue() == null || event.getVenue().isEmpty()) ? "Venue not specified " : event.getVenue());
        ( (TextView)findViewById(R.id.evt_region)).setText((event.getRegion() == null || event.getRegion().isEmpty()) ? "Region not specified " : event.getRegion());
        ((TextView) findViewById(R.id.evt_description)).setText((event.getDescription() == null || event.getDescription().contains("null") || event.getDescription().isEmpty()) ? "No Description " : event.getDescription());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_detail_menu, menu);
        this.item = menu.findItem(R.id.reserve_seat);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.reserve_seat:
                if(isNetworkAndInternetAvailable()){
                    if(mAuth != null && mAuth.getCurrentUser() !=null){
                        //TODO: check if user is logged and open either login or reservation activity
                        //Toast.makeText(this, "Reserve Seats", Toast.LENGTH_SHORT).show();
                        Intent mIntent = new Intent(this,ReservationActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.EVENT,event);
                        mIntent.putExtra(Constants.BUNDLE,bundle);
                        startActivity(mIntent);
                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);
                        builder.setTitle("NOT LOGGED IN");
                        builder.setMessage("Sorry, You need to login first in order to reserve a seat! Goto the Side-Navigation and login if you are already registered.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);
                    builder.setTitle("NO NETWORK AVAILABLE");
                    builder.setMessage("Sorry, no internet connection available. Please check your network connection and try again!");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                break;
            case R.id.share:
                if(isNetworkAndInternetAvailable()){
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT,"Did you know "+ event.getTitle() +" is happening on "+event.getEvent_date() +" ? To get the ticket, just download the Evento Android App from the Google Play Store and reserve a seat because i just did. please don't miss this event.");
                    startActivity(intent);
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);
                    builder.setTitle("NO NETWORK AVAILABLE");
                    builder.setMessage("Sorry, no internet connection available. Please check your network connection and try again!");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                break;
            case R.id.directMe:

                if(isNetworkAndInternetAvailable()){
                    double longitude = event.getLocation().getLongitude();
                    double latitude = event.getLocation().getLatitude();
                    Uri location = Uri.parse("geo:"+String.valueOf(latitude)+","+String.valueOf(longitude)+"?q="+event.getVenue()+", "+event.getRegion());
                    Intent mapLocation = new Intent(Intent.ACTION_VIEW,location);
                    startActivity(mapLocation);
                }
                else
                {

                    AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);
                    builder.setTitle("NO NETWORK AVAILABLE");
                    builder.setMessage("Sorry, no internet connection available. Please check your network connection and try again!");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                break;
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isInternetOn() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager)  getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED)
        {
            return false;
        }
        return false;
    }
    private boolean isNetworkOn(){
        ConnectivityManager ConnectionManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()==true )
        {
           return true;
        }
        else
        {
          return  false;
        }
    }
    private boolean isNetworkAndInternetAvailable(){
        return  isNetworkOn()&& isInternetOn() ;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
