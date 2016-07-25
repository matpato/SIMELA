package isel.alsrm_android.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class Operations {

    public static ContentValues getContentValuesExam (int id, String exam_type, int exam_muscle, String exam_state, int user_id, String exam_initialDate, String exam_endDate){

        ContentValues values = new ContentValues();

        values.put(AlsrmSchema.id, id);
        values.put(AlsrmSchema.exam_type, exam_type);
        values.put(AlsrmSchema.exam_muscle, exam_muscle);
        values.put(AlsrmSchema.exam_state, exam_state);
        values.put(AlsrmSchema.exam_user_id, user_id);
        values.put(AlsrmSchema.exam_initialDate, exam_initialDate);
        values.put(AlsrmSchema.exam_endDate, exam_endDate);
        return values;
    }

    public static ContentValues getContentValuesExam (int id, String exam_type, int exam_muscle, int user_id, String exam_initialDate, String exam_endDate){

        ContentValues values = new ContentValues();

        values.put(AlsrmSchema.id, id);
        values.put(AlsrmSchema.exam_type, exam_type);
        values.put(AlsrmSchema.exam_muscle, exam_muscle);
        values.put(AlsrmSchema.exam_user_id, user_id);
        values.put(AlsrmSchema.exam_initialDate, exam_initialDate);
        values.put(AlsrmSchema.exam_endDate, exam_endDate);
        return values;
    }

    public static String[] examProjection = new String[]{AlsrmSchema.id, AlsrmSchema.exam_type, AlsrmSchema.exam_muscle,
            AlsrmSchema.exam_state, AlsrmSchema.exam_user_id, AlsrmSchema.exam_initialDate, AlsrmSchema.exam_endDate};

    public static ContentValues getContentValuesExamSteps (int exam_id, int examStep_num, String examStep_description, String examStep_state, int examStep_time, int examStep_elapsed_time){

        ContentValues values = new ContentValues();

        values.put(AlsrmSchema.exam_id, exam_id);
        values.put(AlsrmSchema.examStep_num, examStep_num);
        values.put(AlsrmSchema.examStep_description, examStep_description);
        values.put(AlsrmSchema.examStep_state, examStep_state);
        values.put(AlsrmSchema.examStep_time, examStep_time);
        values.put(AlsrmSchema.examStep_elapsed_time, examStep_elapsed_time);

        return values;
    }

    public static ContentValues getContentValuesExamSteps (int exam_id, int examStep_num, String examStep_description, int examStep_time, int examStep_elapsed_time){

        ContentValues values = new ContentValues();

        values.put(AlsrmSchema.exam_id, exam_id);
        values.put(AlsrmSchema.examStep_num, examStep_num);
        values.put(AlsrmSchema.examStep_description, examStep_description);
        values.put(AlsrmSchema.examStep_time, examStep_time);
        values.put(AlsrmSchema.examStep_elapsed_time, examStep_elapsed_time);

        return values;
    }

    public static String[] ExamStepsProjection = new String[]{AlsrmSchema.exam_id, AlsrmSchema.examStep_num, AlsrmSchema.examStep_description,
            AlsrmSchema.examStep_state, AlsrmSchema.examStep_time, AlsrmSchema.examStep_elapsed_time, AlsrmSchema.exam_initialDate, AlsrmSchema.exam_endDate};

    public static ContentValues getContentValuesPoint (int exam_id, int examstepnum, int x, float y){

        ContentValues values = new ContentValues();

        values.put(AlsrmSchema.exam_id, exam_id);
        values.put(AlsrmSchema.examstepnum, examstepnum);
        values.put(AlsrmSchema.x_data, x);
        values.put(AlsrmSchema.y_data, y);
        return values;
    }

    public static ContentValues getContentValuesUpdatePoint (float y){

        ContentValues values = new ContentValues();
        values.put(AlsrmSchema.y_data, y);
        return values;
    }

    public static String[] pointProjection = new String[]{AlsrmSchema.exam_id, AlsrmSchema.examstepnum, AlsrmSchema.x_data, AlsrmSchema.y_data};

    //////////////////////////////////////////////////////////////////////

    public static Uri insertExam(Context context, int id, String exam_type, int exam_muscle, String exam_state, int user_id, String exam_initialDate, String exam_endDate) {

        ContentValues values = getContentValuesExam (id, exam_type, exam_muscle, exam_state, user_id, exam_initialDate, exam_endDate);

        return context.getContentResolver().insert(AlsrmContract.Exams.CONTENT_URI, values);
    }

    public static Cursor getExam(int id, Context context) {

        return context.getContentResolver().query(AlsrmContract.Exams.CONTENT_URI, examProjection, AlsrmSchema.id + " = ? ", new String[]{"" + id}, null);
    }

    public static Cursor getAllExamState(String exam_state, Context context) {

        return context.getContentResolver().query(AlsrmContract.Exams.CONTENT_URI, examProjection, AlsrmSchema.exam_state + " = ? ", new String[]{exam_state}, null);
    }

    public static Cursor getPerformExam(String exam_type, int exam_muscle, String exam_state, Context context) {

        return context.getContentResolver().query(AlsrmContract.Exams.CONTENT_URI, examProjection, AlsrmSchema.exam_type+ " = ? AND " + AlsrmSchema.exam_state + " = ? AND " + AlsrmSchema.exam_muscle + " = ? ", new String[]{exam_type, exam_state, ""+exam_muscle}, null);
    }

    public static Cursor getAllExams(Context context) {

        return context.getContentResolver().query(AlsrmContract.Exams.CONTENT_URI, examProjection, null, null, null);
    }

    public static int updateExam(Context context, int id, String exam_type, int exam_muscle, String exam_state, int user_id, String exam_initialDate, String exam_endDate) {

        ContentValues values = getContentValuesExam (id, exam_type, exam_muscle, exam_state, user_id, exam_initialDate, exam_endDate);

        return context.getContentResolver().update(AlsrmContract.Exams.CONTENT_URI, values, AlsrmSchema.id + " = ? ", new String[]{"" + id});
    }

    public static int deleteExam(int id, Context context) {
        return context.getContentResolver().delete(AlsrmContract.Exams.CONTENT_URI,  AlsrmSchema.id + " = ? ", new String[]{"" + id});
    }

    ///////////////////////////////////////////////////////////////////////////////

    public static Uri ExamStepsExam(Context context, int exam_id, int examStep_num, String examStep_description, String examStep_state, int examStep_time, int examStep_elapsed_time) {

        ContentValues values = getContentValuesExamSteps( exam_id, examStep_num, examStep_description, examStep_state, examStep_time, examStep_elapsed_time);

        return context.getContentResolver().insert(AlsrmContract.ExamSteps.CONTENT_URI, values);
    }

    public static Cursor getExamSteps(int exam_id, int examStep_num, Context context) {

        return context.getContentResolver().query(AlsrmContract.ExamSteps.CONTENT_URI, ExamStepsProjection, AlsrmSchema.exam_id + " = ? AND " + AlsrmSchema.examStep_num + " = ? ", new String[]{""+exam_id, ""+examStep_num}, null);
    }

    public static Cursor getAllExamSteps(int exam_id, Context context) {

        return context.getContentResolver().query(AlsrmContract.ExamSteps.CONTENT_URI, ExamStepsProjection, AlsrmSchema.exam_id + " = ? ", new String[]{""+exam_id,}, null);
    }

    public static Cursor getExamSteps(int exam_id, String description, String state, Context context) {

        return context.getContentResolver().query(AlsrmContract.ExamSteps.CONTENT_URI, ExamStepsProjection, AlsrmSchema.exam_id + " = ? AND " + AlsrmSchema.examStep_description  + " = ? AND " + AlsrmSchema.examStep_state + " = ? ", new String[]{""+exam_id, description, state }, null);
    }

    public static int updateExamSteps(Context context, int exam_id, int examStep_num, String examStep_description, String examStep_state, int examStep_time, int examStep_elapsed_time) {

        ContentValues values = getContentValuesExamSteps( exam_id, examStep_num, examStep_description, examStep_state, examStep_time, examStep_elapsed_time);

        return context.getContentResolver().update(AlsrmContract.ExamSteps.CONTENT_URI, values, AlsrmSchema.exam_id + " = ? AND " + AlsrmSchema.examStep_num + " = ? ", new String[]{""+exam_id, ""+examStep_num});
    }

    public static int deleteExamSteps(int exam_id, Context context) {
        return context.getContentResolver().delete(AlsrmContract.ExamSteps.CONTENT_URI,  AlsrmSchema.exam_id + " = ? ", new String[]{"" + exam_id});
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Uri insertPoint(Context context, int exam_id, int examstepnum, int x, float y) {

        ContentValues values = getContentValuesPoint(exam_id, examstepnum, x, y);

        return context.getContentResolver().insert(AlsrmContract.Points.CONTENT_URI, values);
    }

    public static Cursor getPoints(int exam_id, int examstepnum, Context context) {

        return context.getContentResolver().query(AlsrmContract.Points.CONTENT_URI, pointProjection, AlsrmSchema.exam_id + " = ? AND "+AlsrmSchema.examstepnum + " = ? ", new String[]{""+exam_id, ""+examstepnum}, null);
    }

    public static Cursor getMaxPoint(int exam_id, int examstepnum, Context context) {

        return context.getContentResolver().query(AlsrmContract.Points.CONTENT_URI, new String[] {"MAX("+AlsrmSchema.x_data+") as " +AlsrmSchema.x_data}, AlsrmSchema.exam_id + " = ? AND "+AlsrmSchema.examstepnum + " = ? ", new String[]{""+exam_id, ""+examstepnum}, null);
    }

    public static int updatePoint(Context context, int exam_id, int examstepnum, int x, float y) {

        ContentValues values = getContentValuesUpdatePoint(y);
        return context.getContentResolver().update(AlsrmContract.Points.CONTENT_URI, values, AlsrmSchema.exam_id + " = ? AND "+AlsrmSchema.examstepnum + " = ? AND" + AlsrmSchema.x_data + " = ? ", new String[]{""+exam_id, ""+examstepnum, "" + x} );
    }

    public static int deletePoint(int exam_id, int examstepnum, int x, Context context) {
        return context.getContentResolver().delete(AlsrmContract.Points.CONTENT_URI, AlsrmSchema.exam_id + " = ? AND "+AlsrmSchema.examstepnum + " = ? AND" + AlsrmSchema.x_data + " = ? ", new String[]{""+exam_id, ""+examstepnum, "" + x} );
    }

    public static int deletePoints(int exam_id, int examstepnum, Context context) {
        return context.getContentResolver().delete(AlsrmContract.Points.CONTENT_URI, AlsrmSchema.exam_id + " = ? AND "+AlsrmSchema.examstepnum + " = ? ", new String[]{""+exam_id, ""+examstepnum} );
    }
}
