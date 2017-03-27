package com.example.jaredkohler.ontimefitness;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Andy on 3/25/2017.
 */

public class  StepService extends IntentService implements SensorEventListener{
    private LoginDbHelper mDbHelper;
    private SQLiteDatabase db;
    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;
    private Sensor mStepDetectorSensor;
    private final String TAG = getClass().getSimpleName();

    public StepService(){
        super("test-service");
    }

    @Override
    public void onCreate(){
        super.onCreate();

        //Gets data repository in write mode
        mDbHelper = new LoginDbHelper(this);
        db = mDbHelper.getWritableDatabase();

        //Initializes the step sensors
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (mStepCounterSensor != null){
            mSensorManager.registerListener(this, mStepCounterSensor, mSensorManager.SENSOR_DELAY_NORMAL);
        }else{
            SharedPreferences sharedPreferences = getSharedPreferences(Login_Activity.MyPREFERENCES, Context.MODE_PRIVATE);
            String id = sharedPreferences.getString("ID",null);

            ContentValues values = new ContentValues();
            values.put(LogInContract.LogInEntry.COLUMN_NAME_STEPS, -800);
            db.update(LogInContract.LogInEntry.TABLE_NAME, values, "_id="+id,null);
        }
        Toast.makeText(getApplicationContext(), "Service Loaded", Toast.LENGTH_SHORT).show();
    }


    public void onSensorChanged(SensorEvent event){
        Log.d(TAG,"+++ onSensorChanged() +++");
        //Gets the ID of the logged in user from Shared Preferences
        SharedPreferences sharedPreferences = getSharedPreferences(Login_Activity.MyPREFERENCES, Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("ID",null);

        //Updates the database with the new amount of steps
        ContentValues values = new ContentValues();
        values.put(LogInContract.LogInEntry.COLUMN_NAME_STEPS, Math.round(event.values[0]));
        db.update(LogInContract.LogInEntry.TABLE_NAME, values, "_id="+id,null);

    }

    @Override
    protected void onHandleIntent(Intent intent){

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }
}
