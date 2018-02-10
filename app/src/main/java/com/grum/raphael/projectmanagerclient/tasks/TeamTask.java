package com.grum.raphael.projectmanagerclient.tasks;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

/**
 * Created by Raphael on 24.08.2017.
 */

public class TeamTask extends AsyncTask<String, Void, JSONObject> {

    @Override
    protected JSONObject doInBackground(String... params) {
        String token = params[0];
        String url = params[1];
        String teamName = params[2];
        JSONObject result;
        if (token != null && !(token.equals("")) && url != null && !(url.equals(""))
                && teamName != null && !(teamName.equals(""))) {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost requestTeam = new HttpPost(url);
            String requestData = createRequestData(token, teamName);
            try {
                StringEntity stringEntity = new StringEntity(requestData);
                stringEntity.setContentType("application/json");
                requestTeam.setEntity(stringEntity);
                HttpResponse response = client.execute(requestTeam);
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
            } catch (IOException | JSONException e) {
                result = null;
            }
        }else {
            result = null;
        }
        return result;
    }

    private String createRequestData(String token, String teamName) {
        return "{\"token\": \"" + token + "\", \"team\": \"" + teamName + "\"}";
    }

}
