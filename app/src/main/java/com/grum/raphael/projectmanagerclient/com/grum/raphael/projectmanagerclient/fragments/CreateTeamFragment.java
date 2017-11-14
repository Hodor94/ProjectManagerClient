package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.ErrorAlertExpiredRights;
import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;
import com.grum.raphael.projectmanagerclient.tasks.CreateTeamTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class CreateTeamFragment extends Fragment {

    private EditText teamName;
    private EditText teamDescription;
    private TextView info;
    private Button btnCreate;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_team, container, false);
        teamName = (EditText) rootView.findViewById(R.id.team_name);
        info = (TextView) rootView.findViewById(R.id.create_team_info);
        teamDescription = (EditText) rootView.findViewById(R.id.team_description);
        btnCreate = (Button) rootView.findViewById(R.id.btn_create_team);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.userData.getTeamName().equals("null")) {
                    createTeam();
                } else {
                    AlertDialog alertDialog = createAlreadyGotTeam();
                    alertDialog.show();
                }
            }
        });
        return rootView;
    }

    private void createTeam() {
        if (CheckInternet.isNetworkAvailable(getContext())) {
            if (!MainActivity.userData.isEmpty()) {
                String teamName = this.teamName.getText().toString();
                String teamDescription = this.teamDescription.getText().toString();
                if (validateUserInput(teamName, teamDescription)) {
                    info.setText("");
                    CreateTeamTask createTeamTask = new CreateTeamTask();
                    try {
                        JSONObject fetchedData
                                = createTeamTask.execute(new String[]{MainActivity.URL + "create/team",
                                teamName, teamDescription, MainActivity.userData.getUsername(),
                                MainActivity.userData.getToken()}).get();
                        String success = fetchedData.getString("success");
                        String token = fetchedData.getString("token");
                        if (success.equals("true")) {
                            teamName = fetchedData.getString("teamName");
                            MainActivity.userData.setTeamName(teamName);
                            MainActivity.userData.setUserRole(MainActivity.ADMIN);
                            MainActivity.userData.setToken(token);
                            AlertDialog alertDialog = creatingTeamSuccessfulAlert(teamName);
                            alertDialog.show();
                        } else {
                            String reason = fetchedData.getString("reason");
                            AlertDialog alertDialog = creatingTeamNotSuccessfulAlert(reason);
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
                    info.setText(R.string.error_create_team_input);
                }
            } else {
                AlertDialog alertDialog = new ErrorAlertExpiredRights(getActivity()).getAlertDialog();
                alertDialog.show();
            }
        } else {
            AlertDialog alertDialog = CheckInternet.internetNotAvailable(getActivity());
            alertDialog.show();
        }
    }

    private boolean validateUserInput(String teamName, String teamDescription) {
        boolean result = false;
        if (teamName != null && teamDescription != null && !(teamName.equals(""))
                && !(teamDescription.equals("")) && !(teamName.equals("null"))) {
            result = true;
        }
        return result;
    }

    private AlertDialog creatingTeamNotSuccessfulAlert(String reason) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.error)
                .setMessage(reason)
                .setNegativeButton("OK", null)
                .create();
        return alertDialog;
    }

    private AlertDialog creatingTeamSuccessfulAlert(String teamName) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.success)
                .setMessage("Sie haben das Team " + teamName + " erfolgreich angelegt!")
                .setNegativeButton("OK", null)
                .create();
        return alertDialog;
    }

    private AlertDialog createAlreadyGotTeam() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.error)
                .setMessage("Sie sind schon Mitglied eines Teams.\n" +
                        "Um ein neues Team zu erstellen, verlassen Sie bitte ihr aktuelles Team.")
                .setNegativeButton("OK", null)
                .create();
        return alertDialog;
    }
}

