package isel.alsrm_android.API;

public class Point {

    private int ExamId;
    private int ExamStepNum;
    private int X;
    private float Y;

    public Point(int examId, int examStepNum, int x, float y) {
        ExamId = examId;
        ExamStepNum = examStepNum;
        X = x;
        Y = y;
    }

    public Point() {}

    public int getX() {
        return X;
    }

    public void setX(int x) {
        this.X = x;
    }

    public float getY() {
        return Y;
    }

    public void setY(float y) {
        this.Y = y;
    }

    public int getExamId() {
        return ExamId;
    }

    public void setExamId(int examId) {
        ExamId = examId;
    }

    public int getExamStepNum() {
        return ExamStepNum;
    }

    public void setExamStepNum(int examStepNum) {
        ExamStepNum = examStepNum;
    }
}
