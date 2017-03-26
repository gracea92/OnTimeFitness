package com.example.jaredkohler.ontimefitness;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class CalendarOnTime {

    private String calID;

    public CalendarOnTime(String calID){
        this.calID = calID;
    }

    public String getCalID(){
        return calID;
    }

    public Cursor getListOfEvents(AppCompatActivity context){

        // This is the select criteria
        String SELECTION = "((" +
                CalendarContract.Events.CALENDAR_ID + " == '" + calID + "') AND (" +
                CalendarContract.Events.DELETED + " != '1'))";

        return getListOfEvents(context, SELECTION);
    }

    public Cursor getListOfEvents(AppCompatActivity context, String SELECTION) {
        Cursor cur =null;

        // These are the Contacts rows that we will retrieve
        String[] PROJECTION = new String[] {CalendarContract.Events._ID,
                CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART};
        cur = generalQuery(context, PROJECTION, SELECTION, null, CalendarContract.Events.DTSTART + " ASC");
        return cur;
    }

    public Cursor generalQuery(AppCompatActivity context, String[] PROJECTION, String SELECTION, String[] SELECTARGS, String ORDER){
        ContentResolver cr = context.getContentResolver();
        Cursor cur =null;

        CalendarsHelper.requestCalendarPermission(context);
        if(ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            cur = cr.query(CalendarContract.Events.CONTENT_URI, PROJECTION, SELECTION, SELECTARGS, ORDER);

        }

        return cur;
    }
}
