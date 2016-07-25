package isel.alsrm_android.Fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.LinkedList;
import java.util.List;

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
import isel.alsrm_android.R;

public class ListExamsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ExpandableListAdapter listAdapter;
    private AlsrmAsyncQueryHandler queryHandler;
    private List<Exams> exams = new LinkedList<>();
    private View view;

    private static class AlsrmAsyncQueryHandler extends AsyncQueryHandler {

        public AlsrmAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader loader = null;
        if(id == 0) {
            loader = new CursorLoader(
                    this.getActivity(),
                    AlsrmContract.Exams.CONTENT_URI,
                    Operations.examProjection,
                    null, null, null);
        }
        if(id >= 1){
            loader = new CursorLoader(
                    this.getActivity(),
                    AlsrmContract.ExamSteps.CONTENT_URI,
                    Operations.ExamStepsProjection,
                    AlsrmSchema.exam_id + " = ? ",
                    new String[]{""+args.getInt("exam_id"),},
                    null);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if(loader.getId() == 0) {
            exams = new LinkedList<>();
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                int id  = 1;

                do {
                    Exams exam = new Exams();
                    exam.setExamId(cursor.getInt(cursor.getColumnIndex(AlsrmSchema.id)));
                    exam.setExamType(cursor.getString(cursor.getColumnIndex(AlsrmSchema.exam_type)));
                    exam.setMuscleId(cursor.getInt(cursor.getColumnIndex(AlsrmSchema.exam_muscle)));
                    exam.setExamState(cursor.getString(cursor.getColumnIndex(AlsrmSchema.exam_state)));
                    exam.setExamStringInitialDate(cursor.getString(cursor.getColumnIndex(AlsrmSchema.exam_initialDate)));
                    exam.setExamStringEndDate(cursor.getString(cursor.getColumnIndex(AlsrmSchema.exam_endDate)));
                    exam.setUserId(cursor.getInt(cursor.getColumnIndex(AlsrmSchema.exam_user_id)));

                    exams.add(exam);

                    Bundle bundle = new Bundle();
                    bundle.putInt("exam_id", exam.getExamId());
                    getLoaderManager().initLoader(id, bundle, this);
                    ++id;

                } while (cursor.moveToNext());

                listAdapter.setData(exams);
            }
        }

        if(loader.getId() >= 1) {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                boolean isCompleted = true;
                LinkedList<ExamSteps> list_ExamSteps = new LinkedList<>();
                if (cursor.moveToFirst()) {
                    do {

                        ExamSteps examSteps = new ExamSteps();
                        examSteps.setExamId(cursor.getInt(cursor.getColumnIndex(AlsrmSchema.exam_id)));
                        examSteps.setStepNum(cursor.getInt(cursor.getColumnIndex(AlsrmSchema.examStep_num)));
                        examSteps.setDescription(cursor.getString(cursor.getColumnIndex(AlsrmSchema.examStep_description)));
                        examSteps.setState(cursor.getString(cursor.getColumnIndex(AlsrmSchema.examStep_state)));
                        examSteps.setTime(cursor.getInt(cursor.getColumnIndex(AlsrmSchema.examStep_time)));
                        examSteps.setElapsedTime(cursor.getInt(cursor.getColumnIndex(AlsrmSchema.examStep_elapsed_time)));
                        examSteps.setStringInitialDate(cursor.getString(cursor.getColumnIndex(AlsrmSchema.exam_initialDate)));
                        examSteps.setStringEndDate(cursor.getString(cursor.getColumnIndex(AlsrmSchema.exam_endDate)));

                        list_ExamSteps.add(examSteps);

                        if(examSteps.getState().equals(AlsrmSchema.PENDING))
                            isCompleted = false;

                    } while (cursor.moveToNext());
                }

                int idx = getExamById(list_ExamSteps.getFirst().getExamId());
                if(idx != -1) {
                    exams.get(idx).setExamSteps(list_ExamSteps);

                    if (isCompleted)
                        if(!exams.get(idx).getExamState().equals(AlsrmSchema.COMPLETED)){

                            exams.get(idx).setExamState(AlsrmSchema.COMPLETED);

                            ContentValues values = new ContentValues();
                            values.put(AlsrmSchema.exam_state, AlsrmSchema.COMPLETED);
                            queryHandler.startUpdate(1, null, AlsrmContract.Exams.CONTENT_URI, values,
                                    AlsrmSchema.id + " = ? ", new String[]{"" + exams.get(idx).getExamId(),});
                        }

                    listAdapter.setData(exams);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    public ListExamsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        queryHandler = new AlsrmAsyncQueryHandler(this.getActivity().getContentResolver());

        if(view == null) {

            view = inflater.inflate(R.layout.fragment_list_exams, container, false);
            ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
            listAdapter = new ExpandableListAdapter(getActivity(), exams);
            getLoaderManager().initLoader(0, null, this);

            // setting list adapter
            expandableListView.setAdapter(listAdapter);

            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                    Exams exam = exams.get(groupPosition);
                    ExamSteps examSteps = exams.get(groupPosition).getExamSteps().get(childPosition);
                    if(examSteps.getState().equals(AlsrmSchema.PENDING))
                        goToExam(exam, examSteps.getExamId(), examSteps.getStepNum());

                    return false;
                }
            });
        }
        return view;
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

    private int getExamById(int id){
        for(int i = 0; i< exams.size(); i++) {
            if(exams.get(i).getExamId() == id){
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }
}
