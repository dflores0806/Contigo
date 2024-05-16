package com.spilab.monact.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;


import com.spilab.contigo.R;
import com.spilab.monact.activities.MainActivity;

import java.util.Random;

public class NotificationUtils {



    public static void showNotification(String title, String body, Context context, int icon) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID="11";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel= new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NotificationAlert", NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription ("Alert");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.WHITE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Intent to open APP when click in the notification.
        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notificationBuilder= new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.
                setAutoCancel(true).
                setPriority(NotificationCompat.PRIORITY_MAX).
                setContentIntent(resultPendingIntent).
                setLights(Color.WHITE,500,2000).
                setSmallIcon(icon).
                setContentTitle(title).
                addAction(com.google.android.gms.base.R.drawable.common_google_signin_btn_icon_dark, "Yes", resultPendingIntent).
                addAction(com.google.android.gms.base.R.drawable.common_google_signin_btn_icon_dark, "No", resultPendingIntent).
                setContentText(body);

        notificationManager.notify((new Random().nextInt()),notificationBuilder.build());

    }
}
