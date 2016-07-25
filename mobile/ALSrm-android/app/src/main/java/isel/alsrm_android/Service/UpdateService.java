package isel.alsrm_android.Service;

import android.app.IntentService;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import isel.alsrm_android.API.ExamSteps;
import isel.alsrm_android.API.Exams;
import isel.alsrm_android.API.ServerAPI;
import isel.alsrm_android.API.Token;
import isel.alsrm_android.API.ResponseFromServer;
import isel.alsrm_android.Activity.MainActivity;
import isel.alsrm_android.Database.AlsrmContract;
import isel.alsrm_android.Database.AlsrmSchema;
import isel.alsrm_android.Fragment.SettingsFragment;
import isel.alsrm_android.Receiver.NotificationUpdate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static isel.alsrm_android.Database.Operations.getContentValuesExam;
import static isel.alsrm_android.Database.Operations.getContentValuesExamSteps;
import static isel.alsrm_android.Database.Operations.getExam;
import static isel.alsrm_android.Database.Operations.getExamSteps;
import static isel.alsrm_android.Database.Operations.insertExam;

public class UpdateService extends IntentService {

    private static class AlsrmAsyncQueryHandler extends AsyncQueryHandler {

        public AlsrmAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }
    }

    private static class StatusExam {

        public Boolean isItInDB;
        public String state;
    }

    private AlsrmAsyncQueryHandler queryHandler;
    private List<Exams> exams = new LinkedList<>();
    private boolean showMessage;
    private String userId;
    private String password;

    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        queryHandler = new AlsrmAsyncQueryHandler(this.getContentResolver());
        showMessage = true;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = sharedPreferences.getString(MainActivity.USER_ID, null);
        password = sharedPreferences.getString(MainActivity.PASSWORD, null);

        if(userId != null && password != null)
            getTokenApiRequestAndData();
    }

    private StatusExam thereIsThisExam(int examId){

        Cursor cursor = getExam(examId, getBaseContext());

        if(cursor.moveToFirst()){

            StatusExam statusExam = new StatusExam();
            statusExam.isItInDB = true;
            statusExam.state = (cursor.getString(cursor.getColumnIndex(AlsrmSchema.exam_state)));
            return statusExam;
        }
        else{
            StatusExam statusExam = new StatusExam();
            statusExam.isItInDB = false;
            return statusExam;
        }
    }

    private StatusExam thereIsThisExamSteps(int examId,  int examStep_num){

        Cursor cursor = getExamSteps(examId, examStep_num, getBaseContext());

        if (cursor.moveToFirst()) {

            StatusExam statusExam = new StatusExam();
            statusExam.isItInDB = true;
            statusExam.state = (cursor.getString(cursor.getColumnIndex(AlsrmSchema.examStep_state)));
            return statusExam;
        }
        else{
            StatusExam statusExam = new StatusExam();
            statusExam.isItInDB = false;
            return statusExam;
        }
    }

    private void getDataFromServer(String authorization, String userId){

        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Service setup
        ServerAPI service = retrofit.create(ServerAPI.class);

        // Prepare the HTTP request
        Call<ResponseFromServer> call = service.getUserExams(Integer.parseInt(userId), "Bearer "+authorization, "Exams/ExamSteps, Exams/ThisMuscle");

        // Asynchronously execute HTTP request
        call.enqueue(new Callback<ResponseFromServer>() {

            /**
             * onResponse is called when any kind of response has been received.
             */
            @Override
            public void onResponse(Call<ResponseFromServer> call, Response<ResponseFromServer> response) {
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
                ResponseFromServer decodedResponse = response.body();
                if (decodedResponse == null) return;

                // at this point the JSON body has been successfully parsed
                exams = decodedResponse.value.get(0).Exams;

                for(Exams exam : exams) {

                    StatusExam statusExam = thereIsThisExam(exam.getExamId());
                    if(statusExam.isItInDB){

                        ContentValues examValues;
                        if(statusExam.state.equals(AlsrmSchema.COMPLETED)) {
                            examValues = getContentValuesExam(exam.getExamId(), exam.getExamType(), exam.getMuscleId(),
                                    exam.getUserId(), exam.getExamInitialDateString(), exam.getExamEndDateString());
                        }
                        else{
                            examValues = getContentValuesExam(exam.getExamId(), exam.getExamType(), exam.getMuscleId(),
                                    exam.getExamState(), exam.getUserId(), exam.getExamInitialDateString(), exam.getExamEndDateString());
                        }

                        queryHandler.startUpdate(1, null, AlsrmContract.Exams.CONTENT_URI,
                                examValues, AlsrmSchema.id + " = ? ", new String[]{"" + exam.getExamId()});
                    }
                    else {
                        insertExam(getBaseContext(), exam.getExamId(), exam.getExamType(), exam.getMuscleId(),
                                exam.getExamState(), exam.getUserId(), exam.getExamInitialDateString(), exam.getExamEndDateString());

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        boolean notification = sharedPreferences.getBoolean(SettingsFragment.NOTIFICATION, true);

                        if(showMessage && notification) {
                            showMessage = false;
                            Intent i = new Intent(getBaseContext(), NotificationUpdate.class);
                            getBaseContext().sendBroadcast(i);
                        }
                    }

                    for(ExamSteps examSteps : exam.getExamSteps()) {

                        statusExam = thereIsThisExamSteps(examSteps.getExamId(), examSteps.getStepNum());
                        if(statusExam.isItInDB) {

                            if (statusExam.state.equals(AlsrmSchema.COMPLETED)) {

                                ContentValues examStepsValues = getContentValuesExamSteps(examSteps.getExamId(), examSteps.getStepNum(),
                                        examSteps.getDescription(), examSteps.getTime(), 0);

                                queryHandler.startUpdate(1, null, AlsrmContract.ExamSteps.CONTENT_URI, examStepsValues,
                                        AlsrmSchema.exam_id + " = ? AND " + AlsrmSchema.examStep_num + " = ? ", new String[]{"" + examSteps.getExamId(), "" + examSteps.getStepNum()});
                            } else {

                                ContentValues examStepsValues = getContentValuesExamSteps(examSteps.getExamId(), examSteps.getStepNum(),
                                        examSteps.getDescription(), examSteps.getState(), examSteps.getTime(), 0);

                                queryHandler.startUpdate(1, null, AlsrmContract.ExamSteps.CONTENT_URI, examStepsValues,
                                        AlsrmSchema.exam_id + " = ? AND " + AlsrmSchema.examStep_num + " = ? ", new String[]{"" + examSteps.getExamId(), "" + examSteps.getStepNum()});
                            }
                        }

                        else {
                            ContentValues examStepsValues = getContentValuesExamSteps(examSteps.getExamId(), examSteps.getStepNum(),
                                    examSteps.getDescription(), examSteps.getState(), examSteps.getTime(), 0);
                            queryHandler.startInsert(1, null, AlsrmContract.ExamSteps.CONTENT_URI, examStepsValues);

                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                            boolean notification = sharedPreferences.getBoolean(SettingsFragment.NOTIFICATION, true);

                            if(showMessage && notification) {
                                showMessage = false;
                                Intent i = new Intent(getBaseContext(), NotificationUpdate.class);
                                getBaseContext().sendBroadcast(i);
                            }
                        }
                    }
                }
            }

            /**
             * onFailure gets called when the HTTP request didn't get through.
             * For instance if the URL is invalid / host not reachable
             */
            @Override
            public void onFailure(Call<ResponseFromServer> call, Throwable t) {
                System.out.println("onFailure");
                System.out.println(t.getMessage());

            }
        });
    }

    private void getTokenApiRequestAndData() {
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

                getDataFromServer(decodedResponse.access_token, userId);
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
}