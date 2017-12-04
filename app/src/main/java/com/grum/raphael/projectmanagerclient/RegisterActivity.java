package com.grum.raphael.projectmanagerclient;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpEntityEnclosingRequestBase;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import okhttp3.MediaType;

// TODO: comment
public class RegisterActivity extends AppCompatActivity {

    // For validating phoneNr
    private final String PHONE_REGEX = "^[0-9]";
    // For validating email
    private final String EMAIL_REGEX = "^[[A-Z]|[0-9]|[._-]]*@[[A-Z]|[0-9]]*.[A-Z]*";
    // Limit for the age. People younger than this number are not allowed to register
    private final int AGE_LIMIT = 12;

    private EditText firstName;
    private EditText surname;
    private DatePicker birthdayPicker;
    String birthday = null;
    private EditText address;
    private EditText email;
    private EditText phoneNr;
    private EditText username;
    private EditText password;
    private EditText passwordValidation;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Fetching all the components within this activity
        firstName = (EditText) findViewById(R.id.text_register_first_name);
        surname = (EditText) findViewById(R.id.text_register_surname);
        birthdayPicker = (DatePicker) findViewById(R.id.text_register_birthday);
        address = (EditText) findViewById(R.id.text_register_address);
        email = (EditText) findViewById(R.id.text_register_email);
        phoneNr = (EditText) findViewById(R.id.text_register_phoneNr);
        username = (EditText) findViewById(R.id.text_register_username);
        password = (EditText) findViewById(R.id.text_register_password);
        passwordValidation = (EditText) findViewById(R.id.text_register_password_validation);
        register = (Button) findViewById(R.id.button_register);

        // Disable emoji and other symbols
        firstName.setFilters(new InputFilter[] {MainActivity.EMOJI_FILTER});
        surname.setFilters(new InputFilter[] {MainActivity.EMOJI_FILTER});
        address.setFilters(new InputFilter[] {MainActivity.EMOJI_FILTER});
        email.setFilters(new InputFilter[] {MainActivity.EMOJI_FILTER});
        phoneNr.setFilters(new InputFilter[] {MainActivity.EMOJI_FILTER});
        username.setFilters(new InputFilter[] {MainActivity.EMOJI_FILTER});
        password.setFilters(new InputFilter[] {MainActivity.EMOJI_FILTER});
        passwordValidation.setFilters(new InputFilter[] {MainActivity.EMOJI_FILTER});

        // Set birthday String
        birthday = "" + birthdayPicker.getDayOfMonth() + "." + (birthdayPicker.getMonth() + 1) + "."
                + birthdayPicker.getYear() + " 00:00:00";

