package com.mtn.evento.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mtn.evento.Evento;
import com.mtn.evento.R;
import com.mtn.evento.activities.HomeScreenActivity;
import com.mtn.evento.data.Constants;
import com.mtn.evento.data.DisplayTicket;
import com.mtn.evento.data.EncodeData;
import com.mtn.evento.data.Event;
import com.mtn.evento.data.ServerConnector;
import com.mtn.evento.data.SinglePurchaseData;
import com.mtn.evento.data.Ticket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.mtn.evento.data.Constants.APP_USERNAME;
import static com.mtn.evento.data.Constants.APP_USER_EMAIL;
import static com.mtn.evento.data.Constants.LOGMESSAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReserveSeatFragment extends Fragment implements View.OnClickListener  {
    static String transactionId;
    Context context;
    static MenuItem cartView;
    PaymentListener mPaymentListener;
    private ReservationHolder holder;
    private Event mEvent;
    private Button makePayment;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private de.hdodenhof.circleimageview.CircleImageView event_image;
    ProgressDialog loading;
    private static FloatingActionButton menu_add, menu_remove;
    private static LinearLayout seats_group;
    IPay iPay ;
    Timer  t;
    private MaterialSpinner spinner;
    AlertDialog.Builder builder;
    boolean isAlertVisible;
    int count=0 ;
    ArrayList<String> transacIds = new ArrayList<>();
    static CountDownTimer countDownTimer;


    public ReserveSeatFragment() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }


    public interface IPay{
        void onPaymentConfirm(String transactionId,String result, List<DisplayTicket> displayTickets);
        void onPaymentDenied(String transactionId,String result);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
         builder = new AlertDialog.Builder(getContext());
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
        ArrayList<Ticket> tickets = mEvent.getTicket_type();
        ArrayList<String> strTickets = new ArrayList<>();
        if(tickets != null && tickets.size()> 0){
            for (Ticket ticket : tickets) {
                strTickets.add(ticket.getName());
            }
            String[] types = new String[strTickets.size()];
            for (int i = 0; i < strTickets.size(); i++) {
                types[i] = strTickets.get(i).toUpperCase();
            }

            holder = new ReservationHolder(view,this,mEvent,mDatabase,mAuth,types );
            spinner = (com.jaredrummler.materialspinner.MaterialSpinner) view.findViewById(R.id.spinner);
            spinner.setOnItemSelectedListener(new SpinnerListener());
            Glide.with(context)
                    .load(mEvent.getBanner())
                    .asBitmap()
                    .into(event_image) ;

        }


        return view;
    }
    class SpinnerListener implements MaterialSpinner.OnItemSelectedListener {

            @Override
            public void onItemSelected(final MaterialSpinner view, int position, long id,final Object item) {
                Log.d(LOGMESSAGE, "onItemSelected on Spinner");
                mDatabase.child("events").child(mEvent.getEvent_id()).child("ticket_type").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int result =  0 ;

                        if(dataSnapshot != null && dataSnapshot.getValue() != null )
                        {
                            for(DataSnapshot snapshot : dataSnapshot.getChildren())
                            {


                                Ticket ticket = snapshot.getValue(Ticket.class);
                                Log.d(LOGMESSAGE, "selected :" + ((String)item).toUpperCase() +"  from ticket : "+ticket.getName().toUpperCase());
                                Log.d(LOGMESSAGE, "onDataChange in Spinner : ticket is " + ticket);
                                if(ticket != null)
                                {

                                    if(((String)item).toUpperCase().equals(ticket.getName().toUpperCase()))
                                    {
                                        result = Integer.parseInt(ticket.getAvailable_seats());
                                        if(result > 0 )
                                        {
                                            Snackbar.make(view, "Ticket(s) left for " + item +" category is "+result, Snackbar.LENGTH_LONG).show();
                                            Log.d(LOGMESSAGE, "Available_seats  is "+ result);
                                            break;
                                        }
                                        else
                                        {
                                            int childs = seats_group.getChildCount();
                                            Snackbar.make(view, "Sorry! no ticket left for '" + item + "' category", Snackbar.LENGTH_LONG).show();
                                            if( childs > 0)
                                            {
                                                seats_group.removeViewAt(childs-1);
                                                if( cartView != null &&  cartView.getTitle()!= null){

                                                    if(!cartView.getTitle().toString().isEmpty()){
                                                        String numberOnly = cartView.getTitle().toString().replaceAll("[^0-9]", "");
                                                        cartView.setTitle( "Cart : "+ ( (Integer.parseInt(numberOnly))- 1));
                                                    }
                                                }
                                            }

                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            Log.d(LOGMESSAGE, "No ticket types available");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOGMESSAGE, "databaseError : \n"+databaseError.getMessage());
                        Log.d(LOGMESSAGE, "databaseError : \n"+databaseError.getDetails());
                        Log.d(LOGMESSAGE, "databaseError : \n"+databaseError.toException());
                    }
                });
            }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu_add :
               int childs = holder.seats_group.getChildCount();
               View mView =   LayoutInflater.from( this.context).inflate(R.layout.add_seats,null);

                MaterialSpinner spinner = (MaterialSpinner) mView.findViewById(R.id.spinner);
                spinner.setItems(holder.ticketCategories);
                spinner.setOnItemSelectedListener(new SpinnerListener());
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

                if(holder != null  && holder.seats_group != null && holder.seats_group.getChildCount()> 0 ){
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
                                String typeName = holder.ticketCategories[spiner.getSelectedIndex()];
                                ArrayList<Ticket>   tickets  =  mEvent.getTicket_type();
                                ArrayList<String>   strTickets  = new ArrayList<>();
                                if(tickets != null )
                                {
                                    for (Ticket  ticket : tickets  )
                                    {
                                        if (typeName.toUpperCase().contentEquals(ticket.getName().toUpperCase())) {
                                            amount =   ticket.getAmount().trim();
                                            dubleAmount = Double.parseDouble(amount);
                                            singlePurchaseData.setType(typeName);
                                            break;
                                        }
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

                                if(total_price > 0 ){
                                    confirmPayment(String.format("%.2f",total_price),singlePurchaseDataArrayList);
                                }
                                else
                                {
                                   showErrorOrWarningAlert("SORRY","You can not enter '0'  for quantity");
                                }

                            }
                            else
                            {
                                Toast.makeText(getActivity(),"You may have not specified the quantity of some ticket type",Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                            //TODO: Alert nothing added to chart.
                            Toast.makeText(getActivity(),"Sorry! you cannot make payment because anything added to the ticket cart.",Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else
                {
                    showErrorOrWarningAlert("UNKNOWN ERROR","An error occurred while trying to access UI component content. The event published might have contain some errors that are affecting it. Please contact us to report such issues. ");
                }

                break;
        }
    }

    private void confirmPayment(String amount, ArrayList<SinglePurchaseData> singlePurchaseDataArrayList) {
        builder.setTitle("Confirm Reservation");
        builder.setMessage("Total Price : "+ amount);
        builder.setNegativeButton("Cancel", confirmListener(null, amount));
        builder.setPositiveButton("Confirm", confirmListener(singlePurchaseDataArrayList,amount));
        builder.create().show();
    }
    private DialogInterface.OnClickListener confirmListener(final ArrayList<SinglePurchaseData> singlePurchaseDataArrayList,final String amount) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if(isNetworkAndInternetAvailable()){
                            if(mAuth != null && mAuth.getCurrentUser() != null)
                            {

                                alertPaymentMethod((AppCompatActivity) context,singlePurchaseDataArrayList,amount );

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

    private void makePayment(final List<SinglePurchaseData> singlePurchaseData, String[] networkPossibleName,final String amount, final String number) {

        runOnUI(new Runnable() {
            @Override
            public void run()
            {
                if(loading != null){
                    loading.setMessage(  "Please wait...\n\nTrying to Contact Network Operator. This may take about 15secs ...");
                    loading.setCancelable(false);
                    loading.setIndeterminate(false);
                }
                else
                {
                    loading = new ProgressDialog(context);
                    loading.setMessage(  "Please wait...\n\nTrying to Contact Network Operator. This may take about 15secs ...");
                    loading.setCancelable(false);
                    loading.setIndeterminate(false);
                }
            }
        });

        String str_username = null , str_email = null ;
        if ( context != null &&((AppCompatActivity) context).getApplication() !=null &&((Evento)((AppCompatActivity) context).getApplication()).getSettings().contains(APP_USER_EMAIL)) {
            str_email = ((Evento)((AppCompatActivity) context).getApplication()).getSettings().getString(APP_USER_EMAIL, "");

        }
        if ( context != null &&((AppCompatActivity) context).getApplication() !=null &&((Evento)((AppCompatActivity) context).getApplication()).getSettings().contains(APP_USERNAME)) {
             str_username = ((Evento)((AppCompatActivity) context).getApplication()).getSettings().getString(APP_USERNAME, "");

        }
        Log.d("contentValue","number: "+ number + " amount: " + amount + " username: " +  str_username + "  email: " +  str_email);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(LOGMESSAGE, "Payment 1 stopper started");
                runOnUI(new Runnable() {
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
                            runOnUI(r2,60000);
                        }
                    }
                });

                Log.d(LOGMESSAGE, "Payment 1 stopper ended");
            }
        };
        runOnUI(runnable,30000);

        HashMap<String,String> contentValue = new HashMap<>();
        contentValue.put("CustomerName",""+str_username);
        contentValue.put("CustomerEmail", str_email); //"prncfoli@gmail.com";

        if(networkPossibleName[0].contains("MTN")){
            contentValue.put("Channel", "mtn-gh");
            contentValue.put("CustomerMsisdn", number); //541243508//233241994733
        }
        else
        if(networkPossibleName[0].contains("TIGO")){
            contentValue.put("Channel", "tigo-gh");
            contentValue.put("CustomerMsisdn", number); //0274320517
        }
        else
        if(networkPossibleName[0].contains("AIRTEL")){
            contentValue.put("Channel", "airtel-gh");
            contentValue.put("CustomerMsisdn", number);//"233572636005"
        }
        else
        if(networkPossibleName[0].contains("VODAFONE")){
            contentValue.put("Channel", "vodafone-gh");
            contentValue.put("CustomerMsisdn", number);//"233572636005"

        }
        contentValue.put("Amount",amount);
        contentValue.put("Description", "Payment for Ticket purchased");
        contentValue.put("PrimaryCallbackUrl", "https://www.crust-media.com/callback/index.php");

        try
        {

            final String url = "https://www.crust-media.com/callback/index2.php";
            ServerConnector.newInstance(url).setParameters(contentValue).attachListener(new ServerConnector.Callback() {
                @Override
                public void getResult(String result) {
                    Log.d(LOGMESSAGE, "result index2 : " + result);
                    if (result == null || result.isEmpty()) {
                        return;
                    }
                    Log.d(LOGMESSAGE, "result index2 : " + result);
                    try
                    {
                        if( result.contains("\"data\":") && !result.contains("\"data\":null") )
                        {
                            JSONObject object = new JSONObject(result);
                            JSONObject object2 = object.getJSONObject("data");
                            transactionId = object2.getString("TransactionId");

                            Log.d(LOGMESSAGE, "result index2 TransactionId : " + transactionId);

                            if (transactionId != null && !transactionId.isEmpty())
                            {
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                    Log.d(LOGMESSAGE, "Payment stopper started");
                                    if (loading != null) {
                                        loading.setCancelable(true);
                                        loading.hide();
                                        Log.d(LOGMESSAGE, "Payment setCancelable true");
                                    }
                                    Log.d(LOGMESSAGE, "Payment stopper ended");
                                    }
                                };
                                runOnUI(runnable,20000);

                                if (countDownTimer == null ){
                                    usingCountDownTimer(singlePurchaseData);
                                }
                                else
                                {
                                   runOnUI(new Runnable() {
                                        @Override
                                        public void run() {
                                            showErrorOrWarningAlert("TRANSACTION PROCESSING FAILURE","Could not process the transaction! please try again");
                                        }
                                    });
                                }
                            }
                            Log.d(LOGMESSAGE, "TransactionId : " + transactionId);
                        }
                        else
                        {
                            Log.d(LOGMESSAGE, "TransactionError : Could not process the transaction because of the incorrect information provided. Please the correct information and try again");
                            hideLoading();
                            runOnUI(new Runnable() {
                                 @Override
                                  public void run() {
                                     showErrorOrWarningAlert("TRANSACTION PROCESSING FAILED","Server could not process this transaction. Please try again later.\n");
                                     //DELAY IN PROCESSING","Could not process the transaction immediately because there may be incorrect information provided. Please wait for some about 15secs and try again if there is still delay in processing.!
                                 }
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(LOGMESSAGE, "TransactionError : " + e);
                        hideLoading();
                    }
                }
            }).attachServerErrorListener(new ServerConnector.ErrorCallback() {
                @Override
                public void getError(final String error, IOException e) {
                    if(countDownTimer != null){
                        countDownTimer.cancel();
                    }
                    runOnUI(new Runnable() {
                        @Override
                        public void run () {
                            if (loading != null)
                            {
                                loading.hide();
                            }
                            showErrorOrWarningAlert("SERVER CONNECT ERROR",error);
                        }
                    });
                }
            }).connectToServer();
            runOnUI(new Runnable() {
                @Override
                public void run() {
                    if( loading != null ){
                        loading.show();
                    }
                    else
                    {
                        Toast.makeText(getContext(),"Could not initialize progress dialog!",Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
        catch(final Exception e)
        {
         runOnUI(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,"Error ::: "+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
        finally {}
    }
    private List<DisplayTicket> processPayment(final List<SinglePurchaseData> singlePurchaseData, final String transId){
        //TODO  add all purchased tickets to list of DisplayTicket object
        EncodeData.getInstance();
        List<DisplayTicket> displayTickets = new ArrayList<>();
       // String transId = System.currentTimeMillis()+""+ (new Random().nextInt()) * 100000 ;
        for (final SinglePurchaseData purchaseData: singlePurchaseData ) {

            int qty = Integer.parseInt(purchaseData.getQuantity());

            for (int i = 0; i < qty ; i++) {
                DisplayTicket  displayTicket = new DisplayTicket();
                displayTicket.setName(purchaseData.getType().toUpperCase());
                displayTicket.setTransactionId(transId);

                String timestamp = "" +System.currentTimeMillis() ;
                String secret = ( ""+ FirebaseAuth.getInstance().getCurrentUser().getUid() + timestamp ).trim();
                // Firebase UserId space transactionId space secret(FirebaseUserId + Timestamp)
                String encoded = ( EncodeData.encode( (""+FirebaseAuth.getInstance().getCurrentUser().getUid()+" "+displayTicket.getTransactionId()+ " " +secret ).trim())).trim();
                Log.d(LOGMESSAGE," Result Data Encoded:  "+FirebaseAuth.getInstance().getCurrentUser().getUid());
                displayTicket.setQrCode(encoded);
                displayTickets.add(displayTicket);

                com.mtn.evento.bookings.Ticket ticket = new com.mtn.evento.bookings.Ticket();
                ticket.setType(displayTicket.getName());
                ticket.setSecrets(secret);
                ticket.setTimestamp(timestamp);
                mDatabase.child("bookings").child(mEvent.getEvent_id()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(secret).setValue(ticket);
                Log.d(LOGMESSAGE," path:  "+mEvent.getEvent_id()+ "  "+FirebaseAuth.getInstance().getCurrentUser().getUid() +  "  "+secret);
                //TODO : update available seat
                // mDatabase.child(mEvent.getEvent_id())
            }

            mDatabase.child("events").child(mEvent.getEvent_id()).child("ticket_type").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(LOGMESSAGE, "onDataChange: called ");
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Ticket ticket = snapshot.getValue(Ticket.class);
                        Log.d(LOGMESSAGE, "onDataChange: ticket is " + ticket);
                        if(ticket != null) {
                            if(purchaseData.getType().toUpperCase().equals(ticket.getName().toUpperCase())){

                                int avail_seat = Integer.parseInt(ticket.getAvailable_seats());
                                int purchaseQty = Integer.parseInt(purchaseData.getQuantity());
                                if(avail_seat > 0 &&  purchaseQty <= avail_seat)
                                {
                                    int result = Integer.parseInt(ticket.getAvailable_seats()) - Integer.parseInt(purchaseData.getQuantity());
                                    Log.d(LOGMESSAGE, "onDataChange: result after subtraction : " + result);
                                    //TODO: subtract from total seat
                                    mDatabase.child("events").child(mEvent.getEvent_id()).child("ticket_type").child(snapshot.getKey()).child("available_seats").setValue(""+result);

                                }
                                else
                                {
                                   //TODO: No tickets for reservation.
                                    Log.d(LOGMESSAGE, "onDataChange: No seats availble");
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }


        return displayTickets;

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ;
        this.context = context ;
        loading = new ProgressDialog((AppCompatActivity)context);
                iPay = new IPay() {
            @Override
            public void onPaymentConfirm(String transactionId,String resultMSG, final List<DisplayTicket> displayTickets) {
                 Toast.makeText(ReserveSeatFragment.this.context,""+resultMSG,Toast.LENGTH_LONG).show();
                 mPaymentListener.payed(transactionId,true,displayTickets);

            }

            @Override
            public void onPaymentDenied(String transactionId,String resultMSG) {
                Toast.makeText(ReserveSeatFragment.this.context,transactionId+ "  did not go through!. Reason : "+ resultMSG,Toast.LENGTH_LONG).show();
                //mPaymentListener.payed(transactionId,false,null);
            }
        };
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
    public  void alertPaymentMethod(final AppCompatActivity app, final ArrayList<SinglePurchaseData> singlePurchaseDataArrayList,final String amount){

        android.app.AlertDialog.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            builder = new android.app.AlertDialog.Builder(app, R.style.AppTheme_Alert);
        }
        else
        {
            builder = new android.app.AlertDialog.Builder(app);
        }
        builder.setTitle("Select Payment Method")
               .setSingleChoiceItems(R.array.choices, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int selectedPosition) {

                        switch (selectedPosition){
                            case 0 :
                                createUserInputResquest(singlePurchaseDataArrayList,new String[]{"MTN","MTN GH","MTN-GH","MTN GHANA"},amount,false);
                                break;
                            case 1:
                                createUserInputResquest(singlePurchaseDataArrayList,new String[]{"TIGO","TIGO GH","TIGO-GH","TIGO GHANA"},amount,false);
                                break;
                            case 2:
                                createUserInputResquest(singlePurchaseDataArrayList,new String[]{"AIRTEL","AIRTEL GH","AIRTEL-GH","AIRTEL GHANA"},amount,false);
                                break;
                            case 3:
                                createUserInputResquest(singlePurchaseDataArrayList,new String[]{"VODAFONE","VODAFONE GH","VODAFONE-GH","VODAFONE GHANA"},amount,false);
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
                        dialog.cancel();
                   }
               })
               .show();
    }

    private void createUserInputResquest(final ArrayList<SinglePurchaseData> singlePurchaseDataArrayList, final String[] networkNames, final String amount,boolean recreate){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
        alertDialogBuilderUserInput.setView(mView);


        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
        if(recreate){
            userInputDialogEditText.setError("invalid number");
        }
        userInputDialogEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // ToDo get user input here

                        if(userInputDialogEditText != null && userInputDialogEditText.getText() != null && !userInputDialogEditText.getText().toString().isEmpty() && userInputDialogEditText.getText().toString().length() >= 10){

                            makePayment(singlePurchaseDataArrayList,networkNames,amount,userInputDialogEditText.getText().toString());
                        }
                        else
                        {
                            userInputDialogEditText.setError("invalid number");
                            createUserInputResquest(singlePurchaseDataArrayList,networkNames,amount,true);
                            Toast.makeText(context,"invalid number",Toast.LENGTH_LONG).show();
                        }

                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        userInputDialogEditText.setError("invalid number");
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }
    public interface PaymentListener {
        void payed(String transactionId, boolean status, List<DisplayTicket> displayTickets);
    }
    private static class ReservationHolder {
        String[] ticketCategories;
        private FloatingActionButton menu_add, menu_remove;
        private LinearLayout seats_group;

        public ReservationHolder(View holder, View.OnClickListener onClickListener, final Event event, final DatabaseReference mDatabase, final FirebaseAuth mAuth, String[] types) {

            ticketCategories = types;
            seats_group = (LinearLayout) holder.findViewById(R.id.seats_group);
            ReserveSeatFragment.seats_group = this.seats_group ;
            menu_add = (FloatingActionButton) holder.findViewById(R.id.menu_add);
            ReserveSeatFragment.menu_add = this.menu_add ;
            menu_add.setOnClickListener(onClickListener);
            menu_remove = (FloatingActionButton) holder.findViewById(R.id.menu_remove);

            ReserveSeatFragment.menu_remove = this.menu_remove ;
            menu_remove.setOnClickListener(onClickListener);
            MaterialSpinner spinner = (MaterialSpinner) holder.findViewById(R.id.spinner);
            spinner.setItems(ticketCategories);
            spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

                @Override
                public void onItemSelected(final MaterialSpinner view, int position, long id,final String item) {
                    Log.d(LOGMESSAGE, "onItemSelected on Spinner");
                    mDatabase.child("events").child(event.getEvent_id()).child("ticket_type").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int result =  0 ;
                            if(dataSnapshot != null && dataSnapshot.getValue() != null )
                            {
                                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                                {
                                    Ticket ticket = snapshot.getValue(Ticket.class);
                                    Log.d(LOGMESSAGE, "selected :" + ((String)item).toUpperCase() +"  from ticket : "+ticket.getName().toUpperCase());
                                    Log.d(LOGMESSAGE, "onDataChange in Spinner : ticket is " + ticket);
                                    if(ticket != null)
                                    {
                                        if(((String)item).toUpperCase().equals(ticket.getName().toUpperCase()))
                                        {
                                            result = Integer.parseInt(ticket.getAvailable_seats());
                                            if(result > 0 )
                                            {
                                                Snackbar.make(view, "Ticket(s) left for " + item +" category is "+result, Snackbar.LENGTH_LONG).show();
                                                Log.d(LOGMESSAGE, "Available_seats  is "+ result);
                                                break;
                                            }
                                            else
                                            {
                                                int childs = seats_group.getChildCount();
                                                Snackbar.make(view, "Sorry! no ticket left for '" + item + "' category", Snackbar.LENGTH_LONG).show();
                                                if( childs > 0)
                                                {
                                                    seats_group.removeViewAt(childs-1);
                                                    if( cartView != null &&  cartView.getTitle()!= null){

                                                        if(!cartView.getTitle().toString().isEmpty()){
                                                            String numberOnly = cartView.getTitle().toString().replaceAll("[^0-9]", "");
                                                            cartView.setTitle( "Cart : "+ ( (Integer.parseInt(numberOnly))- 1));
                                                        }
                                                    }
                                                }

                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            else
                            {
                                Log.d(LOGMESSAGE, "No ticket types available");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(LOGMESSAGE, "databaseError : \n"+databaseError.getMessage());
                            Log.d(LOGMESSAGE, "databaseError : \n"+databaseError.getDetails());
                            Log.d(LOGMESSAGE, "databaseError : \n"+databaseError.toException());
                        }
                    });
                }
            });
        }
    }

    public void runOnUI( final Runnable run){
        if(getContext() != null && ((AppCompatActivity)getContext()) != null ) {
            ((AppCompatActivity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Handler handler = new Handler();
                    handler.post(run);
                }
            });

        }
    }

    public void runOnUI(final Runnable run, final int timeDelay){
        if(getContext() != null && ((AppCompatActivity)getContext()) != null ){
            ((AppCompatActivity)getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Handler handler = new Handler() ;
                    handler.postDelayed(run,timeDelay) ;
                }
            });
        }

    }

    public void showErrorOrWarningAlert(String title, String msg){

        if(isAlertVisible){

            builder.setTitle(title);
            builder.setMessage(msg);
            isAlertVisible = true ;
        }
        else
        {
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    isAlertVisible = false ;
                }
            });
            builder.show();
            isAlertVisible = true ;
        }


    }

    public void hideLoading(){
        runOnUI(new Runnable() {
            @Override
            public void run() {
                if(loading != null)
                {
                    loading.hide();
                }
            }
        });
    }



    public void usingCountDownTimer(final  List<SinglePurchaseData> singlePurchaseData) {
        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 5000) {

            // This is called after every  sec interval.
            public void onTick(long millisUntilFinished) {
                jobTask(singlePurchaseData);
            }

            public void onFinish() {
                start();
            }
        }.start();
    }

    public void jobTask(final  List<SinglePurchaseData> singlePurchaseData){


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
                        hideLoading();
                        if(countDownTimer != null)
                        {
                            countDownTimer.cancel();
                        }
                        if (!object.getString("StatusCode").contentEquals("0000"))
                        {
                            Log.d(LOGMESSAGE, "Result 2: transaction is successful ");

                            if(!transacIds.contains(transactionId)){
                                transacIds.add(transactionId);
                                final List<DisplayTicket> displayTickets = processPayment(singlePurchaseData,transactionId);
                                ((Evento)( (AppCompatActivity) context).getApplication()).getDatabaseHandler().addEvent(mEvent, displayTickets);
                                iPay.onPaymentConfirm(transactionId,""+object.getString("Description"),displayTickets);
                            }
                            else
                            {
                                Log.d(LOGMESSAGE, "Result 2: transaction already processed.\n -- Reason ::  "+object.getString("Description"));
                            }
                        }
                        else
                        if (object.getString("StatusCode").contentEquals("0000")){
                            Log.d(LOGMESSAGE, "Result 2: transaction failed -- Reason ::  "+object.getString("Description"));
                            iPay.onPaymentDenied(transactionId,object.getString("Description"));
                        }
                        transactionId = "";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(LOGMESSAGE, "Result 2:Error:  " + e);
                    if(countDownTimer != null){
                        countDownTimer.cancel();
                    }
                    hideLoading();
                }

            }
        }).attachServerErrorListener(new ServerConnector.ErrorCallback() {
            @Override
            public void getError(final String error, IOException e) {

                if(countDownTimer != null){
                    countDownTimer.cancel();
                }
                runOnUI(new Runnable() {
                    @Override
                    public void run() {
                        if (loading != null)
                        {
                            loading.hide();
                        }

                        showErrorOrWarningAlert("SERVER CONNECT ERROR",error);
                    }
                });
            }
        }).connectToServer();;
    }
}
