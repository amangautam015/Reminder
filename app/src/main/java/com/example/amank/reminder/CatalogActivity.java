package com.example.amank.reminder;
/*
* Designed By AmanK.
* */
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.amank.reminder.data.TimeContract.TimeEntry;
import com.example.amank.reminder.data.TimeDbHelper;

import java.util.ArrayList;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int REMINDER_LOADER= 0;
    ArrayList<Integer> IntegerArray = new ArrayList<Integer>();
    TimeCursorAdapter mCursorAdapter;
    private TimeDbHelper mDbHelper;
    ListView timeListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        mDbHelper = new TimeDbHelper(this);
        timeListView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        timeListView.setEmptyView(emptyView);
        mCursorAdapter = new TimeCursorAdapter(this,null);
        timeListView.setAdapter(mCursorAdapter);
        timeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Uri currentTimeUri = ContentUris.withAppendedId(TimeEntry.CONTENT_URI,id);

                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                intent.setData(currentTimeUri);
                startActivity(intent);
            }
        });
        //Kick off Loader
        getLoaderManager().initLoader(REMINDER_LOADER, null, this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
          
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllTime();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection  = {TimeEntry._ID,TimeEntry.COLUMN_REMINDER_NAME,TimeEntry.COLUMN_REMINDER_DATE,TimeEntry.COLUMN_REMINDER_TIME};
        return new CursorLoader(this,
                TimeEntry.CONTENT_URI,
                projection,
                null,
                null,
                null

        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
    private void deleteAllTime() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                TimeEntry._ID,
                TimeEntry.COLUMN_REMINDER_NAME,
                TimeEntry.COLUMN_REMINDER_TIME,
                TimeEntry.COLUMN_REMINDER_DATE,
                };

        // Perform a query on the Times table
        Cursor cursor1 = db.query(
                TimeEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order
        int i=0;
   try {

       while (cursor1.moveToNext()) {

           int id = cursor1.getInt(cursor1.getColumnIndex(TimeEntry._ID));

           IntegerArray.add(id);
           Log.e("cursorDELETECATALOG K1", "" + id);

       }
   }
   finally {
       cursor1.close();
   }
        int rowsDeleted = getContentResolver().delete(TimeEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from reminder database");
        if(rowsDeleted!=-1){
            deleteAllAlarms();
        }

    }
    public  void deleteAllAlarms(){





        for (int id : IntegerArray) {
            Intent alertIntent = new Intent(this,AlertReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(this,id,alertIntent,0);
            AlarmManager alarmManager =(AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Log.e("DELEETEALLIDD K1", "" + id);
            alarmManager.cancel(pi);
        }




    }
}

