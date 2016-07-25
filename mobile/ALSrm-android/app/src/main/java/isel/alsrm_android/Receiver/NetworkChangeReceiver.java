package isel.alsrm_android.Receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import isel.alsrm_android.Service.UploadService;
import isel.alsrm_android.Utils.Utils;


public class NetworkChangeReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(Utils.getConnectivityStatus(context) == 1) {
            Intent service = new Intent(context, UploadService.class);
            startWakefulService(context, service);
        }
    }
}
