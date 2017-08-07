package com.mtn.evento;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mtn.evento.data.Database;
import com.mtn.evento.data.EncodeData;
import com.mtn.evento.data.Event;
import com.mtn.evento.data.Location;
import com.mtn.evento.data.Ticket_Type;

import net.glxn.qrgen.android.QRCode;

import java.util.Date;


public class EventoMainPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento_main_page);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference eventsRef = firebaseDatabase.getReference(Database.Tables.EVENTS);

        long ts= System.currentTimeMillis();
        Event a = new Event();
        a.setTotal_seats(800);
        a.setEvent_name("VGMA");
        a.setEvent_id(ts);
        a.setVenue("Conference Centre");
        a.setDate_published(""+new Date());
        a.setEvent_date(""+new Date(12,9,2017));
        a.setEvent_type("Music Awards");
        a.setTitle("Vodafone Ghana Music Awards, 2018");
        a.setLocation(new Location(3.27772,-1.455));
        a.setRegions("Volta");
        Ticket_Type ticketType = new Ticket_Type();
        ticketType.setRegular("200");
        ticketType.setVip("500");
        ticketType.setVvip("800");
        a.setTicket_type(ticketType);
        eventsRef.child("evt-"+ ts).getRef().setValue(a);

        Button button2 = (Button) findViewById(R.id.button2);
        final EditText ref = (EditText) findViewById(R.id.editText2);
        final EditText name = (EditText) findViewById(R.id.editText);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EncodeData.getInstance();
                String encoded =  EncodeData.encode(name.getText().toString()+ " "+ref.getText().toString());
                Bitmap myBitmap = QRCode.from( encoded).withColor(0x0a80c9AA, 0xffb907AA).bitmap();
                ImageView myImage = (ImageView) findViewById(R.id.imageView);
                myImage.setImageBitmap(myBitmap);
                TextView securedQR = (TextView) findViewById(R.id.securedQR);
                securedQR.setVisibility(View.VISIBLE);
                securedQR.setText(encoded);
            }
        });

        eventsRef.getRef().addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(EventoMainPage.this,"Data : "+dataSnapshot, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
              //  Toast.makeText(EventoMainPage.this,"Data : "+dataSnapshot, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Toast.makeText(EventoMainPage.this,"Data : "+dataSnapshot, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
               // Toast.makeText(EventoMainPage.this,"Data : "+dataSnapshot, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EventoMainPage.this,"Data : "+databaseError, Toast.LENGTH_LONG).show();
            }


        });







    }
}