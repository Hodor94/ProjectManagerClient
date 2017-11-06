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
import java.nio.Buffer;
import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

/**
 * Created by Raphael on 06.11.2017.
 */

public class EditProjectMembershipTask extends AsyncTask<ArrayList<String>, Void, JSONObject> {

    @Override
    protected JSONObject doInBackground(ArrayList<String>... params) {
        JSONObject result;
        String url = MainActivity.URL + "edit/project/membership";
        String token = MainActivity.userData.getToken();
        String projectName = MainActivity.userData.getAdminOfProject();
        String teamName = MainActivity.userData.getTeamName();
        String username = MainActivity.userData.getUsername();
        ArrayList<String> usersToEdit = params[0];
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost editMembershipRequest = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(createRequestData(token, projectName, teamName,
                username, usersToEdit).toString(), "UTF-8");
        stringEntity.setContentType("application/json");
        editMembershipRequest.setEntity(stringEntity);
        try {
            HttpResponse response = client.execute(editMembershipRequest);
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

    private JSONObject createRequestData(String token, String projectName, String teamName,
                                     String username, ArrayList<String> usersToEdit) {
        JSONObject requestData = new JSONObject();
        JSONArray users = new JSONArray();
        for (String tempUsername : usersToEdit) {
            users.put(tempUsername);
        }
        try {
            requestData.put("token", token);
            requestData.put("projectName", projectName);
            requestData.put("teamName", teamName);
            requestData.put("username", username);
            requestData.put("users", users);
        } catch (JSONException e) {
            // Can never be reached -> do nothing
        }
        return requestData;
    }
}
