package isel.alsrm_android.Service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import isel.alsrm_android.API.Exams;
import isel.alsrm_android.Activity.MainActivity;
import isel.alsrm_android.Database.AlsrmSchema;
import isel.alsrm_android.Database.Operations;
import isel.alsrm_android.Fragment.SettingsFragment;
import isel.alsrm_android.Receiver.NotificationDeadline;
import isel.alsrm_android.Utils.Utils;

public class DeadlineService extends IntentService {

    public DeadlineService() {
        super("DeadlineService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Cursor cursor = Operations.getAllExamState("pending", this);
        LinkedList<Exams> list_exams = new LinkedList<>();

        if(cursor.moveToFirst()){
            do{

                Exams exam = new Exams();
                exam.setExamId(cursor.getInt(cursor.getColumnIndex(AlsrmSchema.id)));
                exam.setExamType(cursor.getString(cursor.getColumnIndex(AlsrmSchema.exam_type)));
                exam.setMuscleId(cursor.getInt(cursor.getColumnIndex(AlsrmSchema.exam_muscle)));
                exam.setExamState(cursor.getString(cursor.getColumnIndex(AlsrmSchema.exam_state)));
                exam.setExamStringInitialDate(cursor.getString(cursor.getColumnIndex(AlsrmSchema.exam_initialDate)));
                exam.setExamStringEndDate(cursor.getString(cursor.getColumnIndex(AlsrmSchema.exam_endDate)));
                exam.setUserId(cursor.getInt(cursor.getColumnIndex(AlsrmSchema.exam_user_id)));

                list_exams.add(exam);
            }while (cursor.moveToNext());
        }

        Date currentDate = new Date();
        Calendar calCurrent = Calendar.getInstance();
        calCurrent.setTime(currentDate);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean notification = sharedPreferences.getBoolean(SettingsFragment.NOTIFICATION, true);

        for(Exams exam : list_exams) {

            Calendar calEndDate = Calendar.getInstance();
            calEndDate.setTime(exam.getExamEndDate());

            if(calCurrent.get(Calendar.YEAR) == calEndDate.get(Calendar.YEAR) &&
                    calCurrent.get(Calendar.MONTH) == calEndDate.get(Calendar.MONTH) &&
                    calCurrent.get(Calendar.DAY_OF_MONTH) == calEndDate.get(Calendar.DAY_OF_MONTH)){

                if(notification) {
                    Intent i = new Intent(getBaseContext(), NotificationDeadline.class);
                    i.putExtra(MainActivity.EXAM, exam.getExamType());
                    i.putExtra(MainActivity.MUSCLE, Utils.muscleAbbreviation(exam.getMuscleId()));
                    getBaseContext().sendBroadcast(i);
                }
            }
        }
    }
}
