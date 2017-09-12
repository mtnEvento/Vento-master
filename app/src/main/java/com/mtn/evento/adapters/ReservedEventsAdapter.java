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
import com.mtn.evento.activities.ReservedDetailActivity;
import com.mtn.evento.data.Constants;
import com.mtn.evento.data.ReservedSeatData;
import com.mtn.evento.data.ResultSet;
import com.mtn.evento.utils.Saver;

import java.util.ArrayList;

public class ReservedEventsAdapter extends RecyclerView.Adapter<ReservedEventsAdapter.ReservedEventHolder> {

    private static View view;
    ArrayList<ResultSet> collectedResult, reservedEvents,filteredEvents;
    Context context;
    private boolean filter;

    public ReservedEventsAdapter(){}

    public void setReservedEvents(ArrayList<ResultSet> reservedEvents, boolean shouldFilter) {
            filter = shouldFilter ;
            this.collectedResult = reservedEvents;
            if(filter){
                this.filteredEvents = reservedEvents;
            }
            else
            {
                this.reservedEvents = reservedEvents;
            }
    }
    @Override
    public ReservedEventHolder onCreateViewHolder(ViewGroup parent, int i) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_event,parent,false);
        context = parent.getContext();
        return new ReservedEventHolder(view).setReservedEvents(this.collectedResult,filter);
    }

    @Override
    public void onBindViewHolder(ReservedEventHolder holder, int position) {

        if(filter)
        {
            Glide.with(context)
                    .load( holder.filteredEvents.get(position).getmEvent().getBanner())
                    .asBitmap()
                    .into(holder.imageView) ;
            holder.title.setText(holder.filteredEvents.get(position).getmEvent().getTitle());
            holder.venue.setText(holder.filteredEvents.get(position).getmEvent().getVenue());
            holder.layout.setTag(new ReservedSeatData(holder.filteredEvents.get(position).getmEvent(),holder.filteredEvents.get(position).getmDisplayTickets()));

        }
        else
        {
            Glide.with(context)
                    .load(holder.reservedEvents.get(position).getmEvent().getBanner())
                    .asBitmap()
                    .into(holder.imageView) ;
            holder.title.setText(holder.reservedEvents.get(position).getmEvent().getTitle());
            holder.venue.setText(reservedEvents.get(position).getmEvent().getVenue());
            holder.layout.setTag(new ReservedSeatData(holder.reservedEvents.get(position).getmEvent(),holder.reservedEvents.get(position).getmDisplayTickets()));

        }

    }

    @Override
    public int getItemCount() {
       return  this.collectedResult.size();
    }

    static class ReservedEventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView title,venue;
        RelativeLayout layout;
        Context context;
        ArrayList<ResultSet> reservedEvents,filteredEvents;

        public ReservedEventHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            imageView = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            venue = (TextView) itemView.findViewById(R.id.venue);
            layout = (RelativeLayout) itemView.findViewById(R.id.layout);
            layout.setOnClickListener(this);
        }

        public ReservedEventHolder setReservedEvents(ArrayList<ResultSet> reservedEvents, boolean filter) {

            if(filter){
                this. filteredEvents  = reservedEvents;
            }
            else
            {
                this.reservedEvents = reservedEvents;
            }
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
