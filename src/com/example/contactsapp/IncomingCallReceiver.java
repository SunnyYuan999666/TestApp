package com.example.contactsapp;

import java.util.ArrayList;

import com.example.caontactsapp.modle.ContactModle;
import com.example.contactsapp.fragment.ContactsImplement;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class IncomingCallReceiver extends BroadcastReceiver {
    private Context mContext;
    private Intent mServiceIntent;
    private static final String TAG = "IncomingCallReceiver";

    public void onReceive(Context context, Intent intent) {
        mContext = context;
        Log.e(TAG, " " + "onReceive");
        try {
            // TELEPHONY MANAGER class object to register one listner
            TelephonyManager tmgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            // Create Listner
            MyPhoneStateListener PhoneListener = new MyPhoneStateListener();

            // Register listener for LISTEN_CALL_STATE
            tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        } catch (Exception e) {
            Log.e("Phone Receive Error", " " + e);
        }

    }

    private class MyPhoneStateListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String incomingNumber) {

           Log.e("MyPhoneStateListener", state + "contactsapp:   incoming no:" + incomingNumber);

            if (state == 1) {

                mServiceIntent = new Intent(mContext, NotificationService.class)
                        .putExtra("phoneNum", incomingNumber);

                mContext.startService(mServiceIntent);
              
            }
        }
    }
    
}
