package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grum.raphael.projectmanagerclient.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChooseAppointmentFragment extends Fragment {

    private final String DEADLINE = "DEADLINE";
    private TextView header;
    private ArrayList<JSONObject> appointments;
    private ArrayList<String> appointmentLabels;
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_choose_appointment, container, false);
        header = (TextView) rootView.findViewById(R.id.header_choose_appointment);
        appointments = new ArrayList<>();
        appointmentLabels = new ArrayList<>();
        listView = (ListView) rootView.findViewById(R.id.list_choose_appointment);
        Bundle bundle = getArguments();
        String date = bundle.getString("date");
        header.setText(getResources().getString(R.string.choose_appointment) + " " + date + ":");
        ArrayList<String> fetchedApponintments = bundle.getStringArrayList("appointments");
        for (String jsonText : fetchedApponintments) {
            try {
                appointments.add(new JSONObject(jsonText));
            } catch (JSONException e) {
                // Never reached
            }
        }
        for (JSONObject jsonObject : appointments) {
            try {
                String appointmentsName = jsonObject.getString("name");
                String appointmentsDate = jsonObject.getString("deadline");
                String[] dateAndTimeSplit = appointmentsDate.split(" ");
                String time = dateAndTimeSplit[1];
                appointmentLabels.add(appointmentsName + ": " + time);
            } catch (JSONException e) {
                // Never reached
            }
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.list_item,
                R.id.list_element, appointmentLabels);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(setUpClickActionsForList());
        return rootView;
    }

    private AdapterView.OnItemClickListener setUpClickActionsForList() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject appointment = null;
                String data = (String) parent.getAdapter().getItem(position);
                if (!identifyAsDeadline(data)) {
                    String[] dataSplit = data.split(" ");
                    dataSplit[0] = dataSplit[0].substring(0, dataSplit[0].length() - 1);
                    for (JSONObject object : appointments) {
                        try {
                            String name = object.getString("name");
                            String time = object.getString("deadline").split(" ")[1];
                            if (name.equals(dataSplit[0]) && time.equals(dataSplit[1])) {
                                appointment = object;
                                break;
                            }
                        } catch (JSONException e) {
                            // Never reached
                        }
                    }
                } else {
                    for (JSONObject object : appointments) {
                        try {
                            String isDeadline = object.getString("isDeadline");
                            if (isDeadline.equals("true")) {
                                appointment = object;
                            }
                        } catch (JSONException e) {
                            // Never reached!
                        }
                    }
                }
                if (appointment != null) {
                    showAppointmentProfile(appointment);
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.error)
                            .setMessage("Interner Fehler! Das Meeting konnte nicht gefunden werden!")
                            .setNegativeButton("OK", null)
                            .create();
                    alertDialog.show();
                }
            }
        };
    }

    private boolean identifyAsDeadline(String data) {
        boolean result = false;
        if (data.contains(DEADLINE)) {
            result = true;
        }
        return result;
    }

    private void showAppointmentProfile(JSONObject appointment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("appointment", appointment.toString());
        Fragment newFragment = new AppointmentProfileFragment();
        newFragment.setArguments(bundle);
        transaction.replace(R.id.containerFrame, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }
}
