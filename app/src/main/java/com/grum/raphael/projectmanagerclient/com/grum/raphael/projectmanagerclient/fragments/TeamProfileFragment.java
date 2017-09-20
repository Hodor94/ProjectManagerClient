package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;

import org.json.JSONException;
import org.json.JSONObject;


public class TeamProfileFragment extends Fragment {

    private TextView teamName;
    private TextView teamDescription;
    private TextView admin;
    private Button btn;

    public TeamProfileFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_team_profile, container, false);
        // Fetch all the items inside the fragment
        teamName = (TextView) rootView.findViewById(R.id.team_profile_name);
        teamDescription = (TextView) rootView.findViewById(R.id.team_profile_description);
        admin = (TextView) rootView.findViewById(R.id.team_profile_admin);
        btn = (Button) rootView.findViewById(R.id.team_profile_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTeamFragment newFragment = new EditTeamFragment();
                Bundle bundle = new Bundle();
                bundle.putString("teamName", teamName.getText().toString());
                bundle.putString("teamDescription", teamDescription.getText().toString());
                bundle.putString("admin", admin.getText().toString());
                newFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction
                        = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.containerFrame, newFragment);
                fragmentTransaction.commit();
            }
        });
        String teamName = null;
        String teamDescription = null;
        String admin = null;

        // Fetch Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            String fetchedData = bundle.getString("teamData");
            try {
                JSONObject data = new JSONObject(fetchedData);
                String success = data.getString("success");
                if (success.equals("true")) {
                    JSONObject team = new JSONObject(data.getString("team"));
                    teamName = team.getString("name");
                    teamDescription = team.getString("description");
                    admin = team.getString("admin");
                    if (admin.equals(MainActivity.userData.getUsername())) {
                        btn.setVisibility(View.VISIBLE);
                    } else {
                        btn.setVisibility(View.INVISIBLE);
                    }
                } else {
                    // TODO
                }
                this.teamName.setText(teamName);
                this.teamDescription.setText(teamDescription);
                this.admin.setText(admin);
            } catch (JSONException e) {
                // TODO
                e.printStackTrace();
            }
        } else {
            // TODO
        }
        return rootView;
    }
}
