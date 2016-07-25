package isel.alsrm_android.Fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import isel.alsrm_android.API.ExamSteps;
import isel.alsrm_android.API.Exams;
import isel.alsrm_android.Database.AlsrmSchema;
import isel.alsrm_android.R;


public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<Exams> values;

    public ExpandableListAdapter(Context context, List<Exams> values) {
        this._context = context;
        this.values = values;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.values.get(groupPosition).getExamSteps().get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ExamSteps examSteps = (ExamSteps) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.sub_item_exam_steps, null);
        }

        ImageView imageState = (ImageView) convertView.findViewById(R.id.ImageState);
        imageState(convertView, imageState, examSteps.getState());

        ImageView imageDescription = (ImageView) convertView.findViewById(R.id.ImageDescription);
        imageDescription(convertView, imageDescription, examSteps.getDescription());

        TextView Time = (TextView) convertView.findViewById(R.id.Time);
        String textTime = examSteps.getTime()+" min";
        Time.setText(textTime);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return values.get(groupPosition).getExamSteps().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.values.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.values.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        Exams exam = (Exams) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_exam, null);
        }

        ImageView imageState = (ImageView) convertView.findViewById(R.id.ImageState);
        ImageView imageMuscle = (ImageView) convertView.findViewById(R.id.ImageMuscle);
        TextView ExamType = (TextView) convertView.findViewById(R.id.ExamType);
        TextView ExamInitialDate = (TextView) convertView.findViewById(R.id.ExamInitialDate);
        TextView ExamEndDate = (TextView) convertView.findViewById(R.id.ExamEndDate);

        convertView.getTag();

        imageState(convertView, imageState, exam.getExamState());
        imageMuscle(convertView, imageMuscle, exam.getMuscleId(), exam.getExamType());

        DateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        ExamType.setText(exam.getExamType());
        String textInitialDate = _context.getString(R.string.initial_date)+" "+dateformat.format(exam.getExamInitialDate());
        String textEndDate = _context.getString(R.string.end_date)+" "+dateformat.format(exam.getExamEndDate());
        ExamInitialDate.setText(textInitialDate);
        ExamEndDate.setText(textEndDate);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setData(List<Exams> values) {
        this.values = values;
        notifyDataSetChanged();
    }

    private void imageState(View convertView, ImageView imageState, String state){

        if(convertView != null) {
            if (convertView.getTag().equals("tablet_screen_xlarge")) {
                if(state.equals(AlsrmSchema.PENDING))
                    imageState.setImageResource(R.drawable.pending_xlarge);
                if(state.equals(AlsrmSchema.COMPLETED))
                    imageState.setImageResource(R.drawable.complete_xlarge);
                if(state.equals(AlsrmSchema.CANCELLED))
                    imageState.setImageResource(R.drawable.cancel_xlarge);
            }

            if (convertView.getTag().equals("tablet_screen_large")) {
                if(state.equals(AlsrmSchema.PENDING))
                    imageState.setImageResource(R.drawable.pending_large);
                if(state.equals(AlsrmSchema.COMPLETED))
                    imageState.setImageResource(R.drawable.complete_large);
                if(state.equals(AlsrmSchema.CANCELLED))
                    imageState.setImageResource(R.drawable.cancel_large);
            }

            if (convertView.getTag().equals("phone_screen")) {
                if(state.equals(AlsrmSchema.PENDING))
                    imageState.setImageResource(R.drawable.pending);
                if(state.equals(AlsrmSchema.COMPLETED))
                    imageState.setImageResource(R.drawable.complete);
                if(state.equals(AlsrmSchema.CANCELLED))
                    imageState.setImageResource(R.drawable.cancel);
            }
        }
    }

    private void imageDescription(View convertView, ImageView imageDescription, String description){

        if(convertView != null) {
            if (convertView.getTag().equals("tablet_screen_xlarge")) {
                if (description.equals(AlsrmSchema.MORNING))
                    imageDescription.setImageResource(R.drawable.morning_xlarge);
                if (description.equals(AlsrmSchema.AFTERNOON))
                    imageDescription.setImageResource(R.drawable.afternoon_xlarge);
                if (description.equals(AlsrmSchema.NIGHT))
                    imageDescription.setImageResource(R.drawable.night_xlarge);
            }

            if (convertView.getTag().equals("tablet_screen_large")) {
                if (description.equals(AlsrmSchema.MORNING))
                    imageDescription.setImageResource(R.drawable.morning_large);
                if (description.equals(AlsrmSchema.AFTERNOON))
                    imageDescription.setImageResource(R.drawable.afternoon_large);
                if (description.equals(AlsrmSchema.NIGHT))
                    imageDescription.setImageResource(R.drawable.night_large);
            }

            if (convertView.getTag().equals("phone_screen")) {
                if (description.equals(AlsrmSchema.MORNING))
                    imageDescription.setImageResource(R.drawable.morning);
                if (description.equals(AlsrmSchema.AFTERNOON))
                    imageDescription.setImageResource(R.drawable.afternoon);
                if (description.equals(AlsrmSchema.NIGHT))
                    imageDescription.setImageResource(R.drawable.night);
            }
        }
    }

    private void imageMuscle(View convertView, ImageView imageMuscle, int muscleId, String type){

        if(convertView != null) {

            if (convertView.getTag().equals("tablet_screen_xlarge")) {

                if(type.equals(ECG_Fragment.ECG)){
                    imageMuscle.setVisibility(View.INVISIBLE);
                }else {
                    imageMuscle.setVisibility(View.VISIBLE);
                    if (muscleId == AlsrmSchema.AnteriorTibialis)
                        imageMuscle.setImageResource(R.drawable.fig_leg_xlarge);
                    if (muscleId == AlsrmSchema.FlexorCarpiRadialis)
                        imageMuscle.setImageResource(R.drawable.fig_arm_xlarge);
                    if (muscleId == AlsrmSchema.SternocleidoMastoideus)
                        imageMuscle.setImageResource(R.drawable.fig_head_xlarge);
                }
            }

            if (convertView.getTag().equals("tablet_screen_large")) {

                if(type.equals(ECG_Fragment.ECG)){
                    imageMuscle.setVisibility(View.INVISIBLE);
                }else {
                    imageMuscle.setVisibility(View.VISIBLE);
                    if (muscleId == AlsrmSchema.AnteriorTibialis)
                        imageMuscle.setImageResource(R.drawable.fig_leg_large);
                    if (muscleId == AlsrmSchema.FlexorCarpiRadialis)
                        imageMuscle.setImageResource(R.drawable.fig_arm_large);
                    if (muscleId == AlsrmSchema.SternocleidoMastoideus)
                        imageMuscle.setImageResource(R.drawable.fig_head_large);
                }
            }

            if (convertView.getTag().equals("phone_screen")) {

                if(type.equals(ECG_Fragment.ECG)){
                    imageMuscle.setVisibility(View.INVISIBLE);
                }else {
                    imageMuscle.setVisibility(View.VISIBLE);
                    if (muscleId == AlsrmSchema.AnteriorTibialis)
                        imageMuscle.setImageResource(R.drawable.fig_leg);
                    if (muscleId == AlsrmSchema.FlexorCarpiRadialis)
                        imageMuscle.setImageResource(R.drawable.fig_arm);
                    if (muscleId == AlsrmSchema.SternocleidoMastoideus)
                        imageMuscle.setImageResource(R.drawable.fig_head);
                }
            }
        }
    }
}
