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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.mtn.evento.R;
import com.mtn.evento.adapters.EventAdapter;
import com.mtn.evento.adapters.ReservedEventsAdapter;
import com.mtn.evento.data.Event;
import com.mtn.evento.data.ResultSet;
import com.mtn.evento.database.DatabaseHandler;

import java.util.ArrayList;

import static com.mtn.evento.data.Constants.LOGMESSAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReservedFragment extends Fragment {
    RecyclerView reservedRecycler;
    RecyclerView.LayoutManager layoutManager;
    ReservedEventsAdapter reservedEventsAdapter;
    ArrayList<ResultSet> reservedEvents;
    AppCompatActivity appContext;
    DatabaseHandler db;
    private FirebaseAuth mAuth;

    public ReservedFragment() {

        Log.d(LOGMESSAGE, "ReservedFragment: CALLED ");
        reservedEventsAdapter = new ReservedEventsAdapter();

    }

    public void setAppContext(AppCompatActivity appContext){
        this.appContext = appContext ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reserved, container, false);
        reservedRecycler = (RecyclerView) v.findViewById(R.id.reservedRecycler);
        layoutManager = new LinearLayoutManager(appContext);
        reservedRecycler.setLayoutManager(layoutManager);
        reservedRecycler.setHasFixedSize(true);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(isNetworkAndInternetAvailable()){

            if(mAuth == null){
                mAuth = FirebaseAuth.getInstance();
            }
            if(mAuth != null && mAuth.getCurrentUser() != null){

                db = new DatabaseHandler(appContext);
                reservedEventsAdapter.setReservedEvents(db.getAllreservedEvents());
                db.close();
                reservedRecycler.setAdapter(reservedEventsAdapter);
                reservedRecycler.invalidate();
            }
            else
            {
                reservedRecycler.invalidate();
            }
           //TODO: User not logged in
        }
        else
        {
            //TODO: Alert no network connection
            Toast.makeText(appContext,"No network connection available. Please check your network and try again!",Toast.LENGTH_LONG).show();
        }

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
        ConnectivityManager ConnectionManager=(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
