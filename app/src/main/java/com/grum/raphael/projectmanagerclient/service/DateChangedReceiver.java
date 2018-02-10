package com.grum.raphael.projectmanagerclient.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Listens to the action of a change of the date and performs action if it detects such an action.
 */
public class DateChangedReceiver extends BroadcastReceiver {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        WakefulIntentService.acquireStaticLock(context);
        context.startService(new Intent(context, TodaysActionService.class));
    }
}
