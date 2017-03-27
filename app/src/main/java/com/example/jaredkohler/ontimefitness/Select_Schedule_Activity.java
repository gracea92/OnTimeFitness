package com.example.jaredkohler.ontimefitness;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.View;
import android.provider.CalendarContract.Calendars;
import android.database.Cursor;
import android.content.ContentResolver;
import android.net.Uri;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.util.Timer;
import java.util.ArrayList;
import java.util.List;

public class Select_Schedule_Activity extends AppCompatActivity {
    private long startTime=0;
    private final String TAG = getClass().getSimpleName();


    private Spinner spinner;

    public static final String MyPREFERENCES = "MyPrefs";

    List<Long> calendarLongID;

    @Override protected void onPause(){
        super.onPause();
        Log.d(TAG, "+++ onPause() +++");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "+++ onResume() +++");
    }
    @Override
    protected void onStop() {
        super.onStop();
        startTime = SystemClock.elapsedRealtime();
        Log.d(TAG, "+++ onStop() +++");
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
        Log.d(TAG, "+++ onCreate() +++");
        setContentView(R.layout.activity_schedule_selector);

        Intent intent = getIntent();
        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_schedule);

        calendarLongID = CalendarsHelper.listOfCalendars(this);

        spinner = (Spinner) findViewById(R.id.spinnerSchedules);
        ArrayAdapter<Long> adapter = new ArrayAdapter<Long>(this, android.R.layout.simple_spinner_item, calendarLongID);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        int position = calendarLongID.indexOf(Long.parseLong(CalendarsHelper.getCurCalID(this)));
        spinner.setSelection(position);
    }

    public void Active(View view){
        SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("ID",null);

        //Gets data repository in write mode
        LoginDbHelper mDbHelper = new LoginDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String scheduleId = (spinner.getSelectedItem().toString());
        ContentValues values = new ContentValues();
        values.put(LogInContract.LogInEntry.COLUMN_NAME_CAL, scheduleId);
        db.update(LogInContract.LogInEntry.TABLE_NAME,values,"_id="+id,null);
        Toast.makeText(this, "Active schedule switched to " + scheduleId, Toast.LENGTH_SHORT).show();
    }

    public void Edit(View view){
        Intent intent = new Intent(this, Schedule_Activity.class);

        String calID = String.valueOf((Long)spinner.getSelectedItem());
        intent.putExtra("calID", calID);

        startActivity(intent);
    }
    public void Exit(View view){
        finish();
    }

}
