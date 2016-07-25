package isel.alsrm_android.PopUpDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import isel.alsrm_android.Activity.MainActivity;
import isel.alsrm_android.R;

public class ChangeMacDialogFragment extends DialogFragment {

    private View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        view = inflater.inflate(R.layout.dialog_change_mac, null);
        builder.setTitle(R.string.change_mac_address);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.setIcon(R.drawable.change_mac);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();

        if (dialog != null) {
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EditText editTextMAC = (EditText) view.findViewById(R.id.changeMAC);
                    String mac = editTextMAC.getText().toString();

                    if (validateMAC(mac)) {

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(MainActivity.MAC, mac);
                        editor.apply();

                        dismiss();
                    } else {

                        TextView invalidMAC = (TextView) view.findViewById(R.id.invalidMAC);
                        invalidMAC.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private boolean validateMAC(String mac){

        String[] parts = mac.split(":");

        if(parts.length!=6)
            return false;
        else{
            for (String part : parts) {
                try {
                    Integer.parseInt(part);
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return true;
        }
    }
}
