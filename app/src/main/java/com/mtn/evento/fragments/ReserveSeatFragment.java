package com.mtn.evento.fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mtn.evento.Evento;
import com.mtn.evento.Payment;
import com.mtn.evento.R;
import com.mtn.evento.TelephonyInfo;
import com.mtn.evento.activities.BarcodeActivity;
import com.mtn.evento.data.Constants;
import com.mtn.evento.data.DisplayTicket;
import com.mtn.evento.data.EncodeData;
import com.mtn.evento.data.Event;
import com.mtn.evento.data.ServerConnector;
import com.mtn.evento.data.SinglePurchaseData;
import com.mtn.evento.data.Ticket;
import com.mtn.evento.database.DatabaseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.mtn.evento.data.Constants.LOGMESSAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReserveSeatFragment extends Fragment implements View.OnClickListener {
    static String transactionId;
    Context context;
    MenuItem cartView;
    PaymentListener mPaymentListener;
    private ReservationHolder holder;
    private Event mEvent;
    private Button makePayment;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private de.hdodenhof.circleimageview.CircleImageView event_image;
    ProgressDialog loading;


    public ReserveSeatFragment() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }


    public void setCartView(MenuItem cartView ){
        this.cartView = cartView ;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_reserve_seat, container, false);
        makePayment = (Button) view.findViewById(R.id.makePayment);
        event_image = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.event_image);

        makePayment.setOnClickListener(this);
        Bundle bag = getArguments();
        mEvent = (Event) bag.getSerializable(Constants.EVENT);
        makePayment.setTag(mEvent);
        holder = new ReservationHolder(view,this,mEvent);
        Glide.with(context)
                .load(mEvent.getBanner())
                .asBitmap()
                .into(event_image) ;

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
                CardView view = (CardView) mView;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 32);
                view.setLayoutParams(params);
                holder.seats_group.addView(mView);

                if( cartView != null &&  cartView.getTitle()!= null){

                    if(!cartView.getTitle().toString().isEmpty()){
                        String numberOnly = cartView.getTitle().toString().replaceAll("[^0-9]", "");
                        cartView.setTitle( "Cart : "+ ( (Integer.parseInt(numberOnly)) + 1));
                    }
                }
                break;
            case R.id.menu_remove:
                 childs = holder.seats_group.getChildCount();

                if( childs > 0)
                {
                    holder.seats_group.removeViewAt(childs-1);
                    if( cartView != null &&  cartView.getTitle()!= null){

                        if(!cartView.getTitle().toString().isEmpty()){
                            String numberOnly = cartView.getTitle().toString().replaceAll("[^0-9]", "");
                            cartView.setTitle( "Cart : "+ ( (Integer.parseInt(numberOnly))- 1));
                        }
                    }
                }
                break;

            case R.id.makePayment:


                childs = holder.seats_group.getChildCount();
                int errrorCount = 0  ;
                String amount = "";
                String strQnt = "";
                if(!cartView.getTitle().toString().isEmpty()){
                       String numberOnly = cartView.getTitle().toString().replaceAll("[^0-9]", "");

                       if (Integer.parseInt(numberOnly) > 0 ){

                           ArrayList<SinglePurchaseData> singlePurchaseDataArrayList = new ArrayList<>();

                           for (int i = 0; i < childs; i++) {
                                 double dubleAmount= 0.00;
                                 int quantity = 0 ;
                                 SinglePurchaseData  singlePurchaseData = new SinglePurchaseData();
                                 CardView crd = (CardView) holder.seats_group.getChildAt(i);
                                 LinearLayout layout = (LinearLayout) crd.getChildAt(0);

                                 MaterialSpinner spiner = (MaterialSpinner) layout.getChildAt(0);
                                 String typeName = holder.ticketCategories[spiner.getSelectedIndex()].toUpperCase();
                                 ArrayList<Ticket>   tickets  =  mEvent.getTicket_type();
                                 ArrayList<String>   strTickets  = new ArrayList<>();
                                 for (Ticket  ticket : tickets  )
                                 {
                                     if (typeName.contentEquals(ticket.getName().toUpperCase())) {
                                          amount =   ticket.getAmount().trim();
                                          dubleAmount = Double.parseDouble(amount);
                                          singlePurchaseData.setType(typeName);
                                          break;
                                      }
                                 }

                               AppCompatEditText qty = (AppCompatEditText) layout.getChildAt(1);
                               if (TextUtils.isEmpty(qty.getText().toString().trim())) {
                                   qty.setError("Quantity Required!");
                                   errrorCount += 1 ;
                                   break;
                               } else {
                                   qty.setError(null);
                                   strQnt = qty.getText().toString().trim();
                                   singlePurchaseData.setQuantity(strQnt);
                                   singlePurchaseData.setPrice( "" + (dubleAmount * Integer.parseInt(strQnt)) );
                                   errrorCount = 0  ;
                               }

                               singlePurchaseDataArrayList.add(singlePurchaseData);
                           }

                           if(errrorCount <= 0){
                               double total_price = 0.00 ;
                               for(SinglePurchaseData data : singlePurchaseDataArrayList){
                                   total_price += Double.parseDouble(data.getPrice());
                               }
                               confirmPayment(String.format("%.2f",total_price),singlePurchaseDataArrayList);
                           }
                           else{
                               Toast.makeText(getActivity(),"You may have not specified the quantity of some ticket type",Toast.LENGTH_LONG).show();
                           }
                       }
                       else
                       {
                           //TODO: Alert nothing added to chart.
                           Toast.makeText(getActivity(),"Sorry! you cannot make payment because anything added to the ticket cart.",Toast.LENGTH_LONG).show();
                       }
                }
                break;
        }
    }

    private void confirmPayment(String amount, ArrayList<SinglePurchaseData> singlePurchaseDataArrayList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirm Reservation");
        builder.setMessage("Total Price : "+ amount);
        builder.setNegativeButton("Cancel", confirmListener(null));
        builder.setPositiveButton("Confirm", confirmListener(singlePurchaseDataArrayList));

        builder.create().show();
    }
    private DialogInterface.OnClickListener confirmListener(final ArrayList<SinglePurchaseData> singlePurchaseDataArrayList) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if(isNetworkAndInternetAvailable()){
                            if(mAuth != null && mAuth.getCurrentUser() != null)
                            {

                                alertPaymentMethod((AppCompatActivity) context,singlePurchaseDataArrayList );

                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("NOT LOGGED IN");
                                builder.setMessage("Sorry, You need to login first in order to reserve a seat! " +
                                        "Goto the User Navigation Drawer and click on login if you have already registered " +
                                        "otherwise use the Register With Us link to do so.");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }

                        }
                        else
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("NO NETWORK AVAILABLE");
                            builder.setMessage("Sorry, no internet connection available. Please check your network connection and try again!");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                }
            }
        };
    }

    private void makePayment( final List<SinglePurchaseData> singlePurchaseData, String[] networkPossibleName) {
        final Handler h2 = new Handler();
        loading = ProgressDialog.show(context, "", "Please wait...\n\nTrying to Contact Network Operator. This may take about 15secs ...", false, false);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(LOGMESSAGE, "Payment 1 stopper started");
                ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (loading != null) {

                            loading.setCancelable(true);
                            loading.setMessage("Failure in network...");
                            Log.d(LOGMESSAGE, "Payment 1 setCancelable true");
                            Runnable r2 = new Runnable() {
                                @Override
                                public void run() {
                                    if(loading != null){
                                        loading.hide();
                                    }
                                }
                            };
                            h2.postDelayed(r2,50000);
                        }
                    }
                });

                Log.d(LOGMESSAGE, "Payment 1 stopper ended");
            }
        };


        final Handler handler = new Handler();
        handler.postDelayed(runnable,30000);

        final Handler h = new Handler();

        HashMap<String,String> contentValue = new HashMap<>();
        contentValue.put("CustomerName","Daniel");
        contentValue.put("CustomerEmail", "prncfoli@gmail.com");


        if(networkPossibleName[0].contains("MTN")){
            contentValue.put("Channel", "mtn-gh");
            contentValue.put("CustomerMsisdn", "233241994733"); //541243508
        }
        else
        if(networkPossibleName[0].contains("TIGO")){
            contentValue.put("Channel", "tigo-gh");
            contentValue.put("CustomerMsisdn", "0274320517");
        }
        else
        if(networkPossibleName[0].contains("AIRTEL")){
            contentValue.put("Channel", "airtel-gh");
            contentValue.put("CustomerMsisdn", "233572636005");
        }
        else
        if(networkPossibleName[0].contains("VODAFONE")){
            contentValue.put("Channel", "vodafone-gh");

        }

        contentValue.put("Amount","1");
        contentValue.put("Description", "Ticket");
        contentValue.put("PrimaryCallbackUrl", "https://www.crust-media.com/callback/index.php");

        final String url = "https://www.crust-media.com/callback/index2.php";
        ServerConnector.newInstance(url).setParameters(contentValue).attachListener(new ServerConnector.Callback() {
            @Override
            public void getResult(String result) {
                Log.d(LOGMESSAGE, "result index2 : " + result);
                if (result == null || result.isEmpty()) {
                    return;
                }
                Log.d(LOGMESSAGE, "result index2 : " + result);
                try {
                    JSONObject object = new JSONObject(result);

                    JSONObject data = object.getJSONObject("data");
                    transactionId = data.getString("TransactionId");

                    Log.d(LOGMESSAGE, "result index2 TransactionId : " + transactionId);

                    if (transactionId != null && !transactionId.isEmpty()) { //cvbfcvbv

                        final Timer  t = new Timer();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                Log.d(LOGMESSAGE, "Payment stopper started");
                                ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (loading != null) {
                                            loading.setCancelable(true);
                                            loading.hide();
                                            Log.d(LOGMESSAGE, "Payment setCancelable true");
                                        }
                                    }
                                });

                                Log.d(LOGMESSAGE, "Payment stopper ended");
                            }
                        };
                        final Handler handler = new Handler();
                        handler.postDelayed(runnable,15000);
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {

                                if(loading != null){

                                    h.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            loading.setMessage("Payment Processing started");
                                        }
                                    });
                                }

                                Log.d(LOGMESSAGE, "result index2 TransactionId run: " + transactionId);
                                HashMap<String, String> contentValue = new HashMap<>();
                                contentValue.put("TransactionId", transactionId);
                                final String url = "https://www.crust-media.com/callback/feedback.php";
                                ServerConnector.newInstance(url).setParameters(contentValue).attachListener(new ServerConnector.Callback() {
                                    @Override
                                    public void getResult(String mResult) {
                                        Log.d(LOGMESSAGE, "Result 2 feedback: " + mResult);
                                        if (mResult == null || mResult.isEmpty()) {
                                            return;
                                        }
                                        try
                                        {
                                            JSONObject object = new JSONObject(mResult);
                                            boolean isReady = object.getBoolean("ready");

                                            if (isReady)
                                            {
                                                if (loading != null) {
                                                    loading.hide();
                                                }
                                                if(t != null){
                                                   t.cancel();
                                                   t.purge();

                                                }
                                               if (!object.getString("StatusCode").contentEquals("0000")){
                                                   Log.d(LOGMESSAGE, "Result 2: transaction is successful ");
                                                   final List<DisplayTicket> displayTickets = processPayment(singlePurchaseData);
                                                   ((Evento) getActivity().getApplication()).getDatabaseHandler().addEvent(mEvent, displayTickets);
                                                   Toast.makeText(ReserveSeatFragment.this.context,""+object.getString("Description"),Toast.LENGTH_LONG).show();

//                                                   todo swap not back


                                               }
                                               else
                                               if (object.getString("StatusCode").contentEquals("0000")){
                                                   Log.d(LOGMESSAGE, "Result 2: transaction is failed -- Reason ::  "+object.getString("Description"));
                                                   //Toast.makeText(ReserveSeatFragment.this.context,""+object.getString("Description"),Toast.LENGTH_LONG).show();
// todo                                             to be removed



                                               }
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Log.d(LOGMESSAGE, "Result 2:Error:  " + e);
                                            t.cancel(); t.purge();
                                            loading.hide();
                                        }

                                    }
                                }).connectToServer();;
                            }
                        };
                        t.schedule(task,1000,2000);


                    }
    //


                    Log.d(LOGMESSAGE, "TransactionId : " + transactionId);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(LOGMESSAGE, "TransactionError : " + e);
                    loading.hide();
                }
            }
        }).connectToServer();


