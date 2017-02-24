package com.example.jaredkohler.ontimefitness;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.View;

import java.util.Timer;

public class Select_Schedule_Activity extends AppCompatActivity {
    private long startTime=0;
    private final String TAG = getClass().getSimpleName();

    @Override protected void onPause(){
        super.onPause();
        Log.d(TAG, "+++ onPause() +++");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "+++ onResume() +++");
    }
    @Override
    protected void onStop() {
        super.onStop();
        startTime = SystemClock.elapsedRealtime();
        Log.d(TAG, "+++ onStop() +++");
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
        setContentView(R.layout.activity_schedule_selector);

        Intent intent = getIntent();
        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_schedule);

    }

    public void Exit(View view){
        finish();
    }
}
