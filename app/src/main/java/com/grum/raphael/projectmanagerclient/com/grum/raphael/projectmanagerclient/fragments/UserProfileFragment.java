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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

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
        try {
            username.setText(URLDecoder.decode(bundle.getString("username"), "latin1"));
            firstName.setText(URLDecoder.decode(bundle.getString("firstName"), "latin1"));
            surname.setText(URLDecoder.decode(bundle.getString("surname"), "latin1"));
            email.setText(URLDecoder.decode(bundle.getString("email"), "latin1"));
            phoneNr.setText(URLDecoder.decode(bundle.getString("phoneNr"), "latin1"));
            address.setText(URLDecoder.decode(bundle.getString("address"), "latin1"));
            String tributes = bundle.getString("tributes", String.valueOf(R.string.blank));
            String birthday = bundle.getString("birthday", String.valueOf(R.string.blank));
            String dayOfEntry = bundle.getString("dayOfEntry", String.valueOf(R.string.blank));
            String team = bundle.getString("team", "----");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

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
