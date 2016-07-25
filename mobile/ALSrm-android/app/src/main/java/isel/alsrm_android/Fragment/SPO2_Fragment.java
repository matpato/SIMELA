package isel.alsrm_android.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import isel.alsrm_android.API.ExamSteps;
import isel.alsrm_android.Activity.MainActivity;
import isel.alsrm_android.Bluetooth.ManageConnectedSocket;
import isel.alsrm_android.Chart.Chart;
import isel.alsrm_android.Database.AlsrmContract;
import isel.alsrm_android.Database.AlsrmSchema;
import isel.alsrm_android.Database.Operations;
import isel.alsrm_android.R;
import isel.alsrm_android.Utils.Utils;

public class SPO2_Fragment extends Fragment {

    private AlsrmAsyncQueryHandler queryHandler;
    private BroadcastReceiverData broadcastReceiverData;
    private Intent intentService;
    public static final String SPO2 = "SPO2";
    private Chart chart = null;
    private View fragmentRootContainer;
    private boolean start = false;
    private boolean stop = false;
    private boolean run = false;
    private int examId;
    private int examStepNum;
    private int xValue;
    private ImageButton button_start_pause;
    private Chronometer chronometer;
    private boolean startTime = false;
    private int muscle;

    private static class AlsrmAsyncQueryHandler extends AsyncQueryHandler {

        public AlsrmAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor.moveToFirst()) {

                ExamSteps examSteps  = new ExamSteps();

                examSteps.setExamId(cursor.getInt(cursor.getColumnIndex(AlsrmSchema.exam_id)));
                examSteps.setStepNum(cursor.getInt(cursor.getColumnIndex(AlsrmSchema.examStep_num)));
                examSteps.setDescription(cursor.getString(cursor.getColumnIndex(AlsrmSchema.examStep_description)));
                examSteps.setState(cursor.getString(cursor.getColumnIndex(AlsrmSchema.examStep_state)));
                examSteps.setTime(cursor.getInt(cursor.getColumnIndex(AlsrmSchema.examStep_time)));
                examSteps.setElapsedTime(cursor.getInt(cursor.getColumnIndex(AlsrmSchema.examStep_elapsed_time)));
                examSteps.setStringInitialDate(cursor.getString(cursor.getColumnIndex(AlsrmSchema.exam_initialDate)));
                examSteps.setStringEndDate(cursor.getString(cursor.getColumnIndex(AlsrmSchema.exam_endDate)));

                if(examSteps.getElapsedTime() >= ((examSteps.getTime()*0.95)*60000)) {
                    Date examEndDate = new Date();
                    DateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                    ContentValues values = new ContentValues();
                    values.put(AlsrmSchema.examStep_state, AlsrmSchema.COMPLETED);
                    values.put(AlsrmSchema.exam_endDate, dateformat.format(examEndDate));
                    this.startUpdate(1, null, AlsrmContract.ExamSteps.CONTENT_URI, values,
                            AlsrmSchema.exam_id + " = ? AND " + AlsrmSchema.examStep_num + " = ? ", new String[]{""+examSteps.getExamId(), ""+examSteps.getStepNum()});
                }else{
                    this.startDelete(1, null, AlsrmContract.Points.CONTENT_URI,
                            AlsrmSchema.exam_id + " = ? AND " + AlsrmSchema.examstepnum + " = ? ", new String[]{""+examSteps.getExamId(), ""+examSteps.getStepNum()});

                    ContentValues values = new ContentValues();
                    values.put(AlsrmSchema.examStep_elapsed_time, 0);
                    this.startUpdate(1, null, AlsrmContract.ExamSteps.CONTENT_URI, values,
                            AlsrmSchema.exam_id + " = ? AND " + AlsrmSchema.examStep_num + " = ? ",new String[]{""+examSteps.getExamId(), ""+examSteps.getStepNum()});
                }
            }
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        queryHandler = new AlsrmAsyncQueryHandler(this.getActivity().getContentResolver());

        if(getArguments() != null) {
            muscle = getArguments().getInt(MainActivity.MUSCLE, 0);
            examId = getArguments().getInt(ManageConnectedSocket.EXAM_ID, 0);
            examStepNum = getArguments().getInt(ManageConnectedSocket.EXAM_STEP_NUM, 0);
        }

