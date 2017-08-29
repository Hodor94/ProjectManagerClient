package com.grum.raphael.projectmanagerclient.tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.NavigationActivity;
import com.grum.raphael.projectmanagerclient.R;

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
 * Created by Raphael on 24.08.2017.
 */

public class UserTask extends AsyncTask<String, Void, JSONObject> {

    private Context context;

    public UserTask() {

    }

    public UserTask(Context context) {
        this.context = context;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        String url = params[0];
        String username = params[1];
        String token = params[2];
        JSONObject result;
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost getUserData = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(createUserInfo(token, username), "UTF-8");
        stringEntity.setContentType("application/json");
        getUserData.setEntity(stringEntity);
        try {
            HttpResponse response = client.execute(getUserData);
            InputStream input = response.getEntity().getContent();
            if (input != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = reader.readLine()) != null) {
                    stringBuilder.append(temp);
                }
                try {
                    result = new JSONObject(stringBuilder.toString());
                } catch (JSONException e) {
                    result = null;
                }
            } else {
                result = null;
            }
        } catch (IOException e) {
            result = null;
        }
        return result;
    }

    private String createUserInfo(String token, String username) {
        return "{\"token\": \"" + token + "\", \"username\": \"" + username + "\"}";
    }
}
