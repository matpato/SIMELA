package com.example.tiago.alsrm_android.Receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.example.tiago.alsrm_android.Activity.MainActivity;
import com.example.tiago.alsrm_android.R;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String title = "Upload";
        String text = "Data sent to the doctor";

        Intent contentIntent = new Intent(context, MainActivity.class);
        PendingIntent pendNotifIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), contentIntent, 0);

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendNotifIntent)
                .build();

        notification.defaults |= (Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification);

    }
}
