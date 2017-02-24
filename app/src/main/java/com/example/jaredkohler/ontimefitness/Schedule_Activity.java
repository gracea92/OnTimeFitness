package com.example.jaredkohler.ontimefitness;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

public class Schedule_Activity extends AppCompatActivity {
    private long startTime=0;

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
        setContentView(R.layout.activity_schedule);

        Intent intent = getIntent();
        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_schedule);
    }


}