//        todo set payment listner


//        if (mPaymentListener != null ){
//            if (displayTickets != null && !displayTickets.isEmpty()){
//                mPaymentListener.payed("1234ddtryu99",true,displayTickets);
//            }
//            else {
//                mPaymentListener.payed("1234ddtryu99",false,displayTickets);
//            }
//
//        }


    }
    private List<DisplayTicket> processPayment(final List<SinglePurchaseData> singlePurchaseData){
        //TODO  add all purchased tickets to list of DisplayTicket object
        EncodeData.getInstance();
        List<DisplayTicket> displayTickets = new ArrayList<>();
        String transId = System.currentTimeMillis()+""+ (new Random().nextInt()) * 100000 ;
        for (final SinglePurchaseData purchaseData: singlePurchaseData ) {

            int qty = Integer.parseInt(purchaseData.getQuantity());

            for (int i = 0; i < qty ; i++) {
                DisplayTicket  displayTicket = new DisplayTicket();
                displayTicket.setName(purchaseData.getType().toUpperCase());
                displayTicket.setTransactionId(transId);

                String timestamp = "" +System.currentTimeMillis() ;
                String secret = ( FirebaseAuth.getInstance().getCurrentUser().getUid() +timestamp ).trim();
                String encoded =  EncodeData.encode( (FirebaseAuth.getInstance().getCurrentUser()+" "+displayTicket.getTransactionId()+ " " +secret ).trim());
                displayTicket.setQrCode(encoded);
                displayTickets.add(displayTicket);

                com.mtn.evento.bookings.Ticket ticket = new com.mtn.evento.bookings.Ticket();
                ticket.setType(displayTicket.getName());
                ticket.setSecrets(secret);
                ticket.setSecrets(timestamp);
                mDatabase.child("bookings").child(mEvent.getEvent_id()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(secret).setValue(ticket);
                Log.d(LOGMESSAGE," path:  "+mEvent.getEvent_id()+ "  "+FirebaseAuth.getInstance().getCurrentUser().getUid() +  "  "+secret);
                //TODO : update available seat
                // mDatabase.child(mEvent.getEvent_id())

                mDatabase.child(mEvent.getEvent_id()).child("ticket_type").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(LOGMESSAGE, "onDataChange: called ");
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Ticket ticket = snapshot.getValue(Ticket.class);
                            Log.d(LOGMESSAGE, "onDataChange: ticket is " + ticket);
                            if(ticket != null) {
                                if(purchaseData.getType().equals(ticket.getName())){
                                    int result = Integer.parseInt(ticket.getAvailable_seats()) - 1;
                                    Log.d(LOGMESSAGE, "onDataChange: result after subtraction : " + result);
//                                    todo subtract from total seat
                                    mDatabase.child(mEvent.getEvent_id()).child("ticket_type").child(snapshot.getKey()).setValue(ticket.getName(),result);

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }


        }


        return displayTickets;

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context ;
    }
    public void setCallback(PaymentListener paymentListener) {
        mPaymentListener = paymentListener;
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
    public  void alertPaymentMethod(final AppCompatActivity app,final ArrayList<SinglePurchaseData>singlePurchaseDataArrayList ){

        android.app.AlertDialog.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            builder = new android.app.AlertDialog.Builder(app, R.style.AppTheme_Alert);
        }
        else
        {
            builder = new android.app.AlertDialog.Builder(app);
        }

        // Set the dialog title
        builder.setTitle("Select Payment Method")

                // specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive call backs when items are selected
                // again, R.array.choices were set in the resources res/values/strings.xml
                .setSingleChoiceItems(R.array.choices, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int selectedPosition) {

                        switch (selectedPosition){
                            case 0 :

                                makePayment(singlePurchaseDataArrayList,new String[]{"MTN","MTN GH","MTN-GH","MTN GHANA"});
                                break;
                            case 1:
                                makePayment(singlePurchaseDataArrayList,new String[]{"TIGO","TIGO GH","TIGO-GH","TIGO GHANA"});
                                break;
                            case 2:
                                makePayment(singlePurchaseDataArrayList,new String[]{"AIRTEL","AIRTEL GH","AIRTEL-GH","AIRTEL GHANA"});
                                break;
                            case 3:
                                makePayment(singlePurchaseDataArrayList,new String[]{"VODAFONE","VODAFONE GH","VODAFONE-GH","VODAFONE GHANA"});
                                break;
                            case 4:
                                // alertScrollView(app,"This services not available for Glo " +
                                //       "network","SERVICE NOT AVAILABLE");

                                break;
                            default:
                                Toast.makeText( app.getApplicationContext(), "No Selected " +
                                        "Network", Toast.LENGTH_SHORT ).show();
                                break;
                        }
                        arg0.cancel();
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // removes the dialog from the screen
                    }
                })
                .show();
    }
    public interface PaymentListener {
        void payed(String transactionId, boolean status, List<DisplayTicket> displayTickets);
    }
    private static class ReservationHolder {
        String[] ticketCategories;
        private FloatingActionButton menu_add, menu_remove;
        private LinearLayout seats_group;

        public ReservationHolder(View holder, View.OnClickListener onClickListener, Event event) {

            ArrayList<Ticket> tickets = event.getTicket_type();
            ArrayList<String> strTickets = new ArrayList<>();
            for (Ticket ticket : tickets) {
                strTickets.add(ticket.getName());
            }
            String[] types = new String[strTickets.size()];
            for (int i = 0; i < strTickets.size(); i++) {
                types[i] = strTickets.get(i).toUpperCase();
            }

            ticketCategories = types;
            seats_group = (LinearLayout) holder.findViewById(R.id.seats_group);
            menu_add = (FloatingActionButton) holder.findViewById(R.id.menu_add);
            menu_add.setOnClickListener(onClickListener);
            menu_remove = (FloatingActionButton) holder.findViewById(R.id.menu_remove);
            menu_remove.setOnClickListener(onClickListener);
            MaterialSpinner spinner = (MaterialSpinner) holder.findViewById(R.id.spinner);
            spinner.setItems(ticketCategories);
            spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

                @Override
                public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                    Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                }
            });
        }

    }
}
