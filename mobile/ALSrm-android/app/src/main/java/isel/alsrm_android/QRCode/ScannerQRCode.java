package isel.alsrm_android.QRCode;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.zxing.Result;

import isel.alsrm_android.Activity.MainActivity;
import isel.alsrm_android.PopUpDialogFragment.PasswordDialogFragment;
import isel.alsrm_android.R;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerQRCode extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private boolean mFlash;
    private boolean mAutoFocus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            mFlash = savedInstanceState.getBoolean(FLASH_STATE, false);
            mAutoFocus = savedInstanceState.getBoolean(AUTO_FOCUS_STATE, true);
        } else {
            mFlash = false;
            mAutoFocus = true;
        }

        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_qrcode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_flash:

                if(mFlash){
                    mFlash = false;
                    mScannerView.setFlash(false);
                    item.setIcon(R.drawable.flash_on);
                }else {
                    mFlash = true;
                    mScannerView.setFlash(true);
                    item.setIcon(R.drawable.flash_off);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void handleResult(Result rawResult) {

        String result = rawResult.getText();
        result = result.substring(0, result.length()-1);
        result = result.substring(1, result.length());
        String[] parts = result.split(";");

        if(parts.length == 2) {
            String[] userId = parts[0].split("=");
            String[] mac = parts[1].split("=");

            if(userId.length == 2 && mac.length == 2 && userId[0].equals("Id") && mac[0].equals("Mac_Bitalino")) {

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(MainActivity.USER_ID, userId[1]);
                editor.putString(MainActivity.MAC, mac[1]);
                editor.apply();

                FragmentManager fm = this.getFragmentManager();
                DialogFragment dialog = new PasswordDialogFragment();
                dialog.show(fm, "dialog");
            }
            else
                errorQRCode();
        }
        else
            errorQRCode();
    }

    private void errorQRCode(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(R.string.qrcode_corrupted)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(R.drawable.error)
                .show();
    }
}
