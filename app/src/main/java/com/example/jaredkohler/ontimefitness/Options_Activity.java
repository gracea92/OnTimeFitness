package com.example.jaredkohler.ontimefitness;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Options_Activity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    public final static String EXTRA_MESSAGE = "com.example.jaredkohler.ontimefitness.Schedule_Activity";
    public static final String MyPREFERENCES = "MyPrefs";

    @Override protected void onPause(){
        super.onPause();
        Log.d(TAG, "+++ onPause() +++");
    }

    @Override protected void onResume(){
        super.onResume();
        Log.d(TAG, "+++ onResume() +++");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR},
                    259);

        }
        super.onCreate(savedInstanceState);
        Log.d(TAG, "+++ onCreate() +++");
        setContentView(R.layout.activity_options);
    }

    @Override
    protected void onStart(){
        super.onStart();
        launchStepService();
    }

    public void loadSchedule(View view){
        Intent intent = new Intent(this, Schedule_Activity.class);


        SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("ID",null);

        //Gets data repository in write mode
        LoginDbHelper mDbHelper = new LoginDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Cursor cursor  = db.query(LogInContract.LogInEntry.TABLE_NAME, new String[]{LogInContract.LogInEntry.COLUMN_NAME_CAL}
                , "(" +LogInContract.LogInEntry._ID + " == '" + id + "')", null,null, null, null);
        cursor.moveToNext();

        intent.putExtra("calID", cursor.getString(0));
        startActivity(intent);
    }

    public void loadSelectSchedule(View view){
        Intent intent = new Intent(this, Select_Schedule_Activity.class);
        startActivity(intent);
    }

    public void loadDailyRoute(View view){
        Intent intent = new Intent(this, Daily_Route_Activity.class);
        startActivity(intent);
    }

    public void loadBodySettings(View view){
        Intent intent = new Intent(this, Body_Settings_Activity.class);
        startActivity(intent);
    }

    public void launchStepService(){
        Intent intent = new Intent(this, StepService.class);
        startService(intent);
    }
}
