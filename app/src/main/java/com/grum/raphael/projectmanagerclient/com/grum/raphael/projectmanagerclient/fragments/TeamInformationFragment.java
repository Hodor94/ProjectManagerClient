package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;
import com.grum.raphael.projectmanagerclient.tasks.DeleteTeamTask;
import com.grum.raphael.projectmanagerclient.tasks.LeaveTeamTask;
import com.grum.raphael.projectmanagerclient.tasks.UserTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class TeamInformationFragment extends Fragment {

    private TextView teamName;
    private TextView teamDescription;
    private TextView admin;
    private Button btn;
    private Button leaveTeam;
    private ImageView googleDrive;

    public TeamInformationFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_team_information, container, false);
        // Fetch all the items inside the fragment
        teamName = (TextView) rootView.findViewById(R.id.team_profile_name);
        teamDescription = (TextView) rootView.findViewById(R.id.team_profile_description);
        admin = (TextView) rootView.findViewById(R.id.team_profile_admin);
        btn = (Button) rootView.findViewById(R.id.team_profile_btn_edit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTeam();
            }
        });
        leaveTeam = (Button) rootView.findViewById(R.id.btn_leave_team);
        leaveTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.attention)
                        .setMessage("Wollden Sie das Team wirklich verlassen?")
                        .setNegativeButton("Abbrechen", null)
                        .setPositiveButton("Verlassen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                leaveTeam();
                            }
                        }).create();
                alertDialog.show();
            }
        });
        googleDrive = (ImageView) rootView.findViewById(R.id.google_drive);
        googleDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGoogleDrive();
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

    private void goToGoogleDrive() {
        Uri googleDriveUri = Uri.parse(MainActivity.GOOLE_DRIVE_URL);
        Intent intent = new Intent(Intent.ACTION_VIEW, googleDriveUri);
        startActivity(intent);
    }

    private void editTeam() {
        if (MainActivity.userData.getUserRole().equals("ADMINISTRATOR")) {
            EditTeamFragment newFragment = new EditTeamFragment();
            Bundle bundle = new Bundle();
            bundle.putString("teamName", teamName.getText().toString());
            bundle.putString("teamDescription", teamDescription.getText().toString());
            bundle.putString("admin", admin.getText().toString());
            newFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction
                    = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.containerFrame, newFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.error)
                    .setMessage("Sie sind zu dieser Aktion nicht berechtigt")
                    .setNegativeButton("OK", null)
                    .create();
            alertDialog.show();
        }
    }

    private void leaveTeam() {
        if (CheckInternet.isNetworkAvailable(getContext())) {
            if (!MainActivity.userData.getUserRole().equals(MainActivity.ADMIN)) {
                String[] params = new String[]{MainActivity.URL + "leave/team",
                        MainActivity.userData.getToken(), MainActivity.userData.getUsername(),
                        MainActivity.userData.getTeamName()};
                LeaveTeamTask leaveTeamTask = new LeaveTeamTask();
                try {
                    JSONObject response = leaveTeamTask.execute(params).get();
                    String success = response.getString("success");
                    if (success.equals("true")) {
                        String token = response.getString("token");
                        MainActivity.userData.setToken(token);
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.success)
                                .setMessage("Sie haben das Team erfolgreich verlassen!")
                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        goToUserProfile();
                                    }
                                })
                                .create();
                        alertDialog.show();
                    } else {
                        String reason = response.getString("reason");
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.error)
                                .setMessage("Sie konnten das Team nicht verlassen!\n" + reason)
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
                MainActivity.userData.setTeamName("null");
                MainActivity.userData.setUserRole(MainActivity.USER);
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.attention)
                        .setMessage("Sie sind Administrator des Teams!\n " +
                                "Das Team wäre nutzlos ohne Sie! Wenn Sie trotzdem das Team verlassen " +
                                "wollen, bitte löschen Sie dieses nun.")
                        .setNegativeButton("Abbrechen", null)
                        .setPositiveButton("Löschen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteTeam();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        } else {
            AlertDialog alertDialog = CheckInternet.internetNotAvailable(getActivity());
            alertDialog.show();
        }
    }

    private void goToUserProfile() {
        if (CheckInternet.isNetworkAvailable(getContext())) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            Fragment newFragment;
            UserTask userTask = new UserTask();
            String[] params = new String[]{MainActivity.URL + "user",
                    MainActivity.userData.getUsername(),
                    MainActivity.userData.getToken()};
            try {
                JSONObject userData = userTask.execute(params).get();
                if (userData != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("userData", userData.toString());
                    newFragment = new UserProfileFragment();
                    newFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.containerFrame, newFragment);
                    fragmentTransaction.commit();
                } else {
                    newFragment = new PinboardFragment();
                    fragmentTransaction.replace(R.id.containerFrame, newFragment);
                    fragmentTransaction.commit();
                }
            } catch (InterruptedException e) {
                // TODO
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO
                e.printStackTrace();
            }
        } else  {
            AlertDialog alertDialog = CheckInternet.internetNotAvailable(getActivity());
            alertDialog.show();
        }
    }

    private void deleteTeam() {
        if (CheckInternet.isNetworkAvailable(getContext())) {
            String[] params = new String[]{MainActivity.URL + "delete/team",
                    MainActivity.userData.getToken(), MainActivity.userData.getUsername(),
                    MainActivity.userData.getTeamName()};
            DeleteTeamTask deleteTeamTask = new DeleteTeamTask();
            try {
                JSONObject result = deleteTeamTask.execute(params).get();
                String success = result.getString("success");
                if (success.equals("true")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.success)
                            .setMessage("Sie haben das Team erfolgreich gelöscht!")
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    goToUserProfile();
                                }
                            })
                            .create();
                    alertDialog.show();
                    MainActivity.userData.setTeamName("null");
                    MainActivity.userData.setUserRole(MainActivity.USER);
                } else {
                    String reason = result.getString("reason");
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.error)
                            .setMessage("Das Team konnte nicht gelöscht werden!\n" + reason)
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
            AlertDialog alertDialog = CheckInternet.internetNotAvailable(getActivity());
            alertDialog.show();
        }
    }
}
