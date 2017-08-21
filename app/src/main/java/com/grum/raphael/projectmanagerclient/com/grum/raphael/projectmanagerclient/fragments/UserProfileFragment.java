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

    public UserProfileFragment() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        username = (TextView) getView().findViewById(R.id.username);
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
