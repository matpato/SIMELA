package com.example.tiago.alsrm_android.Service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.example.tiago.alsrm_android.Receiver.NetworkChangeReceiver;
import com.example.tiago.alsrm_android.Receiver.NotificationReceiver;

public class UploadService extends IntentService {

    public UploadService() {
        super("UploadIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Do the work

        Intent i = new Intent(this, NotificationReceiver.class);
        this.sendBroadcast(i);

        NetworkChangeReceiver.completeWakefulIntent(intent);
    }
}