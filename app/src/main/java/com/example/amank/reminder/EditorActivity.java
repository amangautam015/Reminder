package com.example.amank.reminder;
/*
* Designed By AmanK
* */
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.amank.reminder.data.TimeDbHelper;
import com.example.amank.reminder.data.TimeContract.TimeEntry;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by amank on 3/8/17.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,View.OnClickListener {
    private TimeDbHelper mDbHelper;
    int i=0,l;
    Uri headAche;
    private EditText mNameEditText;
    String shourOfDay,minutesOfDay,sDate,sYear,sMonth;
    long presentTime,alertTime;
    private EditText txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String cDate, cTime;
    private EditText txtDate;
    private Uri mCurrentTimeUri;
    private boolean mTimeHasChanged = false;
    private static final int EXISTING_REMINDER_LOADER = 1;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mTimeHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        JodaTimeAndroid.init(this);
        Intent intent = getIntent();
        mCurrentTimeUri = intent.getData();
        if (mCurrentTimeUri == null) {
            setTitle(getString(R.string.add_reminder));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            getLoaderManager().initLoader(EXISTING_REMINDER_LOADER, null, this);
            setTitle(getString(R.string.gedit_reminder));
        }
        mNameEditText = (EditText) findViewById(R.id.edit_time_name);
        txtTime = (EditText) findViewById(R.id.txtTime);
        txtDate = (EditText) findViewById(R.id.txtDate);
        mNameEditText.setOnTouchListener(mTouchListener);
        txtTime.setOnTouchListener(mTouchListener);
        txtDate.setOnTouchListener(mTouchListener);
        txtDate.setOnClickListener(this);
        txtTime.setOnClickListener(this);
        mDbHelper = new TimeDbHelper(this);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.no_changes_saved));
        builder.setPositiveButton(getString(R.string.discard), discardButtonClickListener);
        builder.setNegativeButton(getString(R.string.keep_editing), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the event.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mTimeHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentTimeUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void saveTime() {

        String name = mNameEditText.getText().toString().trim();
        String date = txtDate.getText().toString().trim();
        String time = txtTime.getText().toString().trim();

        if (mCurrentTimeUri == null &&
                TextUtils.isEmpty(name)) {
            return;
        }
        if (mCurrentTimeUri == null &&
                TextUtils.isEmpty(date)) {
            return;
        }
        if (mCurrentTimeUri == null &&
                TextUtils.isEmpty(time)) {
            return;
        }


        // If the weight is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys

        ContentValues values = new ContentValues();

        values.put(TimeEntry.COLUMN_REMINDER_NAME, name);
        values.put(TimeEntry.COLUMN_REMINDER_TIME, time);
        values.put(TimeEntry.COLUMN_REMINDER_DATE, date);


        if (mCurrentTimeUri == null) {
            Uri newUri = getContentResolver().insert(TimeEntry.CONTENT_URI, values);
            headAche = newUri;

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.unable_toSave_time),
                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, getString(R.string.time_saved),
                        Toast.LENGTH_SHORT).show();
                long l1 = ContentUris.parseId(headAche);
                String l11 = String.valueOf(l1);
                l = Integer.parseInt(l11);
                Log.e("EditorInsterR",""+l);
            }

        }
        // UPPADTE TIME
        else {
            int rowsAffected = getContentResolver().update(mCurrentTimeUri, values, null, null);
            if (rowsAffected == -1) {

                Toast.makeText(this, getString(R.string.unable_toSave_time),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,getString(R.string.time_saved),
                        Toast.LENGTH_SHORT).show();
              // headAche =  ContentUris.withAppendedId(headAche, rowsAffected);
                l = rowsAffected;
                Log.e("EditorInsterR",""+l);
                cDate = txtDate.getText().toString();
                cTime = txtTime.getText().toString();
                deleteAlarm(rowsAffected);
            }
        }
        String DateString = cDate  +  " " + cTime;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm");
        DateTime dt = formatter.parseDateTime(DateString);
        presentTime = dt.getMillis();
     //   long k= ContentUris.parseId(headAche);
        alertTime = presentTime - System.currentTimeMillis();
     //   String k2=""+k;
     //   int k1=Integer.parseInt(k2);
        setAlarm(alertTime,l,name);
        i++;
        //Navigate back toCatalog acivity
        Intent intent = new Intent(EditorActivity.this, CatalogActivity.class);
        startActivity(intent);

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pets from user input

                    saveTime();

                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();

                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mTimeHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                TimeEntry._ID,
                TimeEntry.COLUMN_REMINDER_NAME,
                TimeEntry.COLUMN_REMINDER_DATE,
                TimeEntry.COLUMN_REMINDER_TIME,
                };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentTimeUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(TimeEntry.COLUMN_REMINDER_NAME);
            int dateColumnIndex = cursor.getColumnIndex(TimeEntry.COLUMN_REMINDER_DATE);
            int timeColumnIndex = cursor.getColumnIndex(TimeEntry.COLUMN_REMINDER_TIME);


            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            String time = cursor.getString(timeColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            txtDate.setText(date);
            txtTime.setText(time);
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        txtDate.setText("");
        txtTime.setText("");
    }
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_delete));
        builder.setPositiveButton(getString(R.string.caps_delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        if (v == txtDate) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            if(monthOfYear<10){
                                sMonth = "0" + (monthOfYear+1);
                            }
                            else{
                                sMonth = "" + (monthOfYear+1);
                            }
                            if(dayOfMonth<10){
                                sDate = "0"  + dayOfMonth;
                            }
                            else {
                                sDate = "" + dayOfMonth;
                            }
                            sYear = "" + year;
                            cDate = sDate+"-"+sMonth+"-"+sYear;
                            txtDate.setText(cDate);

                        }
                    }, mYear, mMonth, mDay);

            datePickerDialog.show();
        }
        if (v == txtTime) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {


                            if(hourOfDay<10){
                                 shourOfDay  = "0" + hourOfDay;
                            }
                            else{
                                 shourOfDay = "" + hourOfDay;
                            }
                            if(minute<10){
                                minutesOfDay = "0" + minute;

                            }
                            else {

                                minutesOfDay = ""  +  minute;
                            }

                            cTime=shourOfDay+":"+minutesOfDay;
                            txtTime.setText(cTime);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }
    private void deletePet() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentTimeUri != null) {
            // Call the ContentResolver to delete the Time at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentTimeUri
            // content URI already identifies the Time that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentTimeUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_Time_failed),
                        Toast.LENGTH_SHORT).show();

            } else {

                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_Time_successful),
                        Toast.LENGTH_SHORT).show();
                long k = ContentUris.parseId(mCurrentTimeUri);
                String k1  = "" + k;
                int k2 = Integer.parseInt(k1);
                deleteAlarm(k2);
            }
            // Close the activity
            finish();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAlarm(long ltime,int id,String name ){
        Long alertTime = new GregorianCalendar().getTimeInMillis()+ltime;
        Intent alertIntent = new Intent(this,AlertReceiver.class);
        Log.e("SETALARM",""+id);
        alertIntent.putExtra("EXTRA_SESSION_ID", id);
        alertIntent.putExtra("MESSAGE",name );
        AlarmManager alarmManager =(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //   PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alertIntent, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP,alertTime, PendingIntent.getBroadcast(this,id,alertIntent,PendingIntent.FLAG_UPDATE_CURRENT));
        //alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 5000,
        //         pendingIntent);
    }
    //DELETE SINGLE ALARM
    public void deleteAlarm(int id){
        Intent alertIntent = new Intent(this,AlertReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,id,alertIntent,0);
        AlarmManager alarmManager =(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);
        Log.e("DELETESINGLE",""+id);

    }

}

