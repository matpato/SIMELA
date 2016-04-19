package com.example.tiago.alsrm_android.Receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

import com.example.tiago.alsrm_android.Service.UploadService;
import com.example.tiago.alsrm_android.Utils.Utils;

public class NetworkChangeReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //String status = Utils.getConnectivityStatusString(context);
        //Toast.makeText(context, status, Toast.LENGTH_LONG).show();

        if(Utils.getConnectivityStatus(context) == 1) {
            Intent service = new Intent(context, UploadService.class);
            startWakefulService(context, service);
        }
    }
}
