package com.grum.raphael.projectmanagerclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * The first page of the application. Is used for login and save global information used in the
 * application.
 */
public class MainActivity extends AppCompatActivity {

    public final static String URL
            = "http://127.0.0.1:5500/ProjectManager-0.0.1-SNAPSHOT/pmservice/";
    public final static String ADMIN = "ADMINISTRATOR";
    public final static String USER = "USER";
    public final static String PROJECT_OWNER = "PROJECT_OWNER";
    public final static String GOOLE_DRIVE_URL = "https://www.google.com/drive/";
    public final static float DP_TEXT_SIZE = 20f;
    public static final String FILE = "MyFile";
    public static final int LOCAL_PORT = 5500;
    public static Session session;
    private String userInfo;
    public static DataContainer userData = new DataContainer();

    private Button loginButton;
    private EditText username;
    private EditText password;
    private TextView registerLink;
    private TextView forgotPasswordLink;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // Fetch all components within activity
        loginButton = (Button) findViewById(R.id.button_login);
        username = (EditText) findViewById(R.id.main_username);
        password = (EditText) findViewById(R.id.main_password);
        username.setFilters(new InputFilter[]{EMOJI_FILTER});
        password.setFilters(new InputFilter[]{EMOJI_FILTER});
        registerLink = (TextView) findViewById(R.id.main_link_register);
        forgotPasswordLink = (TextView) findViewById(R.id.main_link_forgot_password);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
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
        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    /*
    Validates the user input.
     */
    private boolean validateInput(String username, String password) {
        boolean result = false;
        if (username != "" && password != "") {
            result = true;
        }
        return result;
    }

    /*
    Starts the login request to the server and extracts the answer. Due to this answer it performs
    specific actions like forwarding to the user profile page if the login was a success.
     */
    private void login() {
        final String username = this.username.getText().toString().trim();
        final String password = this.password.getText().toString().trim();
        if (CheckInternet.isNetworkAvailable(getApplicationContext())) {
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
        } else {
            AlertDialog alertDialog = CheckInternet.internetNotAvailable(this);
            alertDialog.show();
        }
    }

    public static InputFilter EMOJI_FILTER = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int index = start; index < end; index++) {

                int type = Character.getType(source.charAt(index));

                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            return null;
        }
    };

    /*
    This calss sends the login request to the server.
     */
    private class LoginTask extends AsyncTask<String, Void, JSONObject> {

        /**
         * Sends the login request to the server.
         *
         * @param params The data as a String {@List} needed for the request.
         *
         * @return A JSONObject with the result of the request.
         */
        @Override
        protected JSONObject doInBackground(String... params) {
            session = SSHSession.createSession();
            try {
                session.connect();
                session.setPortForwardingL("0.0.0.0", LOCAL_PORT, "localhost", 8080);
            } catch (JSchException e) {
                e.printStackTrace();
            }
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
                    loginRequest.setHeader("Content-Type", "application/json");
                    loginRequest.setHeader("Accept-Encoding", "application/json");
                    try {
                        HttpResponse response = client.execute(loginRequest);
                        InputStream input = response.getEntity().getContent();
                        String tempJson;
                        if (input != null) {
                            BufferedReader reader
                                    = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                            while ((tempJson = reader.readLine()) != null) {
                                stringBuilder.append(tempJson);
                            }
                            result = new JSONObject(stringBuilder.toString());
                            return result;
                        }
                    } catch (final IOException | JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                e.printStackTrace();
                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                        .setTitle(R.string.error_login_title)
                                        .setMessage("Sie können derzeit nicht auf den Port im Netz"
                                                + " der Universität Passau zugreifen!\n" +

                                                "Bitte versuchen Sie es erneut im entsprechenden "
                                                + "Netz mit entsprechender Port-Weiterleitung!")
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

        /**
         * Extracts the answer of the server and notifies the user.
         *
         * @param jsonObject The answer of the server.
         */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                try {
                    String success = jsonObject.getString("success");
                    if (success.equals("true")) {
                        fillDataContainer(jsonObject);
                        goToNavigationActivity();
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle(R.string.error_login_title)
                                .setMessage("Login fehlgeschlagen! Eingegebene Informationen sind " +
                                        "fehlerhaft. Bitte versuchen Sie es erneut.")
                                .setNegativeButton("OK", null)
                                .create();
                        alertDialog.show();
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

        /*
        Forwards to the NavigationActivity.
         */
        private void goToNavigationActivity() {
            Intent navigationAction = new Intent(MainActivity.this, NavigationActivity.class);
            startActivity(navigationAction);
        }

        /*
        Saves the data received from the server and needed for the usage of this application.
         */
        private void fillDataContainer(JSONObject userData) {
            try {
                JSONObject user = new JSONObject(userData.getString("user"));
                String username = user.getString("username");
                String token = userData.getString("token");
                String userRole = user.getString("userRole");
                String teamName = user.getString("team");
                String adminOfProject = user.getString("adminOfProject");
                MainActivity.userData.setToken(token);
                MainActivity.userData.setAdminOfProject(adminOfProject);
                MainActivity.userData.setTeamName(teamName);
                MainActivity.userData.setUsername(username);
                MainActivity.userData.setUserRole(userRole);
                SharedPreferences settings = getSharedPreferences(FILE, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("team", teamName);
                editor.putString("username", username);
                editor.commit();
            } catch (JSONException e) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.error_login_title)
                        .setMessage("Interner Fehler! Bitte versuchen Sie, sich erneut einzuloggen.")
                        .setNegativeButton("OK", null)
                        .create();
                username.setText("");
                password.setText("");
                alertDialog.show();
            }
        }
    }
}
