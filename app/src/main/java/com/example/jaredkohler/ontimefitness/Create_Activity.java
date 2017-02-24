package com.example.jaredkohler.ontimefitness;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Create_Activity extends AppCompatActivity {
    long startTime;

    @Override
    protected void onStop() {
        super.onStop();
        startTime = SystemClock.elapsedRealtime();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(startTime + 5000 <SystemClock.elapsedRealtime()){
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_);
    }

    public void createUser(View view){
        EditText username = (EditText) findViewById(R.id.editUsername);
        EditText password = (EditText) findViewById(R.id.editPassword);
        EditText confirmPass = (EditText) findViewById(R.id.editConfirmPass);

        if(username.getText().toString().equals("UserName") || username.getText().toString().equals(getString(R.string.username).toString()) || username.getText().toString() == null){
            Toast.makeText(getApplicationContext(), "Username is already taken, please use a different one.", Toast.LENGTH_SHORT).show();
        }else if(password.getText().toString().equals("Password") || !password.getText().toString().equals(confirmPass.getText().toString())){
            Toast.makeText(getApplicationContext(), "Passwords do not match, please try again", Toast.LENGTH_SHORT).show();
        }else{
            LoginDbHelper mDbHelper = new LoginDbHelper(this);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(LogInContract.LogInEntry.COLUMN_NAME_TITLE, username.getText().toString());
            values.put(LogInContract.LogInEntry.COLUMN_NAME_SUBTITLE, password.getText().toString());
            long newRowId = db.insert(LogInContract.LogInEntry.TABLE_NAME, null, values);

            Intent intent = new Intent(this, Login_Activity.class);
            startActivity(intent);
        }
    }

    public void Exit(View view){
        finish();
    }
}
