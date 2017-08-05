package com.grum.raphael.projectmanagerclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ProjectProfileActivity extends AppCompatActivity {

    private TextView projectName;
    private TextView description;
    private TextView deadline;
    private TextView manager;
    private TextView team;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_profile);

        // Fetching all components within activity.
        projectName = (TextView) findViewById(R.id.text_project_name);
        description = (TextView) findViewById(R.id.text_project_description);
        deadline = (TextView) findViewById(R.id.text_project_deadline);
        manager = (TextView) findViewById(R.id.text_project_manager);
        team = (TextView) findViewById(R.id.text_project_team);

        // TODO method to set values
    }
}
