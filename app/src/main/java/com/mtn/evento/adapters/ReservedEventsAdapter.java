package com.mtn.evento.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mtn.evento.R;
import com.mtn.evento.activities.EventDetailActivity;
import com.mtn.evento.activities.ReservedDetailActivity;
import com.mtn.evento.data.Constants;
import com.mtn.evento.data.Event;

import java.util.ArrayList;


/**
 * Created by Summy on 8/8/2017.
 */

public class ReservedEventsAdapter extends RecyclerView.Adapter<ReservedEventsAdapter.ReservedEventHolder> {

    ArrayList<Event> reservedEvents;
    Context context;
    public ReservedEventsAdapter() {

    }

    public void setReservedEvents(ArrayList<Event> reservedEvents) {
        this.reservedEvents = reservedEvents;
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
                .load(reservedEvents.get(position).getBanner())
                .asBitmap()
                .into(reservedEventHolder.imageView) ;
        reservedEventHolder.title.setText(reservedEvents.get(position).getTitle());
        reservedEventHolder.venue.setText(reservedEvents.get(position).getVenue());
        reservedEventHolder.layout.setTag(reservedEvents.get(position));
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class ReservedEventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView title,venue;
        RelativeLayout layout;
        Context context;
        ArrayList<Event> reservedEvents;

        public ReservedEventHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            imageView = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            venue = (TextView) itemView.findViewById(R.id.venue);
            layout = (RelativeLayout) itemView.findViewById(R.id.layout);
            layout.setOnClickListener(this);
        }

        public ReservedEventHolder setReservedEvents(ArrayList<Event> reservedEvents) {
            this.reservedEvents = reservedEvents;
            return this;
        }

        @Override
        public void onClick(View v) {
            //TODO get Bundle At event detail page and set it's values events.get(getAdapterPosition())
            Event event = (Event) v.getTag();
            Intent intent = new Intent(context, ReservedDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.EVENT,event);
            intent.putExtra(Constants.BUNDLE,bundle);
            context.startActivity(intent);
        }
    }


}
