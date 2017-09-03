package com.mtn.evento;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mtn.evento.data.ServerConnector;
import com.mtn.evento.database.DatabaseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.mtn.evento.data.Constants.LOGMESSAGE;

public class Payment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
    }

    public void makePayment(View v){

        HashMap<String,String> contentValue = new HashMap<>();
        contentValue.put("CustomerName", "Prince Foli");
        contentValue.put("CustomerMsisdn", "0274320517");
        contentValue.put("CustomerEmail", "prncfoli@gmail.com");
        contentValue.put("Channel", "tigo-gh");//mtn-gh
        contentValue.put("Amount","1");
        contentValue.put("Description", "Ticket for event.");

        Log.d(LOGMESSAGE, "button clicked: ");

        ServerConnector.newInstance("https://www.crust-media.com/callback/index2.php").setParameters(contentValue).attachListener(new ServerConnector.Callback() {
            @Override
            public void getResult(String result) {
                if(result == null || result.isEmpty()){return; }
                Log.d(LOGMESSAGE, "getResult: " + result);
                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject data =  object.getJSONObject("data");
                    String transactionId = data.getString("TransactionId");

                    DatabaseHandler db = new DatabaseHandler(Payment.this);


                    Log.d(LOGMESSAGE, "TransactionId : " + transactionId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).connectToServer();
    }

}
