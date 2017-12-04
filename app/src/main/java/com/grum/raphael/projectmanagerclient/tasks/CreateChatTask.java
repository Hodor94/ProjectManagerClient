package com.grum.raphael.projectmanagerclient.tasks;

import android.os.AsyncTask;

import com.grum.raphael.projectmanagerclient.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

/**
 * Created by Raphael on 30.11.2017.
 */

public class CreateChatTask extends AsyncTask<ArrayList<String>, Void, JSONObject> {

    private String chatName;
    private String isSoloChat;

    public CreateChatTask(String chatName, String isSoloChat) {
        this.chatName = chatName;
        this.isSoloChat = isSoloChat;
    }

    @Override
    protected JSONObject doInBackground(ArrayList<String>... params) {
        JSONObject result;
        String url = MainActivity.URL + "create/chat";
        String token = MainActivity.userData.getToken();
        String teamName = MainActivity.userData.getTeamName();
        ArrayList<String> usersOfChat = params[0];
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost createTeamRequest = new HttpPost(url);
        try {
            StringEntity stringEntity = new StringEntity(createRequestData(token, teamName,
                    usersOfChat, chatName, isSoloChat), "UTF-8");
            stringEntity.setContentType("application/json");
            createTeamRequest.setEntity(stringEntity);
            HttpResponse response = client.execute(createTeamRequest);
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
                // TODO
                result = null;
            }
        } catch (IOException e) {
            result = null;
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO
            result = null;
            e.printStackTrace();
        }
        return result;
    }

    private String createRequestData(String token, String teamName, ArrayList<String> usersOfChat,
                                     String chatName, String isSoloChat) throws JSONException {
        JSONObject result = new JSONObject();
        result.put("token", token);
        result.put("teamName", teamName);
        JSONArray arrayUsers = new JSONArray(usersOfChat);
        result.put("users", arrayUsers);
        result.put("name", chatName);
        result.put("isSoloChat", isSoloChat);
        return result.toString();
    }
}
