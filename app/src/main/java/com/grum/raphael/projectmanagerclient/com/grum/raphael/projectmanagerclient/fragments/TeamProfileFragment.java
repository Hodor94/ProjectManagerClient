package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;

import org.json.JSONException;
import org.json.JSONObject;


public class TeamProfileFragment extends Fragment {

    private TabLayout tabLayout;

    public TeamProfileFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_team_profile, container, false);
        final Bundle bundle = getArguments();
        tabLayout = (TabLayout) rootView.findViewById(R.id.tab_team_profile);
        tabLayout.addTab(tabLayout.newTab().setText("Team Übersicht"));
        tabLayout.addTab(tabLayout.newTab().setText("Gruppen"));
        tabLayout.addTab(tabLayout.newTab().setText("Mitglieder"));
        TabLayout.Tab startingTab = tabLayout.getTabAt(0);
        startingTab.select();
        Fragment initialFragment = new TeamInformationFragment();
        initialFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.pager_team_profile, initialFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentManager manager = getFragmentManager();
                FragmentTransaction fragmentTransaction = manager.beginTransaction();
                if (tab.getPosition() == 0) {
                    TeamInformationFragment newFragment = new TeamInformationFragment();
                    newFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.pager_team_profile, newFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else if (tab.getPosition() == 1) {
                    fragmentTransaction.replace(R.id.pager_team_profile, new RegisterFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    // TODO
                    fragmentTransaction.replace(R.id.pager_team_profile,
                            new TeamMembersFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        return rootView;
    }

}
