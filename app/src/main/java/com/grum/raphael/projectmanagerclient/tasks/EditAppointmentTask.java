package com.grum.raphael.projectmanagerclient.tasks;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

/**
 * Created by Raphael on 13.11.2017.
 */

public class EditAppointmentTask extends AsyncTask<String, Void, JSONObject> {

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject result;
        String url = params[0];
        String token = params[1];
        String username = params[2];
        String projectName = params[3];
        String teamName = params[4];
        String id = params[5];
        String name = params[6];
        String description = params[7];
        String deadline = params[8];
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost editAppointmentTask = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(createRequestData(token, username, projectName,
                teamName, id, name, description, deadline), "UTF-8");
        stringEntity.setContentType("application/json");
        editAppointmentTask.setEntity(stringEntity);
        try {
            HttpResponse response = client.execute(editAppointmentTask);
            InputStream input = response.getEntity().getContent();
            if (input != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder stringBuilder = new StringBuilder();
                String temp;
                while ((temp = reader.readLine()) != null) {
                    stringBuilder.append(temp);
                }
                result = new JSONObject(stringBuilder.toString());
            } else {
                // TODO
                result = null;
            }
        } catch (IOException e) {
            // TODO
            result = null;
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO
            result = null;
            e.printStackTrace();
        }
        return result;
    }

    private String createRequestData(String token, String username, String projectName,
                                     String teamName, String id, String name, String description,
                                     String deadline) {
        return "{\"token\": \"" + token + "\", \"username\": \"" + username + "\", \"projectName\":"
                + " \"" + projectName + "\", \"teamName\": \"" + teamName + "\", \"id\": \"" + id
                + "\", \"appointmentName\": \"" + name + "\", \"appointmentDescription\": \""
                + description + "\", \"deadline\": \"" + deadline + "\"}";
    }

}
