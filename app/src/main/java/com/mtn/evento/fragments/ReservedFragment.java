package com.mtn.evento.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.mtn.evento.Evento;
import com.mtn.evento.Factory;
import com.mtn.evento.R;
import com.mtn.evento.activities.HomeScreenActivity;
import com.mtn.evento.adapters.EventAdapter;
import com.mtn.evento.adapters.ReservedEventsAdapter;
import com.mtn.evento.data.Event;
import com.mtn.evento.data.ResultSet;
import com.mtn.evento.database.DatabaseHandler;

import java.util.ArrayList;

import static com.mtn.evento.data.Constants.APP_LOGIN;
import static com.mtn.evento.data.Constants.APP_LOGOUT;
import static com.mtn.evento.data.Constants.LOGMESSAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReservedFragment extends Fragment implements HomeScreenActivity.LoginLogoutListener, HomeScreenActivity.SearchRequestListener, SwipeRefreshLayout.OnRefreshListener {
    static RecyclerView reservedRecycler;
    static RecyclerView.LayoutManager layoutManager;
    static ReservedEventsAdapter reservedEventsAdapter;
    static HomeScreenActivity appContext;
    static private FirebaseAuth mAuth;
    static private TextView errorHandler;
    static private int innerCount;
    static ArrayList<ResultSet> reservedEvents,cachedReservedEvents;
    static SwipeRefreshLayout refreshLayout ;
    private volatile boolean isFragmentAttached, resumed; ;


    public ReservedFragment() {
        Log.d(LOGMESSAGE, "ReservedFragment: instantiated ");

    }
    public void setAppContext(HomeScreenActivity appContext){
        if(this.appContext == null ){
            this.appContext = appContext ;

            reservedEvents = new ArrayList<>();
            cachedReservedEvents = new ArrayList<>();
            Log.d(LOGMESSAGE, "ReservedFragment appContext: instantiated ");
            reservedEvents = ((Evento) (appContext).getApplication()).getDatabaseHandler().getAllreservedEvents();
            cachedReservedEvents =  ((Evento) (appContext).getApplication()).getDatabaseHandler().getAllreservedEvents();
            Log.d(LOGMESSAGE, "ReservedFragment reservedEvents : instantiated " +reservedEvents);
            Log.d(LOGMESSAGE, "ReservedFragment cachedReservedEvents : instantiated " +cachedReservedEvents);
            reservedEventsAdapter = new ReservedEventsAdapter();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reserved, container, false);
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.refresher);
        reservedRecycler = (RecyclerView) v.findViewById(R.id.reservedRecycler);
        errorHandler = (TextView) v.findViewById(R.id.no_seats);
        layoutManager = new LinearLayoutManager(appContext);
        reservedRecycler.setLayoutManager(layoutManager);
        reservedRecycler.setHasFixedSize(true);
        return v;
    }

    @Override
    public void onResume() {
        checkReservedSeats();
        super.onResume();
        Log.d(LOGMESSAGE, "ReservedFragment: onResume called ");

        resumed = true ;
    }
    private void checkReservedSeats(){

        if(FirebaseAuth.getInstance().getCurrentUser() != null){

                if(cachedReservedEvents != null && cachedReservedEvents.size() > 0 )
                {
                    Log.d(LOGMESSAGE, "ReservedFragment:2 cachedReservedEvents "+ cachedReservedEvents);
                    reservedEventsAdapter.setReservedEvents(cachedReservedEvents,false);
                    reservedRecycler.setAdapter(reservedEventsAdapter);
                    reservedRecycler.invalidate();
                    errorHandler.setVisibility(View.GONE);
                    refreshLayout.setVisibility(View.VISIBLE);
                }
                else
                {

                    reservedRecycler.invalidate();
                    errorHandler.setText(R.string.no_seats_reservred);
                    errorHandler.setTextSize(3,12);
                    refreshLayout.setVisibility(View.GONE);
                    errorHandler.setVisibility(View.VISIBLE);
                    Log.d(LOGMESSAGE, "ReservedFragment:2 no reserved seats :  "+ cachedReservedEvents);
                }

            Log.d(LOGMESSAGE, "ReservedFragment:2 signin "+ cachedReservedEvents);
        }
        else
        {
            Log.d(LOGMESSAGE, "ReservedFragment:2 signout "+ cachedReservedEvents);
                reservedRecycler.invalidate();
                errorHandler.setText(R.string.not_logged_in);
                errorHandler.setTextSize(3,12);
                refreshLayout.setVisibility(View.GONE);
                errorHandler.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public boolean onLoginLogout(String which) {

            switch (which){
                case APP_LOGIN:

//                    if(reservedEventsAdapter != null && reservedRecycler != null){
//                        if(cachedReservedEvents != null){
//                            reservedRecycler.setVisibility(View.VISIBLE);
//                            errorHandler.setVisibility(View.GONE);
//                        }
//                    }
                    //if()
                    return true;
                case APP_LOGOUT :
                    if(reservedEventsAdapter != null && reservedRecycler != null){
                        refreshLayout.setVisibility(View.GONE);
                        errorHandler.setText(R.string.not_logged_in);
                        errorHandler.setTextSize(3,12);
                        errorHandler.setVisibility(View.VISIBLE);
                    }

                    return true;
            }
        return false;
    }


    @Override
    public void onSearch(String query, int adapterPosition) {
        Log.d(LOGMESSAGE, "ReservedFragment: query: " + query);
       if( !query.isEmpty() ) {
           if (adapterPosition == 1) {

               Log.d(LOGMESSAGE, "ReservedFragment: query: " + query);

               if (reservedEvents != null) {
                   if ( reservedEvents.size() > 0 && !query.isEmpty()) {
                       Log.d(LOGMESSAGE, "ReservedFragment: query 2: " + query);
                       ArrayList<ResultSet> filteredResultSet = new ArrayList<>();
                       filteredResultSet.clear();
                       for (int i = 0; i < reservedEvents.size(); i++) {

                           if (reservedEvents.get(i) != null)
                           {

                               ResultSet resultSet = reservedEvents.get(i);
                               if (resultSet != null)
                               {
                                   if (resultSet.getmEvent() != null) {
                                       Event events = resultSet.getmEvent();
                                       if (events != null) {
                                           if (events != null && events.getTitle() != null && events.getTitle().toLowerCase().contains(query.toLowerCase())) {

                                               Log.d(LOGMESSAGE, "ReservedFragment: resultSet: " + resultSet);
                                               Log.d(LOGMESSAGE, "filtered event added");
                                               filteredResultSet.add(resultSet);
                                           }
                                       }
                                   }

                               }
                           }
                       }

                       if ( filteredResultSet.size() > 0 && !query.isEmpty()) {
                           errorHandler.setVisibility(View.GONE);
                           reservedEventsAdapter.setReservedEvents(filteredResultSet,true);
                           reservedRecycler.setAdapter(reservedEventsAdapter);
                          // reservedRecycler.invalidate();
                           refreshLayout.setVisibility(View.VISIBLE);
                       }
                       else if( filteredResultSet.size() <= 0 && !query.isEmpty()){
                           refreshLayout.setVisibility(View.GONE);
                           errorHandler.setText("No match found");
                           errorHandler.setTextSize(3, 12);
                           errorHandler.setVisibility(View.VISIBLE);
                       }
                       else if(query.isEmpty()){
                           if( cachedReservedEvents != null ){
                               errorHandler.setVisibility(View.GONE);
                               reservedEventsAdapter.setReservedEvents(cachedReservedEvents,false);
                               reservedRecycler.setAdapter(reservedEventsAdapter);
                               reservedRecycler.invalidate();
                               refreshLayout.setVisibility(View.VISIBLE);
                           }
                       }

                   }
                   else
                   {
                          //TODO: no match found
                       if(query.isEmpty()){
                           if( cachedReservedEvents != null ){
                               errorHandler.setVisibility(View.GONE);
                               reservedEventsAdapter.setReservedEvents(cachedReservedEvents,false);
                               reservedRecycler.setAdapter(reservedEventsAdapter);
                               reservedRecycler.invalidate();
                               refreshLayout.setVisibility(View.VISIBLE);
                           }
                       }

                   }

               }
               else
               {
                   //TODO: no match found

               }
           }
       }
       else{
           if( cachedReservedEvents != null ){
               errorHandler.setVisibility(View.GONE);
               reservedEventsAdapter.setReservedEvents(cachedReservedEvents,false);
               reservedRecycler.setAdapter(reservedEventsAdapter);
               reservedRecycler.invalidate();
               refreshLayout.setVisibility(View.VISIBLE);
           }
       }
    }

    @Override
    public void onSearchQueryEmpty(int adapterPosition) {
        if (adapterPosition == 1) {
            if( cachedReservedEvents != null ){
                errorHandler.setVisibility(View.GONE);
                reservedEventsAdapter.setReservedEvents(cachedReservedEvents,false);
                reservedRecycler.setAdapter(reservedEventsAdapter);
                reservedRecycler.invalidate();
                refreshLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onPause() {
        Log.d(LOGMESSAGE, "ReservedFragment: onPause called ");
        resumed = false;
        if(appContext != null &&  appContext.getSearchView() != null){
            appContext.getSearchView().clearFocus();
        }
        super.onPause();

    }

    @Override
    public void onRefresh() {
        if(appContext != null ){
            appContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   if(refreshLayout != null)
                   {
                       refreshLayout.post(new Runnable() {
                           @Override
                           public void run() {

                               cachedReservedEvents =  ((Evento) (appContext).getApplication()).getDatabaseHandler().getAllreservedEvents();
                               if( cachedReservedEvents != null ){
                                   errorHandler.setVisibility(View.GONE);
                                   reservedEventsAdapter.setReservedEvents(cachedReservedEvents,false);
                                   reservedRecycler.setAdapter(reservedEventsAdapter);
                                   reservedRecycler.invalidate();
                                   refreshLayout.setVisibility(View.VISIBLE);
                                   refreshLayout.setRefreshing(false);
                               }

                               refreshLayout.setRefreshing(false);
                           }
                       });
                   }
                }
            });
        }
    }
}
