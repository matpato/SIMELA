package isel.alsrm_android.Database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public interface AlsrmContract {

    String AUTHORITY = "isel.alsrm_android.Database";

    Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    String MEDIA_BASE_SUBTYPE = "/vnd.alsrmdb.";

    interface Exams {

        String RESOURCE = "Exams";

        Uri CONTENT_URI = Uri.withAppendedPath(AlsrmContract.CONTENT_URI, RESOURCE);

        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
    }

    interface ExamSteps{

        String RESOURCE = "ExamSteps";

        Uri CONTENT_URI = Uri.withAppendedPath(AlsrmContract.CONTENT_URI, RESOURCE);

        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
    }

    interface Points extends BaseColumns {

        String RESOURCE = "Points";

        Uri CONTENT_URI = Uri.withAppendedPath(AlsrmContract.CONTENT_URI, RESOURCE);

        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
    }
}
