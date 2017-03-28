package com.example.jaredkohler.ontimefitness;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    Handler handler = new Handler();

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

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        Thread.sleep(10000);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                //Saves the id of the logged in user
                                SharedPreferences sharedpreferences = getSharedPreferences(Login_Activity.MyPREFERENCES, Context.MODE_PRIVATE);

                                String pastDate = sharedpreferences.getString("date","null");
                                String date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
                                if(!pastDate.equals(date)){
                                    //Gets the ID of the logged in user from Shared Preferences
                                    SharedPreferences sharedPreferences = getSharedPreferences(Login_Activity.MyPREFERENCES, Context.MODE_PRIVATE);
                                    String id = sharedPreferences.getString("ID",null);

                                    //Gets data repository in read mode
                                    final LoginDbHelper mDbHelper = new LoginDbHelper(StepService.this);
                                    SQLiteDatabase db = mDbHelper.getReadableDatabase();

                                    //Selects the current steps column to be returned after query
                                    final String[] projection = {
                                            LogInContract.LogInEntry.COLUMN_NAME_STEPS
                                    };
                                    //Filters the results where the id is equal to the logged in users id
                                    String selection = LogInContract.LogInEntry._ID + " = ?";
                                    String[] selectionArgs = {id};

                                    //Query the database with the settings set above
                                    Cursor cursor = db.query(
                                            LogInContract.LogInEntry.TABLE_NAME,
                                            projection,
                                            selection,
                                            selectionArgs,
                                            null,
                                            null,
                                            null
                                    );

                                    //Gets the steps from the previous day
                                    cursor.moveToNext();
                                    String prevSteps = cursor.getString(cursor.getColumnIndex(LogInContract.LogInEntry.COLUMN_NAME_STEPS));

                                    //Updates the previous day steps and resets the current day steps
                                    ContentValues values = new ContentValues();
                                    values.put(LogInContract.LogInEntry.COLUMN_NAME_PREVSTEPS, prevSteps);
                                    values.put(LogInContract.LogInEntry.COLUMN_NAME_STEPS, "0");
                                    db.update(LogInContract.LogInEntry.TABLE_NAME,values,"_id="+id,null);

                                    //Updates the saved date to the current date.
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("date", date);
                                    editor.commit();
                                }
                            }
                        });
                    }catch(Exception e){

                    }
                }
            }
        }).start();
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
