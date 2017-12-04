package com.grum.raphael.projectmanagerclient.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Created by Raphael on 04.12.2017.
 */

public class MessageBroadcastReceiver extends BroadcastReceiver {

    private final int PERIOD = 120000; // 2 minutes

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager alarmManager
                = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, MessageAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, i, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 60000, PERIOD, pendingIntent);
    }

}
