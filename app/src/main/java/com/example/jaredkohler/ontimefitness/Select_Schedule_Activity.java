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

import java.util.Timer;
import java.util.ArrayList;
import java.util.List;

public class Select_Schedule_Activity extends AppCompatActivity {
    private long startTime=0;
    private final String TAG = getClass().getSimpleName();


    private Spinner spinner;

    public static final String[] EVENT_PROJECTION = new String[] {
            Calendars._ID,                           // 0
            Calendars.ACCOUNT_NAME,                  // 1
            Calendars.CALENDAR_DISPLAY_NAME,         // 2
            Calendars.OWNER_ACCOUNT                  // 3
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    public static final String MyPREFERENCES = "MyPrefs";

    List<String> calendarDisplayNames;
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
        calendarDisplayNames = new ArrayList<String>();
        calendarLongID = new ArrayList<Long>();

        super.onCreate(savedInstanceState);
        Log.d(TAG, "+++ onCreate() +++");
        setContentView(R.layout.activity_schedule_selector);

        Intent intent = getIntent();
        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_schedule);

        // Run query
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri uri = Calendars.CONTENT_URI;
        String[] selection = new String[]{Calendars._ID, Calendars.NAME, Calendars.ACCOUNT_NAME,
            Calendars.ACCOUNT_TYPE};
        String[] selectionArgs = new String[] {"hera@example.com", "com.example",
                "hera@example.com"};
        // Submit the query and get a Cursor object back.
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR},
                    259);

        }

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            cur = cr.query(uri, selection, Calendars.VISIBLE + " =1", null, Calendars._ID + " ASC");


            while (cur.moveToNext()) {
                long calID = 0;
                String displayName = null;
                String accountName = null;
                String ownerName = null;

                // Get the field values
                calID = cur.getLong(PROJECTION_ID_INDEX);
                displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
                accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
                ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

                calendarDisplayNames.add(displayName);
                calendarLongID.add(calID);


            }

            spinner = (Spinner) findViewById(R.id.spinnerSchedules);
            ArrayAdapter<Long> adapter = new ArrayAdapter<Long>(this, android.R.layout.simple_spinner_item, calendarLongID);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinner.setAdapter(adapter);

            SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            String id = sharedPreferences.getString("ID",null);

            //Gets data repository in write mode
            LoginDbHelper mDbHelper = new LoginDbHelper(this);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            Cursor cursor  = db.query(LogInContract.LogInEntry.TABLE_NAME, new String[]{LogInContract.LogInEntry.COLUMN_NAME_CAL}
                    , "(" +LogInContract.LogInEntry._ID + " == '" + id + "')", null,null, null, null);
            cursor.moveToNext();

            spinner.setSelection(Integer.parseInt(cursor.getString(0))-1);
        }
    }

    public void Active(View view){
        SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("ID",null);

        //Gets data repository in write mode
        LoginDbHelper mDbHelper = new LoginDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LogInContract.LogInEntry.COLUMN_NAME_CAL, Long.toString(spinner.getSelectedItemId()+ 1));
        db.update(LogInContract.LogInEntry.TABLE_NAME,values,"_id="+id,null);
    }

    public void Edit(View view){
        Intent intent = new Intent(this, Schedule_Activity.class);

        String calID = String.valueOf(spinner.getSelectedItemId()+1);
        intent.putExtra("calID", calID);

        startActivity(intent);
    }
    public void Exit(View view){
        finish();
    }

}
