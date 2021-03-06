package com.grum.raphael.projectmanagerclient.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
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
 * Created by Raphael on 28.11.2017.
 */

/**
 * Asks server for changes in the team environment.
 */
public class DetectChangesService extends WakefulIntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DetectChangesService(String name) {
        super(name);
    }

    /**
     * Creates an instance of this service.
     */
    public DetectChangesService() {
        super(DetectChangesService.class.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void doWakefulWork(Intent intent) {
        SharedPreferences settings = getSharedPreferences("MyFile", 0);
        String teamName = settings.getString("team", "null");
        getNews(teamName);
    }

    /**
     * Requests the news of the team of this client's user.
     *
     * @param teamName The name of the user's team.
     */
    private void getNews(String teamName) {
        GetTeamsNews getTeamsNews = new GetTeamsNews();
        String[] params = new String[] {MainActivity.URL + "team/news", teamName};
        try {
            JSONObject result = getTeamsNews.execute(params).get();
            if (result != null) {
                String success = result.getString("success");
                if (success.equals("true")) {
                    String content = getContent(result);
                    if (content.length() != 0) {
                        Intent notificationIntent = new Intent(getApplicationContext(),
                                MainActivity.class);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(),
                                0, notificationIntent, 0);
                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService
                                        (Service.NOTIFICATION_SERVICE);
                        NotificationCompat.Builder builder =
                                new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_notification)
                                .setContentTitle("News ProManager:")
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
                stopSelf();
            }
        } catch (InterruptedException e) {
            stopSelf();
        } catch (ExecutionException e) {
            stopSelf();
        } catch (JSONException e) {
            stopSelf();
        }
    }

    /*
    Extracts the news of the team received from the server.
     */
    private String getContent(JSONObject result) throws JSONException {
        StringBuilder stringBuilder = new StringBuilder();
        JSONArray array = result.getJSONArray("news");
        for (int i = 0; i < array.length(); i++) {
            stringBuilder.append(array.getString(i));
        }
        return stringBuilder.toString();
    }
}
