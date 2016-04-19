package com.example.tiago.alsrm_android.Activity;

import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import com.example.tiago.alsrm_android.R;

import java.util.ArrayList;

public class RecordingActivity extends AppCompatActivity {

    Button startBtn;
    Button armBtn;
    Button legBtn;
    Button externBtn;
    static Button btnSelected;
    static ArrayList<Button> buttons;
    private boolean start = false;
    private Class<?> exam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        buttons = new ArrayList<Button>();

        startBtn = (Button) this.findViewById(R.id.btnStart);
        buttons.add(startBtn);
        armBtn = (Button) this.findViewById(R.id.btnArm);
        buttons.add(armBtn);
        legBtn = (Button) this.findViewById(R.id.btnLeg);
        buttons.add(legBtn);
        externBtn = (Button) this.findViewById(R.id.btnExtern);
        buttons.add(externBtn);


        GridLayout gl0 = (GridLayout) this.findViewById(R.id.gridLayout);
        GridLayout gl1 = (GridLayout) this.findViewById(R.id.gridLayout2);
        GridLayout gl2 = (GridLayout) this.findViewById(R.id.gridLayout3);
        GridLayout gl3 = (GridLayout) this.findViewById(R.id.gridLayout4);

        // correct graphic interface presentation
        gl0.bringToFront();
        gl1.bringToFront();
        gl2.bringToFront();
        gl3.bringToFront();

        try {
            String s = getIntent().getExtras().getString("exam");
            exam = Class.forName(getIntent().getExtras().getString("exam"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        // armBtn event - set button pressed
        armBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchSupport(armBtn, event);
            }
        });

        // legBtn event - set button pressed
        legBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchSupport(legBtn, event);
            }
        });

        // externBtn event - set button pressed
        externBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchSupport(externBtn, event);
            }
        });

    }

    // Action onTouch from buttons, and set button permanently pressed or not.
    public static boolean onTouchSupport(Button btn, MotionEvent event){
        // show interest in events resulting from ACTION_DOWN
        if (event.getAction() == MotionEvent.ACTION_DOWN) return true;

        // don't handle event unless its ACTION_UP so "doSomething()" only runs once.
        if (event.getAction() != MotionEvent.ACTION_UP) return false;

        if(btn.isPressed())
            btn.setPressed(false);
        else{
            btn.setPressed(true);
            unpressedOtherBtns(btn);
            btnSelected = btn;
        }

        return true;
    }

    // Function to change all buttons to not pressed, except the button received from argument
    private static void unpressedOtherBtns(Button pressed) {
        for (Button b : buttons ) {
            if(b != pressed)
                b.setPressed(false);
        }
    }

    // TODO Alterar este Start!!!
    public void onClickStartBtn(View view) {

        if(!start){
            if(!areSelectedButtons()) {
                Toast.makeText(this, "Tem que escolher pelo menos 1 exame.", Toast.LENGTH_SHORT).show();
                return;
            }
            startBtn.setText("Pause");
            startBtn.getBackground().setColorFilter(new LightingColorFilter(ContextCompat.getColor(this, R.color.colorOnClickStart), ContextCompat.getColor(this, R.color.colorOnClickStartSecundary)));
            start = true;
            // New implementation
            Intent i = new Intent(getBaseContext(), exam);
            i.putExtra("area", btnSelected.getText());
            startActivity(i);
            finish();
        }
        else{
            startBtn.setText("Resume");
            startBtn.getBackground().clearColorFilter();
            startBtn.getBackground().setColorFilter(new LightingColorFilter(ContextCompat.getColor(this, R.color.colorOnClickResume), 0));
            start = false;
        }

    }

    public boolean areSelectedButtons(){
        return armBtn.isPressed() || legBtn.isPressed() || externBtn.isPressed();
    }
}
