package com.grum.raphael.projectmanagerclient;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {

    public final static String URL
            = "http://10.0.2.2:8080/ProjectManager-0.0.1-SNAPSHOT/pmservice/";
    public static byte[] secret;

    private Button login;
    private EditText username;
    private EditText password;
    private TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fetch all components within activity
        login = (Button) findViewById(R.id.button_login);
        username = (EditText) findViewById(R.id.main_username);
        password = (EditText) findViewById(R.id.main_password);
        registerLink = (TextView) findViewById(R.id.main_link_register);

        // Set Listener for clicking buttons
        login.setOnClickListener(new View.OnClickListener() {

            /**
             * TODO
             * @param v
             */
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // Set Listener for clicking the link to the register page
        registerLink.setOnClickListener(new View.OnClickListener() {

            /**
             * CLicking the text links to the register activity.
             *
             * @param v The current view.
             */
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
    }

    private boolean validateInput(String username, String password) {
        boolean result = false;
        if (username != "" && password != "") {
            result = true;
        }
        return result;
    }

    private void login() {
        final String username = this.username.getText().toString();
        final String password = this.password.getText().toString();
        if (validateInput(username, password)) {
            LoginTask loginTask = new LoginTask();
            String url = MainActivity.URL + "login";
            String[] params = {url, username, password};
            loginTask.execute(params);
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.error_login_title)
                    .setMessage("Bitte füllen Sie die Felder für Usernamen und Passwort aus!")
                    .setNegativeButton("OK", null)
                    .create();
            alertDialog.show();
        }
    }

    private class LoginTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject result = null;
            StringBuilder stringBuilder = new StringBuilder();
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost loginRequest = new HttpPost(params[0]);
            JSONObject userInfo = createUserInfo(params[1], params[2]);
            if (userInfo != null) {
                StringEntity stringEntity = new StringEntity(userInfo.toString(), "UTF-8");
                stringEntity.setContentType("application/json");
                if (stringEntity != null) {
                    loginRequest.setEntity(stringEntity);
                    try {
                        HttpResponse response = client.execute(loginRequest);
                        InputStream input = response.getEntity().getContent();
                        String tempJson;
                        if (input != null) {
                            BufferedReader reader
                                    = new BufferedReader(new InputStreamReader(input));
                            while ((tempJson = reader.readLine()) != null) {
                                stringBuilder.append(tempJson);
                            }
                            result = new JSONObject(stringBuilder.toString());
                            return result;
                        }
                    } catch (IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                        .setTitle(R.string.error_login_title)
                                        .setMessage("App reagiert falsch! Bitte benachrichtigen Sie den " +
                                                "Admin unter grum02@gw.uni-passau.de")
                                        .setNegativeButton("OK", null)
                                        .create();
                                alertDialog.show();
                            }
                        });
                    } catch (JSONException e) {
                        // AlertDialog has to run on UI thread.
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                        .setTitle(R.string.error_login_title)
                                        .setMessage("Server Fehler! Bitte benachrichtigen Sie den " +
                                                "Admin unter grum02@gw.uni-passau.de")
                                        .setNegativeButton("OK", null)
                                        .create();
                                alertDialog.show();
                            }
                        });
                    }
                }
            }
            return result;
        }

        // TODO debug
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                try {
                    String success = jsonObject.getString("success");
                    if (success.equals("true")) {
                        String token = jsonObject.getString("token");
                        System.out.println(token);
                        System.out.println("Login erfolgreich");
                    } else {
                        // TODO
                        System.out.println("Login fehlgeschlagen");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.error_login_title)
                            .setMessage("Server Fehler! Bitte benachrichtigen Sie den " +
                                    "Admin unter grum02@gw.uni-passau.de")
                            .setNegativeButton("OK", null)
                            .create();
                    alertDialog.show();
                }
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.error_login_title)
                        .setMessage("Server Fehler! Bitte benachrichtigen Sie den " +
                                "Admin unter grum02@gw.uni-passau.de")
                        .setNegativeButton("OK", null)
                        .create();
                alertDialog.show();
            }
        }

        private JSONObject createUserInfo(String username, String password) {
            JSONObject result = null;
            try {
                result = new JSONObject("{\"username\": \"" + username + "\", " + "\"password\": "
                        + "\"" + password + "\"}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

    }
}
