package isel.alsrm_android.PopUpDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import isel.alsrm_android.API.ResponseFromServer;
import isel.alsrm_android.API.ServerAPI;
import isel.alsrm_android.API.Token;
import isel.alsrm_android.Activity.MainActivity;
import isel.alsrm_android.Database.DatabaseHelper;
import isel.alsrm_android.R;
import isel.alsrm_android.Service.UpdateService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LogInDialogFragment extends DialogFragment {

    private View view;
    private Call<Token> callValidate;
    private Call<ResponseFromServer> callMAC;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        view = inflater.inflate(R.layout.dialog_log_in, null);
        builder.setTitle(R.string.authentication);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // cancel
                        if(callValidate != null)
                            callValidate.cancel();
                        if(callMAC != null)
                            callMAC.cancel();
                    }
                });
        builder.setIcon(R.drawable.log_in);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();

        if (dialog != null) {
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    // sign in the user ...

                    EditText editTextId = (EditText) view.findViewById(R.id.usernameId);
                    String userId = editTextId.getText().toString();
                    EditText editTextPassword = (EditText) view.findViewById(R.id.password);
                    String password = editTextPassword.getText().toString();

                    validateUserPassword(userId,password);
                }
            });
        }
    }

    private void validateUserPassword(final String userId, final String password) {
        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Service setup
        ServerAPI service = retrofit.create(ServerAPI.class);

        // Prepare the HTTP request
        callValidate = service.getToken("password", userId, password);

        // Asynchronously execute HTTP request
        callValidate.enqueue(new Callback<Token>() {

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
                        if(view!=null) {
                            TextView invalid = (TextView) view.findViewById(R.id.password_user_incorrect);
                            invalid.setVisibility(View.VISIBLE);
                        }
                    } catch (IOException e) {
                        // do nothing
                    }
                    return;
                }

                // if parsing the JSON body failed, `response.body()` returns null
                Token decodedResponse = response.body();
                if (decodedResponse == null) return;

                if(getActivity() != null) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(MainActivity.USER_ID, userId);
                    editor.putString(MainActivity.PASSWORD, password);
                    editor.apply();
                    // at this point the JSON body has been successfully parsed
                    getMacBitalino(decodedResponse.access_token, userId);
                }
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

    private void getMacBitalino(String authorization, final String userId){

        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Service setup
        ServerAPI service = retrofit.create(ServerAPI.class);

        // Prepare the HTTP request

        callMAC = service.getUser(Integer.parseInt(userId), "Bearer "+authorization);

        // Asynchronously execute HTTP request
        callMAC.enqueue(new Callback<ResponseFromServer>() {

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
                String bitalinoMAC = decodedResponse.value.get(0).Mac_Bitalino;
                if (getActivity()!=null) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(MainActivity.MAC, bitalinoMAC);
                    editor.apply();

                    Intent service = new Intent(getActivity(), UpdateService.class);
                    getActivity().startService(service);

                    dismiss();
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
}
