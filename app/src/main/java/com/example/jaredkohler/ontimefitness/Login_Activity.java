package com.example.jaredkohler.ontimefitness;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class Login_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    public void login(View view){
        EditText username = (EditText) findViewById(R.id.editName);
        EditText password = (EditText)findViewById(R.id.editPassword);

        LoginDbHelper mDbHelper = new LoginDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String[] projection = {
                LogInContract.LogInEntry._ID,
                LogInContract.LogInEntry.COLUMN_NAME_TITLE,
                LogInContract.LogInEntry.COLUMN_NAME_SUBTITLE
        };

        String selection = LogInContract.LogInEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = {username.getText().toString()};

        Cursor cursor = db.query(
                LogInContract.LogInEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()){
            long itemId = cursor.getLong(cursor.getColumnIndex(LogInContract.LogInEntry._ID));
            itemIds.add(itemId);
        }
        cursor.close();
        if(itemIds.isEmpty()){
            Toast.makeText(getApplicationContext(), "Sorry that username is not recognized.", Toast.LENGTH_SHORT).show();
        }else{
            selection = LogInContract.LogInEntry.COLUMN_NAME_TITLE + " = \"" + username.getText().toString()
                    + "\" AND " +LogInContract.LogInEntry.COLUMN_NAME_SUBTITLE + " = ?";
            selectionArgs[0]  = password.getText().toString();

            cursor = db.query(
                    LogInContract.LogInEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );
            List passwordIds = new ArrayList<>();
            while(cursor.moveToNext()){
                long passwordId = cursor.getLong(cursor.getColumnIndex(LogInContract.LogInEntry._ID));
                passwordIds.add(passwordId);
            }
            cursor.close();

            if(passwordIds.isEmpty()){
                Toast.makeText(getApplicationContext(), "Sorry that password does not match the username", Toast.LENGTH_SHORT).show();
            }else{
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
