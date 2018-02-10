package com.grum.raphael.projectmanagerclient.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This receiver class starts the service which asks for changes in the team environment.
 */
public class OnAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        WakefulIntentService.acquireStaticLock(context);
        context.startService(new Intent(context,
                DetectChangesService.class));
    }
}
