package com.mtn.evento.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mtn.evento.R;
import com.mtn.evento.activities.EventDetailActivity;
import com.mtn.evento.data.Constants;
import com.mtn.evento.data.CustomBitmap;
import com.mtn.evento.data.Event;

import java.util.ArrayList;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {

    ArrayList<Event> events;
    Context context;

    public EventAdapter() {

    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_event,parent,false);
        context = parent.getContext();
        return new EventHolder(view).setEvents(this.events);
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int position) {
        Glide.with(context)
                .load(events.get(position).getBanner())
                .asBitmap()
                .into(holder.imageView) ;
        holder.title.setText(events.get(position).getTitle());
        holder.venue.setText(events.get(position).getVenue());
        holder.layout.setTag(events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView title,venue;
        RelativeLayout layout;
        Context context;
        ArrayList<Event> events;

        public EventHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            imageView = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            venue = (TextView) itemView.findViewById(R.id.venue);
            layout = (RelativeLayout) itemView.findViewById(R.id.layout);
            layout.setOnClickListener(this);
        }

        public   EventHolder  setEvents(ArrayList<Event> events) {
            this.events = events;
            return this;
        }

        @Override
        public void onClick(View v) {

            Event event = (Event) v.getTag();
            Intent intent = new Intent(context, EventDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.EVENT,event);
            intent.putExtra(Constants.BUNDLE,bundle);
            //TODO: get Bundle At event detail page and set it's values events.get(getAdapterPosition())
            context.startActivity(intent);
        }
    }


}
