package com.example.chatapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;



import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);

        String notTitel = remoteMessage.getNotification().getTitle();
        String notBericht = remoteMessage.getNotification().getBody();
        String actie = remoteMessage.getNotification().getClickAction();
        String vanGebId = remoteMessage.getData().get("vanGebId");




        Notification.Builder mBuilder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notTitel)
                .setContentText(notBericht)
                .setPriority(Notification.PRIORITY_DEFAULT);

        Intent resultIntent = new Intent(actie);
        resultIntent.putExtra("gebId", vanGebId);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        int notId = (int) System.currentTimeMillis();
        NotificationManager notMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notMan.notify(notId, mBuilder.build());


    }
}