package com.grum.raphael.projectmanagerclient.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.GetTeamsNews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * This class is used for the background communication with the server. It forces the device to
 * stay awake during the whole process.
 */
abstract public class WakefulIntentService extends IntentService {


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public WakefulIntentService(String name) {
        super(name);
    }

    /**
     * Is used to perform the communication action with the server inside a background thread.
     * Is defined by the service which has to perform the action. Basically uses HTTP to request
     * data om the server.
     *
     * @param intent The abstract information about the action to perform.
     */
    abstract void doWakefulWork(Intent intent);

    private static PowerManager.WakeLock lockStatic = null;

    public static final String LOCK_NAME_STATIC
            = "com.grum.raphael.projectmanagerclient.service.DetectChangesService.Static";
    // for debugging purpose


    /**
     * Is used to force the device to stay awake during the process. Increases power usage.
     *
     * @param context
     */
    public static void acquireStaticLock(Context context) {
        getLock(context).acquire();
    }

    /**
     * Returns the device's PowerManager.WakeLock to be able to force the devi ce to stay awake.
     *
     * @param context The interface to access the global resources of the application.
     *
     * @return The PowerManager.WakeLock. The device's power manager to force it staying awake.
     */
    synchronized private static PowerManager.WakeLock getLock(Context context) {
        if (lockStatic == null) {
            PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    LOCK_NAME_STATIC);
            lockStatic.setReferenceCounted(true);
        }
        return lockStatic;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final protected void onHandleIntent(@Nullable Intent intent) {
        try {
            doWakefulWork(intent);
        } finally {
            getLock(getBaseContext()).release();
        }
    }
}
