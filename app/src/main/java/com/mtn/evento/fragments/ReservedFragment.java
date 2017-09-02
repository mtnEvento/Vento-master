package com.mtn.evento.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
public class ReservedFragment extends Fragment implements HomeScreenActivity.LoginLogoutListener ,Factory.InternetDataListenter, Factory.ReservedSeatsDataAvailableListener {
    static RecyclerView reservedRecycler;
    static RecyclerView.LayoutManager layoutManager;
    static ReservedEventsAdapter reservedEventsAdapter;
    static HomeScreenActivity appContext;
    static private FirebaseAuth mAuth;
    static private TextView errorHandler;
    static private int innerCount;
     ArrayList<ResultSet> reservedEvents;

    public ReservedFragment() {
        Log.d(LOGMESSAGE, "ReservedFragment: instantiated ");
        reservedEventsAdapter = new ReservedEventsAdapter();
    }
    public void setAppContext(HomeScreenActivity appContext){
        if(this.appContext == null ){
            this.appContext = appContext ;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reserved, container, false);
        reservedRecycler = (RecyclerView) v.findViewById(R.id.reservedRecycler);
        errorHandler = (TextView) v.findViewById(R.id.no_seats);
        layoutManager = new LinearLayoutManager(appContext);
        reservedRecycler.setLayoutManager(layoutManager);
        reservedRecycler.setHasFixedSize(true);
        return v;
    }
    @Override
    public void onStart() {
        super.onStart();
        checkReservedSeats();
    }
    @Override
    public void onResume() {
        super.onResume();
        checkReservedSeats();
    }
    private void checkReservedSeats(){
        if(mAuth == null){
            mAuth = FirebaseAuth.getInstance();
        }
        if(mAuth != null && mAuth.getCurrentUser() != null){
            if(reservedEventsAdapter != null && reservedRecycler != null && appContext != null && appContext.getApplication() != null  && ((Evento) appContext.getApplication()).getDatabaseHandler() !=null )
            {
                reservedEvents = ((Evento) appContext.getApplication()).getDatabaseHandler().getAllreservedEvents();
                if(reservedEvents != null && reservedEvents.size() > 0 )
                {
                    errorHandler.setVisibility(View.GONE);
                    reservedEventsAdapter.setReservedEvents(reservedEvents);
                    reservedRecycler.setAdapter(reservedEventsAdapter);
                    reservedRecycler.invalidate();
                    reservedRecycler.setVisibility(View.VISIBLE);
                }
                else  if(reservedEvents != null && reservedEvents.size() == 0 )
                {
                    reservedRecycler.setVisibility(View.GONE);
                    reservedRecycler.invalidate();
                    errorHandler.setText(R.string.no_seats_reservred);
                    errorHandler.setVisibility(View.VISIBLE);
                }
                else
                {
                    reservedRecycler.setVisibility(View.GONE);
                    reservedRecycler.invalidate();
                    errorHandler.setText(R.string.no_seats_reservred);
                    errorHandler.setVisibility(View.VISIBLE);
                }
            }
        }
        else
        {
            if( reservedRecycler != null ) {
                reservedRecycler.removeAllViews();
                reservedRecycler.invalidate();
                reservedRecycler.setVisibility(View.GONE);
                errorHandler.setText(R.string.not_logged_in);
                errorHandler.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public boolean onLoginLogout(String which) {
        switch (which){
            case APP_LOGIN:

                if(reservedEventsAdapter != null && reservedRecycler != null){

                    reservedEvents = ((Evento) appContext.getApplication()).getDatabaseHandler().getAllreservedEvents();
                    if(reservedEvents != null){
                         reservedRecycler.removeAllViews();
                        reservedEventsAdapter.setReservedEvents(reservedEvents);
                        reservedRecycler.setAdapter(reservedEventsAdapter);
                        reservedRecycler.invalidate();
                        reservedRecycler.setVisibility(View.VISIBLE);
                        errorHandler.setVisibility(View.GONE);
                    }

                }

                return true;
            case APP_LOGOUT :
                if(reservedEventsAdapter != null && reservedRecycler != null){
                    reservedRecycler.removeAllViews();
                    reservedRecycler.invalidate();
                    reservedRecycler.setVisibility(View.GONE);
                    errorHandler.setText(R.string.not_logged_in);
                    errorHandler.setVisibility(View.VISIBLE);
                }

                return true;
        }
        return false;
    }
    @Override
    public void onInternetConnected() {

      /*/
      reservedRecycler.setVisibility(View.GONE);
      errorHandler.setText(R.string.no_connection);
      errorHandler.setVisibility(View.VISIBLE);
     /*/
    }
    @Override
    public void onInternetDisconnected() {
        /*/
        reservedRecycler.setVisibility(View.GONE);
        errorHandler.setText(R.string.no_connection);
        errorHandler.setVisibility(View.VISIBLE);
       /*/
    }
    @Override
    public void onReservedSeatsDataAvailable(int count, ArrayList<ResultSet> reservedResultSets) {
       if(innerCount <= count) {
           if(innerCount == count)
           {
               //do nothing
           }
           else  if(innerCount < count)
           {
               errorHandler.setVisibility(View.GONE);
               reservedEventsAdapter.setReservedEvents(reservedResultSets);
               reservedRecycler.setAdapter(reservedEventsAdapter);
               reservedRecycler.invalidate();
               reservedRecycler.setVisibility(View.VISIBLE);
               innerCount = count ;
           }
       }
    }
}
