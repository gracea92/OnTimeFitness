package com.example.jaredkohler.ontimefitness;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class Login_Activity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    public static final String MyPREFERENCES = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "+++ onCreate() +++");
        setContentView(R.layout.activity_login);
    }


    public void login(View view){
        //Gets the user inputted username and password
        EditText username = (EditText) findViewById(R.id.editName);
        EditText password = (EditText)findViewById(R.id.editPassword);

        //Gets data repository in write mode
        LoginDbHelper mDbHelper = new LoginDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Selects the three columns ID, Username, and password to be returned after query
        String[] projection = {
                LogInContract.LogInEntry._ID,
                LogInContract.LogInEntry.COLUMN_NAME_TITLE,
                LogInContract.LogInEntry.COLUMN_NAME_SUBTITLE,
                LogInContract.LogInEntry.COLUMN_NAME_CAL
        };

        //Filters the results where username column = inputted username
        String selection = LogInContract.LogInEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = {username.getText().toString()};

        //Query the database with settings from above
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

        //Checks if username is recognize
        if(itemIds.isEmpty()){
            Toast.makeText(getApplicationContext(), "Sorry that username is not recognized.", Toast.LENGTH_SHORT).show();
        }else{
            //Changes the selection to username = input username and password = input password
            selection = LogInContract.LogInEntry.COLUMN_NAME_TITLE + " = \"" + username.getText().toString()
                    + "\" AND " +LogInContract.LogInEntry.COLUMN_NAME_SUBTITLE + " = ?";
            selectionArgs[0]  = password.getText().toString();

            //Query the database with new settings
            cursor = db.query(
                    LogInContract.LogInEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            //Gets the list of ids from the query and list of associated calIDs
            List passwordIds = new ArrayList<>();
            List calIDs = new ArrayList<>();
            while(cursor.moveToNext()){
                long passwordId = cursor.getLong(cursor.getColumnIndex(LogInContract.LogInEntry._ID));
                passwordIds.add(passwordId);
                String calID = cursor.getString(cursor.getColumnIndex(LogInContract.LogInEntry.COLUMN_NAME_CAL));
                calIDs.add(calID);
            }
            cursor.close();

            //Checks if the password was valid
            if(passwordIds.isEmpty()){
                Toast.makeText(getApplicationContext(), "Sorry that password does not match the username", Toast.LENGTH_SHORT).show();
            }else{
                //Saves the id of the logged in user
                SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("ID",passwordIds.get(0).toString());

                editor.putString("calID", calIDs.get(0).toString());
                editor.commit();

                //Loads the options activity
                Intent intent = new Intent(this, Options_Activity.class);
                startActivity(intent);
            }
        }

    }

    public void loadCreateUser(View view){
        Intent intent = new Intent(this, Create_Activity.class);
        startActivity(intent);
    }

    public void loadDb(View view){
        Intent dbmanager = new Intent(this, AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }
}
