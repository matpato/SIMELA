package com.example.tiago.alsrm_android.Activity;

import android.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tiago.alsrm_android.Fragment.DialogFragmentChannels;
import com.example.tiago.alsrm_android.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button channels = (Button) findViewById(R.id.button_channels);

        channels.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DialogFragment myFragment = new DialogFragmentChannels();
                myFragment.show(getFragmentManager(), null);
            }
        });
    }
}
