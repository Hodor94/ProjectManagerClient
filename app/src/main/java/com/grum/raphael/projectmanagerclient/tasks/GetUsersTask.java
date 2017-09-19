package com.grum.raphael.projectmanagerclient.tasks;

import android.os.AsyncTask;

import com.grum.raphael.projectmanagerclient.MainActivity;

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
 * Created by Raphael on 11.09.2017.
 */

public class GetUsersTask extends AsyncTask<String, Void, JSONObject> {

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject result;
        if (!MainActivity.userData.isEmpty()) {
            String url = params[0];
            String token = MainActivity.userData.getToken();
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost getUsersRequest = new HttpPost(url);
            String requestData;
            try {
                StringEntity stringEntity = new StringEntity(createRequestData(token));
                stringEntity.setContentType("application/json");
                getUsersRequest.setEntity(stringEntity);
                HttpResponse response = client.execute(getUsersRequest);
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

    private String createRequestData(String token) {
        return "{\"token\": \"" + token + "\"}";
    }
}
