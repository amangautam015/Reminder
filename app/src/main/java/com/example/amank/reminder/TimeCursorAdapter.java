package com.example.amank.reminder;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.amank.reminder.data.TimeContract.TimeEntry;

/**
 * Created by amank on 3/8/17.
 */

public class TimeCursorAdapter extends CursorAdapter {

    public TimeCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent , false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView time = (TextView) view.findViewById(R.id.time);
        TextView date = (TextView) view.findViewById(R.id.date);
        String nameToset = cursor.getString(cursor.getColumnIndexOrThrow(TimeEntry.COLUMN_REMINDER_NAME));
        String timeToset = cursor.getString(cursor.getColumnIndexOrThrow(TimeEntry.COLUMN_REMINDER_TIME));
        String dateToset = cursor.getString(cursor.getColumnIndexOrThrow(TimeEntry.COLUMN_REMINDER_DATE));
        name.setText(nameToset);
        time.setText(timeToset);
        date.setText(dateToset);
    }
}
