package com.grum.raphael.projectmanagerclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class UserProfileActivity extends AppCompatActivity {

    private TextView username;
    private TextView firstName;
    private TextView surname;
    private TextView email;
    private TextView phoneNr;
    private TextView address;
    private TextView tributes;
    private TextView birthday;
    private TextView dayOfEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Fetch all components on within activity
        username = (TextView) findViewById(R.id.username);
        firstName = (TextView) findViewById(R.id.firstName);
        surname = (TextView) findViewById(R.id.surname);
        email = (TextView) findViewById(R.id.email);
        phoneNr = (TextView) findViewById(R.id.phoneNr);
        address = (TextView) findViewById(R.id.address);
        tributes = (TextView) findViewById(R.id.tributes);
        birthday = (TextView) findViewById(R.id.birthday);
        dayOfEntry = (TextView) findViewById(R.id.dayOfEntry);

        // TODO method for setting values -> look TestAsyncTask
    }

}
