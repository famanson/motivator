package com.famanson.motivator;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;

/**
 * Created by famanson on 26/01/14.
 */
public class SimpleActivity extends Activity {

    private static String TAG = "FAMANSON_MOTIVATOR";

    private volatile boolean isSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        try {
            Process p = Runtime.getRuntime().exec(new String[] {"su"});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(View view) {
        if (!isSet) {
            setAlarm(this);
        }
    }

    public void cancel(View view) {
        if (isSet) {
            cancelAlarm(this);
        }
    }

    private void setAlarm(Context context)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pi); // Millisec * Second * Minute
        isSet = true;
    }

    private void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        isSet = false;
    }

}
