package com.example.jaredkohler.ontimefitness;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class Create_Activity extends AppCompatActivity {
    long startTime;
    private final String TAG = getClass().getSimpleName();
    private int userHeight;
    private int userWeight;
    private int goal;
    private double bmi;

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
        setContentView(R.layout.activity_create_);

        //Sets up the spinner so user can choose their gender
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
    }

    public void createUser(View view) throws Exception{
        //Gets the user inputted settings
        EditText username = (EditText) findViewById(R.id.editUsername);
        EditText password = (EditText) findViewById(R.id.editPassword);
        EditText confirmPass = (EditText) findViewById(R.id.editConfirmPass);
        EditText weight = (EditText) findViewById(R.id.editWeight);
        EditText height = (EditText) findViewById(R.id.editHeight);
        Spinner gender = (Spinner) findViewById(R.id.spinner);


        //Gets data repository in write mode
        LoginDbHelper mDbHelper = new LoginDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Selects the three columns ID, Username, and password to be returned after query
        String[] projection = {
                LogInContract.LogInEntry._ID,
                LogInContract.LogInEntry.COLUMN_NAME_TITLE,
                LogInContract.LogInEntry.COLUMN_NAME_SUBTITLE
        };

        //Filters the results where username column = inputted username
        String selection = LogInContract.LogInEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = {username.getText().toString()};

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

        //Gets the list of ids from the result of the query
        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()){
            long itemId = cursor.getLong(cursor.getColumnIndex(LogInContract.LogInEntry._ID));
            itemIds.add(itemId);
        }
        cursor.close();

        //Checks if the username is unique, and that the passwords match
        if(!itemIds.isEmpty() || username.getText().toString() == null){
            Toast.makeText(getApplicationContext(), "Username is already taken, please use a different one.", Toast.LENGTH_SHORT).show();
        }else if(!password.getText().toString().equals(confirmPass.getText().toString())){
            Toast.makeText(getApplicationContext(), "Passwords do not match, please try again", Toast.LENGTH_SHORT).show();
        }else{
            //Adds the user's inputted values into
            ContentValues values = new ContentValues();
            userHeight = Integer.parseInt(height.getText().toString());
            userWeight = Integer.parseInt(weight.getText().toString());
            bmi = ((double) userWeight/((double) userHeight* (double) userHeight))*703;
            int goal = 10000;
            if(bmi<18.5){
                goal -= 500;
            }else if(bmi >= 25 && bmi < 30){
                goal += 500;
            }else if(bmi > 30){
                goal += 1000;
            }

            String encoded = encrypt(password.getText().toString());
            values.put(LogInContract.LogInEntry.COLUMN_NAME_TITLE, username.getText().toString());
            values.put(LogInContract.LogInEntry.COLUMN_NAME_SUBTITLE, encoded);
            values.put(LogInContract.LogInEntry.COLUMN_NAME_WEIGHT, weight.getText().toString());
            values.put(LogInContract.LogInEntry.COLUMN_NAME_HEIGHT, height.getText().toString());
            values.put(LogInContract.LogInEntry.COLUMN_NAME_GENDER, gender.getSelectedItem().toString());
            values.put(LogInContract.LogInEntry.COLUMN_NAME_STEPS, "0");
            values.put(LogInContract.LogInEntry.COLUMN_NAME_CAL, "1");
            values.put(LogInContract.LogInEntry.COLUMN_NAME_PREVSTEPS, "0");
            values.put(LogInContract.LogInEntry.COLUMN_NAME_GOAL, goal);
            long newRowId = db.insert(LogInContract.LogInEntry.TABLE_NAME, null, values);

            //Loads the login activity
            Intent intent = new Intent(this, Login_Activity.class);
            startActivity(intent);
        }
    }

    public void Exit(View view){
        finish();
    }

    private static String encrypt(String toBeEncrypt) throws GeneralSecurityException, UnsupportedEncodingException {
        byte[] data = null;
        try {
            data = toBeEncrypt.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        return Base64.encodeToString(data, Base64.DEFAULT);
    }
}