        if(fragmentRootContainer == null) {
            fragmentRootContainer = inflater.inflate(R.layout.activity_exam, container, false);

            if (chart == null)
                chart = new Chart((LineChart) fragmentRootContainer.findViewById(R.id.chart), SPO2+" - "+ Utils.muscleAbbreviation(muscle), 1023f, 1f);

            button_start_pause = (ImageButton) fragmentRootContainer.findViewById(R.id.button_start_pause);
            if (button_start_pause != null)
                buttonStartPauseClick(button_start_pause);

            ImageButton button_stop = (ImageButton) fragmentRootContainer.findViewById(R.id.button_stop);
            if (button_stop != null)
                buttonStopClick(button_stop);

            chronometer = (Chronometer) fragmentRootContainer.findViewById(R.id.chronometer);
        }
        return fragmentRootContainer;
    }

    private void buttonStartPauseClick(ImageButton button_start_pause){
        button_start_pause.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                ImageButton imageButton = (ImageButton) v;
                if (!stop) {
                    if (!run) {
                        if (MainFragment.bluetoothDevice != null) {

                            if (!start)
                                start = true;

                            //Start IntentService
                            intentService = new Intent(getActivity(), ManageConnectedSocket.class);
                            intentService.putExtra(ManageConnectedSocket.EXAM_TYPE, SPO2);
                            intentService.putExtra(MainActivity.MUSCLE, muscle);
                            intentService.putExtra(ManageConnectedSocket.ANALOG_CHANNELS,  new int[]{2});
                            intentService.putExtra(ManageConnectedSocket.DIGITAL_CHANNELS, new int[]{1, 0, 1, 1});
                            intentService.putExtra(ManageConnectedSocket.X_VALUE, xValue);

                            if(examId != 0 && examStepNum != 0) {
                                intentService.putExtra(ManageConnectedSocket.EXAM_ID, examId);
                                intentService.putExtra(ManageConnectedSocket.EXAM_STEP_NUM, examStepNum);
                            }

                            getActivity().startService(intentService);

                            if(getView() != null) {
                                if (getView().getTag().equals("tablet_screen_xlarge"))
                                    imageButton.setImageResource(R.drawable.pause_xlarge);
                                if (getView().getTag().equals("tablet_screen_large"))
                                    imageButton.setImageResource(R.drawable.pause_large);
                                if (getView().getTag().equals("phone_screen"))
                                    imageButton.setImageResource(R.drawable.pause);
                            }
                            run = true;
                        }
                    } else {
                        if(intentService != null){
                            getActivity().stopService(intentService);
                        }

                        if(getView() != null) {
                            if (getView().getTag().equals("tablet_screen_xlarge"))
                                imageButton.setImageResource(R.drawable.play_xlarge);
                            if (getView().getTag().equals("tablet_screen_large"))
                                imageButton.setImageResource(R.drawable.play_large);
                            if (getView().getTag().equals("phone_screen"))
                                imageButton.setImageResource(R.drawable.play);
                        }

                        run = false;
                        if(chronometer != null && examId != 0 && examStepNum != 0) {
                            chronometer.stop();
                            chronometer.setVisibility(View.INVISIBLE);
                            startTime = false;
                        }
                    }
                }
            }
        });
    }

    private void buttonStopClick(ImageButton button_stop){
        button_stop.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (start)
                    if(!stop)
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.stop)
                                .setMessage(R.string.are_you_sure_stop)
                                .setPositiveButton(R.string.stop, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        stop = true;
                                        if (chronometer != null && examId != 0 && examStepNum != 0) {
                                            chronometer.stop();
                                            chronometer.setVisibility(View.INVISIBLE);
                                        }

                                        ImageButton button_start_pause = (ImageButton) fragmentRootContainer.findViewById(R.id.button_start_pause);
                                        if (button_start_pause != null)
                                            button_start_pause.setVisibility(View.INVISIBLE);

                                        if (run) {
                                            if (intentService != null)
                                                getActivity().stopService(intentService);
                                        }

                                        if (examId != 0 && examStepNum != 0) {
                                            queryHandler.startQuery(1, null, AlsrmContract.ExamSteps.CONTENT_URI, Operations.ExamStepsProjection,
                                                    AlsrmSchema.exam_id + " = ? AND " + AlsrmSchema.examStep_num + " = ? ", new String[]{"" + examId, "" + examStepNum}, null);
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(R.drawable.stop_xlarge)
                                .show();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        //register BroadcastReceiver
        broadcastReceiverData = new BroadcastReceiverData();
        IntentFilter intentFilter = null;

        if(muscle == AlsrmSchema.AnteriorTibialis)
            intentFilter = new IntentFilter(ManageConnectedSocket.ACTION_IntentService_SPO2_AT);
        if(muscle == AlsrmSchema.FlexorCarpiRadialis)
            intentFilter = new IntentFilter(ManageConnectedSocket.ACTION_IntentService_SPO2_FCR);
        if(muscle == AlsrmSchema.SternocleidoMastoideus)
            intentFilter = new IntentFilter(ManageConnectedSocket.ACTION_IntentService_SPO2_SCM);

        if (intentFilter != null) intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        getActivity().registerReceiver(broadcastReceiverData, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        //un-register BroadcastReceiver
        getActivity().unregisterReceiver(broadcastReceiverData);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout linearLayout = (LinearLayout) getView();
        if(linearLayout != null)
            linearLayout.removeAllViewsInLayout();

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            ViewGroup parentViewGroup = (ViewGroup)chart.getChart().getParent();
            if (parentViewGroup != null)
                parentViewGroup.removeView(chart.getChart());

            fragmentRootContainer = inflater.inflate(R.layout.activity_exam, linearLayout, true);
            LineChart lineChart = (LineChart) fragmentRootContainer.findViewById(R.id.chart);
            lineChart.addView(chart.getChart());
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

            ViewGroup parentViewGroup = (ViewGroup)chart.getChart().getParent();
            if (parentViewGroup != null)
                parentViewGroup.removeView(chart.getChart());

            fragmentRootContainer = inflater.inflate(R.layout.activity_exam, linearLayout, true);
            inflater.inflate(R.layout.activity_exam, linearLayout, false);

            LineChart lineChart = (LineChart) fragmentRootContainer.findViewById(R.id.chart);
            lineChart.addView(chart.getChart());

            startTime = false;
            chronometer = (Chronometer) fragmentRootContainer.findViewById(R.id.chronometer);

            button_start_pause = (ImageButton) fragmentRootContainer.findViewById(R.id.button_start_pause);
            if (button_start_pause != null)
                buttonStartPauseClick(button_start_pause);

            ImageButton button_stop = (ImageButton) fragmentRootContainer.findViewById(R.id.button_stop);
            if (button_stop != null)
                buttonStopClick(button_stop);

            if(button_start_pause != null) {
                if (stop)
                    button_start_pause.setVisibility(View.INVISIBLE);
                if (run){
                    if(getView().getTag().equals("tablet_screen_xlarge"))
                        button_start_pause.setImageResource(R.drawable.pause_xlarge);
                    if(getView().getTag().equals("tablet_screen_large"))
                        button_start_pause.setImageResource(R.drawable.pause_large);
                    if(getView().getTag().equals("phone_screen"))
                        button_start_pause.setImageResource(R.drawable.pause);
                }
                else{
                    if(getView().getTag().equals("tablet_screen_xlarge"))
                        button_start_pause.setImageResource(R.drawable.play_xlarge);
                    if(getView().getTag().equals("tablet_screen_large"))
                        button_start_pause.setImageResource(R.drawable.play_large);
                    if(getView().getTag().equals("phone_screen"))
                        button_start_pause.setImageResource(R.drawable.play);
                }
            }
        }
    }

    private class BroadcastReceiverData extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getBooleanExtra(ManageConnectedSocket.EXAM_COMPLETED, false)){

                stop = true;
                if(chronometer != null && examId != 0 && examStepNum != 0) {
                    chronometer.stop();
                    chronometer.setVisibility(View.INVISIBLE);
                }

                ImageButton button_start_pause = (ImageButton) fragmentRootContainer.findViewById(R.id.button_start_pause);
                if (button_start_pause != null)
                    button_start_pause.setVisibility(View.INVISIBLE);

                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.completed_exam)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(R.drawable.complete)
                        .show();

            } else if(intent.getBooleanExtra(ManageConnectedSocket.LOST_COMMUNICATION, false)){

                stop = true;
                if(chronometer != null && examId != 0 && examStepNum != 0) {
                    chronometer.stop();
                    chronometer.setVisibility(View.INVISIBLE);
                }

                lostCommunication();

                ImageButton button_start_pause = (ImageButton) fragmentRootContainer.findViewById(R.id.button_start_pause);
                if (button_start_pause != null) {
                    if(getView() != null) {
                        if (getView().getTag().equals("tablet_screen_xlarge"))
                            button_start_pause.setImageResource(R.drawable.play_xlarge);
                        if (getView().getTag().equals("tablet_screen_large"))
                            button_start_pause.setImageResource(R.drawable.play_large);
                        if (getView().getTag().equals("phone_screen"))
                            button_start_pause.setImageResource(R.drawable.play);
                    }
                }

            } else{

                chart.addEntry(intent.getIntExtra(ManageConnectedSocket.X_KEY_OUT, 0), intent.getDoubleArrayExtra(ManageConnectedSocket.DATA_KEY_OUT)[2]);
                xValue = intent.getIntExtra(ManageConnectedSocket.X_KEY_OUT, 0);
                examId = intent.getIntExtra(ManageConnectedSocket.EXAM_ID, 0);
                examStepNum = intent.getIntExtra(ManageConnectedSocket.EXAM_STEP_NUM, 0);

                if (!start) {
                    start = true;
                    run = true;
                    intentService = new Intent(getActivity(), ManageConnectedSocket.class);
                    if (button_start_pause != null) {
                        if(getView() != null) {
                            if (getView().getTag().equals("tablet_screen_xlarge"))
                                button_start_pause.setImageResource(R.drawable.pause_xlarge);
                            if (getView().getTag().equals("tablet_screen_large"))
                                button_start_pause.setImageResource(R.drawable.pause_large);
                            if (getView().getTag().equals("phone_screen"))
                                button_start_pause.setImageResource(R.drawable.pause);
                        }
                    }
                }

                if (examId != 0 && examStepNum != 0 && !startTime && run) {

                    if (chronometer != null) {
                        startTime = true;
                        chronometer.setVisibility(View.VISIBLE);
                        chronometer.setBase(SystemClock.elapsedRealtime() - intent.getLongExtra(ManageConnectedSocket.TIME_KEY_OUT, 0));
                        chronometer.start();
                    }
                }
            }
        }
    }

    private void lostCommunication(){
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.bluetooth_error)
                .setMessage(R.string.lost_communication)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(R.drawable.error)
                .show();
    }
}
