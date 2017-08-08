package com.mtn.evento.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mtn.evento.R;
import com.mtn.evento.adapters.EventAdapter;
import com.mtn.evento.adapters.ReservedEventsAdapter;
import com.mtn.evento.data.Event;
import com.mtn.evento.database.DatabaseHandler;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReservedFragment extends Fragment {
    RecyclerView reservedRecycler;
    RecyclerView.LayoutManager layoutManager;
    ReservedEventsAdapter reservedEventsAdapter;
    ArrayList<Event> reservedEvents;
    AppCompatActivity appContext;

    public ReservedFragment() {
        reservedEvents = new ArrayList<>();
        reservedEventsAdapter = new ReservedEventsAdapter();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reserved, container, false);
        reservedRecycler = (RecyclerView) v.findViewById(R.id.reservedRecycler);
        layoutManager = new LinearLayoutManager(getContext());
        reservedRecycler.setLayoutManager(layoutManager);
        reservedRecycler.setHasFixedSize(true);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DatabaseHandler db = new DatabaseHandler(appContext);
        reservedEventsAdapter.setReservedEvents(db.getAllreservedEvents());
        reservedRecycler.setAdapter(reservedEventsAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.appContext = (AppCompatActivity) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.appContext = (AppCompatActivity) activity;
    }
}
