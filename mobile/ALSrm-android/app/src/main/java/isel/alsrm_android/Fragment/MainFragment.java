package isel.alsrm_android.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

import isel.alsrm_android.Activity.MainActivity;
import isel.alsrm_android.Bluetooth.ManageConnectedSocket;
import isel.alsrm_android.QRCode.ScannerQRCode;
import isel.alsrm_android.R;

public class MainFragment extends Fragment {

    private ProgressDialog ProgressDlg;
    private ArrayList<BluetoothDevice> DeviceList = new ArrayList<>();
    private static final int REQUEST_BLUETOOTH = 1;
    private String bitalino_MAC = null;
    private boolean paired = false;

    public static BluetoothAdapter bluetoothAdapt;
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final int samplingFrequency = 1000;

    public static BluetoothDevice bluetoothDevice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        bitalino_MAC = sharedPreferences.getString(MainActivity.MAC, null);
        String userID = sharedPreferences.getString(MainActivity.USER_ID, null);
        String password = sharedPreferences.getString(MainActivity.PASSWORD, null);

        if (userID == null || password == null)
            errorLogIn();

        if(!paired && bitalino_MAC != null) {
            ProgressDlg = new ProgressDialog(getActivity());
            ProgressDlg.setMessage(getString(R.string.scanning));

            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            getActivity().registerReceiver(mReceiver, filter);

            bluetoothAdapt = BluetoothAdapter.getDefaultAdapter();

            if (bluetoothAdapt == null) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.not_compatible)
                        .setMessage(R.string.not_support_bluetooth)
                        .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);
                            }
                        })
                        .setIcon(R.drawable.error)
                        .show();
            } else {
                // if disable, request to connect bluetooth
                if (!bluetoothAdapt.isEnabled()) {
                    Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBT, REQUEST_BLUETOOTH);
                } else
                    bluetoothAdapt.startDiscovery();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        toDiscardExam();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    if(isAdded()) {
                        showToast(getString(R.string.bluetooth_on));
                    }
                    bluetoothAdapt.startDiscovery();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                DeviceList = new ArrayList<>();
                ProgressDlg.show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                ProgressDlg.dismiss();

                connected();
                if(!paired) {
                    if(isAdded()) {
                        errorConnection();
                    }
                }
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                DeviceList.add(device);
                if(isAdded()){
                    showToast(getString(R.string.found_device)+": " + device.getName());
                }
                if(device.getAddress().equals(bitalino_MAC)){
                    ProgressDlg.dismiss();
                    bluetoothDevice = device;
                    paired = true;
                }
            }
        }
    };

    private void connected(){
        for(BluetoothDevice device : DeviceList){
            if(device.getAddress().equals(bitalino_MAC)){
                bluetoothDevice = device;
                paired = true;
                return;
            }
        }
        paired = false;
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void errorConnection(){
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.error_connection)
                .setMessage(getString(R.string.device_not_connected) + ": " + bitalino_MAC)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(R.drawable.bluetooth)
                .show();
    }

    private void errorLogIn(){
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.login)
                .setMessage(R.string.to_login)
                .setPositiveButton(R.string.button_continue, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), ScannerQRCode.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(R.drawable.log_in)
                .show();
    }

    private void toDiscardExam(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean run = sharedPreferences.getBoolean(ManageConnectedSocket.IS_INTENT_SERVICE_RUNNING, false);
        int examId = sharedPreferences.getInt(ManageConnectedSocket.EXAM_ID, 0);
        int examStepNum = sharedPreferences.getInt(ManageConnectedSocket.EXAM_STEP_NUM, 0);

        if(run && examId == 0 && examStepNum == 0){

            Intent intentService = new Intent(getActivity(), ManageConnectedSocket.class);
            getActivity().stopService(intentService);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(ManageConnectedSocket.IS_INTENT_SERVICE_RUNNING, false);
            editor.apply();
        }
    }
}
