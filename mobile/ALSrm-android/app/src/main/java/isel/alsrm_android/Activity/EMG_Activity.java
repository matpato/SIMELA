package isel.alsrm_android.Activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import isel.alsrm_android.Fragment.EMG_Fragment;

public class EMG_Activity extends AppCompatActivity {

    private static final String TAG = "EMGFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getIntent().getExtras();
        if(savedInstanceState == null) {
            EMG_Fragment fragment = new EMG_Fragment();
            fragment.setArguments(args);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(android.R.id.content, fragment, TAG);
            fragmentTransaction.commit();
        }
    }
}
