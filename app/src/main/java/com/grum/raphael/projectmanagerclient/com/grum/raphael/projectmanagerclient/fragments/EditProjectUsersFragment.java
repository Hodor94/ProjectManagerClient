package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;
import com.grum.raphael.projectmanagerclient.tasks.EditProjectMembershipTask;
import com.grum.raphael.projectmanagerclient.tasks.GetProjectMembersTask;
import com.grum.raphael.projectmanagerclient.tasks.GetTeamMembersTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class EditProjectUsersFragment extends Fragment {

    private String projectName;
    private String projectManagerUsername;
    private TextView header;
    private ListView membersOfProject;
    private ListView notMemberYet;
    private List<String> projectMembers;
    private List<String> teamMembers;
    private ArrayList<String> usernamesToEdit;
    private Button editMembership;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_project_users, container, false);
        Bundle bundle = getArguments();
        projectName = bundle.getString("projectName");
        projectManagerUsername = bundle.getString("username");
        header = (TextView) rootView.findViewById(R.id.project_users_header);
        header.setText(getResources().getString(R.string.project_users_header) + " " + projectName);
        projectMembers = getMembers();
        membersOfProject = (ListView) rootView.findViewById(R.id.project_already_members);
        notMemberYet = (ListView) rootView.findViewById(R.id.project_team_members);
        membersOfProject.setOnItemClickListener(new CheckBoxClick());
        notMemberYet.setOnItemClickListener(new CheckBoxClick());
        editMembership = (Button) rootView.findViewById(R.id.edit_project_membership);
        teamMembers = getTeamMembers();
        extractAlreadyProjectMembers();
        TextView headerMembers = new TextView(getActivity());
        headerMembers.setGravity(Gravity.CENTER);
        headerMembers.setTextColor(Color.BLACK);
        headerMembers.setText(getResources().getString(R.string.label_remove_members));
        headerMembers.setTextSize(MainActivity.DP_TEXT_SIZE);
        membersOfProject.addHeaderView(headerMembers, null, false);
        ArrayAdapter arrayAdapterMembers = new ArrayAdapter(getActivity(),
                R.layout.list_item_checkbox, projectMembers);
        membersOfProject.setAdapter(arrayAdapterMembers);
        TextView headerTeamMembers = new TextView(getActivity());
        headerTeamMembers.setGravity(Gravity.CENTER);
        headerTeamMembers.setTextColor(Color.BLACK);
        headerTeamMembers.setText(getResources().getString(R.string.label_add));
        headerTeamMembers.setTextSize(MainActivity.DP_TEXT_SIZE
        );
        notMemberYet.addHeaderView(headerTeamMembers, null, false);
        ArrayAdapter arrayAdapterTeamMembers = new ArrayAdapter(getActivity(),
                R.layout.list_item_checkbox, teamMembers);
        notMemberYet.setAdapter(arrayAdapterTeamMembers);
        editMembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMembership();
            }
        });
        usernamesToEdit = new ArrayList<>();
        return rootView;
    }

    private void editMembership() {
        if (CheckInternet.isNetworkAvailable(getContext())) {
            if (usernamesToEdit.size() != 0) {
                EditProjectMembershipTask editProjectMembershipTask = new EditProjectMembershipTask();
                try {
                    JSONObject result = editProjectMembershipTask.execute(usernamesToEdit).get();
                    String success = result.getString("success");
                    if (success.equals("true")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.success)
                                .setMessage("Sie haben die Zugehörigkeiten erfolgreich geändert.")
                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        reloadPage();
                                    }
                                })
                                .create();
                        alertDialog.show();
                    } else {
                        String reason = result.getString("reason");
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.error)
                                .setMessage("Die Zugehörigkeiten zum Projekt konnten nicht " +
                                        "erfolgreich bearbeitet werden!\n" + reason)
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
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.error)
                        .setMessage("Sie haben níchts ausgewählt!")
                        .setNegativeButton("OK", null)
                        .create();
                alertDialog.show();
            }
        } else {
            AlertDialog alertDialog = CheckInternet.internetNotAvailable(getActivity());
            alertDialog.show();
        }
    }

    private void reloadPage() {
        Bundle bundle = new Bundle();
        bundle.putString("projectName", projectName);
        bundle.putString("username", projectManagerUsername);
        Fragment newFragment = new EditProjectUsersFragment();
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.containerFrame, newFragment);
        transaction.commit();
    }

    private void extractAlreadyProjectMembers() {
        for (String username : projectMembers) {
            teamMembers.remove(username);
        }
    }

    private List<String> getMembers() {
        List<String> usernames = new ArrayList<>();
        if (CheckInternet.isNetworkAvailable(getContext())) {
            String[] params = new String[]{MainActivity.URL + "project/members",
                    MainActivity.userData.getToken(), projectName, MainActivity.userData.getTeamName(),
                    MainActivity.userData.getUsername()};
            GetProjectMembersTask getProjectMembersTask = new GetProjectMembersTask();
            try {
                JSONObject result = getProjectMembersTask.execute(params).get();
                String success = result.getString("success");
                if (success.equals("true")) {
                    JSONArray membersInfo = result.getJSONArray("members");
                    for (int i = 0; i < membersInfo.length(); i++) {
                        String username = membersInfo.getString(i);
                        if (!username.equals(MainActivity.userData.getUsername())) {
                            usernames.add(username);
                        }
                    }
                } else {
                    String reason = result.getString("reason");
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.error)
                            .setMessage("Die Projekt-Mitglieder konnten nicht geladen werden!\n"
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
            AlertDialog alertDialog = CheckInternet.internetNotAvailable(getActivity());
            alertDialog.show();
        }
        return usernames;
    }

    private List<String> getTeamMembers() {
        List<String> teamMembers = new ArrayList<>();
        if (CheckInternet.isNetworkAvailable(getContext())) {
            String[] params = new String[]{MainActivity.URL + "team/members",
                    MainActivity.userData.getToken(), MainActivity.userData.getTeamName()};
            GetTeamMembersTask getTeamMembersTask = new GetTeamMembersTask();
            try {
                JSONObject result = getTeamMembersTask.execute(params).get();
                String success = result.getString("success");
                if (success.equals("true")) {
                    JSONArray fetchedMembers = result.getJSONArray("members");
                    for (int i = 0; i < fetchedMembers.length(); i++) {
                        JSONObject teamMember = fetchedMembers.getJSONObject(i);
                        String username = teamMember.getString("username");
                        if (!username.equals(projectManagerUsername)) {
                            teamMembers.add(username);
                        }
                    }
                } else {
                    String reason = result.getString("reason");
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.error)
                            .setMessage("Die Team-Mitglierder konnten nicht geladen werden!\n" + reason)
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
        return teamMembers;
    }

    public class CheckBoxClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            final CheckedTextView ctv = (CheckedTextView)arg1;
            ctv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String checkedUsername = ctv.getText().toString();
                    if (ctv.isChecked()) {
                        ctv.setChecked(false);
                        usernamesToEdit.remove(checkedUsername);
                    } else {
                        ctv.setChecked(true);
                        usernamesToEdit.add(checkedUsername);
                    }
                }
            });
        }
    }


}
