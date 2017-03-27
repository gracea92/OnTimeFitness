package com.example.jaredkohler.ontimefitness;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.services.directions.v5.models.DirectionsRoute;

public class Daily_Route_Activity extends AppCompatActivity {
    long startTime;
    private final String TAG = getClass().getSimpleName();
    Handler handler = new Handler();

    private MapView mapView;
    private MapboxMap map;
    private DirectionsRoute currentRoute;

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



        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    279);

        }
        MapboxAccountManager.start(this, getString(R.string.access_token));
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    public void Exit(View view) {
        finish();
    }
}
