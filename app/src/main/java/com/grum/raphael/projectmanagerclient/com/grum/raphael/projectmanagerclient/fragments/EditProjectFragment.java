package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;


import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.DeleteProjectTask;
import com.grum.raphael.projectmanagerclient.tasks.DeleteTeamTask;
import com.grum.raphael.projectmanagerclient.tasks.EditProjectTask;
import com.grum.raphael.projectmanagerclient.tasks.GetProjectTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class EditProjectFragment extends Fragment {

    private Calendar currentDate;
    private String projectName;
    private String description;
    private String manager;
    private String deadline;
    private TextView projectNameTextView;
    private EditText projectDescriptionTextView;
    private DatePicker datePickerDeadline;
    private TimePicker timePicker;
    private TextView projectManagerTextView;
    private Button edit;
    private Button delete;
    private Button editUsers;
    private TextView info;
    private String time;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_project, container, false);
        Bundle bundle = getArguments();
        projectName = bundle.getString("projectName");
        info = (TextView) rootView.findViewById(R.id.edit_project_info);
        projectNameTextView = (TextView) rootView.findViewById(R.id.edit_project_name);
        projectDescriptionTextView = (EditText) rootView.findViewById(R.id.edit_project_description);
        datePickerDeadline = (DatePicker) rootView.findViewById(R.id.edit_project_deadline);
        timePicker = (TimePicker) rootView.findViewById(R.id.time_picker_edit_project);
        timePicker.setIs24HourView(true);
        timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        Calendar calendar = Calendar.getInstance();
        String currentHour = "" + calendar.get(Calendar.HOUR_OF_DAY);
        String currentMinutes = "" + calendar.get(Calendar.MONTH);
        time = currentHour + ":" + currentMinutes + ":00";
        projectManagerTextView = (TextView) rootView.findViewById(R.id.edit_project_manager);
        projectManagerTextView.setText(MainActivity.userData.getUsername());
        edit = (Button) rootView.findViewById(R.id.edit_project_button);
        delete = (Button) rootView.findViewById(R.id.edit_project_delete);
        editUsers = (Button) rootView.findViewById(R.id.edit_project_users);
        getProjectData();
        projectNameTextView.setText(projectName);
        projectDescriptionTextView.setText(description);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String hour;
                String minutes;
                if (minute < 10) {
                    minutes = "0" + minute;
                } else {
                    minutes = "" + minute;
                }
                if (hourOfDay < 10) {
                    hour = "0" + hourOfDay;
                } else {
                    hour = "" + hourOfDay;
                }
                time = hour + ":" + minutes + ":00";
            }
        });
        currentDate = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        try {
            currentDate.setTime(formatter.parse(deadline));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        datePickerDeadline.init(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH),
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
        deadline = "" + datePickerDeadline.getDayOfMonth() + "." + (datePickerDeadline.getMonth() + 1) + "."
                + datePickerDeadline.getYear();
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProject();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProject();
            }
        });
        editUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUsers();
            }
        });
        if (!manager.equals(MainActivity.userData.getUsername())) {
            edit.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            projectDescriptionTextView.setFocusable(false);
            datePickerDeadline.setFocusable(false);
            editUsers.setVisibility(View.GONE);
        }
        return rootView;
    }

    private void editUsers() {
        Bundle bundle = new Bundle();
        bundle.putString("projectName", projectName);
        bundle.putString("username", manager);
        Fragment newFragment = new EditProjectUsersFragment();
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.containerFrame, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void deleteProject() {
        if (manager.equals(MainActivity.userData.getUsername())) {
            String[] params = new String[] {MainActivity.URL + "delete/project",
                    MainActivity.userData.getToken(), projectName,
                    MainActivity.userData.getTeamName(), MainActivity.userData.getUsername()};
            DeleteProjectTask deleteProjectTask = new DeleteProjectTask();
            try {
                JSONObject result = deleteProjectTask.execute(params).get();
                String success = result.getString("success");
                if (success.equals("true")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.success)
                            .setMessage("Sie haben das Projekt " + projectName
                                    + " erfolgreich gelöscht!")
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FragmentTransaction transaction = getFragmentManager()
                                            .beginTransaction();
                                    transaction.replace(R.id.containerFrame, new ProjectsFragment());
                                    transaction.commit();
                                }
                            })
                            .create();
                    alertDialog.show();
                } else {
                    String reason = result.getString("reason");
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.error)
                            .setMessage("Das Projekt " + projectName
                                    + " konnte nicht gelöscht werden!\n" + reason)
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
                    .setMessage(getResources().getString(R.string.no_rights))
                    .setNegativeButton("OK", null)
                    .create();
            alertDialog.show();
        }
    }

    private void editProject() {
        info.setText("");
        deadline = concatenateDeadline(deadline, time);
        if (description != null && !(description.equals("")) && deadline != null
                && !(deadline.equals(""))) {
            if (validateDeadline()) {
                description = projectDescriptionTextView.getText().toString();
                String[] params = new String[]{MainActivity.URL + "edit/project",
                        MainActivity.userData.getToken(), MainActivity.userData.getUsername(),
                        projectName, MainActivity.userData.getTeamName(), description, deadline};
                EditProjectTask editProjectTask = new EditProjectTask();
                try {
                    JSONObject result = editProjectTask.execute(params).get();
                    String success = result.getString("success");
                    if (success.equals("true")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.success)
                                .setMessage("Sie haben das Projekt erfolgreich editiert!")
                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        leavePage();
                                    }
                                })
                                .create();
                        alertDialog.show();
                    } else {
                        String reason = result.getString("reason");
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.error)
                                .setMessage("Das Projekt konnte nicht bearbeitet werden!\n" + reason)
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
                info. setText(getResources().getString(R.string.error_deadline));
            }
        } else {
            info.setText(R.string.error_fields_empty);
        }
    }

    private String concatenateDeadline(String deadline, String time) {
        return deadline + " " + time;
    }

    private boolean validateDeadline() {
        boolean result = false;
        Calendar chosenDeadline = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        try {
            chosenDeadline.setTime(formatter.parse(deadline));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (currentDate.before(chosenDeadline)) {
            result = true;
        }
        return result;
    }

    private void leavePage() {
        Fragment fragment = new ProjectsFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.containerFrame, fragment);
        transaction.commit();
    }

    private void getProjectData() {
        String[] params = new String[]{MainActivity.URL + "project",
                MainActivity.userData.getToken(), projectName,
                MainActivity.userData.getTeamName()};
        GetProjectTask getProjectTask = new GetProjectTask();
        try {
            JSONObject result = getProjectTask.execute(params).get();
            String success = result.getString("success");
            if (success.equals("true")) {
                JSONObject projectData = new JSONObject(result.getString("project"));
                description = projectData.getString("description");
                deadline = projectData.getString("deadline");
                JSONObject projectManager = new JSONObject(projectData.getString("manager"));
                manager = projectManager.getString("username");
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.error)
                        .setMessage("Das Projekt " + projectName + " konnte nicht geladen werden!")
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
    }
}
