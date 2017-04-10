package com.example.jaredkohler.ontimefitness;

import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.CalendarContract;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Base64;
import android.widget.EditText;
import android.widget.Spinner;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.*;

/**
 * Created by Jared Kohler on 4/10/2017.
 */
@RunWith(AndroidJUnit4.class)
public class OnTimeFitnessTest{

    private Event_Activity event;
    private Create_Activity create;
    @Rule
    public ActivityTestRule<Event_Activity> mActivityRule = new ActivityTestRule<Event_Activity>(
            Event_Activity.class){
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            Intent result = new Intent(targetContext, Event_Activity.class);
            result.putExtra("ID", (long)-1);
            result.putExtra("calID", "1");
            return result;
        }
    };

    @Rule
    public ActivityTestRule<Create_Activity> mActivityRule2 = new ActivityTestRule<Create_Activity>(
            Create_Activity.class){
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            Intent result = new Intent(targetContext, Create_Activity.class);
            return result;
        }
    };

    public OnTimeFitnessTest() {
        super();
    }

    @Before
    public void setUp() throws Exception{
        event = mActivityRule.getActivity();
        create = mActivityRule2.getActivity();
    }

    public void tearDown(){
        event.finish();
        create.finish();
    }

    @Test
    public void retrieveListOfEvents(){
        CalendarOnTime cal = new CalendarOnTime("1");
        Cursor listOfEvents = cal.getListOfEvents(event);
        assertNotNull(listOfEvents);
    }

    @Test
    public void addNewEvent(){
        CalendarOnTime cal = new CalendarOnTime("1");
        Cursor listOfEvents= cal.getListOfEvents(event);
        int count = 0;
        while(listOfEvents.moveToNext()){
            count++;
        }

        EventOnTime newEvent = new EventOnTime(-1);
        ContentResolver cr = event.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, 0);
        values.put(CalendarContract.Events.DTEND, 0);
        values.put(CalendarContract.Events.TITLE, "Test");
        values.put(CalendarContract.Events.EVENT_LOCATION, "");
        values.put(CalendarContract.Events.CALENDAR_ID, "1");
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Chicago");

        newEvent.addOrUpdateEvent(event,cr,values);

        int newCount = 0;
        listOfEvents= cal.getListOfEvents(event);
        while(listOfEvents.moveToNext()){
            newCount++;
        }
        assertEquals(count+1, newCount);
    }

    @Test
    public void addUser() throws Exception {
        mActivityRule2.getActivity().runOnUiThread(new Runnable(){

            @Override
            public void run() {
                String user = String.valueOf(System.currentTimeMillis());
                EditText username = (EditText) create.findViewById(R.id.editUsername);
                EditText password = (EditText) create.findViewById(R.id.editPassword);
                EditText confirmPass = (EditText) create.findViewById(R.id.editConfirmPass);
                username.setText(user);
                password.setText("password");
                confirmPass.setText("password");

                try {
                    OnTimeFitnessTest.this.create.createUser(create.findViewById(R.id.editCreate));
                } catch (Exception e) {
                    e.printStackTrace();
                }


                LoginDbHelper mDbHelper = new LoginDbHelper(create);
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                //Selects the three columns ID, Username, and password to be returned after query
                String[] projection = {
                        LogInContract.LogInEntry._ID,
                        LogInContract.LogInEntry.COLUMN_NAME_TITLE,
                        LogInContract.LogInEntry.COLUMN_NAME_SUBTITLE
                };

                //Filters the results where username column = inputted username
                String selection = LogInContract.LogInEntry.COLUMN_NAME_TITLE + " = ?";
                String[] selectionArgs = {user};

                //Query the database with the settings set above
                Cursor cursor = db.query(
                        LogInContract.LogInEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );

                String got = "";
                while(cursor.moveToNext()){
                    String itemId = cursor.getString(cursor.getColumnIndex(LogInContract.LogInEntry.COLUMN_NAME_TITLE));
                    got = itemId;
                }
                cursor.close();

                assertEquals(user, got);
            }
        });

    }


    @Test
    public void encryptTest() throws GeneralSecurityException, UnsupportedEncodingException {
        String test = "newPass";
        String encrypt = create.encrypt(test);


        byte[] decoded = Base64.decode(encrypt,Base64.DEFAULT);
        assertEquals(test, new String(decoded, "UTF-8"));
    }

}
