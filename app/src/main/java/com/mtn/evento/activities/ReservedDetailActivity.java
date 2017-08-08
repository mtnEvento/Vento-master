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
import com.mtn.evento.data.Event;

import java.io.Serializable;

public class ReservedDetailActivity extends AppCompatActivity {
    Intent mIntent;
    Event mEvent;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserved_detail);
        init();
        this.mIntent =  getIntent() ;
        Bundle ser =  this.mIntent.getBundleExtra(Constants.BUNDLE);
        Serializable serz =   ser.getSerializable(Constants.EVENT);
        Event event = (Event) serz;
        mEvent =  event ;
        //TODO: change the current url to the event banner url
         Glide.with(this)
                .load(event.getBanner())
                .asBitmap()
                .into(( (ImageView)findViewById(R.id.event_banner))) ;

        ( (TextView)findViewById(R.id.evt_name)).setText(event.getTitle());
        ( (TextView)findViewById(R.id.evt_date)).setText(event.getEvent_date());
        ( (TextView)findViewById(R.id.evt_venue)).setText(event.getVenue());
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
                intent.putExtra(Intent.EXTRA_TEXT,"Delivery service is just an App away from you!");
                startActivity(intent);
                break;
            case R.id.directMe:

                double longitude = mEvent.getLocation().getLongitude();
                double latitude = mEvent.getLocation().getLatitude();
                Uri location = Uri.parse("geo:"+String.valueOf(latitude)+","+String.valueOf(longitude)+"?q="+mEvent.getVenue()+", "+mEvent.getRegion());
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
