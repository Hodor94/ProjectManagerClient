package com.grum.raphael.projectmanagerclient.tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.grum.raphael.projectmanagerclient.R;

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
 * Created by Raphael on 31.08.2017.
 */

public class CreateTeamTask extends AsyncTask<String, Void, JSONObject> {

    private Context context;

    public CreateTeamTask() {

    }

    public CreateTeamTask(Context context) {
        this.context = context;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject result;
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost createTeamRequest = new HttpPost(params[0]);
        StringEntity teamInfo
                = new StringEntity(createTeamInfo(params[1], params[2], params[3], params[4]),
                "UTF-8");
        teamInfo.setContentType("application/json");
        createTeamRequest.setEntity(teamInfo);
        try {
            HttpResponse response = client.execute(createTeamRequest);
            InputStream input = response.getEntity().getContent();
            String temp;
            if (input != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = reader.readLine()) != null) {
                    stringBuilder.append(temp);
                }
                result = new JSONObject(stringBuilder.toString());
            } else {
                result = null;
            }
        } catch (IOException e) {
            result = null;
        } catch (JSONException e) {
            result = null;
        }
        return result;
    }

    private String createTeamInfo(String teamName, String teamDescription, String username,
                                  String token) {
        String data = "{\"token\": \"" + token + "\", \"teamName\": \"" + teamName
                + "\", \"teamDescription\": \"" + teamDescription + "\", " +
                "\"admin\": \"" + username +  "\"}";
        return data;
    }
}
