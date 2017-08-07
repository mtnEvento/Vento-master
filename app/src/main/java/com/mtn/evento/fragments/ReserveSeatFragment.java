package com.mtn.evento.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.clans.fab.FloatingActionButton;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mtn.evento.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReserveSeatFragment extends Fragment implements View.OnClickListener {
    Context context;
    ReservationHolder holder;
    public ReserveSeatFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_reserve_seat, container, false);
        holder = new ReservationHolder(view,this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu_add :
                int childs = holder.seats_group.getChildCount();
               View mView =   LayoutInflater.from( this.context).inflate(R.layout.add_seats,null);

                MaterialSpinner spinner = (MaterialSpinner) mView.findViewById(R.id.spinner);
                spinner.setItems(holder.ticketCategories);
                spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

                    @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                        Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                    }
                });
                holder.seats_group.addView(mView);

                break;
            case R.id.menu_remove:
                 childs = holder.seats_group.getChildCount();

                if( childs > 0)
                   holder.seats_group.removeViewAt(childs-1);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context ;
    }

    private static class ReservationHolder{
        String[]  ticketCategories;
       private FloatingActionButton menu_add ,menu_remove;
       private LinearLayout seats_group;
        public ReservationHolder(View holder, View.OnClickListener onClickListener){
            ticketCategories = new String[]{"Ice Cream Sandwich", "Jelly Bean", "KitKat", "Lollipop", "Marshmallow"};
            seats_group = (LinearLayout) holder.findViewById(R.id.seats_group);
            menu_add = (FloatingActionButton) holder.findViewById(R.id.menu_add);
            menu_add.setOnClickListener(onClickListener);
            menu_remove = (FloatingActionButton) holder.findViewById(R.id.menu_remove);
            menu_remove.setOnClickListener(onClickListener);
            MaterialSpinner spinner = (MaterialSpinner) holder.findViewById(R.id.spinner);
            spinner.setItems(ticketCategories);
            spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

                @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                    Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                }
            });
        }

    }
}
