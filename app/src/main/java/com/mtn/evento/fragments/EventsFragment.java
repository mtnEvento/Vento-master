package com.mtn.evento.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mtn.evento.Factory;
import com.mtn.evento.R;
import com.mtn.evento.activities.HomeScreenActivity;
import com.mtn.evento.adapters.EventAdapter;
import com.mtn.evento.data.Database;
import com.mtn.evento.data.Event;
import java.util.ArrayList;

import static com.mtn.evento.activities.HomeScreenActivity.cacheEvent;
import static com.mtn.evento.activities.HomeScreenActivity.events;
import static com.mtn.evento.data.Constants.LOGMESSAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment implements HomeScreenActivity.SearchRequestListener, HomeScreenActivity.SearchRegionRequestListener, Factory.InternetDataListenter {

    static EventAdapter eventAdapter;
    private static RecyclerView eventRecycler;
    private static RecyclerView.LayoutManager layoutManager;
    private static HomeScreenActivity appContext;
    private static TextView no_networkView;
    private static int dataCount ;
    private static String currentSpinnerSelection = "-region-";
    boolean isNotFirstResume = false;
    private DatabaseReference eventsRef;
    private FirebaseDatabase firebaseDatabase;
    private EventValueListener eventValueListener;
    private String mQuery;
    ArrayList<Event> regionFiltered;
    private static  boolean isFragmentAttached = false ;
    IAttach iAttach;

    public EventsFragment() {
        Log.d(LOGMESSAGE,"EventsFragment called") ;
        regionFiltered = new ArrayList<>();
    }
    public void setAppContext(HomeScreenActivity appContext){
        if( this.appContext == null){
            this.appContext = appContext ;

            if(eventAdapter == null){
                eventAdapter = new EventAdapter();
            }
        }
    }

    @Override
    public void onInternetConnected() {

        if(isFragmentAttached){
           //Do any thing you want here.
        }
    }
    @Override
    public void onInternetDisconnected() {

        if(isFragmentAttached){

            if(iAttach != null){
                iAttach.onAttachedcheck();
            }
        }


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
        no_networkView.setText("Loading Events...");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOGMESSAGE, "onStart: isSearching is " + HomeScreenActivity.isSearching);

            initFirebase();
            if (Factory.isNetworkAndInternetAvailableNow()) {
                if (eventRecycler != null && no_networkView != null) {
                    if (cacheEvent != null && cacheEvent.size() > 0) {
                        eventAdapter.setEvents(cacheEvent, false);
                        eventRecycler.setAdapter(eventAdapter);
                        no_networkView.setVisibility(View.GONE);
                        eventRecycler.setVisibility(View.VISIBLE);
                    } else {
                        eventRecycler.setVisibility(View.GONE);
                        no_networkView.setText("Loading Events...");
                        no_networkView.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                if (eventRecycler != null && no_networkView != null && cacheEvent != null && cacheEvent.size() > 0) {
                    eventAdapter.setEvents(cacheEvent, false);
                    eventRecycler.setAdapter(eventAdapter);
                    no_networkView.setVisibility(View.GONE);
                    eventRecycler.setVisibility(View.VISIBLE);
                } else {
                    eventRecycler.setVisibility(View.GONE);
                    no_networkView.setText(getString(R.string.no_connection));
                    no_networkView.setVisibility(View.VISIBLE);
                }
            }


    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG", "" + cacheEvent);
        if(Factory.isNetworkAndInternetAvailableNow()){
            if (eventRecycler != null && no_networkView != null){
                if (cacheEvent != null && cacheEvent.size() > 0) {
                    eventAdapter.setEvents(cacheEvent, false);
                    eventRecycler.setAdapter(eventAdapter);
                    no_networkView.setVisibility(View.GONE);
                    eventRecycler.setVisibility(View.VISIBLE);
                } else {
                    eventRecycler.setVisibility(View.GONE);
                    no_networkView.setText("Loading Events...");
                    no_networkView.setVisibility(View.VISIBLE);
                }
            }
        }
        else
        {
            if (eventRecycler != null && no_networkView != null && cacheEvent != null && cacheEvent.size()> 0){
                eventAdapter.setEvents(cacheEvent, false);
                eventRecycler.setAdapter(eventAdapter);
                no_networkView.setVisibility(View.GONE);
                eventRecycler.setVisibility(View.VISIBLE);
            } else {

                eventRecycler.setVisibility(View.GONE);
                no_networkView.setText( getString(R.string.no_connection));
                no_networkView.setVisibility(View.VISIBLE);
            }
        }
        if (isNotFirstResume) {
            restoreSpinnerState();
        }

    }

    @Override
    public ArrayList<Event> onSearch(String query) {
        mQuery = query;
        Log.d(LOGMESSAGE, "onSearch: is called " + query);
        ArrayList<Event> filteredEvents = new ArrayList<>();

        if(regionFiltered != null && regionFiltered.size() > 0){
            for (Event e : regionFiltered) {
                if( e != null  && e.getTitle() != null && e.getTitle().toLowerCase().contains(query)){
                    filteredEvents.add(e);
                    Log.d(LOGMESSAGE, "filtered event added");
                }
            }
        }else{
            for (Event e : cacheEvent) {
                if( e != null  && e.getTitle() != null && e.getTitle().toLowerCase().contains(query)){
                    filteredEvents.add(e);
                    Log.d(LOGMESSAGE, "filtered event added");
                }
            }
        }


        if (eventAdapter != null) {

            Log.d(LOGMESSAGE, "filteredEvents.size() : " + filteredEvents.size());

            if (filteredEvents.size() > 0) {
                if (eventRecycler != null) {
                    eventAdapter.setEvents(filteredEvents, true);
                    eventRecycler.setAdapter(eventAdapter);
                    eventRecycler.setVisibility(View.VISIBLE);
                    no_networkView.setVisibility(View.GONE);
                }
            } else {
                eventRecycler.setVisibility(View.GONE);
                no_networkView.setText("NO EVENTS MATCH THE SEARCH");
                no_networkView.setVisibility(View.VISIBLE);
            }

        }

        return null;
    }
    @Override
    public ArrayList<Event> onRegionSearch(String query, ViewPager vp) {
        if(!query.isEmpty()){
            currentSpinnerSelection = query;
            isNotFirstResume = true;
            if (query.contains("--region--")) {
                if (cacheEvent != null && !cacheEvent.isEmpty() && eventAdapter != null && eventRecycler != null) {
                    eventAdapter.setEvents(cacheEvent, false);
                    eventRecycler.setAdapter(eventAdapter);
                    eventRecycler.setVisibility(View.VISIBLE);
                    no_networkView.setVisibility(View.GONE);
                }
            }
            else
            {
                ArrayList<Event> filteredEvents = new ArrayList<>();
                for (Event e : cacheEvent) {
                    if( e != null  && e.getRegion() != null && e.getRegion().toLowerCase().contains(query)){
                        filteredEvents.add(e);
                    }
                }
                regionFiltered = filteredEvents;
                if (eventAdapter != null) {
                    if (filteredEvents.size() > 0) {

                        if (eventRecycler != null) {
                            eventAdapter.setEvents(filteredEvents, true);
                            eventRecycler.setAdapter(eventAdapter);
                            eventRecycler.setVisibility(View.VISIBLE);
                            no_networkView.setVisibility(View.GONE);
                        }
                    } else {
                        eventRecycler.setVisibility(View.GONE);
                        no_networkView.setText("NO EVENTS AVAILABLE");
                        no_networkView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        return null;
    }
    public void initFirebase() {
        if (firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
        }
        if (eventsRef == null) {
            eventsRef = firebaseDatabase.getReference(Database.Tables.EVENTS);
            eventValueListener = new EventValueListener();
            eventsRef.addValueEventListener(eventValueListener);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        eventsRef.addValueEventListener(eventValueListener);
    }

    public void restoreSpinnerState() {

        if (currentSpinnerSelection.contains("--region--")) {
            if (cacheEvent != null && !cacheEvent.isEmpty() && eventAdapter != null && eventRecycler != null) {
                eventAdapter.setEvents(cacheEvent, false);
                eventRecycler.setAdapter(eventAdapter);
                eventRecycler.setVisibility(View.VISIBLE);
                no_networkView.setVisibility(View.GONE);

            }
        } else {
            ArrayList<Event> filteredEvents = new ArrayList<>();
            for (Event e : cacheEvent) {
                if (e != null && e.getRegion() != null && e.getRegion().toLowerCase().contains(currentSpinnerSelection)) {
                    filteredEvents.add(e);
                }
            }
            if (eventAdapter != null) {
                if (filteredEvents != null && filteredEvents.size() > 0) {

                    if (eventRecycler != null) {
                        eventAdapter.setEvents(filteredEvents, true);
                        eventRecycler.setAdapter(eventAdapter);
                        eventRecycler.setVisibility(View.VISIBLE);
                        no_networkView.setVisibility(View.GONE);
                    }
                } else {
                    eventRecycler.setVisibility(View.GONE);
                    no_networkView.setText("NO EVENTS AVAILABLE");
                    no_networkView.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    public interface EventFilter {
        public void onFilterEvent(String filterTerm, ArrayList<Event> events);
    }

    class EventValueListener implements ValueEventListener {
        volatile int preDataCount;

        public EventValueListener() {

        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            events.clear();
            for (DataSnapshot aDataSnapshot : dataSnapshot.getChildren()) {
                Event evt = aDataSnapshot.getValue(Event.class);
                events.add(evt);
            }
            cacheEvent = events;

            Log.d(LOGMESSAGE, "onDataChange: Events " + cacheEvent);


            EventsFragment.this.appContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Log.d(LOGMESSAGE, "onEventsDataAvailable called ");
                    eventAdapter.setEvents(cacheEvent, false);
                    eventRecycler.setAdapter(eventAdapter);
                    no_networkView.setVisibility(View.GONE);
                    eventRecycler.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isFragmentAttached = true ;
         iAttach = new IAttach() {
            @Override
            public void onAttachedcheck() {
                //Do any thing you want here.
                EventsFragment.this.appContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (eventAdapter != null && eventRecycler != null) {

                           if(isFragmentAttached){
                                eventRecycler.setVisibility(View.GONE);
                                no_networkView.setText(getString(R.string.no_connection));
                                no_networkView.setVisibility(View.VISIBLE);
                           }
                        }
                    }
                });
            }


        };

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        isFragmentAttached = true ;
        iAttach = new IAttach() {
            @Override
            public void onAttachedcheck() {
                //Do any thing you want here.
                EventsFragment.this.appContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (eventAdapter != null && eventRecycler != null) {

                            if(isFragmentAttached){
                                eventRecycler.setVisibility(View.GONE);
                                no_networkView.setText(getString(R.string.no_connection));
                                no_networkView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }


        };

    }

    @Override
    public void onDetach() {
        super.onDetach();
        isFragmentAttached = false ;
    }

    public interface IAttach{
        void onAttachedcheck();
    }

}
