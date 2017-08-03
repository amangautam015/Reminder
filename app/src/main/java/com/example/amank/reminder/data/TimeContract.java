package com.example.amank.reminder.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * all rights to amak-Inspiron
 * API Contract for the Reminder app.
 */
public final class TimeContract {


    private TimeContract() {}


    public static final String CONTENT_AUTHORITY = "com.example.amank.reminder";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_REMINDER = "reminder";

    public static final class TimeEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_REMINDER);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REMINDER;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single reminder.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REMINDER;

        /**
         * Name of database table for pets
         */
        public final static String TABLE_NAME = "reminder";

        /**
         * Unique ID number for the reminder (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;


        public final static String COLUMN_REMINDER_NAME = "name";


        public final static String COLUMN_REMINDER_TIME = "time";


        public final static String COLUMN_REMINDER_DATE = "date";


    }
}
