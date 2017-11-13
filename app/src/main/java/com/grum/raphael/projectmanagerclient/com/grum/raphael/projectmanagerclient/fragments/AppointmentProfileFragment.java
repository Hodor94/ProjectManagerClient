package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.content.Context;
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

import com.grum.raphael.projectmanagerclient.R;


public class AppointmentProfileFragment extends Fragment {

    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_appointment_profile, container, false);
        final Bundle bundle = getArguments();
        String appointment = bundle.getString("appointment");
        tabLayout = (TabLayout) rootView.findViewById(R.id.tab_appointment_profile);
        tabLayout.addTab(tabLayout.newTab().setText("Meeting bearbeiten"));
        tabLayout.addTab(tabLayout.newTab().setText("Statistik"));
        TabLayout.Tab startingTab = tabLayout.getTabAt(0);
        startingTab.select();
        Fragment initialFragment = new AppointmentDetailFragment();
        initialFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.pager_appointment_profile, initialFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentManager manager = getFragmentManager();
                FragmentTransaction fragmentTransaction = manager.beginTransaction();
                Fragment newFragment;
                if (tab.getPosition() == 0) {
                    newFragment = new AppointmentDetailFragment();
                    newFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.pager_appointment_profile, newFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    // TODO
                    /*
                    newFragment = new RegisterFragment();
                    fragmentTransaction.replace(R.id.pager_team_profile, newFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    */
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
