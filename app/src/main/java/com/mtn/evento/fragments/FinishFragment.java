package com.mtn.evento.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mtn.evento.Evento;
import com.mtn.evento.R;
import com.mtn.evento.activities.BarcodeActivity;
import com.mtn.evento.activities.ReservationActivity;
import com.mtn.evento.data.Constants;
import com.mtn.evento.data.DisplayTicket;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.List;

import static com.mtn.evento.data.Constants.APP_USERNAME;
import static com.mtn.evento.data.Constants.APP_USER_PHONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FinishFragment extends Fragment implements View.OnClickListener {

     List<DisplayTicket> mDisplayTickets;
     String mTransactionId;
    public FinishFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_finish, container, false);
        TextView username = (TextView) view.findViewById(R.id.name);
        TextView number = (TextView) view.findViewById(R.id.phone);

        if (getActivity()!= null && getActivity().getApplication() !=null && ((Evento) getActivity().getApplication()).getSettings().contains(APP_USER_PHONE)) {
            String str_phone = ((Evento) getActivity().getApplication()).getSettings().getString(APP_USER_PHONE, "Phone Number");
            number.setText(  (str_phone == null || str_phone .isEmpty() )? "No Phone number": "Tel: "+str_phone );
        }

        if (getActivity() != null && getActivity().getApplication() !=null && ((Evento) getActivity().getApplication()).getSettings().contains(APP_USERNAME)) {
            String str_username = ((Evento) getActivity().getApplication()).getSettings().getString(APP_USERNAME, "Username");
            username.setText(str_username);
        }

        Button finish = (Button) view.findViewById(R.id.next);
        finish.setText("View Ticket(s)");
        Button transactionId = (Button) view.findViewById(R.id.transactionId);

        transactionId.setText(( mTransactionId != null  && mTransactionId.length() > 10)? mTransactionId.substring(0,7) + "..." : mTransactionId);

        transactionId.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                Toast.makeText(getActivity(), "Click to see detail", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        transactionId.setOnClickListener(this);
        finish.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.transactionId:
                openDialog();
                break;
            case R.id.next:
                Intent intent = new Intent(getActivity(), BarcodeActivity.class);
                intent.putExtra(Constants.TICKET,(Serializable) mDisplayTickets);
                startActivity(intent);
                break;
        }
    }

    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Transaction ID".toUpperCase());
        builder.setMessage(this.mTransactionId + "\n\n" + "Keep this Transaction ID for future reference");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }


    public void setValues(String transactionId, List<DisplayTicket> displayTickets) {

        mDisplayTickets = displayTickets;
        mTransactionId = transactionId;

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
