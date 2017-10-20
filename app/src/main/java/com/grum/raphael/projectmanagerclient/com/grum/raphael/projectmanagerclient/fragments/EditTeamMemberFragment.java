package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.EditTeamMemberTask;
import com.grum.raphael.projectmanagerclient.tasks.GetRegistersTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class EditTeamMemberFragment extends Fragment {

    private TextView username;
    private Spinner dropDownRegsiters;
    private Button ready;
    private List<JSONObject> registers;
    private String registerName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_team_member, container, false);
        registers = new ArrayList<>();
        Bundle bundle = getArguments();
        final String username = bundle.getString("username");
        registerName = bundle.getString("register");
        this.username = (TextView) rootView.findViewById(R.id.text_username);
        this.username.setText(username);
        ArrayList<String> registerNames = getRegisters();
        registerNames.add(0, getResources().getString(R.string.blank));
        int indexOfInitialRegister = 0;
        for (int i = 0; i < registerNames.size(); i++) {
            if (registerNames.get(i).equals(registerName)) {
                indexOfInitialRegister = i;
                break;
            }
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(),
                R.layout.drop_down, R.id.drop_down_element, registerNames);
        dropDownRegsiters = (Spinner) rootView.findViewById(R.id.drop_down_registers);
        dropDownRegsiters.setAdapter(arrayAdapter);
        dropDownRegsiters.setSelection(indexOfInitialRegister);
        ready = (Button) rootView.findViewById(R.id.btn_finish_edit_team_member);
        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.userData.getUserRole().equals(MainActivity.ADMIN)) {
                    editTeamMember(username);
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.error)
                            .setMessage("Sie haben keine Berechtigung für diese Aktion")
                            .setNegativeButton("OK", null)
                            .create();
                    alertDialog.show();
                }

            }
        });
        return rootView;
    }

    private ArrayList<String> getRegisters() {
        String[] params = new String[]{MainActivity.URL + "team/registers",
                MainActivity.userData.getToken(), MainActivity.userData.getTeamName()};
        GetRegistersTask getRegistersTask = new GetRegistersTask();
        JSONObject result;
        ArrayList<String> registerNames = new ArrayList<>();
        try {
            result = getRegistersTask.execute(params).get();
            String success = result.getString("success");
            if (success.equals("true")) {
                JSONArray registers = result.getJSONArray("registers");
                if (registers.length() != 0) {
                    for (int i = 0; i < registers.length(); i++) {
                        JSONObject tempJsonObject = registers.getJSONObject(i);
                        this.registers.add(tempJsonObject);
                        registerNames.add(tempJsonObject.getString("registerName"));

                    }
                }
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.error)
                        .setMessage("Die Gruppen des Teams konnten nicht abgefragt werden!")
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int index = getActivity().getFragmentManager()
                                        .getBackStackEntryCount() - 1;
                                FragmentManager.BackStackEntry backEntry = getFragmentManager().getBackStackEntryAt(index);
                                String tag = backEntry.getName();
                                Fragment fragment = getFragmentManager().findFragmentByTag(tag);
                                FragmentTransaction fragmentTransaction
                                        = getFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.pager_team_profile, fragment);
                                fragmentTransaction.commit();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return registerNames;
    }

    private void editTeamMember(String username) {
        registerName = dropDownRegsiters.getSelectedItem().toString();
        String[] params = new String[] {MainActivity.URL + "member/edit",
                MainActivity.userData.getToken(), username, registerName,
                MainActivity.userData.getUsername(), MainActivity.userData.getTeamName()};
        EditTeamMemberTask editTeamMemberTask = new EditTeamMemberTask();
        try {
            JSONObject result = editTeamMemberTask.execute(params).get();
            String success = result.getString("success");
            if (success.equals("true")) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.success)
                        .setMessage("Sie haben die Daten des Team-Mitglieds erfolgreich bearbeitet!")
                        .setNegativeButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FragmentTransaction transaction
                                                = getFragmentManager().beginTransaction();
                                        transaction.replace(R.id.pager_team_profile,
                                                new TeamMembersFragment());
                                        transaction.commit();
                                    }
                                })
                        .create();
                alertDialog.show();
            } else {
                String reason = result.getString("reason");
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.error)
                        .setMessage("Das Team-Mitglied konnte nicht bearbeitet werden!\n" + reason)
                        .setNegativeButton("OK", null)
                        .create();
                alertDialog.show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
