package com.example.tiago.alsrm_android.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.example.tiago.alsrm_android.R;

public class DialogFragmentChannels extends DialogFragment {

    private int selection;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String [] items = { "1", "2", "3", "4", "5"};

        AlertDialog.Builder theDialog = new AlertDialog.Builder(getActivity());

        theDialog.setTitle(getString(R.string.channels));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int position = sharedPreferences.getInt("channels", -1);

        theDialog.setSingleChoiceItems(items, position,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        selection = which;
                        //dialog.dismiss();
                    }
                });

        theDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (selection > 0) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("channels", selection);
                    editor.commit();
                }
            }
        });

        theDialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        return theDialog.create();
    }
}
