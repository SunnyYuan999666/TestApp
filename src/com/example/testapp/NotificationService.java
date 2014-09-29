package com.example.testapp;

import java.util.ArrayList;

import com.example.testapp.fragment.ContactsImplement;
import com.example.testapp.modle.ContactModle;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class NotificationService extends IntentService {
    private static final String TAG = "NotificationService";

    public NotificationService() {
        super("NotificationService");
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onHandleIntent(Intent arg0) {
        // TODO Auto-generated method stub

        String incomingNumber = arg0.getExtras().getString("phoneNum");
        Log.v(TAG, "onHandleIntent");

        int notificationId = 001;
        PendingIntent viewPendingIntent = null;
        if (!isExistPhoneNumber(incomingNumber)) {
            // Build intent for notification content
            Intent viewIntent = new Intent(this, InsertContactActivity.class);
            // Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP
            // viewIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
            // Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // // Intent.FLAG_ACTIVITY_CLEAR_TASK
            viewIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    ); // Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
            viewIntent.putExtra("data", incomingNumber);
            viewPendingIntent = PendingIntent.getActivity(this,
                    MainActivity.INSERTCONTACTCODE, viewIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Log.e(TAG, "!isExistPhoneNumber");

        } else {
            Intent viewIntent = new Intent(this, MainActivity.class);
            viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    );// | Intent.FLAG_ACTIVITY_CLEAR_TASK
            Log.e(TAG, "isExistPhoneNumber");

        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.coming_phone_no))
                .setContentText(incomingNumber)
                .setContentIntent(viewPendingIntent);
        // .setAutoCancel(true);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager = NotificationManagerCompat
                .from(this);

        // Build the notification and issues it with notification
        // manager.
        notificationManager.notify(notificationId, notificationBuilder.build());

        // NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
        // this).setSmallIcon(R.drawable.ic_launcher)
        // .setContentTitle(getString(R.string.coming_phone_no))
        // .setContentText(incomingNumber).setAutoCancel(true);
        // // Creates an explicit intent for an Activity in your app
        // Intent resultIntent = new Intent(this, InsertContactActivity.class);
        // resultIntent.putExtra("data", incomingNumber);
        // // The stack builder object will contain an artificial back stack for
        // // the
        // // started Activity.
        // // This ensures that navigating backward from the Activity leads out
        // //of
        // // your application to the Home screen.
        // TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // // Adds the back stack for the Intent (but not the Intent itself)
        // stackBuilder.addParentStack(InsertContactActivity.class);
        // // Adds the Intent that starts the Activity to the top of the stack
        // stackBuilder.addNextIntent(resultIntent);
        // PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
        // PendingIntent.FLAG_UPDATE_CURRENT);
        // mBuilder.setContentIntent(resultPendingIntent);
        // NotificationManager mNotificationManager = (NotificationManager)
        // getSystemService(NOTIFICATION_SERVICE);
        // // notificationId allows you to update the notification later on.
        // mNotificationManager.notify(notificationId, mBuilder.build());

    }

    public boolean isExistPhoneNumber(String incomingNumber) {
        Log.e(TAG, "in isExistPhoneNumber()");
        if(null != ContactsImplement.rowIdOfExistPhoneNum(this,incomingNumber))
            return true;
        
//        ArrayList<ContactModle> contacts = ContactsImplement
//                .fetchContacts(this);
//        for (int i = 0; i < contacts.size(); i++) {
//            if (null != contacts.get(i).getContactPhoneNum()
//                    && contacts.get(i).getContactPhoneNum()
//                            .equals(incomingNumber))
//                return true;
//        }

        return false;

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}
