package isel.alsrm_android.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDB) {
        sqlDB.execSQL(AlsrmSchema.Exams.CREATE_DB_TABLE);
        sqlDB.execSQL(AlsrmSchema.ExamSteps.CREATE_DB_TABLE);
        sqlDB.execSQL(AlsrmSchema.Points.CREATE_DB_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqlDB, int oldVersion, int newVersion) {
        sqlDB.execSQL("DROP TABLE IF EXISTS " + AlsrmSchema.Exams.TABLE_NAME);
        sqlDB.execSQL("DROP TABLE IF EXISTS " + AlsrmSchema.ExamSteps.TABLE_NAME);
        sqlDB.execSQL("DROP TABLE IF EXISTS " + AlsrmSchema.Points.TABLE_NAME);
        onCreate(sqlDB);
    }
}
