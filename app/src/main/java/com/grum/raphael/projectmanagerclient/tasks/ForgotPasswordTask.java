package com.grum.raphael.projectmanagerclient.tasks;

import android.os.AsyncTask;

import java.io.IOException;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

/**
 * Created by Raphael on 29.11.2017.
 */

public class ForgotPasswordTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        String url = params[0];
        String username = params[1];
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost forgotPasswordRequest = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(createRequestData(username), "UTF-8");
        stringEntity.setContentType("application/json");
        forgotPasswordRequest.setEntity(stringEntity);
        try {
           client.execute(forgotPasswordRequest);System.out.println("YOLO");
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }
        return "DONE";
    }

    private String createRequestData(String username) {
        return "{\"username\": \"" + username + "\"}";
    }
}
