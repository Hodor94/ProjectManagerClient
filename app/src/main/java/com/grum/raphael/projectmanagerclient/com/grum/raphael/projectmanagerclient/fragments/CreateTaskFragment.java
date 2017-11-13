package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
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
import com.grum.raphael.projectmanagerclient.tasks.CreateTaskTask;
import com.grum.raphael.projectmanagerclient.tasks.GetTeamMembersTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;


public class CreateTaskFragment extends Fragment {

    private String time;
    private String name;
    private String description;
    private String deadline;
    private String worker;
    private ArrayList<String> teamMembers;
    private EditText taskName;
    private EditText taskDescription;
    private DatePicker deadlineDatePicker;
    private TimePicker timePicker;
    private Spinner dropdownWorker;
    private Button createTaskBtn;
    private TextView info;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_task, container, false);
        teamMembers = getTeamMembers();
        taskName = (EditText) rootView.findViewById(R.id.task_name);
        taskDescription = (EditText) rootView.findViewById(R.id.task_description);
        deadlineDatePicker = (DatePicker) rootView.findViewById(R.id.deadline_task);
        timePicker = (TimePicker) rootView.findViewById(R.id.create_task_time);
        timePicker.setIs24HourView(true);
        timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        Calendar calendar = Calendar.getInstance();
        String currentHour = "" + calendar.get(Calendar.HOUR_OF_DAY);
        String currentMinutes = "" + calendar.get(Calendar.MONTH);
        time = currentHour + ":" + currentMinutes + ":00";
        dropdownWorker = (Spinner) rootView.findViewById(R.id.dropdown_task_worker);
        createTaskBtn = (Button) rootView.findViewById(R.id.btn_create_task);
        info = (TextView) rootView.findViewById(R.id.text_create_task_info);
        taskName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                name = taskName.getText().toString();
            }
        });
        taskDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                description = taskDescription.getText().toString();
            }
        });
        Calendar currentDate = Calendar.getInstance();
        deadlineDatePicker.init(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH),
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
        deadline = "" + deadlineDatePicker.getDayOfMonth() + "."
                + (deadlineDatePicker.getMonth() + 1) + "." + deadlineDatePicker.getYear();
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
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(),
                R.layout.drop_down, R.id.drop_down_element, teamMembers);
        dropdownWorker.setAdapter(arrayAdapter);
        dropdownWorker.setSelection(0);
        dropdownWorker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                worker = dropdownWorker.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        createTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTask();
            }
        });
        return rootView;
    }

    private String concatenateDeadline(String deadline, String time) {
        return deadline + " " + time;
    }

    private void createTask() {
        info.setText("");
        deadline = concatenateDeadline(deadline, time);
        if (MainActivity.userData.getUserRole().equals(MainActivity.ADMIN)) {
            if (validateInput(name, description, worker)) {
                if (validateDeadline()) {
                    CreateTaskTask createTaskTask = new CreateTaskTask();
                    String[] params = new String[]{MainActivity.URL + "create/task",
                            MainActivity.userData.getToken(), name, description, worker,
                            MainActivity.userData.getTeamName(), deadline};
                    try {
                        JSONObject result = createTaskTask.execute(params).get();
                        String success = result.getString("success");
                        if (success.equals("true")) {
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.success)
                                    .setMessage("Die Aufgabe " + name + " wurde erfolgreich erstellt!")
                                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Fragment newFragment = new TaskFragment();
                                            FragmentTransaction transaction
                                                    = getFragmentManager().beginTransaction();
                                            transaction.replace(R.id.containerFrame, newFragment);
                                            transaction.commit();
                                        }
                                    })
                                    .create();
                            alertDialog.show();
                        } else {
                            String reason = result.getString("reason");
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.error)
                                    .setMessage("Die Aufgabe " + name
                                            + " konnte nicht erstellt werden!\n" + reason)
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
                    info.setText(getResources().getString(R.string.error_deadline));
                }
            } else {
                info.setText(getResources().getString(R.string.error_fields_filled_wrong));
            }
        } else {
            info.setText(getResources().getString(R.string.no_rights));
        }
    }

    private boolean validateInput(String name, String description, String worker) {
        boolean result = false;
        if (name != null && !(name.equals("")) && description != null && !(description.equals(""))
                && worker != null && !(worker.equals(""))) {
            result = true;
        }
        return result;
    }

    private boolean validateDeadline() {
        boolean result = false;
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        Calendar currentDate = Calendar.getInstance();
        Calendar calendarDeadline = Calendar.getInstance();
        try {
            calendarDeadline.setTime(formatter.parse(deadline));
            if (calendarDeadline.getTime().after(currentDate.getTime())) {
                result = true;
            }
        } catch (ParseException e) {
            // DO nothing
        }
        return result;
    }

    private ArrayList<String> getTeamMembers() {
        ArrayList<String> members = new ArrayList<>();
        GetTeamMembersTask getTeamMembersTask = new GetTeamMembersTask();
        String[] params = new String[] {MainActivity.URL + "team/members",
                MainActivity.userData.getToken(), MainActivity.userData.getTeamName()};
        try {
            JSONObject result = getTeamMembersTask.execute(params).get();
            String success = result.getString("success");
            if (success.equals("true")) {
                JSONArray fetchedUsers = result.getJSONArray("members");
                for (int i = 0; i < fetchedUsers.length(); i++) {
                    JSONObject tempUser = fetchedUsers.getJSONObject(i);
                    members.add(tempUser.getString("username"));
                }
            } else {
                String reason = result.getString("reason");
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.error)
                        .setMessage("Die Team-Mitglieder konnten nicht geladen werden!\n" + reason)
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
