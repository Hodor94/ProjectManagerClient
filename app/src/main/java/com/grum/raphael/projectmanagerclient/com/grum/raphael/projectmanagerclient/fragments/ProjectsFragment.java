package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.GetTeamMembersTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class ProjectsFragment extends Fragment {

    private String projectName;
    private String projectDescription;
    String projectOwner;
    private TabLayout tabLayout;
    private Button btnCreateProject;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_projects, container, false);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tab_projects_profile);
        tabLayout.addTab(tabLayout.newTab().setText("Projekt Erstellen"));
        tabLayout.addTab(tabLayout.newTab().setText("Meine Projekte"));
        tabLayout.addTab(tabLayout.newTab().setText("Alle Projekte"));
        TabLayout.Tab initialTab = tabLayout.getTabAt(0);
        initialTab.select();
        Fragment initialFragment = new CreateProjectFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.pager_projects_profile, initialFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // TODO
                Fragment newFragment = null;
                switch (tab.getPosition()) {
                    case 0 :
                        newFragment = new CreateProjectFragment();
                        break;
                    case 1 :
                        newFragment = new MyProjectsFragment();
                        break;
                    case 2 :
                        newFragment = new AllProjectsFragment();
                        break;
                }
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.pager_projects_profile, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
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
