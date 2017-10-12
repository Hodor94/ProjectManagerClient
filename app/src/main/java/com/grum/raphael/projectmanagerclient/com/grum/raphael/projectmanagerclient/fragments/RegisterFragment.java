package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.CreateRegisterTask;
import com.grum.raphael.projectmanagerclient.tasks.GetRegistersTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RegisterFragment extends Fragment {

    private List<String> registers;
    private ListView registersList;
    private Button createRegister;
    private TextView info;
    private EditText registerName;
    private String name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        info = (TextView) rootView.findViewById(R.id.text_info_register_fragment);
        info.setVisibility(View.GONE);
        registerName = (EditText) rootView.findViewById(R.id.register_name);
        registerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                name = registerName.getText().toString();
            }
        });
        name = registerName.getText().toString();
        createRegister = (Button) rootView.findViewById(R.id.create_register);
        createRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRegister();
            }
        });
        registersList = (ListView) rootView.findViewById(R.id.register_list);
        TextView listHeader = new TextView(getContext());
        listHeader.setText("Gruppen:");
        registersList.addHeaderView(listHeader);
        List<String> registers = getRegisters();
        if (registers != null) {
            if (registers.size() != 0) {
                ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item,
                        R.id.list_element, registers);
                registersList.setAdapter(arrayAdapter);
            } else {
                // TODO set info txt to inform about missing registers
                registersList.setVisibility(View.GONE);
            }
        } else {
            // TODO
        }
        return rootView;
    }

    private void createRegister() {
        if (!name.equals("") && name != null) {
            info.setVisibility(View.GONE);
            CreateRegisterTask createRegisterTask = new CreateRegisterTask();
            String[] params = new String[]{MainActivity.URL + "create/register",
                    MainActivity.userData.getToken(), MainActivity.userData.getTeamName(),
                    name, MainActivity.userData.getUsername()};
            try {
                JSONObject result = createRegisterTask.execute(params).get();
                String success = result.getString("success");
                if (success.equals("true")) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.success)
                            .setMessage("Die Gruppe wurde erfolgreich angelegt!")
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FragmentTransaction transaction
                                            = getFragmentManager().beginTransaction();
                                    transaction.replace(R.id.pager_team_profile,
                                            new RegisterFragment());
                                    transaction.commit();
                                }
                            }).create();
                    alertDialog.show();
                } else {
                    String reason = result.getString("reason");
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.error)
                            .setMessage("Die Gruppe konnte nicht angelegt werden!\n"
                                    + reason)
                            .setNegativeButton("OK", null)
                            .create();
                    alertDialog.show();
                }
            } catch (InterruptedException e) {
                // TODO
            } catch (ExecutionException e) {
                // TODO
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO
                e.printStackTrace();
            }
        } else {
            info.setText(R.string.error_create_team_input);
            info.setVisibility(View.VISIBLE);
        }
    }

    private List<String> getRegisters() {
        String[] params = new String[]{MainActivity.URL + "team/registers",
                MainActivity.userData.getToken(), MainActivity.userData.getTeamName()};
        registers = new ArrayList<>();
        GetRegistersTask getRegistersTask = new GetRegistersTask();
        JSONObject response = null;
        JSONArray fetchedRegisters;
        try {
            response = getRegistersTask.execute(params).get();
            String success = response.getString("success");
            if (success.equals("true")) {
                fetchedRegisters = response.getJSONArray("registers");
                if (fetchedRegisters != null) {
                    for (int i = 0; i < fetchedRegisters.length(); i++) {
                        registers.add(fetchedRegisters.getString(i));
                    }
                }
            }
        } catch (InterruptedException e) {
            // TODO
            e.printStackTrace();
            registers = null;
        } catch (ExecutionException e) {
            // TODO
            e.printStackTrace();
            registers = null;
        } catch (JSONException e) {
            // TODO
            e.printStackTrace();
            registers = null;
        }
        return registers;
    }
}