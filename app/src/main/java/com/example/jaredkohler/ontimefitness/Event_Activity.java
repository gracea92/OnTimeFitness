package com.example.jaredkohler.ontimefitness;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.provider.CalendarContract.Events;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.content.ContentValues;
import android.net.Uri;

import android.content.ContentResolver;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Event_Activity extends AppCompatActivity {
    private long startTime=0;
    private final String TAG = getClass().getSimpleName();

    EditText name;
    EditText location;
    TextView time;
    TextView date;
    long calID;
    EventOnTime event;
    TextView setTime;
    TextView setDate;

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
        event = new EventOnTime(getIntent().getExtras().getLong("ID"));
        calID = Long.parseLong(getIntent().getExtras().getString("calID"));

        Log.d(TAG, "+++ onCreate +++");
        setContentView(R.layout.activity_event);
        name = (EditText) findViewById(R.id.editName);
        location = (EditText)findViewById(R.id.editLocation);
        //time = (EditText)findViewById((R.id.editTime));
        //date = (EditText) findViewById((R.id.editDate));
        time = (TextView) findViewById(R.id.textTime);
        date = (TextView) findViewById(R.id.textDate);

        if(event.getEventID() != -1) {
            Cursor cur = event.getOnTimeEventInfo(this);
            cur.moveToNext();

            //process cursor
            name.setText(cur.getString(0));
            location.setText(cur.getString(1));
            String dateTime = CalendarsHelper.fromLongToFormatDate(Long.parseLong(cur.getString(2)));

            date.setText(dateTime.substring(0, dateTime.indexOf(' ')));
            time.setText(dateTime.substring(dateTime.indexOf(' ') + 1));
        }

    }

    public void save(View view){

        String timeString = time.getText().toString();
        if(timeString.equals("")){
            Toast.makeText(this,"You must enter a time.", Toast.LENGTH_SHORT).show();
            return;
        }

        int startHour = Integer.parseInt(timeString.substring(0, timeString.indexOf(':')));
        int startMinute = Integer.parseInt(timeString.substring(timeString.indexOf(':') +1));

        String fullDate = date.getText().toString();
        if(fullDate.equals("")){
            Toast.makeText(this,"You must enter a date.", Toast.LENGTH_SHORT).show();
            return;
        }
        int year = Integer.parseInt(fullDate.substring(0, fullDate.indexOf('/')));
        fullDate = fullDate.substring(fullDate.indexOf('/')+1);
        int month = Integer.parseInt(fullDate.substring(0, fullDate.indexOf('/')));
        month--;
        fullDate = fullDate.substring(fullDate.indexOf('/')+1);
        int day = Integer.parseInt(fullDate);

        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(year, month, day, startHour, startMinute);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(year, month, day, startHour + 1, startMinute);
        endMillis = endTime.getTimeInMillis();

        if(name.getText().toString().equals("")){
            Toast.makeText(this,"You must enter a name.", Toast.LENGTH_SHORT).show();
            return;
        }else if(location.getText().toString().equals("")){
            Toast.makeText(this,"You must enter a location.", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, startMillis);
        values.put(Events.DTEND, endMillis);
        values.put(Events.TITLE, name.getText().toString());
        values.put(Events.EVENT_LOCATION, location.getText().toString());
        values.put(Events.CALENDAR_ID, calID);
        values.put(Events.EVENT_TIMEZONE, "America/Chicago");

        event.addOrUpdateEvent(this,cr,values);
        Toast.makeText(this,"New event added to schedule.", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void exit(View view){
        finish();
    }

    public void showTimePickerDialog(View v){
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(),"timePicker");
    }

    public void showDatePickerDialog(View v){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            setTime = (TextView) getActivity().findViewById(R.id.textTime);
            setTime.setText(String.valueOf(hourOfDay)+":"+String.valueOf(minute));
        }

    }

    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            setDate = (TextView) getActivity().findViewById(R.id.textDate);
            String setMonth = String.valueOf(month+1%12);
            if(setMonth.length() == 1){
                setMonth = "0" + setMonth;
            }
            setDate.setText(String.valueOf(year) + "/" + setMonth + "/" + String.valueOf(day));
        }
    }
}
