package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import yuku.ambilwarna.AmbilWarnaDialog;

public class RegisterFragment extends Fragment {

    private List<JSONObject> registers;
    private ListView registersList;
    private Button createRegister;
    private TextView info;
    private EditText registerName;
    private String name;
    private int defaultColor;
    private ImageView selectColor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        selectColor = (ImageView) rootView.findViewById(R.id.select_color_for_register);
        selectColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPickerDialog(false);
            }
        });
        defaultColor = R.color.white;
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
                if (MainActivity.userData.getUserRole().equals(MainActivity.ADMIN)) {
                    createRegister();
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
        registersList = (ListView) rootView.findViewById(R.id.register_list);
        TextView listHeader = new TextView(getContext());
        listHeader.setText("Gruppen:");
        listHeader.setTextSize(MainActivity.DP_TEXT_SIZE);
        listHeader.setGravity(Gravity.CENTER);
        listHeader.setTextColor(Color.BLACK);
        registersList.addHeaderView(listHeader);
        this.registers = getRegisters();
        ArrayList<String> registerNames = getRegisterNames();
        if (registers != null) {
            ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item,
                    R.id.list_element, registerNames);
            registersList.setAdapter(arrayAdapter);
        }
        registersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("registerName", registerName.getText().toString());
                Fragment newFragment = new EditTeamFragment();
                newFragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.containerFrame, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return rootView;
    }

    private ArrayList<String> getRegisterNames() {
        ArrayList<String> result = new ArrayList<>();
        if (registers != null && registers.size() != 0) {
            for (JSONObject jsonObject : registers) {
                try {
                    result.add(jsonObject.getString("registerName"));
                } catch (JSONException e) {
                    // TODO alert dialog
                }
            }
        }
        return result;
    }

    private void createRegister() {
        if (!name.equals("") && name != null) {
            info.setVisibility(View.GONE);
            CreateRegisterTask createRegisterTask = new CreateRegisterTask();
            String[] params = new String[]{MainActivity.URL + "create/register",
                    MainActivity.userData.getToken(), MainActivity.userData.getTeamName(),
                    name, MainActivity.userData.getUsername(), "" + defaultColor};
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

    private List<JSONObject> getRegisters() {
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
                        registers.add(fetchedRegisters.getJSONObject(i));
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

    private void openColorPickerDialog(boolean AlphaSupport) {

        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(getActivity(), defaultColor, AlphaSupport, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog ambilWarnaDialog, int color) {
                defaultColor = color;
                selectColor.setBackgroundColor(defaultColor);
            }

            @Override
            public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {
                Toast.makeText(getActivity(), "Color Picker Closed", Toast.LENGTH_SHORT).show();
            }
        });
        ambilWarnaDialog.show();

    }

}