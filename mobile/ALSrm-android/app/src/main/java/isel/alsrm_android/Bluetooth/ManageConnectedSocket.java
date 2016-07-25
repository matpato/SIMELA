package isel.alsrm_android.Bluetooth;

import android.app.AlertDialog;
import android.app.IntentService;
import android.bluetooth.BluetoothSocket;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import isel.alsrm_android.API.ExamSteps;
import isel.alsrm_android.API.Exams;
import isel.alsrm_android.Activity.MainActivity;
import isel.alsrm_android.BITalino.BITalinoDevice;
import isel.alsrm_android.BITalino.BITalinoException;
import isel.alsrm_android.BITalino.BITalinoFrame;
import isel.alsrm_android.BITalino.SensorDataConverter;
import isel.alsrm_android.Database.AlsrmContract;
import isel.alsrm_android.Database.AlsrmSchema;
import isel.alsrm_android.Database.Operations;
import isel.alsrm_android.Fragment.MainFragment;
import isel.alsrm_android.R;

import static isel.alsrm_android.Database.Operations.getContentValuesPoint;
import static isel.alsrm_android.Database.Operations.getMaxPoint;
import static isel.alsrm_android.Database.Operations.getPerformExam;

public class ManageConnectedSocket extends IntentService {

    public static final String ACTION_IntentService_ECG = "com.example.tiago.alsrm_android.Bluetooth.RESULT_ECG";
    public static final String ACTION_IntentService_SPO2_AT = "com.example.tiago.alsrm_android.Bluetooth.RESULT_SPO2_AT";
    public static final String ACTION_IntentService_SPO2_FCR = "com.example.tiago.alsrm_android.Bluetooth.RESULT_SPO2_FCR";
    public static final String ACTION_IntentService_SPO2_SCM = "com.example.tiago.alsrm_android.Bluetooth.RESULT_SPO2_SCM";
    public static final String ACTION_IntentService_EMG_AT = "com.example.tiago.alsrm_android.Bluetooth.RESULT_EMG_AT";
    public static final String ACTION_IntentService_EMG_FCR = "com.example.tiago.alsrm_android.Bluetooth.RESULT_EMG_FCR";
    public static final String ACTION_IntentService_EMG_SCM = "com.example.tiago.alsrm_android.Bluetooth.RESULT_EMG_SCM";
    public static final String ACTION_IntentService_EEG = "com.example.tiago.alsrm_android.Bluetooth.RESULT_EEG";

    public static final String TIME_KEY_OUT = "TIME";
    public static final String DATA_KEY_OUT = "DATA";
    public static final String X_KEY_OUT = "X";
    public static final String X_VALUE = "X_VALUE";
    public static final String DIGITAL_CHANNELS = "DIGITAL_CHANNELS";
    public static final String ANALOG_CHANNELS = "ANALOG_CHANNELS";
    public static final String EXAM_TYPE = "EXAM_TYPE";
    public static final String EXAM_ID = "EXAM_ID";
    public static final String IS_INTENT_SERVICE_RUNNING = "IS_INTENT_SERVICE_RUNNING";
    public static final String EXAM_STEP_NUM = "EXAM_STEP_NUM";
    public static final String EXAM_COMPLETED = "EXAM_COMPLETED";
    public static final String LOST_COMMUNICATION = "LOST_COMMUNICATION";

    private AlsrmAsyncQueryHandler queryHandler;
    private BluetoothSocket btSocket;
    private BITalinoDevice bitalino;
    private boolean stop = false;
    private String exam_type;
    private long startTimeMillis;
    private int xValue;
    private long elapsedMillis;
    private ExamSteps examToPerform;
    private int muscle;
    private boolean isIntentServiceRunning = false;

    private static class AlsrmAsyncQueryHandler extends AsyncQueryHandler {

