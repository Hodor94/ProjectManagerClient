package com.grum.raphael.projectmanagerclient;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.grum.raphael.projectmanagerclient.tasks.ChangePasswordTask;
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * The page the user can change his or her password with a PIN created on the server side.
 */
public class ChangePasswordActivity extends AppCompatActivity {

    private TextView header;
    private EditText oldPasswordView;
    private EditText newPasswordView;
    private EditText newPasswordRepetitionView;
    private Button changePassword;
    private String oldPassword;
    private String newPassword;
    private String newPasswordRepetition;
    private String username;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        header = (TextView) findViewById(R.id.header_change_password);
        header.setText(username + ", ändern Sie nun bitte Ihr Passwort:");
        oldPasswordView = (EditText) findViewById(R.id.old_password);
        oldPasswordView.setFilters(new InputFilter[] {MainActivity.EMOJI_FILTER});
        oldPasswordView.addTextChangedListener(new TextWatcher() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void afterTextChanged(Editable s) {
                oldPassword = oldPasswordView.getText().toString();
            }
        });
        newPasswordView = (EditText) findViewById(R.id.new_password_one);
        newPasswordView.setFilters(new InputFilter[] {MainActivity.EMOJI_FILTER});
        newPasswordView.addTextChangedListener(new TextWatcher() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void afterTextChanged(Editable s) {
                newPassword = newPasswordView.getText().toString();
            }
        });
        newPasswordRepetitionView = (EditText) findViewById(R.id.new_password_two);
        newPasswordRepetitionView.setFilters(new InputFilter[] {MainActivity.EMOJI_FILTER});
        newPasswordRepetitionView.addTextChangedListener(new TextWatcher() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void afterTextChanged(Editable s) {
                newPasswordRepetition = newPasswordRepetitionView.getText().toString();
            }
        });
        changePassword = (Button) findViewById(R.id.change_password);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    /*
    Sends the password request to the server.
    */
    private void changePassword() {
        if (validateInput()) {
            if (CheckInternet.isNetworkAvailable(getApplicationContext())) {
                String[] params = new String[]{MainActivity.URL + "change/password",
                        oldPassword, newPassword, username};
                ChangePasswordTask changePasswordTask = new ChangePasswordTask();

                try {
                    JSONObject result = changePasswordTask.execute(params).get();
                    String success = result.getString("success");
                    if (success.equals("true")) {
                        Toast.makeText(getApplicationContext(), "Das Passwort wurde erfolgreich " +
                                "geändert", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
                    } else {
                        String reason = result.getString("reason");
                        AlertDialog alertDialog = new AlertDialog.Builder(getApplication())
                                .setTitle(R.string.error)
                                .setMessage("Das Passwort konnte nicht geändert werden!\n"
                                + reason)
                                .setNegativeButton("OK", null)
                                .create();
                        alertDialog.show();
                    }
                } catch (InterruptedException e) {
                    // TODO
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO
                    e.printStackTrace();
                } catch (JSONException e) {
                    // TODO
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.error_fields_filled_wrong),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            android.app.AlertDialog alertDialog = CheckInternet.internetNotAvailable(this);
            alertDialog.show();
        }
    }

    /*
    Validates the whole input.
     */
    private boolean validateInput() {
        boolean result = false;
        if (oldPassword != null && oldPassword.length() != 0 && newPassword != null
                && newPassword.length() != 0 && newPasswordRepetition != null
                && newPasswordRepetition.length() != 0) {
            result = true;
        }
        return result;
    }
}
