package com.example.jaredkohler.ontimefitness;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class EventOnTime {

    private long eventID;

    public EventOnTime(long eventID){
        this.eventID = eventID;
    }

    public long getEventID(){
        return eventID;
    }

    public Cursor getOnTimeEventInfo(AppCompatActivity context) {
        CalendarsHelper.requestCalendarPermission(context);
        Cursor cur = null;
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            ContentResolver cr = context.getContentResolver();

            cur = cr.query(CalendarContract.Events.CONTENT_URI, new String[]{CalendarContract.Events.TITLE,
                            CalendarContract.Events.EVENT_LOCATION, CalendarContract.Events.DTSTART},
                    "(" + CalendarContract.Events._ID + " = " + eventID + ")", null, null);
        }
        return cur;

    }

    public void addOrUpdateEvent(AppCompatActivity context,ContentResolver cr,ContentValues values ){
        CalendarsHelper.requestCalendarPermission(context);
        if(eventID == -1 && ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            eventID = Long.parseLong(uri.getLastPathSegment());
        } else {
            Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
            context.getContentResolver().update(updateUri, values, null, null);
        }
    }
}


