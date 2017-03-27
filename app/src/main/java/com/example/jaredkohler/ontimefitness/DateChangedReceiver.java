package com.example.jaredkohler.ontimefitness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Andy on 3/26/2017.
 */

public class DateChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        Log.d(getClass().getSimpleName(),"Date Changed????");
        if(intent.getAction().equals(Intent.ACTION_DATE_CHANGED)){
            Log.d(getClass().getSimpleName(),"+++ ACTION_DATE_CHANGED received");
        }
    }
}
