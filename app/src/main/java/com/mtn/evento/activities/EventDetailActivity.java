package com.mtn.evento.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mtn.evento.R;
import com.mtn.evento.data.Constants;
import com.mtn.evento.data.Event;

import java.io.Serializable;

import static com.mtn.evento.data.Constants.LOGMESSAGE;

public class EventDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    Intent mIntent;
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        init();

        this.mIntent =  getIntent() ;
        Bundle ser =  this.mIntent.getBundleExtra(Constants.BUNDLE);
        Serializable serz =   ser.getSerializable(Constants.EVENT);
        event = (Event) serz;

        //TODO: change the current url to the event banner url
         Glide.with(this)
                .load(event.getBanner())
                .asBitmap()
                .into(( (ImageView)findViewById(R.id.event_banner))) ;

        ( (TextView)findViewById(R.id.evt_name)).setText(event.getTitle());
        ( (TextView)findViewById(R.id.evt_date)).setText(event.getEvent_date());
        ( (TextView)findViewById(R.id.evt_available_seat)).setText("");
        ( (TextView)findViewById(R.id.evt_venue)).setText(event.getVenue());
        ( (TextView)findViewById(R.id.evt_region)).setText(event.getRegion());
        ( (TextView)findViewById(R.id.evt_description)).setText(event.getDescription());
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.reserve_seat:
              //TODO: check if user is logged and open either login or reservation activity
                Toast.makeText(this, "Reserve Seats", Toast.LENGTH_SHORT).show();

                Intent mIntent = new Intent(this,ReservationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.EVENT,event);
                mIntent.putExtra(Constants.BUNDLE,bundle);
                startActivity(mIntent);

                break;
            case R.id.share:

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,"Did you know "+ event.getTitle() +" is happening on "+event.getEvent_date() +" ? To get the ticket, just download the Evento Android App from the Google Play Store and reserve a seat because i just did. please don't miss this event.");
                startActivity(intent);
                break;
            case R.id.directMe:

                double longitude = event.getLocation().getLongitude();
                double latitude = event.getLocation().getLatitude();
                Uri location = Uri.parse("geo:"+String.valueOf(latitude)+","+String.valueOf(longitude)+"?q="+event.getVenue()+", "+event.getRegion());
                Intent mapLocation = new Intent(Intent.ACTION_VIEW,location);
                startActivity(mapLocation);
                break;
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
