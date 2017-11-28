package com.grum.raphael.projectmanagerclient.tasks;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

/**
 * Created by Raphael on 28.11.2017.
 */

public class NewsflashTodayTaskTokenless extends AsyncTask<String, Void, JSONObject> {

    /**
     * Created by Raphael on 21.11.2017.
     */

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject result;
        String url = params[0];
        String teamName = params[1];
        String username = params[2];
        String currentDate = params[3];
        String mondayOfWeek = params[4];
        String sundayOfWeek = params[4];
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost getNewsflashRequest = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(createRequestData(teamName, username,
                currentDate, mondayOfWeek, sundayOfWeek), "UTF-8");
        stringEntity.setContentType("application/json");
        getNewsflashRequest.setEntity(stringEntity);
        try {
            HttpResponse response = client.execute(getNewsflashRequest);
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

    private String createRequestData(String teamName, String username,
                                     String currentDate, String mondayOfWeek, String sundayOfWeek) {
        return "{\"teamName\": \"" + teamName + "\", \"username\": " + "\"" + username + "\", "
                + "\"currentDate\": \"" + currentDate + "\", \"mondayOfWeek\": "
                + "\"" + mondayOfWeek + "\", \"sundayOfWeek\": \"" + sundayOfWeek + "\"}";
    }
}

