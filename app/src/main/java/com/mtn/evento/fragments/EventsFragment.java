package com.mtn.evento.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import com.mtn.evento.activities.EventDetailActivity;
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
public class EventsFragment extends Fragment implements HomeScreenActivity.SearchRequestListener, HomeScreenActivity.SearchRegionRequestListener, Factory.InternetDataListenter, SwipeRefreshLayout.OnRefreshListener {

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
    private static SwipeRefreshLayout refreshLayout;

    public EventsFragment() {
        Log.d(LOGMESSAGE,"EventsFragment called") ;
        regionFiltered = new ArrayList<>();
    }
    public void setAppContext(HomeScreenActivity appContext){
        if( this.appContext == null){
            this.appContext = appContext ;
            if(this.appContext == null)
            {
                this.setAppContext((HomeScreenActivity) getContext());
            }

            if(eventAdapter == null){
                eventAdapter = new EventAdapter();
            }
        }
    }

    @Override
    public void onInternetConnected() {}
    @Override
    public void onInternetDisconnected() {
        if(isFragmentAttached)
        {
            if(iAttach != null){
                iAttach.onAttachedcheck();
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresher);
        refreshLayout.setOnRefreshListener(this);
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
        initFirebase();
        if (Factory.isNetworkAndInternetAvailableNow())
        {
             if (refreshLayout != null && no_networkView != null)
             {
                  if (cacheEvent != null && cacheEvent.size() > 0) {
                      eventAdapter.setEvents(cacheEvent, false);
                      eventRecycler.setAdapter(eventAdapter);
                      no_networkView.setVisibility(View.GONE);
                      refreshLayout.setVisibility(View.VISIBLE);
                  }
                  else
                  {
                      refreshLayout.setVisibility(View.GONE);
                      no_networkView.setText("Loading Events...");
                      no_networkView.setVisibility(View.VISIBLE);
                  }
             }
        }
        else
        {
            if ( refreshLayout!= null &&  eventRecycler != null && no_networkView != null && cacheEvent != null && cacheEvent.size() > 0) {
                eventAdapter.setEvents(cacheEvent, false);
                eventRecycler.setAdapter(eventAdapter);
                no_networkView.setVisibility(View.GONE);
                refreshLayout.setVisibility(View.VISIBLE);
            }
            else
            {
                refreshLayout.setVisibility(View.GONE);
                no_networkView.setText(getString(R.string.no_connection));
                no_networkView.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if(Factory.isNetworkAndInternetAvailableNow()){
            if (eventRecycler != null && no_networkView != null){
                if (cacheEvent != null && cacheEvent.size() > 0) {
                    eventAdapter.setEvents(cacheEvent, false);
                    eventRecycler.setAdapter(eventAdapter);
                    no_networkView.setVisibility(View.GONE);
                    refreshLayout.setVisibility(View.VISIBLE);
                } else {
                    refreshLayout.setVisibility(View.GONE);
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
                refreshLayout.setVisibility(View.VISIBLE);
            } else {

                refreshLayout.setVisibility(View.GONE);
                no_networkView.setText( getString(R.string.no_connection));
                no_networkView.setVisibility(View.VISIBLE);
            }
        }
        if (isNotFirstResume)
        {
            restoreSpinnerState();
        }
    }

    @Override
    public void onSearch(String query,int adapterPosition) {
        if( adapterPosition == 0)
        {
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
            }
            else
            {
                for (Event e : cacheEvent) {
                    if( e != null  && e.getTitle() != null && e.getTitle().toLowerCase().contains(query)){
                        filteredEvents.add(e);
                        Log.d(LOGMESSAGE, "filtered event added");
                    }
                }
            }

            if (eventAdapter != null) {

                Log.d(LOGMESSAGE, "filteredEvents.size() : " + filteredEvents.size());

                if (filteredEvents.size() > 0)
                {
                    if (eventRecycler != null)
                    {
                        eventAdapter.setEvents(filteredEvents, true);
                        eventRecycler.setAdapter(eventAdapter);
                        refreshLayout.setVisibility(View.VISIBLE);
                        no_networkView.setVisibility(View.GONE);
                    }
                }
                else
                {
                    refreshLayout.setVisibility(View.GONE);
                    no_networkView.setText("NO MATCH FOUND");
                    no_networkView.setTextSize(3,12);
                    no_networkView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onSearchQueryEmpty(int adapterPosition) {

    }

    @Override
    public void onRegionSearch(String query) {
        if(!query.isEmpty()){
            currentSpinnerSelection = query;
            isNotFirstResume = true;
            if (query.contains("--region--")) {
                if (cacheEvent != null && !cacheEvent.isEmpty() && eventAdapter != null && eventRecycler != null) {
                    eventAdapter.setEvents(cacheEvent, false);
                    eventRecycler.setAdapter(eventAdapter);
                    refreshLayout.setVisibility(View.VISIBLE);
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
                            refreshLayout.setVisibility(View.VISIBLE);
                            no_networkView.setVisibility(View.GONE);
                        }
                    } else {
                        refreshLayout.setVisibility(View.GONE);
                        no_networkView.setText("NO EVENTS AVAILABLE");
                        no_networkView.setTextSize(3,12);
                        no_networkView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
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
                refreshLayout.setVisibility(View.VISIBLE);
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
                        refreshLayout.setVisibility(View.VISIBLE);
                        no_networkView.setVisibility(View.GONE);
                    }
                } else {
                    refreshLayout.setVisibility(View.GONE);
                    no_networkView.setText("NO EVENTS AVAILABLE");
                    no_networkView.setTextSize(3,12);
                    no_networkView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onRefresh() {
        if(appContext != null )
        {
            Log.d(LOGMESSAGE, "appContext is not null onRefresh");
            appContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(refreshLayout != null)
                    {
                        if(isNetworkAndInternetAvailable())
                        {

                            refreshLayout.post(new Runnable() {
                                @Override
                                public void run() {

                                    eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Log.d(LOGMESSAGE, "addListenerForSingleValueEvent onDataChange: dataSnapshot " + dataSnapshot);
                                            if(dataSnapshot != null && dataSnapshot.getValue() != null)
                                            {
                                                events.clear();
                                                for (DataSnapshot aDataSnapshot : dataSnapshot.getChildren()) {

                                                    Object o = aDataSnapshot.getValue();
                                                    if( o instanceof  Event){
                                                        Event evt = aDataSnapshot.getValue(Event.class);
                                                        events.add(evt);
                                                    }
                                                    else
                                                    {
                                                        Log.d(LOGMESSAGE, "Object not an instanceof");
                                                        Event evt = aDataSnapshot.getValue(Event.class);
                                                        events.add(evt);
                                                    }

                                                }
                                                if(dataSnapshot.getChildrenCount()> 0){
                                                    refreshLayout.setRefreshing(false);
                                                    cacheEvent = events;
                                                    Log.d(LOGMESSAGE, "addListenerForSingleValueEvent onDataChange: cacheEvent " + cacheEvent);
                                                    EventsFragment.this.appContext.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            eventAdapter.setEvents(cacheEvent, false);
                                                            eventRecycler.setAdapter(eventAdapter);
                                                            no_networkView.setVisibility(View.GONE);
                                                            refreshLayout.setVisibility(View.VISIBLE);
                                                            Log.d(LOGMESSAGE, "addListenerForSingleValueEvent onEventsDataAvailable called ");
                                                            refreshLayout.setRefreshing(false);
                                                        }
                                                    });
                                                }
                                                else
                                                {
                                                    refreshLayout.setRefreshing(false);
                                                    Log.d(LOGMESSAGE, "addListenerForSingleValueEvent dataSnapshot has no children");
                                                }

                                                refreshLayout.setRefreshing(false);

                                            }
                                            else
                                            {
                                                refreshLayout.setRefreshing(false);
                                                Log.d(LOGMESSAGE, "addListenerForSingleValueEvent dataSnapshot value is null ");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            refreshLayout.setRefreshing(false);
                                        }
                                    });
                                }
                            });
                        }
                        else
                        {
                            refreshLayout.setRefreshing(false);
                            AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
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

                    }
                }
            });
        }
        else
        {
            Log.d(LOGMESSAGE, "appContext is null onRefresh");
        }
    }


    public interface EventFilter {
        public void onFilterEvent(String filterTerm, ArrayList<Event> events);
    }

    class EventValueListener implements ValueEventListener {
        public EventValueListener() {}
        @Override
        public void onDataChange(DataSnapshot dataSnapshot)
        {
            Log.d(LOGMESSAGE, "onDataChange: dataSnapshot " + dataSnapshot);
            if(dataSnapshot != null && dataSnapshot.getValue() != null)
            {
                events.clear();
                for (DataSnapshot aDataSnapshot : dataSnapshot.getChildren()) {

                    Object o = aDataSnapshot.getValue();
                    if( o instanceof  Event){
                        Event evt = aDataSnapshot.getValue(Event.class);
                        events.add(evt);
                    }
                    else
                    {
                        Log.d(LOGMESSAGE, "Object not an instanceof");
                        Event evt = aDataSnapshot.getValue(Event.class);
                        events.add(evt);
                    }

                }
                if(dataSnapshot.getChildrenCount()> 0){
                    cacheEvent = events;
                    Log.d(LOGMESSAGE, "onDataChange: cacheEvent " + cacheEvent);
                    EventsFragment.this.appContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            eventAdapter.setEvents(cacheEvent, false);
                            eventRecycler.setAdapter(eventAdapter);
                            no_networkView.setVisibility(View.GONE);
                            refreshLayout.setVisibility(View.VISIBLE);
                            Log.d(LOGMESSAGE, "onEventsDataAvailable called ");
                        }
                    });
                }
                else
                {
                    Log.d(LOGMESSAGE, "dataSnapshot has no children");
                }

            }
            else
            {
                Log.d(LOGMESSAGE, "dataSnapshot value is null ");
            }
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
                    if (eventAdapter != null && eventRecycler != null)
                    {
                        if(isFragmentAttached)
                        {
                            refreshLayout.setVisibility(View.GONE);
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

    public boolean isInternetOn() {

        ConnectivityManager connec =(ConnectivityManager) appContext .getSystemService(appContext.getBaseContext().CONNECTIVITY_SERVICE);
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
        ConnectivityManager ConnectionManager=(ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

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
