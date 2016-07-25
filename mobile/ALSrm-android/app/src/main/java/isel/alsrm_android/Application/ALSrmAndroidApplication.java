package isel.alsrm_android.Application;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import java.util.Random;

import isel.alsrm_android.Service.DeadlineService;
import isel.alsrm_android.Service.UpdateService;

public class ALSrmAndroidApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Intent service = new Intent(this, UpdateService.class);
        this.startService(service);
        setService();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    private void setService() {

        /************************ Notification **************************/

        long alertTime = 24*60*60*1000;
        Random generator = new Random();

        Intent intentNotification = new Intent(this, DeadlineService.class);
        PendingIntent pendingIntentNotification = PendingIntent.getService(this, generator.nextInt(), intentNotification, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmNotification = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmNotification.setInexactRepeating(AlarmManager.RTC_WAKEUP, 0, alertTime, pendingIntentNotification);

        /************************ Update ************************/

        Intent intentUpdate = new Intent(this, UpdateService.class);
        PendingIntent pendingIntentUpdate = PendingIntent.getService(this, generator.nextInt(), intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmUpdate = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmUpdate.setInexactRepeating(AlarmManager.RTC_WAKEUP, 0, alertTime, pendingIntentUpdate);
    }
}
