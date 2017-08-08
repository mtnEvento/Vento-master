package com.mtn.evento.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mtn.evento.R;
import com.mtn.evento.adapters.EventAdapter;
import com.mtn.evento.adapters.ReservedEventsAdapter;
import com.mtn.evento.data.Event;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReservedFragment extends Fragment {
    RecyclerView reservedRecycler;
    RecyclerView.LayoutManager layoutManager;
    ReservedEventsAdapter reservedEventsAdapter;
    ArrayList<Event> reservedEvents;

    public ReservedFragment() {
        reservedEvents = new ArrayList<>();
        reservedEventsAdapter = new ReservedEventsAdapter();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reserved, container, false);
        RecyclerView reservedRecycler = (RecyclerView) v.findViewById(R.id. reservedRecycler);
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        reservedRecycler = (RecyclerView) view.findViewById(R.id.eventRecycler);
        layoutManager = new LinearLayoutManager(getContext());
        reservedRecycler.setLayoutManager(layoutManager);
        reservedRecycler.setHasFixedSize(true);
        return v;
    }

}
