package isel.alsrm_android.Receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;

import isel.alsrm_android.Activity.MainActivity;
import isel.alsrm_android.R;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String title = context.getString(R.string.upload);
        String text = context.getString(R.string.data_sent_to_the_doctor);

        Intent contentIntent = new Intent(context, MainActivity.class);
        PendingIntent pendNotifIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), contentIntent, 0);

        final Resources res = context.getResources();
        final Bitmap picture = BitmapFactory.decodeResource(res, R.mipmap.ic_launcher);

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(text)
                .setLargeIcon(picture)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendNotifIntent)
                .build();

        notification.defaults |= (Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification);

    }
}

