package com.grum.raphael.projectmanagerclient.tasks;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

/**
 * Created by Raphael on 06.09.2017.
 */

public class EditTeamTask extends AsyncTask<String, Void, JSONObject> {
    @Override
    protected JSONObject doInBackground(String... params) {
        String url;
        String token;
        String teamName;
        String teamDescription;
        String admin;
        JSONObject result;
        // Set the values
        url = params[0];
        token = params[1];
        teamName = params[2];
        teamDescription = params[3];
        admin = params[4];
        if (url != null && !(url.equals("")) && teamName != null && !(teamName.equals(""))
                && teamDescription != null && !(teamDescription.equals(""))
                && token != null && !(token.equals(""))) {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost editTeamRequest = new HttpPost(url);
            String requestData = createRequestData(token, teamName, teamDescription, admin);
            try {
                StringEntity stringEntity = new StringEntity(requestData);
                stringEntity.setContentType("application/json");
                editTeamRequest.setEntity(stringEntity);
                HttpResponse response = client.execute(editTeamRequest);
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
            } catch (UnsupportedEncodingException e) {
                result = null;
            } catch (ClientProtocolException e) {
                result = null;
            } catch (IOException e) {
                result = null;
            } catch (JSONException e) {
                result = null;
            }
        } else {
            result = null;
        }
        return result;
    }

    private String createRequestData(String token, String teamName, String teamDescription,
                                     String admin) {
        return "{\"token\": \"" + token + "\", \"teamName\": \"" + teamName + "\", " +
                "\"teamDescription\": \"" + teamDescription + "\", \"admin\": \"" + admin + "\"}";
    }
}
