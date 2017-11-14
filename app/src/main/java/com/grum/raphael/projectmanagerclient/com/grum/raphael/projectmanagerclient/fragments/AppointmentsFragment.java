package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;
import com.grum.raphael.projectmanagerclient.tasks.GetMyProjectsTask;
import com.grum.raphael.projectmanagerclient.tasks.GetProjectsAppointmentsTask;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class AppointmentsFragment extends Fragment {

    private Spinner chooseProject;
    private Button createAppointment;
    private List<String> myProjects;
    private List<JSONObject> appointments;
    private String chosenProject;
    public final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_appointments, container, false);
        CaldroidFragment caldroidFragment = new CaldroidFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        caldroidFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_caldroid, caldroidFragment).commit();
        createAppointment = (Button) rootView.findViewById(R.id.btn_create_appointment);
        chooseProject = (Spinner) rootView.findViewById(R.id.choose_project);
        myProjects = getMyProjects();
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(),
                R.layout.drop_down, R.id.drop_down_element, myProjects);
        chooseProject.setAdapter(arrayAdapter);
        if (myProjects.size() == 0) {
            chooseProject.setVisibility(View.GONE);
            LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.frg_appointments);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ABOVE, R.id.choose_project);
            TextView info = new TextView(getActivity());
            info.setTextColor(Color.BLACK);
            info.setTextSize(MainActivity.DP_TEXT_SIZE);
            info.setText(getResources().getString(R.string.no_projects));
            info.setGravity(Gravity.CENTER);
            info.setLayoutParams(params);
            layout.addView(info, 0);
        } else {
            chosenProject = (String) chooseProject.getSelectedItem();
            chooseProject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    chosenProject = (String) chooseProject.getAdapter().getItem(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            appointments = getAppointments(chosenProject);
            highlightExistingEvents(caldroidFragment, appointments);
        }
        createAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAppointment();
            }

        });
        caldroidFragment.setCaldroidListener(setUpCaldroidListener());
        return rootView;
    }

    private CaldroidListener setUpCaldroidListener() {
        return new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                String dateString = formatter.format(date);
                ArrayList<String> relevantAppointments = new ArrayList<>();
                for (JSONObject appointment : appointments) {
                    try {
                        String appointmentsDate = appointment.getString("deadline");
                        String[] dateAndTimeSplit = appointmentsDate.split(" ");
                        if (dateAndTimeSplit[0].equals(dateString)) {
                            relevantAppointments.add(appointment.toString());
                        }
                    } catch (JSONException e) {
                        // TODO
                        e.printStackTrace();
                    }
                }
                if (relevantAppointments.size() != 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString("date", dateString);
                    bundle.putStringArrayList("appointments", relevantAppointments);
                    Fragment newFragment = new ChooseAppointmentFragment();
                    newFragment.setArguments(bundle);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.containerFrame, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    Toast.makeText(getContext(), "Am " + dateString
                            + " sind keine Meetings geplant!", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void createAppointment() {
        CreateAppointmentFragment createAppointmentFragment = new CreateAppointmentFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.containerFrame, createAppointmentFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void highlightExistingEvents(CaldroidFragment caldroidFragment,
                                         List<JSONObject> meetings) {
        for (JSONObject jsonObject : meetings) {
            try {
                ColorDrawable drawableColor
                        = new ColorDrawable(ContextCompat.getColor(getContext(), R.color.toolbar));
                String date = jsonObject.getString("deadline");
                Calendar currentMeeting = Calendar.getInstance();
                currentMeeting.setTime(formatter.parse(date));
                caldroidFragment.setBackgroundDrawableForDate(drawableColor, currentMeeting.getTime());
            } catch (JSONException e) {
                // TODO
                e.printStackTrace();
            } catch (ParseException e) {
                // TODO
                e.printStackTrace();
            }
        }
    }

    private ArrayList<JSONObject> getAppointments(String chosenProject) {
        ArrayList<JSONObject> appointments = new ArrayList<>();
        if (CheckInternet.isNetworkAvailable(getContext())) {
            GetProjectsAppointmentsTask getProjectsAppointmentsTask = new GetProjectsAppointmentsTask();
            String[] params = new String[]{MainActivity.URL + "project/appointments",
                    MainActivity.userData.getToken(), chosenProject,
                    MainActivity.userData.getTeamName()};
            try {
                JSONObject result = getProjectsAppointmentsTask.execute(params).get();
                String success = result.getString("success");
                if (success.equals("true")) {
                    JSONArray fetchedAppointments = result.getJSONArray("appointments");
                    for (int i = 0; i < fetchedAppointments.length(); i++) {
                        JSONObject currentAppointment = fetchedAppointments.getJSONObject(i);
                        appointments.add(currentAppointment);
                    }
                } else {
                    String reason = result.getString("reason");
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.error)
                            .setMessage("Die Meetings des Projekts " + chosenProject + " konnten " +
                                    "nicht geladen werden!\n" + reason)
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
        return appointments;
    }

    private ArrayList<String> getMyProjects() {
        ArrayList<String> myProjects = new ArrayList<>();
        if (CheckInternet.isNetworkAvailable(getContext())) {
            String[] params = new String[]{MainActivity.URL + "user/projects",
                    MainActivity.userData.getToken(), MainActivity.userData.getUsername(),
                    MainActivity.userData.getTeamName()};
            GetMyProjectsTask getMyProjectsTask = new GetMyProjectsTask();
            try {
                JSONObject result = getMyProjectsTask.execute(params).get();
                String success = result.getString("success");
                if (success.equals("true")) {
                    JSONArray projects = result.getJSONArray("projects");
                    for (int i = 0; i < projects.length(); i++) {
                        String projectName = projects.getString(i);
                        myProjects.add(projectName);
                    }
                } else {
                    String reason = result.getString("reason");
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.error)
                            .setMessage("Ihre Projekte konnten nicht geladen werden!\n")
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
        return myProjects;
    }


}
