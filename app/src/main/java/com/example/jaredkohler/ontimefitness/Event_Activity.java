package com.example.jaredkohler.ontimefitness;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.provider.CalendarContract.Events;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.content.ContentValues;
import android.net.Uri;

import android.content.ContentResolver;
import android.widget.EditText;

public class Event_Activity extends AppCompatActivity {
    private long startTime=0;
    private final String TAG = getClass().getSimpleName();

    private long eventID;
    EditText name;
    EditText location;
    EditText time;
    EditText date;
    long calID;

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "+++ onStop() +++");
        startTime = SystemClock.elapsedRealtime();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "+++ onRestart() +++");
        if(startTime + 5000 <SystemClock.elapsedRealtime()){
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventID = getIntent().getExtras().getLong("ID");
        calID = Long.parseLong(getIntent().getExtras().getString("calID"));

        Log.d(TAG, "+++ onCreate +++");
        setContentView(R.layout.activity_event);
        name = (EditText) findViewById(R.id.editName);
        location = (EditText)findViewById(R.id.editLocation);
        time = (EditText)findViewById((R.id.editTime));
        date = (EditText) findViewById((R.id.editDate));

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR},
                    259);

        }
        if(eventID != -1) {
            ContentResolver cr = getContentResolver();

            Cursor cur= cr.query(CalendarContract.Events.CONTENT_URI, new String[]{CalendarContract.Events.TITLE},
                    "(" + CalendarContract.Events._ID + " = " + eventID + ")", null, null);
            cur.moveToNext();
            name.setText(cur.getString(0));
            cur= cr.query(CalendarContract.Events.CONTENT_URI, new String[]{CalendarContract.Events.EVENT_LOCATION},
                    "(" + CalendarContract.Events._ID + " == '" + eventID + "')", null, null);
            cur.moveToNext();
            location.setText(cur.getString(0));

            cur= cr.query(CalendarContract.Events.CONTENT_URI, new String[]{CalendarContract.Events.DTSTART},
                    "(" + CalendarContract.Events._ID + " == '" + eventID + "')", null, null);
            cur.moveToNext();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(cur.getString(0)));
            String dateTime = formatter.format(calendar.getTime());



            date.setText(dateTime.substring(0, dateTime.indexOf(' ')));
            time.setText(dateTime.substring(dateTime.indexOf(' ') + 1));
        }
    }

    public void save(View view){

        String timeString = time.getText().toString();

        int startHour = Integer.parseInt(timeString.substring(0, timeString.indexOf(':')));
        int startMinute = Integer.parseInt(timeString.substring(timeString.indexOf(':') +1));

        String fullDate = date.getText().toString();

        int year = Integer.parseInt(fullDate.substring(0, fullDate.indexOf('/')));
        fullDate = fullDate.substring(fullDate.indexOf('/')+1);
        int month = Integer.parseInt(fullDate.substring(0, fullDate.indexOf('/')));
        month--;
        fullDate = fullDate.substring(fullDate.indexOf('/')+1);
        int day = Integer.parseInt(fullDate);

        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(year, month, day, startHour, startMinute);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(year, month, day, startHour + 1, startMinute);
        endMillis = endTime.getTimeInMillis();


        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, startMillis);
        values.put(Events.DTEND, endMillis);
        values.put(Events.TITLE, name.getText().toString());
        values.put(Events.EVENT_LOCATION, location.getText().toString());
        values.put(Events.CALENDAR_ID, calID);
        values.put(Events.EVENT_TIMEZONE, "America/Chicago");


        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR},
                    259);

        }
        if(eventID == -1) {
            Uri uri = cr.insert(Events.CONTENT_URI, values);
            eventID = Long.parseLong(uri.getLastPathSegment());
        } else {
            Uri updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID);
            getContentResolver().update(updateUri, values, null, null);
        }

    }

    public void exit(View view){
        finish();
    }

}