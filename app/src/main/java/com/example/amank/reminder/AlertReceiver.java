package com.example.amank.reminder;
/*
* Designed By AmanK
* */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
/*
* CUSTOMIZE YOUR NOTIFICATION
* */
public class AlertReceiver extends BroadcastReceiver {
    String name1 ;
    int itd;

    @Override
    public void onReceive(Context context, Intent intent) {
        itd = intent.getIntExtra("EXTRA_SESSION_ID",0);
        name1 = intent.getStringExtra("MESSAGE");
        Log.e("TAGGG",""+itd);
        Log.e("TAGGG2",name1);
        createNotification(context, name1,"Your Event is about to begin please report Asap,Thankyou", "Alert",itd);


    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert,int itd) {

        PendingIntent notificationIntent = PendingIntent.getActivity(context, itd,
                new Intent(context, CatalogActivity.class), itd);
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notification_icon)  //ICON
                .setContentTitle(msg) //MEESAGE IN TITLE
                .setTicker(msgAlert)   //WHEN NOTIFACTION  ISN'T EXPANDED
                .setContentText(msgText); //TEXT EXPLAINIATION
        mBuilder.setContentIntent(notificationIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);
        //mBuilder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(itd, mBuilder.build());
    }
}