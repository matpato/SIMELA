package isel.alsrm_android.Fragment;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import isel.alsrm_android.PopUpDialogFragment.ChangeMacDialogFragment;
import isel.alsrm_android.PopUpDialogFragment.LogInDialogFragment;
import isel.alsrm_android.QRCode.ScannerQRCode;
import isel.alsrm_android.R;


public class SettingsFragment extends Fragment {

    public static final String NOTIFICATION = "NOTIFICATION";

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_settings, container, false);

        ImageButton imageButtonQRCode = (ImageButton) view.findViewById(R.id.buttonQRCode);
        imageButtonQRCode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(getActivity(), ScannerQRCode.class);
                startActivity(intent);
            }
        });

        ImageButton imageButtonKey = (ImageButton) view.findViewById(R.id.buttonKey);
        imageButtonKey.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                FragmentManager fm = getActivity().getFragmentManager();
                DialogFragment dialog = new LogInDialogFragment();
                dialog.show(fm, "dialog");
            }
        });

        ImageButton imageChangeMac = (ImageButton) view.findViewById(R.id.imageChangeMac);
        imageChangeMac.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                FragmentManager fm = getActivity().getFragmentManager();
                DialogFragment dialog = new ChangeMacDialogFragment();
                dialog.show(fm, "dialog");
            }
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean notification = sharedPreferences.getBoolean(NOTIFICATION, true);

        Switch switchNotifications = (Switch) view.findViewById(R.id.switchNotifications);
        switchNotifications.setChecked(notification);
        switchNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(NOTIFICATION, isChecked); //off
                editor.apply();
            }
        });
        return view;
    }
}
