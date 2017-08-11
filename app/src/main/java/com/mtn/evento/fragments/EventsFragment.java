package com.mtn.evento.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mtn.evento.EventoMainPage;
import com.mtn.evento.R;
import com.mtn.evento.activities.HomeScreenActivity;
import com.mtn.evento.adapters.EventAdapter;
import com.mtn.evento.data.Database;
import com.mtn.evento.data.Event;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static com.mtn.evento.data.Constants.LOGMESSAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment implements HomeScreenActivity.SearchRequestListener,HomeScreenActivity.SearchRegionRequestListener {

    static private RecyclerView eventRecycler;
    static private RecyclerView.LayoutManager layoutManager;
    static EventAdapter eventAdapter;
    static ArrayList<Event> events;
    static private FirebaseDatabase firebaseDatabase ;
    static private DatabaseReference eventsRef;
    static private AppCompatActivity appContext;
    static private EventValueListener eventValueListener;
    private TextView no_networkView;

    public interface EventFilter{
        public void onFilterEvent(String filterTerm, ArrayList<Event> events);

    }
    public EventsFragment() {
        events = new ArrayList<>();
        eventAdapter = new EventAdapter();
        firebaseDatabase = FirebaseDatabase.getInstance() ;
        eventsRef =  firebaseDatabase.getReference(Database.Tables.EVENTS);
    }
    public void setAppContext(AppCompatActivity appContext){
        this.appContext = appContext ;



    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        eventRecycler = (RecyclerView) view.findViewById(R.id.eventRecycler);
        no_networkView = (TextView) view.findViewById(R.id.no_network);
        layoutManager = new LinearLayoutManager(appContext);
        eventRecycler.setLayoutManager(layoutManager);
        eventRecycler.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        attachValuelistener();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(eventValueListener != null){

            if(eventsRef != null){
                eventsRef.removeEventListener(eventValueListener);
            }
        }

    }
    private void attachValuelistener(){
        if(isNetworkAndInternetAvailable())
        {
            eventValueListener = new EventValueListener();

            eventsRef.addValueEventListener(eventValueListener);
        }
        else
        {
            //TODO: Alert no network connection
            eventRecycler.setVisibility(View.GONE);
            no_networkView.setVisibility(View.VISIBLE);

        }
    }
     class EventValueListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot)
        {
            for (DataSnapshot aDataSnapshot: dataSnapshot.getChildren()){
                 Event evt = aDataSnapshot.getValue(Event.class);
                events.add(evt);
            }

            Log.d(LOGMESSAGE, "onDataChange: Events " + events);
            eventAdapter.setEvents(events, false);
            eventRecycler.setAdapter(eventAdapter);
            no_networkView.setVisibility(View.GONE);
            eventRecycler.setVisibility(View.VISIBLE);


        }
        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
    @Override
    public ArrayList<Event> onSearch(String query) {

        if(!query.isEmpty()){

                ArrayList<Event> filteredEvents = new ArrayList<>();
                for (Event e: events ) {
                    if(e != null  && e.getTitle() != null && e.getTitle().toLowerCase().contains(query)){
                        filteredEvents.add(e);
                    }
                }
                eventAdapter.setEvents(filteredEvents, true);
                eventRecycler.setAdapter(eventAdapter);
        }
        else
        {
              if(events != null && !events.isEmpty() )
              {
                    eventAdapter.setEvents(events,false);
                    eventRecycler.setAdapter(eventAdapter);
                    Log.d(LOGMESSAGE, "onSearch: Events " + events);
                    Log.d(LOGMESSAGE, "onSearch: Events Obj title" + events.get(0));
              }
        }


        return null;
    }
    @Override
    public ArrayList<Event> onRegionSearch(String query, ViewPager vp) {
        if(!query.isEmpty()){
            if(query.contains("--region--")){
                if(events != null && !events.isEmpty() ){
                    eventAdapter.setEvents(events,false);
                    eventRecycler.setAdapter(eventAdapter);

                }
                else
                {
                    ArrayList<Event> filteredEvents = new ArrayList<>();
                    for (Event e : events ) {
                        if( e != null  && e.getRegion() != null && e.getRegion().toLowerCase().contains(query)){
                            filteredEvents.add(e);
                        }
                    }
                    eventAdapter.setEvents(filteredEvents,true);
                    eventRecycler.setAdapter(eventAdapter);

                }
            }
            else
            {
                ArrayList<Event> filteredEvents = new ArrayList<>();
                for (Event e : events ) {
                    if( e != null  && e.getRegion() != null && e.getRegion().toLowerCase().contains(query)){
                        filteredEvents.add(e);
                    }
                }
                eventAdapter.setEvents(filteredEvents, true);
                eventRecycler.setAdapter(eventAdapter);
            }
        }
        return null;
    }
    public boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getActivity(). getSystemService(getActivity().getBaseContext().CONNECTIVITY_SERVICE);

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
        ConnectivityManager ConnectionManager=(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
}
