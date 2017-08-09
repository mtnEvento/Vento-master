package com.mtn.evento.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.github.clans.fab.FloatingActionButton;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mtn.evento.Payment;
import com.mtn.evento.R;
import com.mtn.evento.data.Constants;
import com.mtn.evento.data.Event;
import com.mtn.evento.data.ServerConnector;
import com.mtn.evento.database.DatabaseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.mtn.evento.data.Constants.LOGMESSAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReserveSeatFragment extends Fragment implements View.OnClickListener {
    Context context;
    private ReservationHolder holder;
    private Event mEvent;
    private Button makePayment;
    public ReserveSeatFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_reserve_seat, container, false);
        makePayment = (Button) view.findViewById(R.id.makePayment);
        makePayment.setOnClickListener(this);
        holder = new ReservationHolder(view,this);
        Bundle bag = getArguments();
        mEvent = (Event) bag.getSerializable(Constants.EVENT);
        makePayment.setTag(mEvent);
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

            case R.id.makePayment:
                childs = holder.seats_group.getChildCount();
                if(childs > 0){

                }

            confirmPayment("1000.00");
                break;

        }
    }

    private void confirmPayment(String amount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirm Reservation");
        builder.setMessage("Total Price : "+ amount);
        builder.setNegativeButton("Cancel", confirmListener());
        builder.setPositiveButton("Confirm", confirmListener());

        builder.create().show();
    }

    private DialogInterface.OnClickListener confirmListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        makePayment();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                }
            }
        };
    }


    private void makePayment() {

        HashMap<String,String> contentValue = new HashMap<>();
        contentValue.put("CustomerName","Daniel");
        contentValue.put("CustomerMsisdn","233241361156");
        contentValue.put("CustomerEmail","user@gmail.com");
        contentValue.put("Channel","mtn-gh");
        contentValue.put("Amount","1");
        contentValue.put("Description","T Shirt");

        String url = "http://10.13.56.107/payment.php";

        ServerConnector.newInstance(url).setParameters(contentValue).attachListener(new ServerConnector.Callback() {
            @Override
            public void getResult(String result) {
                if(result == null || result.isEmpty()){return; }
                Log.d(LOGMESSAGE, "getResult: " + result);
                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject data =  object.getJSONObject("data");
                    String transactionId = data.getString("TransactionId");

                    DatabaseHandler db = new DatabaseHandler(getContext());
                    db.addEvent(mEvent);

                    Log.d(LOGMESSAGE, "TransactionId : " + transactionId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).connectToServer();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }




}
