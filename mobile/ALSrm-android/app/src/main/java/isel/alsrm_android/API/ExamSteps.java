package isel.alsrm_android.API;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExamSteps {

    int StepNum;
    String Description;
    String State;
    int Time;
    int ElapsedTime;
    Date InitialDate;
    Date EndDate;
    int ExamId;

    public ExamSteps(int stepNum, String description, String state, int time, Date initialDate, Date endDate, int examId) {
        StepNum = stepNum;
        Description = description;
        State = state;
        Time = time;
        InitialDate = initialDate;
        EndDate = endDate;
        ExamId = examId;
        ElapsedTime = 0;
    }

    public ExamSteps() {

    }

    public int getStepNum() {
        return StepNum;
    }

    public void setStepNum(int stepNum) {
        StepNum = stepNum;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public int getTime() {
        return Time;
    }

    public void setTime(int time) {
        Time = time;
    }

    public Date getInitialDate() {
        return InitialDate;
    }

    public void setInitialDate(Date initialDate) {
        InitialDate = initialDate;
    }

    public Date getEndDate() {
        return EndDate;
    }

    public void setEndDate(Date endDate) {
        EndDate = endDate;
    }

    public int getExamId() {
        return ExamId;
    }

    public void setExamId(int examId) {
        ExamId = examId;
    }

    public String getInitialDateString(){
        if(InitialDate == null)
            return null;
        else {
            DateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            return dateformat.format(InitialDate);
        }
    }

    public String getEndDateString(){
        if(EndDate == null)
            return null;
        else {
            DateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            return dateformat.format(EndDate);
        }
    }

    public void setStringInitialDate(String date) {

        if(date != null) {
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            try {
                InitialDate = format.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void setStringEndDate(String date) {

        if(date != null) {
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            try {
                EndDate = format.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public int getElapsedTime() {
        return ElapsedTime;
    }

    public void setElapsedTime(int elapsedTime) {
        ElapsedTime = elapsedTime;
    }
}
