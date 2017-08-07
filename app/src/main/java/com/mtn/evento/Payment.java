package com.mtn.evento;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mtn.evento.data.ServerConnector;

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
        contentValue.put("CustomerName","Daniel");
        contentValue.put("CustomerMsisdn","233241361156");
        contentValue.put("CustomerEmail","user@gmail.com");
        contentValue.put("Channel","mtn-gh");
        contentValue.put("Amount","1");
        contentValue.put("Description","T Shirt");

        Log.d(LOGMESSAGE, "button clicked: ");

        ServerConnector.newInstance("http://10.13.56.107/payment.php").setParameters(contentValue).attachListener(new ServerConnector.Callback() {
            @Override
            public void getResult(String result) {
                if(result == null || result.isEmpty()){return; }
                Log.d(LOGMESSAGE, "getResult: " + result);
                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject data =  object.getJSONObject("data");
                    String transactionId = data.getString("TransactionId");

                    Log.d(LOGMESSAGE, "TransactionId : " + transactionId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).connectToServer();
    }

}
