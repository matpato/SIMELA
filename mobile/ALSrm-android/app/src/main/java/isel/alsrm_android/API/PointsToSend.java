package isel.alsrm_android.API;

import java.util.Date;
import java.util.List;

public class PointsToSend {

    int ExamId;
    int StepNum;
    Date InitialDate;
    Date EndDate;
    List<Point> ExamStepPoints;

    public PointsToSend(int examId, int stepNum, Date initialDate, Date endDate, List<Point> examStepPoints) {
        ExamId = examId;
        StepNum = stepNum;
        InitialDate = initialDate;
        EndDate = endDate;
        ExamStepPoints = examStepPoints;
    }

    public int getExamId() {
        return ExamId;
    }

    public void setExamId(int examId) {
        ExamId = examId;
    }

    public int getStepNum() {
        return StepNum;
    }

    public void setStepNum(int stepNum) {
        StepNum = stepNum;
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

    public List<Point> getExamStepPoints() {
        return ExamStepPoints;
    }

    public void setExamStepPoints(List<Point> examStepPoints) {
        ExamStepPoints = examStepPoints;
    }
}
