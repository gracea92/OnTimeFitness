package com.example.jaredkohler.ontimefitness;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
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


    private CalendarOnTime cal;


    //For launching edit or remove on listClick.
    private int Pos;
    private View View;
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

        cal = new CalendarOnTime(getIntent().getExtras().getString("calID"));
        TextView current = (TextView) findViewById(R.id.textSchedule);
        current.setText("Current Schedule " + cal.getCalID());

        // .For the cursor adapter, specify which columns go into which views
        String[] fromColumns = {CalendarContract.Events.TITLE};
        int[] toViews = {android.R.id.text1}; // The TextView in simple_list_item_1
        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        Cursor cur = cal.getListOfEvents(this);
        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, cur ,
                fromColumns, toViews, 0);

        ListView list = (ListView) findViewById(R.id.listEvent);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick (AdapterView < ? > adapter, View view,int position, long arg){

                    View = view;
                    Pos = position;
                    CharSequence options[] = new CharSequence[] {"Edit", "Delete", "Cancel"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Edit or Delete event?");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            long i = mAdapter.getItemId(Pos);
                            if(which == 0) {
                                Intent intent = new Intent(View.getContext(), Event_Activity.class);
                                intent.putExtra("ID", i);
                                intent.putExtra("calID", cal.getCalID());
                                startActivity(intent);
                            } else if(which == 1) {
                                Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, i);
                                int rows = getContentResolver().delete(deleteUri, null, null);
                                Log.d(TAG, "Deleted rows: " + rows);
                            }
                        }
                    });
                    builder.show();


                }
            }
        );

        Log.d(TAG, "+++ onCreate +++");

    }

    public void addEvent(View view){
        Intent intent = new Intent(this, Event_Activity.class);
        long i = -1;
        intent.putExtra("ID", i);
        intent.putExtra("calID", cal.getCalID());
        startActivity(intent);
    }

    public void Exit(View view){
        finish();
    }

}
