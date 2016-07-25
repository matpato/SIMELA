package isel.alsrm_android.PopUpDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import isel.alsrm_android.API.ExamSteps;
import isel.alsrm_android.API.Exams;
import isel.alsrm_android.Activity.ECG_Activity;
import isel.alsrm_android.Activity.EMG_Activity;
import isel.alsrm_android.Activity.MainActivity;
import isel.alsrm_android.Activity.SPO2_Activity;
import isel.alsrm_android.Bluetooth.ManageConnectedSocket;
import isel.alsrm_android.Database.AlsrmContract;
import isel.alsrm_android.Database.AlsrmSchema;
import isel.alsrm_android.Database.Operations;
import isel.alsrm_android.Fragment.ECG_Fragment;
import isel.alsrm_android.Fragment.EMG_Fragment;
import isel.alsrm_android.Fragment.SPO2_Fragment;
import isel.alsrm_android.R;

import static isel.alsrm_android.Database.Operations.getExam;

public class StopDialogFragment extends DialogFragment {

    private AlsrmAsyncQueryHandler queryHandler;

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
                }
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        queryHandler = new AlsrmAsyncQueryHandler(this.getActivity().getContentResolver());
        final Intent intentService = new Intent(getActivity(), ManageConnectedSocket.class);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.stop);
        builder.setMessage(R.string.are_you_sure_stop)
                .setPositiveButton(R.string.stop, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        boolean run = sharedPreferences.getBoolean(ManageConnectedSocket.IS_INTENT_SERVICE_RUNNING, false);
                        int examId = sharedPreferences.getInt(ManageConnectedSocket.EXAM_ID, 0);
                        int examStepNum = sharedPreferences.getInt(ManageConnectedSocket.EXAM_STEP_NUM, 0);

                        if(run) {

                            getActivity().stopService(intentService);
                            if (examId != 0 && examStepNum != 0) {
                                queryHandler.startQuery(1, null, AlsrmContract.ExamSteps.CONTENT_URI, Operations.ExamStepsProjection,
                                        AlsrmSchema.exam_id + " = ? AND " + AlsrmSchema.examStep_num + " = ? ", new String[]{"" + examId, "" + examStepNum}, null);
                            }
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(ManageConnectedSocket.IS_INTENT_SERVICE_RUNNING, false);
                            editor.apply();
                        }
                    }
                })
                .setNegativeButton(R.string.see_exam, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        boolean run = sharedPreferences.getBoolean(ManageConnectedSocket.IS_INTENT_SERVICE_RUNNING, false);
                        int examId = sharedPreferences.getInt(ManageConnectedSocket.EXAM_ID, 0);
                        int examStepNum = sharedPreferences.getInt(ManageConnectedSocket.EXAM_STEP_NUM, 0);

                        if(run) {
                            if (examId != 0 && examStepNum != 0) {
                                Exams exam = getExamDB(examId);
                                goToExam(exam, examId, examStepNum);
                            }
                        }
                    }
                });
        builder.setIcon(R.drawable.stop_xlarge);

        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void goToExam(Exams exam, int examId, int examStepNum){
        Intent intent = null;
        if(exam != null) {
            if (exam.getExamType().equals(EMG_Fragment.EMG))
                intent = new Intent(getActivity(), EMG_Activity.class);

            if (exam.getExamType().equals(ECG_Fragment.ECG))
                intent = new Intent(getActivity(), ECG_Activity.class);

            if (exam.getExamType().equals(SPO2_Fragment.SPO2))
                intent = new Intent(getActivity(), SPO2_Activity.class);
            if (intent != null) {
                intent.putExtra(ManageConnectedSocket.EXAM_ID, examId);
                intent.putExtra(ManageConnectedSocket.EXAM_STEP_NUM, examStepNum);
                intent.putExtra(MainActivity.MUSCLE, exam.getMuscleId());
                startActivity(intent);
            }
        }
    }

    private Exams getExamDB(int examId){

        Cursor cursorExam = getExam(examId, getActivity());
        if(cursorExam.moveToFirst()){

            Exams exam = new Exams();
            exam.setExamId(cursorExam.getInt(cursorExam.getColumnIndex(AlsrmSchema.id)));
            exam.setExamType(cursorExam.getString(cursorExam.getColumnIndex(AlsrmSchema.exam_type)));
            exam.setMuscleId(cursorExam.getInt(cursorExam.getColumnIndex(AlsrmSchema.exam_muscle)));
            exam.setExamState(cursorExam.getString(cursorExam.getColumnIndex(AlsrmSchema.exam_state)));
            exam.setExamStringInitialDate(cursorExam.getString(cursorExam.getColumnIndex(AlsrmSchema.exam_initialDate)));
            exam.setExamStringEndDate(cursorExam.getString(cursorExam.getColumnIndex(AlsrmSchema.exam_endDate)));
            exam.setUserId(cursorExam.getInt(cursorExam.getColumnIndex(AlsrmSchema.exam_user_id)));
            return exam;
        }
        return null;
    }
}