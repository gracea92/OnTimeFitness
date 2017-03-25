package com.example.jaredkohler.ontimefitness;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class Schedule_Activity extends AppCompatActivity {
    private long startTime=0;
    private final String TAG = getClass().getSimpleName();


    // This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;

    // These are the Contacts rows that we will retrieve
    static final String[] PROJECTION = new String[] {CalendarContract.Events._ID,
            CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART};

    String calID;

    // This is the select criteria
    String SELECTION;

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
        setContentView(R.layout.activity_schedule);

        calID = getIntent().getExtras().getString("calID");

        TextView current = (TextView) findViewById(R.id.textSchedule);

        current.setText("Current Schedule " + calID);
        SELECTION = "(" +
                CalendarContract.Events.CALENDAR_ID + " == '" + calID + "' )";
        Log.d(TAG, "+++ onCreate +++");


        Intent intent = getIntent();
        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_schedule);
        ListView list = (ListView) findViewById(R.id.listEvent);


        // For the cursor adapter, specify which columns go into which views
        String[] fromColumns = {CalendarContract.Events.TITLE};
        int[] toViews = {android.R.id.text1}; // The TextView in simple_list_item_1



        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR},
                    259);

        }
        cur = cr.query(CalendarContract.Events.CONTENT_URI, PROJECTION, SELECTION, null, CalendarContract.Events.DTSTART + " ASC");


        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, cur ,
                fromColumns, toViews, 0);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick (AdapterView < ? > adapter, View view,int position, long arg){

                    CharSequence colors[] = new CharSequence[] {"red", "green", "blue", "black"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Pick a color");
                    builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // the user clicked on colors[which]
                        }
                    });
                    builder.show();

                    long i = mAdapter.getItemId(position);
                    Intent intent = new Intent(view.getContext(), Event_Activity.class);
                    intent.putExtra("ID", i);
                    intent.putExtra("calID", calID);
                    startActivity(intent);
                }
            }
        );

    }

    public void addEvent(View view){
        Intent intent = new Intent(this, Event_Activity.class);
        long i = -1;
        intent.putExtra("ID", i);
        intent.putExtra("calID", calID);
        startActivity(intent);
    }

    public void Exit(View view){
        finish();
    }

}
