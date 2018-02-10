package com.grum.raphael.projectmanagerclient.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This receiver class starts the service which asks for new messages of the user's chats.
 */
public class MessageAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        WakefulIntentService.acquireStaticLock(context);
        context.startService(new Intent(context, GetNewMessagesService.class));
    };
}
