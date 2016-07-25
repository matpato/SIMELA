package isel.alsrm_android.Database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

public class AlsrmProvider extends ContentProvider {

    static final int uriCodeExams = 1;
    static final int uriCodeExamSteps = 2;
    static final int uriCodePoints = 3;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AlsrmContract.AUTHORITY, AlsrmContract.Exams.RESOURCE , uriCodeExams);
        uriMatcher.addURI(AlsrmContract.AUTHORITY, AlsrmContract.ExamSteps.RESOURCE , uriCodeExamSteps);
        uriMatcher.addURI(AlsrmContract.AUTHORITY, AlsrmContract.Points.RESOURCE, uriCodePoints);
    }

    private SQLiteDatabase sqlDB;

    @Override
    public boolean onCreate() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext(), AlsrmSchema.DATABASE_NAME, null, AlsrmSchema.DATABASE_VERSION);
        sqlDB = dbHelper.getWritableDatabase();
        return sqlDB != null;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {
            case uriCodeExams:
                queryBuilder.setTables(AlsrmSchema.Exams.TABLE_NAME);
                break;

            case uriCodeExamSteps:
                queryBuilder.setTables(AlsrmSchema.ExamSteps.TABLE_NAME);
                break;

            case uriCodePoints:
                queryBuilder.setTables(AlsrmSchema.Points.TABLE_NAME);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor cursor = queryBuilder.query(sqlDB, projection, selection, selectionArgs, null, null, sortOrder);

        if(getContext() !=null)
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {

        switch (uriMatcher.match(uri)) {

            case uriCodeExams:
                return AlsrmContract.Exams.CONTENT_TYPE;
            case uriCodeExamSteps:
                return AlsrmContract.ExamSteps.CONTENT_TYPE;
            case uriCodePoints:
                return AlsrmContract.Points.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        String table;
        switch (uriMatcher.match(uri)) {
            case uriCodeExams:
                table = AlsrmSchema.Exams.TABLE_NAME;
                break;
            case uriCodeExamSteps:
                table = AlsrmSchema.ExamSteps.TABLE_NAME;
                break;
            case uriCodePoints:
                table = AlsrmSchema.Points.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        long rowID = sqlDB.insert(table, null, values);

        if (rowID > 0) {
            if(getContext() !=null)
                getContext().getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, rowID);
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;

        switch (uriMatcher.match(uri)) {
            case uriCodeExams:
                rowsDeleted = sqlDB.delete(AlsrmSchema.Exams.TABLE_NAME, selection, selectionArgs);
                break;
            case uriCodeExamSteps:
                rowsDeleted = sqlDB.delete(AlsrmSchema.ExamSteps.TABLE_NAME, selection, selectionArgs);
                break;
            case uriCodePoints:
                rowsDeleted = sqlDB.delete(AlsrmSchema.Points.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if(getContext() !=null)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated;

        switch (uriMatcher.match(uri)) {
            case uriCodeExams:
                rowsUpdated = sqlDB.update(AlsrmSchema.Exams.TABLE_NAME, values, selection, selectionArgs);
                break;
            case uriCodeExamSteps:
                rowsUpdated = sqlDB.update(AlsrmSchema.ExamSteps.TABLE_NAME, values, selection, selectionArgs);
                break;
            case uriCodePoints:
                rowsUpdated = sqlDB.update(AlsrmSchema.Points.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if(getContext() !=null)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
