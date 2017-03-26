package com.example.jaredkohler.ontimefitness;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarsHelper {
    public static final String MyPREFERENCES = "MyPrefs";

    public static String getCurCalID(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("ID",null);


        LoginDbHelper mDbHelper = new LoginDbHelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor  = db.query(LogInContract.LogInEntry.TABLE_NAME, new String[]{LogInContract.LogInEntry.COLUMN_NAME_CAL}
                , "(" +LogInContract.LogInEntry._ID + " == '" + id + "')", null,null, null, null);
        cursor.moveToNext();
        return cursor.getString(0);
    }

    public static void requestCalendarPermission(AppCompatActivity context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR},
                    259);

        }
    }

    public static List<Long> listOfCalendars(AppCompatActivity context){
        List<Long> list = new ArrayList<Long>();

        // Run query
        Cursor cur = null;
        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String[] selection = new String[]{CalendarContract.Calendars._ID};

        requestCalendarPermission(context);
        if(ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            // Submit the query and get a Cursor object back.
            cur = cr.query(uri, selection, CalendarContract.Calendars.VISIBLE + " =1", null, CalendarContract.Calendars._ID + " ASC");


            while (cur.moveToNext()) {
                long calID = cur.getLong(0);
                list.add(calID);


            }
        }
        return list;
    }

    public static String fromLongToFormatDate(Long time){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm");
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(time);

        return formatter.format(calendar.getTime());
    }

    public static Long fromFormatToLongDate(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm");
        long datetime = 0;
        try {
            Date date = sdf.parse(time);
            datetime = date.getTime();
        }catch(ParseException e) {
            Log.d("+++CalendarsHelper +++:", "Error failed converting to long version of datetime");
        }

        return datetime;
    }

    public Long currentDate(){
        String date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        return fromFormatToLongDate(date + " 00:00");
    }

    public Long endOfCurrentDate(){
        return currentDate() + 86499999;
    }
}