        // Init DatePicker
        Calendar currentDate = Calendar.getInstance();
        birthdayPicker.init(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                        String mDay;
                        String mMonth;
                        String mYear;
                        if (dayOfMonth < 10) {
                            mDay = "0" + dayOfMonth;
                        } else {
                            mDay = "" + dayOfMonth;
                        }
                        if (monthOfYear < 9) {
                            mMonth = "0" + (monthOfYear + 1);
                        } else {
                            mMonth = "" + (monthOfYear + 1);
                        }
                        mYear = "" + year;
                        // The 00:00:00 is for the formatter on server side
                        birthday = mDay + "." + mMonth + "." + mYear + " 00:00:00";
                    }
                });

        // Add OnClickListener
        register.setOnClickListener(new View.OnClickListener() {

            /**
             * Registers a new user if he or she does not exist already.
             * In this case it will alert the user that operation failed due to existing user.
             *
             * @param v The current view.
             */
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        }
        return super.onKeyDown(keyCode, keyEvent);
    }

    private boolean validateInput(String email, String phoneNr, String birthday, String username,
                                  String password, String passwordValidation) {
        boolean result = false;
        StringBuilder builder = new StringBuilder();
        String errorMessages;
        if (!isValidEmail(email)) {
            builder.append("Die E-Mail fehlt oder ist im falschen Format.\n" +
                    "        Bitte geben Sie die E-Mail im folgenden Format ein: " +
                    "\"präfix@mailanbieter.domäne\" \n");
        }
        if (!validatePhoneNr(phoneNr)) {
            builder.append("Die Telefonnummer fehlt oder ist im falschen Format. " +
                    "Bitte geben Sie Ihre Nummer in Zahlen ein. \n");
        }
        if (!validatePasswords(password, passwordValidation)) {
            builder.append("Das Passwort ist nicht gesetzt oder stimmt nicht mit der wiederholten " +
                    "Eingabe überein! Bitte korrigieren Sie dies. \n");
        }
        if (!validateDateIsEmpty(birthday)) {
            builder.append("Das Geburtsdatum wurde nicht ausgewählt! \n");
        } else {
            if (!validateDate(birthday)) {
                builder.append("Sie sind leider zu jung für diesen Service! " +
                        "\nSie müssen mindestens 12 Jahre alt sein! \n");
            }
        }
        if (!validateUsername(username)) {
            builder.append("Der Username wurde nicht gesetzt oder enthält nicht zugelassene Zeichen! " +
                    "\n Bitte vermeiden Sie folgende Zeichen: \n< > | ^ ° , ; . : { } ( ) \n");
        }
        if (builder.toString().equals("")) {
            result = true;
        } else {
            errorMessages = builder.toString();
            Toast.makeText(getApplicationContext(), errorMessages, Toast.LENGTH_LONG).show();
        }
        return result;
    }

    private boolean isValidEmail(String email) {
        boolean result = false;
        if (email != "") {
            Pattern emailPattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
            Matcher matcher = emailPattern.matcher(email);
            result = matcher.find();
        }
        return result;
    }

    private boolean validatePhoneNr(String phoneNr) {
        boolean result = false;
        if (phoneNr != "") {
            Pattern phoneNrPattern = Pattern.compile(PHONE_REGEX, Pattern.CASE_INSENSITIVE);
            Matcher matcher = phoneNrPattern.matcher(phoneNr);
            result = matcher.find();
        }
        return result;
    }

    private boolean validateDateIsEmpty(String date) {
        boolean result = false;
        if (date != "" && date != null) {
            result = true;
        }
        return result;
    }

    private boolean validateDate(String date) {
        boolean result = false;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        try {
            Calendar currentTime = Calendar.getInstance();
            Calendar userBirthday = Calendar.getInstance();
            userBirthday.setTime(simpleDateFormat.parse(date));
            int userYear = userBirthday.get(Calendar.YEAR);
            int currentYear = currentTime.get(Calendar.YEAR);
            if ((currentYear - userYear) > AGE_LIMIT) {
                result = true;
            }
        } catch (ParseException e) {
            // TODO interner Fehler
            e.printStackTrace();
        }
        return result;
    }

    private boolean validateUsername(String username) {
        boolean result = false;
        if (username != "" && username != null && !(username.equals("null"))) {
            for (int i = 0; i < username.length(); i++) {
                switch (username.charAt(i)) {
                    case ';':
                        return false;
                    case ',':
                        return false;
                    case ':':
                        return false;
                    case '<':
                        return false;
                    case '>':
                        return false;
                    case '°':
                        return false;
                    case '|':
                        return false;
                    case '{':
                        return false;
                    case '}':
                        return false;
                    case '(':
                        return false;
                    case ')':
                        return false;
                    default:
                        result = true;
                }
            }
        }
        return result;
    }

    private boolean validatePasswords(String password, String passwordValidate) {
        boolean result = false;
        if (password != "" && passwordValidate != "" && password.equals(passwordValidate)) {
            result = true;
        }
        return result;
    }

    private void registerUser() {
        if (CheckInternet.isNetworkAvailable(getApplicationContext())) {
            RegisterUserTask registerTask = new RegisterUserTask();
            // Getting all the user input made in the register form.
            String firstName = this.firstName.getText().toString().trim();
            String surname = this.surname.getText().toString().trim();
            String address = this.address.getText().toString().trim();
            String email = this.email.getText().toString().trim();
            String phoneNr = this.phoneNr.getText().toString().trim();
            String username = this.username.getText().toString().trim();
            String password = this.password.getText().toString().trim();
            String passwordValidation = this.passwordValidation.getText().toString();
            String url = "";

            if (validateInput(email, phoneNr, birthday, username, password, passwordValidation)) {
                url = MainActivity.URL + "register/user";
                String[] params = {url, firstName, surname, birthday, address, email, phoneNr,
                        username, password};
                registerTask.execute(params);
            }
        } else {
            AlertDialog alertDialog = CheckInternet.internetNotAvailable(this);
            alertDialog.show();
        }
    }

    //----------------------------------------------------------------------------------------------

    // TODO prograss bar or sthg like this
    private class RegisterUserTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... urls) {
            JSONObject result = null;
            try {
                String tempJson = null;
                StringBuilder builder = new StringBuilder();
                HttpClient client = HttpClientBuilder.create().build();
                HttpPost registerPost = new HttpPost(urls[0]);
                // Give the users information to the method.
                JSONObject userJson = creatUserInfo(urls[1], urls[2], urls[3], urls[4], urls[5],
                        urls[6], urls[7], urls[8]);
                if (userJson != null) {
                    StringEntity stringEntity = new StringEntity(userJson.toString(), "UTF-8");
                    stringEntity.setContentType("application/json");

                    if (stringEntity != null) {
                        registerPost.setEntity(stringEntity);
                        HttpResponse response = client.execute(registerPost);
                        InputStream input = response.getEntity().getContent();
                        if (input != null) {
                            BufferedReader reader
                                    = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                            while ((tempJson = reader.readLine()) != null) {
                                builder.append(tempJson);
                            }
                            tempJson = builder.toString();
                            result = new JSONObject(tempJson);
                            return result;
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this)
                                            .setTitle(R.string.error_registration_title)
                                            .setMessage("Keine Antwort vom Server! Bitte benachrichtigen " +
                                                    "Sie den Admin unter grum02@gw.uni-passau.de")
                                            .setNegativeButton("OK", null)
                                            .create();
                                    alertDialog.show();
                                }
                            });
                        }
                    }
                }
            } catch (IOException exc) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle(R.string.error_registration_title)
                                .setMessage("App reagiert falsch! Bitte benachrichtigen Sie den " +
                                        "Admin unter grum02@gw.uni-passau.de")
                                .setNegativeButton("OK", null)
                                .create();
                        alertDialog.show();
                    }
                });
            } catch (JSONException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle(R.string.error_registration_title)
                                .setMessage("Server Fehler! Bitte benachrichtigen Sie den " +
                                        "Admin unter grum02@gw.uni-passau.de")
                                .setNegativeButton("OK", null)
                                .create();
                        alertDialog.show();
                    }
                });
            }
            return result;
        }

        private JSONObject creatUserInfo(String firstName, String surname, String birthday,
                                         String address, String email, String phoneNr,
                                         String username, String password) {
            JSONObject result = null;
            try {
                result = new JSONObject("{\"firstName\": \"" + firstName + "\", " +
                        "\"surname\": \"" + surname + "\", \"birthday\": \"" + birthday + "\", " +
                        "\"address\": \"" + address + "\", \"email\": \"" + email + "\", " +
                        "\"phoneNr\": \"" + phoneNr + "\", \"username\": \"" + username + "\", " +
                        "\"password\": \"" + password + "\"}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                try {
                    String success = jsonObject.getString("success");
                    if (success != "") {
                        if (success.equals("true")) {
                            AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this)
                                    .setTitle(R.string.success_registration_title)
                                    .setMessage("Der User " + username.getText().toString()
                                            + " wurde erfolgreich registriert!")
                                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivity(new Intent(RegisterActivity.this,
                                                    MainActivity.class));
                                        }
                                    })
                                    .create();
                            MainActivity.userData = new DataContainer();
                            alertDialog.show();
                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this)
                                    .setTitle(R.string.error_registration_title)
                                    .setMessage("Der User " + username.getText().toString()
                                            + " existiert bereits. Bitte wählen Sie einen anderen"
                                            + " Usernamen")
                                    .setNegativeButton("OK", null)
                                    .create();
                            alertDialog.show();
                        }
                    }
                } catch (JSONException e) {
                    AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle(R.string.error_registration_title)
                            .setMessage("Falsche Antwort erhalten! Bitte kontaktieren Sie " +
                                    "den Administrator unter grum02@gw.uni-passau.de")
                            .setNegativeButton("OK", null)
                            .create();
                    alertDialog.show();
                }
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle(R.string.error_registration_title)
                        .setMessage("Server Fehler! Keina Antwort erhalten! Bitte kontaktieren Sie " +
                                "den Administrator unter grum02@gw.uni-passau.de")
                        .setNegativeButton("OK", null)
                        .create();
                alertDialog.show();
            }
        }
    }

}
