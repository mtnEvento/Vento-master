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
import com.mtn.evento.Factory;
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
public class EventsFragment extends Fragment implements HomeScreenActivity.SearchRequestListener,HomeScreenActivity.SearchRegionRequestListener,Factory.EventsDataAvailableListener ,Factory.InternetDataListenter {

    private static RecyclerView eventRecycler;
    private static RecyclerView.LayoutManager layoutManager;
    static EventAdapter eventAdapter;
    private static ArrayList<Event> events ,cacheEvent;
    private static HomeScreenActivity appContext;
    private static TextView no_networkView;
    private static int dataCount ;

    public EventsFragment() {

        Log.d(LOGMESSAGE,"EventsFragment called") ;
    }
    public void setAppContext(HomeScreenActivity appContext){
        if( this.appContext == null){
            this.appContext = appContext ;

            if(eventAdapter == null){
                eventAdapter = new EventAdapter();
            }

            if(events == null){
                events = new ArrayList<>();
            }
            if(cacheEvent == null){
                cacheEvent = new ArrayList<>();
            }

            dataCount = 0;


        }
    }
    //On the event data is ready
    @Override
    public void onEventsDataAvailable(int count, ArrayList<Event> reservedResultSets) {
        // events.isEmpty();

        cacheEvent = reservedResultSets ;
        Log.d(LOGMESSAGE,"onEventsDataAvailable called  count :"+ count) ;
        if( dataCount <= count ){

            if(dataCount == count){

            }
            else if (dataCount < count){
                events = reservedResultSets ;
                eventAdapter.setEvents(events, false);
                eventRecycler.setAdapter(eventAdapter);
                no_networkView.setVisibility(View.GONE);
                eventRecycler.setVisibility(View.VISIBLE);
                dataCount =  count ;
            }

        }


    }

    @Override
    public void onInternetConnected() {


        if(eventAdapter != null && eventRecycler != null && events != null &&  events.size() > 0){
            eventAdapter.setEvents(events, false);
            eventRecycler.setAdapter(eventAdapter);
            no_networkView.setVisibility(View.GONE);
            eventRecycler.setVisibility(View.VISIBLE);
        }
        else
        {
            if(eventAdapter != null && eventRecycler != null){
                eventRecycler.setVisibility(View.GONE);
                no_networkView.setText( "Loading Event ....");
                no_networkView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onInternetDisconnected() {
        if(eventAdapter != null && eventRecycler != null && events != null && events.size()>0 ){

            eventAdapter.setEvents(events, false);
            eventRecycler.setAdapter(eventAdapter);
            no_networkView.setVisibility(View.GONE);
            eventRecycler.setVisibility(View.VISIBLE);
        }
        else
        {
            if(eventAdapter != null && eventRecycler != null ){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        eventRecycler.setVisibility(View.GONE);
                        no_networkView.setText( getString(R.string.no_connection));
                        no_networkView.setVisibility(View.VISIBLE);
                    }
                });

            }
        }

    }

    public interface EventFilter{
        public void onFilterEvent(String filterTerm, ArrayList<Event> events);
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
        if(Factory.isNetworkAndInternetAvailableNow()){
            if (eventRecycler != null && no_networkView != null ){
                if(cacheEvent != null && cacheEvent.size()> 0){
                    eventAdapter.setEvents(cacheEvent, false);
                    eventRecycler.setAdapter(eventAdapter);
                    no_networkView.setVisibility(View.GONE);
                    eventRecycler.setVisibility(View.VISIBLE);
                }
                else{
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
            }
            else{
                eventRecycler.setVisibility(View.GONE);
                no_networkView.setText( getString(R.string.no_connection));
                no_networkView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Factory.isNetworkAndInternetAvailableNow()){
            if (eventRecycler != null && no_networkView != null){
                if(cacheEvent != null){
                    eventAdapter.setEvents(cacheEvent, false);
                    eventRecycler.setAdapter(eventAdapter);
                    no_networkView.setVisibility(View.GONE);
                    eventRecycler.setVisibility(View.VISIBLE);
                }
                else{
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
            }
            else{

                eventRecycler.setVisibility(View.GONE);
                no_networkView.setText( getString(R.string.no_connection));
                no_networkView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public ArrayList<Event> onSearch(String query) {
        return null;
    }
    @Override
    public ArrayList<Event> onRegionSearch(String query, ViewPager vp) {
        if(!query.isEmpty()){
            if(query.contains("--region--")){
                if(events != null && !events.isEmpty() &&   eventAdapter != null && eventRecycler != null){
                    eventAdapter.setEvents(events,false);
                    eventRecycler.setAdapter(eventAdapter);
                    eventRecycler.setVisibility(View.VISIBLE);
                    no_networkView.setVisibility(View.GONE);

                }
                else
                {
                    ArrayList<Event> filteredEvents = new ArrayList<>();
                    for (Event e : events ) {
                        if( e != null  && e.getRegion() != null && e.getRegion().toLowerCase().contains(query)){
                            filteredEvents.add(e);
                        }
                    }
                    if(eventAdapter != null){
                        eventAdapter.setEvents(filteredEvents,true);
                        if( eventRecycler != null){
                            eventRecycler.setAdapter(eventAdapter);
                            eventRecycler.setVisibility(View.VISIBLE);
                            no_networkView.setVisibility(View.GONE);
                        }
                    }


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
                if(eventAdapter != null){
                    eventAdapter.setEvents(filteredEvents,true);
                    if( eventRecycler != null){
                        eventRecycler.setAdapter(eventAdapter);
                        eventRecycler.setVisibility(View.VISIBLE);
                        no_networkView.setVisibility(View.GONE);
                    }
                }
            }
        }
        return null;
    }
}
