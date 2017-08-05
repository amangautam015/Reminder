package com.example.amank.reminder.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.amank.reminder.data.TimeContract.TimeEntry;

/**
 * {@link ContentProvider} for Reminder app.
 */
public class TimeProvider extends ContentProvider {

    private TimeDbHelper mDbHelper;
    /** Tag for the log messages */
    public static final String LOG_TAG = TimeProvider.class.getSimpleName();
    private static final int REMINDER = 100;
    private static final int REMINDER_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static  {

        sUriMatcher.addURI(TimeContract.CONTENT_AUTHORITY,TimeContract.PATH_REMINDER, REMINDER);
        sUriMatcher.addURI(TimeContract.CONTENT_AUTHORITY, TimeContract.PATH_REMINDER + "/#", REMINDER_ID);


    }
    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new TimeDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);

        switch (match) {
            case REMINDER:
                
                cursor = database.query(TimeEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case REMINDER_ID:

                selection = TimeEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(TimeEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case REMINDER:
                return saveTime(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri saveTime(Uri uri, ContentValues values) {


        String name = values.getAsString(TimeEntry.COLUMN_REMINDER_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Time requires a name");
        }
        String time = values.getAsString(TimeEntry.COLUMN_REMINDER_TIME);
        if (time == null ) {
            throw new IllegalArgumentException("Time requires valid time");
        }
        String date = values.getAsString(TimeEntry.COLUMN_REMINDER_DATE);
        if (date == null ) {
            throw new IllegalArgumentException("Time requires valid date");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(TimeEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case REMINDER:
                return updateTime(uri, contentValues, selection, selectionArgs);
            case REMINDER_ID:
                // For the REMINDER_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = TimeEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateTime(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updateTime(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If the {@link TimeEntry#COLUMN_REMINDER_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(TimeEntry.COLUMN_REMINDER_NAME)) {
            String name = values.getAsString(TimeEntry.COLUMN_REMINDER_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Time requires a name");
            }
        }

        // If the {@link TimeEntry#COLUMN_REMINDER_TIME} key is present,
        // check that the gender value is valid.
        if (values.containsKey(TimeEntry.COLUMN_REMINDER_TIME)) {
            String time = values.getAsString(TimeEntry.COLUMN_REMINDER_TIME);
            if (time == null) {
                throw new IllegalArgumentException("Time requires valid time");
            }
        }

        // If the {@link TimeEntry#COLUMN_REMINDER_DATE} key is present,
        // check that the weight value is valid.
        if (values.containsKey(TimeEntry.COLUMN_REMINDER_DATE)) {
            // Check that the weight is greater than or equal to 0 kg
            String date = values.getAsString(TimeEntry.COLUMN_REMINDER_DATE);
            if (date == null ) {
                throw new IllegalArgumentException("Time requires valid date");
            }
        }

        // No need to check the breed, any value is valid (including null).
        if (values.size()==0){
            return 0;
        }
        SQLiteDatabase database =  mDbHelper.getWritableDatabase();
        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(TimeEntry.TABLE_NAME, values, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        if(rowsUpdated==0){
            rowsUpdated = -1;
        }
        else {
            // Turn Rows Updated to row ID.
            String l = String.valueOf(ContentUris.parseId(uri));
            rowsUpdated = Integer.parseInt(l);
        }
        // Return the number of ID rows updated
        return rowsUpdated;
    }


    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case REMINDER:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(TimeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REMINDER_ID:
                // Delete a single row given by the ID in the URI
                selection = TimeEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(TimeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case REMINDER:
                return TimeEntry.CONTENT_LIST_TYPE;
            case REMINDER_ID:
                return TimeEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}