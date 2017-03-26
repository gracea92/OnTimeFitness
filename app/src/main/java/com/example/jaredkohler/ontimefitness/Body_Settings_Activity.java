package com.example.jaredkohler.ontimefitness;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Body_Settings_Activity extends AppCompatActivity {
    long startTime;
    private final String TAG = getClass().getSimpleName();
    String savedGender;
    String savedWeight;
    String savedHeight;

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "+++ onStop +++");
        startTime = SystemClock.elapsedRealtime();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "+++ onRestart +++");
        if(startTime + 5000 <SystemClock.elapsedRealtime()){
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "+++ onCreate +++");
        setContentView(R.layout.activity_body);

        Intent intent = getIntent();
        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_schedule);

        Spinner gender = (Spinner) findViewById(R.id.spinner);
        EditText weight = (EditText) findViewById(R.id.editWeight);
        EditText height = (EditText) findViewById(R.id.editHeight);
        Button save = (Button) findViewById(R.id.editCreate);

        //Sets up the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        gender.setAdapter(adapter);

        //Gets the ID of the logged in user from Shared Preferences
        SharedPreferences sharedPreferences = getSharedPreferences(Login_Activity.MyPREFERENCES, Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("ID",null);

        //Gets data repository in read mode
        final LoginDbHelper mDbHelper = new LoginDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        /*
            This part is to set the gender spinner to match the user's gender
         */

        //Selects the gender column to be returned after query
        final String[] projection = {
                LogInContract.LogInEntry.COLUMN_NAME_GENDER
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

        //Gets the gender from the result of the query
        if(cursor.moveToNext()){
            savedGender = cursor.getString(cursor.getColumnIndex(LogInContract.LogInEntry.COLUMN_NAME_GENDER));
        }else{
            Toast.makeText(this, "Failed to get gender", Toast.LENGTH_SHORT).show();
            //Loads the login page if the query returns nothing
            intent = new Intent(this, Login_Activity.class);
            startActivity(intent);
        }

        //Changes the selected gender to Female if the user's gender is female
        if(savedGender.equals("Female")){
            gender.setSelection(1);
        }

        /*
            This part is to set the weight text field equal to the user's weight
         */

        //Changes the projection to return the weight column
        projection[0] = LogInContract.LogInEntry.COLUMN_NAME_WEIGHT;

        //Query the database with the new column
        cursor = db.query(
                LogInContract.LogInEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        //Gets the weight from the result of the query
        if(cursor.moveToNext()){
            savedWeight = cursor.getString(cursor.getColumnIndex(LogInContract.LogInEntry.COLUMN_NAME_WEIGHT));
        }else{
            //Loads the login page if the query returns nothing
            intent = new Intent(this, Login_Activity.class);
            startActivity(intent);
        }

        //Sets the displayed weight equal to the user's weight
        weight.setText(savedWeight);

        /*
            This part is to set the height text field equal to the user's height
         */

        //Changes the projection to return the height column
        projection[0] = LogInContract.LogInEntry.COLUMN_NAME_HEIGHT;

        //Query the database with the new  column
        cursor = db.query(
                LogInContract.LogInEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        //Gets the height from the result of the query
        if(cursor.moveToNext()){
            savedHeight = cursor.getString(cursor.getColumnIndex(LogInContract.LogInEntry.COLUMN_NAME_HEIGHT));
        }else{
            //Loads the login page if teh query returns nothing
            intent = new Intent(this, Login_Activity.class);
            startActivity(intent);
        }

        //Sets the displayed height equal to the user's height
        height.setText(savedHeight);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Gets data repository in write mode
                LoginDbHelper mDbHelper = new LoginDbHelper(Body_Settings_Activity.this);
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                Spinner gender = (Spinner) findViewById(R.id.spinner);
                EditText weight = (EditText) findViewById(R.id.editWeight);
                EditText height = (EditText) findViewById(R.id.editHeight);

                //Gets the ID of the logged in user from Shared Preferences
                SharedPreferences sharedPreferences = getSharedPreferences(Login_Activity.MyPREFERENCES, Context.MODE_PRIVATE);
                String id = sharedPreferences.getString("ID",null);

                //Updates the database to match the new body settings
                ContentValues values = new ContentValues();
                values.put(LogInContract.LogInEntry.COLUMN_NAME_GENDER, gender.getSelectedItem().toString());
                values.put(LogInContract.LogInEntry.COLUMN_NAME_WEIGHT, weight.getText().toString());
                values.put(LogInContract.LogInEntry.COLUMN_NAME_HEIGHT, height.getText().toString());
                db.update(LogInContract.LogInEntry.TABLE_NAME,values,"_id="+id,null);

            }
        });
    }

    public void Exit(View view) {
        finish();
    }
}
