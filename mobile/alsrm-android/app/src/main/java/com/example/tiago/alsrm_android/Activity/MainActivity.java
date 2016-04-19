package com.example.tiago.alsrm_android.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import com.example.tiago.alsrm_android.R;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog ProgressDlg;
    private ArrayList<BluetoothDevice> DeviceList = new ArrayList<BluetoothDevice>();
    private static final int REQUEST_BLUETOOTH = 1;
    private static final String bitalino_MAC =  "20:15:12:22:84:72";
    private boolean paired = false;

    public static BluetoothAdapter bluetoothAdapt;
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final int samplingFrequency = 1000;

    public static BluetoothDevice bluetoothDevice ;

    Button ECGBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridLayout gl0 = (GridLayout) this.findViewById(R.id.gridLayout);
        GridLayout gl1 = (GridLayout) this.findViewById(R.id.gridLayout2);
        GridLayout gl2 = (GridLayout) this.findViewById(R.id.gridLayout3);
        GridLayout gl3 = (GridLayout) this.findViewById(R.id.gridLayout4);

        ECGBtn = (Button) this.findViewById(R.id.ECGBtn);

        // correct graphic interface presentation
        gl0.bringToFront();
        gl1.bringToFront();
        gl2.bringToFront();
        gl3.bringToFront();


        ProgressDlg = new ProgressDialog(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        bluetoothAdapt = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapt == null) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.not_compatible)
                    .setMessage(R.string.not_support_bluetooth)
                    .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else {
            // if disable, request to connect bluetooth
            if (!bluetoothAdapt.isEnabled()) {
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, REQUEST_BLUETOOTH);
            }
            bluetoothAdapt.startDiscovery();
        }

    }

    public void onClickEMGBtn(View v){
        Intent i = new Intent(getBaseContext(), RecordingActivity.class);
        i.putExtra("exam", "com.example.tiago.alsrm_android.Activity.EMG_Activity");
        startActivity(i);
    }

    public void onClickECGBtn(View v){
        Intent i = new Intent(getBaseContext(), RecordingActivity.class);
        i.putExtra("exam", "com.example.tiago.alsrm_android.Activity.ECG_Activity");
        startActivity(i);
    }

    public void onClickEDABtn(View v){
        Intent i = new Intent(getBaseContext(), RecordingActivity.class);
        i.putExtra("exam", "com.example.tiago.alsrm_android.Activity.EDA_Activity");
        startActivity(i);
    }
/*
    public void onClickEEG(View v){
        Intent i = new Intent(getBaseContext(), EEG_Activity.class);
        startActivity(i);
    }

    public void onClickACC(View v){
        Intent i = new Intent(getBaseContext(), ACC_Activity.class);
        startActivity(i);
    }

    public void onClickLUX(View v){
        Intent i = new Intent(getBaseContext(), LUX_Activity.class);
        startActivity(i);
    }
    */

    @Override
    public void onPause() {
        super.onPause();
        if (bluetoothAdapt != null) {
            if (bluetoothAdapt.isDiscovering()) {
                bluetoothAdapt.cancelDiscovery();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (bluetoothAdapt != null) {
            if (!bluetoothAdapt.isDiscovering() && !paired) {
                bluetoothAdapt.startDiscovery();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    showToast("Enabled");
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                DeviceList = new ArrayList<BluetoothDevice>();
                ProgressDlg.show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                ProgressDlg.dismiss();

                for(BluetoothDevice device : DeviceList){
                    if(device.getAddress().equals(bitalino_MAC)){

                        bluetoothDevice = device;
                        paired = true;
                        break;
                    }
                }
                if(!paired)
                    showToast("Device "+ bitalino_MAC + " not connected!");
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                DeviceList.add(device);
                showToast("Found device " + device.getName());
            }
        }
    };

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
