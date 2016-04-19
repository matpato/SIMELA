package com.example.tiago.alsrm_android.Activity;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tiago.alsrm_android.Bluetooth.ManageConnectedSocket;
import com.example.tiago.alsrm_android.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

public class EMG_Activity extends AppCompatActivity {

    // An object that manages Messages in a Thread
    public static Handler HandlerMessager;

    private LineChart mChart;
    private int xValue;

    private boolean start = false;
    private Button button_start_stop;

    private Thread manageThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg);

        mChart = (LineChart) findViewById(R.id.chart);

        // no description text
        mChart.setDescription("EMG - " + getIntent().getExtras().getString("area"));

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(5);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaxValue(1.65f);
        leftAxis.setAxisMinValue(-1.65f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        HandlerMessager = new Handler(Looper.getMainLooper()){

            /*
             * handleMessage() defines the operations to perform when the
             * Handler receives a new Message to process.
             */
            @Override
            public void handleMessage (Message inputMessage){

                // Gets the image task from the incoming Message object.
                double[] frame = (double[]) inputMessage.obj;

                System.out.println("" + frame[1]);
                addEntry(frame[1]);
            }
        };

        button_start_stop = (Button) findViewById(R.id.button_start_stop);
        button_start_stop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!start) {

                    ManageConnectedSocket manageConnectedSocket = new ManageConnectedSocket(MainActivity.bluetoothDevice, MainActivity.samplingFrequency, new int[] {0}, new int[] {1, 0, 1, 1});
                    manageThread = new Thread(manageConnectedSocket);
                    manageThread.start();

                    button_start_stop.setText("STOP");
                    start = true;
                } else {

                    manageThread.interrupt();
                    button_start_stop.setText("START");
                    start = false;
                }
            }
        });
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        return set;
    }

    private void addEntry(double value) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            // add a new x-value first

            data.addXValue(""+xValue);
            xValue++;

            data.addEntry(new Entry( (float)value, set.getEntryCount() ), 0);

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(120);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getXValCount() - 121);

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }
}
