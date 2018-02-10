package com.grum.raphael.projectmanagerclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.grum.raphael.projectmanagerclient.tasks.ForgotPasswordTask;

/**
 * The page of the user for the user to request a PIN from the server.
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText username;
    private String usernameText;
    private Button getPin;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        username = (EditText) findViewById(R.id.add_username);
        username.setFilters(new InputFilter[] {MainActivity.EMOJI_FILTER});
        username.addTextChangedListener(new TextWatcher() {
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
                usernameText = username.getText().toString();
            }
        });
        getPin = (Button) findViewById(R.id.get_pin);
        getPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    ForgotPasswordTask forgotPasswordTask = new ForgotPasswordTask();
                    forgotPasswordTask.execute(new String [] {MainActivity.URL
                            + "password/forgotten", usernameText});
                    Bundle bundle = new Bundle();
                    bundle.putString("username", usernameText);
                    Intent intent = new Intent(ForgotPasswordActivity.this,
                            ChangePasswordActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.error_fields_empty),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /*
     Validates the user input.
     */
    private boolean validateInput() {
        boolean result = false;
        if (usernameText != null && usernameText.length() != 0) {
            result = true;
        }
        return result;
    }
}
