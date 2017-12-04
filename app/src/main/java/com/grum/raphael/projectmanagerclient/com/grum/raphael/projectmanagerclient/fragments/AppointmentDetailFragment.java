package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;
import com.grum.raphael.projectmanagerclient.tasks.DeleteAppointmentTask;
import com.grum.raphael.projectmanagerclient.tasks.EditAppointmentTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;


public class AppointmentDetailFragment extends Fragment {

    private boolean isDeadline;
    private String id;
    private String deadline;
    private String time;
    private String name;
    private String description;
    private String date;
    private EditText nameText;
    private EditText descriptionText;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button edit;
    private Button delete;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_appointment_detail, container, false);
        Bundle bundle = getArguments();
        fetchDataFromBundle(bundle);
        nameText = (EditText) rootView.findViewById(R.id.appointment_detail_name);
        nameText.setFilters(new InputFilter[] {MainActivity.EMOJI_FILTER});
        if (isDeadline) {
            nameText.setFocusable(false);
            nameText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Der Titel des Meetings kann nicht verändert werden" +
                            ", da es eine Deadline ist!", Toast.LENGTH_LONG).show();
                }
            });
        }
        nameText.addTextChangedListener(nameChanged());
        descriptionText = (EditText) rootView.findViewById(R.id.appointment_detail_description);
        descriptionText.setFilters(new InputFilter[] {MainActivity.EMOJI_FILTER});
        descriptionText.addTextChangedListener(descriptionChanged());
        datePicker = (DatePicker) rootView.findViewById(R.id.appointment_detail_date);
        timePicker = (TimePicker) rootView.findViewById(R.id.appointment_detail_time);
        timePicker.setIs24HourView(true);
        edit = (Button) rootView.findViewById(R.id.appointment_detail_edit_btn);
        delete = (Button) rootView.findViewById(R.id.appointment_detail_delete_btn);
        nameText.setText(name);
        descriptionText.setText(description);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(formatter.parse(date));
        } catch (ParseException e) {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.error)
                    .setMessage("Interner Fehler! Die Daten wurden falsch weitergegeben!")
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getFragmentManager().popBackStack();
                        }
                    }).create();
            alertDialog.show();
        }
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

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
                        date = mDay + "." + mMonth + "." + mYear;
                    }
                });
        date = "" + datePicker.getDayOfMonth() + "." + (datePicker.getMonth() + 1) + "."
                + datePicker.getYear();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(calendar.get(Calendar.MINUTE));
        } else {
            timePicker.setCurrentHour(Calendar.HOUR_OF_DAY);
            timePicker.setCurrentMinute(Calendar.MINUTE);
        }
        edit.setOnClickListener(editAppointment());
        delete.setOnClickListener(deleteAppointment());
        return rootView;
    }

    private TextWatcher descriptionChanged() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                description = descriptionText.getText().toString();
            }
        };
    }

    private TextWatcher nameChanged() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                name = nameText.getText().toString();
            }
        };
    }

    private View.OnClickListener deleteAppointment() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInternet.isNetworkAvailable(getContext())) {
                    if (validateInput(name, description)) {
                        String[] params = new String[]{MainActivity.URL + "delete/appointment",
                                MainActivity.userData.getToken(), MainActivity.userData.getAdminOfProject(),
                                MainActivity.userData.getTeamName(), MainActivity.userData.getUsername(), id};
                        DeleteAppointmentTask deleteAppointmentTask = new DeleteAppointmentTask();
                        try {
                            JSONObject result = deleteAppointmentTask.execute(params).get();
                            String success = result.getString("success");
                            if (success.equals("true")) {
                                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                        .setTitle(R.string.success)
                                        .setMessage("Das Meeting wurde erfolgreich gelöscht!")
                                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Fragment newFragment = new AppointmentsFragment();
                                                FragmentTransaction transaction
                                                        = getFragmentManager().beginTransaction();
                                                transaction.replace(R.id.containerFrame, newFragment);
                                                transaction.commit();
                                            }
                                        }).create();
                                alertDialog.show();
                            } else {
                                String reason = result.getString("reason");
                                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                        .setTitle(R.string.error)
                                        .setMessage("Das Meeting konnte nicht gelöscht werden!\n" + reason)
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
                        Toast.makeText(getContext(),
                                getResources().getString(R.string.error_fields_filled_wrong),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    AlertDialog alertDialog = CheckInternet.internetNotAvailable(getActivity());
                    alertDialog.show();
                }
            }
        };
    }

    private boolean validateInput(String name, String description) {
        boolean result = false;
        if (name != null && !(name.equals("")) && description != null
                && !(description.equals(""))) {
            result = true;
        }
        return result;
    }

    private boolean validateDeadline(String deadline) {
        boolean result = false;
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        Calendar currentCalendar = Calendar.getInstance();
        Calendar deadlineCalendar = Calendar.getInstance();
        try {
            deadlineCalendar.setTime(formatter.parse(deadline));
            if (currentCalendar.getTime().before(deadlineCalendar.getTime())) {
                result = true;
            }
        } catch (ParseException e) {
            // Never reached
        }
        return result;
    }

    private View.OnClickListener editAppointment() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInternet.isNetworkAvailable(getContext())) {
                    if (validateInput(name, description)) {
                        concatenateDateAndTime();
                        if (validateDeadline(deadline)) {
                            String[] params = new String[]{MainActivity.URL + "edit/appointment",
                                    MainActivity.userData.getToken(), MainActivity.userData.getUsername(),
                                    MainActivity.userData.getAdminOfProject(), MainActivity.userData.getTeamName(),
                                    id, name, description, deadline};
                            EditAppointmentTask editAppointmentTask = new EditAppointmentTask();
                            try {
                                JSONObject result = editAppointmentTask.execute(params).get();
                                String success = result.getString("success");
                                if (success.equals("true")) {
                                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                            .setTitle(R.string.success)
                                            .setMessage("Sie haben das Meeting erfolgreich editiert!")
                                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Fragment newFragment = new AppointmentsFragment();
                                                    FragmentTransaction transaction
                                                            = getFragmentManager().beginTransaction();
                                                    transaction.replace(R.id.containerFrame, newFragment);
                                                    transaction.commit();
                                                }
                                            }).create();
                                    alertDialog.show();
                                } else {
                                    String reason = result.getString("reason");
                                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                            .setTitle(R.string.error)
                                            .setMessage("Das Meeting konnte nicht editiert werden!\n" + reason)
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
                            Toast.makeText(getContext(),
                                    getResources().getString(R.string.error_deadline),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(),
                                getResources().getString(R.string.error_fields_filled_wrong),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    AlertDialog alertDialog = CheckInternet.internetNotAvailable(getActivity());
                    alertDialog.show();
                }
            }
        };
    }

    private void concatenateDateAndTime() {
        deadline = date + " " + time;
    }

    private void fetchDataFromBundle(Bundle bundle) {
        try {
            JSONObject appointment = new JSONObject(bundle.getString("appointment"));
            String isDeadLine = appointment.getString("isDeadline");
            if (isDeadLine.equals("true")) {
                this.isDeadline = true;
            } else {
                this.isDeadline = false;
            }
            id = appointment.getString("id");
            name = appointment.getString("name");
            description = appointment.getString("description");
            date = appointment.getString("deadline");
        } catch (JSONException e) {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.error)
                    .setMessage("Interner Fehler! Die Date wurden falsch übergeben!")
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getFragmentManager().popBackStack();
                        }
                    })
                    .create();
            alertDialog.show();
        }
    }
}
