package isel.alsrm_android.Service;

import android.app.IntentService;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;


import java.io.IOException;
import java.util.LinkedList;


import isel.alsrm_android.API.ExamSteps;
import isel.alsrm_android.API.Exams;
import isel.alsrm_android.API.Point;
import isel.alsrm_android.API.PointsToSend;
import isel.alsrm_android.API.ResponseBody;
import isel.alsrm_android.API.ServerAPI;
import isel.alsrm_android.API.Token;
import isel.alsrm_android.Activity.MainActivity;
import isel.alsrm_android.Database.AlsrmContract;
import isel.alsrm_android.Database.AlsrmSchema;
import isel.alsrm_android.Fragment.SettingsFragment;
import isel.alsrm_android.Receiver.NetworkChangeReceiver;
import isel.alsrm_android.Receiver.NotificationReceiver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static isel.alsrm_android.Database.Operations.getAllExamSteps;
import static isel.alsrm_android.Database.Operations.getAllExams;
import static isel.alsrm_android.Database.Operations.getPoints;

public class UploadService extends IntentService {

    private static class AlsrmAsyncQueryHandler extends AsyncQueryHandler {

        public AlsrmAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }
    }

    private AlsrmAsyncQueryHandler queryHandler;
    private boolean showMessage;
    private String userId;
    private String password;

    public UploadService() {
        super("UploadIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        queryHandler = new AlsrmAsyncQueryHandler(this.getContentResolver());
        showMessage = true;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = sharedPreferences.getString(MainActivity.USER_ID, null);
        password = sharedPreferences.getString(MainActivity.PASSWORD, null);

        if(userId != null && password != null)
            getExamsSendToTheServer();

        NetworkChangeReceiver.completeWakefulIntent(intent);
    }

    private void getExamsSendToTheServer(){
        Cursor cursor = getAllExams(this);
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

        for(Exams exam : list_exams) {

            Cursor cursorExamSteps = getAllExamSteps(exam.getExamId(), this);
            LinkedList<ExamSteps> list_ExamSteps = new LinkedList<>();
            if (cursorExamSteps.moveToFirst()) {
                do {

                    ExamSteps examSteps  = new ExamSteps();
                    examSteps.setExamId(cursorExamSteps.getInt(cursorExamSteps.getColumnIndex(AlsrmSchema.exam_id)));
                    examSteps.setStepNum(cursorExamSteps.getInt(cursorExamSteps.getColumnIndex(AlsrmSchema.examStep_num)));
                    examSteps.setDescription(cursorExamSteps.getString(cursorExamSteps.getColumnIndex(AlsrmSchema.examStep_description)));
                    examSteps.setState(cursorExamSteps.getString(cursorExamSteps.getColumnIndex(AlsrmSchema.examStep_state)));
                    examSteps.setTime(cursorExamSteps.getInt(cursorExamSteps.getColumnIndex(AlsrmSchema.examStep_time)));
                    examSteps.setElapsedTime(cursorExamSteps.getInt(cursorExamSteps.getColumnIndex(AlsrmSchema.examStep_elapsed_time)));
                    examSteps.setStringInitialDate(cursorExamSteps.getString(cursorExamSteps.getColumnIndex(AlsrmSchema.exam_initialDate)));
                    examSteps.setStringEndDate(cursorExamSteps.getString(cursorExamSteps.getColumnIndex(AlsrmSchema.exam_endDate)));

                    list_ExamSteps.add(examSteps);

                } while (cursorExamSteps.moveToNext());
            }

            exam.setExamSteps(list_ExamSteps);
        }


        LinkedList<PointsToSend> pointsToSend = new LinkedList<>();

        for(Exams exam : list_exams) {

            for(ExamSteps examStep : exam.getExamSteps()) {

                Cursor cursorPoints = getPoints(examStep.getExamId(), examStep.getStepNum(), this);

                LinkedList<Point> points = new LinkedList<>();

                if (cursorPoints.moveToFirst()) {
                    do {

                        Point point = new Point();
                        point.setExamId(cursorPoints.getInt(cursorPoints.getColumnIndex(AlsrmSchema.exam_id)));
                        point.setExamStepNum(cursorPoints.getInt(cursorPoints.getColumnIndex(AlsrmSchema.examstepnum)));
                        point.setX(cursorPoints.getInt(cursorPoints.getColumnIndex(AlsrmSchema.x_data)));
                        point.setY(cursorPoints.getFloat(cursorPoints.getColumnIndex(AlsrmSchema.y_data)));

                        points.add(point);
                    } while (cursorPoints.moveToNext());
                }

                if(!points.isEmpty()){
                    PointsToSend send = new PointsToSend(examStep.getExamId(), examStep.getStepNum(),
                            examStep.getInitialDate(), examStep.getEndDate(), points);
                    pointsToSend.add(send);
                }
            }
        }

        if(!pointsToSend.isEmpty()) {
            for(PointsToSend exam : pointsToSend) {
                getTokenApiRequestAndSend(exam);

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean notification = sharedPreferences.getBoolean(SettingsFragment.NOTIFICATION, true);

                if(showMessage && notification) {
                    showMessage = false;
                    Intent i = new Intent(getBaseContext(), NotificationReceiver.class);
                    getBaseContext().sendBroadcast(i);
                }
            }
        }
    }

    private void getTokenApiRequestAndSend(final PointsToSend exam) {
        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Service setup
        ServerAPI service = retrofit.create(ServerAPI.class);

        // Prepare the HTTP request
        Call<Token> call = service.getToken("password", userId, password);

        // Asynchronously execute HTTP request
        call.enqueue(new Callback<Token>() {

            /**
             * onResponse is called when any kind of response has been received.
             */
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                // http response status code + headers
                System.out.println("Response status code: " + response.code());

                // isSuccess is true if response code => 200 and <= 300
                if (!response.isSuccessful()) {
                    // print response body if unsuccessful
                    try {
                        System.out.println(response.errorBody().string());
                    } catch (IOException e) {
                        // do nothing
                    }
                    return;
                }

                // if parsing the JSON body failed, `response.body()` returns null
                Token decodedResponse = response.body();
                if (decodedResponse == null) return;

                // at this point the JSON body has been successfully parsed

                PostDataInServer(exam, "Bearer "+decodedResponse.access_token);
            }

            /**
             * onFailure gets called when the HTTP request didn't get through.
             * For instance if the URL is invalid / host not reachable
             */
            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                System.out.println("onFailure");
                System.out.println(t.getMessage());
            }
        });
    }

    private void PostDataInServer(final PointsToSend exam, String authorization){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerAPI service = retrofit.create(ServerAPI.class);

        // Prepare the HTTP request
        Call<ResponseBody> call = service.postPoints(exam.getExamId(), exam.getStepNum(), exam, authorization);

        // Asynchronously execute HTTP request
        call.enqueue(new Callback<ResponseBody>() {
            /**
             * onResponse is called when any kind of response has been received.
             */
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // http response status code + headers
                System.out.println("Response status code: " + response.code());

                // isSuccess is true if response code => 200 and <= 300
                if (!response.isSuccessful()) {
                    // print response body if unsuccessful
                    try {
                        System.out.println(response.errorBody().string());
                    } catch (IOException e) {
                        // do nothing
                    }
                    return;
                }

                queryHandler.startDelete(1, null, AlsrmContract.Points.CONTENT_URI, AlsrmSchema.exam_id + " = ? AND "+AlsrmSchema.examstepnum + " = ? ",
                        new String[]{""+exam.getExamId(), ""+exam.getStepNum()});
            }

            /**
             * onFailure gets called when the HTTP request didn't get through.
             * For instance if the URL is invalid / host not reachable
             */
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("onFailure");
                System.out.println(t.getMessage());
            }
        });
    }
}