package com.mtn.evento.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mtn.evento.R;
import com.mtn.evento.activities.ReservedDetailActivity;
import com.mtn.evento.data.Constants;
import com.mtn.evento.data.Event;
import com.mtn.evento.data.ReservedSeatData;
import com.mtn.evento.data.ResultSet;

import java.util.ArrayList;

import static com.mtn.evento.data.Constants.LOGMESSAGE;

public class ReservedEventsAdapter extends RecyclerView.Adapter<ReservedEventsAdapter.ReservedEventHolder> {

    ArrayList<ResultSet> reservedEvents;
    Context context;

    public ReservedEventsAdapter() {
        //Log.d(LOGMESSAGE, "adapter init ");
    }

    public void setReservedEvents(ArrayList<ResultSet> reservedEvents) {
        this.reservedEvents = reservedEvents;
       // Log.d(LOGMESSAGE, "adapter set: ");
    }
    @Override
    public ReservedEventHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_event,parent,false);
        context = parent.getContext();
        return new ReservedEventHolder(view).setReservedEvents(this.reservedEvents);
    }

    @Override
    public void onBindViewHolder(ReservedEventHolder reservedEventHolder, int position) {

        Glide.with(context)
                .load( reservedEvents.get(position).getmEvent().get(position).getBanner())
                .asBitmap()
                .into(reservedEventHolder.imageView) ;
        reservedEventHolder.title.setText(reservedEvents.get(position).getmEvent().get(position).getTitle());
        reservedEventHolder.venue.setText(reservedEvents.get(position).getmEvent().get(position).getVenue());
        reservedEventHolder.layout.setTag(new ReservedSeatData(reservedEvents.get(position).getmEvent().get(position),reservedEvents.get(position).getmDisplayTickets()));

    }

    @Override
    public int getItemCount() {
       // Log.d(LOGMESSAGE, "adapter size " + reservedEvents.size());
        return reservedEvents.size();
    }

    static class ReservedEventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView title,venue;
        RelativeLayout layout;
        Context context;
        ArrayList<ResultSet> reservedEvents;

        public ReservedEventHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            imageView = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            venue = (TextView) itemView.findViewById(R.id.venue);
            layout = (RelativeLayout) itemView.findViewById(R.id.layout);
            layout.setOnClickListener(this);
        }

        public ReservedEventHolder setReservedEvents(ArrayList<ResultSet> reservedEvents) {
            this.reservedEvents = reservedEvents;
            return this;
        }

        @Override
        public void onClick(View v) {
            //TODO get Bundle At event detail page and set it's values events.get(getAdapterPosition())
            ReservedSeatData reservedSeatData = (ReservedSeatData) v.getTag();
            Intent intent = new Intent(context, ReservedDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.RESERVED_SEAT,reservedSeatData);
            intent.putExtra(Constants.BUNDLE,bundle);
            context.startActivity(intent);
        }
    }
}
