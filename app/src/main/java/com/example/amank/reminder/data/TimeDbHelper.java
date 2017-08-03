package com.example.amank.reminder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.amank.reminder.data.TimeContract.TimeEntry;

public class TimeDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = TimeDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "reminder.db";
    private static final int DATABASE_VERSION = 1;
    /**
     * Constructs a new instance of {@link TimeDbHelper}.
     *
     * @param context of the app
     */
    public TimeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + TimeEntry.TABLE_NAME + " ("
                + TimeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TimeEntry.COLUMN_REMINDER_NAME + " TEXT NOT NULL, "
                + TimeEntry.COLUMN_REMINDER_DATE + " TEXT NOT NULL, "
                + TimeEntry.COLUMN_REMINDER_TIME + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
