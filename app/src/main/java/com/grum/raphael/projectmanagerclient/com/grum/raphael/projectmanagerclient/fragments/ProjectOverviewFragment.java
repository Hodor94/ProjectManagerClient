package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

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

import com.grum.raphael.projectmanagerclient.R;

public class ProjectOverviewFragment extends Fragment {

    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_project_overview, container, false);
        Bundle bundle = getArguments();
        tabLayout = (TabLayout) rootView.findViewById(R.id.project_overview_tab);
        tabLayout.addTab(tabLayout.newTab().setText("Projekt bearbeiten"));
        tabLayout.addTab(tabLayout.newTab().setText("Statistik"));
        TabLayout.Tab initialTab = tabLayout.getTabAt(0);
        initialTab.select();
        Fragment initialFragment = new EditProjectFragment();
        initialFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.pager_project_overview, initialFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment newFragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        newFragment = new EditProjectFragment();
                        newFragment.setArguments(getArguments());
                        break;
                    case 1:
                        newFragment = new ProjectStatisticFragment();
                        newFragment.setArguments(getArguments());
                        break;
                }
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.pager_project_overview, newFragment);
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
