package com.grum.raphael.projectmanagerclient.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Raphael on 23.11.2017.
 */

public class OnAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        WakefulIntentService.acquireStaticLock(context);
        context.startService(new Intent(context, AppService.class));
    }
}
