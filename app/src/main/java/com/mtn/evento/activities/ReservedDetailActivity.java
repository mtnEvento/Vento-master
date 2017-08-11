package com.mtn.evento.activities;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mtn.evento.R;
import com.mtn.evento.data.Constants;
import com.mtn.evento.data.DisplayTicket;
import com.mtn.evento.data.Event;
import com.mtn.evento.data.ReservedSeatData;
import com.mtn.evento.data.ResultSet;

import java.io.Serializable;
import java.util.ArrayList;

public class ReservedDetailActivity extends AppCompatActivity {
    Intent mIntent;
    private ReservedSeatData reservedSeatData;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserved_detail);
        init();
        this.mIntent =  getIntent() ;
        Bundle ser =  this.mIntent.getBundleExtra(Constants.BUNDLE);
        Serializable serz =   ser.getSerializable(Constants.RESERVED_SEAT);

        reservedSeatData  = ( ReservedSeatData )  serz;
        if( reservedSeatData != null){
            //TODO: change the current url to the event banner url
            Glide.with(this)
                    .load(reservedSeatData.getEvent().getBanner())
                    .asBitmap()
                    .into(( (ImageView)findViewById(R.id.event_banner))) ;

            ( (TextView)findViewById(R.id.reserved_evt_name)).setText((reservedSeatData.getEvent().getTitle() == null || reservedSeatData.getEvent().getTitle().isEmpty()) ? "N/A": reservedSeatData.getEvent().getTitle());
            ( (TextView)findViewById(R.id.reserved_evt_date)).setText((reservedSeatData.getEvent().getEvent_date() == null || reservedSeatData.getEvent().getEvent_date().isEmpty()) ? "N/A": reservedSeatData.getEvent().getEvent_date());
            ( (TextView)findViewById(R.id.reserved_evt_venue)).setText((reservedSeatData.getEvent().getVenue()== null || reservedSeatData.getEvent().getVenue().isEmpty()) ? "N/A" : reservedSeatData.getEvent().getVenue());
            ( (TextView)findViewById(R.id.reserved_evt_description)).setText((reservedSeatData.getEvent().getDescription()== null || reservedSeatData.getEvent().getDescription()== "null" ||reservedSeatData.getEvent().getDescription().isEmpty()) ? "N/A" : reservedSeatData.getEvent().getDescription());
        }

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
        getMenuInflater().inflate(R.menu.reserved_event_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.share:

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,"Did you know "+reservedSeatData.getEvent().getTitle() +" is happening on "+reservedSeatData.getEvent().getEvent_date() +" ? To get the ticket, just download the Evento Android App from the Google Play Store and reserve a seat because i just did. please don't miss this event.");
                startActivity(intent);
                break;
            case R.id.directMe:

                double longitude = reservedSeatData.getEvent().getLocation().getLongitude();
                double latitude = reservedSeatData.getEvent().getLocation().getLatitude();
                Uri location = Uri.parse("geo:"+String.valueOf(latitude)+","+String.valueOf(longitude)+"?q="+reservedSeatData.getEvent().getVenue()+", "+reservedSeatData.getEvent().getRegion());
                Intent mapLocation = new Intent(Intent.ACTION_VIEW,location);
                startActivity(mapLocation);
                break;

            case android.R.id.home:
                super.onBackPressed();
                break;

            case R.id.view_tickets:

                Intent displyIntent = new Intent(this, BarcodeActivity.class);
                displyIntent.putExtra(Constants.TICKET, (Serializable) reservedSeatData.getDisplayTickets());
                startActivity(displyIntent);

       // TODO: 8/10/2017  click should lead to barcode detail page
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
