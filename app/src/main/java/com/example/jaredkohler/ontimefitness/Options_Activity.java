package com.example.jaredkohler.ontimefitness;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Options_Activity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.jaredkohler.ontimefitness.Schedule_Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
    }

    public void loadSchedule(View view){
        Intent intent = new Intent(this, Schedule_Activity.class);
        startActivity(intent);
    }

    public void loadSelectSchedule(View view){
        Intent intent = new Intent(this, Select_Schedule_Activity.class);
        startActivity(intent);
    }

    public void loadDailyRoute(View view){
        Intent intent = new Intent(this, Daily_Route_Activity.class);
        startActivity(intent);
    }

    public void loadBodySettings(View view){
        Intent intent = new Intent(this, Body_Settings_Activity.class);
        startActivity(intent);
    }
}
