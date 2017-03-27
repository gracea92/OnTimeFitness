package com.example.jaredkohler.ontimefitness;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Daily_Route_Activity extends AppCompatActivity {
    long startTime;
    private final String TAG = getClass().getSimpleName();
    Handler handler = new Handler();

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
        Log.d(TAG, "+++ onCreate() +++");
        setContentView(R.layout.activity_route);

        Intent intent = getIntent();
        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_schedule);

        TextView steps = (TextView) findViewById(R.id.textCurrent);

        //Gets the ID of the logged in user from Shared Preferences
        SharedPreferences sharedPreferences = getSharedPreferences(Login_Activity.MyPREFERENCES, Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("ID",null);

        //Gets data repository in read mode
        final LoginDbHelper mDbHelper = new LoginDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Selects the steps column to be returned after query
        final String[] projection = {
                LogInContract.LogInEntry.COLUMN_NAME_STEPS
        };

        //Filters the results where the id is equal to the logged in user's id
        String selection = LogInContract.LogInEntry._ID + " = ?";
        String[] selectionArgs = {id};

        //Query the database with the setting set above
        Cursor cursor = db.query(
                LogInContract.LogInEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        //Gets the steps from teh result of the query
        if(cursor.moveToNext()){
            steps.setText(cursor.getString(cursor.getColumnIndex(LogInContract.LogInEntry.COLUMN_NAME_STEPS)));
        }else{
            Toast.makeText(this, "Failed to get number of steps", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, Login_Activity.class);
            startActivity(intent);
        }

        //Creates a thread to refresh the step counter every 10 seconds.
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        //Refreshes every 10 seconds
                        Thread.sleep(10000);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                TextView steps = (TextView) findViewById(R.id.textCurrent);

                                //Gets the ID of the logged in user from Shared Preferences
                                SharedPreferences sharedPreferences = getSharedPreferences(Login_Activity.MyPREFERENCES, Context.MODE_PRIVATE);
                                String id = sharedPreferences.getString("ID",null);

                                //Gets data repository in read mode
                                final LoginDbHelper mDbHelper = new LoginDbHelper(Daily_Route_Activity.this);
                                SQLiteDatabase db = mDbHelper.getReadableDatabase();

                                //Selects the steps column to be returned after query
                                final String[] projection = {
                                        LogInContract.LogInEntry.COLUMN_NAME_STEPS
                                };

                                //Filters the results where the id is equal to the logged in user's id
                                String selection = LogInContract.LogInEntry._ID + " = ?";
                                String[] selectionArgs = {id};

                                Cursor cursor = db.query(
                                        LogInContract.LogInEntry.TABLE_NAME,
                                        projection,
                                        selection,
                                        selectionArgs,
                                        null,
                                        null,
                                        null
                                );

                                if(cursor.moveToNext()){
                                    steps.setText(cursor.getString(cursor.getColumnIndex(LogInContract.LogInEntry.COLUMN_NAME_STEPS)));
                                }else{

                                }
                            }
                        });

                    } catch(Exception e){

                    }
                }
            }
        }).start();
    }

    public void Exit(View view) {
        finish();
    }
}
