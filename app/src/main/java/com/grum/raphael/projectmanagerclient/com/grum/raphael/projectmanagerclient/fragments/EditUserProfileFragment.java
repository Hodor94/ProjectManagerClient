package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.EditUserTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class EditUserProfileFragment extends Fragment {

    private TextView username;
    private TextView team;
    private TextView tributes;
    private TextView dayOfEntry;
    private EditText firstName;
    private EditText surname;
    private EditText address;
    private EditText phoneNr;
    private EditText email;
    private DatePicker birthday;
    private String userBirthday;
    private Button ready;

    public EditUserProfileFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_user_profile, container, false);
        Bundle bundle = getArguments();
        // Fetch components of fragment
        username = (TextView) rootView.findViewById(R.id.edit_username);
        team = (TextView) rootView.findViewById(R.id.edit_team);
        tributes = (TextView) rootView.findViewById(R.id.edit_tributes);
        dayOfEntry = (TextView) rootView.findViewById(R.id.edit_dayOfEntry);
        firstName = (EditText) rootView.findViewById(R.id.edit_firstName);
        surname = (EditText) rootView.findViewById(R.id.edit_surname);
        address = (EditText) rootView.findViewById(R.id.edit_address);
        phoneNr = (EditText) rootView.findViewById(R.id.edit_phoneNr);
        email = (EditText) rootView.findViewById(R.id.edit_email);
        birthday = (DatePicker) rootView.findViewById(R.id.edit_birthday);
        ready = (Button) rootView.findViewById(R.id.edit_user_profile);

        // Fetch values of Bundle
        final String firstName = bundle.getString("firstName");
        final String username = bundle.getString("username");
        String team = bundle.getString("team");
        String tributes = bundle.getString("tributes");
        final String dayOfEntry = bundle.getString("dayOfEntry");
        final String surname = bundle.getString("surname");
        final String address = bundle.getString("address");
        final String phoneNr = bundle.getString("phoneNr");
        userBirthday = bundle.getString("birthday");
        final String email = bundle.getString("email");

        // Set values to components
        this.username.setText(username);
        this.team.setText(team);
        this.tributes.setText(tributes);
        this.dayOfEntry.setText(dayOfEntry);
        this.firstName.setText(firstName);
        this.surname.setText(surname);
        this.address.setText(address);
        this.phoneNr.setText(phoneNr);
        this.email.setText(email);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        final Calendar usersBirthday = Calendar.getInstance();
        try {
            usersBirthday.setTime(formatter.parse(userBirthday));
            this.birthday.init(usersBirthday.get(Calendar.YEAR), usersBirthday.get(Calendar.MONTH),
                    usersBirthday.get(Calendar.DAY_OF_MONTH),
                    new DatePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            String mDay;
                            String mMonth;
                            String mYear;
                            if (dayOfMonth < 10) {
                                mDay = "0" + dayOfMonth;
                            } else {
                                mDay = "" + dayOfMonth;
                            }
                            if (monthOfYear < 10) {
                                mMonth = "0" + (monthOfYear + 1);
                            } else {
                                mMonth = "" + (monthOfYear + 1);
                            }
                            mYear = "" + year;
                            // The 00:00:00 is for the formatter on server side
                            userBirthday = mDay + "." + mMonth + "." + mYear + " 00:00:00";
                        }
                    });
        } catch (ParseException e) {
            // TODO
            e.printStackTrace();
        }
        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText firstName = (EditText) getActivity().findViewById(R.id.edit_firstName);
                EditText surname = (EditText) getActivity().findViewById(R.id.edit_surname);
                EditText address = (EditText) getActivity().findViewById(R.id.edit_address);
                EditText phoneNr = (EditText) getActivity().findViewById(R.id.edit_phoneNr);
                EditText email = (EditText) getActivity().findViewById(R.id.edit_email);
                if (validateInput(firstName.getText().toString(), surname.getText().toString(),
                        address.getText().toString(), phoneNr.getText().toString(),
                        email.getText().toString(), userBirthday)) {
                    String[] params = new String[]{MainActivity.URL + "edit/user",
                            MainActivity.userData.getToken(), username,
                            firstName.getText().toString(), surname.getText().toString(),
                            address.getText().toString(), phoneNr.getText().toString(),
                            email.getText().toString(), userBirthday};
                    EditUserTask editUserTask = new EditUserTask();
                    try {
                        JSONObject response = editUserTask.execute(params).get();
                        String success = response.getString("success");
                        if (success.equals("true")) {
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.success)
                                    .setMessage("Sie haben Ihr Profil erfolgreich aktualisiert")
                                    .setNegativeButton("OK", null)
                                    .create();
                            alertDialog.show();
                        } else {
                            // TODO open User Profile
                            String reason = response.getString("reason");
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.error)
                                    .setMessage("Ihr Profil konnte nicht erfolgreich aktualisiert " +
                                            "werden. \n" + reason)
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
                            .setMessage(R.string.error_fields_empty)
                            .setNegativeButton("OK", null)
                            .create();
                    alertDialog.show();
                }

            }
        });
        return rootView;
    }

    private boolean validateInput(String firstName, String surname, String address, String phoneNr,
                                  String email, String birthday) {
        boolean result = false;
        if (firstName != null && !(firstName.equals("")) && surname != null && !(surname.equals(""))
                && address != null && !(address.equals("")) && phoneNr != null
                && !(phoneNr.equals("")) && email != null && !(email.equals("")) &&
                birthday != null && !(birthday.equals(""))) {
            result = true;
        }
        return result;
    }


}