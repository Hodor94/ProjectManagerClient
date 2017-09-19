package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.EditTeamTask;
import com.grum.raphael.projectmanagerclient.tasks.TeamTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class EditTeamFragment extends Fragment {

    private EditText teamName;
    private EditText teamDescription;
    private TextView admin;
    private Button btn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        View rootView = inflater.inflate(R.layout.fragment_edit_team, container, false);
        teamName = (EditText) rootView.findViewById(R.id.edit_team_profile_name);
        teamDescription = (EditText) rootView.findViewById(R.id.edit_team_profile_description);
        admin = (TextView) rootView.findViewById(R.id.edit_team_profile_admin);
        String teamName = bundle.getString("teamName");
        String teamDescription = bundle.getString("teamDescription");
        String admin = bundle.getString("admin");
        this.teamName.setText(teamName);
        this.teamDescription.setText(teamDescription);
        this.admin.setText(admin);
        btn = (Button) rootView.findViewById(R.id.edit_team_btn);
        if (MainActivity.userData.getUserRole().equals("ADMINISTRATOR")) {
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(setUpBtnAction(getActivity()));
        }
        return rootView;
    }

    public EditText getTeamName() {
        return teamName;
    }

    public EditText getTeamDescription() {
        return teamDescription;
    }

    public TextView getAdmin() {
        return admin;
    }


    // TODO debug
    private OnClickListener setUpBtnAction(final Context context) {
        final OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTeamTask editTeamTask = new EditTeamTask();
                String teamName = getTeamName().getText().toString();
                String teamDescription = getTeamDescription().getText().toString();
                String admin = getAdmin().getText().toString();
                if (teamName != null && !(teamName.equals("")) && teamDescription != null
                        && !(teamDescription.equals("")) && admin != null
                        && !(admin.equals(""))) {
                    String[] params = new String[]{MainActivity.URL + "edit/team",
                            MainActivity.userData.getToken(), teamName, teamDescription, admin};
                    try {
                        JSONObject result = editTeamTask.execute(params).get();
                        String success = result.getString("success");
                        if (success.equals("true")) {
                            MainActivity.userData.setTeamName(teamName);
                            TeamProfileFragment newFragment = new TeamProfileFragment();
                            Bundle bundle = new Bundle();
                            teamName = result.getString("teamName");
                            params = new String[]{MainActivity.userData.getToken(),
                                    MainActivity.URL + "team", teamName};
                            TeamTask teamTask = new TeamTask();
                            JSONObject fetchedTeamData = teamTask.execute(params).get();
                            bundle.putString("teamData", fetchedTeamData.toString());
                            newFragment.setArguments(bundle);
                            if (newFragment != null) {
                                FragmentTransaction fragmentTransaction
                                        = getFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.containerFrame, newFragment);
                                fragmentTransaction.commit();
                            }
                        } else {
                            String reason = result.getString("reason");
                            AlertDialog alertDialog = new AlertDialog.Builder(context)
                                    .setTitle(R.string.error)
                                    .setMessage(reason)
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
                    // TODO info alert
                }
            }
        };
        return listener;
    }
}