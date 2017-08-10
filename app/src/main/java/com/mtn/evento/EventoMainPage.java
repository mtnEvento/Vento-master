package com.mtn.evento;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mtn.evento.data.Database;
import com.mtn.evento.data.EncodeData;
import com.mtn.evento.data.Event;
import com.mtn.evento.data.Location;
import com.mtn.evento.data.Ticket;
import com.mtn.evento.utils.SmartGet;

import net.glxn.qrgen.android.QRCode;

import java.util.ArrayList;

import static com.mtn.evento.data.Constants.LOGMESSAGE;


public class EventoMainPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento_main_page);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference eventsRef = firebaseDatabase.getReference(Database.Tables.EVENTS);

        long ts= System.currentTimeMillis();
        Event a = new Event();
        a.setTotal_seats("1300");
        a.setEvent_publisher("Vodafone");
        a.setEvent_id(""+ts);
        a.setVenue("Conference Centre");
        a.setDate_published(""+SmartGet.getDateTimeFromTimeStamp(""+ts));
        a.setEvent_date(""+ SmartGet.getDateTimeFromTimeStamp(""+ts));
        a.setEvent_type("Music Awards");
        a.setTitle("Vodafone Ghana Music Awards, 2018");
        a.setLocation(new Location(3.27772,-1.455));
        a.setRegion("Volta");
        a.setBanner("https://firebasestorage.googleapis.com/v0/b/evento-14a5b.appspot.com/o/banner.jpg?alt=media&token=4c828fa6-5589-40eb-ac46-bc18e599e35d");

        ArrayList<Ticket> tickets = new ArrayList<Ticket>();
        tickets.add(new Ticket("vvip","200","100","60"));
        tickets.add(new Ticket("regular","100","1200","1000"));
        a.setTicket_type(tickets);
        eventsRef.child("evt-"+ ts).getRef().setValue(a);

        Button button2 = (Button) findViewById(R.id.button2);
        final EditText ref = (EditText) findViewById(R.id.editText2);
        final EditText name = (EditText) findViewById(R.id.editText);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EncodeData.getInstance();//  0xFF0000AA -bg :blue  rgb
                String encoded =  EncodeData.encode(name.getText().toString().trim()+ ""+ref.getText().toString().trim());

                Log.d(LOGMESSAGE, "encoded : " + encoded);
                Bitmap myBitmap = QRCode.from( encoded).withColor(0x000000, 0xFd0012AC).bitmap();
                ImageView myImage = (ImageView) findViewById(R.id.imageView);
                myImage.setImageBitmap(myBitmap);
                TextView securedQR = (TextView) findViewById(R.id.securedQR);
                securedQR.setVisibility(View.VISIBLE);
                securedQR.setText(encoded);
            }
        });

//        eventsRef.getRef().addChildEventListener(new ChildEventListener() {
//
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Toast.makeText(EventoMainPage.this,"Data : "+dataSnapshot, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//              //  Toast.makeText(EventoMainPage.this,"Data : "+dataSnapshot, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Toast.makeText(EventoMainPage.this,"Data : "+dataSnapshot, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//               // Toast.makeText(EventoMainPage.this,"Data : "+dataSnapshot, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(EventoMainPage.this,"Data : "+databaseError, Toast.LENGTH_LONG).show();
//            }
//
//
//        });







    }
}
