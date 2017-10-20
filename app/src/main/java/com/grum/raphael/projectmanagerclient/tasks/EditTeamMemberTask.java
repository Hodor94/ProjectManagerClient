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
 * Created by Raphael on 18.10.2017.
 */

public class EditTeamMemberTask extends AsyncTask<String, Void, JSONObject> {
    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject result;
        String url = params[0];
        String token = params[1];
        String username = params[2];
        String registerName = params[3];
        String admin = params[4];
        String teamName = params[5];
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost editTeamMemberRequest = new HttpPost(url);
        StringEntity stringEntity
                = new StringEntity(createRequestData(token, username, registerName, admin,
                teamName), "UTF-8");
        stringEntity.setContentType("application/json");
        editTeamMemberRequest.setEntity(stringEntity);
        try {
            HttpResponse response = client.execute(editTeamMemberRequest);
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
            result = null;
            e.printStackTrace();
        }
        return result;
    }

    private String createRequestData(String token, String username, String registerName,
                                     String admin, String teamName) {
        return "{\"token\": \"" + token + "\", \"username\": \"" + username + "\", \"registerName\":" +
                " \"" + registerName + "\", \"admin\": \"" + admin + "\", \"teamName\": \"" + teamName + "\"}";
    }
}