        public AlsrmAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }
    }

    public ManageConnectedSocket() {
        super("ManageConnectedSocketService");
    }

    @Override
    public void onDestroy() {

        stop = true;
        isIntentServiceRunning = false;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_INTENT_SERVICE_RUNNING, isIntentServiceRunning);
        editor.apply();

        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        queryHandler = new AlsrmAsyncQueryHandler(this.getContentResolver());
        isIntentServiceRunning = true;

        muscle = intent.getIntExtra(MainActivity.MUSCLE, 0);
        exam_type = intent.getStringExtra(EXAM_TYPE);
        int examId = intent.getIntExtra(EXAM_ID, 0);
        int examStepNum = intent.getIntExtra(EXAM_STEP_NUM, 0);
        xValue = intent.getIntExtra(X_VALUE, 0); //exam dummy, is not in the database

        Date currentDate = new Date();
        String description = stateOfDay(currentDate);

        if(examId != 0 && examStepNum != 0)
            examToPerform = getExamStep(examId, examStepNum);
        else{
            LinkedList<Exams> list_exams = getExamToPerformInDB(currentDate);
            examToPerform = getExamStepToPerformDB(list_exams, description);
        }
        sharedPreferencesRunning();

        if(examId != 0 && examStepNum != 0)
            getXvalue();

        establishBluetoothConnection();

        try {
            // Get the input and output streams, using temp objects because
            // member streams are final
            InputStream InStream = btSocket.getInputStream();
            OutputStream OutStream = btSocket.getOutputStream();

            bitalino = new BITalinoDevice(MainFragment.samplingFrequency, intent.getIntArrayExtra(ANALOG_CHANNELS));
            bitalino.open(InStream, OutStream);

            // start acquisition on predefined analog channels
            bitalino.start();
            // trigger digital outputs
            bitalino.trigger(intent.getIntArrayExtra(DIGITAL_CHANNELS));

            initialUpdateDBExam(currentDate);

            // Keep listening to the InputStream
            while (!stop) {
                // Read from the InputStream
                final int numberOfSamplesToRead = 10;
                BITalinoFrame[] frames = bitalino.read(numberOfSamplesToRead);

                for (BITalinoFrame frame : frames) {

                    if(stop) break;

                    double [] data = dataConverter(frame);
                    System.out.println(frame.toString());

                    if(examToPerform!=null)
                        insertContentProvider(data);

                    sendMessage(data, xValue, examToPerform);
                    xValue++;
                    Thread.sleep(5);
                }
                updateDBExamElapsedTime();
            }
        } catch (BITalinoException ex) {
            sendMessageLostCommunication();
            stop = true;
            stopSelf();
        }
        catch (InterruptedException | IOException ex) {
            ex.printStackTrace();
        }
        stopBitalino();
        cancel();
    }

    private void sharedPreferencesRunning(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_INTENT_SERVICE_RUNNING, isIntentServiceRunning);
        if(examToPerform != null) {
            editor.putInt(EXAM_ID, examToPerform.getExamId());
            editor.putInt(EXAM_STEP_NUM, examToPerform.getStepNum());
        }
        editor.apply();
    }

    private ExamSteps getExamStep(int examId, int examStepNum) {

        Cursor cursorExamSteps = Operations.getExamSteps(examId, examStepNum, this);
        if (cursorExamSteps.moveToFirst()) {

            ExamSteps examSteps  = new ExamSteps();

            examSteps.setExamId(cursorExamSteps.getInt(cursorExamSteps.getColumnIndex(AlsrmSchema.exam_id)));
            examSteps.setStepNum(cursorExamSteps.getInt(cursorExamSteps.getColumnIndex(AlsrmSchema.examStep_num)));
            examSteps.setDescription(cursorExamSteps.getString(cursorExamSteps.getColumnIndex(AlsrmSchema.examStep_description)));
            examSteps.setState(cursorExamSteps.getString(cursorExamSteps.getColumnIndex(AlsrmSchema.examStep_state)));
            examSteps.setTime(cursorExamSteps.getInt(cursorExamSteps.getColumnIndex(AlsrmSchema.examStep_time)));
            examSteps.setElapsedTime(cursorExamSteps.getInt(cursorExamSteps.getColumnIndex(AlsrmSchema.examStep_elapsed_time)));
            examSteps.setStringInitialDate(cursorExamSteps.getString(cursorExamSteps.getColumnIndex(AlsrmSchema.exam_initialDate)));
            examSteps.setStringEndDate(cursorExamSteps.getString(cursorExamSteps.getColumnIndex(AlsrmSchema.exam_endDate)));

            return examSteps;
        }
        return null;
    }

    private void updateDBExamElapsedTime(){
        elapsedMillis = SystemClock.elapsedRealtime() - startTimeMillis;
        if(examToPerform!=null)
            updateElapsedTime();
    }

    private void initialUpdateDBExam(Date currentDate){

        if(examToPerform!=null) {

            elapsedMillis = examToPerform.getElapsedTime();
            startTimeMillis = SystemClock.elapsedRealtime() - examToPerform.getElapsedTime();
            DateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());

            if (examToPerform.getInitialDate() == null) {
                ContentValues values = new ContentValues();
                values.put(AlsrmSchema.exam_initialDate, dateformat.format(currentDate));
                queryHandler.startUpdate(1, null, AlsrmContract.ExamSteps.CONTENT_URI, values,
                        AlsrmSchema.exam_id + " = ? AND " + AlsrmSchema.examStep_num + " = ? ", new String[]{"" + examToPerform.getExamId(), "" + examToPerform.getStepNum()});

            }
        }
    }

    private void establishBluetoothConnection(){

        BluetoothSocket tmp = null;
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp =  MainFragment.bluetoothDevice.createRfcommSocketToServiceRecord(MainFragment.MY_UUID);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        btSocket = tmp;

        // Cancel discovery because it will slow down the connection
        MainFragment.bluetoothAdapt.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            btSocket.connect();

        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                btSocket.close();
            } catch (IOException closeException) {
                closeException.printStackTrace();
            }
        }
    }

    private void getXvalue(){
        if(examToPerform!=null) {
            Cursor cursorXvalue = getMaxPoint(examToPerform.getExamId(), examToPerform.getStepNum(), this);
            if (cursorXvalue.moveToFirst()) {
                xValue = cursorXvalue.getInt(cursorXvalue.getColumnIndex(AlsrmSchema.x_data));
                xValue++;
            }
        }
        else{
            xValue = 1;
        }
    }

    private LinkedList<Exams> getExamToPerformInDB(Date currentDate){

        Cursor cursorexams = getPerformExam(exam_type, muscle, AlsrmSchema.PENDING, this);
        LinkedList<Exams> list_exams = new LinkedList<>();

        if(cursorexams.moveToFirst()){
            do{
                Exams exam = new Exams();

                exam.setExamId(cursorexams.getInt(cursorexams.getColumnIndex(AlsrmSchema.id)));
                exam.setExamType(cursorexams.getString(cursorexams.getColumnIndex(AlsrmSchema.exam_type)));
                exam.setMuscleId(cursorexams.getInt(cursorexams.getColumnIndex(AlsrmSchema.exam_muscle)));
                exam.setExamState(cursorexams.getString(cursorexams.getColumnIndex(AlsrmSchema.exam_state)));
                exam.setExamStringInitialDate(cursorexams.getString(cursorexams.getColumnIndex(AlsrmSchema.exam_initialDate)));
                exam.setExamStringEndDate(cursorexams.getString(cursorexams.getColumnIndex(AlsrmSchema.exam_endDate)));
                exam.setUserId(cursorexams.getInt(cursorexams.getColumnIndex(AlsrmSchema.exam_user_id)));

                if(currentDate.after(exam.getExamInitialDate()) && currentDate.before(exam.getExamEndDate()) )
                    list_exams.add(exam);

            }while (cursorexams.moveToNext());
        }

        return list_exams;
    }

    private void updateElapsedTime(){

        if(elapsedMillis >= (examToPerform.getTime()*60000)) {
            stop = true;
            DateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            Date examEndDate = new Date();

            ContentValues values = new ContentValues();
            values.put(AlsrmSchema.examStep_state, AlsrmSchema.COMPLETED);
            values.put(AlsrmSchema.exam_endDate, dateformat.format(examEndDate));
            queryHandler.startUpdate(1, null, AlsrmContract.ExamSteps.CONTENT_URI, values,
                    AlsrmSchema.exam_id + " = ? AND " + AlsrmSchema.examStep_num + " = ? ", new String[]{""+examToPerform.getExamId(), ""+examToPerform.getStepNum()});

            sendMessageCompleted();
        }

        ContentValues values = new ContentValues();
        values.put(AlsrmSchema.examStep_elapsed_time, elapsedMillis);
        queryHandler.startUpdate(1, null, AlsrmContract.ExamSteps.CONTENT_URI, values,
                AlsrmSchema.exam_id + " = ? AND " + AlsrmSchema.examStep_num + " = ? ", new String[]{""+examToPerform.getExamId(), ""+examToPerform.getStepNum()});
    }

    private void insertContentProvider(double[]data){

        float yValue = 0f;
        int[] analogChannels = bitalino.getAnalogChannels();

        if (analogChannels[0] == 0) yValue = (float) data[0];
        if (analogChannels[0] == 1) yValue = (float) data[1];
        if (analogChannels[0] == 2) yValue = (float) data[2];
        if (analogChannels[0] == 3) yValue = (float) data[3];

        ContentValues values = getContentValuesPoint(examToPerform.getExamId(), examToPerform.getStepNum(), xValue, yValue);
        queryHandler.startInsert(1, null, AlsrmContract.Points.CONTENT_URI, values);
    }

    private void sendMessage(double[]data, int x, ExamSteps examToPerform){

        Intent intentResult = new Intent();
        int[] analogChannels = bitalino.getAnalogChannels();

        setAction(intentResult, analogChannels);

        intentResult.putExtra(DATA_KEY_OUT, data);
        intentResult.putExtra(X_KEY_OUT, x);
        if(examToPerform != null) {
            intentResult.putExtra(EXAM_ID, examToPerform.getExamId());
            intentResult.putExtra(EXAM_STEP_NUM, examToPerform.getStepNum());
            intentResult.putExtra(TIME_KEY_OUT, elapsedMillis);
        }

        sendBroadcast(intentResult);
    }

    private void sendMessageLostCommunication(){

        Intent intentResult = new Intent();
        int[] analogChannels = bitalino.getAnalogChannels();

        setAction(intentResult, analogChannels);

        intentResult.putExtra(LOST_COMMUNICATION, true);
        sendBroadcast(intentResult);
    }

    private void sendMessageCompleted(){

        Intent intentResult = new Intent();
        int[] analogChannels = bitalino.getAnalogChannels();

        setAction(intentResult, analogChannels);

        intentResult.putExtra(EXAM_COMPLETED, true);
        sendBroadcast(intentResult);
    }

    private void setAction(Intent intentResult, int[] analogChannels){

        if (analogChannels[0] == 0 && muscle == AlsrmSchema.AnteriorTibialis)
            intentResult.setAction(ACTION_IntentService_EMG_AT);
        if (analogChannels[0] == 0 && muscle == AlsrmSchema.FlexorCarpiRadialis)
            intentResult.setAction(ACTION_IntentService_EMG_FCR);
        if (analogChannels[0] == 0 && muscle == AlsrmSchema.SternocleidoMastoideus)
            intentResult.setAction(ACTION_IntentService_EMG_SCM);

        if (analogChannels[0] == 1) intentResult.setAction(ACTION_IntentService_ECG);

        if (analogChannels[0] == 2 && muscle == AlsrmSchema.AnteriorTibialis)
            intentResult.setAction(ACTION_IntentService_SPO2_AT);
        if (analogChannels[0] == 2 && muscle == AlsrmSchema.FlexorCarpiRadialis)
            intentResult.setAction(ACTION_IntentService_SPO2_FCR);
        if (analogChannels[0] == 2 && muscle == AlsrmSchema.SternocleidoMastoideus)
            intentResult.setAction(ACTION_IntentService_SPO2_SCM);

        if (analogChannels[0] == 3) intentResult.setAction(ACTION_IntentService_EEG);

        intentResult.addCategory(Intent.CATEGORY_DEFAULT);
    }

    private double[] dataConverter(BITalinoFrame frame){

        double [] data = new double [6];
        int[] analogChannels = bitalino.getAnalogChannels();

        if (analogChannels[0] == 0) data[0] = SensorDataConverter.scaleEMG(0, frame.getAnalog(0));
        if (analogChannels[0] == 1) data[1] = SensorDataConverter.scaleECG(1, frame.getAnalog(1));
        if (analogChannels[0] == 2) data[2] = SensorDataConverter.scaleEDA(2, frame.getAnalog(2));
        if (analogChannels[0] == 3) data[3] = SensorDataConverter.scaleEEG(3, frame.getAnalog(3));

        return data;
    }

    private ExamSteps getExamStepToPerformDB(LinkedList<Exams> list_exams, String description) {
        for(Exams exam : list_exams) {

            Cursor cursorExamSteps = Operations.getExamSteps(exam.getExamId(), description, AlsrmSchema.PENDING, this);
            if (cursorExamSteps.moveToFirst()) {

                ExamSteps examSteps  = new ExamSteps();

                examSteps.setExamId(cursorExamSteps.getInt(cursorExamSteps.getColumnIndex(AlsrmSchema.exam_id)));
                examSteps.setStepNum(cursorExamSteps.getInt(cursorExamSteps.getColumnIndex(AlsrmSchema.examStep_num)));
                examSteps.setDescription(cursorExamSteps.getString(cursorExamSteps.getColumnIndex(AlsrmSchema.examStep_description)));
                examSteps.setState(cursorExamSteps.getString(cursorExamSteps.getColumnIndex(AlsrmSchema.examStep_state)));
                examSteps.setTime(cursorExamSteps.getInt(cursorExamSteps.getColumnIndex(AlsrmSchema.examStep_time)));
                examSteps.setElapsedTime(cursorExamSteps.getInt(cursorExamSteps.getColumnIndex(AlsrmSchema.examStep_elapsed_time)));
                examSteps.setStringInitialDate(cursorExamSteps.getString(cursorExamSteps.getColumnIndex(AlsrmSchema.exam_initialDate)));
                examSteps.setStringEndDate(cursorExamSteps.getString(cursorExamSteps.getColumnIndex(AlsrmSchema.exam_endDate)));

                return examSteps;
            }
        }
        return null;
    }

    private String stateOfDay(Date currentDate){

        String stateOfDay = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);

        if(hours>=0 && hours<6){
            stateOfDay = AlsrmSchema.NIGHT;
        }
        if(hours>=6 && hours<12){
            stateOfDay = AlsrmSchema.MORNING;
        }
        if(hours>=12 && hours<20){
            stateOfDay = AlsrmSchema.AFTERNOON;
        }
        if(hours>=20 && hours<=24){
            stateOfDay = AlsrmSchema.NIGHT;
        }
        return stateOfDay;
    }

    public void stopBitalino(){
        try {
            bitalino.stop();
        } catch (BITalinoException ex) {
            ex.printStackTrace();
        }
    }

    public void cancel() {
        try {
            btSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

