package com.grum.raphael.projectmanagerclient.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.os.Vibrator;
import android.widget.Toast;

import com.grum.raphael.projectmanagerclient.MainActivity;

import java.sql.Time;
import java.util.Calendar;

/**
 * Created by Raphael on 22.11.2017.
 */

public class Autostart extends BroadcastReceiver {

    private static final int PERIOD = 15000000; // 5 minutes

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager alarmManager
                = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, OnAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, i, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 60000, PERIOD, pendingIntent);
    }

}
