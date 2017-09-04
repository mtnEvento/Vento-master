package com.mtn.evento.broadcasts;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.gsm.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.mtn.evento.TelephonyInfo;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.mtn.evento.TelephonyInfo.getOutput;


/**
 * Created by Summy on 8/26/2017.
 */

public class SmsListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(SmsListener.class.getSimpleName(), "onReceive called :"+intent.getAction());
        if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            Log.d(SmsListener.class.getSimpleName(), "onReceive entered :"+intent.getAction());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Log.d(SmsListener.class.getSimpleName(), "onReceive entered KITKAT");
                for (android.telephony.SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    String messageBody = smsMessage.getMessageBody();
                    if (messageBody != null && !messageBody.isEmpty()) {
                        Log.d(SmsListener.class.getSimpleName(), "messageBody not null ");
                        Log.d(SmsListener.class.getSimpleName(), "messageBody:  "+messageBody);

                        if (messageBody.contains("Dial") && messageBody.contains("*501*5#") && messageBody.contains("confirm") && messageBody.contains("payment")) {
                            String encodedHash = Uri.encode("#");
                            String ussd = "*501*5" + encodedHash;
                            //Toast.makeText(context,"Dial"+ussd,Toast.LENGTH_LONG).show();

                            Intent ussdIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussd));
                            ussdIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                Log.d(SmsListener.class.getSimpleName(), "checkSelfPermission not granted ");
                                return;
                            }

                            TelephonyInfo info = TelephonyInfo.getInstance(context);
                            if(info.isDualSIM()){

//                                if (getOutput(context, "SimOperatorName", 1).toLowerCase() .contains(simOperatorPossibleNames[0].toLowerCase())
//                                        || getOutput
//                                        (context, "SimOperatorName", 1) .toLowerCase().contains(simOperatorPossibleNames[1].toLowerCase())
//                                        || getOutput
//                                        (context, "SimOperatorName", 1).toLowerCase() .contains(simOperatorPossibleNames[2].toLowerCase()))
//                                {
//                                    //SIM1 : 1
//
//
//                                    Toast.makeText(context,"NUMBER PORTING REQUEST SENT!",Toast.LENGTH_LONG).show();
//                                }
//                                else if(getOutput(context, "SimOperatorName", 2).toLowerCase() .contains
//                                        (simOperatorPossibleNames[0].toLowerCase())
//                                        || getOutput
//                                        (context, "SimOperatorName", 2).toLowerCase() .contains(simOperatorPossibleNames[1].toLowerCase())
//                                        || getOutput
//                                        (context, "SimOperatorName", 2).toLowerCase() .contains(simOperatorPossibleNames[2].toLowerCase()))
//                                {
//                                    // SIM2 : 2
//
//
//                                    Toast.makeText(context,"NUMBER PORTING REQUEST SENT!",Toast.LENGTH_LONG).show();
//                                }
                            }
                            else
                            {
                                context.startActivity(ussdIntent);
                            }

                            break;
                        }
                    }
                }

            } else {
                /*---get the SMS message passed in--- */
                Bundle bundle = intent.getExtras();
                SmsMessage[] msgs = null;
                String msg_from;
                if (bundle != null) {
                    //---retrieve the SMS message received---
                    try {
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        msgs = new SmsMessage[pdus.length];
                        for (int i = 0; i < msgs.length; i++) {
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                            msg_from = msgs[i].getOriginatingAddress();
                            String msgBody = msgs[i].getMessageBody();
                            if (msgBody != null && !msgBody.isEmpty()) {
                                if (msgBody.contains("Dial") && msgBody.contains("*501*5#") && msgBody.contains("confirm") && msgBody.contains("payment")) {
                                    String encodedHash = Uri.encode("#");
                                    String ussd = "*501*5" + encodedHash;
                                    Toast.makeText(context,"Dial"+ussd,Toast.LENGTH_LONG).show();

                                    Intent ussdIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussd));
                                    ussdIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    context.startActivity(ussdIntent);
                                }
                            }
                        }
                    }catch(Exception e){
                        Log.d("Exception caught",e.getMessage());
                    }
                }
            }

        }

    }
}
/*
 if (getOutput(context, "SimOperatorName", 1).toLowerCase() .contains(simOperatorPossibleNames[0].toLowerCase())
                        || getOutput
                        (context, "SimOperatorName", 1) .toLowerCase().contains(simOperatorPossibleNames[1].toLowerCase())
                        || getOutput
                        (context, "SimOperatorName", 1).toLowerCase() .contains(simOperatorPossibleNames[2].toLowerCase()))
                {
                    //SIM1 : 1
                    portabilityManager =   SmsManager.getSmsManagerForSubscriptionId(1);
                    portabilityManager.sendTextMessage(SERVICES_NumberPortability,null,SERVICES_PORT_TEXT,null,null);
                    Toast.makeText(context,"NUMBER PORTING REQUEST SENT!",Toast.LENGTH_LONG).show();
                }
                else if(getOutput(context, "SimOperatorName", 2).toLowerCase() .contains
                        (simOperatorPossibleNames[0].toLowerCase())
                        || getOutput
                        (context, "SimOperatorName", 2).toLowerCase() .contains(simOperatorPossibleNames[1].toLowerCase())
                        || getOutput
                        (context, "SimOperatorName", 2).toLowerCase() .contains(simOperatorPossibleNames[2].toLowerCase()))
                {
                    // SIM2 : 2
                    portabilityManager =   SmsManager.getSmsManagerForSubscriptionId(2);
                    portabilityManager.sendTextMessage(SERVICES_NumberPortability,null,SERVICES_PORT_TEXT,null,null);
                    Toast.makeText(context,"NUMBER PORTING REQUEST SENT!",Toast.LENGTH_LONG).show();
                }

*/