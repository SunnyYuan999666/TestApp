package com.example.contactsapp;

import java.util.ArrayList;

import com.example.caontactsapp.modle.ContactModle;
import com.example.contactsapp.fragment.ContactsImplement;
import com.example.contactsapp.R;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class NotificationService extends IntentService {
    private static final String TAG = "NotificationService";
    private static final String PHONE_NUM = "phoneNum";
    private static final String RAWID_OF_EXIST_PHONE_NUM = "rawIdOfExistPhoneNum";

    public NotificationService() {
        super("NotificationService");
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onHandleIntent(Intent arg0) {
        // TODO Auto-generated method stub
        String incomingNumber = arg0.getExtras().getString(PHONE_NUM);
        Log.v(TAG, "onHandleIntent");
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                this);
        int notificationId = 001;
        PendingIntent viewPendingIntent = null;
        String rawIdOfExistPhoneNum = isExistPhoneNumber(incomingNumber);
        if (null == rawIdOfExistPhoneNum) {
            Log.e(TAG, "!isExistPhoneNumber");
            Intent viewIntent = new Intent(this, InsertContactActivity.class);
            // viewIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); //
            // Intent.FLAG_ACTIVITY_NEW_TASK
            // |
            // Intent.FLAG_ACTIVITY_CLEAR_TASK
            viewIntent.putExtra("data", incomingNumber);
            viewPendingIntent = PendingIntent.getActivity(this,
                    MainActivity.INSERTCONTACTCODE, viewIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder
                    .setContentTitle(getString(R.string.coming_phone_no));
        } else {
            Log.e(TAG, "isExistPhoneNumber");
            Intent viewIntent = new Intent(this, MainActivity.class);
            viewIntent.putExtra(RAWID_OF_EXIST_PHONE_NUM, rawIdOfExistPhoneNum);
            // viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setContentTitle(ContactsImplement.getContact(
                    this, rawIdOfExistPhoneNum).getContactName());
        }
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher)
                .setContentText(incomingNumber)
                .setContentIntent(viewPendingIntent).setAutoCancel(true);
        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager = NotificationManagerCompat
                .from(this);
        // Build the notification and issues it with notification
        // manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public String isExistPhoneNumber(String incomingNumber) {
        Log.e(TAG, "in isExistPhoneNumber()");

        return ContactsImplement.rawIdOfExistPhoneNum(this, incomingNumber);
    }
}
