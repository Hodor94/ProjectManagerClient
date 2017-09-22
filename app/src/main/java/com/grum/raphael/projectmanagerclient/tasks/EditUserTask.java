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
 * Created by Raphael on 21.09.2017.
 */

// TODO debug
public class EditUserTask extends AsyncTask<String, Void, JSONObject> {
    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject result;
        String url = params[0];
        String token = params[1];
        String username = params[2];
        String firstName = params[3];
        String surname = params[4];
        String address = params[5];
        String phoneNr = params[6];
        String email = params[7];
        String birthday = params[8];
        if (url != null && !(url.equals("")) && username != null && !(username.equals(""))
                && firstName != null && !(firstName.equals("")) && surname != null
                && address != null && !(address.equals("")) && phoneNr != null
                && !(phoneNr.equals("")) && email != null && !(email.equals(""))
                && birthday != null && !(birthday.equals(""))) {
            String userData = createUserData(token, username, firstName, surname, address, phoneNr,
                    email, birthday);
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost editUserRequest = new HttpPost(url);
            StringEntity stringEntity = new StringEntity(userData, "UTF-8");
            stringEntity.setContentType("application/json");
            editUserRequest.setEntity(stringEntity);
            try {
                HttpResponse response = client.execute(editUserRequest);
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
                result = null;
            } catch (JSONException e) {
                result = null;
            }
        } else {
            result = null;
        }
        return result;
    }

    private String createUserData(String token, String username, String firstName, String surname,
                                  String address, String phoneNr, String email,
                                  String birthday) {
        return "{\"token\": \"" + token + "\", \"username\": \"" + username + "\", "
                + "\"firstName\": \"" + firstName + "\", \"surname\": \"" + surname + "\", "
                + "\"address\": \"" + address + "\", \"phoneNr\": \"" + phoneNr + "\", \"email\": \""
                + email + "\", \"birthday\": \"" + birthday + "\"}";
    }
}
