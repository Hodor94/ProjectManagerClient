package com.grum.raphael.projectmanagerclient.tasks;

import android.content.Context;
import android.content.Intent;
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
 * Created by Raphael on 03.09.2017.
 */

public class GetTeamsTask extends AsyncTask<String, Void, JSONObject> {

    private Context context;

    public GetTeamsTask() {

    }

    public GetTeamsTask(Context context) {
        this.context = context;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject result;
        if (!MainActivity.userData.isEmpty()) {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost getTeams = new HttpPost(params[0]);
            try {
                String tokenData = "{\"token\": \"" + params[1] + "\"}";
                StringEntity stringEntity = new StringEntity(tokenData);
                stringEntity.setContentType("application/json");
                getTeams.setEntity(stringEntity);
                HttpResponse fetchedTeams = client.execute(getTeams);
                InputStream input = fetchedTeams.getEntity().getContent();
                if (input != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
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
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }
        return result;
    }
}
