package com.grum.raphael.projectmanagerclient.tasks;

import android.os.AsyncTask;
import android.renderscript.ScriptGroup;

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
 * Created by Raphael on 10.10.2017.
 */

public class GetRegistersTask extends AsyncTask<String, Void, JSONObject> {

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject result;
        String url = params[0];
        String token = params[1];
        String teamName = params[2];
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost getRegistersRequest = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(createUserData(token, teamName), "UTF-8");
        stringEntity.setContentType("application/json");
        getRegistersRequest.setEntity(stringEntity);
        try {
            HttpResponse response = client.execute(getRegistersRequest);
            InputStream input = response.getEntity().getContent();
            if (input != null) {
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
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

    private String createUserData(String token, String teamName) {
        return "{\"token\": \"" + token + "\", \"teamName\": \"" + teamName + "\"}";
    }
}
