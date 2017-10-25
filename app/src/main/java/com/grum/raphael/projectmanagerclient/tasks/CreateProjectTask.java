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
 * Created by Raphael on 23.10.2017.
 */

public class CreateProjectTask extends AsyncTask<String, Void, JSONObject> {
    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject result;
        String url = params[0];
        String token = params[1];
        String teamName = params[2];
        String projectName = params[3];
        String projectDescription = params[4];
        String projectManager = params[5];
        String deadline = params[6];
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost createProjectRequest = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(createRequestData(token, teamName, projectName,
                projectDescription, projectManager, deadline), "UTF-8");
        stringEntity.setContentType("application/json");
        createProjectRequest.setEntity(stringEntity);
        try {
            HttpResponse response = client.execute(createProjectRequest);
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
                // TODO
            }
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
            result = null;
        } catch (JSONException e) {
            // TODO
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    private String createRequestData(String token, String teamName, String projectName,
                                     String projectDescription, String projectManager,
                                     String deadline) {
        return "{\"token\": \"" + token + "\", \"teamName\": \"" + teamName + "\", " +
                "\"projectName\": \"" + projectName + "\", \"projectDescription\": \""
                + projectDescription + "\", \"projectManager\": \"" + projectManager + "\", " +
                "\"deadline\": \"" + deadline + "\"}";
    }
}
