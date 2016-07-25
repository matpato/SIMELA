package isel.alsrm_android.API;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Exams {

    int ExamId;
    String ExamType;

    int MuscleId;
    Muscle ThisMuscle;
    String ExamMuscle;

    String ExamState;
    Date ExamInitialDate;
    Date ExamEndDate;
    int UserId;
    List<ExamSteps> ExamSteps;

    public Exams(int examId, String examType, int muscleId, Muscle thisMuscle, String examState, Date examInitialDate, Date examEndDate, int userId, List<ExamSteps> examSteps) {
        ExamId = examId;
        ExamType = examType;
        MuscleId = muscleId;
        ThisMuscle = thisMuscle;
        ExamState = examState;
        ExamInitialDate = examInitialDate;
        ExamEndDate = examEndDate;
        UserId = userId;
        ExamSteps = examSteps;
    }

    public Exams() {
        ExamSteps = new LinkedList<>();
    }

    public int getExamId() {
        return ExamId;
    }

    public void setExamId(int examId) {
        ExamId = examId;
    }

    public String getExamType() {
        return ExamType.toUpperCase();
    }

    public void setExamType(String examType) {
        ExamType = examType;
    }

    public int getMuscleId() {
        return MuscleId;
    }

    public void setMuscleId(int muscleId) {
        MuscleId = muscleId;
    }

    public Muscle getThisMuscle() {
        return ThisMuscle;
    }

    public void setThisMuscle(Muscle thisMuscle) {
        ThisMuscle = thisMuscle;
    }


    public String getExamState() {
        return ExamState;
    }

    public void setExamState(String examState) {
        ExamState = examState;
    }

    public Date getExamInitialDate() {
        return ExamInitialDate;
    }

    public void setExamInitialDate(Date examInitialDate) {
        ExamInitialDate = examInitialDate;
    }

    public Date getExamEndDate() {
        return ExamEndDate;
    }

    public void setExamEndDate(Date examEndDate) {
        ExamEndDate = examEndDate;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public List<ExamSteps> getExamSteps() {
        return ExamSteps;
    }

    public void setExamSteps(List<ExamSteps> examSteps) {
        ExamSteps = examSteps;
    }

    public String getExamInitialDateString(){
        if(ExamInitialDate == null){
            return null;
        }
        else {
            DateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            return dateformat.format(ExamInitialDate);
        }
    }

    public String getExamEndDateString(){
        if(ExamEndDate == null){
            return null;
        }
        else {
            DateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            return dateformat.format(ExamEndDate);
        }
    }

    public void setExamStringInitialDate(String date) {

        if(date != null) {
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            try {
                ExamInitialDate = format.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void setExamStringEndDate(String date) {

        if(date != null) {
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            try {
                ExamEndDate = format.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
