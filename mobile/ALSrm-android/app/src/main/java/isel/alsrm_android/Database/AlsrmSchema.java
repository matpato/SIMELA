package isel.alsrm_android.Database;

public interface AlsrmSchema {

    String DATABASE_NAME = "alsrm.db";
    int DATABASE_VERSION = 1;

    String PENDING = "pending";
    String COMPLETED = "completed";
    String CANCELLED = "cancelled";

    String MORNING = "morning";
    String AFTERNOON = "afternoon";
    String NIGHT = "night";

    int AnteriorTibialis = 1; //perna
    int FlexorCarpiRadialis = 2; //braço
    int SternocleidoMastoideus = 3; //externo

    String id = "_id";
    String exam_type = "exam_type";
    String exam_muscle = "exam_muscle";
    String exam_state = "exam_state";
    String exam_user_id = "user_id";
    String exam_initialDate = "exam_initialDate";
    String exam_endDate = "exam_endDate";

    String exam_id = "exam_id";
    String examStep_num = "stepnum";
    String examStep_description = "description";
    String examStep_state = "state";
    String examStep_time = "time";
    String examStep_elapsed_time = "elapsed_time";


    String examstepnum = "examstepnum";
    String x_data = "x_data";
    String y_data = "y_data";

    interface Exams {

        String TABLE_NAME = "Exams";

        String CREATE_DB_TABLE = " CREATE TABLE IF NOT EXISTS " + TABLE_NAME

                + " ( " + id +  " INTEGER PRIMARY KEY, "
                + exam_type + " TEXT, "
                + exam_muscle + " INTEGER, "
                + exam_state + " TEXT, "
                + exam_user_id + " INTEGER, "
                + exam_initialDate + " DATE, "
                + exam_endDate + " DATE );";
    }

    interface ExamSteps {

        String TABLE_NAME = "ExamSteps";

        String CREATE_DB_TABLE = " CREATE TABLE IF NOT EXISTS " + TABLE_NAME

                + " ( " + exam_id +  " INTEGER, "
                + examStep_num + " INTEGER, "
                + examStep_description + " TEXT, "
                + examStep_state + " TEXT, "
                + examStep_time + " INTEGER, "
                + examStep_elapsed_time + " INTEGER, "
                + exam_initialDate + " DATE, "
                + exam_endDate + " DATE, "
                + "FOREIGN KEY(exam_id) REFERENCES Exam(_id), "
                + "PRIMARY KEY (exam_id, stepnum) );";
    }

    interface Points {

        String TABLE_NAME = "Points";

        String CREATE_DB_TABLE = " CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                + " ( " +  exam_id + " INTEGER, "
                + examstepnum + " INTEGER, "
                + x_data + " INTEGER, "
                + y_data + " REAL, "
                + "FOREIGN KEY(exam_id, examstepnum) REFERENCES ExamStep(exam_id, stepnum), "
                + "PRIMARY KEY (x_data, exam_id, examstepnum) );";
    }
}
