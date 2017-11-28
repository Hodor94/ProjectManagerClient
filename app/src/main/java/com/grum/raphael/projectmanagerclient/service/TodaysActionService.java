package com.grum.raphael.projectmanagerclient.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.NewsflashTodayTaskTokenless;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

/**
 * Created by Raphael on 28.11.2017.
 */

public class TodaysActionService extends WakefulIntentService {

    private String content = "";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public TodaysActionService(String name) {
        super(name);
    }

    public TodaysActionService() {
        super(TodaysActionService.class.getName());
    }

    @Override
    void doWakefulWork(Intent intent) {
        SharedPreferences settings = getSharedPreferences("MyFile", 0);
        String teamName = settings.getString("team", "null");
        String username = settings.getString("username", "null");
        getNewsflashForToday(teamName, username);
    }

    private void getNewsflashForToday(String teamName, String username) {
        JSONArray projects;
        JSONArray appointments;
        JSONArray tasks;
        JSONArray birthdays;
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String currentDate = formatter.format(calendar.getTime());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String mondayOfWeek = formatter.format(calendar.getTime());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        String sundayOfWeek = formatter.format(calendar.getTime());
        String[] params = new String[] {MainActivity.URL + "newsflash/tokenless", teamName,
        username, currentDate, mondayOfWeek, sundayOfWeek};
        NewsflashTodayTaskTokenless getNewsflashTask = new NewsflashTodayTaskTokenless();
        try {
            JSONObject result = getNewsflashTask.execute(params).get();
            String success = result.getString("success");
            if (success.equals("true")) {
                JSONObject relevantDates = result.getJSONObject("dates");
                projects = relevantDates.getJSONArray("projects");
                appointments = relevantDates.getJSONArray("appointments");
                tasks = relevantDates.getJSONArray("tasks");
                birthdays = relevantDates.getJSONArray("birthdays");
                editProjects(projects);
                editAppointments(appointments);
                editTasks(tasks);
                editBirthdays(birthdays);
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
                                    .setContentTitle("Das steht heute an:")
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
        } catch (InterruptedException | JSONException | ExecutionException e) {
            stopSelf();
        }
        stopSelf();
    }

    private void editTasks(JSONArray tasks) throws JSONException {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < tasks.length(); i++) {
            JSONObject task = tasks.getJSONObject(i);
            String time = task.getString("deadline").split(" ")[1];
            String name = task.getString("appointment");
            stringBuilder.append(name + " " + time + "\n");
        }
        if (stringBuilder.length() != 0) {
            content += "Folgende Aufgabe sind heute zu erledigen:\n" + stringBuilder.toString();
        }
    }

    private void editAppointments(JSONArray appointments) throws JSONException {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < appointments.length(); i++) {
            JSONObject appointment = appointments.getJSONObject(i);
            String time = appointment.getString("deadline").split(" ")[1];
            String name = appointment.getString("appointment");
            stringBuilder.append(name + " " + time + "\n");
        }
        if (stringBuilder.length() != 0) {
            content += "Folgende Meetings finden heute statt\n:" + stringBuilder.toString();
        }
    }

    private void editProjects(JSONArray projects) throws JSONException {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < projects.length(); i++) {
            JSONObject project = projects.getJSONObject(i);
            String time = project.getString("deadline").split(" ")[1];
            String name = project.getString("project");
            stringBuilder.append(name + " " + time + "\n");
        }
        if (stringBuilder.length() != 0) {
            content += "Folgende Projekte finden heute ein Ende:\n" + stringBuilder.toString();
        }
    }

    private void editBirthdays(JSONArray birthdays) throws JSONException {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < birthdays.length(); i++) {
                JSONObject user = birthdays.getJSONObject(i);
                stringBuilder.append(user.getString("username") + "\n");
        }
        if (stringBuilder.length() != 0) {
            content += "Folgende User haben heute Geburtstag:\n" + stringBuilder.toString();
        }
    }
}
