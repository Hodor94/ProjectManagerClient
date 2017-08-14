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
        Bundle userData = getIntent().getExtras();

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

        // Set the values fetched from intent
        username.setText(userData.getString("username"));
        firstName.setText(userData.getString("dirstName"));
        surname.setText(userData.getString("surname"));
        email.setText(userData.getString("email"));
        phoneNr.setText(userData.getString("phoneNr"));
        address.setText(userData.getString("address"));
        tributes.setText(userData.getString("tributes"));
        birthday.setText(userData.getString("birthday"));
        dayOfEntry.setText(userData.getString("dayOfEntry"));

        // TODO team
    }

}
