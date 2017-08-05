package com.grum.raphael.projectmanagerclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class TeamProfileActivity extends AppCompatActivity {

    private TextView name;
    private TextView description;
    private TextView admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_profile);

        // Fetch all compontents within the activity
        name = (TextView) findViewById(R.id.text_team_name);
        description = (TextView) findViewById(R.id.text_team_description);
        admin = (TextView) findViewById(R.id.text_team_admin);

        // TODO method to fill in values
    }
}
