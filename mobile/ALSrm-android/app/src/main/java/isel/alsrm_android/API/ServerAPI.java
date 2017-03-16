package isel.alsrm_android.API;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public  interface ServerAPI {

        String BASE_URL = "http://alsrmwebapi.azurewebsites.net"; // MP: alterado a 16-03-2017 "http://alsrm.azurewebsites.net/";//400

        @FormUrlEncoded
        @POST("odata/security/token")
        Call<Token> getToken(@Field("grant_type") String grant_type, @Field("username") String username, @Field("password") String password);

        @GET("odata/Users({id})")
        Call<ResponseFromServer> getUserExams(@Path("id") int id, @Header("Authorization") String authorization, @Query("$expand") String expand);

        @GET("odata/Users({id})")
        Call<ResponseFromServer> getUser(@Path("id") int id, @Header("Authorization") String authorization);


        @POST("odata/Exams({ExamsId})/ExamSteps({ExamStepsId})/ExamStepPoints")
        Call<ResponseBody> postPoints(@Path("ExamsId") int ExamsId, @Path("ExamStepsId") int ExamStepsId, @Body PointsToSend pointsToSend, @Header("Authorization") String authorization);
}
