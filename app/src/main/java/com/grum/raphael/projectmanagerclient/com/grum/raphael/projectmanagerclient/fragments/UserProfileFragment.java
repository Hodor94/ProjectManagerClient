package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;

import org.json.JSONObject;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;


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
        // TODO debug
        birthday = (TextView) getView().findViewById(R.id.team);
        dayOfEntry = (TextView) getView().findViewById(R.id.dayOfEntry);
        team = (TextView) getView().findViewById(R.id.team);

        // Set the text to the TextView elements
        // TODO format and proof if null
        username.setText(bundle.getString("username"));
        firstName.setText(bundle.getString("firstName"));
        surname.setText(bundle.getString("surname"));
        email.setText(bundle.getString("email"));
        phoneNr.setText(bundle.getString("phoneNr"));
        address.setText(bundle.getString("address"));
        tributes.setText(bundle.getString("tributes"));
        birthday.setText(bundle.getString("birthday"));
        dayOfEntry.setText(bundle.getString("dayOfEntry"));
        team.setText(bundle.getString("team"));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        return rootView;
    }

    private void getUserData() {
        // TODO
    }

    private class UserDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet getData = new HttpGet(MainActivity.URL + "user");

            return null;
        }
    }
}
