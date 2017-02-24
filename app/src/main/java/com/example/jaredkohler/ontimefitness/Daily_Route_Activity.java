package com.example.jaredkohler.ontimefitness;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.View;

public class Daily_Route_Activity extends AppCompatActivity {
    long startTime;
    private final String TAG = getClass().getSimpleName();

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
    }

    public void Exit(View view) {
        finish();
    }
}
