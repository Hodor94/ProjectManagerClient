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
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;
import com.grum.raphael.projectmanagerclient.tasks.EditTeamTask;
import com.grum.raphael.projectmanagerclient.tasks.TeamTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.concurrent.ExecutionException;

public class EditTeamFragment extends Fragment {

    private TextView info;
    private TextView teamName;
    private EditText teamDescription;
    private TextView admin;
    private Button btn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        View rootView = inflater.inflate(R.layout.fragment_edit_team, container, false);
        info = (TextView) rootView.findViewById(R.id.edit_team_info);
        teamName = (TextView) rootView.findViewById(R.id.edit_team_profile_name);
        teamDescription = (EditText) rootView.findViewById(R.id.edit_team_profile_description);
        admin = (TextView) rootView.findViewById(R.id.edit_team_profile_admin);
        String teamName = bundle.getString("teamName");
        String teamDescription = bundle.getString("teamDescription");
        String admin = bundle.getString("admin");
        this.teamName.setText(teamName);
        this.teamDescription.setText(teamDescription);
        this.admin.setText(admin);
        btn = (Button) rootView.findViewById(R.id.edit_team_btn);
        if (MainActivity.userData.getUserRole().equals(MainActivity.ADMIN)) {
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    editTeam();
                }
            });
        } else {
            btn.setVisibility(View.INVISIBLE);
        }

        return rootView;
    }


    public TextView getTeamName() {
        return teamName;
    }

    public EditText getTeamDescription() {
        return teamDescription;
    }

    public TextView getAdmin() {
        return admin;
    }

    public void editTeam() {
        info.setText("");
        if (CheckInternet.isNetworkAvailable(getContext())) {
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
                        // TODO open team profile
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.success)
                                .setMessage(R.string.team_edited)
                                .setNegativeButton("OK", null)
                                .create();
                        alertDialog.show();
                    } else {
                        String reason = result.getString("reason");
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
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
                info.setText(getResources().getString(R.string.error_fields_filled_wrong));
            }
        } else {
            AlertDialog alertDialog = CheckInternet.internetNotAvailable(getActivity());
            alertDialog.show();
        }
    }

}