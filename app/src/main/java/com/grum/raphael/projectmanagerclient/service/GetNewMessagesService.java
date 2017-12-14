package com.grum.raphael.projectmanagerclient.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;
import com.grum.raphael.projectmanagerclient.tasks.GetNewMessagesTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

/**
 * Created by Raphael on 04.12.2017.
 */

public class GetNewMessagesService extends WakefulIntentService {

    private String content = "";

    public GetNewMessagesService() {
        super(GetNewMessagesService.class.getName());
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GetNewMessagesService(String name) {
        super(name);
    }

    @Override
    void doWakefulWork(Intent intent) {
        SharedPreferences settings = getSharedPreferences("MyFile", 0);
        String username = settings.getString("username", "null");
        getNewMessages(username);
        }

    private void getNewMessages(String username) {
        if (CheckInternet.isNetworkAvailable(getApplicationContext())) {
            Calendar currentTime = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            String timestamp = formatter.format(currentTime.getTime());
            String[] params = new String[]{MainActivity.URL + "messages/new", username, timestamp};
            GetNewMessagesTask getNewMessagesTask = new GetNewMessagesTask();
            try {
                JSONObject result = getNewMessagesTask.execute(params).get();
                if (result != null) {
                    String success = result.getString("success");
                    if (success.equals("true")) {
                        JSONArray chatsWithNewMessages = result.getJSONArray("chats");
                        ArrayList<String> chats = new ArrayList<>();
                        for (int i = 0; i < chatsWithNewMessages.length(); i++) {
                            String chatName = chatsWithNewMessages.getString(i);
                            chats.add(chatName);
                        }
                        editContent(chats);
                        if (content.length() != 0) {
                            Intent notificationIntent = new Intent(getApplicationContext(),
                                    MainActivity.class);
                            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            PendingIntent intent = PendingIntent.getActivity(getApplicationContext(),
                                    0, notificationIntent, 0);
                            NotificationManager notificationManager =
                                    (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
                            NotificationCompat.Builder builder =
                                    new NotificationCompat.Builder(getApplicationContext())
                                            .setSmallIcon(R.drawable.ic_notification)
                                            .setContentTitle("Sie haben neue Nachrichten!")
                                            .setContentText(content)
                                            .setDefaults(Notification.DEFAULT_ALL)
                                            .setStyle(new NotificationCompat.BigTextStyle()
                                                    .bigText(content))
                                            .setContentIntent(intent)
                                            .setAutoCancel(true);
                            Notification notification = builder.build();
                            notificationManager.notify(0, notification);
                        }
                    }
                }
            } catch (InterruptedException | ExecutionException | JSONException e) {
                stopSelf();
            }
        } else {
            stopSelf();
        }
        stopSelf();
    }

    private void editContent(ArrayList<String> chats) {
        if (chats.size() != 0) {
            content = "Sie haben neue Nachrichten bei folgenden Chats:\n";
            for (int i = 0; i < chats.size(); i++) {
                if (i != chats.size() - 1) {
                    content += chats.get(i) + ",\n";
                } else {
                    content += chats.get(i);
                }
            }
        }
    }
}
