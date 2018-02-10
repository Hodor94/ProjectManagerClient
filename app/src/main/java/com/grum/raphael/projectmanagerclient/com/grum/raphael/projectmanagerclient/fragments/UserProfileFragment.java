package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.R;

import org.json.JSONException;
import org.json.JSONObject;


public class UserProfileFragment extends Fragment {

    private TextView username;
    private TextView firstName;
    private TextView surname;
    private TextView email;
    private TextView phoneNr;
    private TextView address;
    private TextView tributes;
    private TextView birthday;
    private TextView dayOfEntry;
    private TextView team;
    private Button edit;
    private final String BLANK = "-----";

    public UserProfileFragment() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();

        // Fetch the TextViews of the fragment
        username = (TextView) getView().findViewById(R.id.username);
        firstName = (TextView) getView().findViewById(R.id.firstName);
        surname = (TextView) getView().findViewById(R.id.surname);
        email = (TextView) getView().findViewById(R.id.email);
        phoneNr = (TextView) getView().findViewById(R.id.phoneNr);
        address = (TextView) getView().findViewById(R.id.address);
        tributes = (TextView) getView().findViewById(R.id.tributes);
        birthday = (TextView) getView().findViewById(R.id.birthday);
        dayOfEntry = (TextView) getView().findViewById(R.id.dayOfEntry);
        team = (TextView) getView().findViewById(R.id.team);
        edit = (Button) getView().findViewById(R.id.edit_user_profile);

        // Set OnClickListener for edit Button
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = createBundleForEdit();
                EditUserProfileFragment newFragment = new EditUserProfileFragment();
                newFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.containerFrame, newFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        if (bundle != null) {
            try {
                JSONObject userData = new JSONObject(bundle.getString("userData"));
                setTextData(userData);
            } catch (JSONException e) {
                // TODO
                e.printStackTrace();
            }
        } else {
            // TODO
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile,
                container, false);
        return rootView;
    }

    private void setTextData(JSONObject userData) {
        try {
            JSONObject user = new JSONObject(userData.getString("user"));
            this.username.setText(user.getString("username"));
            this.firstName.setText(user.getString("firstName"));
            this.surname.setText(user.getString("surname"));
            this.email.setText(user.getString("email"));
            this.phoneNr.setText(user.getString("phoneNr"));
            this.address.setText(user.getString("address"));
            String tributes = user.getString("tributes");
            String birthday = user.getString("birthday");
            String dayOfEntry = user.getString("dayOfEntry");
            String team = user.getString("team");
            if (tributes.equals("null")) {
                this.tributes.setText(BLANK);
            } else {
                this.tributes.setText(tributes);
            }
            if (birthday.equals("null")) {
                this.birthday.setText(BLANK);
            } else {
                String[] splitted = birthday.split("\\s+");
                this.birthday.setText(splitted[0]);
            }
            if (dayOfEntry.equals("null")) {
                this.dayOfEntry.setText(BLANK);
            } else {
                this.dayOfEntry.setText(dayOfEntry);
            }
            if (team.equals("null")) {
                this.team.setText(BLANK);
            } else {
                this.team.setText(team);
            }
        } catch (JSONException e) {
            // TODO
            e.printStackTrace();
        }
    }

    private Bundle createBundleForEdit() {
        Bundle bundle = new Bundle();
        bundle.putString("username", username.getText().toString());
        bundle.putString("firstName", firstName.getText().toString());
        bundle.putString("surname", surname.getText().toString());
        bundle.putString("email", email.getText().toString());
        bundle.putString("phoneNr", phoneNr.getText().toString());
        bundle.putString("address", address.getText().toString());
        bundle.putString("tributes", tributes.getText().toString());
        bundle.putString("birthday", birthday.getText().toString());
        bundle.putString("dayOfEntry", dayOfEntry.getText().toString());
        bundle.putString("team", team.getText().toString());
        return bundle;
    }

}
