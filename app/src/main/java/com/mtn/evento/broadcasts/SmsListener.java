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
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.gsm.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.mtn.evento.utils.TelephonyInfo;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.mtn.evento.utils.TelephonyInfo.getOutput;


/**
 * Created by Summy on 8/26/2017.
 */

public class SmsListener extends BroadcastReceiver {

    private final static String simSlotName[] = {
            "extra_asus_dial_use_dualsim",
            "com.android.phone.extra.slot",
            "slot",
            "simslot",
            "sim_slot",
            "subscription",
            "Subscription",
            "phone",
            "com.android.phone.DialingMode",
            "simSlot",
            "slot_id",
            "simId",
            "simnum",
            "phone_type",
            "slotId",
            "slotIdx",
            "com.mediatek.common.telephony.IOnlyOwnerSimSupport"
    };

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
                                TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(context);
                                TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                                List<PhoneAccountHandle> phoneAccountHandleList = null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                        return;
                                    }
                                    phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
                                }
                                ussdIntent.putExtra("com.android.phone.force.slot", true);
                                ussdIntent.putExtra("Cdma_Supp", true);

                                if (getOutput(context, "SimOperatorName", 1).toLowerCase() .contains("mtn"))

                                {
                                    //SIM1 : 1
                                    for (String s : simSlotName) {
                                        ussdIntent.putExtra(s, 0);
                                    }
                                    if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0){
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        {
                                            ussdIntent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(0));
                                        }
                                    }
                                    context.startActivity(ussdIntent);
                                }
                                else if(getOutput(context, "SimOperatorName", 2).toLowerCase() .contains("mtn".toLowerCase()))
                                {
                                    // SIM2 : 2
                                    for (String s : simSlotName) {
                                        ussdIntent.putExtra(s, 1);
                                    }
                                    if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0){
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        {
                                            ussdIntent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(1));
                                        }
                                    }
                                    context.startActivity(ussdIntent);
                                }


                                if (getOutput(context, "SimOperatorName", 1).toLowerCase() .contains("tigo"))

                                {
                                    //SIM1 : 1
                                    for (String s : simSlotName) {
                                        ussdIntent.putExtra(s, 0);
                                    }
                                    if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0){
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        {
                                            ussdIntent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(0));
                                        }
                                    }
                                    context.startActivity(ussdIntent);

                                }
                                else if(getOutput(context, "SimOperatorName", 2).toLowerCase() .contains("tigo".toLowerCase()))
                                {
                                    // SIM2 : 2
                                    for (String s : simSlotName) {
                                        ussdIntent.putExtra(s, 1);
                                    }
                                    if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0){
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        {
                                            ussdIntent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(1));
                                        }
                                    }
                                    context.startActivity(ussdIntent);
                                }

                                if (getOutput(context, "SimOperatorName", 1).toLowerCase() .contains("airtel"))

                                {
                                    //SIM1 : 1
                                    for (String s : simSlotName) {
                                        ussdIntent.putExtra(s, 0);
                                    }
                                    if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0){
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        {
                                            ussdIntent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(0));
                                        }
                                    }
                                    context.startActivity(ussdIntent);


                                }
                                else if(getOutput(context, "SimOperatorName", 2).toLowerCase() .contains("airtel".toLowerCase()))
                                {
                                    // SIM2 : 2
                                    for (String s : simSlotName) {
                                        ussdIntent.putExtra(s, 1);
                                    }
                                    if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0){
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        {
                                            ussdIntent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(1));
                                        }
                                    }

                                }

                                if (getOutput(context, "SimOperatorName", 1).toLowerCase() .contains("vodafone"))

                                {
                                    for (String s : simSlotName) {
                                        ussdIntent.putExtra(s, 0);
                                    }
                                    if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0){
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        {
                                            ussdIntent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(0));
                                        }
                                    }
                                    context.startActivity(ussdIntent);

                                }
                                else if(getOutput(context, "SimOperatorName", 2).toLowerCase() .contains("vodafone".toLowerCase()))
                                {
                                    for (String s : simSlotName) {
                                        ussdIntent.putExtra(s, 1);
                                    }
                                    if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0){
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        {
                                            ussdIntent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(1));
                                        }
                                    }
                                }
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
                                    TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(context);
                                    TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                                    List<PhoneAccountHandle> phoneAccountHandleList = null;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                            return;
                                        }
                                        phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
                                    }


                                    TelephonyInfo info = TelephonyInfo.getInstance(context);
                                    if(info.isDualSIM()){
                                        ussdIntent.putExtra("com.android.phone.force.slot", true);
                                        ussdIntent.putExtra("Cdma_Supp", true);

                                        if (getOutput(context, "SimOperatorName", 1).toLowerCase() .contains("mtn"))

                                        {
                                            //SIM1 : 1
                                            for (String s : simSlotName) {
                                                ussdIntent.putExtra(s, 0);
                                            }
                                            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0){
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                                {
                                                    ussdIntent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(0));
                                                }
                                            }
                                            context.startActivity(ussdIntent);
                                        }
                                        else if(getOutput(context, "SimOperatorName", 2).toLowerCase() .contains("mtn".toLowerCase()))
                                        {
                                            // SIM2 : 2
                                            for (String s : simSlotName) {
                                                ussdIntent.putExtra(s, 1);
                                            }
                                            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0){
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                                {
                                                    ussdIntent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(1));
                                                }
                                            }
                                            context.startActivity(ussdIntent);
                                        }


                                        if (getOutput(context, "SimOperatorName", 1).toLowerCase() .contains("tigo"))

                                        {
                                            //SIM1 : 1
                                            for (String s : simSlotName) {
                                                ussdIntent.putExtra(s, 0);
                                            }
                                            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0){
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                                {
                                                    ussdIntent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(0));
                                                }
                                            }
                                            context.startActivity(ussdIntent);

                                        }
                                        else if(getOutput(context, "SimOperatorName", 2).toLowerCase() .contains("tigo".toLowerCase()))
                                        {
                                            // SIM2 : 2
                                            for (String s : simSlotName) {
                                                ussdIntent.putExtra(s, 1);
                                            }
                                            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0){
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                                {
                                                    ussdIntent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(1));
                                                }
                                            }
                                            context.startActivity(ussdIntent);
                                        }

                                        if (getOutput(context, "SimOperatorName", 1).toLowerCase() .contains("airtel"))

                                        {
                                            //SIM1 : 1
                                            for (String s : simSlotName) {
                                                ussdIntent.putExtra(s, 0);
                                            }
                                            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0){
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                                {
                                                    ussdIntent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(0));
                                                }
                                            }
                                            context.startActivity(ussdIntent);


                                        }
                                        else if(getOutput(context, "SimOperatorName", 2).toLowerCase() .contains("airtel".toLowerCase()))
                                        {
                                            // SIM2 : 2
                                            for (String s : simSlotName) {
                                                ussdIntent.putExtra(s, 1);
                                            }
                                            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0){
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                                {
                                                    ussdIntent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(1));
                                                }
                                            }

                                        }

                                        if (getOutput(context, "SimOperatorName", 1).toLowerCase() .contains("vodafone"))

                                        {
                                            for (String s : simSlotName) {
                                                ussdIntent.putExtra(s, 0);
                                            }
                                            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0){
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                                {
                                                    ussdIntent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(0));
                                                }
                                            }
                                            context.startActivity(ussdIntent);

                                        }
                                        else if(getOutput(context, "SimOperatorName", 2).toLowerCase() .contains("vodafone".toLowerCase()))
                                        {
                                            for (String s : simSlotName) {
                                                ussdIntent.putExtra(s, 1);
                                            }
                                            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0){
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                                {
                                                    ussdIntent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(1));
                                                }
                                            }
                                        }
                                    }
                                    else
                                    {
                                        context.startActivity(ussdIntent);
                                    }

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