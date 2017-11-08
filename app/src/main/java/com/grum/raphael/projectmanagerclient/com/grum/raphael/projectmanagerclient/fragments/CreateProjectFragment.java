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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.CreateProjectTask;
import com.grum.raphael.projectmanagerclient.tasks.GetTeamMembersTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CreateProjectFragment extends Fragment {

    private Spinner dropDownProjectManager;
    private EditText projectNameItem;
    private EditText projectDescriptionItem;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button createProject;
    private TextView info;
    private String projectName;
    private String projectDescription;
    private String projectManager;
    private String deadline;
    private String time;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_project, container, false);
        List<String> members = getTeamMembers();
        info = (TextView) rootView.findViewById(R.id.text_create_project_info);
        datePicker = (DatePicker) rootView.findViewById(R.id.deadline_project);
        timePicker = (TimePicker) rootView.findViewById(R.id.time_picker_create_project);
        timePicker.setIs24HourView(true);
        Calendar calendar = Calendar.getInstance();
        String currentHour = "" + calendar.get(Calendar.HOUR_OF_DAY);
        String currentMinutes = "" + calendar.get(Calendar.MONTH);
        time = currentHour + ":" + currentMinutes + ":00";
        createProject = (Button) rootView.findViewById(R.id.btn_create_project);
        dropDownProjectManager = (Spinner) rootView.findViewById(R.id.dropdown_project_owner);
        projectNameItem = (EditText) rootView.findViewById(R.id.project_name);
        projectDescriptionItem = (EditText)rootView.findViewById(R.id.project_description);
        projectNameItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                projectName = projectNameItem.getText().toString().trim();
            }
        });
        projectDescriptionItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                projectDescription = projectDescriptionItem.getText().toString().trim();
            }
        });
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(),
                R.layout.drop_down, R.id.drop_down_element, members);
        dropDownProjectManager.setAdapter(arrayAdapter);
        dropDownProjectManager.setSelection(0);
        dropDownProjectManager.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                projectManager = dropDownProjectManager.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Calendar currentDate = Calendar.getInstance();
        deadline = "" + datePicker.getDayOfMonth() + "." + (datePicker.getMonth() + 1) + "."
                + datePicker.getYear();
        datePicker.init(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                        String mDay;
                        String mMonth;
                        String mYear;
                        if (dayOfMonth < 10) {
                            mDay = "0" + dayOfMonth;
                        } else {
                            mDay = "" + dayOfMonth;
                        }
                        if (monthOfYear < 9) {
                            mMonth = "0" + (monthOfYear + 1);
                        } else {
                            mMonth = "" + (monthOfYear + 1);
                        }
                        mYear = "" + year;
                        // The 00:00:00 is for the formatter on server side
                        deadline = mDay + "." + mMonth + "." + mYear;
                    }
                });
        createProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info.setText("");
                deadline = concatenateDeadline(deadline, time);
                createNewProject(MainActivity.userData.getTeamName(), projectName,
                        projectDescription, projectManager, deadline);
            }
        });
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                time = "" + hourOfDay + ":" + minute + ":00";
            }
        });
        return rootView;
    }

    private String concatenateDeadline(String deadline, String time) {
        return deadline + " " + time;
    }

    private void createNewProject(String teamName, String projectName, String projectDescription,
                                  String projectManager, String deadline) {
        if (MainActivity.userData.getUserRole().equals(MainActivity.ADMIN)) {
            if (validateInput(teamName, projectName, projectDescription, projectManager, deadline)) {
                String[] params = new String[]{MainActivity.URL + "create/project",
                        MainActivity.userData.getToken(), teamName, projectName, projectDescription,
                        projectManager, deadline};
                CreateProjectTask createProjectTask = new CreateProjectTask();
                try {
                    JSONObject result = createProjectTask.execute(params).get();
                    String success = result.getString("success");

                    if (success.equals("true")) {
                        String adminOfProject = result.getString("adminOfProject");
                        String token = result.getString("token");
                        if (!MainActivity.userData.getUserRole().equals(MainActivity.ADMIN)) {
                            MainActivity.userData.setUserRole(MainActivity.PROJECT_OWNER);
                        }
                        MainActivity.userData.setToken(token);
                        MainActivity.userData.setAdminOfProject(adminOfProject);
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.success)
                                .setMessage("Das Projekt " + projectName + " wurde erfolgreich angelegt")
                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FragmentTransaction transaction
                                                = getFragmentManager().beginTransaction();
                                        transaction.replace(R.id.pager_projects_profile,
                                                new CreateProjectFragment());
                                        transaction.commit();
                                    }
                                }).create();
                        alertDialog.show();
                    } else {
                        String reason = result.getString("reason");
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.error)
                                .setMessage("Das Projekt " + projectName
                                        + " konnte nicht angelegt werden!\n" + reason)
                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FragmentTransaction transaction
                                                = getFragmentManager().beginTransaction();
                                        transaction.replace(R.id.pager_projects_profile,
                                                new CreateProjectFragment());
                                        transaction.commit();
                                    }
                                }).create();
                        alertDialog.show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // TODO
                } catch (ExecutionException e) {
                    // TODO
                    e.printStackTrace();
                } catch (JSONException e) {
                    // TODO
                    e.printStackTrace();
                }

            }
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.error)
                    .setMessage(getResources().getString(R.string.no_rights))
                    .setNegativeButton("OK", null)
                    .create();
            alertDialog.show();
        }
    }

    private boolean validateInput(String teamName, String projectName, String projectDescription,
                                  String projectManager, String deadline) {
        boolean result = false;
        if (teamName != null && !teamName.equals("") && projectName !=  null &&
                !projectName.equals("null") && !projectName.equals("") && projectDescription != null
                && !projectDescription.equals("") && projectManager != null
                && !projectManager.equals("") && !projectManager.equals("null")
                && deadline != null) {
            Calendar currentTime = Calendar.getInstance();
            Calendar choosenTime = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
            try {
                choosenTime.setTime(formatter.parse(deadline));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (currentTime.before(choosenTime)) {
                result = true;
            } else {
                info.setText(info.getText().toString() + "\n"
                        + getResources().getString(R.string.error_deadline));
            }
        } else {
            info.setText(R.string.error_fields_filled_wrong);
        }
        return result;
    }

    private List<String> getTeamMembers() {
        List<String> members = new ArrayList<>();
        GetTeamMembersTask getTeamMembersTask = new GetTeamMembersTask();
        String[] params = new String[] {MainActivity.URL + "team/members",
                MainActivity.userData.getToken(), MainActivity.userData.getTeamName()};
        JSONObject result;
        try {
            result = getTeamMembersTask.execute(params).get();
            String success = result.getString("success");
            if (success.equals("true")) {
                JSONArray array = result.getJSONArray("members");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject temp = array.getJSONObject(i);
                    members.add(temp.getString("username"));
                }
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.error)
                        .setMessage(R.string.error_get_members)
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
        return members;
    }
}
