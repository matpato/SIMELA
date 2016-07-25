package isel.alsrm_android.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import isel.alsrm_android.Bluetooth.ManageConnectedSocket;
import isel.alsrm_android.R;

public class BodyPartExamFragment extends Fragment {

    public BodyPartExamFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_body_part_exam, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        toDiscardExam();
    }

    private void toDiscardExam(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean run = sharedPreferences.getBoolean(ManageConnectedSocket.IS_INTENT_SERVICE_RUNNING, false);
        int examId = sharedPreferences.getInt(ManageConnectedSocket.EXAM_ID, 0);
        int examStepNum = sharedPreferences.getInt(ManageConnectedSocket.EXAM_STEP_NUM, 0);

        if(run && examId == 0 && examStepNum == 0){

            Intent intentService = new Intent(getActivity(), ManageConnectedSocket.class);
            getActivity().stopService(intentService);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(ManageConnectedSocket.IS_INTENT_SERVICE_RUNNING, false);
            editor.apply();
        }
    }
}
